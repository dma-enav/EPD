/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dma.epd.shore.layers.ais;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.ISelectableGraphic;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.MobileTarget;
import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.ais.AisLayerCommon;
import dk.dma.epd.common.prototype.layers.ais.AisTargetInfoPanelCommon;
import dk.dma.epd.common.prototype.layers.ais.PastTrackInfoPanel;
import dk.dma.epd.common.prototype.layers.ais.PastTrackWpCircle;
import dk.dma.epd.common.prototype.layers.ais.SarTargetGraphic;
import dk.dma.epd.common.prototype.layers.ais.SartGraphic;
import dk.dma.epd.common.prototype.layers.ais.TargetGraphic;
import dk.dma.epd.common.prototype.layers.ais.VesselTargetGraphic;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.MainFrame;
import dk.dma.epd.shore.gui.views.MapMenu;
import dk.dma.epd.shore.gui.views.StatusArea;

/**
 * The class AisLayer is the layer containing all AIS targets. The class handles the drawing of vessels on the chartPanel.
 * 
 * SuppressWarnings("serial") as a layer should never be serialized.
 */
@SuppressWarnings("serial")
@ThreadSafe
public class AisLayer extends AisLayerCommon<AisHandler> implements IAisTargetListener {
    private static final Logger LOG = LoggerFactory.getLogger(AisLayer.class);

    private final AisTargetInfoPanelCommon aisTargetInfoPanel = new AisTargetInfoPanelCommon();
    private StatusArea statusArea;
    private ChartPanel chartPanel;
    private final PastTrackInfoPanel pastTrackInfoPanel = new PastTrackInfoPanel();
    private boolean showVesselLabels;

    @GuardedBy("targets")
    private final Map<Long, TargetGraphic> targets = new ConcurrentHashMap<>();

    private volatile float mapScale;

    /**
     * Create a new AisLayer that is redrawn repeatedly at a given interval.
     * @param redrawIntervalMillis The interval at which the AisLayer will redraw itself.
     */
    public AisLayer(int redrawIntervalMillis) {
        super(redrawIntervalMillis);
        // receive left-click events for the following set of classes.
        this.registerMouseClickClasses(VesselTargetGraphic.class);
        // receive right-click events for the following set of classes.
        this.registerMapMenuClasses(VesselTargetGraphic.class, SartGraphic.class);
        // Register mouse over of VesselTargetGraphics to invoke the AisTargetInfoPanel
        this.registerInfoPanel(this.aisTargetInfoPanel, VesselTargetGraphic.class);
        // Register mouse over of PastTrackWpCircle to invoke the PastTrackInfoPanel
        this.registerInfoPanel(this.pastTrackInfoPanel, PastTrackWpCircle.class);
        
        this.showVesselLabels = EPDShore.getInstance().getSettings().getAisSettings().isShowNameLabels();
    }

    /**
     * Clears all targets from the map and in the local memory
     */
    public void mapClearTargets() {
        synchronized (graphics) {
            graphics.clear();
        }
        synchronized (targets) {
            targets.clear();
        }
    }

    /**
     * Check if vessel is near map coordinates or it's
     * sending an intended route
     * @param mobileTarget
     * @return if the target should be included
     */
    private boolean drawTarget(MobileTarget mobileTarget) {
        Point2D lr = chartPanel.getMap().getProjection().getLowerRight();
        Point2D ul = chartPanel.getMap().getProjection().getUpperLeft();
        Position pos = mobileTarget.getPositionData().getPos();
        
        boolean t1 = pos.getLatitude() >= lr.getY();
        boolean t2 = pos.getLatitude() <= ul.getY();
        boolean t3 = pos.getLongitude() >= ul.getX();
        boolean t4 = pos.getLongitude() <= lr.getX();
        
        if (!(t1 && t2 && t3 && t4)) {
            VesselTarget vesselTarget = (mobileTarget instanceof VesselTarget) ? (VesselTarget)mobileTarget : null;
            if (vesselTarget == null) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Draws or updates the supported AIS (vessels and sar) targets on the map
     */
    private void drawTargets() {
        if (aisHandler == null) {
            return;
        }

        if (chartPanel != null) {

            if (chartPanel.getMap().getScale() != mapScale) {
                mapScale = chartPanel.getMap().getScale();
                mapClearTargets();
            }
            
            for (MobileTarget mobileTarget : aisHandler.getMobileTargets(AisTarget.Status.OK)) {
                
                // Check if vessel is near map coordinates or it's
                // sending an intended route
                if (!drawTarget(mobileTarget)) {
                    continue;
                }

                synchronized (targets) {
                    TargetGraphic targetGraphic = targets.get(mobileTarget.getMmsi());
                    if (targetGraphic == null) {
                        if (mobileTarget instanceof VesselTarget) {
                            // TODO fix boolean argument
                            targetGraphic = new VesselTargetGraphic(true, this);
                            targetGraphic.setVague(true);
                        } else if (mobileTarget instanceof SarTarget) {
                            targetGraphic = new SarTargetGraphic();
                        } else {
                            LOG.error("Unknown target type");
                            continue;
                        }
                        synchronized (graphics) {
                            graphics.add(targetGraphic);
                        }
                        targets.put(mobileTarget.getMmsi(), targetGraphic);
                    }
                    
                    // Update the target graphics
                    targetGraphic.update(mobileTarget, null, null, mapScale);
                    // update selection on graphic if selectable graphic
                    if (targetGraphic instanceof ISelectableGraphic) {
                        ((ISelectableGraphic)targetGraphic).setSelection(mobileTarget.getMmsi() == getMainFrame().getSelectedMMSI());
                    }
                    
                }
            }
        }

        doPrepare();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void forceLayerUpdate() {
        this.drawTargets();
    }
    
    @Override
    public OMGraphicList prepare() {
        synchronized (graphics) {
            graphics.project(getProjection());
        }
        return graphics;
    }

    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (obj instanceof ChartPanel) {
            chartPanel = (ChartPanel) obj;
        }
        if (obj instanceof StatusArea) {
            statusArea = (StatusArea) obj;
        }
        if (obj instanceof JMapFrame) {
            mapFrame.getGlassPanel().add(pastTrackInfoPanel);
            mapFrame.getGlassPanel().setVisible(true);
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        super.findAndUndo(obj);
    }

    /**
     * Event handler for left click on the map.
     */
    @Override
    protected void handleMouseClick(OMGraphic clickedGraphics, MouseEvent evt) {
        // Should only handle left clicks.
        assert evt.getButton() == MouseEvent.BUTTON1;
        if(clickedGraphics != null) {
            System.out.println("clickedGraphics has type = " + clickedGraphics.getClass().getSimpleName());
        }
        if(clickedGraphics instanceof ISelectableGraphic) {
            // Update selected graphic and do a repaint
            this.setSelectedGraphic((ISelectableGraphic) clickedGraphics, true);
            
            if(clickedGraphics instanceof VesselTargetGraphic) {
                VesselTarget vt = ((VesselTargetGraphic)clickedGraphics).getVesselTarget();
                if(vt != null) {
                    // Update status text if clicked graphic is a vessel
                    setStatusAreaTxt(vt);
                    // Call mainframe with new selection such that AisLayers in other frames will also display the new selection.
                    getMainFrame().setSelectedMMSI(vt.getMmsi());
                }
            }
        }
        else if(clickedGraphics == null) {
            // User clicked somewhere on the map with no nearby graphics
            // We need to remove the current selection and repaint
            this.setSelectedGraphic(null, true);
            // Call mainframe with invalid selection sucht that AisLayers in other frames will also have their selection removed.
            this.getMainFrame().setSelectedMMSI(-1);
            // remove status info as no target is selected
            this.statusArea.removeHighlight();
        }
    }
    
    /**
     * Event handler for right click on the map.
     */
    @Override
    protected void initMapMenu(OMGraphic clickedGraphics, MouseEvent evt) {
        // Should only handle right clicks
        assert evt.getButton() == MouseEvent.BUTTON3;
        if (clickedGraphics instanceof VesselTargetGraphic) {
            VesselTargetGraphic vesselTargetGraphic = (VesselTargetGraphic) clickedGraphics;
            VesselTarget vt = ((VesselTargetGraphic) clickedGraphics).getVesselTarget();
            // Pass data to the pop up menu that is to be displayed.
            this.getMapMenu().aisMenu(vt, vesselTargetGraphic);
        } else if (clickedGraphics instanceof SartGraphic) {
            SartGraphic sartGraphic = (SartGraphic) clickedGraphics;
            SarTarget sarTarget = sartGraphic.getSarTargetGraphic().getSarTarget();
            // Pass data to the pop up menu that is to be displayed.
            this.getMapMenu().sartMenu(this, sarTarget);
        }
    }
    
    @Override
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest,
            MouseEvent evt, Point containerPoint) {
        if(infoPanel == this.aisTargetInfoPanel && newClosest instanceof VesselTargetGraphic) {
            VesselTarget vt = ((VesselTargetGraphic)newClosest).getVesselTarget(); 
            // TODO: need to put call below in a synchronized(targets) block ?
            this.aisTargetInfoPanel.showAisInfoLabel(vt);
            // adjust info panel such that it fits in the frame
            int x = (int) containerPoint.getX() + 10;
            int y = (int) containerPoint.getY() + 10;
            if (chartPanel.getMap().getProjection().getWidth() - x < aisTargetInfoPanel.getWidth()) {
                x -= aisTargetInfoPanel.getWidth() + 20;
            }
            if (chartPanel.getMap().getProjection().getHeight() - y < aisTargetInfoPanel.getHeight()) {
                y -= aisTargetInfoPanel.getHeight() + 20;
            }
            aisTargetInfoPanel.setPos(x, y);
            return true;
        }
        // TODO 30-01-2014 Janus Varmarken: this is never called as VesselTargetGraphic has vague = true
        // TODO 30-01-2014 Janus Varmarken: consider moving PastTrackGraphic outside VesselTargetGraphic
        else if (infoPanel == this.pastTrackInfoPanel && newClosest instanceof PastTrackWpCircle) {
          PastTrackWpCircle wpCircle = (PastTrackWpCircle) newClosest;
          pastTrackInfoPanel.showWpInfo(wpCircle);
          return true;
        }
        return false;
    }
    
    /**
     * Returns the {@code VesselTargetGraphic} corresponding to the given mmsi, or null if not found
     * @param mmsi the mmsi of the vessel
     * @return the graphic representing the vessel or null if not found
     */
    @SuppressWarnings("unused")
    private VesselTargetGraphic getVessel(Long mmsi) {
        synchronized (targets) {
            TargetGraphic target = this.targets.get(mmsi);
            if (target != null && target instanceof VesselTargetGraphic) {
                return (VesselTargetGraphic)target;
            }
        }
        return null;
    }
    
    private void setStatusAreaTxt(VesselTarget vessel) {
        HashMap<String, String> info = new HashMap<String, String>();
        String currKey;
        if (vessel != null) {
            VesselStaticData vsd = vessel.getStaticData();
            VesselPositionData vpd = vessel.getPositionData();
            
            info.put("MMSI", Long.toString(vessel.getMmsi()));
            currKey = "Name";
            String na = "N/A";
            if(vsd != null && vsd.getName() != null) {
                info.put(currKey, vsd.getName());
            }
            else {
                info.put(currKey, na);
            }
            
            currKey = "COG";
            if(vpd != null) {
                info.put(currKey, Formatter.formatDegrees(new Double(vpd.getCog()), 2));
            }
            else {
                info.put(currKey, na);
            }
            
            currKey = "Call sign";
            if(vsd != null && vsd.getCallsign() != null) {
                info.put(currKey, vsd.getCallsign());
            }
            else {
                info.put(currKey, na);
            }
            
            currKey = "LAT";
            if(vpd != null && vpd.getPos() != null) {
                info.put(currKey, Formatter.latToPrintable(vpd.getPos().getLatitude()));
                currKey = "LON";
                info.put(currKey, Formatter.lonToPrintable(vpd.getPos().getLongitude()));
            }
            else {
                info.put(currKey, na);
                currKey = "LON";
                info.put(currKey, na);
            }
            
            currKey = "SOG";
            if(vpd != null) {
                info.put(currKey, Formatter.formatSpeed(new Double(vpd.getSog())));
            }
            else {
                info.put(currKey, na);
            }
            
            currKey = "ETA";
            if(vsd != null) {
                info.put(currKey, Formatter.formatShortDateTime(new Date(vsd.getEta())));
            }
            else {
                info.put(currKey, na);
            }
            
            currKey = "Type";
            if(vsd != null) {
                info.put(currKey, vsd.getShipType().toString());
            }
            else {
                info.put(currKey, na);
            }
            
            statusArea.receiveHighlight(info, vessel.getMmsi());
        }
    }

    @Override
    public void targetUpdated(AisTarget arg0) {
    }
    
    @Override
    public MainFrame getMainFrame() {
        // get the shore specific main frame
        return (MainFrame) super.getMainFrame();
    }
    
    @Override
    public MapMenu getMapMenu() {
        // get the shore specific map menu
        return (MapMenu) super.getMapMenu();
    }
    
    /**
     * This method is called repeatedly as specified by the {@code LazyLayerCommon} and signals that this AisLayer should repaint itself.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        drawTargets();
    }
    
    /**
     * Updates visibility of vessel names. Updates are done with the redraw interval,
     * defined by the constructor of this object. 
     * @param Sets the vessel names to be shown or not.
     */
    @Override
    public void setShowNameLabels(boolean showLabels) {
        
        if (showLabels == this.showVesselLabels) {
            return;
        }
        
        this.showVesselLabels = showLabels;
        for (TargetGraphic graphic : targets.values()) {
            
            if (graphic instanceof VesselTargetGraphic) {
                ((VesselTargetGraphic) graphic).setShowNameLabel(showLabels);
                targetUpdated(((VesselTargetGraphic) graphic).getVesselTarget());
            }
            
            doPrepare();
        }
    }
}

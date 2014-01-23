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
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.MobileTarget;
import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.layers.ais.AisTargetSelectionGraphic;
import dk.dma.epd.common.prototype.layers.ais.PastTrackInfoPanel;
import dk.dma.epd.common.prototype.layers.ais.PastTrackWpCircle;
import dk.dma.epd.common.prototype.layers.ais.SarTargetGraphic;
import dk.dma.epd.common.prototype.layers.ais.SartGraphic;
import dk.dma.epd.common.prototype.layers.ais.TargetGraphic;
import dk.dma.epd.common.prototype.layers.ais.VesselTargetGraphic;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.StatusArea;
import dk.dma.epd.shore.layers.GeneralLayer;

/**
 * The class AisLayer is the layer containing all AIS targets. The class handles the drawing of vessels on the chartPanel.
 */
@ThreadSafe
public class AisLayer extends GeneralLayer implements Runnable, IAisTargetListener {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(AisLayer.class);

    private volatile AisHandler aisHandler;
    private AisInfoPanel aisInfoPanel;
    private StatusArea statusArea;
    private ChartPanel chartPanel;
    private final PastTrackInfoPanel pastTrackInfoPanel = new PastTrackInfoPanel();

    @GuardedBy("targets")
    private final Map<Long, TargetGraphic> targets = new ConcurrentHashMap<>();

    private volatile boolean shouldRun = true;
    private volatile float mapScale;

    private final Thread aisThread;

    private volatile OMGraphic closest;
    private final AisTargetSelectionGraphic targetSelectionGraphic = new AisTargetSelectionGraphic();

    /**
     * Keeps the AisLayer thread alive
     */
    @Override
    public void run() {

        while (shouldRun) {
            try {
                drawTargets();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

        }
        synchronized (targets) {
            targets.clear();
        }
        synchronized (graphics) {
            graphics.clear();
            graphics.add(targetSelectionGraphic);
        }
    }

    /**
     * Starts the AisLayer thread
     */
    public AisLayer() {
        synchronized (graphics) {
            graphics.add(targetSelectionGraphic);
        }
        aisThread = new Thread(this);
        aisThread.start();
    }

    public Thread getAisThread() {
        return aisThread;
    }

    /**
     * Kills the AisLayer thread
     */
    public void stop() {
        shouldRun = false;
    }

    /**
     * Clears all targets from the map and in the local memory
     */
    public void mapClearTargets() {
        synchronized (graphics) {
            graphics.clear();
            graphics.add(targetSelectionGraphic);
        }
        synchronized (targets) {
            targets.clear();
        }
    }

    public void removeSelection() {
        targetSelectionGraphic.setVisible(false);
        
        getMainFrame().setSelectedMMSI(-1);

        statusArea.removeHighlight();

        doPrepare();
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
            if (vesselTarget == null || !vesselTarget.hasIntendedRoute()) {
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
            // Bug fix: clear selection such that a deselection in one window
            // will propagate to other windows
            this.targetSelectionGraphic.setVisible(false);
            
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
//                            targetGraphic = new Vessel(mobileTarget.getMmsi());
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

                    if (mobileTarget.getMmsi() == getMainFrame().getSelectedMMSI()) {
                        targetSelectionGraphic.moveSymbol(mobileTarget.getPositionData().getPos());
                        setStatusAreaTxt();
                    }
                }
            }
        }

        doPrepare();

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
        
        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
            aisHandler.addListener(this);
        }
        if (obj instanceof ChartPanel) {
            chartPanel = (ChartPanel) obj;
        }
        if (obj instanceof StatusArea) {
            statusArea = (StatusArea) obj;
        }
        if (obj instanceof JMapFrame) {
            aisInfoPanel = new AisInfoPanel();
            jMapFrame.getGlassPanel().add(aisInfoPanel);
            jMapFrame.getGlassPanel().add(pastTrackInfoPanel);
            jMapFrame.getGlassPanel().setVisible(true);
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (obj == aisHandler) {
            aisHandler.removeListener(this);
        }
        super.findAndUndo(obj);
    }

    @Override
    public boolean mouseClicked(MouseEvent e) {
        
        OMGraphic newClosest = getSelectedGraphic(
                e, 
                VesselLayer.class,
                SartGraphic.class,
                VesselTargetGraphic.class);

        if (e.getButton() == MouseEvent.BUTTON1) {

            removeSelection();

            if (newClosest != null && newClosest instanceof VesselLayer) {
                synchronized (targets) {
                    long mmsi = ((VesselLayer) newClosest).getMMSI();
                    getMainFrame().setSelectedMMSI(mmsi);

                    targetSelectionGraphic.setVisible(true);

                    targetSelectionGraphic.moveSymbol(Position.create(((VesselLayer) newClosest).getLat(),
                            ((VesselLayer) newClosest).getLon()));
                }

                doPrepare();

                setStatusAreaTxt();

            }
            else if(newClosest != null && newClosest instanceof VesselTargetGraphic) {
                VesselTargetGraphic vtg = (VesselTargetGraphic) newClosest;
                synchronized(targets) {
                    if(vtg.getVesselTarget() != null && vtg.getVesselTarget().getPositionData() != null) {
                        getMainFrame().setSelectedMMSI(vtg.getVesselTarget().getMmsi());
                        targetSelectionGraphic.setVisible(true);
                        targetSelectionGraphic.moveSymbol(vtg.getVesselTarget().getPositionData().getPos());
                    }
                }
                doPrepare();
                setStatusAreaTxt();
            }

        }

        if (e.getButton() == MouseEvent.BUTTON3 && newClosest != null) {

            if (newClosest instanceof VesselLayer) {

                VesselLayer vesselLayer = (VesselLayer) newClosest;
                getMapMenu().aisMenu(vesselLayer.getVessel().getVesselTarget());
                getMapMenu().setVisible(true);
                getMapMenu().show(this, e.getX() - 2, e.getY() - 2);
                return true;
                
            } else if (newClosest instanceof SartGraphic) {

                SartGraphic sartGraphic = (SartGraphic) newClosest;
                SarTarget sarTarget = sartGraphic.getSarTargetGraphic().getSarTarget();
                getMapMenu().sartMenu(this, sarTarget);
                getMapMenu().setVisible(true);
                getMapMenu().show(this, e.getX() - 2, e.getY() - 2);
                return true;
            }

        }
        return false;
    }

    /**
     * Returns the {@code VesselTargetGraphic} correspondign to the given mmsi, or null if not found
     * @param mmsi the mmsi of the vessel
     * @return the graphic representing the vessel or null if not found
     */
    private VesselTargetGraphic getVessel(Long mmsi) {
        synchronized (targets) {
            TargetGraphic target = this.targets.get(mmsi);
            if (target != null && target instanceof VesselTargetGraphic) {
                return (VesselTargetGraphic)target;
            }
        }
        return null;
    }
    
    private void setStatusAreaTxt() {
        HashMap<String, String> info = new HashMap<String, String>();
        String currKey;
        VesselTargetGraphic vtg = getVessel(getMainFrame().getSelectedMMSI());
        VesselTarget vessel = vtg.getVesselTarget();
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
    public boolean mouseMoved(MouseEvent e) {
        OMGraphic newClosest = null;
        synchronized (graphics) {
            newClosest = graphics.findClosest(e.getX(), e.getY(), 3.0f);
        }

        if (newClosest == null) {
            aisInfoPanel.setVisible(false);
            pastTrackInfoPanel.setVisible(false);
            closest = null;
            return false;
        }

        if (newClosest != closest) {
            Point containerPoint = SwingUtilities.convertPoint(chartPanel, e.getPoint(), jMapFrame);

            if (newClosest instanceof PastTrackWpCircle) {
                closest = newClosest;
                PastTrackWpCircle wpCircle = (PastTrackWpCircle) newClosest;
                pastTrackInfoPanel.setPos((int) containerPoint.getX(), (int) containerPoint.getY() - 10);
                pastTrackInfoPanel.showWpInfo(wpCircle);
                pastTrackInfoPanel.setVisible(true);
            }

            if (newClosest instanceof VesselTargetGraphic) {
                jMapFrame.getGlassPane().setVisible(true);
                closest = newClosest;
                VesselTargetGraphic vtg = (VesselTargetGraphic) newClosest;
                int x = (int) containerPoint.getX() + 10;
                int y = (int) containerPoint.getY() + 10;
                synchronized (targets) {
                    aisInfoPanel.showAisInfo(vtg.getVesselTarget());
                }                
                if (chartPanel.getMap().getProjection().getWidth() - x < aisInfoPanel.getWidth()) {
                    x -= aisInfoPanel.getWidth() + 20;
                }
                if (chartPanel.getMap().getProjection().getHeight() - y < aisInfoPanel.getHeight()) {
                    y -= aisInfoPanel.getHeight() + 20;
                }
                aisInfoPanel.setPos(x, y);
                aisInfoPanel.setVisible(true);

                return true;
            }
        }
        return false;
    }

    @Override
    public void targetUpdated(AisTarget arg0) {
    }
}

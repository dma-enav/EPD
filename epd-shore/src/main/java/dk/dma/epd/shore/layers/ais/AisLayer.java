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

import net.jcip.annotations.ThreadSafe;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.ISelectableGraphic;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.MobileTarget;
import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.ais.AisLayerCommon;
import dk.dma.epd.common.prototype.layers.ais.AisTargetInfoPanelCommon;
import dk.dma.epd.common.prototype.layers.ais.SartGraphic;
import dk.dma.epd.common.prototype.layers.ais.VesselTargetGraphic;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.views.ChartPanel;
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

    private final AisTargetInfoPanelCommon aisTargetInfoPanel = new AisTargetInfoPanelCommon();
    private StatusArea statusArea;
    private ChartPanel chartPanel;

    /**
* Create a new AisLayer that is redrawn repeatedly at a given interval.
* @param redrawIntervalMillis The interval at which the AisLayer will redraw itself.
*/
    public AisLayer(int redrawIntervalMillis) {
        super(redrawIntervalMillis);
        
        // Register mouse over of VesselTargetGraphics to invoke the AisTargetInfoPanel
        this.registerInfoPanel(this.aisTargetInfoPanel, VesselTargetGraphic.class);
    }

    /**
* Check if vessel is near map coordinates or it's
* sending an intended route
* @param mobileTarget
* @return if the target should be included
*/
    @SuppressWarnings("unused")
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
* {@inheritDoc}
*/
    @Override
    public void forceLayerUpdate() {
        // Repaint
        this.doPrepare();
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
    }
    
    /**
* {@inheritDoc} <br/>
* In addition, this sub class implementation updates the status area text to reflect any new selection. Furthermore the {@code MainFrame} is notified about the change in selection such that it can pass this info to {@code AisLayer}s in other frames.
*/
    @Override
    protected void handleMouseClick(OMGraphic clickedGraphics, MouseEvent evt) {
        super.handleMouseClick(clickedGraphics, evt);
        if(clickedGraphics == null) {
            // Selection was cleared.
            // Clear status area text.
            this.statusArea.removeHighlight();
            // Inform other AisLayers about the deselection
            this.getMainFrame().setSelectedMMSI(-1);
        }
        else if(clickedGraphics instanceof ISelectableGraphic && clickedGraphics instanceof VesselTargetGraphic) {
            VesselTarget vt = ((VesselTargetGraphic)clickedGraphics).getVesselTarget();
            if(vt != null) {
                // Update status text if clicked graphic is a vessel
                setStatusAreaTxt(vt);
                // Call mainframe with new selection such that AisLayers in other frames will also display the new selection.
                getMainFrame().setSelectedMMSI(vt.getMmsi());
            }
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest,
            MouseEvent evt, Point containerPoint) {
        if (newClosest instanceof VesselTargetGraphic) {
            
            // Handle past track
            if (initPastTrackInfoPanel((VesselTargetGraphic)newClosest, evt, containerPoint)) {
                aisTargetInfoPanel.setVisible(false);
                return false;
            }
            
            // Other parts of vessel target hit
            VesselTarget vesselTarget = ((VesselTargetGraphic)newClosest).getVesselTarget();
            aisTargetInfoPanel.showAisInfoLabel(vesselTarget);
            return true;
        }
        return false;
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
        // repaint every time the timer expires
        this.doPrepare();
    }
    
    /**
* Set if this AIS layer should show name labels for the AIS targets it
* displays. Use this method to toggle AIS target labels on a per layer
* basis. Modify the application wide AisSettings object to toggle AIS
* label visibility for all AIS layers (if more map windows are open).
*
* @param showLabels
* Use true to show name labels, and use false to hide name
* labels.
*/
    public void setShowNameLabels(boolean showLabels) {
        synchronized(this.graphics) {
            for(OMGraphic og : this.graphics) {
                if(og instanceof VesselTargetGraphic) {
                    ((VesselTargetGraphic)og).setShowNameLabel(showLabels);
                }
            }
        }
        // repaint
        this.doPrepare();
    }
}

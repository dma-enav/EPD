/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import dk.dma.epd.common.prototype.layers.ais.PastTrackWpCircle;
import dk.dma.epd.common.prototype.layers.ais.SartGraphic;
import dk.dma.epd.common.prototype.layers.ais.VesselGraphic;
import dk.dma.epd.common.prototype.layers.ais.VesselGraphicComponentSelector;
import dk.dma.epd.common.prototype.settings.layers.AisLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.observers.AisLayerCommonSettingsListener;
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
    * Creates a new AisLayer.
    * @param settings Settings specifiying the appearance of the new layer.
    */
    public AisLayer(AisLayerCommonSettings<AisLayerCommonSettingsListener> settings) {
        super(settings);
        // Register self as observer of settings.
        settings.addObserver(this);
        // Register mouse over of VesselGraphics to invoke the AisTargetInfoPanel
        this.registerInfoPanel(this.aisTargetInfoPanel, VesselGraphic.class);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public AisLayerCommonSettings<AisLayerCommonSettingsListener> getSettings() {
        // TODO Auto-generated method stub
        return (AisLayerCommonSettings<AisLayerCommonSettingsListener>) super.getSettings();
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
        else if(clickedGraphics instanceof ISelectableGraphic && clickedGraphics instanceof VesselGraphic) {
            VesselTarget vt = ((VesselGraphic)clickedGraphics).getMostRecentVesselTarget();
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
        if (clickedGraphics instanceof VesselGraphic) {
            VesselGraphic vesselGraphic = (VesselGraphic) clickedGraphics;
            VesselTarget vt = vesselGraphic.getMostRecentVesselTarget();
            // Pass data to the pop up menu that is to be displayed.
            // TODO this is NOT pretty. Update aisMenu to take VesselGraphic arg?
            this.getMapMenu().aisMenu(vt, (VesselGraphicComponentSelector) this.getTargetGraphic(vt.getMmsi()));
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
        if(infoPanel == this.aisTargetInfoPanel && newClosest instanceof VesselGraphic) {
            VesselTarget vt = ((VesselGraphic)newClosest).getMostRecentVesselTarget(); 
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
        else if (infoPanel == this.pastTrackInfoPanel && newClosest instanceof PastTrackWpCircle) {
          PastTrackWpCircle wpCircle = (PastTrackWpCircle) newClosest;
          pastTrackInfoPanel.showWpInfo(wpCircle);
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
    
}

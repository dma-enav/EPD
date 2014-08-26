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
package dk.dma.epd.ship.layers.ownship;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Date;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.IVesselTargetSettingsListener;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.ais.VesselTargetSettings;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.layers.ais.PastTrackGraphic;
import dk.dma.epd.common.prototype.layers.ais.PastTrackInfoPanel;
import dk.dma.epd.common.prototype.layers.ais.PastTrackWpCircle;
import dk.dma.epd.common.prototype.layers.ais.VesselOutlineGraphicComponent;
import dk.dma.epd.common.prototype.sensor.rpnt.MultiSourcePntHandler;
import dk.dma.epd.common.prototype.zoom.ZoomLevel;
import dk.dma.epd.ship.gui.MapMenu;
import dk.dma.epd.ship.ownship.IOwnShipListener;
import dk.dma.epd.ship.ownship.OwnShipHandler;

/**
 * Defines the own-ship layer
 */
public class OwnShipLayer extends EPDLayerCommon implements IOwnShipListener, ProjectionListener, IVesselTargetSettingsListener {

    private static final long serialVersionUID = 1L;

    private OwnShipHandler ownShipHandler;
    private MultiSourcePntHandler multiSourcePntHandler;

    private long minRedrawInterval = 5 * 1000; // 5 sec

    private Date lastRedraw;

    private Position lastPos;
    private Position currentPos;

    private OwnShipGraphic ownShipGraphic;
    private VesselOutlineGraphicComponent vesselOutlineGraphic;
    private RpntErrorGraphic rpntErrorGraphic;

    private ZoomLevel currentZoomLevel;

    private PastTrackGraphic pastTrackGraphic = new PastTrackGraphic();
    private PastTrackInfoPanel pastTrackInfoPanel = new PastTrackInfoPanel();

    /**
     * Constructor
     */
    public OwnShipLayer() {
        super();
        
        graphics.setVague(true);
        
        graphics.add(pastTrackGraphic);
        
        // Register the info panels
        registerInfoPanel(pastTrackInfoPanel, PastTrackWpCircle.class);
        infoPanelsGraphics = pastTrackGraphic;
                
        // Register the classes the will trigger the map menu
        registerMapMenuClasses(OMGraphicList.class);
    }

    /**
     * Checks whether an update is due, i.e. when a certain 
     * time interval has passed or when the current position 
     * has moved more than 10 meters.
     * 
     * @return whether an update is due
     */
    private synchronized boolean doUpdate() {
        if (lastRedraw == null || lastPos == null) {
            return true;
        }

        long elapsed = new Date().getTime() - lastRedraw.getTime();
        if (elapsed > minRedrawInterval) {
            return true;
        }

        // Check distance moved
        double dist = currentPos.rhumbLineDistanceTo(lastPos);
        if (dist > 10) { // 10 m
            return true;
        }

        return false;
    }

    /**
     * Called when the own-ship has been updated
     * 
     * @param ownShipHandler
     *            the {@code OwnShipHandler}
     */
    @Override
    public synchronized void ownShipUpdated(OwnShipHandler ownShipHandler) {
        if (ownShipHandler == null || !ownShipHandler.isPositionDefined()) {
            return;
        }

        // Set location of ship
        currentPos = ownShipHandler.getPositionData().getPos();

        // check if proper zoom level and if data is available for ship outline drawing
        if (this.currentZoomLevel == ZoomLevel.VESSEL_OUTLINE && ownShipHandler.getStaticData() != null) {
            this.drawOwnShipOutline();
        } else {
            // draw standard version of own ship for all other zoom levels than VESSEL_OUTLINE
            this.drawOwnShipStandard(ownShipHandler.getPositionData());
        }

        // Update the past-track graphics
        pastTrackGraphic.update(ownShipHandler.getAisTarget(), ownShipHandler.getPositionData().getPos());

        // Redraw
        if (!doUpdate()) {
            graphics.project(getProjection(), true);
            return;
        }

        lastPos = Position.create(currentPos.getLatitude(), currentPos.getLongitude());
        lastRedraw = new Date();
        doPrepare();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void ownShipChanged(VesselTarget oldValue, VesselTarget newValue) {
        // Remove this as listener for settings of old own ship
        if (oldValue != null) {
            oldValue.getSettings().removeChangeListener(this);
        }
        // Add this as listener for settings of new own ship
        if (newValue != null) {
            newValue.getSettings().addChangeListener(this);
        }
    }

    /**
     * {@inheritDoc} This layer observes changes to the {@link VesselTargetSettings} associated with the {@link VesselTarget} that is set as own ship.
     */
    @Override
    public void showPastTrackUpdated(VesselTargetSettings source) {
        // Let PastTrackGraphic add/remove graphic elements according to updated visibility
        this.pastTrackGraphic.update(this.ownShipHandler.getAisTarget(), ownShipHandler.getPositionData().getPos());
        // repaint immediately to visualize hiding or showing past track
        this.doPrepare();
    }
    
    /**
     * Draws/updates own ship in outline mode.
     */
    private void drawOwnShipOutline() {
        if (this.ownShipGraphic != null) {
            // hide standard display of own ship
            this.ownShipGraphic.setVisible(false);
        }
        // init if this is the first time displaying ship outline
        if (this.vesselOutlineGraphic == null) {
            this.vesselOutlineGraphic = new VesselOutlineGraphicComponent(ColorConstants.OWNSHIP_COLOR, 2.0f);
            this.graphics.add(this.vesselOutlineGraphic);
        }
        // re-show outline graphic in case it was hidden by standard ownship graphic
        this.vesselOutlineGraphic.setVisible(true);
        this.vesselOutlineGraphic.update(this.ownShipHandler.getComputedAisTarget(), null, null, this.getProjection().getScale());
        
        // Handle resilient PNT error graphic
        if (rpntErrorGraphic == null) {
            rpntErrorGraphic = new RpntErrorGraphic();
            graphics.add(rpntErrorGraphic);
        }
        rpntErrorGraphic.setVisible(true);
        rpntErrorGraphic.update(ownShipHandler.getPositionData(), multiSourcePntHandler.getRpntData());
    }

    /**
     * Draws/updates own ship in the standard manner.
     * 
     * @param positionData
     *            the vessel position data
     * @return true if ship was successfully drawn (graphics data updated), false if ship could not be updated (due to insufficient
     *         data).
     */
    private boolean drawOwnShipStandard(VesselPositionData positionData) {
        if (this.vesselOutlineGraphic != null) {
            // hide outline display of own ship
            this.vesselOutlineGraphic.setVisible(false);
        }

        // Handle resilient PNT error graphic
        if (rpntErrorGraphic != null) {
            rpntErrorGraphic.setVisible(false);
        }

        // init if this is the first time displaying own ship in standard format
        if (this.ownShipGraphic == null) {
            this.ownShipGraphic = new OwnShipGraphic(this);
            this.graphics.add(this.ownShipGraphic);
        }
        // re-show standard ownship graphic in case it was hidden by outline graphic
        this.ownShipGraphic.setVisible(true);
        return this.ownShipGraphic.update(positionData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (ownShipHandler == null && obj instanceof OwnShipHandler) {
            ownShipHandler = (OwnShipHandler) obj;
            ownShipHandler.addListener(this);
            // Register this as listener for changes to VesselTargetSettings of own ship VesselTarget
            ownShipHandler.getAisTarget().getSettings().addChangeListener(this);
        }
        if (multiSourcePntHandler == null && obj instanceof MultiSourcePntHandler) {
            multiSourcePntHandler = (MultiSourcePntHandler) obj;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndUndo(Object obj) {
        if (ownShipHandler == obj) {
            ownShipHandler.removeListener(this);
            // Unregister this as listener for changes to VesselTargetSettings of own ship VesselTarget
            ownShipHandler.getAisTarget().getSettings().removeChangeListener(this);
            ownShipHandler = null;
        }
        if (multiSourcePntHandler == obj) {
            multiSourcePntHandler = null;
        }

        super.findAndUndo(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void projectionChanged(ProjectionEvent pe) {
        // the new zoom level
        ZoomLevel updatedZoomLevel = ZoomLevel.getFromScale(pe.getProjection().getScale());
        // did the zoom level change?
        boolean changeInZoomLevel = this.currentZoomLevel != updatedZoomLevel;
        // update zoom level
        this.currentZoomLevel = updatedZoomLevel;
        if (changeInZoomLevel) {
            // Zoom level was changed
            // May imply new ship draw mode so do a fake pnt update to check for change in drawing mode.
            ownShipUpdated(ownShipHandler);
        }
        super.projectionChanged(pe);
    }

    /**
     * {@inheritDoc}
     */
    protected void initMapMenu(OMGraphic clickedGraphics, MouseEvent evt) {        
        if (clickedGraphics == graphics) {
            ((MapMenu)getMapMenu()).ownShipMenu();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest, MouseEvent evt, Point containerPoint) {
        PastTrackWpCircle wpCircle = (PastTrackWpCircle) newClosest;
        pastTrackInfoPanel.showWpInfo(wpCircle);
        return true;
    }
}

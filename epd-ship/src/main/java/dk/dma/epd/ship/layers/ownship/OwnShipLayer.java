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
package dk.dma.epd.ship.layers.ownship;

import java.awt.event.MouseEvent;
import java.util.Date;

import com.bbn.openmap.event.MapEventUtils;
import com.bbn.openmap.event.MapMouseAdapter;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.ProjMath;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.layers.ais.PastTrackGraphic;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;
import dk.dma.epd.common.prototype.layers.ais.VesselOutlineGraphic;
import dk.dma.epd.common.prototype.sensor.rpnt.MultiSourcePntHandler;
import dk.dma.epd.common.prototype.zoom.ZoomLevel;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.gui.MapMenu;
import dk.dma.epd.ship.ownship.IOwnShipListener;
import dk.dma.epd.ship.ownship.OwnShipHandler;

/**
 * Defines the own-ship layer
 */
public class OwnShipLayer extends OMGraphicHandlerLayer implements IOwnShipListener, ProjectionListener {
    
    private static final long serialVersionUID = 1L;
    
    private MapMenu ownShipMenu;
    
    private OwnShipHandler ownShipHandler;
    private MultiSourcePntHandler multiSourcePntHandler;
    
    private long minRedrawInterval = 5 * 1000; // 5 sec
    
    private Date lastRedraw;
    private OMGraphicList graphics = new OMGraphicList();
    private LatLonPoint startPos;

    private Position lastPos;
    private Position currentPos;

    private OwnShipGraphic ownShipGraphic;
    private VesselOutlineGraphic vesselOutlineGraphic;
    private RpntErrorGraphic rpntErrorGraphic;
    
    private ZoomLevel currentZoomLevel;

    private PastTrackGraphic pastTrackGraphic;
    
    public OwnShipLayer() {
        graphics.setVague(true);
    }
    
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
        //System.out.println("dist: " + dist);
        if (dist > 10) { // 10 m
            return true;
        }
        
        return false;
    }
    
    /**
     * Called when the own-ship has been updated
     * @param ownShipHandler the {@code OwnShipHandler}
     */
    @Override
    public void ownShipUpdated(OwnShipHandler ownShipHandler) {
        if (ownShipHandler == null || ownShipHandler.getPositionData().getPos() == null) {
            return;
        }
        
        // Set location of ship
        currentPos = ownShipHandler.getPositionData().getPos();
        
        // check if proper zoom level and if data is available for ship outline drawing
        if(this.currentZoomLevel == ZoomLevel.VESSEL_OUTLINE && 
                ownShipHandler.getStaticData() != null) {
            this.drawOwnShipOutline(ownShipHandler.getPositionData(), ownShipHandler.getStaticData());
        } else {
            // draw standard version of own ship for all other zoom levels than VESSEL_OUTLINE
            this.drawOwnShipStandard(ownShipHandler.getPositionData());
        }
        
        // Add the past-track graphics the first time around
        if (pastTrackGraphic == null) {
            pastTrackGraphic = new PastTrackGraphic();
            this.graphics.add(pastTrackGraphic);
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
     * Draws/updates own ship in outline mode.
     * @param positionData the vessel position data
     * @param staticData the vessel static data
     */
    private void drawOwnShipOutline(VesselPositionData positionData, VesselStaticData staticData) {
        if(this.ownShipGraphic != null) {
            // hide standard display of own ship
            this.ownShipGraphic.setVisible(false);
        }
        // init if this is the first time displaying ship outline
        if(this.vesselOutlineGraphic == null) {
            this.vesselOutlineGraphic = new VesselOutlineGraphic(ColorConstants.EPD_SHIP_OWNSHIP_COLOR, 2.0f);
            this.graphics.add(this.vesselOutlineGraphic);
        }
        // re-show outline graphic in case it was hidden by standard ownship graphic
        this.vesselOutlineGraphic.setVisible(true);
        this.vesselOutlineGraphic.setLocation(positionData, staticData);
        
        // Handle resilient PNT error graphic
        if (rpntErrorGraphic == null) {
            rpntErrorGraphic = new RpntErrorGraphic();
            graphics.add(rpntErrorGraphic);
        }
        rpntErrorGraphic.setVisible(true);
        rpntErrorGraphic.update(positionData, multiSourcePntHandler.getRpntData());
    }
    
    /**
     * Draws/updates own ship in the standard manner.
     * @param positionData the vessel position data
     * @return true if ship was successfully drawn (graphics data updated),
     * false if ship could not be updated (due to insufficient data).
     */
    private boolean drawOwnShipStandard(VesselPositionData positionData) {
        if(this.vesselOutlineGraphic != null) {
            // hide outline display of own ship
            this.vesselOutlineGraphic.setVisible(false);
        }

        // Handle resilient PNT error graphic
        if (rpntErrorGraphic != null) {
            rpntErrorGraphic.setVisible(false);
        }
        
        // init if this is the first time displaying own ship in standard format
        if(this.ownShipGraphic == null) {
            this.ownShipGraphic = new OwnShipGraphic();
            this.graphics.add(this.ownShipGraphic);
        }
        // re-show standard ownship graphic in case it was hidden by outline graphic
        this.ownShipGraphic.setVisible(true);
        return this.ownShipGraphic.update(positionData);
    }
    
    public double[] calculateMinuteMarker(LatLonPoint startPoint, int minute) {
        VesselPositionData posData = ownShipHandler.getPositionData();
        float length = (float) Length.NM.toRadians(EPDShip.getSettings().getNavSettings().getCogVectorLength()/6 * minute * (posData.getSog() / 60.0));
        LatLonPoint marker = startPos.getPoint(length, (float) ProjMath.degToRad(posData.getCog()));
        double[] newMarker = {marker.getLatitude(), marker.getLongitude(), 0, 0};
        return newMarker;
    }
    
    
    @Override
    public synchronized OMGraphicList prepare() {
        if (getProjection() == null) {
            return graphics;
        }
        graphics.project(getProjection(), true);
        return graphics;
    }
    
    @Override
    public void findAndInit(Object obj) {
        if (ownShipHandler == null && obj instanceof OwnShipHandler) {
            ownShipHandler = (OwnShipHandler)obj;
            ownShipHandler.addListener(this);
        }
        if (multiSourcePntHandler == null && obj instanceof MultiSourcePntHandler) {
            multiSourcePntHandler = (MultiSourcePntHandler)obj;
        }
        if (obj instanceof MapMenu) {
            ownShipMenu = (MapMenu) obj;
        }
    }
    
    @Override
    public void findAndUndo(Object obj) {
        if (ownShipHandler == obj) {
            ownShipHandler.removeListener(this);
            ownShipHandler = null;
        }
        if (multiSourcePntHandler == obj) {
            multiSourcePntHandler = null;
        }
        if (ownShipMenu == obj) {
            ownShipMenu = null;
        }
    }
    
    @Override
    public void projectionChanged(ProjectionEvent pe) {
        // the new zoom level
        ZoomLevel updatedZoomLevel = ZoomLevel.getFromScale(pe.getProjection().getScale());
        // did the zoom level change?
        boolean changeInZoomLevel = this.currentZoomLevel != updatedZoomLevel;
        // update zoom level
        this.currentZoomLevel = updatedZoomLevel;
        if(changeInZoomLevel) {
            // Zoom level was changed
            // May imply new ship draw mode so do a fake pnt update to check for change in drawing mode.
            ownShipUpdated(ownShipHandler);
        }
        super.projectionChanged(pe);
    }

    /**
     * Returns the mouse listener for this layer
     * @return the mouse listener for this layer
     */
    @Override
    public MapMouseListener getMapMouseListener() {
        return new MapMouseAdapter() {
            @Override
            public String[] getMouseModeServiceList() {
                String[] ret = new String[2];
                ret[0] = NavigationMouseMode.MODE_ID; // "Gestures"
                ret[1] = DragMouseMode.MODE_ID;
                return ret;
            }

            @Override
            public boolean mouseClicked(MouseEvent evt) {
                OMGraphic ownShipGraphics = MapEventUtils.getSelectedGraphic(graphics, evt, 5.0f, OMGraphicList.class);
                if (ownShipGraphics == graphics && evt.getButton() == MouseEvent.BUTTON3) {
                    ownShipMenu.ownShipMenu();
                    ownShipMenu.setVisible(true);
                    ownShipMenu.show(OwnShipLayer.this, evt.getX() - 2, evt.getY() - 2);
                    return true;
                }
                return false;
            }
        };
    }
}

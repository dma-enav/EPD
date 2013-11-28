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
package dk.dma.epd.ship.layers.gps;

import java.util.Date;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.ProjMath;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.layers.ais.VesselOutlineGraphic;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.zoom.ZoomLevel;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ais.AisHandler;

public class OwnShipLayer extends OMGraphicHandlerLayer implements IPntDataListener, ProjectionListener {
    
    private static final long serialVersionUID = 1L;
    
    private PntHandler gpsHandler;
    private AisHandler aisHandler;
    
    private long minRedrawInterval = 5 * 1000; // 5 sec
    
    private Date lastRedraw;
    private PntData gpsData;
    private OMGraphicList graphics = new OMGraphicList();
    private LatLonPoint startPos;

    private Position lastPos;
    private Position currentPos;

    private OwnShipGraphic ownShipGraphic;
    private VesselOutlineGraphic vesselOutlineGraphic;
    private ZoomLevel currentZoomLevel;
    
    public OwnShipLayer() {
        graphics.setVague(true);
    }
    
    private synchronized boolean doUpdate() {
        if (this.gpsData == null || lastRedraw == null || lastPos == null) {
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
    
    @Override
    public synchronized void pntDataUpdate(PntData pntData) {
        if (pntData == null || pntData.getPosition() == null) {
            return;
        }
        
        VesselTarget ownShip = null;
        if (aisHandler != null) {
            ownShip = aisHandler.getOwnShip();
        }
        
        // Set location of ship
        currentPos = pntData.getPosition();
        
        // check if proper zoom level and if data is available for ship outline drawing
        if(this.currentZoomLevel == ZoomLevel.VESSEL_OUTLINE && ownShip != null && ownShip.getStaticData() != null && ownShip.getPositionData() != null)
        {
            this.drawOwnShipOutline(ownShip);
        }
        else if(ownShip != null) {
            // draw standard version of own ship for all other zoom levels than VESSEL_OUTLINE
            this.drawOwnShipStandard(ownShip);
        }
        
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
     * @param ownShip The VesselTarget object that is currently selected as own ship.
     */
    private void drawOwnShipOutline(VesselTarget ownShip) {
        if(this.ownShipGraphic != null) {
            // hide standard display of own ship
            this.ownShipGraphic.setVisible(false);
        }
        // init if this is the first time displaying ship outline
        if(this.vesselOutlineGraphic == null) {
            this.vesselOutlineGraphic = new VesselOutlineGraphic();
            this.graphics.add(this.vesselOutlineGraphic);
        }
        // re-show outline graphic in case it was hidden by standard ownship graphic
        this.vesselOutlineGraphic.setVisible(true);
        this.vesselOutlineGraphic.setLocation(ownShip);
    }
    
    /**
     * Draws/updates own ship in the standard manner.
     * @param ownShip The VesselTarget object that is currently selected as own ship.
     * @return true if ship was successfully drawn (graphics data updated),
     * false if ship could not be updated (due to insufficient data).
     */
    private boolean drawOwnShipStandard(VesselTarget ownShip) {
        if(this.vesselOutlineGraphic != null) {
            // hide outline display of own ship
            this.vesselOutlineGraphic.setVisible(false);
        }
        // init if this is the first time displaying own ship in standard format
        if(this.ownShipGraphic == null) {
            this.ownShipGraphic = new OwnShipGraphic();
            this.graphics.add(this.ownShipGraphic);
        }
        // re-show standard ownship graphic in case it was hidden by outline graphic
        this.ownShipGraphic.setVisible(true);
        return this.ownShipGraphic.update(ownShip);
    }
    
    public double[] calculateMinuteMarker(LatLonPoint startPoint, int minute){
        float length = (float) Length.NM.toRadians(EPDShip.getSettings().getNavSettings().getCogVectorLength()/6 * minute * (gpsData.getSog() / 60.0));
        LatLonPoint marker = startPos.getPoint(length, (float) ProjMath.degToRad(gpsData.getCog()));
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
        if (gpsHandler == null && obj instanceof PntHandler) {
            gpsHandler = (PntHandler)obj;
            gpsHandler.addListener(this);
        }
        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler)obj;
        }
    }
    
    @Override
    public void findAndUndo(Object obj) {
        if (gpsHandler == obj) {
            gpsHandler.removeListener(this);
            gpsHandler = null;
        }
        if (aisHandler == obj) {
            aisHandler = null;
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
        VesselTarget ownShip = aisHandler.getOwnShip();
        if(ownShip != null && changeInZoomLevel) {
            // Zoom level was changed
            // May imply new ship draw mode so do a fake pnt update to check for change in drawing mode.
            this.pntDataUpdate(gpsHandler.getCurrentData());
        }
        super.projectionChanged(pe);
    }
}

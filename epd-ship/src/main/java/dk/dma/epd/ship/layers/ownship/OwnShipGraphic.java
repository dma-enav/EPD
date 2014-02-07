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

import java.awt.BasicStroke;
import java.awt.Stroke;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;

/**
 * @author Janus Varmarken
 */
public class OwnShipGraphic extends OMGraphicList {

    /**
     * Default.
     */
    private static final long serialVersionUID = 1L;
    
    private static final float STROKE_WIDTH = 1.5f;
    
    private OMCircle circle1; 
    private OMCircle circle2;
    
    /**
     * Visualization of own ship COG and speed vector.
     */
    private OwnShipSpeedVectorGraphic speedVector;
    
    /**
     * Line parallel with the Vessel's stern (assuming the stern straight).
     */
    private RotationalPoly angularVector;
    
    /**
     * A line that visualizes the Vessel's true heading.
     */
    private RotationalPoly directionVector;
    
    /**
     * The layer where this graphic is drawn.
     */
    private OMGraphicHandlerLayer parentLayer;
    
    /**
     * The most recent PNT update.
     */
    private VesselPositionData lastUpdate;
    
    // these are the two arrow heads at the end of the speed vector.
    
    private LatLonPoint startPos;
    
    private double headingRadian;
    private Position currentPos;
    
    /**
     * Create an OwnShipGraphic.
     * @param parentLayer The layer where this graphic is to be drawn.
     */
    public OwnShipGraphic(OMGraphicHandlerLayer parentLayer) {
        this.parentLayer = parentLayer;
        this.setVague(true);
        Stroke stroke = new BasicStroke(STROKE_WIDTH);
        this.circle1 = new OMCircle(0, 0, 0, 0, 18, 18);
        this.circle2 = new OMCircle(0, 0, 0, 0, 8, 8);
        this.circle1.setStroke(stroke);
        this.circle2.setStroke(stroke);
        this.speedVector = new OwnShipSpeedVectorGraphic(ColorConstants.OWNSHIP_HEADING_COLOR);
        
        int[] angularX = {-20,20};
        int[] angularY = {0,0};
        angularVector = new RotationalPoly(angularX, angularY, null, ColorConstants.OWNSHIP_HEADING_COLOR);
        int[] directionX = {0,0};
        int[] directionY = {0,-200};
        directionVector = new RotationalPoly(directionX, directionY, stroke, ColorConstants.OWNSHIP_HEADING_COLOR);
        
        this.add(this.circle1);
        this.add(this.circle2);
        this.add(this.speedVector);
        this.add(this.angularVector);
        this.add(this.directionVector);
    }
    
    public boolean update(VesselPositionData ownShipData) {
        if(ownShipData.getPos() == null) {
            return false;
        }
        this.lastUpdate = ownShipData;
        
        double heading = 0.0;
        
        heading = ownShipData.getCog();
        
        if(ownShipData.getTrueHeading() <= 360){
            heading = ownShipData.getTrueHeading();
        }
        
        if(ownShipData != null && ownShipData.getTrueHeading() <= 360){
            heading = ownShipData.getTrueHeading();
        }
        
        this.headingRadian = Math.toRadians(heading);
        
        // Set location of ship
        this.currentPos = ownShipData.getPos();
        this.circle1.setLatLon(this.currentPos.getLatitude(), this.currentPos.getLongitude());
        this.circle2.setLatLon(this.currentPos.getLatitude(), this.currentPos.getLongitude());
        
        this.startPos = new LatLonPoint.Double(this.currentPos.getLatitude(), this.currentPos.getLongitude());
        
        if (parentLayer != null && parentLayer.getProjection() != null) {
            this.speedVector.update(ownShipData, parentLayer.getProjection().getScale());
        }
        this.angularVector.setLocation(this.startPos.getLatitude(), this.startPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, this.headingRadian);
        this.directionVector.setLocation(this.startPos.getLatitude(), this.startPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, this.headingRadian);

        return true;
    }
    
    @Override
    public boolean generate(Projection p, boolean forceProjectAll) {
        if(this.lastUpdate != null) {
            // force an update to apply possible change to positions of arrow heads (in case a scale change has occurred)
            this.update(this.lastUpdate);
        }
        return super.generate(p, forceProjectAll);
    }
}

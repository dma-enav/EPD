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

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.ProjMath;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.ship.EPDShip;

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
    private OMLine speedVector;
    private RotationalPoly angularVector;
    private RotationalPoly directionVector;
    private RotationalPoly frontShipArrow;
    private RotationalPoly backShipArrow;
    private OMGraphicList marks;
    private LatLonPoint endPos;
    private LatLonPoint startPos;
    
    private int[] markX = {-5,5};
    private int[] markY = {0,0};
    
    private double headingRadian;
    private Position currentPos;
    
    public OwnShipGraphic() {
        this.setVague(true);
        Stroke stroke = new BasicStroke(STROKE_WIDTH);
        this.circle1 = new OMCircle(0, 0, 0, 0, 18, 18);
        this.circle2 = new OMCircle(0, 0, 0, 0, 8, 8);
        this.circle1.setStroke(stroke);
        this.circle2.setStroke(stroke);
        this.speedVector = new OMLine(0d, 0d, 0d, 0d, OMGraphicConstants.LINETYPE_STRAIGHT);
        this.speedVector.setStroke(new BasicStroke(
                STROKE_WIDTH,                      // Width
                BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_MITER,    // Join style
                10.0f,                     // Miter limit
                new float[] { 10.0f, 8.0f }, // Dash pattern
                0.0f)                     // Dash phase
        );
        this.marks = new OMGraphicList();
        
        int[] angularX = {-20,20};
        int[] angularY = {0,0};
        angularVector = new RotationalPoly(angularX, angularY, null, null);
        int[] directionX = {0,0};
        int[] directionY = {0,-200};
        directionVector = new RotationalPoly(directionX, directionY, stroke, null);
        int[] frontArrowX = {5,0,-5};
        int[] frontArrowY = {10,0,10};
        frontShipArrow = new RotationalPoly(frontArrowX, frontArrowY, stroke, null);
        int[] backArrowX = {5,0,-5};
        int[] backArrowY = {20,10,20};
        backShipArrow = new RotationalPoly(backArrowX, backArrowY, stroke, null);
        
        this.add(this.circle1);
        this.add(this.circle2);
        this.add(this.speedVector);
        this.add(this.marks);
        this.add(this.backShipArrow);
        this.add(this.frontShipArrow);
        this.add(this.angularVector);
        this.add(this.directionVector);
    }
    
    public boolean update(VesselTarget ownShip) {
        VesselPositionData ownShipData = null;
        if(ownShip == null || (ownShipData = ownShip.getPositionData()) == null || ownShipData.getPos() == null) {
            return false;
        }
        
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
        
        // Calculate speed vector
        this.startPos = new LatLonPoint.Double(this.currentPos.getLatitude(), this.currentPos.getLongitude());
        float length = (float) Length.NM.toRadians(EPDShip.getSettings().getNavSettings().getCogVectorLength() * (ownShipData.getSog() / 60.0));
        this.endPos = this.startPos.getPoint(length, (float) ProjMath.degToRad(ownShipData.getCog()));
        double[] newLLPos = {this.startPos.getLatitude(), this.startPos.getLongitude(), this.endPos.getLatitude(), this.endPos.getLongitude()};
        Double cogRadian = Math.toRadians(ownShipData.getCog());
        
        this.speedVector.setLL(newLLPos);
        this.angularVector.setLocation(this.startPos.getLatitude(), this.startPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, this.headingRadian);
        this.directionVector.setLocation(this.startPos.getLatitude(), this.startPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, this.headingRadian);
        this.frontShipArrow.setLocation(this.endPos.getLatitude(), this.endPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, cogRadian);
        this.backShipArrow.setLocation(this.endPos.getLatitude(), this.endPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, cogRadian);
        
        this.marks.clear();
        for (int i = 0; i < 6; i++) {
            float markLength = (float) Length.NM.toRadians(EPDShip.getSettings().getNavSettings().getCogVectorLength()/6 * i * (ownShipData.getSog() / 60.0));
            LatLonPoint marker = this.startPos.getPoint(markLength, cogRadian);
            RotationalPoly polyMark = new RotationalPoly(this.markX, this.markY, new BasicStroke(STROKE_WIDTH), null);
            polyMark.setLocation(marker.getLatitude(), marker.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, cogRadian);
            this.marks.add(polyMark);
        }
        
        if(ownShipData.getSog() < 0.1){
            backShipArrow.setVisible(false);
            frontShipArrow.setVisible(false);
        } else {
            backShipArrow.setVisible(true);
            frontShipArrow.setVisible(true);
        }
        
        return true;
    }
    
}

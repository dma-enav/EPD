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

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Date;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.ProjMath;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.math.Vector2D;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ais.AisHandler;

public class OwnShipLayer extends OMGraphicHandlerLayer implements IPntDataListener {
    
    private static final long serialVersionUID = 1L;
    
    private static final float STROKE_WIDTH = 1.5f;
    
    private PntHandler gpsHandler;
    private AisHandler aisHandler;
    
    private long minRedrawInterval = 5 * 1000; // 5 sec
    
    private Date lastRedraw;
    private PntData gpsData;
    private OMGraphicList graphics = new OMGraphicList();
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
    private Vector2D vector = new Vector2D();
    private int[] markX = {-5,5};
    private int[] markY = {0,0};

    private double headingRadian;
    private Position lastPos;
    private Position currentPos;

    public OwnShipLayer() {
        graphics.setVague(true);
        Stroke stroke = new BasicStroke(STROKE_WIDTH);
        circle1 = new OMCircle(0, 0, 0, 0, 18, 18);
        circle2 = new OMCircle(0, 0, 0, 0, 8, 8);
        circle1.setStroke(stroke);
        circle2.setStroke(stroke);
        speedVector = new OMLine(0d, 0d, 0d, 0d, OMGraphicConstants.LINETYPE_STRAIGHT);
        speedVector.setStroke(new BasicStroke(
                STROKE_WIDTH,                      // Width
                BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_MITER,    // Join style
                10.0f,                     // Miter limit
                new float[] { 10.0f, 8.0f }, // Dash pattern
                0.0f)                     // Dash phase
        );
        marks = new OMGraphicList();
        
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
        if (this.gpsData == null) {
            graphics.add(circle1);
            graphics.add(circle2);
            graphics.add(speedVector);
            graphics.add(marks);
            graphics.add(backShipArrow);
            graphics.add(frontShipArrow);
            graphics.add(angularVector);
            graphics.add(directionVector);
        }
        
        this.gpsData = gpsData;
        
        double heading = 0;
        if (gpsData.getCog() != null) {
            heading = gpsData.getCog();
        }
        
        VesselTarget ownShip = null;
        VesselPositionData ownShipData = null;
        if (aisHandler != null) {
            ownShip = aisHandler.getOwnShip();
        }
        
        if (ownShip != null) {
            ownShipData = ownShip.getPositionData();
            if(ownShipData != null && ownShipData.getTrueHeading() <= 360){
                heading = ownShipData.getTrueHeading();
            }
        }
        
        headingRadian = Math.toRadians(heading);
        
        // Set location of ship
        currentPos = gpsData.getPosition();        
        circle1.setLatLon(currentPos.getLatitude(), currentPos.getLongitude());
        circle2.setLatLon(currentPos.getLatitude(), currentPos.getLongitude());
        
        // Calculate speed vector
        if (gpsData.getCog() != null && gpsData.getSog() != null) {
            startPos = new LatLonPoint.Double(currentPos.getLatitude(), currentPos.getLongitude());
            float length = (float) Length.NM.toRadians(EPDShip.getSettings().getNavSettings().getCogVectorLength() * (gpsData.getSog() / 60.0));
            endPos = startPos.getPoint(length, (float) ProjMath.degToRad(gpsData.getCog()));
            double[] newLLPos = {startPos.getLatitude(), startPos.getLongitude(), endPos.getLatitude(), endPos.getLongitude()};
            Double cogRadian = Math.toRadians(gpsData.getCog());
            
            speedVector.setLL(newLLPos);
            angularVector.setLocation(startPos.getLatitude(), startPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES,headingRadian);
            directionVector.setLocation(startPos.getLatitude(), startPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, headingRadian);
            frontShipArrow.setLocation(endPos.getLatitude(), endPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, cogRadian);
            backShipArrow.setLocation(endPos.getLatitude(), endPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, cogRadian);
            
            marks.clear();
            for (int i = 0; i < 6; i++) {
                float markLength = (float) Length.NM.toRadians(EPDShip.getSettings().getNavSettings().getCogVectorLength()/6 * i * (gpsData.getSog() / 60.0));
                LatLonPoint marker = startPos.getPoint(markLength, cogRadian);
                RotationalPoly polyMark = new RotationalPoly(markX, markY, new BasicStroke(STROKE_WIDTH), null);
                polyMark.setLocation(marker.getLatitude(), marker.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, cogRadian);
                marks.add(polyMark);
            }
            
        }
        
        // Redraw    
        if (!doUpdate()) {
            graphics.project(getProjection(), true);
            //System.out.println("Dropping update");
            return;
        }
        
        //System.out.println("Doing redraw");
        lastPos =Position.create(currentPos.getLatitude(), currentPos.getLongitude());
        lastRedraw = new Date();                
        doPrepare();
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
        if(startPos != null && endPos != null){
            Point2D start = getProjection().forward(startPos);
            Point2D end = getProjection().forward(endPos);
            vector.setX1(start.getX());
            vector.setY1(start.getY());
            vector.setX2(end.getX());
            vector.setY2(end.getY());
            if(vector.norm() < EPDShip.getSettings().getNavSettings().getShowMinuteMarksSelf()){
                marks.setVisible(false);
            } else {
                marks.setVisible(true);
            }
        }
        if(gpsData != null && gpsData.getSog() != null && gpsData.getSog() < 0.1){
            backShipArrow.setVisible(false);
            frontShipArrow.setVisible(false);
        } else {
            backShipArrow.setVisible(true);
            frontShipArrow.setVisible(true);
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
    
    
}

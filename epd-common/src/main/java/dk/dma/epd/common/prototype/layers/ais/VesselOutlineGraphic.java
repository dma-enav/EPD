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
package dk.dma.epd.common.prototype.layers.ais;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;

/**
 * @author Janus Varmarken
 */
public class VesselOutlineGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;
    
    public static final float STROKE_WIDTH = 1.0f;

    /**
     * Displays the ship outline
     */
    private OMPoly shipOutline;
    
    /**
     * Displays the position of the PNT device.
     */
    private OMCircle pntDevice;

    /**
     * The Vessel displayed by this VesselOutlineGrahpic
     */
    private VesselTarget vessel;

    /**
     * Displays a COG vector.
     */
    private RotationalPoly cogVector;
    
    /**
     * Ship COG stroke
     */
    private Stroke cogStroke = new BasicStroke(STROKE_WIDTH);
    
    private Color lineColor;
    
    private float lineThickness = 1.0f;
    
    private BasicStroke lineStroke;
    
    public VesselOutlineGraphic(Color lineColor, float lineThickness) {
        this.setVague(true);
        this.lineColor = lineColor;
        this.lineThickness = lineThickness;
        this.lineStroke = new BasicStroke(this.lineThickness);
    }

    private void producePolygon(VesselTarget vessel) {
        this.vessel = vessel;
        // TODO add null check on static data here or in client?
        VesselStaticData vsd = this.vessel.getStaticData();
        // Get angle from GPS to lower left corner of ship
        double anglLowerLeft = this.calcAngleFromCenter(vsd.getDimStern(),
                vsd.getDimPort());
        // calculate distance to lower left corner of vessel (Pythagoras)
        double distLowerLeftCorner = Math.sqrt(Math.pow(vsd.getDimStern(), 2.0)
                + Math.pow(vsd.getDimPort(), 2.0));
        
        float heading = vessel.getPositionData().getTrueHeading();

        anglLowerLeft += heading + 180;
        
        if(360 <= anglLowerLeft) {
            anglLowerLeft -= 360.0;
        }
        Position vessPos = vessel.getPositionData().getPos();
        double lat = vessel.getPositionData().getPos().getLatitude();
        double lon = vessel.getPositionData().getPos().getLongitude();
        
        // find latlon of lower left corner of ship
        Position leftSideBottomLL = CoordinateSystem.CARTESIAN.pointOnBearing(vessPos,
                distLowerLeftCorner, anglLowerLeft);

        double shipFullLength = vsd.getDimBow() + vsd.getDimStern();
        double shipSideLength = shipFullLength * 0.85;
        double shipSternWidth = vsd.getDimPort() + vsd.getDimStarboard();
        
        // Not a point in the final polygon, simply used for finding polygon points in the bow.
        Position outerRectTopLeftLL = CoordinateSystem.CARTESIAN.pointOnBearing(leftSideBottomLL, shipFullLength, 0.0 + heading);
        
        // Point on port side of ship where the bow begins.
        Position leftSideTopLL = CoordinateSystem.CARTESIAN.pointOnBearing(leftSideBottomLL, shipSideLength, 0.0 + heading);
        
        // Left point in ship's tip 
        Position bowLeftLL = CoordinateSystem.CARTESIAN.pointOnBearing(outerRectTopLeftLL, shipSternWidth / 4.0, 90.0 + heading);
        // right point in ship's tip
        Position bowRightLL = CoordinateSystem.CARTESIAN.pointOnBearing(bowLeftLL, shipSternWidth / 2.0, 90.0 + heading);
        // find lat lon of lower right corner of ship
        Position rightSideBottomLL = CoordinateSystem.CARTESIAN.pointOnBearing(leftSideBottomLL, shipSternWidth, 90.0 + heading);
        
        // Point on starboard side of ship where the bow begins.
        Position rightSideTopLL = CoordinateSystem.CARTESIAN.pointOnBearing(leftSideTopLL, shipSternWidth, 90.0 + heading);

        int[] xs = {0,0};
        int[] ys = {0,-100};
        if(this.cogVector == null) {
            this.cogVector = new RotationalPoly(xs, ys, cogStroke, null);
            this.add(this.cogVector);
        }
        // don't show COG vector if vessel is docked
        this.cogVector.setVisible(vessel.getPositionData().getSog() > 0.1);
        this.cogVector.setLocation(lat, lon, OMGraphic.DECIMAL_DEGREES, Math.toRadians(this.vessel.getPositionData().getCog()));
        // clear old PntDevice display
        this.remove(this.pntDevice);
        this.pntDevice = new OMCircle(lat, lon, 3, 3);
        this.pntDevice.setFillPaint(this.lineColor);
        this.add(pntDevice);
        
        double[] shipCorners = new double[14];
        shipCorners[0] = leftSideBottomLL.getLatitude();
        shipCorners[1] = leftSideBottomLL.getLongitude();
        shipCorners[2] = leftSideTopLL.getLatitude();
        shipCorners[3] = leftSideTopLL.getLongitude();
        shipCorners[4] = bowLeftLL.getLatitude();
        shipCorners[5] = bowLeftLL.getLongitude();
        shipCorners[6] = bowRightLL.getLatitude();
        shipCorners[7] = bowRightLL.getLongitude();
        shipCorners[8] = rightSideTopLL.getLatitude();
        shipCorners[9] = rightSideTopLL.getLongitude();
        shipCorners[10] = rightSideBottomLL.getLatitude();
        shipCorners[11] = rightSideBottomLL.getLongitude();
        // end poly where it begun (to create a closed shape)
        shipCorners[12] = shipCorners[0];
        shipCorners[13] = shipCorners[1];
        // clear old shape
        this.remove(this.shipOutline);
        // create and add new shape
        this.shipOutline = new OMPoly(shipCorners, OMGraphic.DECIMAL_DEGREES, OMGraphic.LINETYPE_RHUMB);
        this.add(this.shipOutline);
        this.setLinePaint(this.lineColor);
        this.setStroke(this.lineStroke);
    }

    public void setLocation(VesselTarget vessel) {
        this.producePolygon(vessel);
    }

    /**
     * Assumes a triangle:      B
     *                       c /|a
     *                        /_|
     *                       A b C Calculates the value of angle B.
     * 
     * @param a
     *            length of side a
     * @param b
     *            length of side b
     * @return the angle B in degrees
     */
    private double calcAngleFromCenter(float a, float b) {
        // Use Pythagoras to find the distance to the corner
        double dist = Math.sqrt(a * a + b * b);
        // find angle A
        double angleA = Math.toDegrees(Math.asin(a / dist));

        // find angle B
        double angleB = 180.0 - 90.0 - angleA;
        return angleB;
    }
}

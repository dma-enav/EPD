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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.omGraphics.OMText;

import dk.dma.ais.message.AisMessage;
import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.ISelectableGraphic;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;

/**
 * @author Janus Varmarken
 */
public class VesselOutlineGraphic extends OMGraphicList implements ISelectableGraphic {

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
     * The COG/Speed vector
     */
    private SpeedVectorGraphic speedVector;

    private Color lineColor;

    /**
     * Color used to draw ship outline when this graphic is selected.
     */
    private Color selectionColor = Color.GREEN;
    
    private float lineThickness = 1.0f;

    private BasicStroke lineStroke;

    /**
     * The layer that displays this VesselOutlineGraphic. If this VesselOutlineGraphic is a subgraphic of another graphic, use the
     * top level graphic's parent layer.
     */
    private OMGraphicHandlerLayer parentLayer;

    /**
     * The VesselTargetGraphics which created this object.
     */
    private VesselTargetGraphic vesselTargetGraphic;
    private OMText aisName;
    private boolean showNameLabel = true;

    private Font font;

    public VesselOutlineGraphic(Color lineColor, float lineThickness, OMGraphicHandlerLayer parentLayer,
            VesselTargetGraphic vesselTargetGraphic) {
        this.vesselTargetGraphic = vesselTargetGraphic;
        this.setVague(true);
        this.lineColor = lineColor;
        this.lineThickness = lineThickness;
        this.lineStroke = new BasicStroke(this.lineThickness);
        this.parentLayer = parentLayer;
        
        this.font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
        this.aisName = new OMText(0, 0, 0, 0, "", font, OMText.JUSTIFY_CENTER);
        this.setLinePaint(Color.BLACK);
        this.add(aisName);
    }

    /**
     * Produces the vessel outline polygon based on the given vessel position and static data
     * 
     * @param positionData
     *            the vessel position data
     * @param staticData
     *            the vessel static data
     */
    private void producePolygon(VesselPositionData positionData, VesselStaticData staticData) {

        // Get angle from PNT to lower left corner of ship
        double anglLowerLeft = this.calcAngleFromCenter(staticData.getDimStern(), staticData.getDimPort());
        // calculate distance to lower left corner of vessel (Pythagoras)
        double distLowerLeftCorner = Math.sqrt(Math.pow(staticData.getDimStern(), 2.0) + Math.pow(staticData.getDimPort(), 2.0));

        float heading = positionData.getTrueHeading();

        anglLowerLeft += heading + 180;

        if (360 <= anglLowerLeft) {
            anglLowerLeft -= 360.0;
        }
        Position vessPos = positionData.getPos();
        double lat = positionData.getPos().getLatitude();
        double lon = positionData.getPos().getLongitude();

        // find latlon of lower left corner of ship
        Position leftSideBottomLL = CoordinateSystem.CARTESIAN.pointOnBearing(vessPos, distLowerLeftCorner, anglLowerLeft);

        double shipFullLength = staticData.getDimBow() + staticData.getDimStern();
        double shipSideLength = shipFullLength * 0.85;
        double shipSternWidth = staticData.getDimPort() + staticData.getDimStarboard();

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

        if (this.speedVector == null) {
            this.speedVector = new SpeedVectorGraphic(this.lineColor);
            this.add(this.speedVector);
        }
        // don't show COG vector if vessel is docked
        this.speedVector.setVisible(positionData.getSog() > 0.1);
        if (parentLayer != null && parentLayer.getProjection() != null) {
            this.speedVector.update(positionData, this.parentLayer.getProjection().getScale());
        }
        
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
        // Fill the polygon with an invisible color - helps the AisLayer to keep showing the infopanel,
        // when mouse is hovering the outline of the vessel.
        this.shipOutline.setFillPaint(new Color(0, 0, 0, 1));

        this.add(this.shipOutline);
        this.setLinePaint(this.lineColor);
        // this.setStroke(this.lineStroke);
        this.shipOutline.setStroke(this.lineStroke);
        
        
        
        
        this.aisName.setLon(positionData.getPos().getLongitude());
        this.aisName.setLat(positionData.getPos().getLatitude());
        this.aisName.setY(-20);
        this.aisName.setData(AisMessage.trimText(staticData.getName()));
        this.aisName.setVisible(this.showNameLabel);
        
    }

    /**
     * Produces the vessel outline polygon based on the given vessel position and static data
     * 
     * @param positionData
     *            the vessel position data
     * @param staticData
     *            the vessel static data
     */
    public void setLocation(VesselPositionData positionData, VesselStaticData staticData) {
        
        this.producePolygon(positionData, staticData);
    }

    /**
     * Assumes a triangle:         B 
     *                           c/|a 
     *                           /_| 
     *                          A b C 
     * 
     * Calculates the value of angle B.
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

    /**
     * Turn on anti-aliasing
     */
    @Override
    public void render(Graphics g) {
        Graphics2D image = (Graphics2D) g;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }

    public VesselTargetGraphic getVesselTargetGraphic() {
        return vesselTargetGraphic;

    }
    
    public void setShowNameLabel(boolean showNameLabel) {
        this.showNameLabel = showNameLabel;
    }

    public boolean getShowNameLabel() {
        return showNameLabel;
    }

    @Override
    public void setSelection(boolean selected) {
        if(this.shipOutline != null) {
            // Simply change the color of the outline when selected.
            if(selected) {
                this.shipOutline.setLinePaint(this.selectionColor); 
            }
            else {
                this.shipOutline.setLinePaint(this.lineColor);
            }
        }
    }
}

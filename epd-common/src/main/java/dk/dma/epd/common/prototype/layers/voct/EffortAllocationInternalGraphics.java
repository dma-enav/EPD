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
package dk.dma.epd.common.prototype.layers.voct;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.Intersection;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.proj.Length;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Converter;

/**
 * Graphic for effective area for a SRU
 */
@SuppressWarnings("unused")
public class EffortAllocationInternalGraphics extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    private Rectangle hatchFillRectangle;
    private BufferedImage hatchFill;

    private OMPoly poly;

    private boolean frame;
    Double height;
    Double width;

    EffortAllocationAreaGraphics effecticeSRUAreaGraphics;

    Position A;
    Position B;
    Position C;
    Position D;

    Double distanceToTop;
    Double distanceToBottom;
    Double distanceToLeft;
    Double distanceToRight;

    Position relativePosition;

    double verticalBearing = 180;
    double horizontalBearing = 90;

    private Font font = new Font(Font.DIALOG, Font.PLAIN, 12);
    private OMText label = new OMText(0, 0, 0, 0, "", font,
            OMText.JUSTIFY_CENTER);

    // Initialize with
    public EffortAllocationInternalGraphics(Position A, Position B, Position C, Position D,
            Double width, Double height,
            EffortAllocationAreaGraphics effecticeSRUAreaGraphics,
            double verticalBearing, double horizontalBearing, String labelText) {
        super();

        this.setVague(true);

        this.height = height;
        this.width = width;

        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;

        this.effecticeSRUAreaGraphics = effecticeSRUAreaGraphics;

        this.verticalBearing = verticalBearing;
        this.horizontalBearing = horizontalBearing;

        // Draw the data

        hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D big = hatchFill.createGraphics();
        Composite originalComposite = big.getComposite();
        big.setComposite(makeComposite(0.1f));
        big.setColor(Color.red);
        big.drawLine(0, 0, 10, 10);

        hatchFillRectangle = new Rectangle(0, 0, 10, 10);
        big.setComposite(originalComposite);

        // Given a startPos, a width and length calculate other points.

        // // First top side of the box
        // Position topCenter = findPosition(datum, 0,
        // Converter.nmToMeters(radius));
        //
        // // Bottom side of the box
        // Position bottomCenter = findPosition(datum, 180,
        // Converter.nmToMeters(radius));

        // Go left radius length
        // Position A = findPosition(topCenter, 270,
        // Converter.nmToMeters(radius));

        // Position A = startPos;
        // Position B = Calculator
        // .findPosition(A, 90, Converter.nmToMeters(width));
        //
        // Position D = Calculator.findPosition(A, 180,
        // Converter.nmToMeters(length));
        // Position C = Calculator
        // .findPosition(D, 90, Converter.nmToMeters(width));

        drawPolygon();

        if (!labelText.equals("")) {

            Position topCenter = Calculator.findPosition(A, horizontalBearing,
                    Converter.nmToMeters(width) / 2);

            Position center = Calculator.findPosition(topCenter,
                    verticalBearing, Converter.nmToMeters(height) / 2);

            double lat = center.getLatitude();
            double lon = center.getLongitude();

            label.setLat(lat);
            label.setLon(lon);
//            label.setY(25);
            label.setLinePaint(Color.black);
            label.setTextMatteColor(Color.WHITE);
            label.setData(labelText);
            add(label);

            // label.getData()
        }

    }

    public void updatePosition(Position A, Position B, Position C, Position D,
            Double width, Double height) {

        graphics.clear();
        this.height = height;
        this.width = width;

        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;

        drawPolygon();

        checkLabel();

    }

    private void checkLabel() {
        if (!label.getData().equals("")) {

            Position topCenter = Calculator.findPosition(A, horizontalBearing,
                    Converter.nmToMeters(width) / 2);

            Position center = Calculator.findPosition(topCenter,
                    verticalBearing, Converter.nmToMeters(height) / 2);

            double lat = center.getLatitude();
            double lon = center.getLongitude();

            label.setLat(lat);
            label.setLon(lon);

            add(label);
        }
    }

    public void moveRelative(Position newPos, SARData data) {

        // relativePosition is

        graphics.clear();

        // find topside of box
        // distance from mouseOffset to

        // First top side of the box
        Position topSide = Calculator.findPosition(newPos,
                Calculator.reverseDirection(verticalBearing),
                Converter.nmToMeters(distanceToTop));

        // Bottom side of the box
        Position bottomSide = Calculator.findPosition(newPos, verticalBearing,
                Converter.nmToMeters(distanceToBottom));

        // Go left radius length
        A = Calculator.findPosition(topSide,
                Calculator.reverseDirection(horizontalBearing),
                Converter.nmToMeters(distanceToLeft));

        B = Calculator.findPosition(topSide, horizontalBearing,
                Converter.nmToMeters(distanceToRight));

        C = Calculator.findPosition(bottomSide,
                Calculator.reverseDirection(horizontalBearing),
                Converter.nmToMeters(distanceToLeft));

        D = Calculator.findPosition(bottomSide, horizontalBearing,
                Converter.nmToMeters(distanceToRight));

        drawPolygon();

        effecticeSRUAreaGraphics.updateLines(A, B, C, D);

        // data.getEffortAllocationData().setEffectiveAreaA(A);
        // data.getEffortAllocationData().setEffectiveAreaB(B);
        // data.getEffortAllocationData().setEffectiveAreaC(C);
        // data.getEffortAllocationData().setEffectiveAreaD(D);

//        System.out.println("Polygon created");

        checkLabel();
    }

    public void adjustInternalPosition(Position relativePosition) {

        // Find the top point from the current Position

        // Create a line going from relativePosition and in reverse vertical
        // bearing - find the intersection between this position and the line
        // going from A to B
        Position verticalEndPosition = Calculator.findPosition(
                relativePosition, Calculator.reverseDirection(verticalBearing),
                Converter.nmToMeters(height));

        Geo a1 = new Geo(relativePosition.getLatitude(),
                relativePosition.getLongitude());
        Geo a2 = new Geo(verticalEndPosition.getLatitude(),
                verticalEndPosition.getLongitude());

        Geo b1 = new Geo(A.getLatitude(), A.getLongitude());
        Geo b2 = new Geo(B.getLatitude(), B.getLongitude());

        Geo intersectionPoint = Intersection.segmentsIntersect(a1, a2, b1, b2);

        Position topPoint = Position.create(intersectionPoint.getLatitude(),
                intersectionPoint.getLongitude());

        Position bottomPoint = Calculator.findPosition(topPoint,
                verticalBearing, Converter.nmToMeters(height));

        // Create a line going from relativePosition and in reverse horizontal
        // bearing - find the intersection between this position and the line
        // going from A to C
        Position horizontalEndPosition = Calculator.findPosition(
                relativePosition,
                Calculator.reverseDirection(horizontalBearing),
                Converter.nmToMeters(width));

        Geo c1 = new Geo(relativePosition.getLatitude(),
                relativePosition.getLongitude());
        Geo c2 = new Geo(horizontalEndPosition.getLatitude(),
                horizontalEndPosition.getLongitude());

        Geo d1 = new Geo(A.getLatitude(), A.getLongitude());
        Geo d2 = new Geo(C.getLatitude(), C.getLongitude());

        Geo intersectionPointLeft = Intersection.segmentsIntersect(c1, c2, d1,
                d2);

        Position leftPoint = Position.create(
                intersectionPointLeft.getLatitude(),
                intersectionPointLeft.getLongitude());

        Position rightPoint = Calculator.findPosition(leftPoint,
                horizontalBearing, Converter.nmToMeters(width));

        // Position leftPoint = Position.create(relativePosition.getLatitude(),
        // A.getLongitude());
        // Position rightPoint = Position.create(relativePosition.getLatitude(),
        // B.getLongitude());

        distanceToTop = Math.abs(Calculator.range(relativePosition, topPoint,
                Heading.RL));
        distanceToBottom = Math.abs(Calculator.range(relativePosition,
                bottomPoint, Heading.RL));

        distanceToLeft = Math.abs(Calculator.range(leftPoint, relativePosition,
                Heading.RL));
        distanceToRight = Math.abs(Calculator.range(rightPoint,
                relativePosition, Heading.RL));

        // System.out.println("Distance to top: " + distanceToTop);
        // System.out.println("Distance to Bottom: " + distanceToBottom);
        //
        // System.out.println("Distance to left: " + distanceToLeft);
        //
        // System.out.println("Distance to right: " + distanceToRight);

        //
        //

        // System.out.println("Height is: " + height);

        // Fix for uncertainly accuracy issue introduced by pixel resolution
        Double newHeight = distanceToTop + distanceToBottom;
        Double newWidth = distanceToLeft + distanceToRight;

        Double differenceHeight = height - newHeight;
        Double differenceWidth = width - newWidth;

        distanceToTop = distanceToTop + (differenceHeight / 2);
        distanceToBottom = distanceToBottom + (differenceHeight / 2);
        distanceToLeft = distanceToLeft + (differenceWidth / 2);
        distanceToRight = distanceToRight + (differenceWidth / 2);

        // System.out.println("Calculated height is: "
        // + (distanceToTop + distanceToBottom));
        // System.out.println("Difference is:" + differenceHeight);

        // System.out.println("Difference height is: " + (height -
        // distanceToLeft+distanceToRight));

        // Double h2 = distanceToTop + distanceToBottom;
        // Double diffh2 = height - h2;

        // double diff2 = width - distanceToLeft + distanceToRight;

        // System.out.println("Difference height is: " + diffh2);

        // System.out.println("Difference width is: " + diff2);

    }

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return AlphaComposite.getInstance(type, alpha);
    }

    private void drawPolygon() {
        // space for lat-lon points plus first lat-lon pair to close the polygon
        double[] polyPoints = new double[8 + 2];
        int j = 0;
        polyPoints[j] = A.getLatitude();
        polyPoints[j + 1] = A.getLongitude();
        j += 2;

        polyPoints[j] = B.getLatitude();
        polyPoints[j + 1] = B.getLongitude();
        j += 2;

        polyPoints[j] = D.getLatitude();
        polyPoints[j + 1] = D.getLongitude();
        j += 2;

        polyPoints[j] = C.getLatitude();
        polyPoints[j + 1] = C.getLongitude();
        j += 2;

        // double[] polyPoints = new double[polygon.getPolygon().size() * 2 +
        // 2];
        // int j = 0;
        // for (int i = 0; i < polygon.getPolygon().size(); i++) {
        // polyPoints[j] = polygon.getPolygon().get(i).getLat();
        // polyPoints[j + 1] = polygon.getPolygon().get(i).getLon();
        // j += 2;
        // }
        polyPoints[j] = polyPoints[0];
        polyPoints[j + 1] = polyPoints[1];
        poly = new OMPoly(polyPoints, OMGraphicConstants.DECIMAL_DEGREES,
                OMGraphicConstants.LINETYPE_RHUMB, 1);
        poly.setLinePaint(clear);
        poly.setFillPaint(new Color(0, 0, 0, 1));
        poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

        add(poly);

    }

    @Override
    public void render(Graphics gr) {
        Graphics2D image = (Graphics2D) gr;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }
}

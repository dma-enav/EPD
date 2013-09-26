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
package dk.dma.epd.common.prototype.layers.voct;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.proj.Length;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.voct.RapidResponseData;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Converter;

/**
 * Graphic for effective area for a SRU
 */
@SuppressWarnings("unused")
public class AreaInternalGraphics extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    private Rectangle hatchFillRectangle;
    private BufferedImage hatchFill;

    private OMPoly poly;

    private boolean frame;
    Double height;
    Double width;

    EffectiveSRUAreaGraphics effecticeSRUAreaGraphics;

    Position A;
    Position B;
    Position C;
    Position D;

    Double distanceToTop;
    Double distanceToBottom;
    Double distanceToLeft;
    Double distanceToRight;

    Position relativePosition;

    // Initialize with
    public AreaInternalGraphics(Position A, Position B, Position C, Position D,
            Double width, Double height,
            EffectiveSRUAreaGraphics effecticeSRUAreaGraphics) {
        super();

        this.setVague(true);

        this.height = height;
        this.width = width;

        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;

        this.effecticeSRUAreaGraphics = effecticeSRUAreaGraphics;

        // this.nogoColor = color;

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

        // drawAreaBox();
        // drawPolyline();
        // drawPolygon(A, B, C, D);
        // drawPoints();

        // Draw the message
        // if (errorCode == -1 || errorCode == 1 || errorCode == 17) {
        // OMPoint polyPoint = new OMPoint(0, 0);
        //
        // // Dummy point to make selection appear
        // // polyPoint.setVisible(false);
        // add(polyPoint);
        // }

        // if (frame) {
        // drawAreaBox();
        // }

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

    }

    public void moveRelative(Position newPos, RapidResponseData data) {

        
        // relativePosition is

        graphics.clear();

        // find topside of box
        // distance from mouseOffset to

        // First top side of the box
        Position topSide = Calculator.findPosition(newPos, 0,
                Converter.nmToMeters(distanceToTop));

        // Bottom side of the box
        Position bottomSide = Calculator.findPosition(newPos, 180,
                Converter.nmToMeters(distanceToBottom));

        // Go left radius length
        A = Calculator.findPosition(topSide, 270,
                Converter.nmToMeters(distanceToLeft));

        B = Calculator.findPosition(topSide, 90,
                Converter.nmToMeters(distanceToRight));

        C = Calculator.findPosition(bottomSide, 270,
                Converter.nmToMeters(distanceToLeft));

        D = Calculator.findPosition(bottomSide, 90,
                Converter.nmToMeters(distanceToRight));

        drawPolygon();

        effecticeSRUAreaGraphics.updateLines(A, B, C, D);
        
        data.setEffectiveAreaA(A);
        data.setEffectiveAreaB(B);
        data.setEffectiveAreaC(C);
        data.setEffectiveAreaD(D);

    }

    public void adjustInternalPosition(Position relativePosition) {

        // this.relativePosition = relativePosition;

        Position topPoint = Position.create(A.getLatitude(),
                relativePosition.getLongitude());
        Position bottomPoint = Position.create(C.getLatitude(),
                relativePosition.getLongitude());

        Position leftPoint = Position.create(relativePosition.getLatitude(),
                A.getLongitude());
        Position rightPoint = Position.create(relativePosition.getLatitude(),
                B.getLongitude());

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

//        System.out.println("Height is: " + height);

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

//        Double h2 = distanceToTop + distanceToBottom;
//        Double diffh2 = height - h2;

//        double diff2 = width - distanceToLeft + distanceToRight;

//        System.out.println("Difference height is: " + diffh2);

//        System.out.println("Difference width is: " + diff2);

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

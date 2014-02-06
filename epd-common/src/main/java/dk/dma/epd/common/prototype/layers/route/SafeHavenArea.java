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
package dk.dma.epd.common.prototype.layers.route;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.util.Calculator;

public class SafeHavenArea extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    // CenterRaster selectionGraphics;

    ImageIcon targetImage;
    int imageWidth;
    int imageHeight;

    private List<Position> polygon;
    private Rectangle hatchFillRectangle;
    private BufferedImage hatchFill;
    OMPoly poly;

    public SafeHavenArea() {
        super();

        hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D big = hatchFill.createGraphics();
        Composite originalComposite = big.getComposite();
        // big.setColor(Color.green);
        big.setColor(new Color(0f, 1f, 0f, 0.7f));
        // big.setComposite(makeComposite(0.7f));
        big.drawLine(0, 0, 10, 10);

        hatchFillRectangle = new Rectangle(0, 0, 10, 10);
        big.setComposite(originalComposite);

        this.polygon = new ArrayList<>();

    }

    private void drawPolygon() {
        // space for lat-lon points plus first lat-lon pair to close the polygon
        double[] polyPoints = new double[polygon.size() * 2 + 2];
        int j = 0;
        for (int i = 0; i < polygon.size(); i++) {
            polyPoints[j] = polygon.get(i).getLatitude();
            polyPoints[j + 1] = polygon.get(i).getLongitude();
            j += 2;
        }
        polyPoints[j] = polyPoints[0];
        polyPoints[j + 1] = polyPoints[1];
        poly = new OMPoly(polyPoints, OMGraphicConstants.DECIMAL_DEGREES, OMGraphicConstants.LINETYPE_RHUMB, 1);
        // poly.setLinePaint(clear);
        poly.setFillPaint(new Color(0, 0, 0, 1));
        poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

        Stroke activeStroke = new BasicStroke(1.0f, // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                10.0f, // Miter limit
                new float[] { 10.0f, 8.0f }, // Dash pattern
                0.0f); // Dash phase

        poly.setStroke(activeStroke);

        add(poly);
    }

    public void moveSymbol(Position pos, double bearing, double width, double height) {

        if (pos != null) {
//            System.out.println("Moving symbol " + pos);

            
            
            // remove(poly);
            graphics.clear();

            // int width = 1000;
            // int height = 500;

            // Create the polygon around the position.
            calculatePolygon(pos, bearing, width, height);

            // createGraphics();
            drawPolygon();
            
            this.setVisible(true);
        }else{
            this.setVisible(false);
        }

    }

    private void calculatePolygon(Position position, double bearing, double width, double height) {
        // double withNm = Converter.nmToMeters(width/2);
        // double heightNm = Converter.nmToMeters(height/2);

        double angle = 90 + bearing;
        double oppositeBearing = 180 + bearing;

        Position topLinePt = Calculator.findPosition(position, bearing, width / 2);

        if (angle > 360) {
            angle = angle - 360;
        }

        if (oppositeBearing > 360) {
            oppositeBearing = oppositeBearing - 360;
        }

        Position bottomLinePt = Calculator.findPosition(position, oppositeBearing, width / 2);

        // System.out.println("Top pnt: " + topLinePt);
        // System.out.println("Btm pnt: " + bottomLinePt);

        Position point1 = Calculator.findPosition(bottomLinePt, angle, height / 2);

        Position point2 = Calculator.findPosition(topLinePt, angle, height / 2);

        Position point3 = Calculator.findPosition(bottomLinePt, angle + 180, height / 2);

        Position point4 = Calculator.findPosition(topLinePt, angle + 180, height / 2);

        polygon.clear();

        // polygon.add(topLinePt);
        // polygon.add(bottomLinePt);

        polygon.add(point1);

        polygon.add(point2);

        polygon.add(point4);
        polygon.add(point3);

    }

    // public void removeSymbol() {
    // remove(selectionGraphics);
    // }

    /**
     * Turn on anti-aliasing
     */
    @Override
    public void render(Graphics g) {
        Graphics2D image = (Graphics2D) g;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }
}

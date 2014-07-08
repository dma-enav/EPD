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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.proj.Projection;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.common.util.SafeHavenUtils;

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
    Position ownShipPosition;
    Position polygonCenterPosition;

    public SafeHavenArea() {
        super();

        hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D big = hatchFill.createGraphics();
        Composite originalComposite = big.getComposite();
        big.setColor(new Color(0f, 1f, 0f, 0.7f));
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

        updateColor();

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

    public void moveSymbol(Position pos, double bearing, double width, double length) {

        if (pos != null) {
            polygonCenterPosition = pos;
            // System.out.println("Moving symbol " + pos);

            // remove(poly);
            graphics.clear();

            // int width = 1000;
            // int height = 500;

            // Create the polygon around the position.
            SafeHavenUtils.calculateBounds(pos, bearing, width, length, polygon);

            // createGraphics();
            drawPolygon();

            this.setVisible(true);
        } else {
            this.setVisible(false);
        }

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

    public void shipPositionChanged(Position position) {
        this.ownShipPosition = position;
    }

    private void updateColor() {

        hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D big = hatchFill.createGraphics();
        Composite originalComposite = big.getComposite();

        if (polygonCenterPosition != null && ownShipPosition != null) {

            double distance = Converter.metersToNm(polygonCenterPosition.distanceTo(ownShipPosition, CoordinateSystem.CARTESIAN));

            Projection proj = EPD.getInstance().getMainFrame().getActiveChartPanel().getMap().getProjection();
            Point2D ownShipPositionPoint = proj.forward(ownShipPosition.getLatitude(), ownShipPosition.getLongitude());
            
            //Generate the shape so we can check if we are located inside it
            poly.generate(proj);
            
            if (poly.contains(ownShipPositionPoint.getX(), ownShipPositionPoint.getY())) {
                big.setColor(new Color(0f, 1f, 0f, 0.5f));
            } else {
                if (distance > 1) {
                    big.setColor(new Color(1f, 0f, 0f, 0.5f));
                } else {
                    big.setColor(new Color(1f, 1f, 0f, 0.5f));
                }

            }

        } else {
            big.setColor(new Color(0.8f, 0.78f, 0.78f, 0.5f));
        }

        big.drawLine(0, 0, 10, 10);

        hatchFillRectangle = new Rectangle(0, 0, 10, 10);
        big.setComposite(originalComposite);

    }
}

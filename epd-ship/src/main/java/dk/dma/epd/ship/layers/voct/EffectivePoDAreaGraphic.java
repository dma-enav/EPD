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
package dk.dma.epd.ship.layers.voct;

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
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Converter;

/**
 * Graphic for effective area for a SRU
 */
@SuppressWarnings("unused")
public class EffectivePoDAreaGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    private Rectangle hatchFillRectangle;
    private BufferedImage hatchFill;

    private OMPoly poly;
    
    private boolean frame;
    Double width;
    Double length;

    // Initialize with
    public EffectivePoDAreaGraphic(Position startPos, Double width,
            Double length) {
        super();
        
        this.setVague(true);
        
        this.width = width;
        this.length = length;

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
        Position A = startPos;
        Position B = Calculator
                .findPosition(A, 90, Converter.nmToMeters(width));

        Position D = Calculator.findPosition(A, 180,
                Converter.nmToMeters(length));
        Position C = Calculator
                .findPosition(D, 90, Converter.nmToMeters(width));

        drawPolygon(A, B, C, D);

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

    public void moveCenter(Position newCenter){
        
        graphics.clear();
        
        // First top side of the box
        Position topCenter = Calculator.findPosition(newCenter, 0,
                Converter.nmToMeters(length/2));

        // Bottom side of the box
        Position bottomCenter = Calculator.findPosition(newCenter, 180,
                Converter.nmToMeters(width/2));
        
        

        // Go left radius length
        Position A = Calculator.findPosition(topCenter, 270,
                Converter.nmToMeters(width/2));
        Position B = Calculator.findPosition(topCenter, 90,
                Converter.nmToMeters(width/2));
        Position C = Calculator.findPosition(bottomCenter, 90,
                Converter.nmToMeters(length/2));
        Position D = Calculator.findPosition(bottomCenter, 270,
                Converter.nmToMeters(length/2));
        
        drawPolygon(A, B, C, D);

    }
    
    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return AlphaComposite.getInstance(type, alpha);
    }

    private void drawPolygon(Position A, Position B, Position C, Position D) {
        // space for lat-lon points plus first lat-lon pair to close the polygon
        double[] polyPoints = new double[8 + 2];
        int j = 0;
        polyPoints[j] = A.getLatitude();
        polyPoints[j + 1] = A.getLongitude();
        j += 2;

        polyPoints[j] = B.getLatitude();
        polyPoints[j + 1] = B.getLongitude();
        j += 2;

        polyPoints[j] = C.getLatitude();
        polyPoints[j + 1] = C.getLongitude();
        j += 2;

        polyPoints[j] = D.getLatitude();
        polyPoints[j + 1] = D.getLongitude();
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
        poly = new OMPoly(polyPoints,
                OMGraphicConstants.DECIMAL_DEGREES,
                OMGraphicConstants.LINETYPE_RHUMB, 1);
        poly.setLinePaint(Color.black);
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

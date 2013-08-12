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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import com.bbn.openmap.omGraphics.OMArrowHead;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.proj.Length;

import dk.dma.enav.model.geometry.Position;

/**
 * Graphic for MSI location/area
 */
@SuppressWarnings("unused")
public class SarLinesGraphics extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    private Rectangle hatchFillRectangle;
    private BufferedImage hatchFill;

    

    float[] dash = { 0.1f };
    
    public SarLinesGraphics(Position LKP, Position current, Position datum           ) {
        super();

//        this.nogoColor = color;

        // Draw the data

        hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D big = hatchFill.createGraphics();
        Composite originalComposite = big.getComposite();
        big.setComposite(makeComposite(0.2f));
        big.setColor(Color.green);
        big.drawLine(0, 0, 10, 10);

        hatchFillRectangle = new Rectangle(0, 0, 10, 10);
        big.setComposite(originalComposite);
        
        lineType = LINETYPE_RHUMB;

        
        //RDV
        drawLine(LKP, datum);
        
        // TWC
        drawLine(LKP, current);
        
        // LW
        drawLine(current, datum);
        
        Font font = new Font(Font.DIALOG, Font.PLAIN, 12);
        
        OMText LKPlabel = new OMText(0, 0, 0, 0, "", font, OMText.JUSTIFY_CENTER);    
        LKPlabel.setLat(LKP.getLatitude());
        LKPlabel.setLon(LKP.getLongitude());
        LKPlabel.setY(25);
        LKPlabel.setLinePaint(Color.black);
        LKPlabel.setTextMatteColor(Color.WHITE);
        LKPlabel.setData("LKP");
        add(LKPlabel);
        
        OMText datumLabel = new OMText(0, 0, 0, 0, "", font, OMText.JUSTIFY_CENTER);    
        datumLabel.setLat(datum.getLatitude());
        datumLabel.setLon(datum.getLongitude());
        datumLabel.setY(25);
        datumLabel.setLinePaint(Color.black);
        datumLabel.setTextMatteColor(Color.WHITE);
        datumLabel.setData("Datum");
        add(datumLabel);
        
        
        
//        drawCircle(datum, radius);

        // drawAreaBox();
        // drawPolyline();
//        drawPolygon(A, B, C, D);
        // drawPoints();

        // Draw the message
//        if (errorCode == -1 || errorCode == 1 || errorCode == 17) {
//            OMPoint polyPoint = new OMPoint(0, 0);
//
//            // Dummy point to make selection appear
//            // polyPoint.setVisible(false);
//            add(polyPoint);
//        }

//        if (frame) {
//            drawAreaBox();
//        }

    }

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return AlphaComposite.getInstance(type, alpha);
    }


    private void drawLine(Position A, Position B){
        OMArrowHead arrow = new OMArrowHead(
                OMArrowHead.ARROWHEAD_DIRECTION_FORWARD, 55, 1, 5);
        
        OMLine line = new OMLine(A.getLatitude(), A.getLongitude(), B.getLatitude(), B.getLongitude(), lineType);
        
        line.setLinePaint(Color.black);
        line.setStroke(new BasicStroke(2.0f, BasicStroke.JOIN_MITER,
                BasicStroke.JOIN_MITER, 1.0f, dash, 0.0f));

        line.setArrowHead(arrow);
        
        add(line);

        
    }

    private void drawCircle(Position datum, double radius) {
         
        OMCircle cirle = new OMCircle(datum.getLatitude(), datum.getLongitude(), radius, Length.NM);
        cirle.setLinePaint(Color.black);
        cirle.setFillPaint(new Color(0, 0, 0, 1));
        cirle.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

        add(cirle);
    }

    
    @Override
    public void render(Graphics gr) {
        Graphics2D image = (Graphics2D) gr;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }

}

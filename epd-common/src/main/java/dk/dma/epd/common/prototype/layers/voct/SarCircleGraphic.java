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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.Date;

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoint;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.proj.Length;

import dk.dma.enav.model.geometry.Position;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

/**
 * Graphic for MSI location/area
 */
@SuppressWarnings("unused")
public class SarCircleGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    private Rectangle hatchFillRectangle;
    private BufferedImage hatchFill;

    private boolean frame;

    public SarCircleGraphic(Position datum, Double radius
           ) {
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
        
        drawCircle(datum, radius);

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

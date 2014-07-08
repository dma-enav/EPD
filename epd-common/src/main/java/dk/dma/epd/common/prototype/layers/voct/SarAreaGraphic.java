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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;

import dk.dma.enav.model.geometry.Position;

/**
 * Graphic for MSI location/area
 */
@SuppressWarnings("unused")
public class SarAreaGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    private Color nogoColor = Color.yellow;

    private Rectangle hatchFillRectangle;
    private BufferedImage hatchFill;

    private boolean frame;

    public SarAreaGraphic(Position A, Position B, Position C, Position D
           ) {
        super();

//        this.nogoColor = color;

        // Draw the data

        hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D big = hatchFill.createGraphics();
        Composite originalComposite = big.getComposite();
        big.setComposite(makeComposite(0.2f));
        big.setColor(nogoColor);
        big.drawLine(0, 0, 10, 10);

        hatchFillRectangle = new Rectangle(0, 0, 10, 10);
        big.setComposite(originalComposite);

        drawPolygon(A, B, C, D);
    }
    
    
    
    public SarAreaGraphic(List<Position> polygon) {
         super();

//         this.nogoColor = color;

         // Draw the data

         hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
         Graphics2D big = hatchFill.createGraphics();
         Composite originalComposite = big.getComposite();
         big.setComposite(makeComposite(0.2f));
         big.setColor(nogoColor);
         big.drawLine(0, 0, 10, 10);

         hatchFillRectangle = new Rectangle(0, 0, 10, 10);
         big.setComposite(originalComposite);

         drawPolygon(polygon);
     }

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return AlphaComposite.getInstance(type, alpha);
    }

    private void drawPolygon(List<Position> polygon) {
        // space for lat-lon points plus first lat-lon pair to close the polygon
        double[] polyPoints = new double[polygon.size() * 2];
        int j = 0;
        for (int i = 0; i < polygon.size(); i++) {
            polyPoints[j] = polygon.get(i).getLatitude();
            polyPoints[j + 1] = polygon.get(i).getLongitude();
            j += 2;
        }

        OMPoly poly = new OMPoly(polyPoints,
                OMGraphicConstants.DECIMAL_DEGREES,
                OMGraphicConstants.LINETYPE_RHUMB, 1);
        poly.setLinePaint(Color.black);
        poly.setFillPaint(new Color(0, 0, 0, 1));
        poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));
        poly.setIsPolygon(true);

        
        
        
        
        add(poly);

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
 
        polyPoints[j] = polyPoints[0];
        polyPoints[j + 1] = polyPoints[1];
        OMPoly poly = new OMPoly(polyPoints,
                OMGraphicConstants.DECIMAL_DEGREES,
                OMGraphicConstants.LINETYPE_RHUMB, 1);
        poly.setLinePaint(Color.black);
        poly.setFillPaint(new Color(0, 0, 0, 1));
        poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

        
        poly.setIsPolygon(true);
        
        add(poly);

    }


}

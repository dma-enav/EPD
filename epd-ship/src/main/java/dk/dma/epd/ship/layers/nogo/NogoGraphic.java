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
package dk.dma.epd.ship.layers.nogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;

import dk.dma.enav.model.geometry.Position;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

public class NogoGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    private NogoPolygon polygon;

    private Color nogoColor = Color.red;

    private Rectangle hatchFillRectangle;
    private BufferedImage hatchFill;

    public NogoGraphic(NogoPolygon polygon) {
        this.polygon = polygon;
        if (polygon != null) {
            hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D big = hatchFill.createGraphics();
            Composite originalComposite = big.getComposite();
            big.setComposite(makeComposite(0.2f));
            big.setColor(nogoColor);
            big.drawLine(0, 0, 10, 10);

            hatchFillRectangle = new Rectangle(0, 0, 10, 10);
            big.setComposite(originalComposite);

            drawPolygon();
        }
    }

    public NogoGraphic(NogoPolygon polygon, Position northWest, Position southEast, boolean frame, Color color) {
        super();

        this.polygon = polygon;

        // Create location grahic
        // NogoLocationGraphic nogoLocationGraphic = new NogoLocationGraphic(this.polygon, validFrom, validTo, draught, message,
        // northWest, southEast, errorCode, frame, color);
        // add(nogoLocationGraphic);
    }

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return AlphaComposite.getInstance(type, alpha);
    }

    private void drawPolygon() {
        // space for lat-lon points plus first lat-lon pair to close the polygon
        double[] polyPoints = new double[polygon.getPolygon().size() * 2 + 2];
        int j = 0;
        for (int i = 0; i < polygon.getPolygon().size(); i++) {
            polyPoints[j] = polygon.getPolygon().get(i).getLat();
            polyPoints[j + 1] = polygon.getPolygon().get(i).getLon();
            j += 2;
        }
        polyPoints[j] = polyPoints[0];
        polyPoints[j + 1] = polyPoints[1];
        OMPoly poly = new OMPoly(polyPoints, OMGraphicConstants.DECIMAL_DEGREES, OMGraphicConstants.LINETYPE_RHUMB, 1);
        poly.setLinePaint(clear);
        poly.setFillPaint(new Color(0, 0, 0, 1));
        poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

        add(poly);

    }
}

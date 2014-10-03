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
package dk.dma.epd.common.prototype.layers.msi;

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.proj.Length;
import dk.dma.epd.common.prototype.notification.MsiNmNotification;
import dk.dma.epd.common.util.Converter;
import dma.msinm.MCLocation;
import dma.msinm.MCPoint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

/**
 * Graphic for MSI-NM location/area
 */
public class MsiNmLocationGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;
    
    private static final int POINT_RADIUS = 100; // meters
    
    private Color msiColor = new Color(183, 68, 237, 150);

    private Rectangle hatchFillRectangle;
    private BufferedImage hatchFill;
    
    public MsiNmLocationGraphic(MsiNmNotification message) {
        super();

        hatchFill = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D big = hatchFill.createGraphics();
        big.setColor(msiColor);
        big.drawLine(0, 0, 10, 10);
        hatchFillRectangle = new Rectangle(0, 0, 10, 10);

        for (MCLocation loc : message.get().getLocations()) {
            switch (loc.getType()) {
                case POINT:
                    for (MCPoint point : loc.getPoints()) {
                        drawCircle(point, POINT_RADIUS);
                    }
                    break;

                case CIRCLE:
                    if (loc.getRadius() != null && loc.getPoints().size() == 1) {
                        drawCircle(loc.getPoints().get(0), (int)Converter.nmToMeters(loc.getRadius().doubleValue()));
                    }
                    break;

                case POLYGON:
                    if (loc.getPoints().size() > 2) {
                        drawPolygon(loc);
                    }
                    break;

                case POLYLINE:
                    if (loc.getPoints().size() > 1) {
                        drawPolyline(loc);
                    }
                    break;

                default:
                    break;
            }
        }
    }
    
    private void drawCircle(MCPoint point, int radius) {
        OMCircle radiusCircle = new OMCircle(point.getLat(), point.getLon(), radius, Length.METER);
        radiusCircle.setLinePaint(msiColor);
        radiusCircle.setFillPaint(new Color(0, 0, 0, 1));
        radiusCircle.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));
        add(radiusCircle);
    }
    
    private void drawPolygon(MCLocation loc) {
        // space for lat-lon points plus first lat-lon pair to close the polygon
        double[] polyPoints = new double[loc.getPoints().size() * 2 + 2];
        int i = 0;
        for (MCPoint point : loc.getPoints()) {
            polyPoints[i] = point.getLat();
            polyPoints[i+1] = point.getLon();
            i+=2;
        }
        polyPoints[i] = polyPoints[0];
        polyPoints[i+1] = polyPoints[1];
        OMPoly poly = new OMPoly(polyPoints, OMGraphicConstants.DECIMAL_DEGREES, OMGraphicConstants.LINETYPE_RHUMB, 1);
        poly.setLinePaint(msiColor);
        poly.setFillPaint(new Color(0, 0, 0, 1));
        poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));
        add(poly);
    }
    
    private void drawPolyline(MCLocation loc) {
        double[] polyPoints = new double[loc.getPoints().size() * 2];
        int i = 0;
        for (MCPoint point : loc.getPoints()) {
            drawCircle(point, POINT_RADIUS);
            polyPoints[i] = point.getLat();
            polyPoints[i+1] = point.getLon();
            i+=2;
        }
        OMPoly poly = new OMPoly(polyPoints, OMGraphicConstants.DECIMAL_DEGREES, OMGraphicConstants.LINETYPE_RHUMB, 1);
        poly.setLinePaint(msiColor);
        add(poly);
    }
    
    @Override
    public void render(Graphics gr) {
        Graphics2D image = (Graphics2D) gr;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }
}

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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMPoly;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.util.Calculator;

/**
 * Graphic for a route leg
 */
public class ActiveRouteLegGraphic extends RouteLegGraphic {

    private static final long serialVersionUID = 1L;

    public ActiveRouteLegGraphic(RouteLeg routeLeg, int routeIndex, Color color, Stroke stroke, Color broadLineColor,
            float[] broadLineDash, float scale) {
        super(routeLeg, routeIndex, color, stroke, broadLineColor, broadLineDash, scale);
        addCrossTrack();
    }

    public ActiveRouteLegGraphic(RouteLeg routeLeg, int routeIndex, Color color, Stroke stroke, Color broadLineColor, float scale) {
        super(routeLeg, routeIndex, color, stroke, broadLineColor, scale);
        addCrossTrack();
    }

    public ActiveRouteLegGraphic(RouteLeg routeLeg, int routeIndex, Color color, Stroke stroke, float scale) {
        super(routeLeg, routeIndex, color, stroke, scale);
        addCrossTrack();

    }

    private void addCrossTrack() {

        if (routeLeg.getEndWp() != null) {

            RouteWaypoint legStart = routeLeg.getStartWp();
            RouteWaypoint legEnd = routeLeg.getEndWp();

            double legDirection = legStart.getPos().geodesicInitialBearingTo(legEnd.getPos());

            double portDirection = Calculator.turn90Minus(legDirection);
            double starboardDirection = Calculator.turn90Plus(legDirection);

            Position startLeft = Calculator.findPosition(legStart.getPos(), portDirection, routeLeg.getXtdPortMeters());

            Position startRight = Calculator.findPosition(legStart.getPos(), starboardDirection, routeLeg.getXtdStarboardMeters());

            Position endLeft = Calculator.findPosition(legEnd.getPos(), portDirection, routeLeg.getXtdPortMeters());

            Position endRight = Calculator.findPosition(legEnd.getPos(), starboardDirection, routeLeg.getXtdStarboardMeters());

            // space for lat-lon points plus first lat-lon pair to close the polygon
            double[] polyPoints = new double[8 + 2];
            int j = 0;
            polyPoints[j] = startLeft.getLatitude();
            polyPoints[j + 1] = startLeft.getLongitude();
            j += 2;

            polyPoints[j] = startRight.getLatitude();
            polyPoints[j + 1] = startRight.getLongitude();
            j += 2;

            polyPoints[j] = endRight.getLatitude();
            polyPoints[j + 1] = endRight.getLongitude();
            j += 2;

            polyPoints[j] = endLeft.getLatitude();
            polyPoints[j + 1] = endLeft.getLongitude();
            j += 2;

            // double[] polyPoints = new double[polygon.getPolygon().size() * 2 +
            // 2];
            // int j = 0;
            // for (int i = 0; i < polygon.getPolygon().size(); i++) {
            // polyPoints[j] = polygon.getPolygon().get(i).getLat();
            // polyPoints[j + 1] = polygon.getPolygon().get(i).getLon();
            // j += 2;
            // }

            // Rectangle hatchFillRectangle;
            // BufferedImage hatchFill;

            BufferedImage hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D big = hatchFill.createGraphics();

            big.setComposite(makeComposite(0.3f));
            big.setColor(Color.GRAY);
            big.drawLine(0, 0, 10, 10);

            Rectangle hatchFillRectangle = new Rectangle(0, 0, 10, 10);

            polyPoints[j] = polyPoints[0];
            polyPoints[j + 1] = polyPoints[1];
            
            int headingType = OMGraphicConstants.LINETYPE_RHUMB;
            
            if (this.getRouteLeg().getHeading() == Heading.GC){
                headingType = OMGraphicConstants.LINETYPE_GREATCIRCLE;
            }
            
            OMPoly poly = new OMPoly(polyPoints, OMGraphicConstants.DECIMAL_DEGREES, headingType, 0);
            poly.setIsPolygon(true);
            poly.setLinePaint(clear);
            poly.setFillPaint(new Color(0, 0, 0, 1));
            poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

            add(poly);
        }

    }

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return AlphaComposite.getInstance(type, alpha);
    }
}

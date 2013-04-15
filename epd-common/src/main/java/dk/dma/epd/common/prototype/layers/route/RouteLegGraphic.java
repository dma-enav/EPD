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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import com.bbn.openmap.omGraphics.OMArrowHead;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;

import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;

/**
 * Graphic for a route leg
 */
public class RouteLegGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    private RouteLeg routeLeg;
    private OMLine line;
    private OMArrowHead arrow = new OMArrowHead(
            OMArrowHead.ARROWHEAD_DIRECTION_FORWARD, 55, 5, 15);
    private Color color;

    private OMLine broadline;

    private int routeIndex;
    
    float[] dash = { 35.0f, 35.0f };
    float dashPhase;

    /**
     * Creates a route leg
     * 
     * @param routeLeg
     *            RouteLeg object containing information about the route leg
     * @param routeIndex
     *            TODO
     * @param color
     *            Color of the route leg
     * @param stroke
     *            Stroke type of the route leg
     */
    public RouteLegGraphic(RouteLeg routeLeg, int routeIndex, Color color,
            Stroke stroke) {
        super();
        this.routeIndex = routeIndex;
        this.routeLeg = routeLeg;
        this.color = color;
        this.stroke = stroke;
        this.setVague(true);
        initGraphics();
    }

    public void initGraphics() {
        if (routeLeg.getEndWp() != null) {
            RouteWaypoint legStart = routeLeg.getStartWp();
            RouteWaypoint legEnd = routeLeg.getEndWp();

            double startLat = legStart.getPos().getLatitude();
            double startLon = legStart.getPos().getLongitude();

            double endLat = legEnd.getPos().getLatitude();
            double endLon = legEnd.getPos().getLongitude();

            if (routeLeg.getHeading() == Heading.GC) {
                lineType = LINETYPE_GREATCIRCLE;
            } else if (routeLeg.getHeading() == Heading.RL) {
                lineType = LINETYPE_RHUMB;
            }

            line = new OMLine(startLat, startLon, endLat, endLon, lineType);
            line.setLinePaint(color);
            line.setStroke(stroke);

            add(line);

            // float[] dash1 = {35.0f};

            // broadline.setLinePaint(new Color(255, 255, 0));

            // float alpha = 0.5f;

            // Color color = new Color(0, 0.1f, 0.1f, alpha); //Red
            // Color color = tColor(Color.YELLOW.getRGB());
            // Color color = new Color(Color.YELLOW.get, alpha);

            // broadline.setLinePaint(color);
        }
    }

    public void addBroadLine() {
        if (routeLeg.getEndWp() != null) {



            RouteWaypoint legStart = routeLeg.getStartWp();
            RouteWaypoint legEnd = routeLeg.getEndWp();

            double startLat = legStart.getPos().getLatitude();
            double startLon = legStart.getPos().getLongitude();

            double endLat = legEnd.getPos().getLatitude();
            double endLon = legEnd.getPos().getLongitude();

            broadline = new OMLine(startLat, startLon, endLat, endLon, lineType);
            broadline.setLinePaint(new Color(1f, 1f, 0, 0.6f));
            broadline.setStroke(new BasicStroke(10.0f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10.0f, dash, dashPhase));

            add(broadline);
        }
    }

    public void changeBroadLine(){
//        broadline.setLinePaint(new Color(0f, 1f, 0, 0.6f));
        float[] dash = { 35.0f, 35.0f };
//        float dashPhase = 18.0f;
//        System.out.println("Adding to dashPhase " + dashPhase);
        dashPhase += 9.0f;
//        System.out.println("Dashphase is now " + dashPhase);
        
        broadline.setStroke(new BasicStroke(10.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, dash, dashPhase));
//        System.out.println("Changing stroke! " + dashPhase);
    }
    
    public void setArrows(boolean arrowsVisible) {
        if (!arrowsVisible) {
            line.setArrowHead(null);
        } else {
            line.setArrowHead(arrow);
        }

    }

    @Override
    public void render(Graphics gr) {
        Graphics2D image = (Graphics2D) gr;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }

    public int getRouteIndex() {
        return routeIndex;
    }

    public RouteLeg getRouteLeg() {
        return routeLeg;
    }
}

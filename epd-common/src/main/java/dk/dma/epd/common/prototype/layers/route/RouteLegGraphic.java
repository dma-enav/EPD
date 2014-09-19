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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import com.bbn.openmap.omGraphics.OMArrowHead;
import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;

/**
 * Graphic for a route leg
 */
public class RouteLegGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    protected RouteLeg routeLeg;
    private OMLine line;
    private OMArrowHead arrow;
    protected Color color;

    private OMLine animationLine;
    private OMLine broadLine;

    float[] dash;
    private int routeIndex;

    float dashPhase = 5000;
    private RouteGraphic routeGraphic;
    int legIndex;

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
     * @param routeGraphic
     */
    public RouteLegGraphic(RouteLeg routeLeg, int routeIndex, Color color,
            Stroke stroke, float scale, RouteGraphic routeGraphic, int legIndex) {
        super();
        this.routeIndex = routeIndex;
        this.routeLeg = routeLeg;
        this.color = color;
        this.stroke = stroke;
        this.routeGraphic = routeGraphic;
        this.legIndex = legIndex;
        dash = new float[] { scale * 35.0f, scale * 35.0f };
        arrow = new OMArrowHead(OMArrowHead.ARROWHEAD_DIRECTION_FORWARD, 55,
                (int) (scale * 5.0), (int) (scale * 15.0));
        this.setVague(true);
        initGraphics();
    }

    /**
     * Creates a voyage leg
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
            Stroke stroke, Color broadLineColor, float[] broadLineDash,
            float scale) {
        super();
        this.routeIndex = routeIndex;
        this.routeLeg = routeLeg;
        this.color = color;
        this.stroke = stroke;
        this.setVague(true);
        dash = new float[] { scale * 35.0f, scale * 35.0f };
        arrow = new OMArrowHead(OMArrowHead.ARROWHEAD_DIRECTION_FORWARD, 55,
                (int) (scale * 5.0), (int) (scale * 15.0));
        initGraphics();
        addBroadLine(broadLineColor, broadLineDash);
    }

    public RouteLegGraphic(RouteLeg routeLeg, int routeIndex, Color color,
            Stroke stroke, Color broadLineColor, float scale) {
        super();
        this.routeIndex = routeIndex;
        this.routeLeg = routeLeg;
        this.color = color;
        this.stroke = stroke;
        dash = new float[] { scale * 35.0f, scale * 35.0f };
        arrow = new OMArrowHead(OMArrowHead.ARROWHEAD_DIRECTION_FORWARD, 55,
                (int) (scale * 5.0), (int) (scale * 15.0));
        this.setVague(true);
        initGraphics();
        addBroadLine(broadLineColor,
                new float[] { scale * 40.0f, scale * 15.0f });
    }

    public RouteLegGraphic(ActiveRouteGraphic activeRouteGraphic,
            Position vesselPos, Position pos, Heading rl, Color color,
            float scale) {
        // TODO Auto-generated constructor stub
        this.color = color;
        initIndicateLegGraphics(vesselPos, pos);
    }

    private void addBroadLine(Color color, float[] broadLineDash) {
        if (routeLeg.getEndWp() != null) {

            RouteWaypoint legStart = routeLeg.getStartWp();
            RouteWaypoint legEnd = routeLeg.getEndWp();

            double startLat = legStart.getPos().getLatitude();
            double startLon = legStart.getPos().getLongitude();

            double endLat = legEnd.getPos().getLatitude();
            double endLon = legEnd.getPos().getLongitude();

            broadLine = new OMLine(startLat, startLon, endLat, endLon, routeLeg
                    .getHeading().getOMLineType());
            broadLine.setLinePaint(color);
            broadLine.setStroke(new BasicStroke(12.0f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10.0f, broadLineDash, 0.0f));

            add(broadLine);
        }
    }

    public void initIndicateLegGraphics(Position startPosition,
            Position endPosition) {

        Stroke activeStroke = new BasicStroke(EPD.getInstance().getSettings()
                .getNavSettings().getRouteWidth(), // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                10.0f, // Miter limit
                new float[] { 10.0f, 8.0f }, // Dash pattern
                0.0f); // Dash phase

        double startLat = startPosition.getLatitude();
        double startLon = startPosition.getLongitude();

        double endLat = endPosition.getLatitude();
        double endLon = endPosition.getLongitude();

        line = new OMLine(startLat, startLon, endLat, endLon,
                OMGraphicConstants.LINETYPE_RHUMB);
        line.setLinePaint(color);
        line.setStroke(activeStroke);

        add(line);

    }

    public void initGraphics() {
        if (routeLeg.getEndWp() != null) {
            RouteWaypoint legStart = routeLeg.getStartWp();
            RouteWaypoint legEnd = routeLeg.getEndWp();

            double startLat = legStart.getPos().getLatitude();
            double startLon = legStart.getPos().getLongitude();

            double endLat = legEnd.getPos().getLatitude();
            double endLon = legEnd.getPos().getLongitude();

            line = new OMLine(startLat, startLon, endLat, endLon, routeLeg
                    .getHeading().getOMLineType());
            line.setLinePaint(color);
            line.setStroke(stroke);

            add(line);
        }
    }

    public void addAnimatorLine() {
        if (routeLeg.getEndWp() != null) {

            RouteWaypoint legStart = routeLeg.getStartWp();
            RouteWaypoint legEnd = routeLeg.getEndWp();

            double startLat = legStart.getPos().getLatitude();
            double startLon = legStart.getPos().getLongitude();

            double endLat = legEnd.getPos().getLatitude();
            double endLon = legEnd.getPos().getLongitude();

            animationLine = new OMLine(startLat, startLon, endLat, endLon,
                    routeLeg.getHeading().getOMLineType());
            animationLine.setLinePaint(new Color(1f, 1f, 0, 0.6f));
            animationLine.setStroke(new BasicStroke(10.0f,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash,
                    dashPhase));

            add(animationLine);
        }
    }

    public void updateAnimationLine() {
        // broadline.setLinePaint(new Color(0f, 1f, 0, 0.6f));
        float[] dash = { 35.0f, 35.0f };
        // float dashPhase = 18.0f;
        // System.out.println("Adding to dashPhase " + dashPhase);

        dashPhase -= 9.0f;
        if (dashPhase < 0) {
            // System.out.println("Resetting!");
            dashPhase = 5000;
        }
        // System.out.println("Dashphase is now " + dashPhase);

        if (animationLine != null) {

            animationLine.setStroke(new BasicStroke(10.0f,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash,
                    dashPhase));

        }
        // System.out.println("Changing stroke! " + dashPhase);
    }

    public void setArrows(boolean arrowsVisible) {

        if (line != null) {
            if (!arrowsVisible) {
                line.setArrowHead(null);
            } else {
                line.setArrowHead(arrow);
            }
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

    /**
     * @return the routeGraphic
     */
    public RouteGraphic getRouteGraphic() {
        return routeGraphic;
    }

    /**
     * @return the legIndex
     */
    public int getLegIndex() {
        return legIndex;
    }

}

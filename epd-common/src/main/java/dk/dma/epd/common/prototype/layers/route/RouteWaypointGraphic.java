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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMText;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;

/**
 * Graphic for a route waypoint
 */
public class RouteWaypointGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    private RouteWaypoint routeWaypoint;
    private WaypointCircle circle;
    private Font font = new Font(Font.DIALOG, Font.PLAIN, 14);
    private OMText label = new OMText(0, 0, 0, 0, "", font, OMText.JUSTIFY_CENTER);
    private Color color;
    private int width;
    private int height;
    private float scale;

    private boolean dotted;

    /**
     * Creates a route waypoint circle
     * 
     * @param routeIndex
     *            TODO
     * @param routeWaypoint
     *            RouteWaypoint object containing information about the route waypoint
     * @param color
     *            Color of the waypoint
     * @param width
     *            Width of the circle
     * @param height
     *            Height of the circle
     */
    public RouteWaypointGraphic(Route route, int routeIndex, int wpIndex, RouteWaypoint routeWaypoint, Color color, int width,
            int height, float scale) {
        super();
        this.routeWaypoint = routeWaypoint;
        this.scale = scale;
        this.color = color;
        this.width = (int) (width * scale);
        this.height = (int) (height * scale);
        this.circle = new WaypointCircle(route, routeIndex, wpIndex);

        initGraphics();
    }

    public RouteWaypointGraphic(Route route, int routeIndex, int wpIndex, RouteWaypoint routeWaypoint, Color color, int width,
            int height, boolean dotted, float scale) {
        super();
        this.routeWaypoint = routeWaypoint;
        this.color = color;
        this.width = (int) (width * scale);
        this.height = (int) (height * scale);
        this.dotted = dotted;
        this.circle = new WaypointCircle(route, routeIndex, wpIndex);
        this.scale = scale;
        initVoyageGraphics();
    }

    public void initGraphics() {
        clear();

        double lat = routeWaypoint.getPos().getLatitude();
        double lon = routeWaypoint.getPos().getLongitude();

        circle.setLatLon(lat, lon);
        circle.setLinePaint(color);
        circle.setWidth(width);
        circle.setHeight(height);

        circle.setStroke(new BasicStroke(3.0f * scale));
        add(circle);

        label.setLat(lat);
        label.setLon(lon);
        label.setY(25);
        label.setLinePaint(color);
        label.setTextMatteColor(Color.WHITE);
        label.setData(routeWaypoint.getName());
        add(label);
    }

    public void initVoyageGraphics() {
        clear();

        double lat = routeWaypoint.getPos().getLatitude();
        double lon = routeWaypoint.getPos().getLongitude();

        circle.setLatLon(lat, lon);
        circle.setLinePaint(color);
        circle.setWidth(width);
        circle.setHeight(height);

        if (dotted) {
            Stroke stroke = new BasicStroke(
                    3.0f * scale, // Width
                    BasicStroke.CAP_SQUARE, // End cap
                    BasicStroke.JOIN_MITER, // Join style
                    10.0f, // Miter limit
                    new float[] { 1.0f, 5.0f }, // Dash pattern
                    0.0f);
            circle.setStroke(stroke);
        } else {
            circle.setStroke(new BasicStroke(3f * scale));
        }

        add(circle);

        label.setLat(lat);
        label.setLon(lon);
        label.setY(25);
        label.setLinePaint(color);
        label.setTextMatteColor(Color.WHITE);
        label.setData(routeWaypoint.getName());
        add(label);
    }

    @Override
    public void render(Graphics gr) {
        Graphics2D image = (Graphics2D) gr;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }

}

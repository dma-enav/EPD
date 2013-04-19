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
    
    private boolean dotted;
    
    /**
     * Creates a route waypoint circle
     * @param routeIndex TODO
     * @param routeWaypoint RouteWaypoint object containing information about the route waypoint
     * @param color Color of the waypoint
     * @param width Width of the circle
     * @param height Height of the circle 
     */
    public RouteWaypointGraphic(Route route, int routeIndex, int wpIndex, RouteWaypoint routeWaypoint, Color color, int width, int height) {
        super();
        this.routeWaypoint = routeWaypoint;
        this.color = color;
        this.width = width;
        this.height = height;
        this.circle = new WaypointCircle(route, routeIndex, wpIndex);
        initGraphics();
    }
    
    public RouteWaypointGraphic(Route route, int routeIndex, int wpIndex, RouteWaypoint routeWaypoint, Color color, int width, int height, boolean dotted) {
        super();
        this.routeWaypoint = routeWaypoint;
        this.color = color;
        this.width = width;
        this.height = height;
        this.dotted = dotted;
        this.circle = new WaypointCircle(route, routeIndex, wpIndex);
        initVoyageGraphics();
    }
    
    public void initGraphics(){
        clear();
        
        double lat = routeWaypoint.getPos().getLatitude();
        double lon = routeWaypoint.getPos().getLongitude();
        
        circle.setLatLon(lat, lon);
        circle.setLinePaint(color);
        circle.setWidth(width);
        circle.setHeight(height);
        circle.setStroke(new BasicStroke(3));
        add(circle);
        
        label.setLat(lat);
        label.setLon(lon);
        label.setY(25);
        label.setLinePaint(color);
        label.setTextMatteColor(Color.WHITE);
        label.setData(routeWaypoint.getName());
        add(label);
    }
    
    
    public void initVoyageGraphics(){
        clear();
        
        double lat = routeWaypoint.getPos().getLatitude();
        double lon = routeWaypoint.getPos().getLongitude();
        
        circle.setLatLon(lat, lon);
        circle.setLinePaint(color);
        circle.setWidth(width);
        circle.setHeight(height);
        
        if (dotted){
            Stroke stroke = new BasicStroke(
                    3.0f,                      // Width
                    BasicStroke.CAP_SQUARE,    // End cap
                    BasicStroke.JOIN_MITER,    // Join style
                    10.0f,                     // Miter limit
                    new float[] { 1.0f, 5.0f }, // Dash pattern
                    0.0f);
            circle.setStroke(stroke);
        }else{
            circle.setStroke(new BasicStroke(3));
        }
        
        
        
        

        add(circle);

    }
    
    @Override
    public void render(Graphics gr) {
        Graphics2D image = (Graphics2D) gr;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }
    
}

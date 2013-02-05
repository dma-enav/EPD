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

import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;

/**
 * Graphic for showing routes
 */
public class RouteGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;
    
    private Route route;
    private boolean arrowsVisible;
    private LinkedList<RouteWaypoint> routeWaypoints;
    private List<RouteLegGraphic> routeLegs = new ArrayList<>();
    
    protected Stroke stroke;
    protected Color color;

    private int routeIndex;
    
    public RouteGraphic(Route route, int routeIndex, boolean arrowsVisible, Stroke stroke, Color color) {
        super();
        this.route = route;
        this.routeIndex = routeIndex;
        this.arrowsVisible = arrowsVisible;
        this.stroke = stroke;
        this.color = color;
        initGraphics();
    }
    
    public RouteGraphic(boolean arrowsVisible, Stroke stroke, Color color) {
        super();
        this.arrowsVisible = arrowsVisible;
        this.stroke = stroke;
        this.color = color;
    }
    
    public void setRoute(Route route) {
        this.route = route;
        initGraphics();
    }
    
    public void initGraphics(){
        routeWaypoints = route.getWaypoints();
        int i = 0;
        for (RouteWaypoint routeWaypoint : routeWaypoints) {
            if(route instanceof ActiveRoute && ((ActiveRoute) route).getActiveWaypointIndex() == i){
                RouteWaypointGraphic routeWaypointGraphicActive = new RouteWaypointGraphic(route, routeIndex, i,routeWaypoint, Color.RED, 30, 30);
                add(0,routeWaypointGraphicActive);
            }            
            if(routeWaypoint.getOutLeg() != null){
                RouteLeg routeLeg = routeWaypoint.getOutLeg();
                RouteLegGraphic routeLegGraphic = new RouteLegGraphic(routeLeg, routeIndex, this.color, this.stroke);
                add(routeLegGraphic);
                routeLegs.add(0,routeLegGraphic);
            }
            RouteWaypointGraphic routeWaypointGraphic = new RouteWaypointGraphic(route, routeIndex, i, routeWaypoint, this.color, 18, 18);
            add(0,routeWaypointGraphic);
            i++;
        }
    }
    
    public void showArrowHeads(boolean show){
        if(this.arrowsVisible != show){
            for (RouteLegGraphic routeLeg : routeLegs) {
                routeLeg.setArrows(show);
            }
            this.arrowsVisible = show;
        }
    }
}

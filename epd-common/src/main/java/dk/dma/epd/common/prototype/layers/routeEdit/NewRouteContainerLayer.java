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
package dk.dma.epd.common.prototype.layers.routeEdit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.LinkedList;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.layers.route.RouteGraphic;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;

/**
 * Container layer for new route drawing.
 */
public class NewRouteContainerLayer extends OMGraphicHandlerLayer {

    private static final long serialVersionUID = 1L;
    
    private LinkedList<RouteWaypoint> waypoints = new LinkedList<>();
    private Route route;
    private OMGraphicList graphics = new OMGraphicList();
    private RouteGraphic routeGraphics;

    public NewRouteContainerLayer() {
        route = new Route();
        route.setWaypoints(waypoints);
        routeGraphics = new RouteGraphic(true, new BasicStroke(2), Color.black);
        graphics.add(routeGraphics);
        
        
        
        
        
    }
    
    @Override
    public synchronized OMGraphicList prepare() {
        routeGraphics.setRoute(route);
        graphics.project(getProjection());
        return graphics;
    }
    
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
    }
    
    @Override
    public void findAndUndo(Object obj) {
        super.findAndUndo(obj);
    }
    
    public Route getRoute() {
        return route;
    }
    
    public LinkedList<RouteWaypoint> getWaypoints() {
        return waypoints;
    }
    
    public RouteGraphic getRouteGraphics() {
        return routeGraphics;
    }
}

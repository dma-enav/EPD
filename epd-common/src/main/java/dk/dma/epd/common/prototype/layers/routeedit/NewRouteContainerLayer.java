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
package dk.dma.epd.common.prototype.layers.routeedit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.LinkedList;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.layers.route.RouteGraphic;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;

/**
 * Container layer for new route drawing.
 */
public class NewRouteContainerLayer extends EPDLayerCommon {

    private static final long serialVersionUID = 1L;
    
    private LinkedList<RouteWaypoint> waypoints = new LinkedList<>();
    private Route route;
    private RouteGraphic routeGraphics;

    /**
     * Constructor
     */
    public NewRouteContainerLayer() {
        super();
        
        route = new Route();
        route.setWaypoints(waypoints);
        routeGraphics = new RouteGraphic(true, new BasicStroke(2), Color.black);
        graphics.add(routeGraphics);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized OMGraphicList prepare() {
        routeGraphics.setRoute(route);
        graphics.project(getProjection());
        return graphics;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndUndo(Object obj) {
        super.findAndUndo(obj);
    }

    /**
     * Returns the route of this layer
     * @return the route of this layer
     */
    public Route getRoute() {
        return route;
    }
    
    /**
     * Returns the way points of this layer
     * @return the way points of this layer
     */
    public LinkedList<RouteWaypoint> getWaypoints() {
        return waypoints;
    }
    
    /**
     * Returns the route graphics
     * @return the route graphics
     */
    public RouteGraphic getRouteGraphics() {
        return routeGraphics;
    }
}

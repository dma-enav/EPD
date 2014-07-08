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
package dk.dma.epd.shore.layers.route;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.metoc.MetocGraphic;
import dk.dma.epd.common.prototype.layers.route.ActiveRouteGraphic;
import dk.dma.epd.common.prototype.layers.route.RouteGraphic;
import dk.dma.epd.common.prototype.layers.route.RouteLayerCommon;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.MapMenu;


//import dk.frv.enav.ins.gui.MapMenu;

/**
 * Layer for showing routes
 */
public class RouteLayer extends RouteLayerCommon {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public RouteLayer() {
        super();
        
        // Hmmm, findAndInit not called with the RouteManager.
        // The RouteManager is in a different bean context, methinks...
        routeManager = EPDShore.getInstance().getRouteManager();
        routeManager.addListener(this);
    }
    
    /**
     * Returns a reference to the map menu
     * @return a reference to the map menu
     */
    @Override
    public MapMenu getMapMenu() {
        return (MapMenu)mapMenu;
    }   

    /**
     * Called when routes are changed
     * @param e the routes update event
     */
    @Override
    public synchronized void routesChanged(RoutesUpdateEvent e) {
        if(e == RoutesUpdateEvent.ROUTE_MSI_UPDATE) {
            return;
        }

        graphics.clear();

        float routeWidth = EPD.getInstance().getSettings().getNavSettings().getRouteWidth();
        Stroke stroke = new BasicStroke(
                routeWidth,                // Width
                BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_MITER,    // Join style
                10.0f,                     // Miter limit
                new float[] { 3.0f, 10.0f }, // Dash pattern
                0.0f);
        Stroke activeStroke = new BasicStroke(
                routeWidth,                // Width
                BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_MITER,    // Join style
                10.0f,                     // Miter limit
                new float[] { 10.0f, 8.0f }, // Dash pattern
                0.0f);                     // Dash phase
        Color ECDISOrange = new Color(213, 103, 45, 255);

        int activeRouteIndex = routeManager.getActiveRouteIndex();
        for (int i = 0; i < routeManager.getRoutes().size(); i++) {
            Route route = routeManager.getRoutes().get(i);
            if(route.isVisible() && i != activeRouteIndex){
                RouteGraphic routeGraphic = new RouteGraphic(route, i, arrowsVisible, stroke, ECDISOrange);
                graphics.add(routeGraphic);
            }
        }

        if (routeManager.isRouteActive()) {
            ActiveRoute activeRoute = routeManager.getActiveRoute();
            if (activeRoute.isVisible()) {
                ActiveRouteGraphic activeRouteExtend = new ActiveRouteGraphic(activeRoute, activeRouteIndex, arrowsVisible, activeStroke, Color.RED);
                graphics.add(activeRouteExtend);
            }
        }

        // Handle route metoc
        metocGraphics.clear();
        for (int i = 0; i < routeManager.getRoutes().size(); i++) {
            Route route = routeManager.getRoutes().get(i);
            boolean activeRoute = false;

            if (routeManager.isActiveRoute(i)) {
                route = routeManager.getActiveRoute();
                activeRoute = true;
            }

            if (routeManager.showMetocForRoute(route)) {
                routeMetoc = new MetocGraphic(route, activeRoute, EPDShore.getInstance().getSettings().getEnavSettings());
                metocGraphics.add(routeMetoc);
            }
        }
        if (metocGraphics.size() > 0) {
            graphics.add(0, metocGraphics);
        }

        graphics.project(getProjection(), true);

        doPrepare();
    }
}

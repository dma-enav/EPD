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
package dk.dma.epd.ship.route;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;

/**
 * A serializable class for storing route information
 */
public class RouteStore implements Serializable {

    private static final long serialVersionUID = -2051449429771367917L;
    
    private List<RouteSuggestionData> suggestedRoutes = new LinkedList<>();
    private List<Route> routes = new LinkedList<>();
    private ActiveRoute activeRoute;
    private int activeRouteIndex = -1;
    
    public RouteStore(RouteManager routeManager) {
        this.routes = routeManager.getRoutes();
        this.activeRoute = routeManager.getActiveRoute();
        this.activeRouteIndex = routeManager.getActiveRouteIndex();
    }
    
    public List<Route> getRoutes() {
        return routes;
    }
    
    public ActiveRoute getActiveRoute() {
        return activeRoute;
    }
    
    public int getActiveRouteIndex() {
        return activeRouteIndex;
    }

    public List<RouteSuggestionData> getSuggestedRoutes() {
        return suggestedRoutes;
    }



    
}

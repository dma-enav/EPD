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

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
package dk.dma.epd.shore.route;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dk.dma.epd.common.prototype.ais.AisAdressedRouteSuggestion;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;

/**
 * A serializable class for storing route information
 */
public class RouteStore implements Serializable {

    private static final long serialVersionUID = 1L;

    private Set<AisAdressedRouteSuggestion> addressedSuggestedRoutes = new HashSet<AisAdressedRouteSuggestion>();
    private List<Route> routes = new LinkedList<Route>();
    private ActiveRoute activeRoute;
    private int activeRouteIndex = -1;

    public RouteStore(RouteManager routeManager) {
        this.routes = routeManager.getRoutes();
        this.activeRoute = routeManager.getActiveRoute();
        this.activeRouteIndex = routeManager.getActiveRouteIndex();
        this.addressedSuggestedRoutes = routeManager.getAddressedSuggestedRoutes();
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

    public Set<AisAdressedRouteSuggestion> getAddressedSuggestedRoutes() {
        return addressedSuggestedRoutes;
    }

}

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
package dk.dma.epd.common.prototype.route;

import java.util.List;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;

public class RouteManager extends MapHandlerChild {

    public void addRoute(Route route) {

    }

    public List<Route> getRoutes() {
        return null;
    }

    public Route getRoute(int index) {
        return null;
    }

    public boolean isActiveRoute(int routeId) {
        // TODO Auto-generated method stub
        return false;
    }

    public Route getActiveRoute() {
        // TODO Auto-generated method stub
        return null;
    }

    public void notifyListeners(RoutesUpdateEvent routeWaypointDeleted) {
        // TODO Auto-generated method stub
        
    }

    public boolean validateMetoc(Route route) {
        // TODO Auto-generated method stub
        return false;
    }

    public int getRouteIndex(Route route) {
        // TODO Auto-generated method stub
        return -1;
    }

    public void removeRoute(int routeIndex) {
        // TODO Auto-generated method stub
        
    }

    public void changeActiveWp(int index) {
        // TODO Auto-generated method stub
        
    }
}

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

import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.msi.MsiHandler;

public class RouteManagerCommon extends MapHandlerChild {

    public void addRoute(Route route) {

    }

    public List<Route> getRoutes() {
        return null;
    }

    public Route getRoute(int index) {
        return null;
    }
    
    public ActiveRoute getActiveRoute() {
        return null;
        
    }

    public void addListener(MsiHandler msiHandler) {
        
    }

    public boolean isActiveRoute(int routeId) {
        return false;
    }

    public void notifyListeners(RoutesUpdateEvent routeWaypointDeleted) {
                
    }

    public boolean validateMetoc(Route route) {        
        return false;
    }

    public int getRouteIndex(Route route) {
        return -1;
    }

    public void removeRoute(int routeIndex) {
        
    }

    public void changeActiveWp(int index) {
        
    }
    
    public void requestRouteMetoc(Route route) throws ShoreServiceException {
        
    }
}

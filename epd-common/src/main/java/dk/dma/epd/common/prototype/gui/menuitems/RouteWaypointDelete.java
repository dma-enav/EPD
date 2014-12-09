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
package dk.dma.epd.common.prototype.gui.menuitems;

import javax.swing.JOptionPane;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;

public class RouteWaypointDelete extends RouteMenuItem {
    
    private static final long serialVersionUID = 1L;
    private int routeWaypointIndex;

    public RouteWaypointDelete(String text) {
        super();
        setText(text);
    }
    
    @Override
    public void doAction() {
        Route route = EPD.getInstance().getRouteManager().getRoute(routeIndex);
        if (route.getWaypoints().size() < 3) {

            int result = JOptionPane
                    .showConfirmDialog(
                            EPD.getInstance().getMainFrame(),
                            "A route must have at least two waypoints.\nDo you want to delete the route?",
                            "Delete Route?", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
//            System.out.println(result);
            if (result == JOptionPane.YES_OPTION) {
                route.deleteWaypoint(routeWaypointIndex);

                EPD.getInstance().getRouteManager().removeRoute(routeIndex);

                EPD.getInstance().getRouteManager()
                        .notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_DELETED);
            }
        } else {
            route.deleteWaypoint(routeWaypointIndex);
            EPD.getInstance().getRouteManager()
                    .notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_DELETED);
        }
        
        

    }
    
    public void setRouteWaypointIndex(int routeWaypointIndex) {
        this.routeWaypointIndex = routeWaypointIndex;
    }
}

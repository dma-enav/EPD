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
package dk.dma.epd.ship.gui.menuitems;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.route.RouteManager;

public class RouteWaypointDelete extends JMenuItem implements IMapMenuAction {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int routeIndex;
    private int routeWaypointIndex;
    private RouteManager routeManager;

    public RouteWaypointDelete(String text) {
        super();
        setText(text);
    }
    
    @Override
    public void doAction() {
        Route route = routeManager.getRoute(routeIndex);
        
        if(route.getWaypoints().size() < 3){
            
            int result = JOptionPane.showConfirmDialog(EPDShip.getMainFrame(), "A route must have at least two waypoints.\nDo you want to delete the route?", "Delete Route?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(result == JOptionPane.YES_OPTION){
                boolean delete = route.deleteWaypoint(routeWaypointIndex);
                if(delete){
                    routeManager.removeRoute(routeIndex);
                }
                routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_DELETED);
            }
            
            
        }
        
        

    }
    
    public void setRouteWaypointIndex(int routeWaypointIndex) {
        this.routeWaypointIndex = routeWaypointIndex;
    }
    
    public void setRouteManager(RouteManager routeManager) {
        this.routeManager = routeManager;
    }
    
    public void setRouteIndex(int routeIndex) {
        this.routeIndex = routeIndex;
    }
}

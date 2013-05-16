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

import java.util.LinkedList;

import javax.swing.JMenuItem;

import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.layers.routeEdit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.route.RouteManager;

public class RouteEditEndRoute extends JMenuItem implements IMapMenuAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private NewRouteContainerLayer newRouteLayer;
    private RouteManager routeManager;

    public RouteEditEndRoute(String text) {
        super();
        setText(text);
    }

    @Override
    public void doAction() {
        if (newRouteLayer.getRoute().getWaypoints().size() > 1) {
            
            
            
            
            Route route = new Route(newRouteLayer.getRoute());
            route.setName("New route");
            int i = 1;
            LinkedList<RouteWaypoint> waypoints = route.getWaypoints();
            for (RouteWaypoint routeWaypoint : waypoints) {
                if (routeWaypoint.getOutLeg() != null) {
                    RouteLeg outLeg = routeWaypoint.getOutLeg();
                    double xtd = EPDShip.getSettings().getNavSettings().getDefaultXtd();
                    outLeg.setXtdPort(xtd);
                    outLeg.setXtdStarboard(xtd);
                    outLeg.setHeading(Heading.RL);
                    outLeg.setSpeed(EPDShip.getSettings().getNavSettings().getDefaultSpeed());
                }
                routeWaypoint.setTurnRad(EPDShip.getSettings().getNavSettings().getDefaultTurnRad());
                routeWaypoint.setName(String.format("WP_%03d", i));
                i++;
            }
            
            route.adjustStartTime();
            route.calcValues(true);
            
            routeManager.addRoute(route);
            routeManager.notifyListeners(null);
        }
        newRouteLayer.getWaypoints().clear();
        newRouteLayer.getRouteGraphics().clear();
        newRouteLayer.doPrepare();
        EPDShip.getMainFrame().getChartPanel().setMouseMode(1);
    }

    public void setNewRouteLayer(NewRouteContainerLayer newRouteLayer) {
        this.newRouteLayer = newRouteLayer;
    }

    public void setRouteManager(RouteManager routeManager) {
        this.routeManager = routeManager;
    }
}

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
package dk.dma.epd.ship.gui.menuitems;

import java.util.LinkedList;

import javax.swing.JMenuItem;

import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.layers.routeedit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.event.NavigationMouseMode;
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
                    double xtd = EPDShip.getInstance().getSettings().getNavSettings().getDefaultXtd();
                    outLeg.setXtdPort(xtd);
                    outLeg.setXtdStarboard(xtd);
                    outLeg.setHeading(Heading.RL);
                    outLeg.setSpeed(EPDShip.getInstance().getSettings().getNavSettings().getDefaultSpeed());
                }
                routeWaypoint.setTurnRad(EPDShip.getInstance().getSettings().getNavSettings().getDefaultTurnRad());
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
        //EPDShip.getInstance().getMainFrame().getChartPanel().setMouseMode(1);
        EPDShip.getInstance().getMainFrame().getChartPanel().setMouseMode(NavigationMouseMode.MODE_ID);
    }

    public void setNewRouteLayer(NewRouteContainerLayer newRouteLayer) {
        this.newRouteLayer = newRouteLayer;
    }

    public void setRouteManager(RouteManager routeManager) {
        this.routeManager = routeManager;
    }
}

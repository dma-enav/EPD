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
package dk.dma.epd.shore.gui.views.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.shore.gui.views.ToolBar;

public class RouteEditEndRoute extends JMenuItem implements IMapMenuAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
//    private NewRouteContainerLayer newRouteLayer;
//    private RouteManager routeManager;
    private ToolBar toolBar;

    public RouteEditEndRoute(String text) {
        super();
        setText(text);
    }

    @Override
    public void doAction() {

        //
        // if (newRouteLayer.getRoute().getWaypoints().size() > 1) {
        // Route route = new Route(newRouteLayer.getRoute());
        // route.setName("New route");
        // int i = 1;
        // LinkedList<RouteWaypoint> waypoints = route.getWaypoints();
        // for (RouteWaypoint routeWaypoint : waypoints) {
        // if (routeWaypoint.getOutLeg() != null) {
        // RouteLeg outLeg = routeWaypoint.getOutLeg();
        // double xtd = ESD.getSettings().getNavSettings().getDefaultXtd();
        // outLeg.setXtdPort(xtd);
        // outLeg.setXtdStarboard(xtd);
        // outLeg.setHeading(Heading.RL);
        // outLeg.setSpeed(ESD.getSettings().getNavSettings().getDefaultSpeed());
        // }
        // routeWaypoint.setTurnRad(ESD.getSettings().getNavSettings().getDefaultTurnRad());
        // routeWaypoint.setName(String.format("WP_%03d", i));
        // i++;
        // }
        // route.calcValues(true);
        // routeManager.addRoute(route);
        // routeManager.notifyListeners(null);
        // }
        // newRouteLayer.getWaypoints().clear();
        // newRouteLayer.getRouteGraphics().clear();
        // newRouteLayer.doPrepare();

        // Edit mode
        // ESD.getMainFrame().getChartPanel().editMode(false);

        toolBar.newRoute();

    }

    public void setToolBar(ToolBar toolBar) {
        this.toolBar = toolBar;
    }
}

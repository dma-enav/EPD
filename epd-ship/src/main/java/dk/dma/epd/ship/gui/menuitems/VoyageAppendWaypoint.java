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

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.voyage.VoyageUpdateEvent;
import dk.dma.epd.ship.EPDShip;

/**
 * @author Janus Varmarken
 */
public class VoyageAppendWaypoint extends JMenuItem implements IMapMenuAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Route to which waypoint is to be appended.
     */
    private Route route;

    /**
     * Index that specifies the type of the voyage associated with this menu
     * item (e.g. a modified STCC route).
     */
    private int routeIndex;

    public VoyageAppendWaypoint(String menuItemText) {
        super(menuItemText);
    }

    @Override
    public void doAction() {
        System.out.println("VoyageAppendWaypoint clicked!");
        this.route.appendWaypoint();
        // Notify listeners of the new waypoint
        EPDShip.getInstance().getVoyageEventDispatcher().notifyListenersOfVoyageUpdate(
                VoyageUpdateEvent.WAYPOINT_APPENDED, this.route,
                this.routeIndex);
    }

    /**
     * Set the route that this menu item will append waypoint(s) to.
     * 
     * @param r
     *            The route to which waypoints will be appended.
     */
    public void setRoute(Route r) {
        this.route = r;
    }

    /**
     * Set the route index that specifies the "type" of the route associated
     * with this menu item (e.g. if it is a modified STCC rotue)
     * 
     * @param routeIndex
     *            The new route index.
     */
    public void setRouteIndex(int routeIndex) {
        this.routeIndex = routeIndex;
    }
}

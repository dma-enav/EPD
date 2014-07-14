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

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.voyage.VoyageUpdateEvent;
import dk.dma.epd.ship.EPDShip;

/**
 * @author Janus Varmarken
 */
public class VoyageHandlingWaypointDelete extends JMenuItem implements
        IMapMenuAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The voyage that this menu item modifies.
     */
    private Route route;
    
    /**
     * Index that specifies the type of the voyage associated with this menu
     * item (e.g. a modified STCC route).
     */
    private int routeIndex;

    /**
     * The index of the waypoint that is to be deleted.
     */
    private int voyageWaypointIndex;

    public VoyageHandlingWaypointDelete(String menuItemText) {
        super(menuItemText);
    }

    @Override
    public void doAction() {
        if (this.route.getWaypoints().size() < 3) {
            JOptionPane.showMessageDialog(EPDShip.getInstance().getMainFrame(),
                    "You cannot delete the waypoint as this would "
                            + "create a route with no travel distance.");
        } else {
            this.route.deleteWaypoint(this.voyageWaypointIndex);
            EPDShip.getInstance().getVoyageEventDispatcher().notifyListenersOfVoyageUpdate(
                    VoyageUpdateEvent.WAYPOINT_DELETED, this.route, this.routeIndex);
        }
    }

    /**
     * Set the route that this menu item will modify (by deleting a waypoint)
     * when its doAction is invoked.
     * 
     * @param routeToBeModified
     *            The route that will have waypoint(s) deleted by invocations of
     *            this menu item.
     */
    public void setRoute(Route routeToBeModified) {
        this.route = routeToBeModified;
    }

    /**
     * Set the index of the waypoint that is to be deleted.
     * 
     * @param waypointIndex
     *            The index of the waypoint that is to be deleted.
     */
    public void setVoyageWaypointIndex(int waypointIndex) {
        this.voyageWaypointIndex = waypointIndex;
    }

    /**
     * Set the route index that specifies the "type" of the route associated
     * with this menu item (e.g. if it is a modified STCC route)
     * 
     * @param routeIndex
     *            The new route index.
     */
    public void setRouteIndex(int routeIndex) {
        this.routeIndex = routeIndex;
    }
}

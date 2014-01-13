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
import javax.swing.JOptionPane;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.layers.voyage.VoyageHandlingLayer;

public class VoyageHandlingWaypointDelete extends JMenuItem implements IMapMenuAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int routeWaypointIndex;
    private Route route;
    private VoyageHandlingLayer voyageHandlingLayer;

    public VoyageHandlingWaypointDelete(String text) {
        super();
        setText(text);
    }

    @Override
    public void doAction() {
        if (route.getWaypoints().size() < 3) {

            JOptionPane.showMessageDialog(EPDShore.getInstance().getMainFrame(), "You cannot delete the waypoint");

        } else {
            route.deleteWaypoint(routeWaypointIndex);
            voyageHandlingLayer.updateVoyages();
        }
    }

    public void setRouteWaypointIndex(int routeWaypointIndex) {
        this.routeWaypointIndex = routeWaypointIndex;
    }

    /**
     * @param route the route to set
     */
    public void setRoute(Route route) {
        this.route = route;
    }

    /**
     * @param voyageHandlingLayer the voyageHandlingLayer to set
     */
    public void setVoyageHandlingLayer(VoyageHandlingLayer voyageHandlingLayer) {
        this.voyageHandlingLayer = voyageHandlingLayer;
    }


    
}

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
package dk.dma.epd.common.prototype.gui.menuitems;

import java.awt.MouseInfo;
import java.awt.Point;

import javax.swing.JOptionPane;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.route.EtaEditDialog;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.Route.EtaAdjust;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;

public class RouteWaypointEditEta extends RouteMenuItem {

    private static final long serialVersionUID = 1L;
    private int routeWaypointIndex;

    public RouteWaypointEditEta(String text) {
        super();
        setText(text);
    }

    @Override
    public void doAction() {
        Route route = EPD.getInstance().getRouteManager().getRoute(routeIndex);
        EtaEditDialog etaDialog = new EtaEditDialog(EPD.getInstance().getMainFrame(), route.getEtas().get(routeWaypointIndex),
                route.getWaypoints().get(routeWaypointIndex).getName());
        Point p = MouseInfo.getPointerInfo().getLocation();
        EtaAdjust etaAdjust = etaDialog.getEtaAdjust((int) p.getX() - 30, (int) p.getY() - 30);

        if (etaAdjust != null) {
            try {
                route.adjustEta(routeWaypointIndex, etaAdjust);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(EPD.getInstance().getMainFrame(), "Input error: " + e.getMessage(), "Input error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            EPD.getInstance().getRouteManager().notifyListeners(RoutesUpdateEvent.ROUTE_CHANGED);
        }
    }

    public void setRouteWaypointIndex(int routeWaypointIndex) {
        this.routeWaypointIndex = routeWaypointIndex;
    }
}

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

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

import java.util.ArrayList;
import java.util.Date;

import dk.dma.epd.common.prototype.gui.MapMenuCommon;
import dk.dma.epd.common.prototype.gui.dialogs.ISimpleConfirmDialogListener;
import dk.dma.epd.common.prototype.gui.dialogs.SimpleConfirmDialog;
import dk.dma.epd.common.prototype.gui.menuitems.RouteMenuItem;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.ship.EPDShip;

public class RouteActivateToggle extends RouteMenuItem implements ISimpleConfirmDialogListener {

    private static final long serialVersionUID = 1L;
    private MapMenuCommon parentMenu;

    public RouteActivateToggle(MapMenuCommon parentMenu) {
        this.parentMenu = parentMenu;
    }

    @Override
    public void doAction() {

        if (EPDShip.getInstance().getRouteManager().getActiveRouteIndex() == routeIndex) {
            EPDShip.getInstance().getRouteManager().deactivateRoute();
        } else {

            Date waypointEndDate = EPDShip.getInstance().getRouteManager().getRoute(routeIndex).getEtas()
                    .get(EPDShip.getInstance().getRouteManager().getRoute(routeIndex).getEtas().size() - 1);

            if (waypointEndDate.compareTo(PntTime.getInstance().getDate()) < 0) {
                ArrayList<ISimpleConfirmDialogListener> diaListeners = new ArrayList<ISimpleConfirmDialogListener>();
                diaListeners.add(this);
                SimpleConfirmDialog
                        .showSimpleConfirmDialog(
                                "Activate Route",
                                "The planned ETAS of the route is in the past, \n recommend you update the route ETAs before activating.\n Do you still wish to activate it?",
                                diaListeners, parentMenu.getLatestVisibleLocation());
            } else {
                EPDShip.getInstance().getRouteManager().activateRoute(routeIndex);
            }

        }

    }

    @Override
    public void onNoClicked() {
        // User cancelled route deletion, do nothing.
    }

    @Override
    public void onYesClicked() {
        // User confirmed route deletion
        EPDShip.getInstance().getRouteManager().activateRoute(routeIndex);
    }
}

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

import java.util.Date;

import javax.swing.JOptionPane;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.menuitems.RouteMenuItem;
import dk.dma.epd.common.prototype.gui.route.RoutePropertiesDialogCommon;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.ship.EPDShip;

public class RouteActivateToggle extends RouteMenuItem {

    private static final long serialVersionUID = 1L;

    public RouteActivateToggle() {

    }

    @Override
    public void doAction() {

        if (EPDShip.getInstance().getRouteManager().getActiveRouteIndex() == routeIndex) {
            EPDShip.getInstance().getRouteManager().deactivateRoute();
        } else {
            Date waypointEndDate = EPDShip.getInstance().getRouteManager().getRoute(routeIndex).getEtas()
                    .get(EPDShip.getInstance().getRouteManager().getRoute(routeIndex).getEtas().size() - 1);

            if (waypointEndDate.compareTo(PntTime.getDate()) < 0) {
                String[] options = { "Adjust ETA's", "Activate", "Cancel" };
                int chosen = JOptionPane
                        .showOptionDialog(
                                this,
                                "The planned ETAs of the route is in the past, \n recommend you adjust the route ETAs before activating.",
                                "Activate route", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                                options[0]);
                System.out.println("chosen: " + chosen);
                switch (chosen) {
                case 0:
                    RoutePropertiesDialogCommon routePropertiesDialog = new RoutePropertiesDialogCommon(EPD.getInstance()
                            .getMainFrame(), EPDShip.getInstance().getMainFrame().getChartPanel(), routeIndex);
                    routePropertiesDialog.setVisible(true);
                    break;
                case 1:
                    EPDShip.getInstance().getRouteManager().activateRoute(routeIndex);
                    break;
                default:
                    break;
                }
            } else {
                EPDShip.getInstance().getRouteManager().activateRoute(routeIndex);
            }
        }
    }

}

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
package dk.dma.epd.ship.monalisa;

import java.util.List;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.monalisa.MonaLisaOptimizationResponse;
import dk.dma.epd.common.prototype.monalisa.SSPAResponse;
import dk.dma.epd.common.prototype.monalisa.sspa.RouterequestType;
import dk.dma.epd.ship.EPDShip;

//import dk.frv.enav.ins.route.monalisa.se.sspa.optiroute.Routerequest;

/**
 * Shore service component providing the functional link to shore.
 */
public class MonaLisaRouteOptimizaton extends dk.dma.epd.common.prototype.monalisa.MonaLisaRouteOptimizaton{
    // Runnable

    // private static final Logger LOG = Logger
    // .getLogger(MonaLisaRouteExchange.class);


    @Override
    public MonaLisaOptimizationResponse makeRouteRequest(Route route,
            boolean removeIntermediateETA, float draft, int ukc, int timeout,
            List<Boolean> selectedWp, boolean showInput, boolean showOutput) {

        // new Thread(this).start();

        RouterequestType monaLisaRoute = convertRoute(route,
                removeIntermediateETA, draft, ukc, selectedWp);

        // monaLisaRoute.

        SSPAResponse routeResponse = null;
        try {
            routeResponse = EPDShip.getShoreServices()
                    .makeMonaLisaRouteRequest(monaLisaRoute, timeout,
                            showInput, showOutput);
        } catch (Exception e) {
            return new MonaLisaOptimizationResponse("An exception occured", e.getMessage());
        }

        if (!routeResponse.isValid()) {
            return new MonaLisaOptimizationResponse("Server error",
                    routeResponse.getErrorMessage());
        }

        Route newRoute = null;

        if (routeResponse.isValid()) {
            try {
                newRoute = convertRouteBack(routeResponse.getMonaLisaResponse());
            } catch (Exception e) {
                return new MonaLisaOptimizationResponse("An exception occured",
                        e.getMessage());
            }
        }

        if (newRoute != null) {
            EPDShip.getRouteManager().addRoute(newRoute);
            route.setVisible(false);
        }

        float fuelSaving = (routeResponse.getMonaLisaResponse().getFuelRequested() - routeResponse.getMonaLisaResponse().getFuelFinal()) / routeResponse.getMonaLisaResponse().getFuelRequested() * 100;

        return new MonaLisaOptimizationResponse("Succesfully recieved optimized route",

        "\nInitial route consumption is "
                + routeResponse.getMonaLisaResponse().getFuelRequested()
                + " Metric Tons.\n"
                + "MonaLisa optimized route consumption is "
                + routeResponse.getMonaLisaResponse().getFuelFinal()
                + " Metric Tons.\n" + "The relative fuel saving is "
                + fuelSaving + " percent\n\n" + "Minimum route UKC is "
                + routeResponse.getMonaLisaResponse().getUkcActual()
                + " meters.\n");

    }
}

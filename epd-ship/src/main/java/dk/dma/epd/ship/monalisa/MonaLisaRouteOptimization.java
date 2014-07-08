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
package dk.dma.epd.ship.monalisa;

import java.util.List;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.monalisa.MonaLisaOptimizationResponse;
import dk.dma.epd.common.prototype.monalisa.SSPAResponse;
import dk.dma.epd.common.prototype.monalisa.sspa.RouterequestType;

//import dk.frv.enav.ins.route.monalisa.se.sspa.optiroute.Routerequest;

/**
 * Shore service component providing the functional link to shore.
 */
public class MonaLisaRouteOptimization extends dk.dma.epd.common.prototype.monalisa.MonaLisaRouteOptimizationCommon{
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
            routeResponse = shoreService
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
            route.setVisible(false);
            routeManager.addRoute(newRoute);

        }

        float fuelSaving = (routeResponse.getMonaLisaResponse().getFuelRequested() - routeResponse.getMonaLisaResponse().getFuelFinal()) / routeResponse.getMonaLisaResponse().getFuelRequested() * 100;

        return new MonaLisaOptimizationResponse("Succesfully received optimized route",

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

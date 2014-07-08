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
package dk.dma.epd.shore.gui.views.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.service.StrategicRouteHandler;
import dk.dma.epd.shore.voyage.Voyage;

public class VoyageRenegotiate extends JMenuItem implements IMapMenuAction {

    private long transactionid;
    private AisHandlerCommon aisHandler;
    private static final long serialVersionUID = 1L;
    private StrategicRouteHandler strategicRouteHandler;

    /**
     * @param transactionid
     *            the transactionid to set
     */
    public void setTransactionid(long transactionid) {
        this.transactionid = transactionid;
    }

    public VoyageRenegotiate(String text) {
        super();
        setText(text);
    }

    /**
     * @param aisHandler
     *            the aisHandler to set
     */
    public void setAisHandler(AisHandlerCommon aisHandler) {
        this.aisHandler = aisHandler;
    }

    /**
     * @param strategicRouteHandler
     *            the strategicRouteHandler to set
     */
    public void setStrategicRouteHandler(
            StrategicRouteHandler strategicRouteHandler) {
        this.strategicRouteHandler = strategicRouteHandler;
    }

    @Override
    public void doAction() {
        handleNegotiation();

    }

    private void handleNegotiation() {

        if (strategicRouteHandler.getStrategicNegotiationData().containsKey(
                transactionid)) {

            StrategicRouteNegotiationData routeData = strategicRouteHandler
                    .getStrategicNegotiationData().get(transactionid);

            String shipName = "" + routeData.getMmsi();

            VesselTarget vesselTarget = aisHandler.getVesselTarget(routeData
                    .getMmsi());
            if (vesselTarget != null) {
                if (vesselTarget.getStaticData() != null) {
                    shipName = vesselTarget.getStaticData().getTrimmedName();
                }
            }

            // Get latest route
            Route route = routeData.getLatestRoute();

            Voyage voyage = new Voyage(routeData.getMmsi(), route,
                    routeData.getId());

            Route originalRoute = routeData.getOriginalRoute();

            EPDShore.getInstance()
                    .getMainFrame()
                    .addStrategicRouteExchangeHandlingWindow(originalRoute,
                            shipName, voyage, true);
        }
    }
}

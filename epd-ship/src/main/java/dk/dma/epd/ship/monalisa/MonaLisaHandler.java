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

import java.awt.Point;
import java.util.HashMap;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteRequestReply;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteStatus;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.monalisa.MonaLisaSTCCDialog;
import dk.dma.epd.ship.layers.voyage.VoyageLayer;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.service.EnavServiceHandler;

public class MonaLisaHandler extends MapHandlerChild {

    private AisHandler aisHandler;
    private EnavServiceHandler enavServiceHandler;

    private HashMap<Long, MonaLisaRouteNegotiationData> monaLisaNegotiationData = new HashMap<Long, MonaLisaRouteNegotiationData>();

    private MonaLisaSTCCDialog monaLisaSTCCDialog;

    private VoyageLayer voyageLayer;
    private RouteManager routeManager;

    private boolean transaction;

    private Route route;

    public MonaLisaHandler() {

    }

    public void handleInput(Route route, Point windowLocation) {

        this.route = route;

        if (monaLisaSTCCDialog == null) {
            monaLisaSTCCDialog = EPDShip.getMainFrame().getMonaLisaSTCCDialog();
        }

        
        
        // Is there already a transaction in progress
        if (transaction) {
            // Transaction is in progress, so just show the dialog
            monaLisaSTCCDialog.setVisible(true);

        } else {

            // No transaction in progress

            // Start the transaction
            transaction = true;

            // Display the route
            voyageLayer.startRouteNegotiation(route.copy());

            // Hide the routeLayer one
            route.setVisible(false);
            routeManager
                    .notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);

            long ownMMSI = aisHandler.getOwnShip().getMmsi();

            // Sending route
            sendMonaLisaRouteRequest(route, ownMMSI, "Route Approval Requested");

            // Display and initialize the GUI
            monaLisaSTCCDialog.initializeNew();
            monaLisaSTCCDialog.setLocation(windowLocation);
            monaLisaSTCCDialog.setLocationRelativeTo(EPDShip.getMainFrame());
            monaLisaSTCCDialog.setVisible(true);
            monaLisaSTCCDialog.setRouteName(route);

        }

    }

    private void sendMonaLisaRouteRequest(Route route, long sender,
            String message) {

        long transactionID = System.currentTimeMillis();

        MonaLisaRouteRequestMessage routeMessage = new MonaLisaRouteService.MonaLisaRouteRequestMessage(
                transactionID, route.getFullRouteData(), sender, message);

        MonaLisaRouteNegotiationData entry;

        // Existing transaction already established
        if (monaLisaNegotiationData.containsKey(transactionID)) {

            entry = monaLisaNegotiationData.get(transactionID);
        } else {
            // Create new entry for the transaction
            entry = new MonaLisaRouteNegotiationData(transactionID);
        }

        entry.addMessage(routeMessage);

        monaLisaNegotiationData.put(transactionID, entry);
        
        
        // Send it off
        enavServiceHandler.sendMonaLisaRouteRequest(routeMessage);
    }

    public void handleReply(MonaLisaRouteRequestReply reply) {

        long transactionID = reply.getId();

        MonaLisaRouteNegotiationData entry;
        // Existing transaction already established
        if (monaLisaNegotiationData.containsKey(transactionID)) {

            entry = monaLisaNegotiationData.get(transactionID);
        } else {
            // Create new entry for the transaction - if ship disconnected, it
            // can still recover - maybe?
            entry = new MonaLisaRouteNegotiationData(transactionID);
            monaLisaNegotiationData.put(transactionID, entry);
        }

        // Store the reply
        entry.addReply(reply);
        
        
        
        System.out.println("Adding entry for " + transactionID);

        // How to handle the reply

        // 1 shore sends back accepted - ship needs to send ack
        // 2 shore sends back new route - ship renegotationes
        // 3 shore sends back rejected - ship sends ack

        // Let GUI handle front-end
        monaLisaSTCCDialog.handleReply(reply);

        // Let layer handle itself
        voyageLayer.handleReply(reply);

        // Three kinds of reply?

        // If success, nothing more
        // If fail and new route returned, start new communication message, like
        // previous, with updated route, same ID maybe?
        // Do we need a message / give reason?

        // Do we need to display on voyageLayer?
        // If agree make fat line green
        // If changes draw old one in red and new one in green with lines
        // seperated on
        // if reject make red and end transaction? or send ack and then end
        // transaction - wait with reject

    }

    public void sendAgreeMsg(long transactionID) {

        System.out.println("Send agree msg? " + transactionID);

        // Send ack message
        // remove from voyage layer show original route
        voyageLayer.routeAccepted();

        route.setVisible(true);
        routeManager
                .notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);

        if (monaLisaNegotiationData.containsKey(transactionID)) {

            MonaLisaRouteNegotiationData transactionData = monaLisaNegotiationData
                    .get(transactionID);

            enavServiceHandler.sendMonaLisaAck(transactionData.getRouteReply()
                    .get(0).getMmsi(), transactionID, transactionData
                    .getRouteMessage().get(0).getMmsi(), true);
        }
        
        transaction = false;
    }

    public void cancelRouteRequest() {
        transaction = false;
        voyageLayer.cancelRequest();

        route.setVisible(true);
        routeManager
                .notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
        } else if (obj instanceof EnavServiceHandler) {
            enavServiceHandler = (EnavServiceHandler) obj;
        } else if (obj instanceof VoyageLayer) {
            voyageLayer = (VoyageLayer) obj;
        } else if (obj instanceof RouteManager) {
            routeManager = (RouteManager) obj;
        }
    }

}

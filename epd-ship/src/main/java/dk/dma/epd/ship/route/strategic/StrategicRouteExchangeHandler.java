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
package dk.dma.epd.ship.route.strategic;

import java.awt.Point;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestReply;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.route.strategic.RequestStrategicRouteDialog;
import dk.dma.epd.ship.layers.voyage.VoyageLayer;
import dk.dma.epd.ship.ownship.OwnShipHandler;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.service.EnavServiceHandler;

public class StrategicRouteExchangeHandler extends MapHandlerChild {

    private OwnShipHandler ownShipHandler;
    private EnavServiceHandler enavServiceHandler;

    private HashMap<Long, StrategicRouteNegotiationData> monaLisaNegotiationData = new HashMap<Long, StrategicRouteNegotiationData>();

    private RequestStrategicRouteDialog monaLisaSTCCDialog;

    private VoyageLayer voyageLayer;
    private RouteManager routeManager;

    private boolean transaction;

    private Route route;

    private boolean routeModified;
    private long currentTransaction;

    public StrategicRouteExchangeHandler() {

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

            long ownMMSI = (ownShipHandler.getMmsi() == null) ? -1L : ownShipHandler.getMmsi();

            // Sending route
            long transactionID = sendMonaLisaRouteRequest(route, ownMMSI,
                    "Route Approval Requested");

            // Display and initialize the GUI
            monaLisaSTCCDialog.initializeNew();
            monaLisaSTCCDialog.setLocation(50, 50);
            // monaLisaSTCCDialog.setLocationRelativeTo(EPDShip.getMainFrame());
            monaLisaSTCCDialog.setVisible(true);
            monaLisaSTCCDialog.setRouteName(route, transactionID);

        }

    }

    public boolean isTransaction() {
        // System.out.println("Is transaction? " + transaction);
        return transaction;
    }

    private long sendMonaLisaRouteRequest(Route route, long sender,
            String message) {

        long transactionID = System.currentTimeMillis();

        StrategicRouteRequestMessage routeMessage = new StrategicRouteService.StrategicRouteRequestMessage(
                transactionID, route.getFullRouteData(), sender, message);

        StrategicRouteNegotiationData entry;

        // Existing transaction already established
        if (monaLisaNegotiationData.containsKey(transactionID)) {

            entry = monaLisaNegotiationData.get(transactionID);
        } else {
            // Create new entry for the transaction
            entry = new StrategicRouteNegotiationData(transactionID);
        }

        entry.addMessage(routeMessage);

        monaLisaNegotiationData.put(transactionID, entry);

        // Send it off
        enavServiceHandler.sendMonaLisaRouteRequest(routeMessage);

        monaLisaNegotiationData.get(transactionID).setStatus(
                StrategicRouteStatus.PENDING);

        return transactionID;
    }

    public void handleReply(StrategicRouteRequestReply reply) {

        currentTransaction = reply.getId();

        StrategicRouteNegotiationData entry;
        // Existing transaction already established
        if (monaLisaNegotiationData.containsKey(currentTransaction)) {
            entry = monaLisaNegotiationData.get(currentTransaction);
        } else {
            // Create new entry for the transaction - if ship disconnected, it
            // can still recover - maybe?
            entry = new StrategicRouteNegotiationData(currentTransaction);
            monaLisaNegotiationData.put(currentTransaction, entry);
        }

        if (entry.getStatus() != StrategicRouteStatus.REJECTED) {

            // Store the reply
            entry.addReply(reply);
            monaLisaNegotiationData.get(currentTransaction).setStatus(
                    StrategicRouteStatus.NEGOTIATING);

            // System.out.println("Adding entry for " + currentTransaction);

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
            // If fail and new route returned, start new communication message,
            // like
            // previous, with updated route, same ID maybe?
            // Do we need a message / give reason?

            // Do we need to display on voyageLayer?
            // If agree make fat line green
            // If changes draw old one in red and new one in green with lines
            // seperated on
            // if reject make red and end transaction? or send ack and then end
            // transaction - wait with reject
        } else {
            System.out.println("Nope cant handle this old thing");
        }
    }

    public void handleReNegotiation(StrategicRouteRequestMessage message) {

        // Shore wants to renegotiate it
        transaction = true;

        currentTransaction = message.getId();

        StrategicRouteNegotiationData entry;
        // Existing transaction already established
        if (monaLisaNegotiationData.containsKey(currentTransaction)) {
            entry = monaLisaNegotiationData.get(currentTransaction);
        } else {
            // Create new entry for the transaction - if ship disconnected, it
            // can still recover - maybe?
            entry = new StrategicRouteNegotiationData(currentTransaction);
            monaLisaNegotiationData.put(currentTransaction, entry);
        }

        entry.setStatus(StrategicRouteStatus.NEGOTIATING);

        StrategicRouteRequestReply newReply = new StrategicRouteService.StrategicRouteRequestReply(
                message.getMessage(), message.getId(), message.getMmsi(),
                message.getSent().getTime(), StrategicRouteStatus.NEGOTIATING,
                message.getRoute());

        // Store the reply
        entry.addReply(newReply);
        monaLisaNegotiationData.get(currentTransaction).setStatus(
                StrategicRouteStatus.NEGOTIATING);

        // Find the old one and set not accepted, possibly hide it?
        for (int i = 0; i < routeManager.getRoutes().size(); i++) {
            if (routeManager.getRoutes().get(i).getMonalisarouteid() == currentTransaction) {

                routeManager.getRoutes().get(i).setStccApproved(false);

                if (routeManager.getActiveRouteIndex() == i) {

                } else {
                    routeManager.getRoutes().get(i).setVisible(false);
                }

                try {
                    routeManager
                            .getRoutes()
                            .get(i)
                            .setName(
                                    routeManager.getRoutes().get(i).getName()
                                            .split(":")[1].trim());
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }

        // System.out.println("Adding entry for " + currentTransaction);

        // How to handle the reply

        // 1 shore sends back accepted - ship needs to send ack
        // 2 shore sends back new route - ship renegotationes
        // 3 shore sends back rejected - ship sends ack

        // Let GUI handle front-end
        monaLisaSTCCDialog.handleReply(newReply);
        monaLisaSTCCDialog.setVisible(true);

        StrategicRouteNegotiationData transactionData = monaLisaNegotiationData
                .get(currentTransaction);

        Route lastRoute = new Route(transactionData.getLatestRoute());

        // if (transactionData.getRouteMessage().size() > transactionData
        // .getRouteReply().size()) {
        // lastRoute = new Route(transactionData.getRouteMessage()
        // .get(transactionData.getRouteMessage().size() - 1)
        // .getRoute());
        //
        // } else {
        // lastRoute = new Route(transactionData.getRouteReply()
        // .get(transactionData.getRouteReply().size() - 1).getRoute());
        //
        // }

        // Let layer handle itself
        voyageLayer.handleReNegotiation(newReply, lastRoute);

        // Three kinds of reply?

        // If success, nothing more
        // If fail and new route returned, start new communication message,
        // like
        // previous, with updated route, same ID maybe?
        // Do we need a message / give reason?

        // Do we need to display on voyageLayer?
        // If agree make fat line green
        // If changes draw old one in red and new one in green with lines
        // seperated on
        // if reject make red and end transaction? or send ack and then end
        // transaction - wait with reject

    }

    public void sendAgreeMsg(long transactionID, String message) {

        System.out.println("Send agree msg? " + transactionID);
        monaLisaSTCCDialog.setVisible(false);
        transaction = false;

        System.out.println("TRANSACTION IS NOW" + transaction);

        // Send ack message
        // remove from voyage layer show original route
        voyageLayer.routeAccepted();

        // voyageLayer.getModifiedSTCCRoute();

        voyageLayer.getModifiedSTCCRoute().setName(
                "STCC Approved: "
                        + voyageLayer.getModifiedSTCCRoute().getName());

        if (monaLisaNegotiationData.containsKey(transactionID)) {

            StrategicRouteNegotiationData transactionData = monaLisaNegotiationData
                    .get(transactionID);

            enavServiceHandler.sendMonaLisaAck(transactionData.getRouteReply()
                    .get(0).getMmsi(), transactionID, transactionData
                    .getRouteMessage().get(0).getMmsi(), true, message);

            monaLisaNegotiationData.get(transactionID).setStatus(
                    StrategicRouteStatus.AGREED);
        }

        Route route = voyageLayer.getModifiedSTCCRoute();
        route.setStccApproved(true);

        route.setMonalisarouteid(transactionID);

        // route.setVisible(true);
        routeManager.addRoute(route);
        routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_ADDED);

        boolean shouldActive = false;

        if (routeManager.getActiveRouteIndex() != -1) {
            int dialogresult = JOptionPane
                    .showConfirmDialog(
                            EPDShip.getMainFrame(),
                            "Do you wish to deactivate and hide your old route\nAnd activate the new route?",
                            "Route Activation", JOptionPane.YES_OPTION);
            if (dialogresult == JOptionPane.YES_OPTION) {
                shouldActive = true;

                routeManager.getRoutes()
                        .get(routeManager.getActiveRouteIndex())
                        .setVisible(false);
                routeManager.deactivateRoute();

            }

        } else {
            int dialogresult = JOptionPane.showConfirmDialog(
                    EPDShip.getMainFrame(),
                    "Do you wish to activate the new route?",
                    "Route Activation", JOptionPane.YES_OPTION);
            if (dialogresult == JOptionPane.YES_OPTION) {
                shouldActive = true;

            }

        }

        if (shouldActive) {
            int routeToActivate = routeManager.getRouteCount() - 1;
            routeManager.activateRoute(routeToActivate);
        }

    }

    private void sendRejectMsg(long transactionID, String message) {

        // System.out.println("Send reject msg? " + transactionID);

        // Send ack message
        // remove from voyage layer show original route
        voyageLayer.cancelRequest();

        // Show original?
        route.setVisible(true);

        routeManager
                .notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);

        if (monaLisaNegotiationData.containsKey(transactionID)) {

            StrategicRouteNegotiationData transactionData = monaLisaNegotiationData
                    .get(transactionID);

            if (enavServiceHandler.getStatus().getStatus() == ComponentStatus.Status.OK) {
                enavServiceHandler.sendMonaLisaAck(transactionData
                        .getRouteMessage().get(0).getMmsi(), transactionID,
                        transactionData.getRouteMessage().get(0).getMmsi(),
                        false, message);
            }

            monaLisaNegotiationData.get(transactionID).setStatus(
                    StrategicRouteStatus.REJECTED);
        }

        transaction = false;
    }

    public void cancelRouteRequest(long transactionID) {
        transaction = false;
        voyageLayer.cancelRequest();

        route.setVisible(true);
        routeManager
                .notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);

        sendRejectMsg(transactionID, "Request cancelled");
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof OwnShipHandler) {
            ownShipHandler = (OwnShipHandler) obj;
        } else if (obj instanceof EnavServiceHandler) {
            enavServiceHandler = (EnavServiceHandler) obj;
        } else if (obj instanceof VoyageLayer) {
            voyageLayer = (VoyageLayer) obj;
        } else if (obj instanceof RouteManager) {
            routeManager = (RouteManager) obj;
        }
    }

    public void modifiedRequest() {
        // System.out.println("Modified request!");
        routeModified = true;
        monaLisaSTCCDialog.changeModifiedAcceptBtn();
    }

    public boolean isRouteModified() {
        return routeModified;
    }

    public void sendReply(String message) {
        System.out.println("Sending reply " + routeModified);

        if (routeModified) {
            // Get new route
            sendModifiedReply(message);
        } else {
            // We agree and are done
            sendAgreeMsg(currentTransaction, message);
        }
        routeModified = false;
    }

    public void sendReject(String message) {
        sendRejectMsg(currentTransaction, message);
    }

    private void sendModifiedReply(String message) {

        Route route = voyageLayer.getModifiedSTCCRoute();

        voyageLayer.startRouteNegotiation(route.copy());

        // Hide the routeLayer one
        route.setVisible(false);

        // System.out.println(route);
        // System.out.println(route.getEtas().get(0));
        // System.out.println(route.getFullRouteData());

        // System.out.println("Sending modified reply");

        StrategicRouteRequestMessage routeMessage = new StrategicRouteService.StrategicRouteRequestMessage(
                currentTransaction, route.getFullRouteData(),
                monaLisaNegotiationData.get(currentTransaction)
                        .getRouteMessage().get(0).getMmsi(), message.trim());

        StrategicRouteNegotiationData entry;

        // Existing transaction already established
        if (monaLisaNegotiationData.containsKey(currentTransaction)) {

            // System.out.println("Existing transaction found");
            entry = monaLisaNegotiationData.get(currentTransaction);
        } else {
            // Create new entry for the transaction
            entry = new StrategicRouteNegotiationData(currentTransaction);
        }

        entry.addMessage(routeMessage);

        monaLisaNegotiationData.put(currentTransaction, entry);
        monaLisaNegotiationData.get(currentTransaction).setStatus(
                StrategicRouteStatus.NEGOTIATING);

        // Send it off
        enavServiceHandler.sendMonaLisaRouteRequest(routeMessage);

        // Display and initialize the GUI
        monaLisaSTCCDialog.initializeNew();
        // monaLisaSTCCDialog.setLocation(windowLocation);
        monaLisaSTCCDialog.setLocationRelativeTo(EPDShip.getMainFrame());
        monaLisaSTCCDialog.setVisible(true);
        monaLisaSTCCDialog.setRouteName(route, routeMessage.getId());

        // Clear layer and prevent editing
        voyageLayer.lockEditing();

    }

    /**
     * @return the monaLisaNegotiationData
     */
    public HashMap<Long, StrategicRouteNegotiationData> getMonaLisaNegotiationData() {
        return monaLisaNegotiationData;
    }

}

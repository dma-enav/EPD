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
package dk.dma.epd.ship.service;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.maritimecloud.net.ConnectionFuture;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.service.ServiceEndpoint;
import net.maritimecloud.net.service.invocation.InvocationCallback;
import net.maritimecloud.util.function.BiConsumer;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteAckService;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteAckService.StrategicRouteAckMsg;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestReply;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.route.strategic.RequestStrategicRouteDialog;
import dk.dma.epd.ship.layers.voyage.VoyageLayer;
import dk.dma.epd.ship.route.RouteManager;

/**
 * Handler class for the strategic route e-Navigation service
 */
public class StrategicRouteHandler extends EnavServiceHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(StrategicRouteHandler.class);

    private RequestStrategicRouteDialog strategicRouteSTCCDialog;

    private VoyageLayer voyageLayer;
    private RouteManager routeManager;

    private boolean transaction;
    private Route route;
    private boolean routeModified;

    private Map<Long, StrategicRouteNegotiationData> strategicRouteNegotiationData = new ConcurrentHashMap<>();
    private List<ServiceEndpoint<StrategicRouteRequestMessage, StrategicRouteRequestReply>> strategicRouteSTCCList = new ArrayList<>();
    private List<ServiceEndpoint<StrategicRouteAckMsg, Void>> strategicRouteRouteAckList = new ArrayList<>();

    /**
     * Constructor
     */
    public StrategicRouteHandler() {
        super(2);

        // Schedule a refresh of the STCC list approximately every minute
        scheduleWithFixedDelayWhenConnected(new Runnable() {
            @Override
            public void run() {
                fetchSTCCList();
            }
        }, 5, 57, TimeUnit.SECONDS);

        // Schedule a refresh of the strategic route acknowledge services approximately every minute
        scheduleWithFixedDelayWhenConnected(new Runnable() {
            @Override
            public void run() {
                fetchStrategicRouteAckList();
            }
        }, 15, 59, TimeUnit.SECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        try {
            getMaritimeCloudConnection().serviceRegister(StrategicRouteService.INIT,
                    new InvocationCallback<StrategicRouteRequestMessage, StrategicRouteRequestReply>() {
                        public void process(StrategicRouteRequestMessage message, Context<StrategicRouteRequestReply> context) {

                            LOG.info("Ship received a request for reopening a transaction!");
                            handleReNegotiation(message);
                        }
                    }).awaitRegistered(4, TimeUnit.SECONDS);

        } catch (Exception e) {
            LOG.error("Error hooking up services", e);
        }

        // Refresh the service lists
        fetchSTCCList();
        fetchStrategicRouteAckList();
    }

    /**
     * Fetch list of strategic route ack's
     */
    private void fetchStrategicRouteAckList() {
        try {
            strategicRouteRouteAckList = getMaritimeCloudConnection().serviceLocate(StrategicRouteAckService.INIT)
                    .nearest(Integer.MAX_VALUE).get();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * Fetches the list of Sea Traffic Control Centers
     */
    private void fetchSTCCList() {
        try {
            strategicRouteSTCCList = getMaritimeCloudConnection().serviceLocate(StrategicRouteService.INIT)
                    .nearest(Integer.MAX_VALUE).get();

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * Return if there are any strategic route STCC available
     */
    public boolean strategicRouteSTCCExists() {
        return strategicRouteSTCCList != null && strategicRouteSTCCList.size() > 0;
    }

    /**
     * Returns the list of strategic route STCC's
     * @return the list of strategic route STCC's
     */
    public List<ServiceEndpoint<StrategicRouteRequestMessage, StrategicRouteRequestReply>> getStrategicRouteSTCCList() {
        return strategicRouteSTCCList;
    }
    
    /**
     * Returns if a transaction is in progress
     * 
     * @return if a transaction is in progress
     */
    public boolean isTransaction() {
        return transaction;
    }

    /**
     * Sends the route to an STCC
     * 
     * @param route
     * @param windowLocation
     */
    public void sendStrategicRouteToSTCC(Route route, Point windowLocation) {

        this.route = route;

        if (strategicRouteSTCCDialog == null) {
            strategicRouteSTCCDialog = EPDShip.getInstance().getMainFrame().getStrategicRouteSTCCDialog();
        }

        // Is there already a transaction in progress
        if (transaction) {
            // Transaction is in progress, so just show the dialog
            strategicRouteSTCCDialog.setVisible(true);

        } else {

            // No transaction in progress

            // Start the transaction
            transaction = true;

            // Display the route
            voyageLayer.startRouteNegotiation(route.copy());

            // Hide the routeLayer one
            route.setVisible(false);
            routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);

            long ownMMSI = (EPDShip.getInstance().getOwnShipMmsi() == null) ? -1L : EPDShip.getInstance().getOwnShipMmsi();

            // Sending route
            long transactionID = sendStrategicRouteRequest(route, ownMMSI, "Route Approval Requested");

            // Initialize the GUI with the new transaction
            strategicRouteSTCCDialog.startTransaction(route, transactionID, true);
        }
    }

    /**
     * Sends the route to an STCC
     * 
     * @param route
     *            the route to send
     * @param ownMmsi
     *            the own MMSI
     * @param message
     *            an additional message
     * @return the transaction id
     */
    private long sendStrategicRouteRequest(Route route, long ownMmsi, String message) {

        long transactionID = System.currentTimeMillis();
        StrategicRouteNegotiationData entry = new StrategicRouteNegotiationData(transactionID);

        StrategicRouteRequestMessage routeMessage = new StrategicRouteRequestMessage(transactionID, route.getFullRouteData(),
                ownMmsi, message);

        entry.addMessage(routeMessage);

        strategicRouteNegotiationData.put(transactionID, entry);

        // Send it off
        sendStrategicRouteRequest(routeMessage);

        entry.setStatus(StrategicRouteStatus.PENDING);

        return transactionID;
    }

    /**
     * Sends a strategic route request
     * 
     * @param routeMessage
     *            the strategic route request
     */
    private void sendStrategicRouteRequest(StrategicRouteRequestMessage routeMessage) {

        ServiceEndpoint<StrategicRouteRequestMessage, StrategicRouteRequestReply> end = MaritimeCloudUtils
                .findSTCCService(strategicRouteSTCCList);

        // Each request has a unique ID, talk to Kasper?

        if (end != null) {
            ConnectionFuture<StrategicRouteRequestReply> f = end.invoke(routeMessage);
            f.handle(new BiConsumer<StrategicRouteRequestReply, Throwable>() {

                @Override
                public void accept(StrategicRouteRequestReply l, Throwable r) {
                    handleReply(l);
                }
            });

        } else {
            // notifyRouteExchangeListeners();
            LOG.error("Did not find strategic route STCC");
        }

    }

    /**
     * Handles a strategic route reply
     * 
     * @param reply
     *            the reply
     */
    private void handleReply(StrategicRouteRequestReply reply) {

        long transactionId = reply.getId();

        StrategicRouteNegotiationData entry = strategicRouteNegotiationData.get(transactionId);

        // Existing transaction already established
        if (entry == null) {
            // Create new entry for the transaction - if ship disconnected, it
            // can still recover - maybe?
            entry = new StrategicRouteNegotiationData(transactionId);
            strategicRouteNegotiationData.put(transactionId, entry);
        }

        if (entry.getStatus() != StrategicRouteStatus.REJECTED) {

            // Store the reply
            entry.addReply(reply);
            entry.setStatus(StrategicRouteStatus.NEGOTIATING);

            // System.out.println("Adding entry for " + currentTransaction);

            // How to handle the reply

            // 1 shore sends back accepted - ship needs to send ack
            // 2 shore sends back new route - ship renegotationes
            // 3 shore sends back rejected - ship sends ack

            // Let GUI handle front-end
            strategicRouteSTCCDialog.handleReply(reply);

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
            LOG.info("Nope cant handle this old thing");
        }
    }

    /**
     * Called when a strategic route is received from an STCC
     * 
     * @param message
     *            the strategic route
     */
    private void handleReNegotiation(StrategicRouteRequestMessage message) {

        // Shore wants to renegotiate it
        transaction = true;

        long transactionId = message.getId();

        StrategicRouteNegotiationData entry;
        // Existing transaction already established
        if (strategicRouteNegotiationData.containsKey(transactionId)) {
            entry = strategicRouteNegotiationData.get(transactionId);
        } else {
            // Create new entry for the transaction - if ship disconnected, it
            // can still recover - maybe?
            entry = new StrategicRouteNegotiationData(transactionId);
            strategicRouteNegotiationData.put(transactionId, entry);
        }

        entry.setStatus(StrategicRouteStatus.NEGOTIATING);

        StrategicRouteRequestReply newReply = new StrategicRouteRequestReply(message.getMessage(), message.getId(),
                message.getMmsi(), message.getSent().getTime(), StrategicRouteStatus.NEGOTIATING, message.getRoute());

        // Store the reply
        entry.addReply(newReply);
        strategicRouteNegotiationData.get(transactionId).setStatus(StrategicRouteStatus.NEGOTIATING);

        // Find the old one and set not accepted, possibly hide it?
        for (int i = 0; i < routeManager.getRoutes().size(); i++) {
            if (routeManager.getRoutes().get(i).getStrategicRouteId() == transactionId) {

                routeManager.getRoutes().get(i).setStccApproved(false);

                if (routeManager.getActiveRouteIndex() == i) {

                } else {
                    routeManager.getRoutes().get(i).setVisible(false);
                }

                try {
                    routeManager.getRoutes().get(i).setName(routeManager.getRoutes().get(i).getName().split(":")[1].trim());
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
        strategicRouteSTCCDialog.handleReply(newReply);

        StrategicRouteNegotiationData transactionData = strategicRouteNegotiationData.get(transactionId);

        Route lastRoute = new Route(transactionData.getLatestRoute());

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

    /**
     * Sends agree message for the given transaction
     * 
     * @param transactionID
     * @param message
     */
    public void sendAgreeMsg(long transactionID, String message) {

        System.out.println("Send agree msg? " + transactionID);
        strategicRouteSTCCDialog.setVisible(false);
        transaction = false;

        // Send ack message
        // remove from voyage layer show original route
        voyageLayer.routeAccepted();

        voyageLayer.getModifiedSTCCRoute().setName("STCC Approved: " + voyageLayer.getModifiedSTCCRoute().getName());

        if (strategicRouteNegotiationData.containsKey(transactionID)) {

            StrategicRouteNegotiationData transactionData = strategicRouteNegotiationData.get(transactionID);

            sendStrategicRouteAck(transactionData.getRouteReply().get(0).getMmsi(), transactionID, transactionData
                    .getRouteMessage().get(0).getMmsi(), true, message);

            strategicRouteNegotiationData.get(transactionID).setStatus(StrategicRouteStatus.AGREED);
        }

        Route route = voyageLayer.getModifiedSTCCRoute();
        route.setStccApproved(true);

        route.setStrategicRouteId(transactionID);

        // route.setVisible(true);
        routeManager.addRoute(route);
        routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_ADDED);

        boolean shouldActive = false;

        if (routeManager.getActiveRouteIndex() != -1) {
            int dialogresult = JOptionPane.showConfirmDialog(EPDShip.getInstance().getMainFrame(),
                    "Do you wish to deactivate and hide your old route\nAnd activate the new route?", "Route Activation",
                    JOptionPane.YES_OPTION);
            if (dialogresult == JOptionPane.YES_OPTION) {
                shouldActive = true;

                routeManager.getRoutes().get(routeManager.getActiveRouteIndex()).setVisible(false);
                routeManager.deactivateRoute();

            }

        } else {
            int dialogresult = JOptionPane.showConfirmDialog(EPDShip.getInstance().getMainFrame(),
                    "Do you wish to activate the new route?", "Route Activation", JOptionPane.YES_OPTION);
            if (dialogresult == JOptionPane.YES_OPTION) {
                shouldActive = true;

            }

        }

        if (shouldActive) {
            int routeToActivate = routeManager.getRouteCount() - 1;
            routeManager.activateRoute(routeToActivate);
        }

    }

    /**
     * Sends rejection message for the given transaction
     * 
     * @param transactionID
     * @param message
     */
    public void sendRejectMsg(long transactionID, String message) {

        // Send ack message
        // remove from voyage layer show original route
        voyageLayer.cancelRequest();

        // Show original?
        route.setVisible(true);

        routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);

        if (strategicRouteNegotiationData.containsKey(transactionID)) {

            StrategicRouteNegotiationData transactionData = strategicRouteNegotiationData.get(transactionID);

            if (getStatus().getStatus() == ComponentStatus.Status.OK) {
                sendStrategicRouteAck(transactionData.getRouteMessage().get(0).getMmsi(), transactionID, transactionData
                        .getRouteMessage().get(0).getMmsi(), false, message);
            }

            strategicRouteNegotiationData.get(transactionID).setStatus(StrategicRouteStatus.REJECTED);
        }

        transaction = false;
    }

    /**
     * Cancels the given route request
     * 
     * @param transactionID
     */
    public void cancelRouteRequest(long transactionID) {
        transaction = false;
        voyageLayer.cancelRequest();

        route.setVisible(true);
        routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);

        sendRejectMsg(transactionID, "Request cancelled");
    }

    /**
     * Sends a strategic route Acknowledge message
     * 
     * @param addressMMSI
     *            the MMSI of the route
     * @param id
     *            the route id
     * @param ownMMSI
     *            own MMSI
     * @param ack
     *            acknowledged or rejected
     * @param message
     *            an additional message
     */
    private void sendStrategicRouteAck(long addressMMSI, long id, long ownMMSI, boolean ack, String message) {

        fetchStrategicRouteAckList();

        ServiceEndpoint<StrategicRouteAckMsg, Void> end = MaritimeCloudUtils.findSTCCService(strategicRouteRouteAckList);

        StrategicRouteAckMsg msg = new StrategicRouteAckMsg(ack, id, ownMMSI, message);

        if (end != null) {

            // ConnectionFuture<Void> f =
            end.invoke(msg);
        } else {
            System.out.println("Failed to send ack " + strategicRouteRouteAckList.size());
        }
    }

    /**
     * Called when the route is modified
     */
    public void modifiedRequest() {
        routeModified = true;
        strategicRouteSTCCDialog.changeModifiedAcceptBtn();
    }

    /**
     * Returns if the route is modified
     * 
     * @return if the route is modified
     */
    public boolean isRouteModified() {
        return routeModified;
    }

    /**
     * Sends a reply to the STCC
     * 
     * @param message
     *            the message to send along
     */
    public void sendReply(long transactionId, String message) {
        LOG.info("Sending reply " + routeModified);

        if (routeModified) {
            // Get new route
            sendModifiedReply(transactionId, message);
        } else {
            // We agree and are done
            sendAgreeMsg(transactionId, message);
        }
        routeModified = false;
    }

    /**
     * Sends a modified reply to the STCC
     * 
     * @param transactionID
     *            the id of the transaction
     * @param message
     *            the message to send along
     */
    private void sendModifiedReply(long transactionID, String message) {

        Route route = voyageLayer.getModifiedSTCCRoute();

        voyageLayer.startRouteNegotiation(route.copy());

        // Hide the routeLayer one
        route.setVisible(false);

        StrategicRouteRequestMessage routeMessage = new StrategicRouteRequestMessage(transactionID, route.getFullRouteData(),
                strategicRouteNegotiationData.get(transactionID).getRouteMessage().get(0).getMmsi(), message.trim());

        StrategicRouteNegotiationData entry;

        // Existing transaction already established
        if (strategicRouteNegotiationData.containsKey(transactionID)) {

            // System.out.println("Existing transaction found");
            entry = strategicRouteNegotiationData.get(transactionID);
        } else {
            // Create new entry for the transaction
            entry = new StrategicRouteNegotiationData(transactionID);
        }

        entry.addMessage(routeMessage);

        strategicRouteNegotiationData.put(transactionID, entry);
        strategicRouteNegotiationData.get(transactionID).setStatus(StrategicRouteStatus.NEGOTIATING);

        // Send it off
        sendStrategicRouteRequest(routeMessage);

        // Initialize the GUI with the new transaction
        strategicRouteSTCCDialog.startTransaction(route, routeMessage.getId(), true);

        // Clear layer and prevent editing
        voyageLayer.lockEditing();

    }

    /**
     * Returns the StrategicRouteNegotiationData for the given transaction id
     * 
     * @param transactionId
     *            the transaction id
     * @return the strategicRouteNegotiationData
     */
    public StrategicRouteNegotiationData getStrategicRouteNegotiationData(Long transactionId) {
        return strategicRouteNegotiationData.get(transactionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (obj instanceof VoyageLayer) {
            voyageLayer = (VoyageLayer) obj;
        } else if (obj instanceof RouteManager) {
            routeManager = (RouteManager) obj;
        }
    }
}

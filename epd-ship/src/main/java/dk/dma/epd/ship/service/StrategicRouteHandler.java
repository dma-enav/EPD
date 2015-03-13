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
package dk.dma.epd.ship.service;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.StrategicRouteHandlerCommon;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.layers.voyage.VoyageLayer;
import dk.dma.epd.ship.route.RouteManager;
import dma.route.StrategicRouteMessage;
import dma.route.StrategicRouteStatus;
import net.maritimecloud.core.id.MaritimeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Handler class for the strategic route e-Navigation service
 * <p>
 * Future improvements: The strategic route handler (along with the voyage layer) manages
 * a "current transaction". This is an unsound approach, and cannot cater with a situation
 * where the ship has multiple on-going strategic route requests, possibly with multiple STCC's.
 * Consider a "Handle request" option similar to EPDShore, or another way of switching between 
 * an active transaction.
 */
public class StrategicRouteHandler extends StrategicRouteHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(StrategicRouteHandler.class);

    private VoyageLayer voyageLayer;
    private RouteManager routeManager;

    private Long transactionId;
    private Route route;
    private boolean routeModified;


    /**
     * Constructor
     */
    public StrategicRouteHandler() {
        super();

        // Schedule a refresh of the STCC list approximately every minute
        scheduleWithFixedDelayWhenConnected(this::fetchStrategicRouteServices, 5, 57, TimeUnit.SECONDS);
    }


    /**
     * Returns if a transaction is in progress
     * 
     * @return if a transaction is in progress
     */
    public boolean isTransaction() {
        return transactionId != null;
    }
    
    /**
     * Returns the current transaction id or null if undefined
     * @return the current transaction id
     */
    public Long getCurrentTransactionId() {
        return transactionId;
    }

    /**
     * Sends the route to an STCC
     */
    public void sendStrategicRouteToSTCC(long stccMmsi, Route route, String message) {

        this.route = route;

        // Display the route
        voyageLayer.startRouteNegotiation(route.copy());

        // Hide the routeLayer one
        route.setVisible(false);
        routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);

        // Sending route and start the transaction
        transactionId = sendStrategicRouteRequest(route, stccMmsi, message);
        notifyStrategicRouteListeners();
    }

    /**
     * Sends the route to an STCC
     * 
     * @param route the route to send
     * @param stccMmsi the STCC MMSI
     * @param message an additional message
     * @return the transaction id
     */
    private long sendStrategicRouteRequest(Route route, long stccMmsi, String message) {
        
        long transactionID = System.currentTimeMillis();
        StrategicRouteNegotiationData routeData = new StrategicRouteNegotiationData(transactionID, stccMmsi);
        strategicRouteNegotiationData.put(transactionID, routeData);

        StrategicRouteMessage routeMessage = new StrategicRouteMessage()
                .setId(transactionID)
                .setRoute(route.toMaritimeCloudRoute())
                .setTextMessage(message)
                .setStatus(StrategicRouteStatus.PENDING);


        // Send it off
        sendStrategicRouteMessage(routeData, routeMessage, stccMmsi);

        routeData.setHandled(false);

        return transactionID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleStrategicRouteMessage(StrategicRouteMessage suggestion, MaritimeId sender, Date senderTime) {

        // Set the current transaction
        transactionId = suggestion.getId();

        StrategicRouteNegotiationData routeData;
        // Existing transaction already established
        if (strategicRouteNegotiationData.containsKey(transactionId)) {
            routeData = strategicRouteNegotiationData.get(transactionId);
        } else {
            // Create new entry for the transaction - if ship disconnected, it
            // can still recover - maybe?
            routeData = new StrategicRouteNegotiationData(transactionId, MaritimeCloudUtils.toMmsi(sender));
            strategicRouteNegotiationData.put(transactionId, routeData);
        }

        // If the strategic route request has been cancelled, do nothing
        if (routeData.getStatus() == StrategicRouteStatus.CANCELED) {
            LOG.warn("Ignoring call from STCC, since request has been cancelled");
            return;
        }
        
        routeData.addMessage(suggestion, senderTime, null, true);

        // Find the old one and set not accepted, possibly hide it?
        routeManager.getRoutes().stream()
                .filter(route -> route.getStrategicRouteId() == transactionId)
                .forEach(route -> {

            route.setStccApproved(false);

            if (routeManager.getActiveRoute() != route) {
                route.setVisible(false);
            }

            try {
                route.setName(route.getName().split(":")[1].trim());
            } catch (Exception e) {
                LOG.debug("Failed assigning route name");
            }
        });

        // How to handle the reply

        // 1 shore sends back accepted - ship needs to send ack
        // 2 shore sends back new route - ship renegotationes
        // 3 shore sends back rejected - ship sends ack

        Route lastAcceptedRoute = routeData.getLatestAcceptedRoute();
        if (lastAcceptedRoute != null) {
            // Let layer handle itself
            voyageLayer.handleReNegotiation(suggestion, lastAcceptedRoute);
        } else {
            voyageLayer.handleReply(suggestion);
        }
         
        routeData.setHandled(false);
        notifyStrategicRouteListeners();

    }

    /**
     * @return the stccMmsi
     */
    public synchronized Long getStccMmsi() {
        return (transactionId != null && strategicRouteNegotiationData.containsKey(transactionId))
                ? strategicRouteNegotiationData.get(transactionId).getMmsi()
                : null;
    }

    /**
     * Sends agree message for the given transaction
     * 
     * @param transactionID the transaction id
     * @param message the message
     */
    public void sendAgreeMsg(long transactionID, String message) {

        LOG.info("Send agree msg for transaction  " + transactionID);
        
        transactionId = null;

        // Send ack message
        // remove from voyage layer show original route
        voyageLayer.routeAccepted();

        voyageLayer.getModifiedSTCCRoute().setName("STCC Approved: " + voyageLayer.getModifiedSTCCRoute().getName());

        if (strategicRouteNegotiationData.containsKey(transactionID)) {

            StrategicRouteNegotiationData routeData = strategicRouteNegotiationData.get(transactionID);
            sendStrategicRouteAck(routeData, message, StrategicRouteStatus.AGREED);
            routeData.setHandled(true);
            notifyStrategicRouteListeners();
            
        }

        // Update route
        Route route = voyageLayer.getModifiedSTCCRoute();
        route.setStccApproved(true);
        route.setStrategicRouteId(transactionID);

        // and add the route to the route manager
        routeManager.addRoute(route);
        routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_ADDED);

        // Ask if we should activate the route
        boolean shouldActivate = false;
        if (routeManager.getActiveRouteIndex() != -1) {
            int dialogresult = JOptionPane.showConfirmDialog(EPDShip.getInstance().getMainFrame(),
                    "Do you wish to deactivate and hide your old route\nAnd activate the new route?", "Route Activation",
                    JOptionPane.YES_NO_OPTION);
            if (dialogresult == JOptionPane.YES_OPTION) {
                shouldActivate = true;
                routeManager.getRoutes().get(routeManager.getActiveRouteIndex()).setVisible(false);
                routeManager.deactivateRoute();
            }

        } else {
            int dialogresult = JOptionPane.showConfirmDialog(EPDShip.getInstance().getMainFrame(),
                    "Do you wish to activate the new route?", "Route Activation", JOptionPane.YES_NO_OPTION);
            if (dialogresult == JOptionPane.YES_OPTION) {
                shouldActivate = true;
            }
        }

        if (shouldActivate) {
            int routeToActivate = routeManager.getRouteCount() - 1;
            routeManager.activateRoute(routeToActivate);
        }

    }

    /**
     * Sends rejection message for the given transaction
     * 
     * @param transactionID the transaction id
     * @param message the message
     * @param status the reject status
     */
    public void sendRejectMsg(long transactionID, String message, StrategicRouteStatus status) {

        // Send ack message
        // remove from voyage layer show original route
        voyageLayer.cancelRequest();

        // Show original?
        if (route != null) {
            route.setVisible(true);
        }
        
        routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);

        if (strategicRouteNegotiationData.containsKey(transactionID)) {

            StrategicRouteNegotiationData routeData = strategicRouteNegotiationData.get(transactionID);

            sendStrategicRouteAck(routeData, message, status);
            transactionId = null;
            routeData.setHandled(true);
            notifyStrategicRouteListeners();
        }

        
    }

    /**
     * Sends a strategic route Acknowledge message
     * 
     * @param routeData the transaction data
     * @param message an additional message
     * @param status the acknowledge status
     */
    private void sendStrategicRouteAck(StrategicRouteNegotiationData routeData, String message, StrategicRouteStatus status) {
        
        Route route = (status == StrategicRouteStatus.AGREED) 
                    ? routeData.getLatestRoute()
                    : routeData.getLatestAcceptedOrOriginalRoute();

        StrategicRouteMessage routeMessage = new StrategicRouteMessage()
                .setId(routeData.getId())
                .setRoute(route.toMaritimeCloudRoute())
                .setTextMessage(message)
                .setStatus(status);

        sendStrategicRouteMessage(routeData, routeMessage, routeData.getMmsi());
        transactionId = null;
    }

    /**
     * Called when the route is modified
     */
    public void modifiedRequest() {
        routeModified = true;
        // Note to self: It would be better to adapt a change-listener interface...
        EPDShip.getInstance().getNotificationCenter()
            .getStrategicRoutePanel().changeToModifiedAcceptBtn();
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

        StrategicRouteNegotiationData routeData = strategicRouteNegotiationData.get(transactionID);

        StrategicRouteMessage routeMessage = new StrategicRouteMessage()
                .setId(transactionID)
                .setRoute(route.toMaritimeCloudRoute())
                .setTextMessage(message.trim())
                .setStatus(StrategicRouteStatus.NEGOTIATING);


        // Send it off
        sendStrategicRouteMessage(routeData, routeMessage, routeData.getMmsi());
        
        routeData.setHandled(false);
        notifyStrategicRouteListeners();

        // Clear layer and prevent editing
        voyageLayer.lockEditing();

    }
    
    /**
     * Returns the voyage layer
     * @return the voyage layer
     */
    public VoyageLayer getVoyageLayer() {
        return voyageLayer;
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

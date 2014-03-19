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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.service.ServiceEndpoint;
import net.maritimecloud.net.service.invocation.InvocationCallback;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteReply;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.StrategicRouteHandlerCommon;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.layers.voyage.VoyageLayer;
import dk.dma.epd.ship.route.RouteManager;

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

    private List<ServiceEndpoint<StrategicRouteMessage, StrategicRouteReply>> strategicRouteSTCCList = new ArrayList<>();

    /**
     * Constructor
     */
    public StrategicRouteHandler() {
        super();

        // Schedule a refresh of the STCC list approximately every minute
        scheduleWithFixedDelayWhenConnected(new Runnable() {
            @Override
            public void run() {
                fetchSTCCList();
            }
        }, 5, 57, TimeUnit.SECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        try {
            getMaritimeCloudConnection().serviceRegister(StrategicRouteService.INIT,
                    new InvocationCallback<StrategicRouteMessage, StrategicRouteReply>() {
                        public void process(StrategicRouteMessage message, Context<StrategicRouteReply> context) {

                            LOG.info("Ship received a request for reopening a transaction!");
                            handleStrategicRouteMessageFromStcc(message, context.getCaller());
                        }
                    }).awaitRegistered(4, TimeUnit.SECONDS);

        } catch (Exception e) {
            LOG.error("Error hooking up services", e);
        }

        // Refresh the service lists
        fetchSTCCList();
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
    public List<ServiceEndpoint<StrategicRouteMessage, StrategicRouteReply>> getStrategicRouteSTCCList() {
        return strategicRouteSTCCList;
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
     * 
     * @param route
     * @param windowLocation
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
    }

    /**
     * Sends the route to an STCC
     * 
     * @param route
     *            the route to send
     * @param stccMmsi
     *            the STCC MMSI
     * @param message
     *            an additional message
     * @return the transaction id
     */
    private long sendStrategicRouteRequest(Route route, long stccMmsi, String message) {

        long transactionID = System.currentTimeMillis();
        StrategicRouteNegotiationData routeData = new StrategicRouteNegotiationData(transactionID, stccMmsi);

        StrategicRouteMessage routeMessage = new StrategicRouteMessage(false, transactionID, route.getFullRouteData(),
                message, StrategicRouteStatus.PENDING);

        routeData.addMessage(routeMessage);

        strategicRouteNegotiationData.put(transactionID, routeData);

        // Send it off
        sendStrategicRouteRequest(routeMessage, stccMmsi);

        routeData.setHandled(false);
        notifyStrategicRouteListeners();
        
        return transactionID;
    }

    /**
     * Sends a strategic route request
     * 
     * @param routeMessage
     *            the strategic route request
     */
    private void sendStrategicRouteRequest(StrategicRouteMessage routeMessage, long stccMmsi) {

        ServiceEndpoint<StrategicRouteMessage, StrategicRouteReply> end = MaritimeCloudUtils
                .findServiceWithMmsi(strategicRouteSTCCList, (int)stccMmsi);

        if (end != null) {
            end.invoke(routeMessage);

        } else {
            // notifyRouteExchangeListeners();
            LOG.error("Did not find strategic route STCC");
        }

    }

    /**
     * Handles an incoming strategic route message received from an STCC
     * 
     * @param routeMessage the strategic route
     * @param caller the Maritime Cloud called
     */
    private void handleStrategicRouteMessageFromStcc(StrategicRouteMessage routeMessage, MaritimeId caller) {

        // Set the current transaction
        transactionId = routeMessage.getId();

        StrategicRouteNegotiationData routeData;
        // Existing transaction already established
        if (strategicRouteNegotiationData.containsKey(transactionId)) {
            routeData = strategicRouteNegotiationData.get(transactionId);
        } else {
            // Create new entry for the transaction - if ship disconnected, it
            // can still recover - maybe?
            routeData = new StrategicRouteNegotiationData(transactionId, MaritimeCloudUtils.toMmsi(caller));
            strategicRouteNegotiationData.put(transactionId, routeData);
        }

        // If the strategic route request has been cancelled, do nothing
        if (routeData.getStatus() == StrategicRouteStatus.CANCELED) {
            LOG.warn("Ignoring call from STCC, since request has been cancelled");
            return;
        }
        
        routeData.addMessage(routeMessage);

        // Find the old one and set not accepted, possibly hide it?
        for (Route route : routeManager.getRoutes()) {
            if (route.getStrategicRouteId() == transactionId) {
                route.setStccApproved(false);
              
                if (routeManager.getActiveRoute() != route) {
                    route.setVisible(false);
                }
                
                try {
                    route.setName(route.getName().split(":")[1].trim());
                } catch (Exception e) {
                }
            }
        }

        // How to handle the reply

        // 1 shore sends back accepted - ship needs to send ack
        // 2 shore sends back new route - ship renegotationes
        // 3 shore sends back rejected - ship sends ack

        Route lastAcceptedRoute = routeData.getLatestAcceptedRoute();
        if (lastAcceptedRoute != null) {
            // Let layer handle itself
            voyageLayer.handleReNegotiation(routeMessage, lastAcceptedRoute);
        } else {
            voyageLayer.handleReply(routeMessage);            
        }
         
        routeData.setHandled(false);
        notifyStrategicRouteListeners();

    }

    /**
     * Sends agree message for the given transaction
     * 
     * @param transactionID
     * @param message
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
                    JOptionPane.YES_OPTION);
            if (dialogresult == JOptionPane.YES_OPTION) {
                shouldActivate = true;
                routeManager.getRoutes().get(routeManager.getActiveRouteIndex()).setVisible(false);
                routeManager.deactivateRoute();
            }

        } else {
            int dialogresult = JOptionPane.showConfirmDialog(EPDShip.getInstance().getMainFrame(),
                    "Do you wish to activate the new route?", "Route Activation", JOptionPane.YES_OPTION);
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
        route.setVisible(true);

        routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);

        if (strategicRouteNegotiationData.containsKey(transactionID)) {

            StrategicRouteNegotiationData routeData = strategicRouteNegotiationData.get(transactionID);

            sendStrategicRouteAck(routeData, message, status);
            
            routeData.setHandled(true);
            notifyStrategicRouteListeners();
        }

        transactionId = null;
    }

    /**
     * Sends a strategic route Acknowledge message
     * 
     * @param routeData the transaction data
     * @param message an additional message
     * @param status the acknowledge status
     */
    private void sendStrategicRouteAck(StrategicRouteNegotiationData routeData, String message, StrategicRouteStatus status) {
            
        StrategicRouteMessage routeMessage = new StrategicRouteMessage(false, routeData.getId(), 
                routeData.getLatestAcceptedOrOriginalRoute().getFullRouteData(), message, status);

        routeData.addMessage(routeMessage);
        
        sendStrategicRouteRequest(routeMessage, routeData.getMmsi());
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

        StrategicRouteMessage routeMessage = new StrategicRouteMessage(false, transactionID, 
                route.getFullRouteData(), message.trim(), StrategicRouteStatus.NEGOTIATING);

        StrategicRouteNegotiationData routeData = strategicRouteNegotiationData.get(transactionID);
        routeData.addMessage(routeMessage);

        // Send it off
        sendStrategicRouteRequest(routeMessage, routeData.getMmsi());
        
        routeData.setHandled(true);
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

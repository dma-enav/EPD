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
package dk.dma.epd.shore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.service.ServiceEndpoint;
import net.maritimecloud.net.service.invocation.InvocationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteReply;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.StrategicRouteHandlerCommon;
import dk.dma.epd.shore.voyage.Voyage;
import dk.dma.epd.shore.voyage.VoyageManager;

/**
 * Handler class for the strategic route e-Navigation service
 */
public class StrategicRouteHandler extends StrategicRouteHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(StrategicRouteHandler.class);
    
    private VoyageManager voyageManager;

    private List<ServiceEndpoint<StrategicRouteMessage, StrategicRouteReply>> strategicRouteShipList = new ArrayList<>();

    /**
     * Constructor
     */
    public StrategicRouteHandler() {
        super();
        
        // Schedule a refresh of the strategic route ship list approximately every minute
        scheduleWithFixedDelayWhenConnected(new Runnable() {
            @Override public void run() {
                fetchStrategicRouteShipList();
            }}, 13, 61, TimeUnit.SECONDS);

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        try {
            registerStrategicRouteService();
        } catch (Exception e) {
            LOG.error("Error hooking up services", e);
        }
        
        // Refresh the service list
        fetchStrategicRouteShipList();
    }

    /**
     * Register a strategic route service
     */
    private void registerStrategicRouteService() throws InterruptedException {

        getMaritimeCloudConnection()
                .serviceRegister(
                        StrategicRouteService.INIT,
                        new InvocationCallback<StrategicRouteMessage, StrategicRouteReply>() {
                            public void process(StrategicRouteMessage message,
                                    Context<StrategicRouteReply> context) {
                                
                                // The cloud status is transient, so this ought to be unnecessary
                                message.setCloudMessageStatus(null);
                                
                                LOG.info("Shore received a strategic route request");
                                handleStrategicRouteRequest(message, context.getCaller());

                                // Acknowledge that the message has been handled 
                                context.complete(new StrategicRouteReply(message.getId()));
                            }
                        }).awaitRegistered(4, TimeUnit.SECONDS);
    }

    /**
     * Sends a strategic route request to the given ship
     * 
     * @param mmsiDestination the destination mmsi
     * @param routeMessage the strategic route to send
     */
    private void sendStrategicRouteRequest(long mmsiDestination, StrategicRouteMessage routeMessage) {

        routeMessage.setCloudMessageStatus(CloudMessageStatus.NOT_SENT);
        if (sendMaritimeCloudMessage(strategicRouteShipList, new MmsiId((int)mmsiDestination), routeMessage, this)) {
            routeMessage.updateCloudMessageStatus(CloudMessageStatus.SENT);
        }
    }

    /**
     * Fetches the list of ships with a strategic route service
     */
    private void fetchStrategicRouteShipList() {
        try {
            strategicRouteShipList = getMaritimeCloudConnection().serviceLocate(StrategicRouteService.INIT).nearest(Integer.MAX_VALUE).get();

        } catch (Exception e) {
            LOG.error(e.getMessage());

        }
    }

    /**
     * Checks if the ship with the given mmsi has a strategic route service
     * @param mmsi the mmsi of the ship
     * @return if the given ship has a strategic route service
     */
    public boolean shipAvailableForStrategicRouteTransaction(long mmsi) {
        if (MaritimeCloudUtils.findServiceWithMmsi(strategicRouteShipList, (int)mmsi) == null) {
            fetchStrategicRouteShipList();
        }
        
        return MaritimeCloudUtils.findServiceWithMmsi(strategicRouteShipList, (int)mmsi) != null;
    }

    
    
    /**
     * Sends a strategic route reply
     * 
     * @param id the route id
     * @param text a text message
     * @param currentTimeMillis the send date
     * @param replyStatus the reply status
     * @param route the route
     * @param renegotiate re-negotiate or not
     */
    
    public void sendStrategicRouteReply(long id, String text,
            long currentTimeMillis, StrategicRouteStatus replyStatus, Route route, boolean renegotiate) {
        
        StrategicRouteNegotiationData routeData = strategicRouteNegotiationData.get(id);
        
        StrategicRouteMessage routeMessage = new StrategicRouteMessage(true, id, route, text, replyStatus);

        if (renegotiate || routeData.getStatus() == StrategicRouteStatus.NEGOTIATING
                || routeData.getStatus() == StrategicRouteStatus.PENDING) {

            if (renegotiate) {
                LOG.info("Restart negotiation");
                dk.dma.epd.common.prototype.model.route.Route naRoute = new dk.dma.epd.common.prototype.model.route.Route(route);
                naRoute.setName("N/A");
                routeMessage.setRoute(naRoute.getFullRouteData());
            }
            
            routeData.addMessage(routeMessage);
            routeData.setStatus(StrategicRouteStatus.NEGOTIATING);
            routeData.setHandled(true);

            sendStrategicRouteRequest(routeData.getMmsi(), routeMessage);
            notifyStrategicRouteListeners();
            
                
        } else {
            routeData.addMessage(routeMessage);
            LOG.error("Cannot send message, transaction concluded");                
        }
    }
    

    /**
     * Sends a strategic route message
     * @param message the reply to send
     * @param caller the caller
     */
    private void handleStrategicRouteRequest(StrategicRouteMessage message, MaritimeId caller) {

        long transactionID = message.getId();

        StrategicRouteNegotiationData routeData;

        if (strategicRouteNegotiationData.containsKey(transactionID)) {
            routeData = strategicRouteNegotiationData.get(transactionID);

            // Not handled anymore, new pending message
            routeData.addMessage(message);
            if (message.getStatus() == StrategicRouteStatus.AGREED || 
                    message.getStatus() == StrategicRouteStatus.REJECTED ||
                    message.getStatus() == StrategicRouteStatus.CANCELED) {
                handleAcknowledgeMsg(routeData);
            } else if (message.getStatus() == StrategicRouteStatus.NEGOTIATING) {
                routeData.setHandled(false);
                notifyStrategicRouteListeners();
            }
            
        } else if (message.getStatus() == StrategicRouteStatus.PENDING) {
            routeData = new StrategicRouteNegotiationData(message.getId(), MaritimeCloudUtils.toMmsi(caller));
            strategicRouteNegotiationData.put(message.getId(), routeData);
            routeData.addMessage(message);
            routeData.setHandled(false);
            notifyStrategicRouteListeners();
        }
    }

    /**
     * Called when an acknowledge message is received
     * @param routeData the route data
     */
    private void handleAcknowledgeMsg(StrategicRouteNegotiationData routeData) {

        if (routeData.getStatus() ==  StrategicRouteStatus.AGREED) {

            LOG.info("Transaction with id" + routeData.getId()
                    + " has been completed!");

            // Ship has ack it, set status to completed and add the finished
            // voyage to the voyageManager

            Voyage voyage = new Voyage(routeData.getMmsi(), routeData.getLatestAcceptedOrOriginalRoute(), routeData.getId());
            voyageManager.addVoyage(voyage);
        }

        routeData.setHandled(true);
        notifyStrategicRouteListeners();
    }

    /**
     * Returns the current list of unhandled transactions
     * @return the current list of unhandled transactions
     */
    public synchronized List<Long> getUnhandledTransactions() {
        List<Long> unhandledTransactions = new ArrayList<>();

        for (StrategicRouteNegotiationData value : strategicRouteNegotiationData.values()) {
            if (!value.isHandled()) {
                unhandledTransactions.add(value.getId());
            }
        }
        return unhandledTransactions;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof VoyageManager) {
            this.voyageManager = (VoyageManager) obj;
        }
    }
}

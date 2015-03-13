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

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.StrategicRouteHandlerCommon;
import dk.dma.epd.shore.voyage.Voyage;
import dk.dma.epd.shore.voyage.VoyageManager;
import dma.route.StrategicRouteMessage;
import dma.route.StrategicRouteStatus;
import net.maritimecloud.core.id.MaritimeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Handler class for the strategic route e-Navigation service
 */
public class StrategicRouteHandler extends StrategicRouteHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(StrategicRouteHandler.class);
    
    private VoyageManager voyageManager;

    /**
     * Constructor
     */
    public StrategicRouteHandler() {
        super();
        
        // Schedule a refresh of the strategic route ship list approximately every minute
        scheduleWithFixedDelayWhenConnected(this::fetchStrategicRouteServices, 13, 61, TimeUnit.SECONDS);
    }
    
    /**
     * Checks if the ship with the given mmsi has a strategic route service
     * @param mmsi the mmsi of the ship
     * @return if the given ship has a strategic route service
     */
    public boolean shipAvailableForStrategicRouteTransaction(long mmsi) {
        if (MaritimeCloudUtils.findServiceWithMmsi(strategicRouteServiceList, (int)mmsi) == null) {
            fetchStrategicRouteServices();
        }

        return MaritimeCloudUtils.findServiceWithMmsi(strategicRouteServiceList, (int)mmsi) != null;
    }

    
    
    /**
     * Sends a strategic route reply
     * 
     * @param id the route id
     * @param text a text message
     * @param replyStatus the reply status
     * @param route the route
     * @param renegotiate re-negotiate or not
     */
    
    public void sendStrategicRouteReply(long id, String text, StrategicRouteStatus replyStatus, Route route, boolean renegotiate) {
        
        StrategicRouteNegotiationData routeData = strategicRouteNegotiationData.get(id);

        StrategicRouteMessage routeMessage = new StrategicRouteMessage()
                .setId(id)
                .setRoute(route.toMaritimeCloudRoute())
                .setTextMessage(text)
                .setStatus(replyStatus);

        if (renegotiate || routeData.getStatus() == StrategicRouteStatus.NEGOTIATING
                || routeData.getStatus() == StrategicRouteStatus.PENDING) {

            if (renegotiate) {
                LOG.info("Restart negotiation");
                Route naRoute = new Route(route);
                naRoute.setName("N/A");
                routeMessage.setRoute(naRoute.toMaritimeCloudRoute());
            }
            
            routeData.setStatus(StrategicRouteStatus.NEGOTIATING);
            routeData.setHandled(true);

            // Send it off
            sendStrategicRouteMessage(routeData, routeMessage, routeData.getMmsi());

            notifyStrategicRouteListeners();
            
                
        } else {
            routeData.addMessage(routeMessage, new Date(), CloudMessageStatus.NOT_SENT, true);
            LOG.error("Cannot send message, transaction concluded");                
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleStrategicRouteMessage(StrategicRouteMessage message, MaritimeId sender, Date senderTime) {

        long transactionID = message.getId();

        StrategicRouteNegotiationData routeData;

        if (strategicRouteNegotiationData.containsKey(transactionID)) {
            routeData = strategicRouteNegotiationData.get(transactionID);

            // Not handled anymore, new pending message
            routeData.addMessage(message, senderTime, null, false);
            if (message.getStatus() == StrategicRouteStatus.AGREED || 
                    message.getStatus() == StrategicRouteStatus.REJECTED ||
                    message.getStatus() == StrategicRouteStatus.CANCELED) {
                handleAcknowledgeMsg(routeData);
            } else if (message.getStatus() == StrategicRouteStatus.NEGOTIATING) {
                routeData.setHandled(false);
                notifyStrategicRouteListeners();
            }
            
        } else if (message.getStatus() == StrategicRouteStatus.PENDING) {
            routeData = new StrategicRouteNegotiationData(message.getId(), MaritimeCloudUtils.toMmsi(sender));
            strategicRouteNegotiationData.put(message.getId(), routeData);
            routeData.addMessage(message, senderTime, null, false);
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
        return strategicRouteNegotiationData.values().stream()
                .filter(value -> !value.isHandled())
                .map(StrategicRouteNegotiationData::getId)
                .collect(Collectors.toList());
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

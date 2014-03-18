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
package dk.dma.epd.shore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.maritimecloud.core.id.MaritimeId;
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
import dk.dma.epd.shore.EPDShore;
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
     * @return the ownMMSI
     */
    public long getOwnMMSI() {
        String shoreID = (String) EPDShore.getInstance().getSettings().getEnavSettings().getShoreId().subSequence(0, 9);
        return Long.parseLong(shoreID);
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

                                handleStrategicRouteRequest(message, context.getCaller());
                            }
                        }).awaitRegistered(4, TimeUnit.SECONDS);
    }

    /**
     * Sends a strategic route request to the given ship
     * 
     * @param mmsiDestination the destination mmsi
     * @param routeMessage the strategic route to send
     */
    private void sendStrategicRenegotiateRequest(long mmsiDestination, StrategicRouteMessage routeMessage) {

        ServiceEndpoint<StrategicRouteMessage, StrategicRouteReply> end 
            = MaritimeCloudUtils.findServiceWithMmsi(strategicRouteShipList, (int)mmsiDestination);

        if (end != null) {
            end.invoke(routeMessage);

        } else {
            LOG.error("Failed to find ship with id " + mmsiDestination);
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
        
        StrategicRouteNegotiationData entry = strategicRouteNegotiationData.get(id);
        
        StrategicRouteMessage routeMessage = new StrategicRouteMessage(true, id, route, text, replyStatus);

        if (renegotiate || entry.getStatus() == StrategicRouteStatus.NEGOTIATING
                || entry.getStatus() == StrategicRouteStatus.PENDING) {

            if (renegotiate) {
                LOG.info("Restart negotiation");
                dk.dma.epd.common.prototype.model.route.Route naRoute = new dk.dma.epd.common.prototype.model.route.Route(route);
                naRoute.setName("N/A");
                routeMessage.setRoute(naRoute.getFullRouteData());
            }
            
            entry.addMessage(routeMessage);
            entry.setStatus(StrategicRouteStatus.NEGOTIATING);
            entry.setHandled(true);

            sendStrategicRenegotiateRequest(entry.getMmsi(), routeMessage);
            notifyStrategicRouteListeners();
            
                
        } else {
            entry.addMessage(routeMessage);
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

        StrategicRouteNegotiationData entry;

        if (strategicRouteNegotiationData.containsKey(transactionID)) {
            entry = strategicRouteNegotiationData.get(transactionID);

            // Not handled anymore, new pending message
            entry.addMessage(message);
            if (message.getStatus() == StrategicRouteStatus.AGREED || 
                    message.getStatus() == StrategicRouteStatus.REJECTED ||
                    message.getStatus() == StrategicRouteStatus.CANCELED) {
                handleAcknowledgeMsg(entry);
            } else if (message.getStatus() == StrategicRouteStatus.NEGOTIATING) {
                entry.setHandled(false);
                notifyStrategicRouteListeners();
            }
            
        } else if (message.getStatus() == StrategicRouteStatus.PENDING) {
            entry = new StrategicRouteNegotiationData(message.getId(), MaritimeCloudUtils.toMmsi(caller));
            strategicRouteNegotiationData.put(message.getId(), entry);
            entry.addMessage(message);
            entry.setHandled(false);
            notifyStrategicRouteListeners();
        }
    }

    /**
     * Called when an acknowledge message is received
     * @param message the message
     */
    private void handleAcknowledgeMsg(StrategicRouteNegotiationData entry) {

        if (entry.getStatus() ==  StrategicRouteStatus.AGREED) {

            LOG.info("Transaction with id" + entry.getId()
                    + " has been completed!");

            // Ship has ack it, set status to completed and add the finished
            // voyage to the voyageManager

            Voyage voyage = new Voyage(entry.getMmsi(), entry.getLatestAcceptedRoute(), entry.getId());
            voyageManager.addVoyage(voyage);
        }

        entry.setHandled(true);
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

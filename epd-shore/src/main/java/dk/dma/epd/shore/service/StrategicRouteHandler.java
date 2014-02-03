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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.service.ServiceEndpoint;
import net.maritimecloud.net.service.invocation.InvocationCallback;
import net.maritimecloud.net.service.invocation.InvocationCallback.Context;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteAckService;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteAckService.StrategicRouteAckMsg;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestReply;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon;
import dk.dma.epd.common.prototype.service.InvocationCallbackContextMap;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.voyage.Voyage;
import dk.dma.epd.shore.voyage.VoyageManager;

/**
 * Handler class for the strategic route e-Navigation service
 */
public class StrategicRouteHandler extends EnavServiceHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(StrategicRouteHandler.class);
    private static final long CALLBACK_TTL = 1 * 60 * 60; // 60 minutes
    
    private VoyageManager voyageManager;

    private InvocationCallbackContextMap<Long, Context<StrategicRouteRequestReply>> strategicRouteContexts = new InvocationCallbackContextMap<>(CALLBACK_TTL);
    private List<ServiceEndpoint<StrategicRouteRequestMessage, StrategicRouteRequestReply>> strategicRouteShipList = new ArrayList<>();
    
    Map<Long, StrategicRouteNegotiationData> strategicNegotiationData = new ConcurrentHashMap<>();
    protected Set<StrategicRouteListener> strategicRouteListener = new ConcurrentHashSet<>();
    private List<Long> unhandledTransactions  = new CopyOnWriteArrayList<>();

    /**
     * Constructor
     */
    public StrategicRouteHandler() {
        super();
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
            listenToStrategicRouteAck();
        } catch (Exception e) {
            getStatus().markFailedSend();
            getStatus().markFailedReceive();
            LOG.error("Error hooking up services", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudPeriodicTask() {
        fetchStrategicShipList();
        
        // Clean up cached invocation callback contexts
        strategicRouteContexts.cleanup();
    }
    
    /**
     * Register a strategic route service
     */
    private void registerStrategicRouteService() throws InterruptedException {

        getMaritimeCloudConnection()
                .serviceRegister(
                        StrategicRouteService.INIT,
                        new InvocationCallback<StrategicRouteRequestMessage, StrategicRouteRequestReply>() {
                            public void process(StrategicRouteRequestMessage message,
                                    Context<StrategicRouteRequestReply> context) {

                                // long mmsi = message.getMmsi();
                                strategicRouteContexts.put(message.getId(), context);

                                handleStrategicRouteRequest(message);
                            }
                        }).awaitRegistered(4, TimeUnit.SECONDS);
    }

    /**
     * Register a strategic route acknowledge service
     */
    private void listenToStrategicRouteAck() throws InterruptedException {
        getMaritimeCloudConnection()
            .serviceRegister(StrategicRouteAckService.INIT, new InvocationCallback<StrategicRouteAckMsg, Void>() {
                @Override
                public void process(StrategicRouteAckMsg message, Context<Void> context) {
    
                    handleSingleAckMsg(message);
                }
            }).awaitRegistered(4, TimeUnit.SECONDS);
    }

    /**
     * Sends a strategic route request to the given ship
     * 
     * @param mmsiDestination the destination mmsi
     * @param routeMessage the strategic route to send
     */
    private void sendStrategicRenegotiateRequest(long mmsiDestination, StrategicRouteRequestMessage routeMessage) {

        ServiceEndpoint<StrategicRouteRequestMessage, StrategicRouteRequestReply> end 
            = MaritimeCloudUtils.findServiceWithMmsi(strategicRouteShipList, (int)mmsiDestination);

        // Each request has a unique ID, talk to Kasper?

        if (end != null) {
            end.invoke(routeMessage);

            // we don't need to handle a reply, a new one will be sent as a
            // normal transaction

        } else {
            LOG.error("Failed to find ship with id " + mmsiDestination);
        }
    }

    /**
     * Fetches the list of ships with a strategic route service
     */
    private void fetchStrategicShipList() {
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
        fetchStrategicShipList();
        
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
        
        // Should a reply be sent?
        
        long mmsi = getOwnMMSI();

        StrategicRouteStatus status = strategicNegotiationData.get(id).getStatus();
        
        StrategicRouteService.StrategicRouteRequestReply reply = new StrategicRouteService.StrategicRouteRequestReply(
                text, id, mmsi, System.currentTimeMillis(), replyStatus,
                route);
        
        if (status == StrategicRouteStatus.NEGOTIATING
                || status == StrategicRouteStatus.PENDING) {

            strategicNegotiationData.get(id).addReply(reply);
            strategicNegotiationData.get(id).setStatus(
                    StrategicRouteStatus.NEGOTIATING);
            strategicNegotiationData.get(id).setHandled(true);

            sendStrategicRouteReply(reply);
            notifyStrategicRouteListeners();
            
        } else {
            
            //We need to re-negotiate
            if (renegotiate){
                
                LOG.info("Restart negotiation");
                
                StrategicRouteRequestMessage routeMessage = new StrategicRouteService.StrategicRouteRequestMessage(
                        id, route, mmsi, text);
                
                dk.dma.epd.common.prototype.model.route.Route naRoute = new dk.dma.epd.common.prototype.model.route.Route(route);
                naRoute.setName("N/A");
                
                
                StrategicRouteRequestMessage internalMessage = new StrategicRouteService.StrategicRouteRequestMessage(
                        id, naRoute.getFullRouteData(), mmsi, "N/A");
                
                StrategicRouteService.StrategicRouteRequestReply shoreInternalReply = new StrategicRouteService.StrategicRouteRequestReply(
                        text, id, mmsi, System.currentTimeMillis(), StrategicRouteStatus.NEGOTIATING,
                        route);
                
                for (int i = 0; i < voyageManager.getVoyages().size(); i++) {
                    if (voyageManager.getVoyages().get(i).getId() == id){
                        voyageManager.removeVoyage(i);
                    }
                }
                
                strategicNegotiationData.get(id).addMessage(internalMessage);
                strategicNegotiationData.get(id).addReply(shoreInternalReply);
                strategicNegotiationData.get(id).setStatus(
                        StrategicRouteStatus.NEGOTIATING);
                strategicNegotiationData.get(id).setHandled(true);
                
                sendStrategicRenegotiateRequest(strategicNegotiationData.get(id).getRouteMessage().get(0).getMmsi(), routeMessage);
                notifyStrategicRouteListeners();
                
                
 
                
//                voyageManager.addVoyage(voyage);
            }else{
                strategicNegotiationData.get(id).addReply(reply);
                LOG.error("Cannot send message, transaction concluded");                
            }
        }
    }
    
    /**
     * Sends a strategic route reply
     * @param reply the reply to send
     */
    private void sendStrategicRouteReply(StrategicRouteService.StrategicRouteRequestReply reply) {
        try {

            if (strategicRouteContexts.containsKey(reply.getId())) {
                System.out.println("Sending");
                strategicRouteContexts.remove(reply.getId()).complete(reply);
            } else {
                LOG.error("No strategic route context found for route request " + reply.getId());
            }

        } catch (Exception e) {
            System.out.println("Failed to reply");
        }

    }


    /**
     * Sends a strategic route reply
     * @param reply the reply to send
     */
    private void handleStrategicRouteRequest(StrategicRouteRequestMessage message) {

        // Received a message, how to handle it?

        long transactionID = message.getId();
        long mmsi = message.getMmsi();

        StrategicRouteNegotiationData entry;

        if (strategicNegotiationData.containsKey(transactionID)) {
            entry = strategicNegotiationData.get(transactionID);

            // Not handled anymore, new pending message
            entry.setHandled(false);
        } else {
            entry = new StrategicRouteNegotiationData(message.getId(), mmsi);

        }

        entry.setStatus(StrategicRouteStatus.PENDING);

        entry.addMessage(message);

        strategicNegotiationData.put(message.getId(), entry);

        notifyStrategicRouteListeners();
    }

    /**
     * Called when an acknowledge message is received
     * @param message the message
     */
    private void handleSingleAckMsg(StrategicRouteAckMsg message) {

        if (strategicNegotiationData.containsKey(message.getId())) {

            StrategicRouteRequestMessage routeMessage = new StrategicRouteService.StrategicRouteRequestMessage(
                    message.getId(), strategicNegotiationData.get(message.getId()).getLatestRoute(), message.getMmsi(), message.getMessage());
            
            
            strategicNegotiationData.get(message.getId()).addMessage(routeMessage);
            
            
            if (message.isAck()) {

                System.out.println("Transaction with id" + message.getId()
                        + " has been completed!");

                strategicNegotiationData.get(message.getId()).setCompleted(true);
                strategicNegotiationData.get(message.getId()).setStatus(
                        StrategicRouteStatus.AGREED);

                // Ship has ack it, set status to completed and add the finished
                // voyage to the voyageManager

                Voyage voyage = new Voyage(message.getMmsi(),
                        new dk.dma.epd.common.prototype.model.route.Route(
                                strategicNegotiationData
                                        .get(message.getId())
                                        .getRouteReply()
                                        .get(strategicNegotiationData
                                                .get(message.getId())
                                                .getRouteReply().size() - 1)
                                        .getRoute()), message.getId());

                voyageManager.addVoyage(voyage);
            } else {

                // Is there a reply? if not then its a cancel, else its a
                // rejected
                if (strategicNegotiationData.get(message.getId())
                        .getRouteReply().size() == 0) {
                    // cancelled
                    strategicNegotiationData.get(message.getId()).setStatus(
                            StrategicRouteStatus.CANCELED);
                } else {
                    strategicNegotiationData.get(message.getId()).setStatus(
                            StrategicRouteStatus.REJECTED);
                }

                strategicNegotiationData.get(message.getId()).setCompleted(true);
                strategicNegotiationData.get(message.getId()).setHandled(true);

                System.out
                        .println("Ship rejected it, end transaction and remove stuff");

                // If rejected?

            }

            notifyStrategicRouteListeners();
        }
    }

    /**
     * Returns the strategic negotiation data
     * @return the strategic negotiation data
     */
    public Map<Long, StrategicRouteNegotiationData> getStrategicNegotiationData() {
        return strategicNegotiationData;
    }

    /**
     * Calculates the list of unhandled transactions
     */
    private void calculateUnhandled() {
        unhandledTransactions = new ArrayList<>();

        for (StrategicRouteNegotiationData value : strategicNegotiationData.values()) {
            if (!value.isHandled()) {
                unhandledTransactions.add(value.getId());
            }
        }

    }

    /**
     * Returns the number of unhandled transactions
     * @return the number of unhandled transactions
     */
    public int getUnHandled() {
        return unhandledTransactions.size();
    }

    /**
     * Adds a listener for strategic route updates
     * @param listener the lister to add
     */
    public synchronized void addStrategicRouteListener(StrategicRouteListener listener) {
        strategicRouteListener.add(listener);
    }

    /**
     * Notifies listeners about a strategic route update
     */
    protected synchronized void notifyStrategicRouteListeners() {
        calculateUnhandled();
        for (StrategicRouteListener listener : strategicRouteListener) {
            listener.strategicRouteUpdate();
        }
    }

    /**
     * Returns the current list of unhandled transactions
     * @return the current list of unhandled transactions
     */
    public List<Long> getUnhandledTransactions() {
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

    /**
     * Interface to be implemented by all clienst wishing 
     * to be notified about updates to strategic routes
     */
    public interface StrategicRouteListener {
        
        /**
         * Cloud messages has changed
         */
        void strategicRouteUpdate();

    }

}

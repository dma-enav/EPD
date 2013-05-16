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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteAck.MonaLisaRouteAckMsg;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteStatus;
import dk.dma.epd.shore.voyage.Voyage;
import dk.dma.epd.shore.voyage.VoyageManager;

public class MonaLisaHandler extends MapHandlerChild {

    HashMap<Long, MonaLisaRouteNegotiationData> monaLisaNegotiationData = new HashMap<Long, MonaLisaRouteNegotiationData>();
    protected Set<MonaLisaRouteExchangeListener> monaLisaRouteExchangeListener = new HashSet<MonaLisaRouteExchangeListener>();

    private EnavServiceHandler enavServiceHandler;
    private VoyageManager voyageManager;

    private List<Long> unhandledTransactions  = new ArrayList<Long>();
    
    private int unhandled;

    // public void sendReply(MonaLisaRouteService.MonaLisaRouteRequestReply
    // reply) {
    // // Store the reply we are sending?
    //
    // enavServiceHandler.sendReply(reply);
    // notifyMonaLisaRouteExchangeListeners();
    // }

    public void sendReply(long id, String text, long mmsi,
            long currentTimeMillis, MonaLisaRouteStatus replyStatus, Route route) {
        
        // Should a reply be sent?

        MonaLisaRouteStatus status = monaLisaNegotiationData.get(id)
                .getStatus();
        
        System.out.println("Reply recieved internal - Current status is " + status);

        System.out.println(status == MonaLisaRouteStatus.NEGOTIATING
                || status == MonaLisaRouteStatus.PENDING);
        
        MonaLisaRouteService.MonaLisaRouteRequestReply reply = new MonaLisaRouteService.MonaLisaRouteRequestReply(
                text, id, mmsi, System.currentTimeMillis(), replyStatus,
                route);
        
        if (status == MonaLisaRouteStatus.NEGOTIATING
                || status == MonaLisaRouteStatus.PENDING) {

            monaLisaNegotiationData.get(id).addReply(reply);
            monaLisaNegotiationData.get(id).setStatus(
                    MonaLisaRouteStatus.NEGOTIATING);
            monaLisaNegotiationData.get(id).setHandled(true);

            enavServiceHandler.sendReply(reply);
            notifyMonaLisaRouteExchangeListeners();
        }else{
            monaLisaNegotiationData.get(id).addReply(reply);
            System.out.println("Cannot send message, transaction concluded");
        }
    }

    public void handleMessage(MonaLisaRouteRequestMessage message) {

        // Recieved a message, how to handle it?

        long transactionID = message.getId();
        long mmsi = message.getMmsi();

        MonaLisaRouteNegotiationData entry;

        if (monaLisaNegotiationData.containsKey(transactionID)) {
            System.out.println("Adding to existing");
            entry = monaLisaNegotiationData.get(transactionID);

            // Not handled anymore, new pending message
            entry.setHandled(false);
        } else {
            entry = new MonaLisaRouteNegotiationData(message.getId(), mmsi);

        }

        entry.setStatus(MonaLisaRouteStatus.PENDING);

        entry.addMessage(message);

        monaLisaNegotiationData.put(message.getId(), entry);

        notifyMonaLisaRouteExchangeListeners();
    }

    public void handleSingleAckMsg(MonaLisaRouteAckMsg message) {

        if (monaLisaNegotiationData.containsKey(message.getId())) {

            if (message.isAck()) {

                System.out.println("Transaction with id" + message.getId()
                        + " has been completed!");

                monaLisaNegotiationData.get(message.getId()).setCompleted(true);
                monaLisaNegotiationData.get(message.getId()).setStatus(
                        MonaLisaRouteStatus.AGREED);

                // Ship has ack it, set status to completed and add the finished
                // voyage to the voyageManager

                Voyage voyage = new Voyage(message.getMmsi(),
                        new dk.dma.epd.common.prototype.model.route.Route(
                                monaLisaNegotiationData
                                        .get(message.getId())
                                        .getRouteReply()
                                        .get(monaLisaNegotiationData
                                                .get(message.getId())
                                                .getRouteReply().size() - 1)
                                        .getRoute()), message.getId());

                voyageManager.addVoyage(voyage);
            } else {

                // Is there a reply? if not then its a cancel, else its a
                // rejected
                if (monaLisaNegotiationData.get(message.getId())
                        .getRouteReply().size() == 0) {
                    // cancelled
                    monaLisaNegotiationData.get(message.getId()).setStatus(
                            MonaLisaRouteStatus.CANCELED);
                } else {
                    monaLisaNegotiationData.get(message.getId()).setStatus(
                            MonaLisaRouteStatus.REJECTED);
                }

                monaLisaNegotiationData.get(message.getId()).setCompleted(true);
                monaLisaNegotiationData.get(message.getId()).setHandled(true);

                System.out
                        .println("Ship rejected it, end transaction and remove stuff");

                // If rejected?

            }

            notifyMonaLisaRouteExchangeListeners();
        }
    }

    public HashMap<Long, MonaLisaRouteNegotiationData> getMonaLisaNegotiationData() {
        return monaLisaNegotiationData;
    }

    private void calculateUnhandled() {
        unhandledTransactions = new ArrayList<Long>();
        unhandled = 0;

        System.out.println(monaLisaNegotiationData.size());

        for (MonaLisaRouteNegotiationData value : monaLisaNegotiationData
                .values()) {
            if (!value.isHandled()) {
                unhandled++;
                unhandledTransactions.add(value.getId());
            }
        }

    }

    public int getUnHandled() {
        return unhandled;
    }

    public synchronized void addMonaLisaRouteExchangeListener(
            MonaLisaRouteExchangeListener listener) {
        monaLisaRouteExchangeListener.add(listener);
    }

    protected synchronized void notifyMonaLisaRouteExchangeListeners() {
        calculateUnhandled();
        for (MonaLisaRouteExchangeListener listener : monaLisaRouteExchangeListener) {
            System.out.println("notify listeners");
            listener.monaLisaRouteUpdate();
        }
    }

    @Override
    public void findAndInit(Object obj) {
        // if (obj instanceof AisHandler) {
        // aisHandler = (AisHandler) obj;
        // } else
        if (obj instanceof EnavServiceHandler) {
            enavServiceHandler = (EnavServiceHandler) obj;
            // } else if (obj instanceof VoyageLayer) {
            // voyageLayer = (VoyageLayer) obj;
            // } else if (obj instanceof RouteManager) {
            // routeManager = (RouteManager) obj;
        } else if (obj instanceof VoyageManager) {
            this.voyageManager = (VoyageManager) obj;
            // intendedRouteService = new IntendedRouteService(this,
            // (ActiveRouteProvider) obj);
            // ((RouteManager) obj).addListener(intendedRouteService);
            // ((RouteManager)
            // obj).setIntendedRouteService(intendedRouteService);

            // intendedRouteService.start();
            // } else if (obj instanceof EnavCloudHandler) {
            // enavCloudHandler = (EnavCloudHandler) obj;
            // enavCloudHandler.start();
        }
    }

    public List<Long> getUnhandledTransactions() {
        return unhandledTransactions;
    }
    
    
    

}

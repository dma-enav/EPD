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
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteAck.StrategicRouteAckMsg;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;
import dk.dma.epd.shore.voyage.Voyage;
import dk.dma.epd.shore.voyage.VoyageManager;

public class StrategicRouteExchangeHandler extends MapHandlerChild {

    HashMap<Long, StrategicRouteNegotiationData> strategicNegotiationData = new HashMap<Long, StrategicRouteNegotiationData>();
    protected Set<StrategicRouteExchangeListener> strategicRouteExchangeListener = new HashSet<StrategicRouteExchangeListener>();

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

    public void sendReply(long id, String text,
            long currentTimeMillis, StrategicRouteStatus replyStatus, Route route, boolean renegotiate) {
        
        // Should a reply be sent?
        
        long mmsi = enavServiceHandler.getOwnMMSI();

        StrategicRouteStatus status = strategicNegotiationData.get(id)
                .getStatus();
        
        System.out.println(status == StrategicRouteStatus.NEGOTIATING
                || status == StrategicRouteStatus.PENDING);
        
        StrategicRouteService.StrategicRouteRequestReply reply = new StrategicRouteService.StrategicRouteRequestReply(
                text, id, mmsi, System.currentTimeMillis(), replyStatus,
                route);
        
        if (status == StrategicRouteStatus.NEGOTIATING
                || status == StrategicRouteStatus.PENDING) {

            strategicNegotiationData.get(id).addReply(reply);
            strategicNegotiationData.get(id).setStatus(
                    StrategicRouteStatus.NEGOTIATING);
            strategicNegotiationData.get(id).setHandled(true);

            enavServiceHandler.sendReply(reply);
            notifyStrategicRouteExchangeListeners();
        }else{
            
            //We need to renegotiate
            if (renegotiate){
                
                System.out.println("Restart negotiation");
                
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
                
                enavServiceHandler.sendStrategicRouteRequest(strategicNegotiationData.get(id).getRouteMessage().get(0).getMmsi(), routeMessage);
                notifyStrategicRouteExchangeListeners();
                
                
 
                
//                voyageManager.addVoyage(voyage);
            }else{
                strategicNegotiationData.get(id).addReply(reply);
                System.out.println("Cannot send message, transaction concluded");                
            }
        }
    }

    public void handleMessage(StrategicRouteRequestMessage message) {

        // Received a message, how to handle it?

        long transactionID = message.getId();
        long mmsi = message.getMmsi();

        StrategicRouteNegotiationData entry;

        if (strategicNegotiationData.containsKey(transactionID)) {
            System.out.println("Adding to existing");
            entry = strategicNegotiationData.get(transactionID);

            // Not handled anymore, new pending message
            entry.setHandled(false);
        } else {
            entry = new StrategicRouteNegotiationData(message.getId(), mmsi);

        }

        entry.setStatus(StrategicRouteStatus.PENDING);

        entry.addMessage(message);

        strategicNegotiationData.put(message.getId(), entry);

        notifyStrategicRouteExchangeListeners();
    }

    public void handleSingleAckMsg(StrategicRouteAckMsg message) {

        if (strategicNegotiationData.containsKey(message.getId())) {

            strategicNegotiationData.containsKey(message.getId());
            
            
            
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

            notifyStrategicRouteExchangeListeners();
        }
    }

    public HashMap<Long, StrategicRouteNegotiationData> getStrategicNegotiationData() {
        return strategicNegotiationData;
    }

    private void calculateUnhandled() {
        unhandledTransactions = new ArrayList<Long>();
        unhandled = 0;

        System.out.println(strategicNegotiationData.size());

        for (StrategicRouteNegotiationData value : strategicNegotiationData
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

    public synchronized void addStrategicRouteExchangeListener(
            StrategicRouteExchangeListener listener) {
        strategicRouteExchangeListener.add(listener);
    }

    protected synchronized void notifyStrategicRouteExchangeListeners() {
        calculateUnhandled();
        for (StrategicRouteExchangeListener listener : strategicRouteExchangeListener) {
            System.out.println("notify listeners");
            listener.strategicRouteUpdate();
        }
    }

    @Override
    public void findAndInit(Object obj) {
        // if (obj instanceof AisHandlerCommon) {
        // aisHandler = (AisHandlerCommon) obj;
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

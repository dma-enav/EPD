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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.communication.ConnectionFuture;
import dk.dma.enav.communication.PersistentConnection;
import dk.dma.enav.communication.broadcast.BroadcastListener;
import dk.dma.enav.communication.broadcast.BroadcastMessage;
import dk.dma.enav.communication.broadcast.BroadcastMessageHeader;
import dk.dma.enav.communication.service.InvocationCallback;
import dk.dma.enav.communication.service.ServiceEndpoint;
import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.geometry.PositionTime;
import dk.dma.enav.model.ship.ShipId;
import dk.dma.enav.model.voyage.Route;
import dk.dma.enav.util.function.BiConsumer;
import dk.dma.enav.util.function.Supplier;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.enavcloud.CloudIntendedRoute;
import dk.dma.epd.common.prototype.enavcloud.EnavCloudSendThread;
import dk.dma.epd.common.prototype.enavcloud.EnavRouteBroadcast;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteStatus;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.AIS_STATUS;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionReply;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.prototype.sensor.gps.IGpsDataListener;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gps.GpsHandler;
import dk.dma.epd.shore.route.RouteManager;
import dk.dma.epd.shore.settings.ESDEnavSettings;
import dk.dma.navnet.client.MaritimeNetworkConnectionBuilder;

/**
 * Component offering e-Navigation services
 */
public class EnavServiceHandler extends MapHandlerChild implements
        IGpsDataListener, Runnable {

    private static final Logger LOG = LoggerFactory
            .getLogger(EnavServiceHandler.class);

    private String hostPort;
    private ShipId shipId;
    private GpsHandler gpsHandler;
    private AisHandler aisHandler;

    PersistentConnection connection;
    RouteSuggestionDataStructure<RouteSuggestionKey, RouteSuggestionData> routeSuggestions = new RouteSuggestionDataStructure<RouteSuggestionKey, RouteSuggestionData>();
    protected Set<RouteExchangeListener> routeExchangeListener = new HashSet<RouteExchangeListener>();
    
    protected Set<MonaLisaRouteExchangeListener> monaLisaRouteExchangeListener = new HashSet<MonaLisaRouteExchangeListener>();
    
    private List<ServiceEndpoint<RouteSuggestionMessage, RouteSuggestionReply>> routeSuggestionList = new ArrayList<>();

    HashMap<Long, InvocationCallback.Context<MonaLisaRouteService.MonaLisaRouteRequestReply>> contextSenders = new HashMap<Long, InvocationCallback.Context<MonaLisaRouteService.MonaLisaRouteRequestReply>>();
    HashMap<Long, MonaLisaRouteNegotationData> monaLisaNegotiationData = new HashMap<Long, MonaLisaRouteNegotationData>();
    
    
    public EnavServiceHandler(ESDEnavSettings enavSettings) {
        this.hostPort = String.format("%s:%d",
                enavSettings.getCloudServerHost(),
                enavSettings.getCloudServerPort());
    }

    public PersistentConnection getConnection() {
        return connection;
    }

    public void listenToBroadcasts() throws InterruptedException {
        connection.broadcastListen(EnavRouteBroadcast.class,
                new BroadcastListener<EnavRouteBroadcast>() {
                    public void onMessage(BroadcastMessageHeader l,
                            EnavRouteBroadcast r) {
                        int id = Integer.parseInt(l.getId().toString()
                                .split("mmsi://")[1]);
                        updateIntendedRoute(id, r.getIntendedRoute());
                    }
                });
    }

    /**
     * Update intended route of vessel target
     * 
     * @param mmsi
     * @param routeData
     */
    private synchronized void updateIntendedRoute(long mmsi, Route routeData) {
        Map<Long, VesselTarget> vesselTargets = aisHandler.getVesselTargets();

        System.out.println("Intended route recieved");
        
        // Try to find exiting target
        VesselTarget vesselTarget = vesselTargets.get(mmsi);
        // If not exists, wait for it to be created by position report
        if (vesselTarget == null) {
            return;
        }

        CloudIntendedRoute intendedRoute = new CloudIntendedRoute(routeData);

        // Update intented route
        vesselTarget.setCloudRouteData(intendedRoute);
        aisHandler.publishUpdate(vesselTarget);
    }

    /**
     * Send maritime message over enav cloud
     * 
     * @param message
     * @return
     * @throws Exception
     */
    public void sendMessage(BroadcastMessage message) throws Exception {

        EnavCloudSendThread sendThread = new EnavCloudSendThread(message,
                connection);

        // Send it in a seperate thread
        sendThread.start();
    }

    private void getRouteSuggestionServiceList() {
        try {
            routeSuggestionList = connection
                    .serviceFind(RouteSuggestionService.INIT)
                    .nearest(Integer.MAX_VALUE).get();
        } catch (Exception e) {
            LOG.error(e.getMessage());

        }

        // for (int i = 0; i < routeSuggestionList.size(); i++) {
        // System.out.println(routeSuggestionList.get(i).getId());
        // }
    }

    public List<ServiceEndpoint<RouteSuggestionMessage, RouteSuggestionReply>> getRouteSuggestionList() {
        return routeSuggestionList;
    }

    public boolean shipAvailableForRouteSuggestion(long mmsi) {
        for (int i = 0; i < routeSuggestionList.size(); i++) {
            if (mmsi == Long.parseLong(routeSuggestionList.get(i).getId()
                    .toString().split("//")[1])) {
                return true;
            }

        }

        return false;
    }

    public void sendRouteSuggestion(long mmsi, Route route, String sender, String message)
            throws InterruptedException, ExecutionException, TimeoutException {

        // System.out.println("Send to : " + mmsi);
        String mmsiStr = "mmsi://" + mmsi;
        ServiceEndpoint<RouteSuggestionService.RouteSuggestionMessage, RouteSuggestionService.RouteSuggestionReply> end = null;

        for (int i = 0; i < routeSuggestionList.size(); i++) {
            if (routeSuggestionList.get(i).getId().toString().equals(mmsiStr)) {
                end = routeSuggestionList.get(i);
                // break;
            }
        }

        // ServiceEndpoint<RouteSuggestionService.RouteSuggestionMessage,
        // RouteSuggestionService.RouteSuggestionReply> end = connection
        // .serviceFind(RouteSuggestionService.INIT).nearest(Integer.MAX_VALUE).get().get(0);

        // mmsi = 219230000;

        RouteSuggestionMessage routeMessage = new RouteSuggestionService.RouteSuggestionMessage(
                route, sender, message);

        System.out.println("Sending to mmsi: " + mmsi + " with ID: "
                + routeMessage.getId());

        RouteSuggestionData suggestionData = new RouteSuggestionData(
                routeMessage, null, routeMessage.getId(), mmsi, false,
                AIS_STATUS.RECIEVED_APP_ACK);
        RouteSuggestionKey routeSuggestionKey = new RouteSuggestionKey(mmsi,
                routeMessage.getId());
        routeSuggestions.put(routeSuggestionKey, suggestionData);

        if (end != null) {
            ConnectionFuture<RouteSuggestionService.RouteSuggestionReply> f = end
                    .invoke(routeMessage);

            // EPDShore.getMainFrame().getNotificationCenter().cloudUpdate();
            notifyRouteExchangeListeners();

//             f.timeout(10, TimeUnit.SECONDS).handle(new BiConsumer<RouteSuggestionService.RouteSuggestionReply, Throwable>() {
//
//                 @Override
//                 public void accept(RouteSuggestionReply l, Throwable r) {
//                     System.out.println("TIME OUT TIME OUT TIME OUT");
//                     System.out.println("TIME OUT TIME OUT TIME OUT");
//                 }
//             });

            f.handle(new BiConsumer<RouteSuggestionService.RouteSuggestionReply, Throwable>() {

                @Override
                public void accept(RouteSuggestionReply l, Throwable r) {
                    replyRecieved(l);
                }
            });

        } else {
            // notifyRouteExchangeListeners();
            System.out.println("Failed to send");
            // replyRecieved(f.get());
        }

    }

    public RouteSuggestionDataStructure<RouteSuggestionKey, RouteSuggestionData> getRouteSuggestions() {
        return routeSuggestions;
    }
    
    public HashMap<Long, MonaLisaRouteNegotationData> getMonaLisaNegotiationData() {
        return monaLisaNegotiationData;
    }

    /**
     * Create the message bus
     */
    public void init() {
        LOG.info("Connecting to enav cloud server: " + hostPort
                + " with shipId " + shipId.getId());

        // enavCloudConnection =
        // MaritimeNetworkConnectionBuilder.create("mmsi://"+shipId.getId());
        MaritimeNetworkConnectionBuilder enavCloudConnection = MaritimeNetworkConnectionBuilder
                .create("mmsi://" + shipId.getId());

        enavCloudConnection.setPositionSupplier(new Supplier<PositionTime>() {
            public PositionTime get() {
                Position position = gpsHandler.getCurrentData().getPosition();
                if (position != null) {
                    return PositionTime.create(position,
                            System.currentTimeMillis());
                } else {
                    return PositionTime.create(Position.create(0.0, 0.0),
                            System.currentTimeMillis());
                }

            }
        });

        try {
            enavCloudConnection.setHost(hostPort);
            System.out.println(hostPort);
            connection = enavCloudConnection.build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ENavContainerConfiguration conf = new ENavContainerConfiguration();
        // conf.addDatasource(new JmsC2SMessageSource(hostPort, shipId));
        // ENavContainer client = conf.createAndStart();
        // messageBus = client.getService(MessageBus.class);
        LOG.info("Started succesfull cloud server: " + hostPort
                + " with shipId " + shipId.getId());

    }

    /**
     * Receive position updates
     */
    @Override
    public void gpsDataUpdate(GpsData gpsData) {
        // TODO give information to messageBus if valid position
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof RouteManager) {

            // intendedRouteService = new IntendedRouteService(this,
            // (ActiveRouteProvider) obj);
            // ((RouteManager) obj).addListener(intendedRouteService);
            // ((RouteManager)
            // obj).setIntendedRouteService(intendedRouteService);

            // intendedRouteService.start();
            // } else if (obj instanceof EnavCloudHandler) {
            // enavCloudHandler = (EnavCloudHandler) obj;
            // enavCloudHandler.start();
        } else if (obj instanceof GpsHandler) {
            this.gpsHandler = (GpsHandler) obj;
            this.gpsHandler.addListener(this);
        } else if (obj instanceof AisHandler) {
            this.aisHandler = (AisHandler) obj;
        }
    }

    @Override
    public void run() {

        // For now ship id will be MMSI so we need to know
        // own ship information. Busy wait for it.

        while (true) {
            Util.sleep(1000);
            if (this.aisHandler != null) {
                VesselTarget ownShip = this.aisHandler.getOwnShip();
                // System.out.println(ownShip);
                if (ownShip != null) {
                    if (ownShip.getMmsi() > 0) {
                        shipId = ShipId
                                .create(Long.toString(ownShip.getMmsi()));
                        init();
                        try {
                            listenToBroadcasts();
                            monaLisaRouteRequestListener();
                        } catch (Exception e) {
                            // Exception for virtual net
                            System.out
                                    .println("An exception occured trying to listen to broadcasts, possibly connection issue");
                        }

                        break;
                    }
                }
            }
        }

        while (true) {
            getRouteSuggestionServiceList();
            Util.sleep(10000);
        }

    }

    public void start() {
        new Thread(this).start();
    }

    /**
     * Add a listener to the asService
     * 
     * @param listener
     */
    public synchronized void addRouteExchangeListener(
            RouteExchangeListener listener) {
        routeExchangeListener.add(listener);
    }
    
    public synchronized void addMonaLisaRouteExchangeListener(
            MonaLisaRouteExchangeListener listener) {
        monaLisaRouteExchangeListener.add(listener);
    }

    protected synchronized void notifyMonaLisaRouteExchangeListeners() {

        for (MonaLisaRouteExchangeListener listener : monaLisaRouteExchangeListener) {
            listener.monaLisaRouteUpdate();
        }
    }
    

    protected synchronized void notifyRouteExchangeListeners() {

        for (RouteExchangeListener listener : routeExchangeListener) {
            listener.routeUpdate();
        }
    }

    public void setAcknowledged(long l, long m) {
        routeSuggestions.get(new RouteSuggestionKey(l, m)).setAcknowleged(true);
        notifyRouteExchangeListeners();
    }

    public void removeSuggestion(long l, long id) {
        routeSuggestions.remove(new RouteSuggestionKey(l, id));
        notifyRouteExchangeListeners();
    }

    public int getUnkAck() {

        int counter = 0;

        Collection<RouteSuggestionData> c = routeSuggestions.values();

        // obtain an Iterator for Collection
        Iterator<RouteSuggestionData> itr = c.iterator();

        // iterate through HashMap values iterator
        while (itr.hasNext()) {
            RouteSuggestionData value = itr.next();
            if (!value.isAcknowleged()) {
                counter++;
            }
        }

        return counter;
    }

    public void replyRecieved(RouteSuggestionReply message) {

        System.out.println("MSG Recieved from MMSI: " + message.getMmsi()
                + " and ID " + message.getId());

        if (routeSuggestions.containsKey(new RouteSuggestionKey(message
                .getMmsi(), message.getId()))) {

            // System.out.println("Reply recieved for " + mmsi + " " +
            // message.getRefMsgLinkId());
            AIS_STATUS response = message.getStatus();

            long mmsi = message.getMmsi();
            long id = message.getId();

            routeSuggestions.get(new RouteSuggestionKey(message
                    .getMmsi(), message.getId())).setReply(message);
            
            switch (response) {
            case RECIEVED_ACCEPTED:
                if (routeSuggestions.get(new RouteSuggestionKey(mmsi, id))
                        .getStatus() != AIS_STATUS.RECIEVED_ACCEPTED) {
                    // Accepted
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id))
                            .setStatus(AIS_STATUS.RECIEVED_ACCEPTED);
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id))
                            .setAcknowleged(false);
                    notifyRouteExchangeListeners();
                }

                break;
            case RECIEVED_REJECTED:
                // Rejected
                if (routeSuggestions.get(new RouteSuggestionKey(mmsi, id))
                        .getStatus() != AIS_STATUS.RECIEVED_REJECTED) {
                    // Accepted
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id))
                            .setStatus(AIS_STATUS.RECIEVED_REJECTED);
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id))
                            .setAcknowleged(false);
                    notifyRouteExchangeListeners();
                }
                break;
            case RECIEVED_NOTED:
                // Noted
                if (routeSuggestions.get(new RouteSuggestionKey(mmsi, id))
                        .getStatus() != AIS_STATUS.RECIEVED_NOTED) {
                    // Accepted
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id))
                            .setStatus(AIS_STATUS.RECIEVED_NOTED);
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id))
                            .setAcknowleged(false);
                    notifyRouteExchangeListeners();
                }
                break;
            default:
                break;
            }
        }

    }
    
    
    private void monaLisaRouteRequestListener() throws InterruptedException {

        connection
                .serviceRegister(
                        MonaLisaRouteService.INIT,
                        new InvocationCallback<MonaLisaRouteService.MonaLisaRouteRequestMessage, MonaLisaRouteService.MonaLisaRouteRequestReply>() {
                            public void process(
                                    MonaLisaRouteRequestMessage message,
                                    InvocationCallback.Context<MonaLisaRouteService.MonaLisaRouteRequestReply> context) {

                                
                                
                                contextSenders.put(message.getId(), context);

                                long mmsi = Integer.parseInt(message.getSender().toString()
                                        .split("mmsi://")[1]);
                                
                                monaLisaNegotiationData.put(message.getId(), new MonaLisaRouteNegotationData(message.getId(), mmsi));
                                monaLisaNegotiationData.get(message.getId()).addMessage(message);
                                
                                System.out.println("Mona Lisa Request detected from " + mmsi +", sending reply automatically");
                                
                                notifyMonaLisaRouteExchangeListeners();
                                //We have recieved a message, what now?

                                Route route = message.getRoute();

                                sendReply(MonaLisaRouteStatus.AGREED, message.getId(), "Automatic reply", route);
                                
                            }
                        }).awaitRegistered(4, TimeUnit.SECONDS);
    }

    
    public void sendReply(MonaLisaRouteStatus status, long id, String message, Route route) {
        try {
            contextSenders.get(id).complete(new MonaLisaRouteService.MonaLisaRouteRequestReply(
                    message, id, aisHandler.getOwnShip().getMmsi(),
                    System.currentTimeMillis(), status, route));            
        } catch (Exception e) {
            System.out.println("Failed to reply");
        }

    }
    
}

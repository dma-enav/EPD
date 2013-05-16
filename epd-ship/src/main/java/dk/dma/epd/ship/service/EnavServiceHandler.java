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
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteAck;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteAck.MonaLisaRouteAckMsg;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteRequestReply;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.AIS_STATUS;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.prototype.sensor.gps.IGpsDataListener;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gps.GpsHandler;
import dk.dma.epd.ship.monalisa.MonaLisaHandler;
import dk.dma.epd.ship.monalisa.RecievedRoute;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.service.intendedroute.ActiveRouteProvider;
import dk.dma.epd.ship.service.intendedroute.IntendedRouteService;
import dk.dma.epd.ship.settings.EPDEnavSettings;
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
    private MonaLisaHandler monaLisaHandler;
    private InvocationCallback.Context<RouteSuggestionService.RouteSuggestionReply> context;
    InvocationCallback.Context<MonaLisaRouteService.MonaLisaRouteRequestReply> monaLisaContext;
    

    // End point holders for Mona Lisa Route Exchange
    private List<ServiceEndpoint<MonaLisaRouteRequestMessage, MonaLisaRouteRequestReply>> monaLisaSTCCList = new ArrayList<>();
    private List<ServiceEndpoint<MonaLisaRouteAckMsg, Void>> monaLisaRouteAckList = new ArrayList<>();


    PersistentConnection connection;

    private IntendedRouteService intendedRouteService;

    public EnavServiceHandler(EPDEnavSettings enavSettings) {
        this.hostPort = String.format("%s:%d",
                enavSettings.getCloudServerHost(),
                enavSettings.getCloudServerPort());
    }

    private void intendedRouteListener() throws InterruptedException {
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

    private void getMonaLisaRouteAckList() {
        try {
            monaLisaRouteAckList = connection
                    .serviceFind(MonaLisaRouteAck.INIT)
                    .nearest(Integer.MAX_VALUE).get();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    public PersistentConnection getConnection() {
        return connection;
    }

    private void routeExchangeListener() throws InterruptedException {

        connection
                .serviceRegister(
                        RouteSuggestionService.INIT,
                        new InvocationCallback<RouteSuggestionService.RouteSuggestionMessage, RouteSuggestionService.RouteSuggestionReply>() {
                            public void process(
                                    RouteSuggestionMessage message,
                                    InvocationCallback.Context<RouteSuggestionService.RouteSuggestionReply> context) {

                                setContext(context);

                                RecievedRoute recievedRoute = new RecievedRoute(
                                        message);

                                EPDShip.getRouteManager()
                                        .recieveRouteSuggestion(recievedRoute);

                            }
                        }).awaitRegistered(4, TimeUnit.SECONDS);
    }

    public InvocationCallback.Context<RouteSuggestionService.RouteSuggestionReply> getContext() {
        return context;
    }

    public void setContext(
            InvocationCallback.Context<RouteSuggestionService.RouteSuggestionReply> context) {
        this.context = context;
    }

    public void sendReply(AIS_STATUS recievedAccepted, long id, String message) {
        try {
            context.complete(new RouteSuggestionService.RouteSuggestionReply(
                    message, id, aisHandler.getOwnShip().getMmsi(), System
                            .currentTimeMillis(), recievedAccepted));
        } catch (Exception e) {
            System.out.println("Failed to reply");
        }

    }

    /**
     * Update intended route of vessel target
     * 
     * @param mmsi
     * @param routeData
     */
    private synchronized void updateIntendedRoute(long mmsi, Route routeData) {
        Map<Long, VesselTarget> vesselTargets = aisHandler.getVesselTargets();

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

        // if connection.
        EnavCloudSendThread sendThread = new EnavCloudSendThread(message,
                connection);

        // Send it in a seperate thread
        sendThread.start();
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
            // System.out.println(hostPort);
            connection = enavCloudConnection.build();
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Failed to connect to server");
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
            intendedRouteService = new IntendedRouteService(this,
                    (ActiveRouteProvider) obj);
            ((RouteManager) obj).addListener(intendedRouteService);
            ((RouteManager) obj).setIntendedRouteService(intendedRouteService);
        } else if (obj instanceof GpsHandler) {
            this.gpsHandler = (GpsHandler) obj;
            this.gpsHandler.addListener(this);
        } else if (obj instanceof AisHandler) {
            this.aisHandler = (AisHandler) obj;
        }
        else if (obj instanceof MonaLisaHandler) {
            this.monaLisaHandler = (MonaLisaHandler) obj;
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
                if (ownShip != null) {
                    if (ownShip.getMmsi() > 0) {
                        shipId = ShipId
                                .create(Long.toString(ownShip.getMmsi()));
                        init();
                        try {
                            intendedRouteListener();
                            routeExchangeListener();
                            monaLisaRouteRequestListener();
                        } catch (Exception e) {
                            // e.printStackTrace();
                            System.out.println("Failed to setup listener");
                        }

                        break;
                    }
                }
            }
        }

        while (true) {
            System.out.println("Get new listener list");
            getSTCCList();
            getMonaLisaRouteAckList();
            Util.sleep(10000);
        }
    }

    public void start() {
        new Thread(this).start();
    }

    private void getSTCCList() {
        try {
            monaLisaSTCCList = connection
                    .serviceFind(MonaLisaRouteService.INIT)
                    .nearest(Integer.MAX_VALUE).get();
        } catch (Exception e) {
            LOG.error(e.getMessage());

        }
    }

    public List<ServiceEndpoint<MonaLisaRouteRequestMessage, MonaLisaRouteRequestReply>> getMonaLisaSTCCList() {
        return monaLisaSTCCList;
    }

    public void sendMonaLisaAck(long addressMMSI, long id, long ownMMSI, boolean ack) {
        String mmsiStr = "mmsi://" + addressMMSI;
        
        System.out.println(mmsiStr);
        System.out.println(ownMMSI);

        ServiceEndpoint<MonaLisaRouteAckMsg, Void> end = null;

        getMonaLisaRouteAckList();
        
        for (int i = 0; i < monaLisaRouteAckList.size(); i++) {
            System.out.println(monaLisaRouteAckList.get(i).getId().toString());
            
//            if (monaLisaRouteAckList.get(i).getId().toString().equals(mmsiStr)) {
                end = monaLisaRouteAckList.get(i);
                // break;
                
//            }
        }

        MonaLisaRouteAckMsg msg = new MonaLisaRouteAckMsg(ack, id, ownMMSI);

        if (end != null) {

            // ConnectionFuture<Void> f =
            end.invoke(msg);
        } else {
            System.out.println("Failed to send ack " + monaLisaRouteAckList.size());
        }
    }

    public void sendMonaLisaRouteRequest(MonaLisaRouteRequestMessage routeMessage) {

        ServiceEndpoint<MonaLisaRouteService.MonaLisaRouteRequestMessage, MonaLisaRouteService.MonaLisaRouteRequestReply> end = null;

        
        //How to determine which to send to?
        for (int i = 0; i < monaLisaSTCCList.size(); i++) {
            end = monaLisaSTCCList.get(i);

        }

        // Each request has a unique ID, talk to Kasper?

        if (end != null) {
            ConnectionFuture<MonaLisaRouteService.MonaLisaRouteRequestReply> f = end
                    .invoke(routeMessage);

            f.handle(new BiConsumer<MonaLisaRouteService.MonaLisaRouteRequestReply, Throwable>() {

                @Override
                public void accept(MonaLisaRouteRequestReply l, Throwable r) {
                    replyRecieved(l);
                }
            });

        } else {
            // notifyRouteExchangeListeners();
            System.out.println("Failed to send?");
            // replyRecieved(f.get());
        }

    }

    private void replyRecieved(MonaLisaRouteRequestReply reply) {
        System.out.println("Mona Lisa Reply recieved: " + reply.getStatus());
        
        
        monaLisaHandler.handleReply(reply);
        
        
    }
    
    
    
    
    
    private void monaLisaRouteRequestListener() throws InterruptedException {

        connection
                .serviceRegister(
                        MonaLisaRouteService.INIT,
                        new InvocationCallback<MonaLisaRouteService.MonaLisaRouteRequestMessage, MonaLisaRouteService.MonaLisaRouteRequestReply>() {
                            public void process(
                                    MonaLisaRouteRequestMessage message,
                                    InvocationCallback.Context<MonaLisaRouteService.MonaLisaRouteRequestReply> context) {

                                monaLisaContext = context;
                                
                                System.out.println("Ship received a request for reopening a transaction!");
                                
                                //Does transaction exist?
                                
                                //If not, recreate as much as possible and open
                                
                                //Start new transaction with the end result
                                
                                
                                
                                
                                
                                
//                                // long mmsi = message.getMmsi();
//                                contextSenders.put(message.getId(), context);
//
//                                // if
//                                // (EPDShore.getAisHandler().getVesselTargets()
//                                // .containsKey(mmsi)) {
//
//                                System.out
//                                        .println("Recieved a message with id "
//                                                + message.getId());
//
//                                monaLisaHandler.handleMessage(message);

                            }
                        }).awaitRegistered(4, TimeUnit.SECONDS);
    }

}

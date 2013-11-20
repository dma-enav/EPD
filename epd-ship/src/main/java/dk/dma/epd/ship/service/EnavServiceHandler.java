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
import java.util.Date;
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
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.AIS_STATUS;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteAck;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteAck.StrategicRouteAckMsg;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestReply;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.CLOUD_STATUS;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.VOCTCommunicationMessage;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.VOCTCommunicationReply;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.status.CloudStatus;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.route.strategic.RecievedRoute;
import dk.dma.epd.ship.route.strategic.StrategicRouteExchangeHandler;
import dk.dma.epd.ship.service.intendedroute.ActiveRouteProvider;
import dk.dma.epd.ship.service.intendedroute.IntendedRouteService;
import dk.dma.epd.ship.service.voct.VOCTManager;
import dk.dma.epd.ship.settings.EPDEnavSettings;
import dk.dma.navnet.client.MaritimeNetworkConnectionBuilder;

/**
 * Component offering e-Navigation services
 */
public class EnavServiceHandler extends MapHandlerChild implements
        IPntDataListener, Runnable, IStatusComponent {

    private static final Logger LOG = LoggerFactory
            .getLogger(EnavServiceHandler.class);

    private String hostPort;
    private ShipId shipId;
    private PntHandler gpsHandler;
    private AisHandler aisHandler;
    private StrategicRouteExchangeHandler monaLisaHandler;
    private VOCTManager voctManager;

    private InvocationCallback.Context<RouteSuggestionService.RouteSuggestionReply> context;

    InvocationCallback.Context<StrategicRouteService.StrategicRouteRequestReply> monaLisaContext;

    private InvocationCallback.Context<VOCTCommunicationService.VOCTCommunicationReply> voctContext;

    protected CloudStatus cloudStatus = new CloudStatus();

    // End point holders for Mona Lisa Route Exchange
    private List<ServiceEndpoint<StrategicRouteRequestMessage, StrategicRouteRequestReply>> monaLisaSTCCList = new ArrayList<>();
    private List<ServiceEndpoint<StrategicRouteAckMsg, Void>> monaLisaRouteAckList = new ArrayList<>();

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

                        cloudStatus.markCloudReception();
                        int id = Integer.parseInt(l.getId().toString()
                                .split("mmsi://")[1]);

                        updateIntendedRoute(id, r.getIntendedRoute());
                    }
                });
    }

    private void getMonaLisaRouteAckList() {
        cloudStatus.markCloudReception();

        try {
            monaLisaRouteAckList = connection
                    .serviceFind(StrategicRouteAck.INIT)
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

                                cloudStatus.markCloudReception();

                                setContext(context);

                                RecievedRoute recievedRoute = new RecievedRoute(
                                        message);

                                EPDShip.getRouteManager()
                                        .recieveRouteSuggestion(recievedRoute);

                            }
                        }).awaitRegistered(4, TimeUnit.SECONDS);
    }

    private void voctMessageListener() throws InterruptedException {
        System.out.println("VOCT Listener");
        connection
                .serviceRegister(
                        VOCTCommunicationService.INIT,
                        new InvocationCallback<VOCTCommunicationService.VOCTCommunicationMessage, VOCTCommunicationService.VOCTCommunicationReply>() {
                            public void process(
                                    VOCTCommunicationMessage message,
                                    InvocationCallback.Context<VOCTCommunicationService.VOCTCommunicationReply> context) {

                                System.out.println("Received SAR Payload!");
                                // SARModelData sarData = message.getSarData();

                                // RapidResponseModelData rapidResponseModelData
                                // = (RapidResponseModelData) sarData;

                                // RapidResponseDTO rapidResponseModelData =
                                // message.getSarData();

                                cloudStatus.markCloudReception();

                                voctContext = context;

                                voctManager.handleSARDataPackage(message);

                                voctContext.complete(new VOCTCommunicationReply(
                                        "Received", (long) 0, aisHandler
                                                .getOwnShip().getMmsi(),
                                        new Date().getTime(),
                                        CLOUD_STATUS.RECIEVED_ACCEPTED));

                                // RecievedRoute recievedRoute = new
                                // RecievedRoute(
                                // message);
                                //
                                // EPDShip.getRouteManager()
                                // .recieveRouteSuggestion(recievedRoute);

                            }

                        }).awaitRegistered(4, TimeUnit.SECONDS);
    }

    public void sendVOCTReply(CLOUD_STATUS recievedAccepted, long id,
            String message) {
        try {
            voctContext
                    .complete(new VOCTCommunicationService.VOCTCommunicationReply(
                            message, id, aisHandler.getOwnShip().getMmsi(),
                            System.currentTimeMillis(), recievedAccepted));
            cloudStatus.markSuccesfullSend();
        } catch (Exception e) {
            cloudStatus.markFailedSend();
            System.out.println("Failed to reply");
        }

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
            cloudStatus.markSuccesfullSend();
        } catch (Exception e) {
            cloudStatus.markFailedSend();
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
     * Send maritime message over cloud
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
        cloudStatus.markSuccesfullSend();
    }

    /**
     * Create the message bus
     */
    public void init() {
        LOG.info("Connecting to cloud server: " + hostPort + " with shipId "
                + shipId.getId());

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

            if (connection != null) {

                cloudStatus.markCloudReception();
                cloudStatus.markSuccesfullSend();
            }
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Failed to connect to server");
            cloudStatus.markFailedSend();
            cloudStatus.markFailedReceive();
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
    public void gpsDataUpdate(PntData gpsData) {
        // TODO give information to messageBus if valid position
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof RouteManager) {
            intendedRouteService = new IntendedRouteService(this,
                    (ActiveRouteProvider) obj);
            ((RouteManager) obj).addListener(intendedRouteService);
            ((RouteManager) obj).setIntendedRouteService(intendedRouteService);
        } else if (obj instanceof PntHandler) {
            this.gpsHandler = (PntHandler) obj;
            this.gpsHandler.addListener(this);
        } else if (obj instanceof AisHandler) {
            this.aisHandler = (AisHandler) obj;
        } else if (obj instanceof StrategicRouteExchangeHandler) {
            this.monaLisaHandler = (StrategicRouteExchangeHandler) obj;
        } else if (obj instanceof VOCTManager) {
            voctManager = (VOCTManager) obj;
        }
    }

    @Override
    public void run() {

        // For now ship id will be MMSI so we need to know
        // own ship information. Busy wait for it.

        while (true) {
            Util.sleep(10000);
            if (this.aisHandler != null) {
                VesselTarget ownShip = this.aisHandler.getOwnShip();
                if (ownShip != null) {
                    if (ownShip.getMmsi() > 0) {
                        shipId = ShipId
                                .create(Long.toString(ownShip.getMmsi()));
                        init();
                        if (connection != null) {

                            try {
                                intendedRouteListener();
                                routeExchangeListener();
                                monaLisaRouteRequestListener();

                                voctMessageListener();

                            } catch (Exception e) {
                                // e.printStackTrace();
                                System.out.println("Failed to setup listener");
                                cloudStatus.markFailedSend();
                                cloudStatus.markFailedReceive();
                            }

                            break;
                        }

                    }
                }
            }
        }

        while (true) {
            getSTCCList();
            getMonaLisaRouteAckList();
            cloudStatus.markCloudReception();
            Util.sleep(10000);
        }
    }

    public void start() {
        new Thread(this).start();
    }

    private void getSTCCList() {
        try {
            monaLisaSTCCList = connection
                    .serviceFind(StrategicRouteService.INIT)
                    .nearest(Integer.MAX_VALUE).get();

        } catch (Exception e) {
            LOG.error(e.getMessage());

        }
    }

    public List<ServiceEndpoint<StrategicRouteRequestMessage, StrategicRouteRequestReply>> getMonaLisaSTCCList() {
        return monaLisaSTCCList;
    }

    public void sendMonaLisaAck(long addressMMSI, long id, long ownMMSI,
            boolean ack, String message) {
        String mmsiStr = "mmsi://" + addressMMSI;

        System.out.println(mmsiStr);
        System.out.println(ownMMSI);

        ServiceEndpoint<StrategicRouteAckMsg, Void> end = null;

        getMonaLisaRouteAckList();

        for (int i = 0; i < monaLisaRouteAckList.size(); i++) {
            System.out.println(monaLisaRouteAckList.get(i).getId().toString());

            if (monaLisaRouteAckList.get(i).getId().toString()
                    .startsWith("mmsi://999")) {
                end = monaLisaRouteAckList.get(i);
                // break;

            }
        }

        StrategicRouteAckMsg msg = new StrategicRouteAckMsg(ack, id, ownMMSI,
                message);

        if (end != null) {

            // ConnectionFuture<Void> f =
            end.invoke(msg);
            cloudStatus.markSuccesfullSend();
        } else {
            System.out.println("Failed to send ack "
                    + monaLisaRouteAckList.size());
            cloudStatus.markFailedSend();
        }
    }

    public void sendMonaLisaRouteRequest(
            StrategicRouteRequestMessage routeMessage) {

        ServiceEndpoint<StrategicRouteService.StrategicRouteRequestMessage, StrategicRouteService.StrategicRouteRequestReply> end = null;

        // How to determine which to send to?
        for (int i = 0; i < monaLisaSTCCList.size(); i++) {

            if (!monaLisaSTCCList.get(i).getId().toString()
                    .contains(routeMessage.getMmsi() + "")) {
                end = monaLisaSTCCList.get(i);
            }
        }

        // Each request has a unique ID, talk to Kasper?

        if (end != null) {
            ConnectionFuture<StrategicRouteService.StrategicRouteRequestReply> f = end
                    .invoke(routeMessage);
            cloudStatus.markSuccesfullSend();
            f.handle(new BiConsumer<StrategicRouteService.StrategicRouteRequestReply, Throwable>() {

                @Override
                public void accept(StrategicRouteRequestReply l, Throwable r) {
                    replyRecieved(l);
                    cloudStatus.markCloudReception();
                }
            });

        } else {
            // notifyRouteExchangeListeners();
            cloudStatus.markFailedSend();
            System.out.println("Failed to send?");
        }

    }

    private void replyRecieved(StrategicRouteRequestReply reply) {
        System.out.println("Mona Lisa Reply recieved: " + reply.getStatus());

        monaLisaHandler.handleReply(reply);

    }

    private void monaLisaRouteRequestListener() throws InterruptedException {

        connection
                .serviceRegister(
                        StrategicRouteService.INIT,
                        new InvocationCallback<StrategicRouteService.StrategicRouteRequestMessage, StrategicRouteService.StrategicRouteRequestReply>() {
                            public void process(
                                    StrategicRouteRequestMessage message,
                                    InvocationCallback.Context<StrategicRouteService.StrategicRouteRequestReply> context) {

                                monaLisaContext = context;

                                System.out
                                        .println("Ship received a request for reopening a transaction!");

                                cloudStatus.markCloudReception();

                                monaLisaHandler.handleReNegotiation(message);
                                // Does transaction exist?

                                // If not, recreate as much as possible and open

                                // Start new transaction with the end result

                                // // long mmsi = message.getMmsi();
                                // contextSenders.put(message.getId(), context);
                                //
                                // // if
                                // //
                                // (EPDShore.getAisHandler().getVesselTargets()
                                // // .containsKey(mmsi)) {
                                //
                                // System.out
                                // .println("Recieved a message with id "
                                // + message.getId());
                                //
                                // monaLisaHandler.handleMessage(message);

                            }
                        }).awaitRegistered(4, TimeUnit.SECONDS);
    }

    @Override
    public ComponentStatus getStatus() {
        return cloudStatus;
    }

}

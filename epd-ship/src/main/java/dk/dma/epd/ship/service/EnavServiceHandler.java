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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import net.maritimecloud.net.ClosingCode;
import net.maritimecloud.net.ConnectionFuture;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.MaritimeCloudClientConfiguration;
import net.maritimecloud.net.MaritimeCloudConnection;
import net.maritimecloud.net.broadcast.BroadcastListener;
import net.maritimecloud.net.broadcast.BroadcastMessage;
import net.maritimecloud.net.broadcast.BroadcastMessageHeader;
import net.maritimecloud.net.service.ServiceEndpoint;
import net.maritimecloud.net.service.invocation.InvocationCallback;
import net.maritimecloud.util.function.BiConsumer;
import net.maritimecloud.util.geometry.PositionReader;
import net.maritimecloud.util.geometry.PositionTime;
import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.ship.ShipId;
import dk.dma.enav.model.voyage.Route;
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
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.status.CloudStatus;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.ownship.OwnShipHandler;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.route.strategic.RecievedRoute;
import dk.dma.epd.ship.route.strategic.StrategicRouteExchangeHandler;
import dk.dma.epd.ship.service.intendedroute.ActiveRouteProvider;
import dk.dma.epd.ship.service.intendedroute.IntendedRouteService;
import dk.dma.epd.ship.settings.EPDEnavSettings;


/**
 * Component offering e-Navigation services via the Maritime Cloud
 * <p>
 * Currently, three types of route handling is supported:
 * <ul>
 *   <li>Intended routes</li>
 *   <li>Suggested routes</li>
 *   <li>Strategic routes - a.k.a. Mona Lisa</li>
 * </ul>
 */
public class EnavServiceHandler extends MapHandlerChild implements
        IPntDataListener, Runnable, IStatusComponent {

    /** 
     * Set this flag to true, if you want to log all 
     * messages sent and received by the {@linkplain MaritimeCloudClient} 
     */
    private static final boolean LOG_MARITIME_CLOUD_ACTIVITY = false;
    
    private static final Logger LOG = LoggerFactory
            .getLogger(EnavServiceHandler.class);

    private String hostPort;
    private ShipId shipId;
    private PntHandler pntHandler;
    private AisHandler aisHandler;
    private OwnShipHandler ownShipHandler;
    private MaritimeCloudClient connection;
    protected CloudStatus cloudStatus = new CloudStatus();
    boolean stopped = true;

    // Intended route
    private IntendedRouteService intendedRouteService;
    
    // Route suggestion service
    private InvocationCallback.Context<RouteSuggestionService.RouteSuggestionReply> routeExchangeCallbackContext;

    // Mona Lisa 
    private StrategicRouteExchangeHandler monaLisaHandler;
    InvocationCallback.Context<StrategicRouteService.StrategicRouteRequestReply> monaLisaContext;
    private List<ServiceEndpoint<StrategicRouteRequestMessage, StrategicRouteRequestReply>> monaLisaSTCCList = new ArrayList<>();
    private List<ServiceEndpoint<StrategicRouteAckMsg, Void>> monaLisaRouteAckList = new ArrayList<>();
    
    /**
     * Constructor 
     * 
     * @param enavSettings the e-navigation settings
     */
    public EnavServiceHandler(EPDEnavSettings enavSettings) {
        readEnavSettings(enavSettings);
    }

    /**
     * Reads the e-Nav settings
     * @param enavSettings the e-Nav settings
     */
    private void readEnavSettings(EPDEnavSettings enavSettings) {
        this.hostPort = String.format("%s:%d",
                enavSettings.getCloudServerHost(),
                enavSettings.getCloudServerPort());
    }
    
    /**
     * Returns a reference to the cloud client connection
     * @return a reference to the cloud client connection
     */
    public MaritimeCloudClient getConnection() {
        return connection;
    }

    /*********************************/
    /** Life cycle functionality    **/
    /*********************************/

    /**
     * Starts the Maritime cloud client
     */
    public void start() {
        if (!stopped) {
            return;
        }
        // Update the eNav settings
        readEnavSettings(EPDShip.getInstance().getSettings().getEnavSettings());
        stopped = false;
        new Thread(this).start();
    }
    
    /**
     * Stops the Maritime cloud client
     */
    public synchronized void stop() {
        if (stopped) {
            return;
        }
        
        this.stopped = true;
        if (connection != null) {
            try {
                connection.close();
                connection.awaitTermination(2, TimeUnit.SECONDS);
            } catch (Exception e) {
                LOG.error("Error terminating cloud connection");
            }
            connection = null;
        }
    }
    
    /**
     * Thread run method
     */
    @Override
    public void run() {

        // For now ship id will be MMSI so we need to know
        // own ship information. Busy wait for it.

        while (!stopped) {
            Util.sleep(10000);
            if (this.ownShipHandler != null) {
                if (ownShipHandler.getMmsi() != null) {
                    shipId = ShipId
                            .create(Long.toString(ownShipHandler.getMmsi()));
                    init();
                    if (connection != null) {

                        try {
                            intendedRouteListener();
                            routeExchangeListener();
                            monaLisaRouteRequestListener();
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

        while (!stopped) {
            getSTCCList();
            getMonaLisaRouteAckList();
            cloudStatus.markCloudReception();
            Util.sleep(10000);
        }
    }

    /**
     * Create the message bus
     */
    public void init() {
        LOG.info("Connecting to cloud server: " + hostPort
                + " with shipId " + shipId.getId());

        // enavCloudConnection =
        // MaritimeNetworkConnectionBuilder.create("mmsi://"+shipId.getId());
        MaritimeCloudClientConfiguration enavCloudConnection = MaritimeCloudClientConfiguration
                .create("mmsi://" + shipId.getId());

        enavCloudConnection.setPositionReader(new PositionReader() {
            @Override
            public PositionTime getCurrentPosition() {
                Position pos = pntHandler.getCurrentData().getPosition();
                if (pos != null) {
                    return PositionTime.create(pos.getLatitude(), pos.getLongitude(),
                            System.currentTimeMillis());
                } else {
                    return PositionTime.create(0.0, 0.0,
                            System.currentTimeMillis());
                }

            }});
        
        // Check if we need to log the MaritimeCloudConnection activity
        if (LOG_MARITIME_CLOUD_ACTIVITY) {
            enavCloudConnection.addListener(new MaritimeCloudConnection.Listener() {
                @Override
                public void messageReceived(String message) {
                    LOG.info("Received:" + message);
                }
    
                @Override
                public void messageSend(String message) {
                    LOG.info("Sending :" + message);
                }
                
                @Override
                public void connecting(URI host) {
                    LOG.info("Connecting to host :" + host);
                }

                @Override
                public void disconnected(ClosingCode closeReason) {
                    LOG.info("Disconnecting from cloud :" + closeReason);
                }
            });
        }
        
        try {
            enavCloudConnection.setHost(hostPort);
            connection = enavCloudConnection.build();

            if (connection != null) {

                cloudStatus.markCloudReception();
                cloudStatus.markSuccesfullSend();
            }
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Failed to connect to server: " + e);
            cloudStatus.markFailedSend();
            cloudStatus.markFailedReceive();
        }

        LOG.info("Started succesfull cloud server: " + hostPort
                + " with shipId " + shipId.getId());

    }

    /**
     * Receive position updates
     */
    @Override
    public void pntDataUpdate(PntData pntData) {
        // TODO give information to messageBus if valid position
    }

    /**
     * Receive position updates from the {@linkplain PntHandler}
     * @param pnt the updated PNT data
     */
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof RouteManager) {
            intendedRouteService = new IntendedRouteService(this,
                    (ActiveRouteProvider) obj);
            ((RouteManager) obj).addListener(intendedRouteService);
            ((RouteManager) obj).setIntendedRouteService(intendedRouteService);
        } else if (obj instanceof PntHandler) {
            this.pntHandler = (PntHandler) obj;
            this.pntHandler.addListener(this);
        } else if (obj instanceof AisHandler) {
            this.aisHandler = (AisHandler) obj;
        } else if (obj instanceof OwnShipHandler) {
            this.ownShipHandler = (OwnShipHandler) obj;
        } else if (obj instanceof StrategicRouteExchangeHandler) {
            this.monaLisaHandler = (StrategicRouteExchangeHandler) obj;
        }
    }

    @Override
    public ComponentStatus getStatus() {
        return cloudStatus;
    }

    /*********************************/
    /** Intended route handling     **/
    /*********************************/
    
    /**
     * Register a cloud broadcast listener that listens for intended routes
     */
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

    /**
     * Update intended route of vessel target
     * 
     * @param mmsi
     * @param routeData
     */
    private synchronized void updateIntendedRoute(long mmsi, Route routeData) {

        // Try to find exiting target
        VesselTarget vesselTarget = aisHandler.getVesselTarget(mmsi);
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
    public void sendIntendedRouteMessage(BroadcastMessage message) throws Exception {

        // if connection.
        EnavCloudSendThread sendThread = new EnavCloudSendThread(message,
                connection);

        // Send it in a seperate thread
        sendThread.start();
        cloudStatus.markSuccesfullSend();
    }
    
    /*********************************/
    /** Suggested route handling    **/
    /*********************************/

    /**
     * Register a cloud route suggestion service
     */
    private void routeExchangeListener() throws InterruptedException {

        connection
                .serviceRegister(
                        RouteSuggestionService.INIT,
                        new InvocationCallback<RouteSuggestionService.RouteSuggestionMessage, RouteSuggestionService.RouteSuggestionReply>() {
                            public void process(
                                    RouteSuggestionMessage message,
                                    InvocationCallback.Context<RouteSuggestionService.RouteSuggestionReply> context) {

                                cloudStatus.markCloudReception();

                                routeExchangeCallbackContext = context;

                                RecievedRoute recievedRoute = new RecievedRoute(
                                        message);

                                EPDShip.getInstance().getRouteManager()
                                        .recieveRouteSuggestion(recievedRoute);

                            }
                        }).awaitRegistered(4, TimeUnit.SECONDS);
    }

    /**
     * Sends a reply to route suggestion reply
     * 
     * @param receivedAccepted the reply
     * @param id the ID of the route suggestion
     * @param message a message to send along with the reply
     */
    public void sendRouteExchangeReply(AIS_STATUS recievedAccepted, long id, String message) {
        try {
            long ownMmsi = ownShipHandler.getMmsi() == null ? -1L : ownShipHandler.getMmsi();
            routeExchangeCallbackContext.complete(new RouteSuggestionService.RouteSuggestionReply(
                    message, id, ownMmsi, System.currentTimeMillis(), recievedAccepted));
            cloudStatus.markSuccesfullSend();
        } catch (Exception e) {
            cloudStatus.markFailedSend();
            System.out.println("Failed to reply");
        }

    }
    
    /*********************************/
    /** Mona Lisa route handling    **/
    /*********************************/

    /**
     * Register a Mona Lisa strategic route service listener
     */
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
                            }
                        }).awaitRegistered(4, TimeUnit.SECONDS);
    }
    
    /**
     * Fetch list of Mona Lisa route ack's
     */
    private void getMonaLisaRouteAckList() {
        cloudStatus.markCloudReception();

        try {
            monaLisaRouteAckList = connection
                    .serviceLocate(StrategicRouteAck.INIT)
                    .nearest(Integer.MAX_VALUE).get();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * Fetches the list of Sea Traffic Control Centers
     */
    private void getSTCCList() {
        try {
            monaLisaSTCCList = connection
                    .serviceLocate(StrategicRouteService.INIT)
                    .nearest(Integer.MAX_VALUE).get();

        } catch (Exception e) {
            LOG.error(e.getMessage());

        }
    }

    /**
     * Returns the current list of Sea Traffic Control Centers
     * @return the current list of Sea Traffic Control Centers
     */
    public List<ServiceEndpoint<StrategicRouteRequestMessage, StrategicRouteRequestReply>> getMonaLisaSTCCList() {
        return monaLisaSTCCList;
    }

    /**
     * Sends a Mona Lisa Ack message
     * 
     * @param addressMMSI the mmsi of the route
     * @param id the route id
     * @param ownMMSI own mmsi
     * @param ack acknowledged or rejected
     * @param message an additional message
     */
    public void sendMonaLisaAck(long addressMMSI, long id, long ownMMSI, boolean ack, String message) {
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

    public void sendMonaLisaRouteRequest(StrategicRouteRequestMessage routeMessage) {

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
}

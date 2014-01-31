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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;
import com.bbn.openmap.proj.coords.LatLonPoint;

import net.maritimecloud.net.ClosingCode;
import net.maritimecloud.net.ConnectionFuture;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.MaritimeCloudClientConfiguration;
import net.maritimecloud.net.MaritimeCloudConnection;
import net.maritimecloud.net.broadcast.BroadcastListener;
import net.maritimecloud.net.broadcast.BroadcastMessageHeader;
import net.maritimecloud.net.service.ServiceEndpoint;
import net.maritimecloud.net.service.invocation.InvocationCallback;
import net.maritimecloud.net.service.invocation.InvocationCallback.Context;
import net.maritimecloud.util.function.BiConsumer;
import net.maritimecloud.util.geometry.PositionReader;
import net.maritimecloud.util.geometry.PositionTime;
import dk.dma.enav.model.ship.ShipId;
import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.enavcloud.CloudIntendedRoute;
import dk.dma.epd.common.prototype.enavcloud.EnavRouteBroadcast;
import dk.dma.epd.common.prototype.enavcloud.EnavCloudUtils;
import dk.dma.epd.common.prototype.enavcloud.InvocationCallbackContextMap;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionStatus;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionReply;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteAck;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteAck.StrategicRouteAckMsg;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestReply;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.settings.EPDEnavSettings;


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
public class EnavServiceHandler extends MapHandlerChild implements Runnable {

    /** 
     * Set this flag to true, if you want to log all 
     * messages sent and received by the {@linkplain MaritimeCloudClient} 
     */
    private static final boolean LOG_MARITIME_CLOUD_ACTIVITY = false;
    private static final long CALLBACK_TTL = 1 * 60 * 60; // 60 minutes
    
    private static final Logger LOG = LoggerFactory.getLogger(EnavServiceHandler.class);

    private String hostPort;
    private ShipId shipId;
    private long ownMMSI;
    private LatLonPoint shorePos = new LatLonPoint.Double(0.0, 0.0);
    
    private AisHandler aisHandler;
    MaritimeCloudClient connection;
    boolean stopped = true;
    
    RouteSuggestionDataStructure<RouteSuggestionKey, RouteSuggestionData> routeSuggestions = new RouteSuggestionDataStructure<>();
    protected Set<RouteExchangeListener> routeExchangeListener = new HashSet<RouteExchangeListener>();
    private List<ServiceEndpoint<RouteSuggestionMessage, RouteSuggestionReply>> routeSuggestionServiceList = new ArrayList<>();

    private StrategicRouteExchangeHandler monaLisaHandler;
    InvocationCallbackContextMap<Long, Context<StrategicRouteRequestReply>> monaLisaContexts = new InvocationCallbackContextMap<>(CALLBACK_TTL);
    List<ServiceEndpoint<StrategicRouteRequestMessage, StrategicRouteRequestReply>> monaLisaShipList = new ArrayList<>();

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
        hostPort = String.format("%s:%d",
                enavSettings.getCloudServerHost(),
                enavSettings.getCloudServerPort());

        // Determine the shore ID
        String shoreID = (String) enavSettings.getShoreId().subSequence(0, 9);
        shipId = ShipId.create(shoreID);
        ownMMSI = Long.parseLong(shoreID);
        
        shorePos = enavSettings.getShorePos();
    }
    
    public MaritimeCloudClient getConnection() {
        return connection;
    }

    /**
     * @return the ownMMSI
     */
    public long getOwnMMSI() {
        return ownMMSI;
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
        // Update the e-Nav settings
        readEnavSettings(EPDShore.getInstance().getSettings().getEnavSettings());
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

        Util.sleep(1000);
        
        init();
        try {
            listenToIntendedRouteBroadcasts();
            monaLisaRouteRequestListener();
            listenToMoneLisaAck();
        } catch (Exception e) {
            // Exception for virtual net
            System.out.println("An exception occured trying to listen to broadcasts, possibly connection issue");
        }

        while (!stopped) {
            fetchRouteSuggestionServiceList();

            // Clean up cached invocation callback contexts
            monaLisaContexts.cleanup();
            
            Util.sleep(10000);
        }
    }

    /**
     * Create the message bus
     */
    public void init() {
        LOG.info("Connecting to enav cloud server: " + hostPort + " with shipId " + shipId.getId());

        MaritimeCloudClientConfiguration enavCloudConnection = 
                MaritimeCloudClientConfiguration.create(EnavCloudUtils.toMaritimeId(shipId.getId()));

        enavCloudConnection.setPositionReader(new PositionReader() {
            @Override
            public PositionTime getCurrentPosition() {
                return PositionTime.create(shorePos.getLatitude(), shorePos.getLongitude(), System.currentTimeMillis());
            }
        });

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
        
        while (!stopped && connection == null) {
            try {
                enavCloudConnection.setHost(hostPort);
                System.out.println(hostPort);
                connection = enavCloudConnection.build();
                break;
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("Failed to connect");
                Util.sleep(10000);
            }

        }
        LOG.info("Started succesfull cloud server: " + hostPort + " with shipId " + shipId.getId());

    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof AisHandler) {
            this.aisHandler = (AisHandler) obj;
        } else if (obj instanceof StrategicRouteExchangeHandler) {
            this.monaLisaHandler = (StrategicRouteExchangeHandler) obj;
        }
    }


    /*********************************/
    /** Intended route handling     **/
    /*********************************/
    
    /**
     * Listener for intended routes broadcasts
     */
    private void listenToIntendedRouteBroadcasts() throws InterruptedException {
        connection.broadcastListen(EnavRouteBroadcast.class, new BroadcastListener<EnavRouteBroadcast>() {
            public void onMessage(BroadcastMessageHeader l, EnavRouteBroadcast r) {
                int id = EnavCloudUtils.toMmsi(l.getId());
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

        LOG.debug("Intended route received");

        // Try to find exiting target
        VesselTarget vesselTarget = aisHandler.getVesselTarget(mmsi);
        // If not exists, wait for it to be created by position report
        if (vesselTarget == null) {
            return;
        }

        CloudIntendedRoute intendedRoute = new CloudIntendedRoute(routeData);

        // Update intended route
        vesselTarget.setCloudRouteData(intendedRoute);
        aisHandler.publishUpdate(vesselTarget);
    }

    /*********************************/
    /** Suggested route handling    **/
    /*********************************/
    
    /**
     * Fetches the route suggestion service list
     */
    private void fetchRouteSuggestionServiceList() {
        try {
            routeSuggestionServiceList = connection.serviceLocate(RouteSuggestionService.INIT).nearest(Integer.MAX_VALUE).get();
        } catch (Exception e) {
            LOG.error(e.getMessage());

        }
    }

    /**
     * Returns the route suggestion service list
     * @return the route suggestion service list
     */
    public List<ServiceEndpoint<RouteSuggestionMessage, RouteSuggestionReply>> getRouteSuggestionServiceList() {
        return routeSuggestionServiceList;
    }

    /**
     * Checks for a ship with the given mmsi in the route suggestion service list
     * 
     * @param mmsi the mmsi of the ship to search for
     * @return if one such ship is available
     */
    public boolean shipAvailableForRouteSuggestion(long mmsi) {
        return EnavCloudUtils.findServiceWithMmsi(routeSuggestionServiceList, (int)mmsi) != null;
    }

    /**
     * Sends a route suggestion to the given ship
     * 
     * @param mmsi the mmsi of the ship
     * @param route the route
     * @param sender the sender
     * @param message an additional message
     */
    public void sendRouteSuggestion(long mmsi, Route route, String sender, String message) throws InterruptedException,
            ExecutionException, TimeoutException {

        // System.out.println("Send to : " + mmsi);
        ServiceEndpoint<RouteSuggestionMessage, RouteSuggestionReply> end 
            = EnavCloudUtils.findServiceWithMmsi(routeSuggestionServiceList, (int)mmsi);

        RouteSuggestionMessage routeMessage = new RouteSuggestionMessage(route, sender, message);

        System.out.println("Sending to mmsi: " + mmsi + " with ID: " + routeMessage.getId());

        RouteSuggestionData suggestionData = new RouteSuggestionData(routeMessage, null, routeMessage.getId(), mmsi, false,
                RouteSuggestionStatus.RECEIVED_APP_ACK);
        RouteSuggestionKey routeSuggestionKey = new RouteSuggestionKey(mmsi, routeMessage.getId());
        routeSuggestions.put(routeSuggestionKey, suggestionData);

        if (end != null) {
            ConnectionFuture<RouteSuggestionReply> f = end.invoke(routeMessage);

            notifyRouteExchangeListeners();

            f.handle(new BiConsumer<RouteSuggestionReply, Throwable>() {
                @Override
                public void accept(RouteSuggestionReply l, Throwable r) {
                    routeSuggestionReplyReceived(l);
                }
            });

        } else {
            // notifyRouteExchangeListeners();
            System.out.println("Failed to send");
        }

    }

    /**
     * Returns the sent route suggestions
     * @return the sent route suggestions
     */
    public RouteSuggestionDataStructure<RouteSuggestionKey, RouteSuggestionData> getRouteSuggestions() {
        return routeSuggestions;
    }

    /**
     * Add a listener to the asService
     * 
     * @param listener
     */
    public synchronized void addRouteExchangeListener(RouteExchangeListener listener) {
        routeExchangeListener.add(listener);
    }

    /**
     * Broadcast a route update to all listeners
     */
    protected synchronized void notifyRouteExchangeListeners() {

        for (RouteExchangeListener listener : routeExchangeListener) {
            listener.routeUpdate();
        }
    }

    /**
     * Flags that the route suggestion ith the given mmsi and id has been acknowledged
     * 
     * @param mmsi the mmsi associated with the route suggestion
     * @param id the id of the route suggestion
     */
    public void setRouteSuggestionAcknowledged(long mmsi, long id) {
        routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setAcknowleged(true);
        notifyRouteExchangeListeners();
    }

    /**
     * Removes the route suggestion with the given mmsi and id
     * 
     * @param mmsi the mmsi associated with the route suggestion
     * @param id the id of the route suggestion
     */
    public void removeSuggestion(long mmsi, long id) {
        routeSuggestions.remove(new RouteSuggestionKey(mmsi, id));
        notifyRouteExchangeListeners();
    }

    /**
     * Returns the number of route suggestions that have not been acknowledged
     * @return the number of route suggestions that have not been acknowledged
     */
    public int getUnacknowledgedRouteSuggestions() {

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

    /**
     * Called when a route suggestion reply has been received
     * @param message the reply
     */
    private void routeSuggestionReplyReceived(RouteSuggestionReply message) {

        System.out.println("MSG Received from MMSI: " + message.getMmsi() + " and ID " + message.getId());

        if (routeSuggestions.containsKey(new RouteSuggestionKey(message.getMmsi(), message.getId()))) {

            RouteSuggestionStatus response = message.getStatus();

            long mmsi = message.getMmsi();
            long id = message.getId();

            routeSuggestions.get(new RouteSuggestionKey(message.getMmsi(), message.getId())).setReply(message);

            switch (response) {
            case RECEIVED_ACCEPTED:
                if (routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).getStatus() != RouteSuggestionStatus.RECEIVED_ACCEPTED) {
                    // Accepted
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setStatus(RouteSuggestionStatus.RECEIVED_ACCEPTED);
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setAcknowleged(false);
                    notifyRouteExchangeListeners();
                }

                break;
            case RECEIVED_REJECTED:
                // Rejected
                if (routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).getStatus() != RouteSuggestionStatus.RECEIVED_REJECTED) {
                    // Accepted
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setStatus(RouteSuggestionStatus.RECEIVED_REJECTED);
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setAcknowleged(false);
                    notifyRouteExchangeListeners();
                }
                break;
            case RECEIVED_NOTED:
                // Noted
                if (routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).getStatus() != RouteSuggestionStatus.RECEIVED_NOTED) {
                    // Accepted
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setStatus(RouteSuggestionStatus.RECEIVED_NOTED);
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setAcknowleged(false);
                    notifyRouteExchangeListeners();
                }
                break;
            default:
                break;
            }
        }

    }

    /*********************************/
    /** Mona Lisa route handling    **/
    /*********************************/
    
    /**
     * Register a Mona Lisa strategic route service
     */
    private void monaLisaRouteRequestListener() throws InterruptedException {

        connection
                .serviceRegister(
                        StrategicRouteService.INIT,
                        new InvocationCallback<StrategicRouteRequestMessage, StrategicRouteRequestReply>() {
                            public void process(StrategicRouteRequestMessage message,
                                    Context<StrategicRouteRequestReply> context) {

                                // long mmsi = message.getMmsi();
                                monaLisaContexts.put(message.getId(), context);

                                monaLisaHandler.handleMessage(message);
                            }
                        }).awaitRegistered(4, TimeUnit.SECONDS);
    }

    /**
     * Register a Mona Lisa strategic route acknowledge service
     */
    private void listenToMoneLisaAck() throws InterruptedException {
        connection.serviceRegister(StrategicRouteAck.INIT, new InvocationCallback<StrategicRouteAckMsg, Void>() {
            @Override
            public void process(StrategicRouteAckMsg message,
                    Context<Void> context) {

                monaLisaHandler.handleSingleAckMsg(message);

            }
        }).awaitRegistered(4, TimeUnit.SECONDS);
    }

    /**
     * Sends a Mona Lisa reply
     * @param reply the reply to send
     */
    public void sendReply(StrategicRouteService.StrategicRouteRequestReply reply) {
        try {

            if (monaLisaContexts.containsKey(reply.getId())) {
                System.out.println("Sending");
                monaLisaContexts.remove(reply.getId()).complete(reply);
            } else {
                LOG.error("No Mona Lisa context found for route request " + reply.getId());
            }

        } catch (Exception e) {
            System.out.println("Failed to reply");
        }

    }

    /**
     * Sends a Mona Lisa request to the given ship
     * 
     * @param mmsiDestination the destination mmsi
     * @param routeMessage the strategic route to send
     */
    public void sendStrategicRouteRequest(long mmsiDestination, StrategicRouteRequestMessage routeMessage) {

        ServiceEndpoint<StrategicRouteRequestMessage, StrategicRouteRequestReply> end 
            = EnavCloudUtils.findServiceWithMmsi(monaLisaShipList, (int)mmsiDestination);

        // Each request has a unique ID, talk to Kasper?

        if (end != null) {
            end.invoke(routeMessage);

            // we don't need to handle a reply, a new one will be sent as a
            // normal transaction

        } else {
            System.out.println("Failed to send?");
        }

    }

    /**
     * Fetches the list of ships with a Mona Lisa service
     */
    private void fetchMonaLisaShipList() {
        try {
            monaLisaShipList = connection.serviceLocate(StrategicRouteService.INIT).nearest(Integer.MAX_VALUE).get();

        } catch (Exception e) {
            LOG.error(e.getMessage());

        }
    }

    /**
     * Checks if the ship with the given mmsi has a Mona Lisa service
     * @param mmsi the mmsi of the ship
     * @return if the given ship has a Mona Lisa srvice
     */
    public boolean shipAvailableForMonaLisaTransaction(long mmsi) {
        fetchMonaLisaShipList();
        
        return EnavCloudUtils.findServiceWithMmsi(monaLisaShipList, (int)mmsi) != null;
    }
}

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

import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.broadcast.BroadcastOptions;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.enavcloud.IntendedRouteBroadcast;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.PartialRouteFilter;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.route.RouteManager;

/**
 * Ship specific intended route service implementation.
 * <p>
 * Listens for changes to the active route and broadcasts it. Also broadcasts the route periodically.
 * <p>
 * Improvements:
 * <ul>
 * <li>Use a worker pool rather than spawning a new thread for each broadcast.</li>
 * </ul>
 */
public class IntendedRouteHandler extends IntendedRouteHandlerCommon implements IRoutesUpdateListener, Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(IntendedRouteHandler.class);
    private static final long BROADCAST_TIME = 60; // Broadcast intended route every minute for now
    private static final long ADAPTIVE_TIME = 60 * 10;    // Set to 10 minutes?
    private static final int BROADCAST_RADIUS = Integer.MAX_VALUE;

    private DateTime lastTransmitActiveWp;
    private DateTime lastSend = new DateTime(1);
    private RouteManager routeManager;
    private boolean running;

    /**
     * Constructor
     */
    public IntendedRouteHandler() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        // Let super hook up for intended route broadcasts from other vessels
        super.cloudConnected(connection);

        // Start broadcasting our own active route
        running = true;
        new Thread(this).start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudDisconnected() {
        running = false;
    }

    /**
     * Main thread run method. Broadcasts the intended route
     */
    public void run() {

        // Initialize first send
        // lastSend = new DateTime();
        // broadcastIntendedRoute();

        while (running) {

            if (routeManager != null) {

                // We have no active route, keep sleeping
                if (routeManager.getActiveRoute() == null) {
                    Util.sleep(BROADCAST_TIME * 1000L);
                } else {

                    // Here we handle the periodical broadcasts
                    DateTime calculatedTimeOfLastSend = new DateTime();
                    calculatedTimeOfLastSend = calculatedTimeOfLastSend.minus(BROADCAST_TIME * 1000L);

                    // Do we need to rebroadcast based on the broadcast time setting
                    if (calculatedTimeOfLastSend.isAfter(lastSend)) {
                        System.out.println("Periodically rebroadcasting");
                        broadcastIntendedRoute();
                        lastSend = new DateTime();
                    } else if (lastTransmitActiveWp != null) {

                        // We check for the adaptive route broadcast here
                        // We need to compare lastTransmitActiveWp which is the last stored
                        // ETA of the waypoint we sent to the current one
                        DateTime currentActiveWaypointETA = new DateTime(routeManager.getActiveRoute().getActiveWaypointEta());

                        // System.out.println("The ETA at last transmission was : " + lastTransmitActiveWp);
                        // System.out.println("It is now                        : " + currentActiveWaypointETA);

                        // //It can either be before or after
                        //
                        if (currentActiveWaypointETA.isAfter(lastTransmitActiveWp)
                                || currentActiveWaypointETA.isBefore(lastTransmitActiveWp)) {

                            long etaTimeChange;

                            // Is it before?
                            if (currentActiveWaypointETA.isAfter(lastTransmitActiveWp)) {

                                etaTimeChange = currentActiveWaypointETA.minus(lastTransmitActiveWp.getMillis()).getMillis();

                                // Must be after
                            } else {
                                etaTimeChange = currentActiveWaypointETA.plus(lastTransmitActiveWp.getMillis()).getMillis();
                            }

                            if (etaTimeChange > ADAPTIVE_TIME * 1000L) {
                                System.out.println("Broadcast based on adaptive time!");
                                broadcastIntendedRoute();
                                lastSend = new DateTime();
                            }

                            // System.out.println("ETA has changed with " + etaTimeChange + " mili seconds" );

                        }
                

                    }

                    Util.sleep(1000L);
                    
                }
            }

        }

    }

    /**
     * Broadcast intended route
     */
    public void broadcastIntendedRoute() {
        System.out.println("Broadcast intended route");
        // Sanity check
        if (!running || routeManager == null || getMaritimeCloudConnection() == null) {
            return;
        }

        // Make intended route message
        final IntendedRouteBroadcast message = new IntendedRouteBroadcast();

        if (routeManager.getActiveRoute() != null) {
            PartialRouteFilter filter = EPDShip.getInstance().getSettings().getCloudSettings().getIntendedRouteFilter();
            routeManager.getActiveRoute().getPartialRouteData(filter, message);

            lastTransmitActiveWp = new DateTime(routeManager.getActiveRoute().getActiveWaypointEta());

        } else {
            message.setIntendedRoute(new Route());
            message.setOriginalEtas(new ArrayList<Date>());
        }

        // send message
        LOG.debug("Broadcasting intended route");
        
        submitIfConnected(new Runnable() {                
            @Override  public void run() {
                BroadcastOptions options = new BroadcastOptions();
                options.setBroadcastRadius(BROADCAST_RADIUS);
                getMaritimeCloudConnection().broadcast(message, options);
                getStatus().markSuccesfullSend();
            }
        });
    }

    /**
     * Handle event of active route change
     */
    @Override
    public void routesChanged(RoutesUpdateEvent e) {
        if (e != null) {
            if (e.is(RoutesUpdateEvent.ACTIVE_ROUTE_UPDATE, RoutesUpdateEvent.ACTIVE_ROUTE_FINISHED,
                    RoutesUpdateEvent.ROUTE_ACTIVATED, RoutesUpdateEvent.ROUTE_DEACTIVATED)) {

                lastSend = new DateTime();
                broadcastIntendedRoute();

            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (obj instanceof RouteManager) {
            routeManager = (RouteManager) obj;
            routeManager.addListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndUndo(Object obj) {
        if (obj instanceof RouteManager) {
            routeManager.removeListener(this);
            routeManager = null;
        }
        super.findAndUndo(obj);
    }

}

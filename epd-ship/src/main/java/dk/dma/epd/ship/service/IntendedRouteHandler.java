/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.ship.service;

import dk.dma.epd.common.prototype.model.intendedroute.FilteredIntendedRoute;
import dk.dma.epd.common.prototype.model.intendedroute.FilteredIntendedRoutes;
import dk.dma.epd.common.prototype.model.intendedroute.IntendedRouteFilterMessage;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.model.route.PartialRouteFilter;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;
import dk.dma.epd.common.prototype.settings.EnavSettings;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.layers.intendedroute.IntendedRouteLayer;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.settings.handlers.IIntendedRouteHandlerSettingsObserver;
import dma.route.MCIntendedRouteBroadcast;
import net.maritimecloud.mms.MmsClient;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Ship specific intended route service implementation.
 * <p>
 * Listens for changes to the active route and broadcasts it. Also broadcasts
 * the route periodically.
 * <p>
 * Improvements:
 * <ul>
 * <li>Use a worker pool rather than spawning a new thread for each broadcast.</li>
 * </ul>
 */
public class IntendedRouteHandler extends IntendedRouteHandlerCommon implements
        IRoutesUpdateListener, Runnable, IIntendedRouteHandlerSettingsObserver {

    private static final Logger LOG = LoggerFactory
            .getLogger(IntendedRouteHandler.class);
    private static long BROADCAST_TIME = 60; // Broadcast intended route every
                                             // minute for now
    private static long ADAPTIVE_TIME = 60 * 10; // Set to 10 minutes?
    private static final int BROADCAST_RADIUS = Integer.MAX_VALUE;

    private DateTime lastTransmitActiveWp;
    private DateTime lastSend = new DateTime(1);
    private RouteManager routeManager;
    private boolean running;

    private IntendedRouteLayer intendedRouteLayer;

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
    public void cloudConnected(MmsClient connection) {
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
     * {@inheritDoc}
     */
    @Override
    public synchronized void shutdown() {
        // Before shutting down, attempt to broadcast a no-intended route
        // message
        // so that other clients will remove the intended route of this ship
        broadcastIntendedRoute(null, false);

        super.shutdown();
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
                    calculatedTimeOfLastSend = calculatedTimeOfLastSend
                            .minus(BROADCAST_TIME * 1000L);

                    // Do we need to rebroadcast based on the broadcast time
                    // setting
                    if (calculatedTimeOfLastSend.isAfter(lastSend)) {
                        LOG.debug("Periodically rebroadcasting");
                        broadcastIntendedRoute();
                        lastSend = new DateTime();
                    } else if (lastTransmitActiveWp != null) {

                        // We check for the adaptive route broadcast here
                        // We need to compare lastTransmitActiveWp which is the
                        // last stored
                        // ETA of the waypoint we sent to the current one
                        DateTime currentActiveWaypointETA = new DateTime(
                                routeManager.getActiveRoute()
                                        .getActiveWaypointEta());

                        // LOG.debug("The ETA at last transmission was : " +
                        // lastTransmitActiveWp);
                        // LOG.debug("It is now                        : " +
                        // currentActiveWaypointETA);

                        // //It can either be before or after
                        //
                        if (currentActiveWaypointETA
                                .isAfter(lastTransmitActiveWp)
                                || currentActiveWaypointETA
                                        .isBefore(lastTransmitActiveWp)) {

                            long etaTimeChange;

                            // Is it before?
                            if (currentActiveWaypointETA
                                    .isAfter(lastTransmitActiveWp)) {

                                etaTimeChange = currentActiveWaypointETA.minus(
                                        lastTransmitActiveWp.getMillis())
                                        .getMillis();

                                // Must be after
                            } else {
                                etaTimeChange = currentActiveWaypointETA.plus(
                                        lastTransmitActiveWp.getMillis())
                                        .getMillis();
                            }

                            if (etaTimeChange > ADAPTIVE_TIME * 1000L) {
                                LOG.debug("Broadcast based on adaptive time!");
                                broadcastIntendedRoute();
                                lastSend = new DateTime();
                            }

                            // LOG.debug("ETA has changed with " + etaTimeChange
                            // + " mili seconds" );

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
        broadcastIntendedRoute(routeManager.getActiveRoute(), true);
    }

    /**
     * Broadcast intended route
     * 
     * @param activeRoute
     *            the active route to broadcast
     * @param async
     *            whether to broadcast the message asynchronously or not
     */
    public void broadcastIntendedRoute(ActiveRoute activeRoute, boolean async) {
        // Sanity check
        if (!running || routeManager == null
                || getMmsClient() == null) {
            return;
        }

        // Make intended route message
        MCIntendedRouteBroadcast message = new MCIntendedRouteBroadcast();

        if (activeRoute != null) {
            PartialRouteFilter filter = EPDShip.getInstance().getSettings()
                    .getCloudSettings().getIntendedRouteFilter();
            message = activeRoute.getPartialRouteData(filter);

            lastTransmitActiveWp = new DateTime(
                    activeRoute.getActiveWaypointEta());

        }

        // send message
        LOG.debug("Broadcasting intended route");
        final MCIntendedRouteBroadcast broadcast = message;
        Runnable broadcastMessage = new Runnable() {
            @Override
            public void run() {
                getMmsClient().withBroadcast(broadcast).send();
            }
        };

        if (async) {
            submitIfConnected(broadcastMessage);
        } else {
            broadcastMessage.run();
        }
    }

    /**
     * Handle event of active route change
     */
    @Override
    public void routesChanged(RoutesUpdateEvent e) {
        if (e != null) {
            if (e.is(RoutesUpdateEvent.ACTIVE_ROUTE_UPDATE,
                    RoutesUpdateEvent.ACTIVE_ROUTE_FINISHED,
                    RoutesUpdateEvent.ROUTE_ACTIVATED,
                    RoutesUpdateEvent.ROUTE_DEACTIVATED)) {

                lastSend = new DateTime();
                broadcastIntendedRoute();

                updateFilter();

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
        } else if (obj instanceof IntendedRouteLayer) {
            intendedRouteLayer = (IntendedRouteLayer) obj;
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

    /****************************************/
    /** Intended route filtering **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    protected String formatNotificationDescription(
            FilteredIntendedRoute filteredIntendedRoute) {

        Long otherMmsi = (filteredIntendedRoute.getMmsi1()
                .equals(getOwnShipMmsi())) ? filteredIntendedRoute.getMmsi2()
                : filteredIntendedRoute.getMmsi1();

        IntendedRouteFilterMessage msg = filteredIntendedRoute
                .getMinimumDistanceMessage();

        String actor = "MMSI " + otherMmsi;
        if (EPDShip.getInstance().getIdentityHandler().getActor(otherMmsi) != null) {
            actor = EPDShip.getInstance().getIdentityHandler()
                    .getActor(otherMmsi).getName();
        }

        return String
                .format("Your active route comes within %s of vessel %s at %s.",
                        Formatter.formatDistNM(Converter.metersToNm(msg
                                .getDistance())), actor, Formatter
                                .formatYodaTime(msg.getTime1()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getMmsi(Route route) {
        if (route instanceof IntendedRoute) {
            return ((IntendedRoute) route).getMmsi();
        }

        // Must be the active route. Return own-ship MMSI
        return getOwnShipMmsi();
    }

    /**
     * Returns the own-ship MMSI or -1 if undefined
     * 
     * @return the own-ship MMSI
     */
    public Long getOwnShipMmsi() {
        Long ownShipMmsi = EPDShip.getInstance().getMmsi();
        return (ownShipMmsi != null) ? ownShipMmsi : -1L;
    }

    /**
     * Update all filters
     */
    @Override
    protected void updateFilter() {

        // Recalculate everything
        // Compare all routes to current active route

        FilteredIntendedRoutes filteredIntendedRoutes = new FilteredIntendedRoutes();

        // Compare all intended routes against our own active route

        if (routeManager.getActiveRoute() != null) {

            // The route we're comparing against
            ActiveRoute activeRoute = routeManager.getActiveRoute();

            Iterator<Entry<Long, IntendedRoute>> it = intendedRoutes.entrySet()
                    .iterator();
            while (it.hasNext()) {
                Entry<Long, IntendedRoute> intendedRoute = it.next();

                IntendedRoute recievedRoute = intendedRoute.getValue();

                FilteredIntendedRoute filter = findTCPA(activeRoute,
                        recievedRoute);
                // Try other way around
                if (!filter.include()) {
                    filter = findTCPA(recievedRoute, activeRoute);
                }

                // No warnings, ignore it
                if (filter.include()) {

                    // Add the filtered route to the list
                    filteredIntendedRoutes.add(filter);

                }

            }

            // Call an update
            intendedRouteLayer.loadIntendedRoutes();

        }

        // Check if we need to raise any alerts
        checkGenerateNotifications(this.filteredIntendedRoutes,
                filteredIntendedRoutes);

        // Override the old set of filtered intended route
        this.filteredIntendedRoutes = filteredIntendedRoutes;
    }

    /**
     * Update filter with new intended route
     * 
     * @param route
     */
    @Override
    protected void applyFilter(IntendedRoute route) {
        // If previous intended route exist re-apply filter

        if (routeManager.getActiveRoute() != null) {

            FilteredIntendedRoute filter = findTCPA(
                    routeManager.getActiveRoute(), route);
            // Try other way around
            if (!filter.include()) {
                filter = findTCPA(route, routeManager.getActiveRoute());
            }

            // No warnings, ignore it
            if (!filter.include()) {

                // Remove it, if it exists
                if (this.filteredIntendedRoutes.containsKey(route.getMmsi())) {
                    filteredIntendedRoutes.remove(route.getMmsi());
                    LOG.debug("Remove from filter");
                }

            } else {
                // Check if we should generate notification
                checkGenerateNotifications(filteredIntendedRoutes, filter);

                // Add the filtered route to the list
                filteredIntendedRoutes.add(filter);

            }
        }
    }

    @Override
    public void sendIntendedRouteChanged(boolean value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void broadcastTimeChanged(long value) {
        BROADCAST_TIME = value;
    }

    @Override
    public void adaptiveBroadcastTimeChanged(int value) {
        // TODO Auto-generated method stub
        ADAPTIVE_TIME = value;
    }

    /**
     * Updates settings by invoking super implementation. Additionally calls
     * {@link #updateFilter()} to refresh the filter according to the updated
     * settings.
     */
    @Override
    public void updateSettings(EnavSettings settings) {
        super.updateSettings(settings);
        if (this.routeManager != null) {
            // Reapply filter with updated settings.
            this.updateFilter();
            /*
             * Fire dummy event such that any listening IntendedRouteTCPALayer
             * will redraw TCPAs.
             */
            this.fireIntendedEvent(null);

        }
    }

}

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

import java.util.Iterator;
import java.util.Map.Entry;

import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.broadcast.BroadcastOptions;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.enavcloud.intendedroute.IntendedRouteBroadcast;
import dk.dma.epd.common.prototype.enavcloud.intendedroute.IntendedRouteMessage;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLayerCommon;
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
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.settings.handlers.IntendedRouteHandlerSettings;

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

    private DateTime lastTransmitActiveWp;
    private DateTime lastSend = new DateTime(1);
    private RouteManager routeManager;
    private boolean running;
    
    private IntendedRouteLayerCommon intendedRouteLayerCommon;

    /**
     * Constructor
     */
    public IntendedRouteHandler(IntendedRouteHandlerSettings settings) {
        super(settings);
    }

    @Override
    protected IntendedRouteHandlerSettings getSettings() {
        return (IntendedRouteHandlerSettings) super.getSettings();
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
                    Util.sleep(getSettings().getTimeBetweenBroadCast() * 1000L);
                } else {

                    // Here we handle the periodical broadcasts
                    DateTime calculatedTimeOfLastSend = new DateTime();
                    calculatedTimeOfLastSend = calculatedTimeOfLastSend.minus(getSettings().getTimeBetweenBroadCast() * 1000L);

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

                            if (etaTimeChange > getSettings().getAdaptionTime() * 1000L) {
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
        // Sanity check
        if (!running || routeManager == null || getMaritimeCloudConnection() == null) {
            return;
        }

        // Make intended route message
        final IntendedRouteBroadcast message = new IntendedRouteBroadcast();

        if (routeManager.getActiveRoute() != null) {
            PartialRouteFilter filter = getSettings().getIntendedRouteFilter();
            routeManager.getActiveRoute().getPartialRouteData(filter, message);

            lastTransmitActiveWp = new DateTime(routeManager.getActiveRoute().getActiveWaypointEta());

        } else {
            message.setRoute(new IntendedRouteMessage());
        }

        // send message
        LOG.debug("Broadcasting intended route");

        submitIfConnected(new Runnable() {
            @Override
            public void run() {
                BroadcastOptions options = new BroadcastOptions();
                options.setBroadcastRadius(IntendedRouteHandler.this.getSettings().getBroadcastRadius());
                getMaritimeCloudConnection().broadcast(message, options);
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
        }
        else if (obj instanceof IntendedRouteLayerCommon) {
            intendedRouteLayerCommon = (IntendedRouteLayerCommon) obj;
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
    /** Intended route filtering           **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    protected String formatNotificationDescription(FilteredIntendedRoute filteredIntendedRoute) {

        Long otherMmsi = (filteredIntendedRoute.getMmsi1().equals(getOwnShipMmsi()))
                ? filteredIntendedRoute.getMmsi2()
                : filteredIntendedRoute.getMmsi1();
        
                IntendedRouteFilterMessage msg = filteredIntendedRoute.getMinimumDistanceMessage();
        return String.format("Your active route comes within %s of MMSI %d at %s.", 
                Formatter.formatDistNM(Converter.metersToNm(msg.getDistance())),
                otherMmsi,
                Formatter.formatYodaTime(msg.getTime1()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Long getMmsi(Route route) {
        if (route instanceof IntendedRoute) {
            return ((IntendedRoute)route).getMmsi(); 
        } 
        
        // Must be the active route. Return own-ship MMSI
        return getOwnShipMmsi();
    }
    
    /**
     * Returns the own-ship MMSI or -1 if undefined
     * @return the own-ship MMSI
     */
    public Long getOwnShipMmsi() {
        Long ownShipMmsi = EPDShip.getInstance().getOwnShipMmsi();
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

            Iterator<Entry<Long, IntendedRoute>> it = intendedRoutes.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Long, IntendedRoute> intendedRoute = it.next();

                IntendedRoute recievedRoute = intendedRoute.getValue();

                FilteredIntendedRoute filter = findTCPA(activeRoute, recievedRoute);                
                //Try other way around
                if (!filter.include()){
                    filter = findTCPA(recievedRoute, activeRoute);
                }
                
                //No warnings, ignore it
                if (filter.include()){
    
                    //Add the filtered route to the list
                    filteredIntendedRoutes.add(filter);
                    
                }
                
            }

            //Call an update
            intendedRouteLayerCommon.loadIntendedRoutes();
            
        }
        
        // Check if we need to raise any alerts
        checkGenerateNotifications(this.filteredIntendedRoutes, filteredIntendedRoutes);
        
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
            
            FilteredIntendedRoute filter = findTCPA(routeManager.getActiveRoute(), route);            
            //Try other way around
            if (!filter.include()){
                filter = findTCPA(route, routeManager.getActiveRoute());
            }
            
            
            //No warnings, ignore it
            if (!filter.include()){
                
                //Remove it, if it exists
                if (this.filteredIntendedRoutes.containsKey(route.getMmsi())){
                    filteredIntendedRoutes.remove(route.getMmsi());
                    System.out.println("Remove from filter");
                }
                
            } else {
                // Check if we should generate notification
                checkGenerateNotifications(filteredIntendedRoutes, filter);
                
                //Add the filtered route to the list
                filteredIntendedRoutes.add(filter);
                
            }            
        }
    }
}

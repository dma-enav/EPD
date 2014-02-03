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

import net.maritimecloud.net.MaritimeCloudClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.enavcloud.IntendedRouteBroadcast;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.service.MaritimeCloudSendThread;
import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.route.RouteManager;

/**
 * Ship specific intended route service implementation.
 * <p>
 * Listens for changes to the active route and broadcasts it.
 * Also broadcasts the route periodically.
 * <p>
 * Improvements:
 * <ul>
 *   <li>Use a worker pool rather than spawning a new thread for each broadcast.</li>
 * </ul>
 */
public class IntendedRouteHandler 
    extends IntendedRouteHandlerCommon 
    implements IRoutesUpdateListener, Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(IntendedRouteHandler.class);
    private static final long BROADCAST_TIME = 60; // Broadcast intended route every minute for now
    
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
     * Main thread run method.
     * Broadcasts the intended route
     */
    public void run() {
        while (running) {
            broadcastIntendedRoute();
            Util.sleep(BROADCAST_TIME * 1000L);
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
        IntendedRouteBroadcast message = new IntendedRouteBroadcast();
        
        if (routeManager.getActiveRoute() != null){
            message.setIntendedRoute(routeManager.getActiveRoute().getFullRouteData());    
        } else {
            message.setIntendedRoute(new Route());
        }
                
        // send message
        try {
            LOG.debug("Sending");
            // if connection.
            MaritimeCloudSendThread sendThread = new MaritimeCloudSendThread(
                    message,
                    getMaritimeCloudConnection());

            // Send it in a seperate thread
            sendThread.start();
            getStatus().markSuccesfullSend();
            LOG.debug("Done sending");        
        } catch (Exception e) {
            LOG.error("Error sending intended route " + e.getMessage(), e);
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
            routeManager = (RouteManager)obj;
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

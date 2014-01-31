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
package dk.dma.epd.ship.service.intendedroute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.enavcloud.IntendedRouteBroadcast;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.ship.service.EnavService;
import dk.dma.epd.ship.service.EnavServiceHandler;

/**
 * Intended route service implementation
 */
public class IntendedRouteService extends EnavService implements
        IRoutesUpdateListener {

    private static final Logger LOG = LoggerFactory
            .getLogger(IntendedRouteService.class);

    /**
     * The current active route provider
     */
    private final ActiveRouteProvider provider;

    /**
     * Constructor
     * 
     * @param enavServiceHandler
     * @param provider
     */
    public IntendedRouteService(EnavServiceHandler enavServiceHandler,
            ActiveRouteProvider provider) {
        super(enavServiceHandler);
        this.provider = provider;
    }
    
    /**
     * Broadcast intended route
     */
    public void broadcastIntendedRoute() {
        // Make intended route message
        IntendedRouteBroadcast message = new IntendedRouteBroadcast();
        
        if (provider.getActiveRoute() != null){
            message.setIntendedRoute(provider.getActiveRoute().getFullRouteData());    
        } else {
            message.setIntendedRoute(new Route());
        }
                
        // send message
        try {
            LOG.debug("Sending");
            enavServiceHandler.sendIntendedRouteMessage(message);
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
}

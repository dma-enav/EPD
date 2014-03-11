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

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.service.invocation.InvocationCallback;
import net.maritimecloud.net.service.invocation.InvocationCallback.Context;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionReply;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionStatus;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon;
import dk.dma.epd.common.prototype.service.InvocationCallbackContextMap;
import dk.dma.epd.ship.EPDShip;

/**
 * Ship-specific route suggestion e-Nav service.
 */
public class RouteSuggestionHandler extends EnavServiceHandlerCommon {

    private static final long CALLBACK_TTL = 60; // 60 minutes
    private static final Logger LOG = LoggerFactory.getLogger(RouteSuggestionHandler.class);  
    
    // Route suggestion service
    private InvocationCallbackContextMap<Long, Context<RouteSuggestionReply>> routeExchangeContexts = new InvocationCallbackContextMap<>(CALLBACK_TTL);
    
    /**
     * Constructor
     */
    public RouteSuggestionHandler() {
        super();
        
        // Schedule a clean-up check of the routeExchangeContexts every 10 minutes
        getScheduler().scheduleWithFixedDelay(new Runnable() {
            @Override public void run() {
                routeExchangeContexts.cleanup();
            }}, 10, 10, TimeUnit.MINUTES);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        
        // Register a cloud route suggestion service
        try {
            getMaritimeCloudConnection().serviceRegister(
                    RouteSuggestionService.INIT,
                    new InvocationCallback<RouteSuggestionMessage, RouteSuggestionReply>() {
                        public void process(
                                RouteSuggestionMessage message,
                                Context<RouteSuggestionReply> context) {

routeExchangeContexts.put(message.getId(), context);

                            SuggestedRoute suggestedRoute = new SuggestedRoute(
                                    message);

                            EPDShip.getInstance().getRouteManager()
                                    .receiveRouteSuggestion(suggestedRoute);

                        }
                    }).awaitRegistered(4, TimeUnit.SECONDS);
            
        } catch (InterruptedException e) {
            LOG.error("Error hooking up services", e);
        }
    }

    /**
     * Sends a reply to route suggestion reply
     * 
     * @param receivedAccepted the reply
     * @param id the ID of the route suggestion
     * @param message a message to send along with the reply
     */
    public void sendRouteExchangeReply(RouteSuggestionStatus receivedAccepted, long id, String message) {
        try {
            long ownMmsi = (maritimeCloudService.getMaritimeId() == null) ? -1 : MaritimeCloudUtils.toMmsi(maritimeCloudService.getMaritimeId());
            if (routeExchangeContexts.containsKey(id)) {
                routeExchangeContexts.remove(id).complete(new RouteSuggestionReply(
                        message, 
                        id, 
                        ownMmsi, 
                        System.currentTimeMillis(), 
                        receivedAccepted));
            } else {
                LOG.error("No callback context found for route suggestion " + id);
            }
        } catch (Exception e) {
            LOG.error("Failed to reply", e);
        }

    }
    

}

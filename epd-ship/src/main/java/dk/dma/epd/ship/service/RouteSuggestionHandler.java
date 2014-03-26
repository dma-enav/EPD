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

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.service.invocation.InvocationCallback;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionReply;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionStatus;
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.RouteSuggestionHandlerCommon;
import dk.dma.epd.ship.EPDShip;

/**
 * Ship-specific route suggestion e-Nav service.
 */
public class RouteSuggestionHandler extends RouteSuggestionHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(RouteSuggestionHandler.class);  
    
    /**
     * Constructor
     */
    public RouteSuggestionHandler() {
        super();
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

                            // The cloud status is transient, so this ought to be unnecessary
                            message.setCloudMessageStatus(null);

                            LOG.info("Shore received a suggeset route reply");
                            routeSuggestionReceived(message, context.getCaller());

                            // Acknowledge that the message has been handled 
                            context.complete(new RouteSuggestionReply(message.getId()));
                        }
                    }).awaitRegistered(4, TimeUnit.SECONDS);
            
        } catch (InterruptedException e) {
            LOG.error("Error hooking up services", e);
        }
    }

    /**
     * Called when a route suggestion is received over the maritime cloud
     * 
     * @param message the route suggestion
     * @param caller the caller
     */
    private void routeSuggestionReceived(RouteSuggestionMessage message, MaritimeId caller) {
        
        // Cache the message
        long mmsi = MaritimeCloudUtils.toMmsi(caller);
        RouteSuggestionData routeData = new RouteSuggestionData(message, mmsi);
        routeSuggestions.put(mmsi, routeData);

        // Update the route manager with the new route
        SuggestedRoute suggestedRoute = new SuggestedRoute(message);
        EPDShip.getInstance().getRouteManager().receiveRouteSuggestion(suggestedRoute);
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
            
            if (routeSuggestions.containsKey(id)) {
                
                RouteSuggestionData routeData = routeSuggestions.get(id);
                LOG.info("Sending to mmsi: " + routeData.getMmsi() + " with ID: " + routeData.getId());
                
                // Create the reply message
                RouteSuggestionMessage routeMessage = new RouteSuggestionMessage(null, null, message, receivedAccepted);
                routeData.setReply(routeMessage);
                routeData.setAcknowleged(true); // TODO ?
                
                // Send the message over the cloud
                routeMessage.setCloudMessageStatus(CloudMessageStatus.NOT_SENT);
                if (sendMaritimeCloudMessage(new MmsiId((int)routeData.getMmsi()), routeMessage, this)) {
                    routeMessage.updateCloudMessageStatus(CloudMessageStatus.SENT);
                }
                
                // Update listeners
                notifyRouteSuggestionListeners();
                
            } else {
                LOG.error("Route suggestion not found: " + id);
            }
        } catch (Exception e) {
            LOG.error("Failed to reply", e);
        }

    }
}

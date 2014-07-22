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

import java.util.concurrent.TimeUnit;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.service.invocation.InvocationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionReply;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionStatus;
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.RouteSuggestionHandlerCommon;

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
            getMaritimeCloudConnection().serviceRegister(RouteSuggestionService.INIT,
                    new InvocationCallback<RouteSuggestionMessage, RouteSuggestionReply>() {
                        public void process(RouteSuggestionMessage message, Context<RouteSuggestionReply> context) {

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
     * @param message
     *            the route suggestion
     * @param caller
     *            the caller
     */
    private void routeSuggestionReceived(RouteSuggestionMessage message, MaritimeId caller) {

        // Cache the message
        long mmsi = MaritimeCloudUtils.toMmsi(caller);
        RouteSuggestionData routeData = new RouteSuggestionData(message, mmsi);
        routeSuggestions.put(message.getId(), routeData);

        // Update listeners
        notifyRouteSuggestionListeners();
    }

    /**
     * Accepts the given suggested route
     * 
     * @param route
     *            the suggested route to accept
     * @return if the route was accepted
     */
    private boolean acceptRouteSuggestion(RouteSuggestionData routeData) {
        notifyRouteSuggestionListeners();
        return true;
    }

    /**
     * Sends a reply to route suggestion reply
     * 
     * @param id
     *            the ID of the route suggestion
     * @param replyStatus
     *            the reply
     * @param message
     *            a message to send along with the reply
     */
    public void sendRouteSuggestionReply(long id, RouteSuggestionStatus replyStatus, String message) {

        // Check that the reply status is valid
        if (replyStatus != RouteSuggestionStatus.ACCEPTED && replyStatus != RouteSuggestionStatus.REJECTED
                && replyStatus != RouteSuggestionStatus.NOTED) {
            LOG.error("Invalid reply status " + replyStatus);
            throw new IllegalArgumentException("Invalid reply status " + replyStatus);
        }

        try {

            if (routeSuggestions.containsKey(id)) {

                RouteSuggestionData routeData = routeSuggestions.get(id);
                LOG.info("Sending to mmsi: " + routeData.getMmsi() + " with ID: " + routeData.getId());

                // Create the reply message
                RouteSuggestionMessage routeMessage = new RouteSuggestionMessage(routeData.getId(), message, replyStatus);
                routeData.setReply(routeMessage);
                routeData.setAcknowleged(true);

                // Send the message over the cloud
                routeMessage.setCloudMessageStatus(CloudMessageStatus.NOT_SENT);
                if (sendMaritimeCloudMessage(new MmsiId((int) routeData.getMmsi()), routeMessage, this)) {
                    routeMessage.updateCloudMessageStatus(CloudMessageStatus.SENT);
                }

                // For accepted routes, add the route to the route manager, and remove the suggestion
                if (replyStatus == RouteSuggestionStatus.ACCEPTED) {
                    acceptRouteSuggestion(routeData);
                } else if (replyStatus == RouteSuggestionStatus.REJECTED) {
                    routeData.getRoute().setVisible(false);
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

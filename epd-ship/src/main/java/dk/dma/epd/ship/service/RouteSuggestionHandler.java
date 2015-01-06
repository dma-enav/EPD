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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.net.EndpointInvocationFuture;
import net.maritimecloud.net.MessageHeader;
import net.maritimecloud.net.mms.MmsClient;
import net.maritimecloud.util.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.RouteSuggestionHandlerCommon;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon.CloudMessageStatus;
import dma.route.AbstractTacticalRouteEndpoint;
import dma.route.RouteSegmentSuggestionStatus;
import dma.route.TacticalRouteReplyEndpoint;
import dma.route.TacticalRouteSuggestion;
import dma.route.TacticalRouteSuggestionReply;

/**
 * Ship-specific route suggestion e-Nav service.
 */
public class RouteSuggestionHandler extends RouteSuggestionHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(RouteSuggestionHandler.class);

    // Replyable endpoints
    private List<TacticalRouteReplyEndpoint> routeSuggestionServiceList = new ArrayList<>();

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
    public void cloudConnected(MmsClient connection) {

        // Refresh the service list
        fetchRouteSuggestionServices();

        // Register a cloud chat service
        try {
            getMmsClient().endpointRegister(new AbstractTacticalRouteEndpoint() {

                @Override
                protected void sendRouteSuggestion(MessageHeader header, TacticalRouteSuggestion suggestion) {
                    routeSuggestionReceived(suggestion, header.getSender(), header.getSenderTime());
                }

            }).awaitRegistered(4, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            LOG.error("Error hooking up services", e);
        }

        // TODO: Maritime Cloud 0.2 re-factoring
        // // Register a cloud route suggestion service
        // try {
        // getMmsClient().serviceRegister(RouteSuggestionService.INIT,
        // new InvocationCallback<RouteSuggestionMessage, RouteSuggestionReply>() {
        // public void process(RouteSuggestionMessage message, Context<RouteSuggestionReply> context) {
        //
        // // The cloud status is transient, so this ought to be unnecessary
        // message.setCloudMessageStatus(null);
        //
        // LOG.info("Shore received a suggeset route reply");
        // routeSuggestionReceived(message, context.getCaller());
        //
        // // Acknowledge that the message has been handled
        // context.complete(new RouteSuggestionReply(message.getId()));
        // }
        // }).awaitRegistered(4, TimeUnit.SECONDS);
        //
        // } catch (InterruptedException e) {
        // LOG.error("Error hooking up services", e);
        // }
    }

    /**
     * Refreshes the list of route suggestion services
     */
    public void fetchRouteSuggestionServices() {

        try {
            routeSuggestionServiceList = getMmsClient().endpointLocate(TacticalRouteReplyEndpoint.class).findAll().get();

        } catch (Exception e) {
            LOG.error("Failed looking up route suggestion services", e.getMessage());
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
    private void routeSuggestionReceived(TacticalRouteSuggestion message, MaritimeId caller, Timestamp timestamp) {

        // // Cache the message
        long mmsi = MaritimeCloudUtils.toMmsi(caller);

        // Hack, we have too many route types, get rid of one?
        dk.dma.enav.model.voyage.Route route = new dk.dma.epd.common.prototype.model.route.Route(message.getRoute())
                .getFullRouteData();

        RouteSuggestionData routeData = new RouteSuggestionData(message, mmsi, route);
        routeData.setAcknowleged(false);
        routeSuggestions.put(message.getId(), routeData);

        // // Update listeners
        notifyRouteSuggestionListeners();
    }

    /**
     * Accepts the given suggested route
     * 
     * @param routeData
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
    public void sendRouteSuggestionReply(long id, RouteSegmentSuggestionStatus replyStatus, String message) {

        // Check that the reply status is valid
        if (replyStatus != RouteSegmentSuggestionStatus.ACCEPTED && replyStatus != RouteSegmentSuggestionStatus.REJECTED) {
            LOG.error("Invalid reply status " + replyStatus);
            throw new IllegalArgumentException("Invalid reply status " + replyStatus);
        }

        try {

            if (routeSuggestions.containsKey(id)) {

                RouteSuggestionData routeData = routeSuggestions.get(id);
                LOG.info("Sending to mmsi: " + routeData.getMmsi() + " with ID: " + routeData.getId());

                TacticalRouteSuggestionReply reply = new TacticalRouteSuggestionReply();
                reply.setId(routeData.getId());
                reply.setReplyText(message);
                reply.setStatus(replyStatus);

                routeSuggestions.get(id).setReply(reply, new Date());

                TacticalRouteReplyEndpoint tacticalRouteReplyEndpoint = MaritimeCloudUtils.findServiceWithMmsi(
                        routeSuggestionServiceList, routeData.getMmsi());

                if (tacticalRouteReplyEndpoint != null) {
                    EndpointInvocationFuture<Void> returnVal = tacticalRouteReplyEndpoint.sendRouteSuggestionReply(reply);

                    returnVal.relayed().handle(new Consumer<Throwable>() {

                        @Override
                        public void accept(Throwable t) {
                            RouteSuggestionData routeData = routeSuggestions.get(id);
                            routeData.setCloudMessageStatus(CloudMessageStatus.RECEIVED_BY_CLOUD);
                            notifyRouteSuggestionListeners();
                        }
                    });

                    returnVal.handle(new BiConsumer<Void, Throwable>() {
                        @Override
                        public void accept(Void t, Throwable u) {
                            RouteSuggestionData routeData = routeSuggestions.get(id);
                            routeData.setCloudMessageStatus(CloudMessageStatus.RECEIVED_BY_CLIENT);
                            notifyRouteSuggestionListeners();
                        }
                    });

                }

                // // Create the reply message
                // RouteSuggestionMessage routeMessage = new RouteSuggestionMessage(routeData.getId(), message, replyStatus);
                // // routeData.setReply(routeMessage);
                // routeData.setAcknowleged(true);
                //
                // // Send the message over the cloud
                // routeMessage.setCloudMessageStatus(CloudMessageStatus.NOT_SENT);
                // if (sendMaritimeCloudMessage(new MmsiId((int) routeData.getMmsi()), routeMessage, this)) {
                // routeMessage.updateCloudMessageStatus(CloudMessageStatus.SENT);
                // }
                //
                // // For accepted routes, add the route to the route manager, and remove the suggestion
                if (replyStatus == RouteSegmentSuggestionStatus.ACCEPTED) {
                    acceptRouteSuggestion(routeData);
                } else if (replyStatus == RouteSegmentSuggestionStatus.REJECTED) {
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

    @SuppressWarnings("unchecked")
    public static RouteSuggestionHandler loadRouteSuggestionHandler() {

        // Where we load or serialize old Route Suggestions
        RouteSuggestionHandler routeSuggestionHandler = new RouteSuggestionHandler();
        try (FileInputStream fileIn = new FileInputStream(ROUTE_SUGGESTION_PATH);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);) {

            // routeSuggestions =);
            routeSuggestionHandler.setRouteSuggestions((Map<Long, RouteSuggestionData>) objectIn.readObject());
            routeSuggestionHandler.notifyRouteSuggestionListeners();
            // voctManager.setLoadSarFromSerialize(true);
            // voctManager.initializeFromSerializedFile(sarDataLoaded);
            //
        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load route suggestion file: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(ROUTE_SUGGESTION_PATH).delete();
        }

        return routeSuggestionHandler;

    }
}

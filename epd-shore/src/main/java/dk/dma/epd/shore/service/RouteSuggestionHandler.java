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
package dk.dma.epd.shore.service;

import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionReply;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionStatus;
import dk.dma.epd.common.prototype.enavcloud.TODO;
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.service.RouteSuggestionHandlerCommon;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.mms.MmsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Shore-specific route suggestion e-Nav service.
 */
public class RouteSuggestionHandler extends RouteSuggestionHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(RouteSuggestionHandler.class);

    private List<TODO.ServiceEndpoint<RouteSuggestionMessage, RouteSuggestionReply>> routeSuggestionServiceList = new ArrayList<>();

    /**
     * Constructor
     */
    public RouteSuggestionHandler() {
        super();

        // Schedule a refresh of the strategic route acknowledge services approximately every minute
        scheduleWithFixedDelayWhenConnected(new Runnable() {
            @Override
            public void run() {
                fetchRouteSuggestionServices();
            }
        }, 5, 62, TimeUnit.SECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MmsClient connection) {
// TODO: Maritime Cloud 0.2 re-factoring
//        try {
//            getMmsClient().serviceRegister(RouteSuggestionService.INIT,
//                    new InvocationCallback<RouteSuggestionMessage, RouteSuggestionReply>() {
//                        public void process(RouteSuggestionMessage message, Context<RouteSuggestionReply> context) {
//
//                            // The cloud status is transient, so this ought to be unnecessary
//                            message.setCloudMessageStatus(null);
//
//                            LOG.info("Shore received a suggeset route reply");
//                            routeSuggestionReplyReceived(message);
//
//                            // Acknowledge that the message has been handled
//                            context.complete(new RouteSuggestionReply(message.getId()));
//                        }
//                    }).awaitRegistered(4, TimeUnit.SECONDS);
//
//        } catch (Exception e) {
//            LOG.error("Error hooking up services", e);
//        }

        // Refresh the service list
        fetchRouteSuggestionServices();
    }

    /**
     * Refreshes the list of route suggestion services
     */
    public void fetchRouteSuggestionServices() {
// TODO: Maritime Cloud 0.2 re-factoring
//        try {
//            routeSuggestionServiceList = getMmsClient().serviceLocate(RouteSuggestionService.INIT)
//                    .nearest(Integer.MAX_VALUE).get();
//        } catch (Exception e) {
//            LOG.error("Failed looking up route suggestion services", e.getMessage());
//        }
    }

    /**
     * Returns the route suggestion service list
     * 
     * @return the route suggestion service list
     */
    public List<TODO.ServiceEndpoint<RouteSuggestionMessage, RouteSuggestionReply>> getRouteSuggestionServiceList() {
        return routeSuggestionServiceList;
    }

    /**
     * Checks for a ship with the given mmsi in the route suggestion service list
     * 
     * @param mmsi
     *            the mmsi of the ship to search for
     * @return if one such ship is available
     */
    public boolean shipAvailableForRouteSuggestion(long mmsi) {
        return false;
// TODO: Maritime Cloud 0.2 re-factoring
//        return MaritimeCloudUtils.findServiceWithMmsi(routeSuggestionServiceList, (int) mmsi) != null;
    }

    /**
     * Sends a route suggestion to the given ship
     * 
     * @param mmsi
     *            the mmsi of the ship
     * @param route
     *            the route
     * @param message
     *            an additional message
     */
    public void sendRouteSuggestion(long mmsi, Route route, String message) throws InterruptedException, ExecutionException,
            TimeoutException {

        // Create a new message
        RouteSuggestionMessage routeMessage = new RouteSuggestionMessage(route, message, RouteSuggestionStatus.PENDING);
        LOG.info("Sending to mmsi: " + mmsi + " with ID: " + routeMessage.getId());

        // Cache the message by the transaction id
        RouteSuggestionData routeData = new RouteSuggestionData(routeMessage, mmsi);
        routeData.setAcknowleged(false);
        routeSuggestions.put(routeMessage.getId(), routeData);

        // Send the message over the cloud
        routeMessage.setCloudMessageStatus(CloudMessageStatus.NOT_SENT);
        if (sendMaritimeCloudMessage(routeSuggestionServiceList, new MmsiId((int) mmsi), routeMessage, this)) {
            routeMessage.updateCloudMessageStatus(CloudMessageStatus.SENT);
        }

        // Update listeners
        notifyRouteSuggestionListeners();
    }

    @SuppressWarnings("unchecked")
    public static RouteSuggestionHandler loadRouteSuggestionHandler() {

        // Where we load or serialize old VOCTS
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

    /**
     * Called when a route suggestion reply has been received
     * 
     * @param message
     *            the reply
     */
    private void routeSuggestionReplyReceived(RouteSuggestionMessage message) {

        LOG.info("Route suggestion reply received for ID " + message.getId());

        if (routeSuggestions.containsKey(message.getId())) {

            RouteSuggestionData routeData = routeSuggestions.get(message.getId());
            RouteSuggestionStatus response = message.getStatus();

            if (response != routeData.getStatus()) {
                routeData.setReply(message);
                routeData.setAcknowleged(false);
                notifyRouteSuggestionListeners();
            }
        }
    }
}

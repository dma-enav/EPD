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
import dk.dma.enav.model.voyage.Waypoint;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionReply;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionStatus;
import dk.dma.epd.common.prototype.enavcloud.TODO;
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.RouteSuggestionHandlerCommon;
import dma.messaging.AbstractMaritimeTextingService;
import dma.messaging.MaritimeText;
import dma.messaging.MaritimeTextingService;
import dma.route.StrategicRouteEndpoint;
import dma.route.TacticalRouteEndpoint;
import dma.route.TacticalRouteSuggestion;
import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.net.MessageHeader;
import net.maritimecloud.net.mms.MmsClient;
import net.maritimecloud.util.Timestamp;

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

    private List<TacticalRouteEndpoint> routeSuggestionServiceList = new ArrayList<>();

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

        // Refresh the service list

        // // Register a cloud chat service
        // try {
        // getMmsClient().endpointRegister(new AbstractMaritimeTextingService() {
        // @Override
        // protected void sendMessage(MessageHeader header, MaritimeText msg) {
        // receiveChatMessage(header.getSender(), msg, header.getSenderTime());
        // }
        // }).awaitRegistered(4, TimeUnit.SECONDS);
        //
        // } catch (InterruptedException e) {
        // LOG.error("Error hooking up services", e);
        // }
        //

        // TODO: Maritime Cloud 0.2 re-factoring
        // try {
        // getMmsClient().serviceRegister(RouteSuggestionService.INIT,
        // new InvocationCallback<RouteSuggestionMessage, RouteSuggestionReply>() {
        // public void process(RouteSuggestionMessage message, Context<RouteSuggestionReply> context) {
        //
        // // The cloud status is transient, so this ought to be unnecessary
        // message.setCloudMessageStatus(null);
        //
        // LOG.info("Shore received a suggeset route reply");
        // routeSuggestionReplyReceived(message);
        //
        // // Acknowledge that the message has been handled
        // context.complete(new RouteSuggestionReply(message.getId()));
        // }
        // }).awaitRegistered(4, TimeUnit.SECONDS);
        //
        // } catch (Exception e) {
        // LOG.error("Error hooking up services", e);
        // }

        // Refresh the service list
        fetchRouteSuggestionServices();
    }

    /**
     * Refreshes the list of route suggestion services
     */
    public void fetchRouteSuggestionServices() {

        try {
            routeSuggestionServiceList = getMmsClient().endpointLocate(TacticalRouteEndpoint.class).findAll().get();

        } catch (Exception e) {
            LOG.error("Failed looking up route suggestion services", e.getMessage());
        }

    }

    /**
     * Returns the route suggestion service list
     * 
     * @return the route suggestion service list
     */
    public List<TacticalRouteEndpoint> getRouteSuggestionServiceList() {
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

        return MaritimeCloudUtils.findServiceWithMmsi(routeSuggestionServiceList, mmsi) != null;
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

        TacticalRouteSuggestion routeSegmentSuggestion = fromRoute(route);

        TacticalRouteEndpoint tacticalRouteEndpoint = MaritimeCloudUtils.findServiceWithMmsi(routeSuggestionServiceList, mmsi);

        if (tacticalRouteEndpoint != null) {
            tacticalRouteEndpoint.sendRouteSuggestion(routeSegmentSuggestion);
        } else {
            LOG.error("Could not find tactical route endpoint for mmsi: " + mmsi);
            return;
        }

        // // Create a new message
        // RouteSuggestionMessage routeMessage = new RouteSuggestionMessage(route, message, RouteSuggestionStatus.PENDING);
        // LOG.info("Sending to mmsi: " + mmsi + " with ID: " + routeMessage.getId());

        // Cache the message by the transaction id
        RouteSuggestionData routeData = new RouteSuggestionData(routeSegmentSuggestion, mmsi);
        routeData.setAcknowleged(false);
        routeSuggestions.put(routeMessage.getId(), routeData);

        // Send the message over the cloud
        // routeMessage.setCloudMessageStatus(CloudMessageStatus.NOT_SENT);
        // if (sendMaritimeCloudMessage(routeSuggestionServiceList, new MmsiId((int) mmsi), routeMessage, this)) {
        // routeMessage.updateCloudMessageStatus(CloudMessageStatus.SENT);
        // }

        // Update listeners
        notifyRouteSuggestionListeners();
    }

    /**
     * Create an TacticalRouteSuggestion from a route model instance Add the ID based on current timestamp
     * 
     * @param route
     * @return
     */
    public static dma.route.TacticalRouteSuggestion fromRoute(dk.dma.enav.model.voyage.Route route) {
        dma.route.TacticalRouteSuggestion irm = new dma.route.TacticalRouteSuggestion();
        dma.route.Route r = new dma.route.Route();
        r.setRoutename(route.getName());
        for (Waypoint wp : route.getWaypoints()) {
            dma.route.Waypoint iwp = new dma.route.Waypoint();
            net.maritimecloud.util.geometry.Position pos = net.maritimecloud.util.geometry.Position.create(wp.getLatitude(),
                    wp.getLongitude());
            iwp.setWaypointPosition(pos);
            iwp.setEta(Timestamp.create(wp.getEta().getTime()));
            iwp.setRot(wp.getRot());
            iwp.setTurnRad(wp.getTurnRad());
            if (wp.getRouteLeg() != null) {
                dma.route.Leg leg = new dma.route.Leg();
                leg.setSpeed(wp.getRouteLeg().getSpeed());
                leg.setXtdStarboard(wp.getRouteLeg().getXtdStarboard());
                leg.setXtdPort(wp.getRouteLeg().getXtdPort());
                if (wp.getRouteLeg().getHeading() == dk.dma.enav.model.voyage.RouteLeg.Heading.RL) {
                    leg.setHeadingType(dma.route.HeadingType.RHUMB_LINE);
                } else {
                    leg.setHeadingType(dma.route.HeadingType.GREAT_CIRCLE);
                }
                iwp.setOutLeg(leg);
            }
            r.addWaypoints(iwp);
        }
        irm.setRoute(r);
        irm.setId(System.currentTimeMillis());
        return irm;
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

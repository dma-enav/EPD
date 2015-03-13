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
package dk.dma.epd.common.prototype.service;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData.StrategicRouteMessageData;
import dma.route.AbstractStrategicRouteEndpoint;
import dma.route.StrategicRouteEndpoint;
import dma.route.StrategicRouteMessage;
import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.net.EndpointInvocationFuture;
import net.maritimecloud.net.MessageHeader;
import net.maritimecloud.net.mms.MmsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * Common handler class for the strategic route e-Navigation service
 */
@SuppressWarnings("unused")
public abstract class StrategicRouteHandlerCommon extends EnavServiceHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(StrategicRouteHandlerCommon.class);
    public static final int CLOUD_TIMEOUT = 10; // Seconds

    protected List<StrategicRouteEndpoint> strategicRouteServiceList = new ArrayList<>();
    protected Map<Long, StrategicRouteNegotiationData> strategicRouteNegotiationData = new ConcurrentHashMap<>();
    protected List<StrategicRouteListener> strategicRouteListeners = new CopyOnWriteArrayList<>();
    
    /**
     * Constructor
     */
    public StrategicRouteHandlerCommon() {
        super(2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MmsClient connection) {
        try {
            getMmsClient().endpointRegister(new AbstractStrategicRouteEndpoint() {
                @Override
                protected void routeSuggestion(MessageHeader header, dma.route.StrategicRouteMessage suggestion) {
                    LOG.info("Received a strategic route request");
                    handleStrategicRouteMessage(suggestion, header.getSender(), Date.from(header.getSenderTime().asInstant()));
                }

            }).awaitRegistered(4, TimeUnit.SECONDS);

        } catch (Exception e) {
            LOG.error("Error hooking up services", e);
        }

        // Refresh the service lists
        fetchStrategicRouteServices();
    }

    /**
     * Fetches the list of strategic route service endpoints
     */
    protected void fetchStrategicRouteServices() {
        try {
            List<StrategicRouteEndpoint> services = getMmsClient().endpointLocate(StrategicRouteEndpoint.class)
                    .withinDistanceOf(1000 * 1000) // 1000 KM
                    .findAll()
                    .timeout(CLOUD_TIMEOUT, TimeUnit.SECONDS)
                    .get();
            strategicRouteServiceList = services
                    .stream()
                    .filter(MaritimeCloudUtils.filterByType(EPD.getInstance().getType() == EPD.EPDType.SHORE))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Failed looking up strategic route services:" + e.getMessage());
        }
    }

    /**
     * Handles a strategic route suggestion message received via MMS
     * @param suggestion the strategic route message
     * @param sender the sender
     * @param senderTime the date
     */
    protected abstract void handleStrategicRouteMessage(StrategicRouteMessage suggestion, MaritimeId sender, Date senderTime);

    /**
     * Sends a strategic route request
     *
     * @param routeData the strategic route data
     * @param routeMessage the strategic route request
     * @param mmsi the MMSI
     */
    protected void sendStrategicRouteMessage(StrategicRouteNegotiationData routeData, StrategicRouteMessage routeMessage, long mmsi) {

        final StrategicRouteMessageData messageData =
                routeData.addMessage(routeMessage, new Date(), CloudMessageStatus.NOT_SENT, EPD.getInstance().getType() == EPD.EPDType.SHORE);

        StrategicRouteEndpoint strategicRouteEndpoint = MaritimeCloudUtils.findServiceWithMmsi(strategicRouteServiceList, mmsi);

        if (strategicRouteEndpoint != null) {
            EndpointInvocationFuture<Void> returnVal = strategicRouteEndpoint.routeSuggestion(routeMessage);
            messageData.setCloudMessageStatus(CloudMessageStatus.SENT);

            returnVal.relayed().handle(t -> messageData.setCloudMessageStatus(CloudMessageStatus.RECEIVED_BY_CLOUD));

            returnVal.handle((t, u) -> messageData.setCloudMessageStatus(CloudMessageStatus.RECEIVED_BY_CLIENT));

        } else {
            messageData.setCloudMessageStatus(CloudMessageStatus.SENT_FAILED);
            LOG.error("Could not find strategic route endpoint for mmsi: " + mmsi);
        }
    }

    /**
     * Return if there are any strategic route service available
     */
    public boolean strategicRouteServiceExists() {
        return strategicRouteServiceList != null && strategicRouteServiceList.size() > 0;
    }

    /**
     * Returns the list of strategic route service endpoints
     * @return the list of strategic route service endpoints
     */
    public List<StrategicRouteEndpoint> getStrategicRouteServiceList() {
        return strategicRouteServiceList;
    }

    /**
     * Returns the strategic negotiation data
     * @return the strategic negotiation data
     */
    public Map<Long, StrategicRouteNegotiationData> getStrategicNegotiationData() {
        return strategicRouteNegotiationData;
    }
    
    /**
     * Returns the strategic negotiation data sorted by date
     * @return the strategic negotiation data
     */
    public List<StrategicRouteNegotiationData> getSortedStrategicNegotiationData() {
        List<StrategicRouteNegotiationData> list = new ArrayList<>(strategicRouteNegotiationData.values());
        Collections.sort(list);
        return list;
    }
    
    /**
     * Returns the StrategicRouteNegotiationData for the given transaction id
     * 
     * @param transactionId the transaction id
     * @return the strategicRouteNegotiationData
     */
    public StrategicRouteNegotiationData getStrategicRouteNegotiationData(Long transactionId) {
        return strategicRouteNegotiationData.get(transactionId);
    }

    /**
     * Adds a listener for strategic route updates
     * @param listener the lister to add
     */
    public synchronized void addStrategicRouteListener(StrategicRouteListener listener) {
        strategicRouteListeners.add(listener);
    }

    /**
     * Removes a listener for strategic route updates
     * @param listener the lister to remove
     */
    public synchronized void removeStrategicRouteListener(StrategicRouteListener listener) {
        strategicRouteListeners.remove(listener);
    }

    /**
     * Notifies listeners about a strategic route update
     */
    protected synchronized void notifyStrategicRouteListeners() {
        strategicRouteListeners.forEach(StrategicRouteHandlerCommon.StrategicRouteListener::strategicRouteUpdate);
    }
    
    /****************************************/
    /** Helper classes                     **/
    /****************************************/    
    
    /**
     * Interface to be implemented by all clients wishing 
     * to be notified about updates to strategic routes
     */
    public interface StrategicRouteListener {
        
        /**
         * Cloud messages has changed
         */
        void strategicRouteUpdate();

    }

}

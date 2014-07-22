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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionReply;
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon.ICloudMessageListener;

/**
 * Common functionality for route suggestion e-Nav services.
 */
public class RouteSuggestionHandlerCommon extends EnavServiceHandlerCommon implements
        ICloudMessageListener<RouteSuggestionMessage, RouteSuggestionReply> {

    protected Map<Long, RouteSuggestionData> routeSuggestions = new ConcurrentHashMap<>();
    protected Set<RouteSuggestionListener> routeExchangeListener = new HashSet<RouteSuggestionListener>();
    protected static final String ROUTE_SUGGESTION_PATH = EPD.getInstance().getHomePath().resolve(".routesuggestions").toString();
    protected static final Logger LOG = LoggerFactory.getLogger(RouteSuggestionHandlerCommon.class);

    /**
     * Constructor
     */
    public RouteSuggestionHandlerCommon() {
        super(2);
    }

    /**
     * Returns the route suggestion data
     * 
     * @return the route suggestion data
     */
    public Map<Long, RouteSuggestionData> getRouteSuggestions() {
        return routeSuggestions;
    }

    /**
     * Returns the route suggestion data sorted by date
     * 
     * @return the route suggestion data
     */
    public List<RouteSuggestionData> getSortedRouteSuggestions() {
        List<RouteSuggestionData> list = new ArrayList<>(routeSuggestions.values());
        Collections.sort(list);
        return list;
    }

    /**
     * Returns the RouteSuggestionData for the given transaction id
     * 
     * @param transactionId
     *            the transaction id
     * @return the RouteSuggestionData
     */
    public RouteSuggestionData getRouteSuggestion(Long transactionId) {
        return routeSuggestions.get(transactionId);
    }

    /**
     * Flags that the route suggestion ith the given id has been acknowledged
     * 
     * @param id
     *            the id of the route suggestion
     */
    public synchronized void setRouteSuggestionAcknowledged(Long id) {
        if (routeSuggestions.containsKey(id)) {
            routeSuggestions.get(id).setAcknowleged(true);
            notifyRouteSuggestionListeners();
        }
    }

    /**
     * Removes the route suggestion with the given id
     * 
     * @param id
     *            the id of the route suggestion
     */
    public synchronized void removeSuggestion(long id) {
        routeSuggestions.remove(id);
        notifyRouteSuggestionListeners();
    }

    /**
     * Returns the number of route suggestions that have not been acknowledged
     * 
     * @return the number of route suggestions that have not been acknowledged
     */
    public synchronized int getUnacknowledgedRouteSuggestions() {

        int counter = 0;
        for (RouteSuggestionData data : routeSuggestions.values()) {
            if (!data.isAcknowleged()) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Add a listener to the route suggestion service
     * 
     * @param listener
     *            the listener to add
     */
    public synchronized void addRouteSuggestionListener(RouteSuggestionListener listener) {
        routeExchangeListener.add(listener);
    }

    /**
     * Removes a listener from the route suggestion service
     * 
     * @param listener
     *            the listener to remove
     */
    public synchronized void removeRouteSuggestionListener(RouteSuggestionListener listener) {
        routeExchangeListener.remove(listener);
    }

    /**
     * Broadcast a route update to all listeners
     */
    public synchronized void notifyRouteSuggestionListeners() {

        for (RouteSuggestionListener listener : routeExchangeListener) {
            listener.routeUpdate();
        }
        saveToFile();
    }

    /**
     * @param routeSuggestions
     *            the routeSuggestions to set
     */
    public void setRouteSuggestions(Map<Long, RouteSuggestionData> routeSuggestions) {
        this.routeSuggestions = routeSuggestions;
    }

    public synchronized void saveToFile() {
        try (FileOutputStream fileOut = new FileOutputStream(ROUTE_SUGGESTION_PATH);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);) {
            objectOut.writeObject(routeSuggestions);
        } catch (IOException e) {
            LOG.error("Failed to save Route Suggestion data: " + e.getMessage());
        }
    }

    /****************************************/
    /** ICloudMessageStatus methods **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceivedByCloud(RouteSuggestionMessage message) {
        message.updateCloudMessageStatus(CloudMessageStatus.RECEIVED_BY_CLOUD);
        EPD.getInstance().getNotificationCenter().checkRefreshSelection(NotificationType.TACTICAL_ROUTE, message.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageHandled(RouteSuggestionMessage message, RouteSuggestionReply reply) {
        message.updateCloudMessageStatus(CloudMessageStatus.HANDLED_BY_CLIENT);
        EPD.getInstance().getNotificationCenter().checkRefreshSelection(NotificationType.TACTICAL_ROUTE, message.getId());
    }

    /****************************************/
    /** Helper classes **/
    /****************************************/

    /**
     * Interface that should be implemented by client wishing to be notified about route changes
     */
    public interface RouteSuggestionListener {

        /**
         * Cloud messages has changed
         */
        void routeUpdate();

    }

}

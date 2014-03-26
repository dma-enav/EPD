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
package dk.dma.epd.common.prototype.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionReply;
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon.ICloudMessageListener;

/**
 * Common functionality for route suggestion e-Nav services.
 */
public class RouteSuggestionHandlerCommon extends EnavServiceHandlerCommon 
    implements ICloudMessageListener<RouteSuggestionMessage, RouteSuggestionReply> {

    protected Map<Long, RouteSuggestionData> routeSuggestions = new ConcurrentHashMap<>();
    protected Set<RouteSuggestionListener> routeExchangeListener = new HashSet<RouteSuggestionListener>();
    
    
    /**
     * Constructor
     */
    public RouteSuggestionHandlerCommon() {
        super(2);
    }

    /**
     * Returns the route suggestion data
     * @return the route suggestion data
     */
    public Map<Long, RouteSuggestionData> getRouteSuggestions() {
        return routeSuggestions;
    }
    
    /**
     * Returns the route suggestion data sorted by date
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
     * @param transactionId the transaction id
     * @return the RouteSuggestionData
     */
    public RouteSuggestionData getRouteSuggestion(Long transactionId) {
        return routeSuggestions.get(transactionId);
    }
    
    
    /**
     * Add a listener to the route suggestion service
     * 
     * @param listener the listener to add
     */
    public synchronized void addRouteSuggestionListener(RouteSuggestionListener listener) {
        routeExchangeListener.add(listener);
    }

    /**
     * Removes a listener from the route suggestion service
     * 
     * @param listener the listener to remove
     */
    public synchronized void removeRouteSuggestionListener(RouteSuggestionListener listener) {
        routeExchangeListener.remove(listener);
    }

    /**
     * Broadcast a route update to all listeners
     */
    protected synchronized void notifyRouteSuggestionListeners() {

        for (RouteSuggestionListener listener : routeExchangeListener) {
            listener.routeUpdate();
        }
    }

    /****************************************/
    /** ICloudMessageStatus methods        **/
    /****************************************/    

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceivedByCloud(RouteSuggestionMessage message) {
        message.updateCloudMessageStatus(CloudMessageStatus.RECEIVED_BY_CLOUD);
        EPD.getInstance().getNotificationCenter()
            .checkRefreshSelection(NotificationType.TACTICAL_ROUTE, message.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageHandled(RouteSuggestionMessage message, RouteSuggestionReply reply) {
        message.updateCloudMessageStatus(CloudMessageStatus.HANDLED_BY_CLIENT);
        EPD.getInstance().getNotificationCenter()
            .checkRefreshSelection(NotificationType.TACTICAL_ROUTE, message.getId());
    }
    
    /****************************************/
    /** Helper classes                     **/
    /****************************************/    
    
    /**
     * Interface that should be implemented by client wishing to 
     * be notified about route changes
     */
    public interface RouteSuggestionListener {

        /**
         * Cloud messages has changed
         */
        void routeUpdate();

    }
}

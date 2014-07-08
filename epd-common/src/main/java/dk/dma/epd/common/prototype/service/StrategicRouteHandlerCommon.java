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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteReply;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon.ICloudMessageListener;


/**
 * Common handler class for the strategic route e-Navigation service
 */
public class StrategicRouteHandlerCommon extends EnavServiceHandlerCommon 
    implements ICloudMessageListener<StrategicRouteMessage, StrategicRouteReply> {

    protected Map<Long, StrategicRouteNegotiationData> strategicRouteNegotiationData = new ConcurrentHashMap<>();
    protected List<StrategicRouteListener> strategicRouteListeners = new CopyOnWriteArrayList<>();
    
    /**
     * Constructor
     */
    public StrategicRouteHandlerCommon() {
        super(2);
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
        for (StrategicRouteListener listener : strategicRouteListeners) {
            listener.strategicRouteUpdate();
        }
    }
    
    /****************************************/
    /** ICloudMessageStatus methods        **/
    /****************************************/    

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceivedByCloud(StrategicRouteMessage message) {
        message.updateCloudMessageStatus(CloudMessageStatus.RECEIVED_BY_CLOUD);
        EPD.getInstance().getNotificationCenter()
            .checkRefreshSelection(NotificationType.STRATEGIC_ROUTE, message.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageHandled(StrategicRouteMessage message, StrategicRouteReply reply) {
        message.updateCloudMessageStatus(CloudMessageStatus.HANDLED_BY_CLIENT);
        EPD.getInstance().getNotificationCenter()
            .checkRefreshSelection(NotificationType.STRATEGIC_ROUTE, message.getId());
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

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
import edu.emory.mathcs.backport.java.util.Collections;


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
        message.setCloudMessageStatus(CloudMessageStatus.RECEIVED_BY_CLOUD);
        EPD.getInstance().getNotificationCenter()
            .checkRefreshSelection(NotificationType.STRATEGIC_ROUTE, message.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceivedByClient(StrategicRouteMessage message) {
        message.setCloudMessageStatus(CloudMessageStatus.RECEIVED_BY_CLIENT);
        notifyStrategicRouteListeners();
        EPD.getInstance().getNotificationCenter()
            .checkRefreshSelection(NotificationType.STRATEGIC_ROUTE, message.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageHandled(StrategicRouteMessage message, StrategicRouteReply reply) {
        message.setCloudMessageStatus(CloudMessageStatus.HANDLED_BY_CLIENT);
        notifyStrategicRouteListeners();
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

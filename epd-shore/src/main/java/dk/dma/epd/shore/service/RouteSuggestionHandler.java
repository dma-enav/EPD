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
package dk.dma.epd.shore.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.maritimecloud.net.ConnectionFuture;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.service.ServiceEndpoint;
import net.maritimecloud.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionReply;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionStatus;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon;

/**
 * Shore-specific route suggestion e-Nav service.
 */
public class RouteSuggestionHandler extends EnavServiceHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(RouteSuggestionHandler.class);  

    private Map<RouteSuggestionKey, RouteSuggestionData> routeSuggestions = new ConcurrentHashMap<>();
    private Set<RouteSuggestionListener> routeExchangeListener = new HashSet<RouteSuggestionListener>();
    private List<ServiceEndpoint<RouteSuggestionMessage, RouteSuggestionReply>> routeSuggestionServiceList = new ArrayList<>();
    
    /**
     * Constructor
     */
    public RouteSuggestionHandler() {
        super();
        
        // Schedule a refresh of the strategic route acknowledge services approximately every minute
        scheduleWithFixedDelayWhenConnected(new Runnable() {
            @Override public void run() {
                fetchRouteSuggestionServices();
            }}, 5, 62, TimeUnit.SECONDS);        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        // Refresh the service list
        fetchRouteSuggestionServices();
    }
    
    /**
     * Refreshes the list of route suggestion services
     */
    public void fetchRouteSuggestionServices() {
        try {
            routeSuggestionServiceList = getMaritimeCloudConnection().serviceLocate(RouteSuggestionService.INIT).nearest(Integer.MAX_VALUE).get();
        } catch (Exception e) {
            LOG.error("Failed looking up route suggestion services", e.getMessage());
        }
    }

    /**
     * Returns the route suggestion service list
     * @return the route suggestion service list
     */
    public List<ServiceEndpoint<RouteSuggestionMessage, RouteSuggestionReply>> getRouteSuggestionServiceList() {
        return routeSuggestionServiceList;
    }

    /**
     * Checks for a ship with the given mmsi in the route suggestion service list
     * 
     * @param mmsi the mmsi of the ship to search for
     * @return if one such ship is available
     */
    public boolean shipAvailableForRouteSuggestion(long mmsi) {
        return MaritimeCloudUtils.findServiceWithMmsi(routeSuggestionServiceList, (int)mmsi) != null;
    }

    /**
     * Sends a route suggestion to the given ship
     * 
     * @param mmsi the mmsi of the ship
     * @param route the route
     * @param sender the sender
     * @param message an additional message
     */
    public void sendRouteSuggestion(long mmsi, Route route, String sender, String message) throws InterruptedException,
            ExecutionException, TimeoutException {

        ServiceEndpoint<RouteSuggestionMessage, RouteSuggestionReply> end 
            = MaritimeCloudUtils.findServiceWithMmsi(routeSuggestionServiceList, (int)mmsi);

        RouteSuggestionMessage routeMessage = new RouteSuggestionMessage(route, sender, message);

        LOG.info("Sending to mmsi: " + mmsi + " with ID: " + routeMessage.getId());

        RouteSuggestionData suggestionData = new RouteSuggestionData(routeMessage, null, routeMessage.getId(), mmsi, false,
                RouteSuggestionStatus.RECEIVED_APP_ACK);
        RouteSuggestionKey routeSuggestionKey = new RouteSuggestionKey(mmsi, routeMessage.getId());
        routeSuggestions.put(routeSuggestionKey, suggestionData);

        if (end != null) {
            ConnectionFuture<RouteSuggestionReply> f = end.invoke(routeMessage);

            notifyRouteSuggestionListeners();

            f.handle(new BiConsumer<RouteSuggestionReply, Throwable>() {
                @Override
                public void accept(RouteSuggestionReply l, Throwable r) {
                    routeSuggestionReplyReceived(l);
                }
            });

        } else {
            // notifyRouteExchangeListeners();
            LOG.error("Could not find route suggestion service for MMSI " + mmsi);
        }

    }

    /**
     * Returns the sent route suggestions
     * @return the sent route suggestions
     */
    public Map<RouteSuggestionKey, RouteSuggestionData> getRouteSuggestions() {
        return routeSuggestions;
    }

    /**
     * Add a listener to the route suggestion service
     * 
     * @param listener
     */
    public synchronized void addRouteSuggestionListener(RouteSuggestionListener listener) {
        routeExchangeListener.add(listener);
    }

    /**
     * Broadcast a route update to all listeners
     */
    protected synchronized void notifyRouteSuggestionListeners() {

        for (RouteSuggestionListener listener : routeExchangeListener) {
            listener.routeUpdate();
        }
    }

    /**
     * Flags that the route suggestion ith the given mmsi and id has been acknowledged
     * 
     * @param mmsi the mmsi associated with the route suggestion
     * @param id the id of the route suggestion
     */
    public void setRouteSuggestionAcknowledged(long mmsi, long id) {
        routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setAcknowleged(true);
        notifyRouteSuggestionListeners();
    }

    /**
     * Removes the route suggestion with the given mmsi and id
     * 
     * @param mmsi the mmsi associated with the route suggestion
     * @param id the id of the route suggestion
     */
    public void removeSuggestion(long mmsi, long id) {
        routeSuggestions.remove(new RouteSuggestionKey(mmsi, id));
        notifyRouteSuggestionListeners();
    }

    /**
     * Returns the number of route suggestions that have not been acknowledged
     * @return the number of route suggestions that have not been acknowledged
     */
    public int getUnacknowledgedRouteSuggestions() {

        int counter = 0;

        Collection<RouteSuggestionData> c = routeSuggestions.values();

        // obtain an Iterator for Collection
        Iterator<RouteSuggestionData> itr = c.iterator();

        // iterate through HashMap values iterator
        while (itr.hasNext()) {
            RouteSuggestionData value = itr.next();
            if (!value.isAcknowleged()) {
                counter++;
            }
        }

        return counter;
    }

    /**
     * Called when a route suggestion reply has been received
     * @param message the reply
     */
    private void routeSuggestionReplyReceived(RouteSuggestionReply message) {

        LOG.info("MSG Received from MMSI: " + message.getMmsi() + " and ID " + message.getId());

        if (routeSuggestions.containsKey(new RouteSuggestionKey(message.getMmsi(), message.getId()))) {

            RouteSuggestionStatus response = message.getStatus();

            long mmsi = message.getMmsi();
            long id = message.getId();

            routeSuggestions.get(new RouteSuggestionKey(message.getMmsi(), message.getId())).setReply(message);

            switch (response) {
            case RECEIVED_ACCEPTED:
                if (routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).getStatus() != RouteSuggestionStatus.RECEIVED_ACCEPTED) {
                    // Accepted
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setStatus(RouteSuggestionStatus.RECEIVED_ACCEPTED);
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setAcknowleged(false);
                    notifyRouteSuggestionListeners();
                }

                break;
            case RECEIVED_REJECTED:
                // Rejected
                if (routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).getStatus() != RouteSuggestionStatus.RECEIVED_REJECTED) {
                    // Accepted
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setStatus(RouteSuggestionStatus.RECEIVED_REJECTED);
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setAcknowleged(false);
                    notifyRouteSuggestionListeners();
                }
                break;
            case RECEIVED_NOTED:
                // Noted
                if (routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).getStatus() != RouteSuggestionStatus.RECEIVED_NOTED) {
                    // Accepted
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setStatus(RouteSuggestionStatus.RECEIVED_NOTED);
                    routeSuggestions.get(new RouteSuggestionKey(mmsi, id)).setAcknowleged(false);
                    notifyRouteSuggestionListeners();
                }
                break;
            default:
                break;
            }
        }
    }
    
    
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

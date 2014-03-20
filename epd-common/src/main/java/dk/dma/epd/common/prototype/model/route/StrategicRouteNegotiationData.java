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
package dk.dma.epd.common.prototype.model.route;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;

/**
 * Data collected for strategic route negotiation
 */
public class StrategicRouteNegotiationData  implements Comparable<StrategicRouteNegotiationData> {

    private long id;
    private long mmsi;
    private List<StrategicRouteMessage> routeMessages = new ArrayList<StrategicRouteMessage>();
    private StrategicRouteStatus status;
    private boolean handled;
    
    
    public StrategicRouteNegotiationData(long id, long mmsi) {
        super();
        this.id = id;
        this.mmsi = mmsi;
        this.status = StrategicRouteStatus.PENDING;
        handled = false;
    }
    
    /**
     * Returns if there are associated route messages or not
     * @return if there are associated route messages or not
     */
    public boolean hasRouteMessages() {
        return routeMessages.size() > 0;
    }
    
    /**
     * Returns the original route message
     * @return the original route message
     */
    public StrategicRouteMessage getOriginalRouteMessage() {
        return !hasRouteMessages() ? null : routeMessages.get(0);
    }
    
    /**
     * Returns the latest route message
     * @return the latest route message
     */
    public StrategicRouteMessage getLatestRouteMessage() {
        return !hasRouteMessages() ? null : routeMessages.get(routeMessages.size() - 1);
    }
    
    /**
     * Returns the original route
     * @return the original route
     */
    public Route getOriginalRoute() {
        return !hasRouteMessages() ? null : new Route(getOriginalRouteMessage().getRoute());
    }
    
    /**
     * Returns the latest route
     * @return the latest route
     */
    public Route getLatestRoute() {
        return !hasRouteMessages() ? null : new Route(getLatestRouteMessage().getRoute());
    }
    
    /**
     * Returns the latest accepted route.
     * Returns null if none is present
     * @return the latest accepted route
     */
    public Route getLatestAcceptedRoute() {
        return getLatestAcceptedRoute(routeMessages.size() - 1, false);
    }
    
    /**
     * Returns the latest accepted route.
     * Returns null if none is present
     * @param startIndex the start index of the messages
     * @param defaultOriginal whether to fall back to use the original or not
     * @return the latest accepted route
     */
    public Route getLatestAcceptedRoute(int startIndex, boolean defaultOriginal) {
        if (hasRouteMessages()) {
            for (int x = startIndex; x >= 0; x--) {
                if (routeMessages.get(x).getStatus() == StrategicRouteStatus.AGREED &&
                        routeMessages.get(x).getRoute() != null) {
                    return new Route(routeMessages.get(x).getRoute());
                }
            }
            if (defaultOriginal) {
                return getOriginalRoute();
            }
        }
        return null;
    }
    
    /**
     * Returns the latest accepted route and if there is no accepted route, returns the original.
     * @return the latest accepted route or the original
     */
    public Route getLatestAcceptedOrOriginalRoute() {
        return getLatestAcceptedRoute(routeMessages.size() - 1, true);
    }
    
    /**
     * Returns the original date of the transaction
     * @return the original date of the transaction
     */
    public Date getOriginalSentDate() {
        return !hasRouteMessages() ? null : getOriginalRouteMessage().getSentDate();
    }
    
    /**
     * Returns the latest date of the transaction
     * @return the latest date of the transaction
     */
    public Date getLatestSentDate() {
        return !hasRouteMessages() ? null : getLatestRouteMessage().getSentDate();
    }
    
    /**
     * Adds a route message and updates the status to match the status of the route message
     * @param message the route message to add
     */
    public void addMessage(StrategicRouteMessage message){
        routeMessages.add(message);
        status = message.getStatus();
    }
    
    public StrategicRouteStatus getStatus() {
        return status;
    }

    public void setStatus(StrategicRouteStatus status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }
    
    public long getMmsi() {
        return mmsi;
    }
    
    public List<StrategicRouteMessage> getRouteMessage() {
        return routeMessages;
    }
    
    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(StrategicRouteNegotiationData other) {
        Date d1 = getLatestSentDate();
        Date d2 = other.getLatestSentDate();
        if (d1 == null && d2 == null) {
            return 0;
        } else if (d1 == null) {
            return -1;
        } else if (d2 == null) {
            return 1;
        } else {
            return d1.compareTo(d2);
        }
    }
}

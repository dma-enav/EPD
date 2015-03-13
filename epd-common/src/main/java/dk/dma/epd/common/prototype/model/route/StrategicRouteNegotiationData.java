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
package dk.dma.epd.common.prototype.model.route;

import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon.CloudMessageStatus;
import dk.dma.epd.common.prototype.service.MaritimeCloudData;
import dma.route.StrategicRouteMessage;
import dma.route.StrategicRouteStatus;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data collected for strategic route negotiation
 */
public class StrategicRouteNegotiationData  implements Comparable<StrategicRouteNegotiationData> {

    private long id;
    private long mmsi;
    private List<StrategicRouteMessageData> routeMessages = new ArrayList<>();
    private StrategicRouteStatus status;
    private boolean handled;
    
    /**
     * Constructor
     * @param id the transaction id
     * @param mmsi the MMSI of the counter-party
     */
    public StrategicRouteNegotiationData(long id, long mmsi) {
        super();
        this.id = id;
        this.mmsi = mmsi;
        this.status = StrategicRouteStatus.PENDING;
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
    public StrategicRouteMessageData getOriginalRouteMessage() {
        return !hasRouteMessages() ? null : routeMessages.get(0);
    }
    
    /**
     * Returns the latest route message
     * @return the latest route message
     */
    public StrategicRouteMessageData getLatestRouteMessage() {
        return !hasRouteMessages() ? null : routeMessages.get(routeMessages.size() - 1);
    }
    
    /**
     * Returns the original route
     * @return the original route
     */
    public Route getOriginalRoute() {
        return !hasRouteMessages() ? null : new Route(getOriginalRouteMessage().getMsg().getRoute());
    }
    
    /**
     * Returns the latest route
     * @return the latest route
     */
    public Route getLatestRoute() {
        return !hasRouteMessages() ? null : new Route(getLatestRouteMessage().getMsg().getRoute());
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
                if (routeMessages.get(x).getMsg().getStatus() == StrategicRouteStatus.AGREED &&
                        routeMessages.get(x).getMsg().getRoute() != null) {
                    return new Route(routeMessages.get(x).getMsg().getRoute());
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
        return !hasRouteMessages() ? null : getOriginalRouteMessage().getSendDate();
    }
    
    /**
     * Returns the latest date of the transaction
     * @return the latest date of the transaction
     */
    public Date getLatestSentDate() {
        return !hasRouteMessages() ? null : getLatestRouteMessage().getSendDate();
    }
    
    /**
     * Adds a route message and updates the status to match the status of the route message
     * @param message the route message to add
     * @param sentDate the date the message was sent
     * @param cloudStatus the Maritime Cloud status of the message
     * @param fromStcc whether the message is from the STCC or the ship
     * @return the strategic route message data
     */
    public StrategicRouteMessageData addMessage(StrategicRouteMessage message, Date sentDate, CloudMessageStatus cloudStatus, boolean fromStcc){
        StrategicRouteMessageData data = new StrategicRouteMessageData(message, sentDate, cloudStatus, fromStcc);
        routeMessages.add(data);
        status = message.getStatus();
        return data;
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
    
    public List<StrategicRouteMessageData> getRouteMessage() {
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


    /**
     * Encapsulates a StrategicRouteMessage along with header information
     */
    public static class StrategicRouteMessageData extends MaritimeCloudData implements Serializable {

        StrategicRouteMessage msg;
        private boolean fromStcc;

        /**
         * No-arg constructor
         */
        public StrategicRouteMessageData() {
            super();
        }

        /**
         * Constructor
         * @param msg the message
         * @param sentDate the send date
         * @param cloudStatus the Maritime Cloud status of the message
         * @param fromStcc whether the message is from the STCC or the ship
         */
        public StrategicRouteMessageData(StrategicRouteMessage msg, Date sentDate, CloudMessageStatus cloudStatus, boolean fromStcc) {
            super(cloudStatus, sentDate);
            this.msg = msg;
            this.fromStcc = fromStcc;
        }

        /**
         * Returns the color associated with the message status
         * @return the color associated with the message status
         */
        public Color getColor() {
            switch (msg.getStatus()) {
                case PENDING: return Color.YELLOW;
                case AGREED: return new Color(130, 165, 80);
                case REJECTED: return new Color(165, 80, 80);
                case NEGOTIATING: return Color.YELLOW;
                case CANCELED: return new Color(165, 80, 80);
            }
            return Color.gray;
        }

        public StrategicRouteMessage getMsg() {
            return msg;
        }

        public void setMsg(StrategicRouteMessage msg) {
            this.msg = msg;
        }

        public boolean isFromStcc() {
            return fromStcc;
        }

        public void setFromStcc(boolean fromStcc) {
            this.fromStcc = fromStcc;
        }
    }
}

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

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionStatus;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dma.route.TacticalRouteSuggestion;

/**
 * Used for caching the negotiation data used tactical routes
 */
public class RouteSuggestionData implements Comparable<RouteSuggestionData>, Serializable {

    private static final long serialVersionUID = -3345162806743074138L;

    private TacticalRouteSuggestion message;
    // private TacticalRouteSuggestion reply;
    private long mmsi;
    private boolean acknowleged;
    private Route route;

    /**
     * Constructor
     * 
     * @param routeSegmentSuggestion
     * @param mmsi
     */
    public RouteSuggestionData(TacticalRouteSuggestion routeSegmentSuggestion, long mmsi) {
        this.message = Objects.requireNonNull(routeSegmentSuggestion);
        this.mmsi = Objects.requireNonNull(mmsi);
        this.route = Objects.requireNonNull(new Route(routeSegmentSuggestion.getRoute()));
    }

    /**
     * Returns the latest message, i.e. the reply if defined and the original message otherwise
     * 
     * @return the latest message
     */
    public RouteSuggestionMessage getLatestMessage() {
        return (reply == null) ? message : reply;
    }

    public RouteSuggestionMessage getMessage() {
        return message;
    }

    public RouteSuggestionMessage getReply() {
        return reply;
    }

    public void setReply(RouteSuggestionMessage reply) {
        this.reply = reply;
    }

    public long getId() {
        return message.getId();
    }

    public long getMmsi() {
        return mmsi;
    }

    public boolean isAcknowleged() {
        return acknowleged;
    }

    public void setAcknowleged(boolean acknowleged) {
        this.acknowleged = acknowleged;
    }

    public RouteSuggestionStatus getStatus() {
        return getLatestMessage().getStatus();
    }

    public Route getRoute() {
        return route;
    }

    public boolean isReplied() {
        return reply != null;
    }

    @Override
    public String toString() {
        return "RouteSuggestionData [message=" + message + ", reply=" + reply + ", id=" + getId() + ", mmsi=" + mmsi
                + ", acknowleged=" + acknowleged + ", status=" + getStatus() + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(RouteSuggestionData other) {
        Date d1 = getLatestMessage().getSentDate();
        Date d2 = other.getLatestMessage().getSentDate();
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

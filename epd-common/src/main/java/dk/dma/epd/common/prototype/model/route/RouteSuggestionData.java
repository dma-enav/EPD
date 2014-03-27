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

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionStatus;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;

/**
 * Used for caching the negotiation data used tactical routes
 */
public class RouteSuggestionData implements Comparable<RouteSuggestionData>, Serializable {

    private static final long serialVersionUID = -3345162806743074138L;
    
    private RouteSuggestionMessage message;
    private RouteSuggestionMessage reply;
    private long mmsi;
    private boolean acknowleged;
    private Route route;

    /**
     * Constructor
     * 
     * @param message
     * @param mmsi
     */
    public RouteSuggestionData(RouteSuggestionMessage message, long mmsi) {
        this.message = Objects.requireNonNull(message);
        this.mmsi = Objects.requireNonNull(mmsi);
        this.route = Objects.requireNonNull(new Route(message.getRoute()));
    }

    /**
     * Returns the latest message, i.e. the reply if defined and
     * the original message otherwise
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
        return "RouteSuggestionData [message=" + message + ", reply="
                + reply + ", id=" + getId() + ", mmsi=" + mmsi + ", acknowleged="
                + acknowleged + ", status=" + getStatus() + "]";
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

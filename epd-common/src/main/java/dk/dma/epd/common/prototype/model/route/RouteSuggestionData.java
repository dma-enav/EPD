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

import java.awt.Color;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon.CloudMessageStatus;
import dma.route.RouteSegmentSuggestionStatus;
import dma.route.TacticalRouteSuggestion;
import dma.route.TacticalRouteSuggestionReply;

/**
 * Used for caching the negotiation data used tactical routes
 */
public class RouteSuggestionData implements Serializable, Comparable<RouteSuggestionData> {
    // implements Comparable<RouteSuggestionData>,
    private static final long serialVersionUID = -3345162806743074138L;

    private TacticalRouteSuggestion message;
    private TacticalRouteSuggestionReply reply;
    private long mmsi;
    private boolean acknowleged;
    private Route route;
    private Date sendDate;
    private Date replyRecieveDate;

    private CloudMessageStatus cloudMessageStatus;

    /**
     * Constructor
     * 
     * @param routeSegmentSuggestion
     * @param mmsi
     */
    public RouteSuggestionData(TacticalRouteSuggestion routeSegmentSuggestion, long mmsi, dk.dma.enav.model.voyage.Route route) {
        this.message = Objects.requireNonNull(routeSegmentSuggestion);
        this.mmsi = Objects.requireNonNull(mmsi);
        this.route = Objects.requireNonNull(new Route(route));
        this.sendDate = new Date();
        this.cloudMessageStatus = CloudMessageStatus.NOT_SENT;
    }

    // /**
    // * Returns the latest message, i.e. the reply if defined and the original message otherwise
    // *
    // * @return the latest message
    // */
    // public TacticalRouteSuggestionReply getLatestMessage() {
    // return reply;
    // }

    public TacticalRouteSuggestion getMessage() {
        return message;
    }

    public TacticalRouteSuggestionReply getReply() {
        return reply;
    }

    public void setReply(TacticalRouteSuggestionReply reply, Date replyDate) {
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

    public RouteSegmentSuggestionStatus getStatus() {
        if (reply == null) {
            return RouteSegmentSuggestionStatus.PENDING;
        } else {
            return reply.getStatus();
        }

    }

    /**
     * @return the cloudMessageStatus
     */
    public CloudMessageStatus getCloudMessageStatus() {
        return cloudMessageStatus;
    }

    /**
     * @param cloudMessageStatus
     *            the cloudMessageStatus to set
     */
    public void setCloudMessageStatus(CloudMessageStatus newStatus) {
        this.cloudMessageStatus = newStatus.combine(cloudMessageStatus);
    }

    public Route getRoute() {
        return route;
    }

    public boolean isReplied() {
        return reply != null;
    }

    /**
     * @return the sendDate
     */
    public Date getSendDate() {
        return sendDate;
    }

    /**
     * @param sendDate
     *            the sendDate to set
     */
    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    /**
     * @return the replyRecieveDate
     */
    public Date getReplyRecieveDate() {
        return replyRecieveDate;
    }

    /**
     * @param replyRecieveDate
     *            the replyRecieveDate to set
     */
    public void setReplyRecieveDate(Date replyRecieveDate) {
        this.replyRecieveDate = replyRecieveDate;
    }

    @Override
    public String toString() {
        return "RouteSuggestionData [message=" + message + ", reply=" + reply + ", id=" + getId() + ", mmsi=" + mmsi
                + ", acknowleged=" + acknowleged + ", status=" + getStatus() + "]";
    }

    public Color replySuggestionColor() {

        if (isReplied()) {

            RouteSegmentSuggestionStatus status = reply.getStatus();

            switch (status) {
            case ACCEPTED:
                return new Color(130, 165, 80);

            case PENDING:
                return Color.YELLOW;
            case REJECTED:
                return new Color(165, 80, 80);

            }

        }
        return Color.GRAY;

    }

    public Date getLatestDateUpdate() {
        if (isReplied()) {
            return replyRecieveDate;
        } else {
            return sendDate;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(RouteSuggestionData other) {
        Date d1 = getLatestDateUpdate();
        Date d2 = other.getLatestDateUpdate();
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

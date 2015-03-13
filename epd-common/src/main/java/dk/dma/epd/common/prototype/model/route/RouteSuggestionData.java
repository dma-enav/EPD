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
import dma.route.RouteSegmentSuggestionStatus;
import dma.route.TacticalRouteSuggestion;
import dma.route.TacticalRouteSuggestionReply;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Used for caching the negotiation data used tactical routes
 */
public class RouteSuggestionData extends MaritimeCloudData implements Serializable, Comparable<RouteSuggestionData> {

    private static final long serialVersionUID = -3345162806743074138L;

    private TacticalRouteSuggestion message;
    private TacticalRouteSuggestionReply reply;
    private long mmsi;
    private boolean acknowleged;
    private Route route;
    private Date replyRecieveDate;

    /**
     * Sadly MSDL generated classes are not serializable. Handle serialization ourselves
     * @param oos the output stream
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(message == null ? null : message.toJSON());
        oos.writeObject(reply == null ? null : reply.toJSON());
        oos.writeObject(mmsi);
        oos.writeObject(acknowleged);
        oos.writeObject(route);
        oos.writeObject(sendDate);
        oos.writeObject(replyRecieveDate);
        oos.writeObject(cloudMessageStatus);
    }

    /**
     * Sadly MSDL generated classes are not serializable. Handle serialization ourselves
     * @param ois the input stream
     */
    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        String msgString = (String)ois.readObject();
        message = msgString == null ? null : TacticalRouteSuggestion.fromJSON(msgString);
        String replyString = (String)ois.readObject();
        reply = replyString == null ? null : TacticalRouteSuggestionReply.fromJSON(replyString);
        mmsi = (Long)ois.readObject();
        acknowleged = (Boolean)ois.readObject();
        route = (Route)ois.readObject();
        sendDate = (Date)ois.readObject();
        replyRecieveDate = (Date)ois.readObject();
        cloudMessageStatus = (CloudMessageStatus)ois.readObject();

    }

    /**
     * Constructor
     * 
     * @param routeSegmentSuggestion
     * @param mmsi
     */
    public RouteSuggestionData(TacticalRouteSuggestion routeSegmentSuggestion, long mmsi, Route route) {
        super();
        this.message = Objects.requireNonNull(routeSegmentSuggestion);
        this.mmsi = Objects.requireNonNull(mmsi);
        this.route = Objects.requireNonNull(new Route(route));
    }

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

    public Route getRoute() {
        return route;
    }

    public boolean isReplied() {
        return reply != null;
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

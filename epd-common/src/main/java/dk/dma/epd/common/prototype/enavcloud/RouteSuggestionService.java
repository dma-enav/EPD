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
package dk.dma.epd.common.prototype.enavcloud;

import static java.util.Objects.requireNonNull;

import java.util.Date;

import net.maritimecloud.net.service.spi.ServiceInitiationPoint;
import net.maritimecloud.net.service.spi.ServiceMessage;
import dk.dma.enav.model.voyage.Route;

/**
 * Maritime cloud service for exchanging routes suggestions from 
 * a sea traffic control center (STCC) to ship.
 * <p>
 * Defines the service initiation point along with the following classes:
 * <ul>
 *   <li>{@linkplain RouteSuggestionStatus} status of the route suggestion.</li>
 *   <li>{@linkplain RouteSuggestionMessage} Used for sending a suggested route from STCC to a ship.</li>
 *   <li>{@linkplain RouteSuggestionReply} Used for sending a reply from a ship to the STCC.</li>
 * </ul>
 */
public class RouteSuggestionService {
    
    /** An initiation point */
    public static final ServiceInitiationPoint<RouteSuggestionMessage> INIT = new ServiceInitiationPoint<>(
            RouteSuggestionMessage.class);
    
    /**
     * Status of the route suggestion
     */
    public enum RouteSuggestionStatus {
        NOT_SENT("Not sent", "Not sent - check network status"), 
        FAILED("Failed", "Failed to send to target"), 
        SENT_NOT_ACK("Sent but not received", "Sent but no answer from route aplication"), 
        RECEIVED_APP_ACK("Sent", "Sent and acknowleged by application but not user"), 
        RECEIVED_ACCEPTED("Accepted", "Route Suggestion Accepted by ship"), 
        RECEIVED_REJECTED("Rejected", "Route Suggestion Rejected by user"), 
        RECEIVED_NOTED("Noted", "Route Suggestion Noted by user");
        
        private String descShort, descLong;
        
        private RouteSuggestionStatus(String descShort, String descLong) {
            this.descShort = descShort;
            this.descLong = descLong;
        }

        public String getDescShort() {
            return descShort;
        }

        public String getDescLong() {
            return descLong;
        }
    }
    
    /**
     * Used for sending a suggested route from STCC to a ship
     */
    public static class RouteSuggestionMessage extends
            ServiceMessage<RouteSuggestionReply> {
        private Route route;
        private Date sent;
        private String sender;
        private String message;
        private long id;
        
        /**
         * Constructor
         */
        public RouteSuggestionMessage() {
        }

        /**
         * Constructor
         * 
         * @param route the suggested route
         * @param sender the sender
         * @param message an additional message
         */
        public RouteSuggestionMessage(Route route, String sender, String message) {
            this.route = requireNonNull(route);
            this.sender = requireNonNull(sender);
            this.id = requireNonNull(System.currentTimeMillis());
            this.sent = requireNonNull(new Date());
            this.message = requireNonNull(message);
        }

        public String getMessage() {
            return message;
        }

        /**
         * @return the route
         */
        public Route getRoute() {
            return route;
        }
        
        public String getSender() {
            return sender;
        }

        public Date getSent() {
            return sent;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * @param route the route to set
         */
        public void setRoute(Route route) {
            this.route = route;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public void setSent(Date sent) {
            this.sent = sent;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
        
        
    }

    /**
     * Used for sending a reply from a ship to the STCC
     */
    public static class RouteSuggestionReply extends ServiceMessage<Void> {

        private String message;
        private long id;
        private long mmsi;
        private long sendDate;
        private RouteSuggestionStatus status;
  
        /**
         * Constructor
         */
        public RouteSuggestionReply() {
        }

        /**
         * Constructor
         * 
         * @param message a message
         * @param id id of the original suggestion
         * @param mmsi the MMSI of the vessel
         * @param sendDate the send date
         * @param status the reply status
         */
        public RouteSuggestionReply(String message, long id, long mmsi, long sendDate, RouteSuggestionStatus status) {
            this.message = message;
            this.id = id;
            this.mmsi = mmsi;
            this.sendDate = sendDate;
            this.status = status;
        }

        /**
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @param message the message to set
         */
        public void setMessage(String message) {
            this.message = message;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getMmsi() {
            return mmsi;
        }

        public void setMmsi(long mmsi) {
            this.mmsi = mmsi;
        }

        public long getSendDate() {
            return sendDate;
        }

        public void setSendDate(long sendDate) {
            this.sendDate = sendDate;
        }

        public RouteSuggestionStatus getStatus() {
            return status;
        }

        public void setStatus(RouteSuggestionStatus status) {
            this.status = status;
        }
    }
}

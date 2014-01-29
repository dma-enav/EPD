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
 * Defines the route suggestion maritime cloud service
 */
public class RouteSuggestionService {
    
    /** An initiation point */
    public static final ServiceInitiationPoint<RouteSuggestionMessage> INIT = new ServiceInitiationPoint<>(
            RouteSuggestionMessage.class);
    
    /**
     * Status of the suggested route
     */
    public enum RouteSuggestionStatus {
        NOT_SENT, 
        FAILED, 
        SENT_NOT_ACK, 
        RECEIVED_APP_ACK, 
        RECEIVED_ACCEPTED, 
        RECEIVED_REJECTED, 
        RECEIVED_NOTED
    }
    
    /**
     * Route suggestion reply class
     */
    public static class RouteSuggestionReply extends ServiceMessage<Void> {

        private String message;
        private long id;
        private long mmsi;
        private long sendDate;
        private RouteSuggestionStatus status;
  

        public RouteSuggestionReply() {
        }

        /**
         * @param message
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
         * @param message
         *            the message to set
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
    
    
    /**
     * Route suggestion message class
     */
    public static class RouteSuggestionMessage extends
            ServiceMessage<RouteSuggestionReply> {
        private Route route;
        private Date sent;
        private String sender;
        private String message;
        private long id;
        

        public RouteSuggestionMessage() {
        }

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
         * @param route
         *            the route to set
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

}

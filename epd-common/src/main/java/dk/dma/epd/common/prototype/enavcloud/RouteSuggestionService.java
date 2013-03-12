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

import dk.dma.enav.communication.service.ServiceInitiationPoint;
import dk.dma.enav.communication.service.spi.MaritimeServiceMessage;
import dk.dma.enav.model.voyage.Route;

public class RouteSuggestionService {
    
    /** An initiation point */
    public static final ServiceInitiationPoint<RouteSuggestionMessage> INIT = new ServiceInitiationPoint<>(
            RouteSuggestionMessage.class);

    public static class RouteSuggestionAck extends MaritimeServiceMessage<Void> {

        private String message;

        public RouteSuggestionAck() {
        }

        /**
         * @param message
         */
        public RouteSuggestionAck(String message) {
            this.message = message;
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
    }
    
    

    public static class RouteSuggestionMessage extends
            MaritimeServiceMessage<RouteSuggestionAck> {
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
            this.message = requireNonNull("Route Send Example");
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

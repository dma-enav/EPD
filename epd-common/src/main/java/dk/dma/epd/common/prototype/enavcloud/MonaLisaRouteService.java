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

import dk.dma.enav.communication.service.spi.ServiceInitiationPoint;
import dk.dma.enav.communication.service.spi.ServiceMessage;
import dk.dma.enav.model.voyage.Route;

public class MonaLisaRouteService {
    
    /** An initiation point */
    public static final ServiceInitiationPoint<MonaLisaRouteRequestMessage> INIT = new ServiceInitiationPoint<>(
            MonaLisaRouteRequestMessage.class);
    
    public enum MonaLisaRouteStatus {
        PENDING, AGREED, NOT_AGREED
    }
    
    public static class MonaLisaRouteRequestReply extends ServiceMessage<Void> {

        private String message;
        private long id;
        private long mmsi;
        private long sendDate;
        private MonaLisaRouteStatus status;
        private Route route;

        public MonaLisaRouteRequestReply() {
        }

        /**
         * @param message
         */
        public MonaLisaRouteRequestReply(String message, long id, long mmsi, long sendDate, MonaLisaRouteStatus status, Route route) {
            this.message = message;
            this.id = id;
            this.mmsi = mmsi;
            this.sendDate = sendDate;
            this.status = status;
            this.route = route;
        }

        /**
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        
        /**
         * @return the route
         */
        public Route getRoute() {
            return route;
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

        public MonaLisaRouteStatus getStatus() {
            return status;
        }

        public void setStatus(MonaLisaRouteStatus status) {
            this.status = status;
        }
        
        /**
         * @param route
         *            the route to set
         */
        public void setRoute(Route route) {
            this.route = route;
        }

        
    }
    
    public static class MonaLisaRouteRequestMessage extends
            ServiceMessage<MonaLisaRouteRequestReply> {
        private Route route;
        private Date sent;
        private String sender;
        private String message;
        private long id;
        

        public MonaLisaRouteRequestMessage() {
        }

        public MonaLisaRouteRequestMessage(long id, Route route, String sender, String message) {
            this.route = requireNonNull(route);
            this.sender = requireNonNull(sender);
            this.id = requireNonNull(id);
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

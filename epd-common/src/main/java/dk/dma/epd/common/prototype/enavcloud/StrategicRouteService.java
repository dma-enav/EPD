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
 * Maritime cloud service for exchanging strategic routes (formerly "Mona Lisa").
 * <p>
 * Defines the service initiation point along with the following classes:
 * <ul>
 *   <li>{@linkplain StrategicRouteStatus} status of the strategic route.</li>
 *   <li>{@linkplain StrategicRouteRequestMessage} Used for sending a request from a ship to an STCC.</li>
 *   <li>{@linkplain StrategicRouteRequestReply} Used for sending a reply an STCC to a ship.</li>
 * </ul>
 */
public class StrategicRouteService {
    
    /** An initiation point */
    public static final ServiceInitiationPoint<StrategicRouteRequestMessage> INIT = new ServiceInitiationPoint<>(
            StrategicRouteRequestMessage.class);
    
    /**
     * Defines the current status of a strategic route exchange between a ship and an STTC.
     */
    public enum StrategicRouteStatus {
        PENDING, AGREED, REJECTED, NEGOTIATING, CANCELED
    }
    
    /**
     * Used for sending a request from a ship to an STCC.
     */
    public static class StrategicRouteRequestMessage extends
        ServiceMessage<StrategicRouteRequestReply> {
        private Route route;
        private Date sent;
        private long mmsi;
        private String message;
        private long id;

        /**
         * No-arg constructor
         */
        public StrategicRouteRequestMessage() {
        }

        /**
         * Constructor
         * 
         * @param id id of the route
         * @param route the route
         * @param mmsi the MMSI of the vessel
         * @param message an additional message
         */
        public StrategicRouteRequestMessage(long id, Route route, long mmsi, String message) {
            this.route = requireNonNull(route);
            this.mmsi = requireNonNull(mmsi);
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

        /**
         * @return the mmsi
         */
        public long getMmsi() {
            return mmsi;
        }

        /**
         * @param mmsi the mmsi to set
         */
        public void setMmsi(long mmsi) {
            this.mmsi = mmsi;
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
     * Used for sending a reply an STCC to a ship
     */
    public static class StrategicRouteRequestReply extends ServiceMessage<Void> {

        private String message;
        private long id;
        private long mmsi;
        private long sendDate;
        private StrategicRouteStatus status;
        private Route route;

        public StrategicRouteRequestReply() {
        }

        /**
         * Constructor
         * 
         * @param message a message
         * @param id id of the route
         * @param mmsi the MMSI of the vessel
         * @param sendDate the send date
         * @param status the reply status
         * @param route the route
         */
        public StrategicRouteRequestReply(String message, long id, long mmsi, long sendDate, StrategicRouteStatus status, Route route) {
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

        public StrategicRouteStatus getStatus() {
            return status;
        }

        public void setStatus(StrategicRouteStatus status) {
            this.status = status;
        }
        
        /**
         * @param route the route to set
         */
        public void setRoute(Route route) {
            this.route = route;
        }
    }
}

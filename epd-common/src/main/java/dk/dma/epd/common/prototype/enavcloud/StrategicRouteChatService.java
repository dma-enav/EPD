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
 * Not used. Should it be deleted?
 */
public class StrategicRouteChatService {

    /** An initiation point */
    public static final ServiceInitiationPoint<StrategicRouteChatAck> INIT = new ServiceInitiationPoint<>(
            StrategicRouteChatAck.class);

    public static class StrategicRouteChatMsg extends ServiceMessage<Void> {

        private String message;
        private long id;
        private long mmsi;
        private long sendDate;
        private String name;
        
        public StrategicRouteChatMsg() {
        }

        /**
         * @param message
         */
        public StrategicRouteChatMsg(String message, long id, long mmsi, long sendDate, String name) {
            this.message = message;
            this.id = id;
            this.mmsi = mmsi;
            this.sendDate = sendDate;
            this.name = name;
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

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        
    }

    public static class StrategicRouteChatAck extends ServiceMessage<StrategicRouteChatAck> {
        private Date sent;
        private long mmsi;

        public StrategicRouteChatAck() {
        }

        public StrategicRouteChatAck(long id, Route route, long mmsi, String message) {
            this.mmsi = requireNonNull(mmsi);
            this.sent = requireNonNull(new Date());
        }

        /**
         * @return the mmsi
         */
        public long getMmsi() {
            return mmsi;
        }

        /**
         * @param mmsi
         *            the mmsi to set
         */
        public void setMmsi(long mmsi) {
            this.mmsi = mmsi;
        }

        public Date getSent() {
            return sent;
        }

        public void setSent(Date sent) {
            this.sent = sent;
        }

    }

}

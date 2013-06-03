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

import dk.dma.enav.communication.service.spi.ServiceInitiationPoint;
import dk.dma.enav.communication.service.spi.ServiceMessage;

public class MonaLisaRouteAck {

    /** An initiation point */
    public static final ServiceInitiationPoint<MonaLisaRouteAckMsg> INIT = new ServiceInitiationPoint<>(
            MonaLisaRouteAckMsg.class);

    public static class MonaLisaRouteAckMsg extends ServiceMessage<Void> {

        private boolean ack;
        private long id;
        private long mmsi;
        private String message;
        
        public MonaLisaRouteAckMsg() {
        }

        /**
         * @param message
         */
        public MonaLisaRouteAckMsg(boolean ack, long id, long mmsi, String message) {
            this.ack = ack;
            this.id = id;
            this.mmsi = mmsi;
            this.message = message;
        }

        
        /**
         * @return the id
         */
        public long getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(long id) {
            this.id = id;
        }

        public boolean isAck() {
            return ack;
        }

        public void setAck(boolean ack) {
            this.ack = ack;
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
        
        

    }

}

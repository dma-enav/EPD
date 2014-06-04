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
import dk.dma.enav.model.voct.DatumPointDTO;
import dk.dma.enav.model.voct.EffortAllocationDTO;
import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon.SRU_NETWORK_STATUS;

public class VOCTCommunicationServiceDatumPoint {

    /** An initiation point */
    public static final ServiceInitiationPoint<VOCTCommunicationMessageDatumPoint> INIT = new ServiceInitiationPoint<>(
            VOCTCommunicationMessageDatumPoint.class);

    public static class VOCTCommunicationReplyDatumPoint extends ServiceMessage<Void> {

        // private String message;
        private long id;
        private long mmsi;
        private long sendDate;

        public VOCTCommunicationReplyDatumPoint() {
        }

        /**
         * @param message
         */
        public VOCTCommunicationReplyDatumPoint(long id, long mmsi, long sendDate) {
            this.id = requireNonNull(id);
            this.mmsi = requireNonNull(mmsi);
            this.sendDate = requireNonNull(sendDate);
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

    }

    public static class VOCTCommunicationMessageDatumPoint extends ServiceMessage<VOCTCommunicationReplyDatumPoint> {

        private DatumPointDTO sarData;
        private EffortAllocationDTO effortAllocationData;
        private Route searchPattern;

        private Date sent;
        private String sender;
        private String message;
        private long id;
        private SRU_NETWORK_STATUS status;

        public VOCTCommunicationMessageDatumPoint() {
        }

        public VOCTCommunicationMessageDatumPoint(DatumPointDTO sarData, EffortAllocationDTO effortAllocationData,
                Route searchPattern, String sender, String message, long id) {
            super();
            this.sarData = requireNonNull(sarData);
            this.effortAllocationData = effortAllocationData;
            this.searchPattern = searchPattern;
            this.sent = requireNonNull(new Date());
            this.message = requireNonNull(message);
            this.sender = requireNonNull(sender);
            this.id = requireNonNull(id);
        }

        /**
         * Constructor - used for replys
         */
        public VOCTCommunicationMessageDatumPoint(long id, String message, SRU_NETWORK_STATUS status) {
            this.id = id;
            this.message = message;
            this.status = requireNonNull(status);
        }

        /**
         * @return the sarData
         */
        public DatumPointDTO getSarData() {
            return sarData;
        }

        /**
         * @param sarData
         *            the sarData to set
         */
        public void setSarData(DatumPointDTO sarData) {
            this.sarData = sarData;
        }

        /**
         * @return the effortAllocationData
         */
        public EffortAllocationDTO getEffortAllocationData() {
            return effortAllocationData;
        }

        /**
         * @param effortAllocationData
         *            the effortAllocationData to set
         */
        public void setEffortAllocationData(EffortAllocationDTO effortAllocationData) {
            this.effortAllocationData = effortAllocationData;
        }

        /**
         * @return the searchPattern
         */
        public Route getSearchPattern() {
            return searchPattern;
        }

        /**
         * @param searchPattern
         *            the searchPattern to set
         */
        public void setSearchPattern(Route searchPattern) {
            this.searchPattern = searchPattern;
        }

        /**
         * @return the sent
         */
        public Date getSent() {
            return sent;
        }

        /**
         * @param sent
         *            the sent to set
         */
        public void setSent(Date sent) {
            this.sent = sent;
        }

        /**
         * @return the sender
         */
        public String getSender() {
            return sender;
        }

        /**
         * @param sender
         *            the sender to set
         */
        public void setSender(String sender) {
            this.sender = sender;
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

        /**
         * @return the id
         */
        public long getId() {
            return id;
        }

        /**
         * @param id
         *            the id to set
         */
        public void setId(long id) {
            this.id = id;
        }

        /**
         * @return the status
         */
        public SRU_NETWORK_STATUS getStatus() {
            return status;
        }

        /**
         * @param status
         *            the status to set
         */
        public void setStatus(SRU_NETWORK_STATUS status) {
            this.status = status;
        }

    }

}

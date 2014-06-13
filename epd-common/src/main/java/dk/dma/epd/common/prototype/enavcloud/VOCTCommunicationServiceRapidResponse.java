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

import java.io.Serializable;
import java.util.Date;

import net.maritimecloud.net.service.spi.ServiceInitiationPoint;
import net.maritimecloud.net.service.spi.ServiceMessage;
import dk.dma.enav.model.voct.EffortAllocationDTO;
import dk.dma.enav.model.voct.RapidResponseDTO;
import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon.VoctMsgStatus;

public class VOCTCommunicationServiceRapidResponse {

    /** An initiation point */
    public static final ServiceInitiationPoint<VOCTCommunicationMessageRapidResponse> INIT = new ServiceInitiationPoint<>(
            VOCTCommunicationMessageRapidResponse.class);

    public static class VOCTCommunicationMessageRapidResponse extends ServiceMessage<VOCTCommunicationReplyRapidResponse> implements
            Serializable {

        private static final long serialVersionUID = -3556815314608410502L;
        private RapidResponseDTO sarData;
        private EffortAllocationDTO effortAllocationData;
        private Route searchPattern;

        private Date sent;
        private String sender;
        private String message;
        private long id;
        private VoctMsgStatus status;
        private long receiversMMSI;

        public VOCTCommunicationMessageRapidResponse() {
        }

        public VOCTCommunicationMessageRapidResponse(RapidResponseDTO sarData, EffortAllocationDTO effortAllocationData,
                Route searchPattern, String sender, String message, long id, long receiversMMSI) {
            super();
            this.sarData = requireNonNull(sarData);
            this.effortAllocationData = effortAllocationData;
            this.searchPattern = searchPattern;
            this.sent = requireNonNull(new Date());
            this.message = requireNonNull(message);
            this.sender = requireNonNull(sender);
            this.id = requireNonNull(id);
            this.receiversMMSI = receiversMMSI;

        }

        /**
         * Constructor - used for replys
         */
        public VOCTCommunicationMessageRapidResponse(long id, String message, VoctMsgStatus status) {
            this.id = id;
            this.message = message;
            this.status = requireNonNull(status);
        }

        /**
         * Constructor - used to withdraw from an operation
         */
        public VOCTCommunicationMessageRapidResponse(long id, VoctMsgStatus status) {
            this.id = id;
            this.status = requireNonNull(status);
        }

        /**
         * @return the sarData
         */
        public RapidResponseDTO getSarData() {
            return sarData;
        }

        /**
         * @param sarData
         *            the sarData to set
         */
        public void setSarData(RapidResponseDTO sarData) {
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
        public VoctMsgStatus getStatus() {
            return status;
        }

        /**
         * @param status
         *            the status to set
         */
        public void setStatus(VoctMsgStatus status) {
            this.status = status;
        }

        /**
         * @return the receiversMMSI
         */
        public long getReceiversMMSI() {
            return receiversMMSI;
        }

        /**
         * @param receiversMMSI
         *            the receiversMMSI to set
         */
        public void setReceiversMMSI(long receiversMMSI) {
            this.receiversMMSI = receiversMMSI;
        }

    }

    public static class VOCTCommunicationReplyRapidResponse extends ServiceMessage<Void> {

        // private String message;
        private long id;
        private long mmsi;
        private long sendDate;

        public VOCTCommunicationReplyRapidResponse() {
        }

        /**
         * @param message
         */
        public VOCTCommunicationReplyRapidResponse(long id, long mmsi, long sendDate) {
            // this.message = message;
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
}

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
package dk.dma.epd.common.prototype.enavcloud;

import dk.dma.enav.model.fal.FALReport;

import java.io.Serializable;
import java.util.Date;

import static java.util.Objects.requireNonNull;

public class FALReportingService {

    /** An initiation point */
    public static final TODO.ServiceInitiationPoint<FALReportMessage> INIT = new TODO.ServiceInitiationPoint<>(FALReportMessage.class);

    public static class FALReportMessage extends TODO.ServiceMessage<FALReportReply> implements Serializable {

        private static final long serialVersionUID = -6477410194395261604L;

        private FALReport falReport;
        private Date sentDate;
        private String sender;
        private String message;
        private long id;

        public FALReportMessage() {
        }

        /**
         * @param falReport
         * @param sent
         * @param sender
         * @param message
         * @param id
         */
        public FALReportMessage(FALReport falReport, Date sent, String sender, String message, long id) {
            super();
            this.falReport = falReport;
            this.sentDate = sent;
            this.sender = sender;
            this.message = message;
            this.id = id;
        }

        /**
         * @return the falReport
         */
        public FALReport getFalReport() {
            return falReport;
        }

        /**
         * @param falReport
         *            the falReport to set
         */
        public void setFalReport(FALReport falReport) {
            this.falReport = falReport;
        }

        /**
         * @return the sentDate
         */
        public Date getSentDate() {
            return sentDate;
        }

        /**
         * @param sentDate
         *            the sentDate to set
         */
        public void setSentDate(Date sentDate) {
            this.sentDate = sentDate;
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

    }

    public static class FALReportReply extends TODO.ServiceMessage<Void> {

        private long id;
        private long mmsi;
        private long sendDate;

        public FALReportReply() {
        }

       public FALReportReply(long id, long mmsi, long sendDate) {
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

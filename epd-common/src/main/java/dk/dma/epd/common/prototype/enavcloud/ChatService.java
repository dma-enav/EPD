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

import java.util.Date;

import net.maritimecloud.net.service.spi.ServiceInitiationPoint;
import net.maritimecloud.net.service.spi.ServiceMessage;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;

/**
 * A maritime cloud service used for sending messages between maritime entities such as ship and shore.
 */
public class ChatService {

    /** An initiation point */
    public static final ServiceInitiationPoint<ChatServiceMessage> INIT = new ServiceInitiationPoint<>(ChatServiceMessage.class);

    /**
     * The chat service message
     */
    public static class ChatServiceMessage extends ServiceMessage<Void> {

        private String message;
        private Date sendDate;
        private NotificationSeverity severity;
        private boolean ownMessage;

        public ChatServiceMessage() {
        }

        /**
         * @param message
         */
        public ChatServiceMessage(String message, boolean ownMessage) {
            this.message = message;
            this.ownMessage = ownMessage;
            this.sendDate = new Date();
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isOwnMessage() {
            return ownMessage;
        }

        public void setOwnMessage(boolean ownMessage) {
            this.ownMessage = ownMessage;
        }

        public Date getSendDate() {
            return sendDate;
        }

        public void setSendDate(Date sendDate) {
            this.sendDate = sendDate;
        }

        public NotificationSeverity getSeverity() {
            return severity;
        }

        public void setSeverity(NotificationSeverity severity) {
            this.severity = severity;
        }        
    }
}

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

import java.util.List;

import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import net.maritimecloud.net.service.spi.ServiceInitiationPoint;
import net.maritimecloud.net.service.spi.ServiceMessage;

/**
 * A maritime cloud service used for sending messages
 * between maritime entities such as ship and shore.
 */
public class ChatService {

    /** An initiation point */
    public static final ServiceInitiationPoint<ChatServiceMessage> INIT = new ServiceInitiationPoint<>(
            ChatServiceMessage.class);

    /**
     * The chat service message
     */
    public static class ChatServiceMessage extends ServiceMessage<Void> {

        private String message;
        private long id;
        private String senderName;
        private long sendDate;
        private NotificationSeverity severity;
        private List<NotificationAlert> alerts;
        
        public ChatServiceMessage() {
        }

        /**
         * @param message
         */
        public ChatServiceMessage(String message, long id, long sendDate, String senderName) {
            this.message = message;
            this.id = id;
            this.sendDate = sendDate;
            this.senderName = senderName;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public long getSendDate() {
            return sendDate;
        }

        public void setSendDate(long sendDate) {
            this.sendDate = sendDate;
        }
        
        public NotificationSeverity getSeverity() {
            return severity;
        }

        public void setSeverity(NotificationSeverity severity) {
            this.severity = severity;
        }

        public List<NotificationAlert> getAlerts() {
            return alerts;
        }

        public void setAlerts(List<NotificationAlert> alerts) {
            this.alerts = alerts;
        }
    }
}

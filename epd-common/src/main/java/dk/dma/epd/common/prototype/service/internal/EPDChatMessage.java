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
package dk.dma.epd.common.prototype.service.internal;

import net.maritimecloud.util.Timestamp;
import dma.messaging.MaritimeTextingNotificationSeverity;
import dma.messaging.MaritimeText;

public class EPDChatMessage {

    MaritimeText chatMessage;

    boolean ownMessage;
    Timestamp sendDate;

    public EPDChatMessage(MaritimeText chatMessage, boolean ownMessage, Timestamp sendDate) {

        this.chatMessage = chatMessage;
        this.sendDate = sendDate;
        this.ownMessage = ownMessage;
    }

    /**
     * @return the chatMessage
     */
    public MaritimeText getChatMessage() {
        return chatMessage;
    }

    /**
     * @return the ownMessage
     */
    public boolean isOwnMessage() {
        return ownMessage;
    }

    /**
     * @return the sendDate
     */
    public Timestamp getSendDate() {
        return sendDate;
    }

    public String getMsg() {
        return chatMessage.getMsg();
    }

    public MaritimeTextingNotificationSeverity getSeverity() {
        return chatMessage.getSeverity();
    }

    public void setSeverity(MaritimeTextingNotificationSeverity alert) {
        chatMessage.setSeverity(alert);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EPDChatMessage [chatMessage=" + chatMessage + ", ownMessage=" + ownMessage + ", sendDate=" + sendDate
                + ", getChatMessage()=" + getChatMessage() + ", isOwnMessage()=" + isOwnMessage() + ", getSendDate()="
                + getSendDate() + ", getMsg()=" + getMsg() + ", getSeverity()=" + getSeverity() + "]";
    }

}

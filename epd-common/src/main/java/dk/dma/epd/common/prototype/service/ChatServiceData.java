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
package dk.dma.epd.common.prototype.service;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.maritimecloud.core.id.MaritimeId;
import dk.dma.epd.common.prototype.service.internal.EPDChatMessage;

/**
 * Wraps a list of chat messages for a given maritime id
 */
public class ChatServiceData implements Serializable {

    private static final long serialVersionUID = 1L;

    private MaritimeId id;
    private List<EPDChatMessage> messages = new CopyOnWriteArrayList<>();
    private boolean read = true; // Initially empty list of messages

    /**
     * Constructor
     * 
     * @param id
     *            the maritime id
     */
    public ChatServiceData(MaritimeId id) {
        this.id = id;
    }

    /**
     * Returns the maritime id of the target
     * 
     * @return the maritime id of the target
     */
    public MaritimeId getId() {
        return id;
    }

    /**
     * Returns the list of chat service messages
     * 
     * @return the list of chat service messages
     */
    public List<EPDChatMessage> getMessages() {
        return messages;
    }

    /**
     * Returns the latest chat message
     * 
     * @return the latest chat message
     */
    public EPDChatMessage getLatestMessage() {
        return (messages == null) ? null : messages.get(messages.size() - 1);
    }

    /**
     * Returns the number of messages
     * 
     * @return the number of messages
     */
    public int getMessageCount() {
        return messages.size();
    }

    /**
     * Adds a chat service message to the list and mark the chat communication as unread if this is not an own-message.
     * 
     * @param message
     *            the message to add
     */
    public void addChatMessage(EPDChatMessage message) {
        messages.add(message);
        if (!message.isOwnMessage()) {
            setRead(false);
        }
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

}

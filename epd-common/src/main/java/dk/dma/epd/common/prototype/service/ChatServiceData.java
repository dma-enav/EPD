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
package dk.dma.epd.common.prototype.service;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.maritimecloud.core.id.MaritimeId;
import dk.dma.epd.common.prototype.enavcloud.ChatService.ChatServiceMessage;

/**
 * Wraps a list of chat messages for a given maritime id
 */
public class ChatServiceData implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private MaritimeId id;
    private List<ChatServiceMessage> messages = new CopyOnWriteArrayList<>();
    private boolean read = true; // Initially empty list of messages

    /**
     * Constructor 
     * 
     * @param id the maritime id 
     */
    public ChatServiceData(MaritimeId id) {
        this.id = id;
    }
    
    /**
     * Returns the maritime id of the target
     * @return the maritime id of the target
     */
    public MaritimeId getId() {
        return id;
    }

    /**
     * Returns the list of chat service messages 
     * @return the list of chat service messages 
     */
    public List<ChatServiceMessage> getMessages() {
        return messages;
    }

    /**
     * Returns the latest chat message
     * @return the latest chat message
     */
    public ChatServiceMessage getLatestMessage() {
        return (messages == null) ? null : messages.get(messages.size() - 1);
    }

    /**
     * Returns the number of messages
     * @return the number of messages
     */
    public int getMessageCount() {
        return messages.size();
    }
    
    /**
     * Adds a chat service message to the list
     * and mark the chat communication as unread if this is not an own-message.
     * @param message the message to add
     */
    public void addChatMessage(ChatServiceMessage message) {
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

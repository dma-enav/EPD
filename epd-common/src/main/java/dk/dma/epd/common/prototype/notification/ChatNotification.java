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
package dk.dma.epd.common.prototype.notification;

import java.util.ArrayList;
import java.util.Date;

import net.maritimecloud.core.id.MaritimeId;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.enavcloud.ChatService.ChatServiceMessage;
import dk.dma.epd.common.text.Formatter;


/**
 * Class that can be used for chat notifications
 */
public class ChatNotification extends GeneralNotification {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new chat notification for sent or received chat messages
     * 
     * @param received whether the message is sent or received
     * @param id the id of the sender/receiver
     * @param message the chat message
     */
    public ChatNotification(boolean received, MaritimeId id, ChatServiceMessage message) {
        super();

        setId(toId(id));
        setDate(new Date(message.getSendDate()));
        setSeverity(message.getSeverity());

        if (received) {
            // Chat message received
            String senderName = EPD.getInstance().getName(id, message.getSenderName());
            
            setTitle("Comms log " + senderName);
            setDescription(Formatter.formatShortDateTime(new Date(message.getSendDate())) + " : " + senderName + " : "
                    + message.getMessage());
            setAlerts(message.getAlerts());
        
        } else {
            // Chat message sent
            String recipientName = EPD.getInstance().getName(id);
            
            setTitle(recipientName);
            setDescription(Formatter.formatShortDateTime(new Date(message.getSendDate())) + " - You : "
                    + message.getMessage());
            setAlerts(new ArrayList<NotificationAlert>());
            setRead(true);
            setAcknowledged(true);            
        }
    }
    
    /**
     * Maps the given maritime id to a unique chat notification id
     * @param id the maritime id 
     * @return a unique chat notification id
     */
    public static String toId(MaritimeId id) {
        return "chat_" + id;
    }
    
    /**
     * Called if a chat message is received or sent to an existing target.
     * Merges the new message with this chat notification.
     * 
     * @param received whether the message is sent or received
     * @param id the id of the sender/receiver
     * @param message the chat message
     * @return this
     */
    public ChatNotification merge(boolean received, MaritimeId id, ChatServiceMessage message) {
        if (received) {
            // Chat message received
            String senderName = EPD.getInstance().getName(id, message.getSenderName());
            
            setDescription(Formatter.formatShortDateTime(new Date(message.getSendDate())) + " : " + senderName + " : "
                    + message.getMessage() + "\n" + getDescription());
            setAcknowledged(false);
            setSeverity(message.getSeverity());
            setAlerts(message.getAlerts());
            
        } else {
            // Chat message sent
            setDescription(Formatter.formatShortDateTime(new Date(message.getSendDate())) + 
                    " - You : " + message.getMessage() + "\n" + getDescription());
            setAcknowledged(true);
        }
        
        return this;
    }
    
    /**
     * Returns a HTML description of this notification
     * @return a HTML description of this notification
     */
    @Override
    public String toHtml() {
        StringBuilder html = new StringBuilder("<html>");
        html.append("<table>");
        html.append(String.format("<tr><th>Communications Log from:</th><td>%s</td></tr>", Formatter.formatHtml(title)));
        html.append(String.format("<tr><th valign='top'>Messages:</th><td>%s</td></tr>", Formatter.formatHtml(description)));
        html.append("</table>");
        return html.append("</html>").toString();
    }}

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

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.enavcloud.ChatService.ChatServiceMessage;
import dk.dma.epd.common.prototype.notification.NotificationAlert.AlertType;
import dk.dma.epd.common.prototype.service.ChatServiceData;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.util.NameUtils;
import dk.dma.epd.common.util.NameUtils.NameFormat;
import net.maritimecloud.core.id.MaritimeId;

/**
 * Class that can be used for chat message notifications
 */
public class ChatNotification extends Notification<ChatServiceData, MaritimeId> {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor 
     * 
     * @param id the maritime id 
     */
    public ChatNotification(ChatServiceData chatData) {
        super(chatData, chatData.getId(), NotificationType.MESSAGES);        
        
        targetId = chatData.getId();
        
        if (chatData.getMessageCount() > 0) {
            ChatServiceMessage msg = chatData.getLatestMessage();
            title = String.format(
                    msg.isOwnMessage() ? "Message to %s" : "Message from %s", 
                    NameUtils.getName(chatData.getId()));
            description = msg.getMessage();
            date = msg.getSendDate();
            severity = msg.isOwnMessage() ? NotificationSeverity.MESSAGE : msg.getSeverity();
            if (!msg.isOwnMessage() && !chatData.isRead()) {
                addAlerts(new NotificationAlert(AlertType.POPUP, AlertType.BEEP));
                read = acknowledged = false;
            } else {
                read = acknowledged = true;
            }
        } else {
            title = description = "";
            read = acknowledged = true;
        }
        
        // Try to determine the position
        AisHandlerCommon aisHandler = EPD.getInstance().getAisHandler();
        Integer mmsi = MaritimeCloudUtils.toMmsi(chatData.getId());
        if (mmsi != null && 
            aisHandler.getVesselTarget(mmsi.longValue()) != null &&
            aisHandler.getVesselTarget(mmsi.longValue()).getPositionData() != null) {
            location = aisHandler.getVesselTarget(mmsi.longValue()).getPositionData().getPos();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRead(boolean read) {
        this.read = this.acknowledged = read;
        get().setRead(read);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setAcknowledged(boolean acknowledged) {
        this.read = this.acknowledged = acknowledged;
        get().setRead(acknowledged);
    }
    
    /**
     * Returns the name of the target
     * @return the name of the target
     */
    public String getTargetName() {
        return NameUtils.getName(getId(), NameFormat.MEDIUM);
    }
    
    /**
     * Returns the type of the target
     * @return the type of the target
     */
    public String getTargetType() {
        return NameUtils.getType(getId());
    }
}

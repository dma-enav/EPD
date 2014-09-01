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
        fireNotificationUpdated();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setAcknowledged(boolean acknowledged) {
        this.read = this.acknowledged = acknowledged;
        get().setRead(acknowledged);
        fireNotificationUpdated();
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

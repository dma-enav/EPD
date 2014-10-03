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

import dk.dma.epd.common.prototype.msi.MsiMessageExtended;

/**
 * An MSI specific notification class
 */
public class MsiNotification extends Notification<MsiMessageExtended, Integer>{

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param message the MSI message
     */
    public MsiNotification(MsiMessageExtended message) {
        super(message, message.getMsiMessage().getMessageId(), NotificationType.MSI_NM);
        
        // Update the notification data from the MSI message
        title = message.getMsiMessage().getMessage();
        severity = NotificationSeverity.MESSAGE;
        read = acknowledged = message.acknowledged;
        if (message.getMsiMessage().getUpdated() == null) {
            date = message.getMsiMessage().getCreated();
        } else {
            date = message.getMsiMessage().getUpdated();
        }
        if (message.getMsiMessage().getLocation() != null && 
                message.getMsiMessage().getLocation().getCenter() != null) {
            location = message.getMsiMessage().getLocation().getCenter();
        }
    } 
    
    /**
     * Sets the acknowledged flag and updates the underlying MSI message
     * @param acknowledged the new acknowledged state
     */
    @Override 
    public void setAcknowledged(boolean acknowledged) {
        super.setAcknowledged(acknowledged);
        get().acknowledged = acknowledged;
    }
}

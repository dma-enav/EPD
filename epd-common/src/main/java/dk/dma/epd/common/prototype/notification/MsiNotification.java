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
        super(message, message.getMsiMessage().getMessageId(), NotificationType.MSI);
        
        // Update the notification data from the MSI message
        title = message.getMsiMessage().getMessage();
        severity = NotificationSeverity.WARNING;
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

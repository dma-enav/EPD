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

import dk.dma.enav.model.geometry.Position;

/**
 * This interface returns information about a notification.
 * <p>
 * The notification will be as associated with some entity
 * of type {@code T} which can be accessed using the
 * {@linkplain #getValue()} method.
 */
public interface Notification<I,T> {

    /**
     * Defines the notification severity
     */
    enum NotificationSeverity {
        MESSAGE, WARNING, ALERT;
    }

    /**
     * Defines the type of alert that the service can cause
     */
    enum NotificationAlert {
        POPUP, SYSTEM_TRAY, BEEP;
    }

    /**
     * Defines the type of actions associated with a notification.
     */
    enum NotificationAction {
        READ, GOTO, DELETE, ACKNOWLEDGE
    }
    
    /**
     * Returns the notification type of this notification
     * @return the notification type
     */
    NotificationType getNotificationType();
    
    /**
     * Returns the notification severity
     * @return the notification severity
     */
    NotificationSeverity getNotificaitonSeverity();
    
    /**
     * Returns the list of notification alerts associated with this notification
     * @return the list of notification alerts associated with this notification
     */
    NotificationAlert[] getNotificationAlerts();
    
    /**
     * Returns the actions associates with this notification
     * @return the actions associates with this notification
     */
    NotificationAction[] getNotificaitonActions();
    
    /**
     * Returns the entity associated with the notification
     * @return the entity associated with the notification
     */
    T getValue();

    /**
     * Returns a unique id for the notification
     * @return a unique id for the notification
     */
    I getId();
    
    /**
     * Returns a title for the notification
     * @return a title for the notification
     */
    String getTitle();
    
    /**
     * Returns a description for the notification
     * @return a description for the notification
     */
    String getDescription();
    
    /**
     * Returns a location associated with the notification
     * @return a location associated with the notification
     */
    Position getLocation();
    
    /**
     * Returns whether the notification has been read or not
     * @return whether the notification has been read or not
     */
    boolean isRead();
    
    /**
     * Returns whether the notification has been acknowledged or not
     * @return whether the notification has been acknowledged or not
     */
    boolean isAcknowledged();
    
}

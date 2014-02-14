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

import java.io.Serializable;

import dk.dma.enav.model.geometry.Position;

/**
 * A default implementation of the {@linkplain Notification}
 * interface.
 * <p>
 * Can either be extended or used as an adapter in notifications.
 */
public class DefaultNotification<I, T> implements Notification<I, T>, Serializable {

    private static final long serialVersionUID = 1L;

    protected NotificationType notificationType = NotificationType.NOTIFICATION;
    protected NotificationSeverity notificationSeverity = NotificationSeverity.MESSAGE;
    protected NotificationAlert[] notificationAlerts = {};
    protected NotificationAction[] notificationActions = {};
    protected T value;
    protected I id;
    protected String title;
    protected String description;
    protected Position location;
    protected boolean read;
    protected boolean acknowledged;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationType getNotificationType() {
        return notificationType;
    }

    /**
     * Sets the notification type
     * @param notificationType the notification type
     */
    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationSeverity getNotificaitonSeverity() {
        return notificationSeverity;
    }
    
    /**
     * Sets the notification severity
     * @param notificationSeverity the notification severity
     */
    public void setNotificationSeverity(NotificationSeverity notificationSeverity) {
        this.notificationSeverity = notificationSeverity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationAlert[] getNotificationAlerts() {
        return notificationAlerts;
    }
    
    /**
     * Sets the notification alerts
     * @param notificationAlerts the notification alerts
     */
    public void setNotificationAlerts(NotificationAlert[] notificationAlerts) {
        this.notificationAlerts = notificationAlerts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationAction[] getNotificaitonActions() {
        return notificationActions;
    }

    /**
     * Sets the notification actions
     * @param notificationActions the notification actions
     */
    public void setNotificationActions(NotificationAction[] notificationActions) {
        this.notificationActions = notificationActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getValue() {
        return value;
    }

    /**
     * Returns a unique id for the notification
     * @return a unique id for the notification
     */
    public I getId() {
        return id;
    }
    
    /**
     * Sets a unique id for the notification
     * @param id a unique id for the notification
     */
    public void setId(I id) {
        this.id = id;
    }
    
    /**
     * Sets the entity associated with the notification
     * @param value the entity associated with the notification
     */    
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return title;
    }
    
    /**
     * Returns a title for the notification
     * @return a title for the notification
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }
    
    /**
     * Returns a description for the notification
     * @return a description for the notification
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Position getLocation() {
        return location;
    }
    
    /**
     * Returns a location associated with the notification
     * @return a location associated with the notification
     */
    public void setLocation(Position location) {
        this.location = location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRead() {
        return read;
    }
    
    /**
     * Returns whether the notification has been read or not
     * @return whether the notification has been read or not
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAcknowledged() {
        return acknowledged;
    }

    /**
     * Returns whether the notification has been acknowledged or not
     * @return whether the notification has been acknowledged or not
     */
    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

}

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.maritimecloud.core.id.MaritimeId;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.text.Formatter;

/**
 * Implementation of a notification.
 */
public class Notification<T, I> implements Serializable {

    /**
     * Defines the notification severity
     */
    public enum NotificationSeverity {
        MESSAGE, WARNING, ALERT;
    }

    private static final long serialVersionUID = 1L;
    
    protected NotificationType type = NotificationType.NOTIFICATION;
    protected NotificationSeverity severity = NotificationSeverity.MESSAGE;
    protected List<NotificationAlert> alerts = new ArrayList<>();
    protected T value;
    protected I id;
    protected String title;
    protected String description;
    protected Position location;
    protected boolean read;
    protected boolean acknowledged;
    protected boolean autoAcknowledge;
    protected Date date = new Date();
    protected MaritimeId targetId;
    
    /**
     * Constructor
     * 
     * @param value
     * @param id
     * @param type
     */
    public Notification(T value, I id, NotificationType type) {
        this.value = value;
        this.id = id;
        this.type = type;
    }
    
    /**
     * Returns the type of this notification
     * @return the type
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * Sets the notification type
     * @param type the notification type
     */
    public void setType(NotificationType type) {
        this.type = type;
    }

    /**
     * Returns the notification severity
     * @return the notification severity
     */
    public NotificationSeverity getSeverity() {
        return severity;
    }
    
    /**
     * Sets the notification severity
     * @param severity the notification severity
     */
    public void setSeverity(NotificationSeverity severity) {
        this.severity = severity;
    }

    /**
     * Returns the list of notification alerts associated with this notification
     * @return the list of notification alerts associated with this notification
     */
    public List<NotificationAlert> getAlerts() {
        return alerts;
    }
    
    /**
     * Sets the notification alerts
     * @param alerts the notification alerts
     */
    public void setAlerts(List<NotificationAlert> alerts) {
        this.alerts = alerts;
    }
    
    /**
     * Adds notification alerts
     * @param alerts the notification alerts to add
     */
    public void addAlerts(NotificationAlert... alerts) {
        for (NotificationAlert alert : alerts) {
            this.alerts.add(alert);
        }
    }

    /**
     * Returns the entity associated with the notification
     * @return the entity associated with the notification
     */
    public T get() {
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
     * Returns a title for the notification
     * @return a title for the notification
     */
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
     * Returns a description for the notification
     * @return a description for the notification
     */
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
     * Returns a location associated with the notification
     * @return a location associated with the notification
     */
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
     * Returns whether the notification has been read or not
     * @return whether the notification has been read or not
     */
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
     * Returns whether the notification has been acknowledged or not
     * @return whether the notification has been acknowledged or not
     */
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
    
    /**
     * Returns whether the notification can be acknowledged or not
     * Sub-classes can override to e.g. avoid a "Dismiss" button in the pop-up notifications.
     * @return whether the notification can be acknowledged or not
     */
    public boolean canAcknowledge() {
        return true;
    }

    /**
     * Returns if the notification should be automatically acknowledged
     * when it is read.
     * @return if the notification should be automatically acknowledged
     */
    public boolean isAutoAcknowledge() {
        return autoAcknowledge;
    }

    /**
     * Sets if the notification should be automatically acknowledged
     * when it is read.
     * @param autoAcknowledge if the notification should be automatically acknowledged
     */
    public void setAutoAcknowledge(boolean autoAcknowledge) {
        this.autoAcknowledge = autoAcknowledge;
    }

    /**
     * Returns the date of the notification
     * @return the date of the notification
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date of the notification
     * @param date the date of the notification
     */
    public void setDate(Date date) {
        this.date = date;
    }
    
    /**
     * Returns the target maritime id of the notification
     * @return the target maritime id of the notification
     */
    public MaritimeId getTargetId() {
        return targetId;
    }

    /**
     * Sets the target maritime id of the notification
     * @param targetId the target maritime id of the notification
     */
    public void setTargetId(MaritimeId targetId) {
        this.targetId = targetId;
    }

    
    /**
     * Returns a HTML description of this notification
     * @return a HTML description of this notification
     */
    public String toHtml() {
        StringBuilder html = new StringBuilder("<html>");
        html.append("<table>");
        html.append(String.format("<tr><th>Title:</th><td>%s</td></tr>", Formatter.formatHtml(title)));
        html.append(String.format("<tr><th>Date:</th><td>%s</td></tr>", Formatter.formatShortDateTime(date)));
        html.append(String.format("<tr><th valign='top'>Description:</th><td>%s</td></tr>", Formatter.formatHtml(description)));
        html.append("</table>");
        return html.append("</html>").toString();
    }
}

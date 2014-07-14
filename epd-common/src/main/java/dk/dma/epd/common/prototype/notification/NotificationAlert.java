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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Defines the alerts triggered by notifications
 * <p>
 * Example usage:
 * <pre>
 * NotificationAlert alert 
 *        = new NotificationAlert(AlertType.POPUP, AlertType.SYSTEM_TRAY)
 *             .setInitial(true)
 *             .setRepeatAt(new SimpleDateFormat("HH:mm").parse("23:55"))
 *             .setRepeatEvery(30)
 *             .setRepeatUntilAcknowledged(true);
 * </pre>
 */
public class NotificationAlert implements Serializable {

    /**
     * Defines the type of alert that the service can cause
     */
    public enum AlertType {
        POPUP, SYSTEM_TRAY, BEEP, OPEN;
    }
    
    private static final long serialVersionUID = 1L;

    private Set<AlertType> alertTypes = new HashSet<>();
    private boolean initial = true;
    private List<Date> repeatAtTimes = new ArrayList<>();
    private List<Long> repeatEveryTimes = new ArrayList<>();
    private boolean repeatUntilAcknowledged = true;
    private boolean repeatUntilRead;
    private long lastTriggered = -1;

    /**
     * Constructor
     */
    public NotificationAlert() {        
    }
    
    /**
     * Constructor
     * 
     * @param alertTypes
     */
    public NotificationAlert(AlertType... alertTypes) {
        Collections.addAll(this.alertTypes, alertTypes);
    }
    
    
    /**
     * Return if the alert should be triggered.
     * <p>
     * After triggering the alert, the {@linkplain #flagTriggered()}
     * function should be called.
     * 
     * @param notification the associated notification
     * @return if the alert should be triggered
     */
    public boolean shouldTrigger(Notification<?,?> notification) {
        // Handle the initial alert
        if (lastTriggered == -1 && initial) {
            return true;
        }
        
        // Check if the alert repeats
        if (!repeats()) {
            return false;
        }
        
        // Check if the stop conditions have been met
        if ((repeatUntilAcknowledged && notification.isAcknowledged()) ||
             repeatUntilRead && notification.isRead()) {
            return false;
        }

        Date now = new Date();
        
        // Check if a repeat-at date is found.
        // Note that the list of date is sorted.
        if (repeatAtTimes.size() > 0 && now.after(repeatAtTimes.get(0))) {
            while (now.after(repeatAtTimes.get(0))) {
                repeatAtTimes.remove(0);
            }
            return true;
        }
        
        // Check if a repeat-every interval is due
        for (Long interval : repeatEveryTimes) {
            if (now.getTime() > lastTriggered + interval.longValue()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Must be called whenever the alert has been triggered
     */
    public void flagTriggered() {
        lastTriggered = System.currentTimeMillis();
    }
    
    /**
     * Returns the alert types
     * @return the alert types
     */
    public Set<AlertType> getAlertTypes() {
        return alertTypes;
    }
    
    /**
     * Returns if the given alert type is included
     * @param alertType the alert type to check
     * @return if the given alert type is included
     */
    public boolean hasAlertType(AlertType alertType) {
        return alertTypes.contains(alertType);
    }
    
    /**
     * Sets the alert types
     * @param alertTypes the alert types
     * @return {@code this}
     */
    public NotificationAlert setAlertTypes(Set<AlertType> alertTypes) {
        this.alertTypes = alertTypes;
        return this;
    }
    
    /**
     * Returns if this is an initial alert, i.e. if it triggers when
     * the notification is first received.
     * @return if this is an initial alert
     */
    public boolean isInitial() {
        return initial;
    }

    /**
     * Sets if this is an initial alert, i.e. if it triggers when
     * the notification is first received.
     * @param initial if this is an initial alert
     * @return {@code this}
     */
    public NotificationAlert setInitial(boolean initial) {
        this.initial = initial;
        return this;
    }

    /**
     * Returns if this alert repeats
     * @return if this alert repeats
     */
    public boolean repeats() {
        return repeatAtTimes.size() > 0 || repeatEveryTimes.size() > 0;
    }

    /**
     * Flags that the alert should trigger at the given time
     * @param date the time to trigger the alert
     * @return {@code this}
     */
    public NotificationAlert setRepeatAt(Date date) {
        repeatAtTimes.add(date);
        Collections.sort(repeatAtTimes);
        return this;
    }

    /**
     * Flags that the alert should trigger periodically at the given interval
     * @param minutes the interval in minutes
     * @return {@code this}
     */
    public NotificationAlert setRepeatEvery(long minutes) {
        // Convert to milliseconds
        repeatEveryTimes.add(minutes * 60 * 1000);
        return this;
    }

    /**
     * Returns if the alert should stop repeating when the notification is acknowledged.
     * The default value is {@code true}.
     * @return if the alert should stop repeating when the notification is acknowledged
     */
    public boolean getRepeatUntilAcknowledged() {
        return repeatUntilAcknowledged;
    }

    /**
     * Sets if the alert should stop repeating when the notification is acknowledged
     * @param repeatUntilAcknowledged if the alert should stop repeating when the notification is acknowledged
     * @return {@code this}
     */
    public NotificationAlert setRepeatUntilAcknowledged(boolean repeatUntilAcknowledged) {
        this.repeatUntilAcknowledged = repeatUntilAcknowledged;
        return this;
    }

    /**
     * Returns if the alert should stop repeating when the notification is read.
     * The default value is {@code false}.
     * @return if the alert should stop repeating when the notification is read
     */
    public boolean getRepeatUntilRead() {
        return repeatUntilRead;
    }

    /**
     * Sets if the alert should stop repeating when the notification is read
     * @param repeatUntilAcknowledged if the alert should stop repeating when the notification is read
     * @return {@code this}
     */
    public NotificationAlert setRepeatUntilRead(boolean repeatUntilRead) {
        this.repeatUntilRead = repeatUntilRead;
        return this;
    }

    /**
     * Returns a string representation of the alert
     * @return a string representation of the alert
     */
    @Override
    public String toString() {
        return String.format("Alert(types=%s, repeats=%b)", alertTypes, repeats());
    }
    
    /** 
     * Do not use the getters and setters below. Sadly Jackson
     * will not serialize without public getters and setters.
     */
    
    public List<Date> getRepeatAtTimes() {
        return repeatAtTimes;
    }

    public void setRepeatAtTimes(List<Date> repeatAtTimes) {
        this.repeatAtTimes = repeatAtTimes;
    }

    public List<Long> getRepeatEveryTimes() {
        return repeatEveryTimes;
    }

    public void setRepeatEveryTimes(List<Long> repeatEveryTimes) {
        this.repeatEveryTimes = repeatEveryTimes;
    }

}

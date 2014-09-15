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

import org.joda.time.DateTime;

/**
 * Class that can be used for general notifications
 */
public class GeneralNotification extends Notification<Object, Object> {

    private static final long serialVersionUID = 1L;
    private DateTime optionalDateTimeOfAlertRelevance;

    /**
     * Designated constructor
     * 
     * @param value
     * @param id
     * @param type
     */
    public GeneralNotification(Object value, Object id, NotificationType type) {
        super(value, id, type);
    }

    /**
     * Designated constructor
     * 
     * @param value
     * @param id
     * @param type
     */
    public GeneralNotification(Object value, Object id,
            DateTime notificationRelevantTime) {
        super(value, id, NotificationType.NOTIFICATION);
        optionalDateTimeOfAlertRelevance = notificationRelevantTime;
    }

    /**
     * Constructor
     * 
     * @param value
     * @param id
     */
    public GeneralNotification(Object value, Object id) {
        this(value, id, NotificationType.NOTIFICATION);
    }

    /**
     * Constructor
     */
    public GeneralNotification() {
        this(null, System.currentTimeMillis(), NotificationType.NOTIFICATION);
    }

    /**
     * @return the optionalDateTimeOfAlertRelevance
     */
    public DateTime getOptionalDateTimeOfAlertRelevance() {
        return optionalDateTimeOfAlertRelevance;
    }

}

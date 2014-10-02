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

import dk.dma.enav.model.geometry.Position;
import dma.msinm.MCLocation;
import dma.msinm.MCMessage;
import dma.msinm.MCPoint;

import java.util.Date;

/**
 * An MSI specific notification class
 */
public class MsiNmNotification extends Notification<MCMessage, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     *
     * @param message the MSI-NM message
     */
    public MsiNmNotification(MCMessage message) {
        super(message, message.getId(), NotificationType.MSI);

        // Update the notification data from the MSI message
        title = message.getDescs().get(0).getTitle();
        severity = NotificationSeverity.MESSAGE;
        date = new Date(message.getUpdated().getTime());

        if (message.getLocations().size() > 0) {
            double minLat = 90, maxLat = -90, minLon = 180, maxLon = -180;

            for (MCLocation loc : message.getLocations()) {
                for (MCPoint pt : loc.getPoints()) {
                    minLat = Math.min(minLat, pt.getLat());
                    maxLat = Math.max(maxLat, pt.getLat());
                    minLon = Math.min(minLon, pt.getLon());
                    maxLon = Math.max(maxLon, pt.getLon());
                }
            }
            location = Position.create((minLat + maxLat) / 2.0, (minLon + maxLon) / 2.0);
        }
    }


}

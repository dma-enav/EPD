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
package dk.dma.epd.ship.gui.notification;

import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.notification.Notification;
import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.notification.RouteSuggestionNotificationCommon;
import dk.dma.epd.common.prototype.notification.NotificationAlert.AlertType;
import dk.dma.epd.common.util.NameUtils;

/**
 * A ship specific route suggestion implementation of the {@linkplain Notification} class
 */
public class RouteSuggestionNotification extends RouteSuggestionNotificationCommon {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param routeData
     *            the strategic route data
     */
    public RouteSuggestionNotification(RouteSuggestionData routeData) {
        super(routeData);

        String shoreName = NameUtils.getName((int) routeData.getMmsi());

        if (routeData.getReply() == null) {
            // Original route suggestion from shore
            description = String.format("Route suggestion '%s' from %s", routeData.getRoute().getName(), shoreName);
            if (routeData.isAcknowleged()) {
                severity = NotificationSeverity.MESSAGE;
            } else {
                severity = NotificationSeverity.WARNING;
                addAlerts(new NotificationAlert(AlertType.POPUP));
            }
            date = routeData.getSendDate();

        } else {
            // Reply to shore
            description = String.format("Route suggestion '%s' for %s is %s", routeData.getRoute().getName(), shoreName, routeData
                    .getStatus().toString());
            severity = NotificationSeverity.MESSAGE;
            date = routeData.getReplyRecieveDate();

        }
    }

}

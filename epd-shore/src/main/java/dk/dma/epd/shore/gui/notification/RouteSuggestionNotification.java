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
package dk.dma.epd.shore.gui.notification;

import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.notification.Notification;
import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.notification.RouteSuggestionNotificationCommon;
import dk.dma.epd.common.prototype.notification.NotificationAlert.AlertType;
import dk.dma.epd.common.util.NameUtils;

/**
 * A shore specific route suggestion implementation of the {@linkplain Notification} class
 */
public class RouteSuggestionNotification extends RouteSuggestionNotificationCommon {

    private static final long serialVersionUID = 1L;


    /**
     * Constructor
     * 
     * @param routeData the strategic route data
     */
    public RouteSuggestionNotification(RouteSuggestionData routeData) {
        super(routeData);
        
        String shipName = NameUtils.getName((int)routeData.getMmsi());
        
        if (routeData.getReply() == null) {
            // Original route suggestion from shore
            description = String.format(
                    "Route suggestion '%s' for %s", 
                    routeData.getMessage().getRoute().getName(),
                    shipName);
            severity = NotificationSeverity.MESSAGE;
            date = routeData.getMessage().getSentDate();
            
        } else {
            // Reply to shore
            description = String.format(
                    "Route suggestion '%s' from %s is %s", 
                    routeData.getMessage().getRoute().getName(),
                    shipName,
                    routeData.getStatus().toString());
            if (routeData.isAcknowleged()) {
                severity = NotificationSeverity.MESSAGE;
            } else {
                severity = NotificationSeverity.WARNING;
                addAlerts(new NotificationAlert(AlertType.POPUP));
            }
            date = routeData.getReply().getSentDate();            
        }
    }
}

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

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.gui.notification.NotificationPanel;
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.notification.Notification;
import dk.dma.epd.common.prototype.notification.NotificationType;

/**
 * An route suggestion implementation of the {@linkplain NotificationPanel} class
 */
public class RouteSuggestionNotification extends Notification<RouteSuggestionData, Long>{

    private static final long serialVersionUID = 1L;


    /**
     * Constructor
     * 
     * @param routeData the strategic route data
     */
    public RouteSuggestionNotification(RouteSuggestionData routeData) {
        super(routeData, routeData.getId(), NotificationType.TACTICAL_ROUTE);
        
        title = String.format("Route suggestion '%s' is %s", 
                routeData.getMessage().getRoute().getName(),
                routeData.getStatus().toString());
        
        description = String.format("Route suggestion '%s' for %s is %s", 
                routeData.getMessage().getRoute().getName(),
                routeData.getMmsi(),
                routeData.getStatus().toString());
        
        severity = NotificationSeverity.MESSAGE;
        read = acknowledged = routeData.isAcknowleged();
        date = routeData.getMessage().getSentDate();
        location = Position.create(
                    routeData.getMessage().getRoute().getWaypoints().get(0).getLatitude(), 
                    routeData.getMessage().getRoute().getWaypoints().get(0).getLongitude());
    }    

    
    /**
     * Sets the acknowledged flag and updates the underlying route suggestion
     * @param acknowledged the new acknowledged state
     */
    @Override 
    public void setAcknowledged(boolean acknowledged) {
        super.setAcknowledged(acknowledged);
        get().setAcknowleged(acknowledged);
    }
}

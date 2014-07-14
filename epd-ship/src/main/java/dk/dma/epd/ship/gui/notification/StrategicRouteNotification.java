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
package dk.dma.epd.ship.gui.notification;

import dk.dma.epd.common.prototype.model.identity.IdentityHandler;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.notification.StrategicRouteNotificationCommon;
import dk.dma.epd.common.prototype.notification.NotificationAlert.AlertType;
import dk.dma.epd.ship.EPDShip;

/**
 * A ship-specific strategic route implementation of the {@linkplain StrategicRouteNotificationCommon} class
 */
public class StrategicRouteNotification extends StrategicRouteNotificationCommon {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param routeData the strategic route data
     */
    public StrategicRouteNotification(StrategicRouteNegotiationData routeData) {
        super(routeData);
        
        title = description = String.format(
                routeData.getLatestRouteMessage().isFromStcc()
                    ? "Route request received from %s with status %s"
                    : "Route request sent to %s with status %s", 
                getCallerlName(), 
                routeData.getStatus());        
        
        if (acknowledged || !routeData.getLatestRouteMessage().isFromStcc()) {
            severity = NotificationSeverity.MESSAGE;
        } else {
            severity = NotificationSeverity.WARNING;
            addAlerts(new NotificationAlert(AlertType.POPUP));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCallerlName() {
        IdentityHandler identityHandler = EPDShip.getInstance().getIdentityHandler();
        long mmsi = get().getMmsi();
        if (identityHandler.actorExists(mmsi)) {
            return identityHandler.getActor(mmsi).getName() + " (" + mmsi + ")";
        }
        return "STCC (" + mmsi + ")";
    }
}

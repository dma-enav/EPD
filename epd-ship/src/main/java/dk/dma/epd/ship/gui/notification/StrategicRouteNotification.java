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

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
package dk.dma.epd.shore.gui.notification;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.notification.StrategicRouteNotificationCommon;
import dk.dma.epd.common.prototype.notification.NotificationAlert.AlertType;

/**
 * A shore-specific strategic route implementation of the {@linkplain StrategicRouteNotificationCommon} class
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
                "Route request from %s with status %s", 
                getCallerlName(), 
                routeData.getStatus());        
        
        if (acknowledged) {
            severity = NotificationSeverity.MESSAGE;
        } else {
            severity = NotificationSeverity.WARNING;
            addAlerts(new NotificationAlert(AlertType.POPUP));
        }
    }
    
    /**
     * Returns the vessel static data or null if not available
     * @return the vessel static data or null if not available
     */
    public VesselStaticData getVesselStaticData() {
        AisHandlerCommon aisHandler = EPD.getInstance().getAisHandler();        
        if (aisHandler != null && aisHandler.getVesselTarget(get().getMmsi()) != null) {
            return aisHandler.getVesselTarget(get().getMmsi()).getStaticData();
        }
        return null;
    }
    
    /**
     * Returns the vessel position data or null if not available
     * @return the vessel position data or null if not available
     */
    public VesselPositionData getVesselPositionData() {
        AisHandlerCommon aisHandler = EPD.getInstance().getAisHandler();        
        if (aisHandler != null && aisHandler.getVesselTarget(get().getMmsi()) != null) {
            return aisHandler.getVesselTarget(get().getMmsi()).getPositionData();
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCallerlName() {
        VesselStaticData staticData = getVesselStaticData();
        if (staticData != null) {
            return staticData.getTrimmedName();
        }
        return String.valueOf(get().getMmsi());
    }

    /**
     * Returns the call sign of the vessel associated with the route
     * @return the call sign of the vessel associated with the route
     */
    public String getVesselCallsign() {
        VesselStaticData staticData = getVesselStaticData();
        if (staticData != null) {
            return staticData.getTrimmedCallsign();
        }
        return "N/A";
    }
}

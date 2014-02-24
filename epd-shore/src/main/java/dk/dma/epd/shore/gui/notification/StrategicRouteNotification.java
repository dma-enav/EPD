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

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestMessage;
import dk.dma.epd.common.prototype.gui.notification.NotificationPanel;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.notification.Notification;
import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.notification.NotificationAlert.AlertType;
import dk.dma.epd.shore.service.StrategicRouteNegotiationData;

/**
 * An strategic route implementation of the {@linkplain NotificationPanel} class
 */
public class StrategicRouteNotification extends Notification<StrategicRouteNegotiationData, Long>{

    private static final long serialVersionUID = 1L;


    /**
     * Constructor
     * 
     * @param routeData the strategic route data
     */
    public StrategicRouteNotification(StrategicRouteNegotiationData routeData) {
        super(routeData, routeData.getId(), NotificationType.STRATEGIC_ROUTE);

        title = description = String.format(
                    "Route request from %s with status %s", 
                    getVesselName(), 
                    routeData.getStatus());
        acknowledged = read = routeData.isHandled();
        date = routeData.getRouteMessage().get(0).getSent();
        
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
     * Returns the name of the vessel associated with the route
     * @return the name of the vessel associated with the route
     */
    public String getVesselName() {
        VesselStaticData staticData = getVesselStaticData();
        if (staticData != null) {
            return staticData.getName().trim();
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
            return staticData.getCallsign();
        }
        return "N/A";
    }
    
    /**
     * Returns a copy of the original route
     * @return a copy of the original route
     */
    public Route getOriginalRoute() {
        return new Route(get().getRouteMessage().get(0).getRoute());
    }
    
    /**
     * Returns a copy of the latest route
     * @return a copy of the latest route
     */
    public Route getLatestRoute() {
        StrategicRouteRequestMessage lastMessage = 
                get().getRouteMessage().get(get().getRouteMessage().size() - 1);
        
        return new Route(lastMessage.getRoute());
    }
}

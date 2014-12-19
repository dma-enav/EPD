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

import net.maritimecloud.core.id.MmsiId;
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;

/**
 * An base route suggestion implementation of the {@linkplain Notification} class
 */
public class RouteSuggestionNotificationCommon extends Notification<RouteSuggestionData, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param routeData
     *            the strategic route data
     */
    public RouteSuggestionNotificationCommon(RouteSuggestionData routeData) {
        super(routeData, routeData.getId(), NotificationType.TACTICAL_ROUTE);

        title = String.format("Route suggestion '%s' is %s", routeData.getRoute().getName(), routeData.getStatus().toString());

        read = acknowledged = routeData.isAcknowleged();
        location = routeData.getRoute().getWaypoints().get(0).getPos();
        targetId = (routeData.getMmsi() != -1) ? new MmsiId((int) routeData.getMmsi()) : null;
    }

    /**
     * Sets the acknowledged flag and updates the underlying route suggestion
     * 
     * @param acknowledged
     *            the new acknowledged state
     */
    @Override
    public void setAcknowledged(boolean acknowledged) {
        super.setAcknowledged(acknowledged);
        get().setAcknowleged(acknowledged);
    }
}

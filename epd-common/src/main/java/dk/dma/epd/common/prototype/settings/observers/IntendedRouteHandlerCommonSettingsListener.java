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
package dk.dma.epd.common.prototype.settings.observers;

import dk.dma.epd.common.prototype.settings.handlers.IntendedRouteHandlerCommonSettings;

/**
 * Interface for observing an {@link IntendedRouteHandlerCommonSettings} for
 * changes.
 * 
 * @author Janus Varmarken
 * 
 */
public interface IntendedRouteHandlerCommonSettingsListener extends
        HandlerSettingsListener {

    /**
     * Invoked when
     * {@link IntendedRouteHandlerCommonSettings#getRouteTimeToLive()} has
     * changed.
     * 
     * @param routeTtl
     *            The new route time to live. See
     *            {@link IntendedRouteHandlerCommonSettings#getRouteTimeToLive()}
     *            for details, e.g. unit.
     */
    void onRouteTimeToLiveChanged(long routeTtl);

    /**
     * Invoked when
     * {@link IntendedRouteHandlerCommonSettings#getFilterDistance()} has
     * changed.
     * 
     * @param filterDist
     *            The new filter distance. See
     *            {@link IntendedRouteHandlerCommonSettings#getFilterDistance()}
     *            for more details, e.g. unit.
     */
    void onFilterDistanceChanged(double filterDist);

    /**
     * Invoked when
     * {@link IntendedRouteHandlerCommonSettings#getNotificationDistance()} has
     * changed.
     * 
     * @param notificationDist
     *            The new notification distance. See
     *            {@link IntendedRouteHandlerCommonSettings#getNotificationDistance()}
     *            for more details, e.g. unit.
     */
    void onNotificationDistanceChanged(double notificationDist);

    /**
     * Invoked when
     * {@link IntendedRouteHandlerCommonSettings#getAlertDistance()} has
     * changed.
     * 
     * @param alertDist
     *            The new alert distance. See
     *            {@link IntendedRouteHandlerCommonSettings#getAlertDistance()}
     *            for more details, e.g. unit.
     */
    void onAlertDistanceChanged(double alertDist);

}

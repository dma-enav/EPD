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
package dk.dma.epd.ship.settings.observers;

import dk.dma.epd.common.prototype.model.route.PartialRouteFilter;
import dk.dma.epd.common.prototype.settings.observers.IntendedRouteHandlerCommonSettingsListener;
import dk.dma.epd.ship.settings.handlers.IntendedRouteHandlerSettings;

/**
 * Interface for observing an {@link IntendedRouteHandlerSettings} for changes.
 * 
 * @author Janus Varmarken
 * 
 */
public interface IntendedRouteHandlerSettingsListener extends
        IntendedRouteHandlerCommonSettingsListener {
    /**
     * Invoked when
     * {@link IntendedRouteHandlerSettings#isBroadcastIntendedRoute()} has
     * changed.
     * 
     * @param broadcast
     *            Specifies if intended route should be broadcasted.
     */
    void broadcastIntendedRouteChanged(boolean broadcast);

    /**
     * Invoked when
     * {@link IntendedRouteHandlerSettings#getTimeBetweenBroadCast()} has
     * changed.
     * 
     * @param timeBetweenBroadcast
     *            The new value for the time between each broadcast of intended
     *            route. See
     *            {@link IntendedRouteHandlerSettings#getTimeBetweenBroadCast()}
     *            .
     */
    void timeBetweenBroadcastChanged(long timeBetweenBroadcast);

    /**
     * Invoked when {@link IntendedRouteHandlerSettings#getAdaptionTime()} has
     * changed.
     * 
     * @param adaptionTime
     *            How much ETA must change for a route broadcast to be forced.
     */
    void adaptionTimeChanged(int adaptionTime);

    /**
     * Invoked when
     * {@link IntendedRouteHandlerSettings#getIntendedRouteFilter()} has
     * changed.
     * 
     * @param intendedRouteFilter
     *            The new intended route filter.
     */
    void intendedRouteFilterChanged(PartialRouteFilter intendedRouteFilter);

    /**
     * Invoked when {@link IntendedRouteHandlerSettings#getBroadcastRadius()}
     * has changed.
     * 
     * @param broadcastRadius
     *            The new broadcast radius.
     */
    void broadcastRadiusChanged(int broadcastRadius);

}

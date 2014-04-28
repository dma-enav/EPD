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
package dk.dma.epd.ship.settings.handlers;

import dk.dma.epd.common.prototype.model.route.PartialRouteFilter;
import dk.dma.epd.common.prototype.settings.handlers.IntendedRouteHandlerCommonSettings;
import dk.dma.epd.ship.service.IntendedRouteHandler;

/**
 * Maintains settings for an {@link IntendedRouteHandler}.
 */
public class IntendedRouteHandlerSettings<OBSERVER extends IntendedRouteHandlerSettings.IObserver>
        extends IntendedRouteHandlerCommonSettings<OBSERVER> {

    /**
     * Specifies if the ship should broadcast its intended route.
     */
    private boolean broadcastIntendedRoute = true;

    /**
     * Specifies the intended route broadcast time. Unit is milliseconds (TODO
     * verify).
     */
    private long timeBetweenBroadcast = 60;

    /**
     * Specifies the change in ETA for a new route broadcast to be forced.
     */
    private int adaptionTime = 1;

    /**
     * Filter for how the route transmission should occur. TODO: Ask DNC about
     * the purpose of this field.
     */
    private PartialRouteFilter intendedRouteFilter = PartialRouteFilter.DEFAULT;

    /**
     * Gets the setting that specifies if own ship intended route should be
     * broadcast.
     * 
     * @return {@code true} if intended route should be broadcast, {@code false}
     *         otherwise.
     */
    public boolean isBroadcastIntendedRoute() {
        try {
            this.settingLock.readLock().lock();
            return this.broadcastIntendedRoute;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies if own ship intended route should be
     * broadcast.
     * 
     * @param broadcastIntendedRoute
     *            {@code true} activate intended route broadcasting,
     *            {@code false} to deactivate intended route broadcasting.
     */
    public void setBroadcastIntendedRoute(final boolean broadcastIntendedRoute) {
        try {
            this.settingLock.writeLock().lock();
            if (this.broadcastIntendedRoute == broadcastIntendedRoute) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.broadcastIntendedRoute = broadcastIntendedRoute;
            for (OBSERVER obs : this.observers) {
                obs.broadcastIntendedRouteChanged(broadcastIntendedRoute);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the setting that specifies the time between each broadcast of the
     * ship's intended route.
     * 
     * @return timeBetweenBroadCast The time between each broadcast in
     *         milliseconds.
     */
    public long getTimeBetweenBroadCast() {
        try {
            this.settingLock.readLock().lock();
            return timeBetweenBroadcast;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the time between each broadcast of the
     * ship's intended route.
     * 
     * @param timeBetweenBroadcast
     *            The new time between each broadcast in milliseconds.
     */
    public void setTimeBetweenBroadCast(final long timeBetweenBroadcast) {
        try {
            this.settingLock.writeLock().lock();
            if (this.timeBetweenBroadcast == timeBetweenBroadcast) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.timeBetweenBroadcast = timeBetweenBroadcast;
            for (OBSERVER obs : this.observers) {
                obs.timeBetweenBroadcastChanged(timeBetweenBroadcast);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies how much ETA must change for a route
     * broadcast to be forced.
     * 
     * @return How much ETA must change for a route broadcast to be forced.
     */
    public int getAdaptionTime() {
        try {
            this.settingLock.readLock().lock();
            return this.adaptionTime;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the change in ETA for a new route
     * broadcast to be forced.
     * 
     * @param adaptionTime
     *            How much ETA must change for a route broadcast to be forced.
     */
    public void setAdaptionTime(final int adaptionTime) {
        try {
            this.settingLock.writeLock().lock();
            if (this.adaptionTime == adaptionTime) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.adaptionTime = adaptionTime;
            for (OBSERVER obs : this.observers) {
                obs.adaptionTimeChanged(adaptionTime);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Interface for observing an {@link IntendedRouteHandlerSettings} for
     * changes.
     * 
     * @author Janus Varmarken
     * 
     */
    public interface IObserver extends
            IntendedRouteHandlerCommonSettings.IObserver {

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
         *            The new value for the time between each broadcast of
         *            intended route. See
         *            {@link IntendedRouteHandlerSettings#getTimeBetweenBroadCast()}
         *            .
         */
        void timeBetweenBroadcastChanged(long timeBetweenBroadcast);

        /**
         * Invoked when {@link IntendedRouteHandlerSettings#getAdaptionTime()}
         * has changed.
         * 
         * @param adaptionTime
         *            How much ETA must change for a route broadcast to be
         *            forced.
         */
        void adaptionTimeChanged(int adaptionTime);
    }

}

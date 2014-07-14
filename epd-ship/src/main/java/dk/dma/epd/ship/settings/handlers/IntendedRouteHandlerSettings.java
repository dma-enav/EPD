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
package dk.dma.epd.ship.settings.handlers;

import dk.dma.epd.common.prototype.model.route.PartialRouteFilter;
import dk.dma.epd.common.prototype.settings.handlers.IntendedRouteHandlerCommonSettings;
import dk.dma.epd.ship.service.IntendedRouteHandler;
import dk.dma.epd.ship.settings.observers.IntendedRouteHandlerSettingsListener;

/**
 * Maintains settings for an {@link IntendedRouteHandler}.
 */
public class IntendedRouteHandlerSettings extends IntendedRouteHandlerCommonSettings<IntendedRouteHandlerSettingsListener> {

    /**
     * Specifies if the ship should broadcast its intended route.
     */
    private boolean broadcastIntendedRoute = true;

    /**
     * Specifies the intended route broadcast time. Unit: seconds.
     */
    private long timeBetweenBroadcast = 60;

    /**
     * Specifies the change in ETA for a new route broadcast to be forced. Unit:
     * seconds.
     */
    private int adaptionTime = 60;

    /**
     * Filter used in route transmission.
     */
    private PartialRouteFilter intendedRouteFilter = PartialRouteFilter.DEFAULT;

    /**
     * The intended route broadcast radius.
     */
    private int broadcastRadius = Integer.MAX_VALUE;

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
            for (IntendedRouteHandlerSettingsListener obs : this.observers) {
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
     * @return timeBetweenBroadCast The time between each broadcast in seconds.
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
     *            The new time between each broadcast in seconds.
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
            for (IntendedRouteHandlerSettingsListener obs : this.observers) {
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
            for (IntendedRouteHandlerSettingsListener obs : this.observers) {
                obs.adaptionTimeChanged(adaptionTime);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the filter to use in route transmission.
     * 
     * @return The filter to use in route transmission.
     */
    public PartialRouteFilter getIntendedRouteFilter() {
        try {
            this.settingLock.readLock().lock();
            return intendedRouteFilter;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Sets the filter to use in route transmission.
     * 
     * @param intendedRouteFilter
     *            The new filter to use in route transmission.
     */
    public void setIntendedRouteFilter(
            final PartialRouteFilter intendedRouteFilter) {
        try {
            this.settingLock.writeLock().lock();
            if (this.intendedRouteFilter.equals(intendedRouteFilter)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            // TODO create copy to avoid reference leak.
            this.intendedRouteFilter = intendedRouteFilter;
            for (IntendedRouteHandlerSettingsListener obs : this.observers) {
                // TODO create copy to avoid reference leak.
                obs.intendedRouteFilterChanged(intendedRouteFilter);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the intended route broadcast radius.
     * 
     * @return The intended route broadcast radius.
     */
    public int getBroadcastRadius() {
        try {
            this.settingLock.readLock().lock();
            return broadcastRadius;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Sets the intended route broadcast radius.
     * 
     * @param broadcastRadius
     *            The new intended route broadcast radius.
     */
    public void setBroadcastRadius(final int broadcastRadius) {
        try {
            this.settingLock.writeLock().lock();
            if (this.broadcastRadius == broadcastRadius) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.broadcastRadius = broadcastRadius;
            for (IntendedRouteHandlerSettingsListener obs : this.observers) {
                obs.broadcastRadiusChanged(broadcastRadius);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }
}

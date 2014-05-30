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
package dk.dma.epd.common.prototype.settings.handlers;

import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;
import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.observers.IntendedRouteHandlerCommonSettingsListener;

/**
 * Maintains settings relevant to an {@link IntendedRouteHandlerCommon} or any
 * of its subclasses. This class inherits from {@link ObservedSettings} allowing
 * clients to register for notifications of changes to any setting maintained by
 * this class.
 */
public class IntendedRouteHandlerCommonSettings<OBSERVER extends IntendedRouteHandlerCommonSettingsListener>
        extends HandlerSettings<OBSERVER> {

    /**
     * If no updates are received for an intended route within this time frame,
     * the route is considered dead/inactive. Unit is milliseconds.
     */
    private long routeTimeToLive = 10 * 60 * 1000; // 10 minutes.

    /**
     * Specifies a distance in nautical miles. If two route way points are
     * within this distance of each other at a certain point in time, these way
     * points are to be included in the filter.
     */
    private double filterDistance = 0.5;

    /**
     * Specifies a distance in nautical miles. If two route way points are
     * within this distance of each other at a certain point in time, a warning
     * should be generated for these way points.
     */
    private double notificationDistance = 0.5;

    /**
     * Specifies a distance in nautical miles. If two route way points are
     * within this distance of each other at a certain point in time, an alert
     * should be generated for these way points.
     */
    private double alertDistance = 0.3;

    /**
     * Get how long a route is kept alive when no updates are received. Unit is
     * milliseconds.
     * 
     * @return How long (in milliseconds) a route is kept alive when no updates
     *         are received.
     */
    public long getRouteTimeToLive() {
        try {
            this.settingLock.readLock().lock();
            return this.routeTimeToLive;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Change how long a route is kept alive when no updates are received. Unit
     * is milliseconds.
     * 
     * @param routeTimeToLive
     *            How long (in milliseconds) a route is kept alive when no
     *            updates are received.
     */
    public void setRouteTimeToLive(final long routeTimeToLive) {
        try {
            this.settingLock.writeLock().lock();
            if (this.routeTimeToLive == routeTimeToLive) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.routeTimeToLive = routeTimeToLive;
            for (OBSERVER obs : this.observers) {
                obs.onRouteTimeToLiveChanged(routeTimeToLive);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the filter distance. If two route way points are within this distance
     * of each other at a certain point in time, these way points are to be
     * included in the filter.
     * 
     * @return The filter distance in nautical miles.
     */
    public double getFilterDistance() {
        try {
            this.settingLock.readLock().lock();
            return this.filterDistance;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Change the filter distance. If two route way points are within the given
     * distance of each other at a certain point in time, these way points are
     * to be included in the filter.
     * 
     * @return The new filter distance in nautical miles.
     */
    public void setFilterDistance(final double filterDistance) {
        try {
            this.settingLock.writeLock().lock();
            if (this.filterDistance == filterDistance) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.filterDistance = filterDistance;
            for (OBSERVER obs : this.observers) {
                obs.onFilterDistanceChanged(filterDistance);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the notification distance. If two route way points are within this
     * distance of each other at a certain point in time, a warning should be
     * generated for these way points.
     * 
     * @return The notification distance in nautical miles.
     */
    public double getNotificationDistance() {
        try {
            this.settingLock.readLock().lock();
            return this.notificationDistance;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Change the notification distance. If two route way points are within the
     * given distance of each other at a certain point in time, a warning should
     * be generated for these way points.
     * 
     * @param notificationDistance
     *            The new notification distance in nautical miles.
     */
    public void setNotificationDistance(final double notificationDistance) {
        try {
            this.settingLock.writeLock().lock();
            if (this.notificationDistance == notificationDistance) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.notificationDistance = notificationDistance;
            for (OBSERVER obs : this.observers) {
                obs.onNotificationDistanceChanged(notificationDistance);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the alert distance. If two route way points are within this distance
     * of each other at a certain point in time, an alert should be generated
     * for these way points.
     * 
     * @return The alert distance in nautical miles.
     */
    public double getAlertDistance() {
        try {
            this.settingLock.readLock().lock();
            return this.alertDistance;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Change the alert distance. If two route way points are within the given
     * distance of each other at a certain point in time, an alert should be
     * generated for these way points.
     * 
     * @param alertDistance
     *            The alert distance in nautical miles.
     */
    public void setAlertDistance(final double alertDistance) {
        try {
            this.settingLock.writeLock().lock();
            if (this.alertDistance == alertDistance) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.alertDistance = alertDistance;
            for (OBSERVER obs : this.observers) {
                obs.onAlertDistanceChanged(alertDistance);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }
}

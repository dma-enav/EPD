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

import dk.dma.epd.common.prototype.settings.observers.MetocHandlerCommonSettingsListener;

/**
 * Maintains settings for a METOC handler.
 * 
 * @author Janus Varmarken
 */
public class MetocHandlerCommonSettings<OBSERVER extends MetocHandlerCommonSettingsListener>
        extends HandlerSettings<OBSERVER> implements
        MetocHandlerCommonSettingsListener {

    /**
     * How long should METOC for route be considered valid. Unit: minutes.
     */
    private int metocTtl = 60;

    /**
     * The minimum interval between METOC polls for active route. 0 means never.
     * Unit: minutes.
     */
    private int activeRouteMetocPollInterval = 5;

    /**
     * The tolerance of how long we may drift from plan before METOC is
     * considered invalid. Unit: minutes.
     */
    private int metocTimeDiffTolerance = 15;

    /**
     * Get the setting that specifies how long (in minutes) METOC data for a
     * route is considered valid.
     * 
     * @return How long METOC data for a route is considered valid. Unit is
     *         minutes.
     */
    public int getMetocTtl() {
        try {
            this.settingLock.readLock().lock();
            return this.metocTtl;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies how long (in minutes) METOC data for a
     * route is considered valid.
     * 
     * @param metocTtl
     *            How long METOC data for a route is considered valid. Unit is
     *            minutes.
     */
    public void setMetocTtl(final int metocTtl) {
        try {
            this.settingLock.writeLock().lock();
            if (this.metocTtl == metocTtl) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify.
            this.metocTtl = metocTtl;
            for (OBSERVER obs : this.observers) {
                obs.metocTtlChanged(metocTtl);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies the minimum interval between METOC polls
     * for an active route. 0 means never. Unit: minutes.
     * 
     * @return The minimum interval between METOC polls for an active route in
     *         minutes. 0 means never.
     */
    public int getActiveRouteMetocPollInterval() {
        try {
            this.settingLock.readLock().lock();
            return this.activeRouteMetocPollInterval;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the minimum interval between METOC
     * polls for an active route. 0 means never. Unit: minutes.
     * 
     * @param activeRouteMetocPollInterval
     *            The minimum interval between METOC polls for an active route
     *            in minutes. 0 means never.
     */
    public void setActiveRouteMetocPollInterval(
            final int activeRouteMetocPollInterval) {
        try {
            this.settingLock.writeLock().lock();
            if (this.activeRouteMetocPollInterval == activeRouteMetocPollInterval) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.activeRouteMetocPollInterval = activeRouteMetocPollInterval;
            for (OBSERVER obs : this.observers) {
                obs.activeRouteMetocPollIntervalChanged(activeRouteMetocPollInterval);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies the tolerance of how long a vessel may
     * drift from plan before METOC is considered invalid. Unit: minutes.
     * 
     * @return The maximum number of minutes a vessel may drift from plan before
     *         METOC is considered invalid.
     */
    public int getMetocTimeDiffTolerance() {
        try {
            this.settingLock.readLock().lock();
            return this.metocTimeDiffTolerance;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the tolerance of how long a vessel may
     * drift from plan before METOC is considered invalid. Unit: minutes.
     * 
     * @param metocTimeDiffTolerance
     *            The maximum number of minutes a vessel may drift from plan
     *            before METOC is considered invalid.
     */
    public void setMetocTimeDiffTolerance(final int metocTimeDiffTolerance) {
        try {
            this.settingLock.writeLock().lock();
            if (this.metocTimeDiffTolerance == metocTimeDiffTolerance) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.metocTimeDiffTolerance = metocTimeDiffTolerance;
            for (OBSERVER obs : this.observers) {
                obs.metocTimeDiffToleranceChanged(metocTimeDiffTolerance);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /*
     * Begin: Listener methods that are only used if this instance observes
     * another instance of this class.
     */

    @Override
    public void metocTtlChanged(int newMetocTtl) {
        // Obey to change in observed instance.
        this.setMetocTtl(newMetocTtl);
    }

    @Override
    public void activeRouteMetocPollIntervalChanged(int newInterval) {
        // Obey to change in observed instance.
        this.setActiveRouteMetocPollInterval(newInterval);
    }

    @Override
    public void metocTimeDiffToleranceChanged(int metocTimeDiffTolerance) {
        // Obey to change in observed instance.
        this.setMetocTimeDiffTolerance(metocTimeDiffTolerance);
    }

    /*
     * End: Listener methods that are only used if this instance observes
     * another instance of this class.
     */

}

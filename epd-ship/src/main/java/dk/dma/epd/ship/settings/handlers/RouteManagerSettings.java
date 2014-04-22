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

import dk.dma.epd.common.prototype.settings.handlers.RouteManagerCommonSettings;
import dk.dma.epd.ship.route.RouteManager;

/**
 * Extends {@link RouteManagerCommonSettings} with settings specific to the EPD
 * ship {@link RouteManager}.
 * 
 * @author Janus Varmarken
 */
public class RouteManagerSettings<OBSERVER extends RouteManagerSettings.IObserver>
        extends RouteManagerCommonSettings<OBSERVER> {

    private double minWpRadius = 0.2;

    private boolean relaxedWpChange = true;

    /**
     * TODO add documentation that describes the purpose of this setting.
     */
    public double getMinWpRadius() {
        try {
            this.settingLock.readLock().lock();
            return this.minWpRadius;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * TODO add documentation that describes the purpose of this setting.
     */
    public void setMinWpRadius(final double minWpRadius) {
        try {
            this.settingLock.writeLock().lock();
            if (this.minWpRadius == minWpRadius) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.minWpRadius = minWpRadius;
            for (OBSERVER obs : this.observers) {
                obs.minWpRadiusChanged(minWpRadius);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * TODO add documentation that describes the purpose of this setting.
     */
    public boolean isRelaxedWpChange() {
        try {
            this.settingLock.readLock().lock();
            return this.relaxedWpChange;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * TODO add documentation that describes the purpose of this setting.
     */
    public void setRelaxedWpChange(final boolean relaxedWpChange) {
        try {
            this.settingLock.writeLock().lock();
            if (this.relaxedWpChange == relaxedWpChange) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.relaxedWpChange = relaxedWpChange;
            for (OBSERVER obs : this.observers) {
                obs.relaxedWpChangeChanged(relaxedWpChange);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Interface for observing a {@link RouteManagerSettings} for changes.
     * 
     * @author Janus Varmarken
     * 
     */
    public interface IObserver extends RouteManagerCommonSettings.IObserver {

        /**
         * Invoked when {@link RouteManagerSettings#getMinWpRadius()} has
         * changed.
         * 
         * @param newMinWpRadius
         */
        void minWpRadiusChanged(double newMinWpRadius);

        /**
         * Invoked when {@link RouteManagerSettings#isRelaxedWpChange()} has
         * changed.
         * 
         * @param newRelaxedWpChange
         */
        void relaxedWpChangeChanged(boolean newRelaxedWpChange);
    }
}

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

import dk.dma.epd.common.prototype.route.RouteManagerCommon;

/**
 * Maintains settings for a {@link RouteManagerCommon}. Clients may register for
 * notifications which are fired whenever a setting value has been changed.
 * 
 * @author Janus Varmarken
 */
public class RouteManagerCommonSettings<OBSERVER extends RouteManagerCommonSettings.IObserver>
        extends HandlerSettings<OBSERVER> {

    /**
     * Default speed for a (new) route.
     */
    private double defaultSpeed = 10.0;

    /**
     * Default turn rad for a (new) route.
     */
    private double defaultTurnRad = 0.5;

    /**
     * Default cross track distance for a (new) route.
     */
    private double defaultXtd = 0.1;

    /**
     * Get the default speed for a (new) route.
     * 
     * @return The default speed for a (new) route. TODO: unit clarification
     *         needed.
     */
    public double getDefaultSpeed() {
        try {
            this.settingLock.readLock().lock();
            return this.defaultSpeed;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Change the default speed for a (new) route.
     * 
     * @param defaultSpeed
     *            The new default speed for a (new) route. TODO: unit
     *            clarification needed.
     */
    public void setDefaultSpeed(final double defaultSpeed) {
        try {
            this.settingLock.writeLock().lock();
            if (this.defaultSpeed == defaultSpeed) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.defaultSpeed = defaultSpeed;
            for (OBSERVER obs : this.observers) {
                obs.onDefaultSpeedChanged(defaultSpeed);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the default turn rad for a (new) route.
     * 
     * @return The default turn rad for a (new) route. TODO: unit clarification
     *         needed.
     */
    public double getDefaultTurnRad() {
        try {
            this.settingLock.readLock().lock();
            return this.defaultTurnRad;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Change the default turn rad for a (new) route.
     * 
     * @param defaultTurnRad
     *            The new default turn rad for a (new) route. TODO: unit
     *            clarification needed.
     */
    public void setDefaultTurnRad(final double defaultTurnRad) {
        try {
            this.settingLock.writeLock().lock();
            if (this.defaultTurnRad == defaultTurnRad) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.defaultTurnRad = defaultTurnRad;
            for (OBSERVER obs : this.observers) {
                obs.onDefaultTurnRadChanged(defaultTurnRad);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the default cross track distance (XTD) for a (new) route.
     * 
     * @return The default XTD for a (new) route. TODO: unit clarification
     *         needed.
     */
    public double getDefaultXtd() {
        try {
            this.settingLock.readLock().lock();
            return this.defaultXtd;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Change the default cross track distance (XTD) for a (new) route.
     * 
     * @param defaultXtd
     *            The new default XTD for a (new) route. TODO: unit
     *            clarification needed.
     */
    public void setDefaultXtd(final double defaultXtd) {
        try {
            this.settingLock.writeLock().lock();
            if (this.defaultXtd == defaultXtd) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.defaultXtd = defaultXtd;
            for (OBSERVER obs : this.observers) {
                obs.onDefaultXtdChanged(defaultXtd);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Interface for observing a {@link RouteManagerCommonSettings} for changes.
     * 
     * @author Janus Varmarken
     * 
     */
    public interface IObserver extends HandlerSettings.IObserver {

        /**
         * Invoked when {@link RouteManagerCommonSettings#getDefaultSpeed()} has
         * changed.
         * 
         * @param defaultSpeed
         *            The new default speed. See
         *            {@link RouteManagerCommonSettings#getDefaultSpeed()} for
         *            more details, e.g. unit.
         */
        void onDefaultSpeedChanged(double defaultSpeed);

        /**
         * Invoked when {@link RouteManagerCommonSettings#getDefaultTurnRad()}
         * has changed.
         * 
         * @param defaultTurnRad
         *            The new default turn rad. See
         *            {@link RouteManagerCommonSettings#getDefaultTurnRad()} for
         *            more details, e.g. unit.
         */
        void onDefaultTurnRadChanged(double defaultTurnRad);

        /**
         * Invoked when {@link RouteManagerCommonSettings#getDefaultXtd()} has
         * changed.
         * 
         * @param defaultXtd
         *            The new default XTD. See
         *            {@link RouteManagerCommonSettings#getDefaultXtd()} for
         *            more details, e.g. unit.
         */
        void onDefaultXtdChanged(double defaultXtd);
    }
}

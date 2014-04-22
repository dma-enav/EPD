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
package dk.dma.epd.ship.settings.gui;

import dk.dma.epd.common.prototype.settings.gui.MapCommonSettings;

/**
 * This class extends {@link MapCommonSettings} with map settings that are
 * specific to EPD Ship.
 * 
 * @author Janus Varmarken
 */
public class MapSettings<OBSERVER extends MapSettings.IObserver> extends
        MapCommonSettings<OBSERVER> {

    private int autoFollowPctOffTollerance = 10;

    private boolean lookAhead;

    /**
     * Setting specifying if the map should automatically follow a vessel.
     */
    private boolean autoFollow;

    /**
     * TODO add documentation that describes the purpose of this setting.
     */
    public int getAutoFollowPctOffTollerance() {
        try {
            this.settingLock.readLock().lock();
            return this.autoFollowPctOffTollerance;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * TODO add documentation that describes the purpose of this setting.
     */
    public void setAutoFollowPctOffTollerance(
            final int autoFollowPctOffTollerance) {
        try {
            this.settingLock.writeLock().lock();
            if (this.autoFollowPctOffTollerance == autoFollowPctOffTollerance) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.autoFollowPctOffTollerance = autoFollowPctOffTollerance;
            for (OBSERVER obs : this.observers) {
                obs.autoFollowPctOffToleranceChanged(autoFollowPctOffTollerance);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * TODO add documentation that describes the purpose of this setting.
     */
    public boolean isLookAhead() {
        try {
            this.settingLock.readLock().lock();
            return this.lookAhead;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * TODO add documentation that describes the purpose of this setting.
     */
    public void setLookAhead(final boolean lookAhead) {
        try {
            this.settingLock.writeLock().lock();
            if (this.lookAhead == lookAhead) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.lookAhead = lookAhead;
            for (OBSERVER obs : this.observers) {
                obs.lookAheadChanged(lookAhead);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the setting that specifies if the map should automatically follow a
     * vessel.
     * 
     * @return {@code true} if auto follow is enabled, {@code false} if it is
     *         disabled.
     */
    public boolean isAutoFollow() {
        try {
            this.settingLock.readLock().lock();
            return this.autoFollow;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies if the map should automatically follow
     * a vessel.
     * 
     * @param autoFollow
     *            {@code true} if auto follow is to be enabled, {@code false} if
     *            it is to be disabled.
     */
    public void setAutoFollow(final boolean autoFollow) {
        try {
            this.settingLock.writeLock().lock();
            if (this.autoFollow == autoFollow) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.autoFollow = autoFollow;
            for (OBSERVER obs : this.observers) {
                obs.autoFollowChanged(autoFollow);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Interface for observing a {@link MapSettings} for changes.
     * 
     * @author Janus Varmarken
     * 
     */
    public interface IObserver extends MapCommonSettings.IObserver {

        /**
         * Invoked when {@link MapSettings#getAutoFollowPctOffTollerance()} has
         * changed.
         * 
         * @param newAutoFollowPctOffTolerance
         */
        void autoFollowPctOffToleranceChanged(int newAutoFollowPctOffTolerance);

        /**
         * Invoked when {@link MapSettings#isLookAhead()} has changed.
         * 
         * @param newLookAhead
         */
        void lookAheadChanged(boolean newLookAhead);

        /**
         * Invoked when {@link MapSettings#isAutoFollow()} has changed.
         * 
         * @param newAutoFollow
         */
        void autoFollowChanged(boolean newAutoFollow);
    }
}

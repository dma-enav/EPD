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
package dk.dma.epd.common.prototype.settings.layers;

import dk.dma.ais.data.AisTarget;
import dk.dma.epd.common.prototype.ais.MobileTarget;
import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.handlers.AisHandlerCommonSettings;

/**
 * <p>
 * Maintains past track settings.
 * </p>
 * <p>
 * Past track settings were originally placed in
 * {@link AisHandlerCommonSettings} but were moved to this class as past track
 * is too specific to belong to the {@link AisHandlerCommonSettings}. This is
 * because {@link AisTarget}s are not necessarily {@link MobileTarget}s, and
 * past track is only relevant to {@link MobileTarget}s. Note that this is not
 * necessarily the best/final solution to this issue, but it was the best I
 * could come up with at the time of writing.
 * </p>
 * 
 * @author Janus Varmarken
 */
public class PastTrackSettings<OBSERVER extends PastTrackSettings.IObserver>
        extends ObservedSettings<OBSERVER> {

    /**
     * Setting specifying the maximum length (in minutes) of the past track.
     */
    private int pastTrackMaxTime = 4 * 60;

    /**
     * Setting specifying the past track display time (in minutes), i.e. how
     * much of the past track is visualized on map. TODO: Need verification of
     * the semantics of this setting. TODO: consider if this should be a layer
     * setting.
     */
    private int pastTrackDisplayTime = 30;

    /**
     * Setting specifying the minimum distance (in meters) between two past
     * track way points. TODO: Need verification of the semantics of this
     * setting.
     */
    private int pastTrackMinDist = 100;

    /*
     * TODO: I have removed this setting. This relies on the assumption that the
     * OwnShip handler can simply use its own instance of this class and hence
     * use the value of pastTrackMinDist.
     */
    // /**
    // * In meters
    // */
    // private int pastTrackOwnShipMinDist = 20;

    /**
     * Get the setting that specifies the maximum length (in minutes) of the
     * past track.
     * 
     * @return The maximum length of the past track in minutes.
     */
    public int getPastTrackMaxTime() {
        try {
            this.settingLock.readLock().lock();
            return this.pastTrackMaxTime;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the maximum length (in minutes) of the
     * past track.
     * 
     * @param pastTrackMaxTime
     *            The maximum length of the past track in minutes.
     */
    public void setPastTrackMaxTime(final int pastTrackMaxTime) {
        try {
            this.settingLock.writeLock().lock();
            if (this.pastTrackMaxTime == pastTrackMaxTime) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.pastTrackMaxTime = pastTrackMaxTime;
            for (OBSERVER obs : this.observers) {
                obs.pastTrackMaxTimeChanged(pastTrackMaxTime);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the setting that specifies the past track display time (in minutes),
     * i.e. how much of the past track is visualized on map. TODO: Need
     * verification of the semantics of this setting, use with caution.
     * 
     * @return The past track display time in minutes.
     */
    public int getPastTrackDisplayTime() {
        try {
            this.settingLock.readLock().lock();
            return this.pastTrackDisplayTime;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the past track display time (in
     * minutes), i.e. how much of the past track is visualized on map. TODO:
     * Need verification of the semantics of this setting, use with caution.
     * 
     * @param pastTrackDisplayTime
     *            The new past track display time in minutes.
     */
    public void setPastTrackDisplayTime(final int pastTrackDisplayTime) {
        try {
            this.settingLock.writeLock().lock();
            if (this.pastTrackDisplayTime == pastTrackDisplayTime) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.pastTrackDisplayTime = pastTrackDisplayTime;
            for (OBSERVER obs : this.observers) {
                obs.pastTrackDisplayTimeChanged(pastTrackDisplayTime);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the setting that specifies the minimum distance (in meters) between
     * two past track way points. TODO: Need verification of the semantics of
     * this setting, use with caution.
     * 
     * @return The minimum distance between two past track points (in meters).
     */
    public int getPastTrackMinDist() {
        try {
            this.settingLock.readLock().lock();
            return this.pastTrackMinDist;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the minimum distance (in meters)
     * between two past track way points. TODO: Need verification of the
     * semantics of this setting, use with caution.
     * 
     * @param pastTrackMinDist
     *            The minimum distance between two past track points (in
     *            meters).
     */
    public void setPastTrackMinDist(final int pastTrackMinDist) {
        try {
            this.settingLock.writeLock().lock();
            if (this.pastTrackMinDist == pastTrackMinDist) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.pastTrackMinDist = pastTrackMinDist;
            for (OBSERVER obs : this.observers) {
                obs.pastTrackMinDistChanged(pastTrackMinDist);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Interface for observing a {@link PastTrackSettings} for changes.
     * 
     * @author Janus Varmarken
     * 
     */
    public interface IObserver {

        /**
         * Invoked when {@link PastTrackSettings#getPastTrackMaxTime()} has
         * changed.
         * 
         * @param maxTime
         *            The new past track max time. See
         *            {@link PastTrackSettings#getPastTrackMaxTime()} for more
         *            details.
         */
        void pastTrackMaxTimeChanged(int maxTime);

        /**
         * Invoked when {@link PastTrackSettings#getPastTrackDisplayTime()} has
         * changed.
         * 
         * @param displayTime
         *            The new past track display time. See
         *            {@link PastTrackSettings#getPastTrackDisplayTime()} for
         *            more details.
         */
        void pastTrackDisplayTimeChanged(int displayTime);

        /**
         * Invoked when {@link PastTrackSettings#getPastTrackMinDist()} has
         * changed.
         * 
         * @param minDist
         *            The new past track minimum distance. See
         *            {@link PastTrackSettings#getPastTrackMinDist()} for more
         *            details.
         */
        void pastTrackMinDistChanged(int minDist);
    }
}

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

import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.observers.VesselLayerSettingsListener;
import dk.dma.epd.common.prototype.zoom.ScaleDependentValues;

/**
 * This class maintains settings that are relevant to any layer that visualizes
 * one or more vessels. This class inherits from {@link ObservedSettings}
 * allowing clients to register for notifications of changes to any setting
 * maintained by this class.
 * 
 * @param <OBSERVER>
 *            The type of observers observing the {@code VesselLayerSettings}
 *            for changes.
 * 
 * @author Janus Varmarken
 */
public abstract class VesselLayerSettings<OBSERVER extends VesselLayerSettingsListener>
        extends LayerSettings<OBSERVER> {

    /**
     * Boolean indicating whether vessel name labels should be displayed.
     */
    private boolean showVesselNameLabels;

    /**
     * The minimum length (in minutes) of the vector that indicates COG and
     * speed.
     */
    private int movementVectorLengthMin = 1;

    /**
     * The maximum length (in minutes) of the vector that indicates COG and
     * speed.
     */
    private int movementVectorLengthMax = 8;

    /**
     * Specifies a step size (in terms of map scale) that is used to dynamically
     * calculate the length (in minutes) of the vector that indicates COG and
     * speed. I.e. this value specifies how much difference in map scale there
     * should be between a vector with length <i>n</i> minutes and a vector with
     * length <i>n</i>+1 minutes (assuming the vector length is incremented by 1
     * for each step). See
     * {@link ScaleDependentValues#getCogVectorLength(float)} for implementation
     * usage example.
     */
    private float movementVectorLengthStepSize = 5000.0f;

    /**
     * Specifies a speed value (in nautical miles per hour) that is a lower
     * bound on the display of the vector that indicates COG and speed. I.e. if
     * the current speed is below this value, the vector should not be
     * displayed.
     */
    private float movementVectorHideBelow = 0.1f;

    /**
     * Gets whether vessel name labels should be displayed.
     * 
     * @return {@code true} if vessel name labels should be displayed,
     *         {@code false} if vessel name labels should be hidden.
     */
    public boolean isShowVesselNameLabels() {
        try {
            this.settingLock.readLock().lock();
            return this.showVesselNameLabels;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Sets whether vessel name labels should be displayed.
     * 
     * @param show
     *            {@code true} if vessel name labels should be displayed,
     *            {@code false} if vessel name labels should be hidden.
     */
    public void setShowVesselNameLabels(final boolean show) {
        try {
            this.settingLock.writeLock().lock();
            if (this.showVesselNameLabels == show) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.showVesselNameLabels = show;
            for (OBSERVER obs : this.observers) {
                obs.showVesselNameLabelsChanged(this, show);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the setting that specifies the minimum length (in minutes) of the
     * vector that indicates COG and speed.
     * 
     * @return The minimum length (in minutes) of the vector that indicates COG
     *         and speed.
     */
    public int getMovementVectorLengthMin() {
        try {
            this.settingLock.readLock().lock();
            return this.movementVectorLengthMin;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the minimum length (in minutes) of the
     * vector that indicates COG and speed.
     * 
     * @param minutes
     *            The new minimum length (in minutes) of the vector that
     *            indicates COG and speed.
     * @throws IllegalArgumentException
     *             if {@code minutes} is negative.
     */
    public void setMovementVectorLengthMin(final int minutes) {
        if (minutes < 0) {
            // TODO: do we want to require (1 <= minutes)?
            throw new IllegalArgumentException(
                    "The movement vector length minimum cannot be negative.");
        }
        try {
            this.settingLock.writeLock().lock();
            if (this.movementVectorLengthMin == minutes) {
                // No change, no need to notify observers.
                return;
            }
            // There was an actual change, update and notify.
            this.movementVectorLengthMin = minutes;
            for (OBSERVER obs : this.observers) {
                obs.movementVectorLengthMinChanged(minutes);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }

    }

    /**
     * Gets the setting that specifies the maximum length (in minutes) of the
     * vector that indicates COG and speed.
     * 
     * @return The maximum length (in minutes) of the vector that indicates COG
     *         and speed.
     */
    public int getMovementVectorLengthMax() {
        try {
            this.settingLock.readLock().lock();
            return this.movementVectorLengthMax;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the maximum length (in minutes) of the
     * vector that indicates COG and speed.
     * 
     * @param minutes
     *            The new maximum length (in minutes) of the vector that
     *            indicates COG and speed.
     * @throws IllegalArgumentException
     *             if {@code minutes} is negative.
     */
    public void setMovementVectorLengthMax(final int minutes) {
        if (minutes < 0) {
            // TODO: do we want to require (1 <= minutes)?
            throw new IllegalArgumentException(
                    "The movement vector length maximum cannot be negative.");
        }
        try {
            this.settingLock.writeLock().lock();
            if (this.movementVectorLengthMax == minutes) {
                // No change, no need to notify observers.
                return;
            }
            // There was an actual change, update and notify.
            this.movementVectorLengthMax = minutes;
            for (OBSERVER obs : this.observers) {
                obs.movementVectorLengthMaxChanged(minutes);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies a step size (in terms of map scale) that
     * is used to dynamically calculate the length (in minutes) of the vector
     * that indicates COG and speed. I.e. this value specifies how much
     * difference in map scale there should be between a vector with length
     * <i>n</i> minutes and a vector with length <i>n</i>+1 minutes (assuming
     * the vector length is incremented by 1 for each step). See
     * {@link ScaleDependentValues#getCogVectorLength(float)} for implementation
     * usage example.
     * 
     * @return The step size in terms of map scale.
     */
    public float getMovementVectorLengthStepSize() {
        try {
            this.settingLock.readLock().lock();
            return this.movementVectorLengthStepSize;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies a step size (in terms of map scale)
     * that is used to dynamically calculate the length (in minutes) of the
     * vector that indicates COG and speed. I.e. this value specifies how much
     * difference in map scale there should be between a vector with length
     * <i>n</i> minutes and a vector with length <i>n</i>+1 minutes (assuming
     * the vector length is incremented by 1 for each step).
     * 
     * @param stepSize
     *            The new step size in terms of map scale.
     * @throws IllegalArgumentException
     *             if {@code stepSize} is negative or zero.
     */
    public void setMovementVectorLengthStepSize(final float stepSize) {
        if (stepSize <= 0f) {
            throw new IllegalArgumentException(
                    "The step size for incrementing the speed vector length cannot be negative nor zero.");
        }
        try {
            this.settingLock.writeLock().lock();
            if (this.movementVectorLengthStepSize == stepSize) {
                // No change, no need to notify observers.
                return;
            }
            // There was an actual change, update and notify.
            this.movementVectorLengthStepSize = stepSize;
            for (OBSERVER obs : this.observers) {
                obs.movementVectorLengthStepSizeChanged(stepSize);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies a speed value (in nautical miles per hour)
     * that is a lower bound on the display of the vector that indicates COG and
     * speed. I.e. if the current speed is below this value, the vector should
     * not be displayed.
     * 
     * @return The minimum speed (in nautical miles per hour) a vessel should
     *         travel with in order for its COG and speed vector to be
     *         displayed.
     */
    public float getMovementVectorHideBelow() {
        try {
            this.settingLock.readLock().lock();
            return this.movementVectorHideBelow;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies a speed value (in nautical miles per
     * hour) that is a lower bound on the display of the vector that indicates
     * COG and speed. I.e. if the current speed is below this value, the vector
     * should not be displayed.
     * 
     * @param minSpeed
     *            The minimum speed (in nautical miles per hour) a vessel should
     *            travel with in order for its COG and speed vector to be
     *            displayed.
     * @throws IllegalArgumentException
     *             if {@code minSpeed} is negative.
     */
    public void setMovementVectorHideBelow(final float minSpeed) {
        if (minSpeed < 0f) {
            throw new IllegalArgumentException(
                    "Minimum speed for display of the COG and speed vector cannot be negative.");
        }
        try {
            this.settingLock.writeLock().lock();
            if (this.movementVectorHideBelow == minSpeed) {
                // No change, no need to notify observers.
                return;
            }
            // There was an actual change, update and notify.
            this.movementVectorHideBelow = minSpeed;
            for (OBSERVER obs : this.observers) {
                obs.movementVectorHideBelowChanged(minSpeed);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }
}

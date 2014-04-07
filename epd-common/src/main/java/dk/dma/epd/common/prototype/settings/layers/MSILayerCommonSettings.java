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

import dk.dma.epd.common.prototype.layers.msi.MsiLayerCommon;

/**
 * This class is used to maintain settings for an {@link MsiLayerCommon}.
 * 
 * @author Janus Varmarken
 */
public class MSILayerCommonSettings<OBSERVER extends IMSILayerCommonSettingsObserver>
        extends LayerSettings<OBSERVER> {

    /**
     * Setting specifying at what scale MSI textboxes should appear. Textboxes
     * appear if the scale value is between 0 and this value.
     */
    private int msiTextboxesVisibleAtScale = 80000;

    /**
     * TODO: Question DNC about the purpose of this setting.
     */
    private double msiVisibilityFromNewWaypoint = 30.0d;

    /**
     * Gets the setting that specifies at what scale MSI textboxes should
     * appear. Textboxes should appear if the map scale is between 0 and this
     * value.
     * 
     * @return An upper bound on scale value for display of MSI textboxes.
     */
    public int getMsiTextboxesVisibleAtScale() {
        try {
            this.settingLock.readLock().lock();
            return this.msiTextboxesVisibleAtScale;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies at what scale MSI textboxes should
     * appear. Textboxes should appear if the map scale is between 0 and the
     * given value.
     * 
     * @param msiTextboxesVisibleAtScale
     *            An upper bound on scale value for display of MSI textboxes.
     */
    public void setMsiTextboxesVisibleAtScale(
            final int msiTextboxesVisibleAtScale) {
        try {
            this.settingLock.writeLock().lock();
            if (this.msiTextboxesVisibleAtScale == msiTextboxesVisibleAtScale) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.msiTextboxesVisibleAtScale = msiTextboxesVisibleAtScale;
            for (OBSERVER obs : this.observers) {
                obs.msiTextboxesVisibleAtScaleChanged(msiTextboxesVisibleAtScale);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * TODO: Question DNC about the purpose of this setting.
     * @return
     */
    public double getMsiVisibilityFromNewWaypoint() {
        try {
            this.settingLock.readLock().lock();
            return msiVisibilityFromNewWaypoint;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * TODO: Question DNC about the purpose of this setting.
     * @param msiVisibilityFromNewWaypoint
     */
    public void setMsiVisibilityFromNewWaypoint(
            final double msiVisibilityFromNewWaypoint) {
        try {
            this.settingLock.writeLock().lock();
            if (this.msiVisibilityFromNewWaypoint == msiVisibilityFromNewWaypoint) {
                // No change, no need to notify observers.
                return;
            }
            // There was change, update and notify observers.
            this.msiVisibilityFromNewWaypoint = msiVisibilityFromNewWaypoint;
            for (OBSERVER obs : this.observers) {
                obs.msiVisibilityFromNewWaypointChanged(msiVisibilityFromNewWaypoint);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }
}

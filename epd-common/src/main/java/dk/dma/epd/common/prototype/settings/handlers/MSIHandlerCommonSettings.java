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

import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.settings.observers.MSIHandlerCommonSettingsListener;

/**
 * Maintains settings for an {@link MsiHandler}.
 * 
 * @author Janus Varmarken
 */
public class MSIHandlerCommonSettings<OBSERVER extends MSIHandlerCommonSettingsListener>
        extends HandlerSettings<OBSERVER> implements
        MSIHandlerCommonSettingsListener {

    /**
     * Setting specifying if MSI filter is in use.
     */
    private boolean msiFilter = true;

    /**
     * The MSI poll interval. Unit: seconds.
     */
    private int msiPollInterval = 600;

    /**
     * Distance from MSI to own ship for MSI to be relevant. TODO: unit? TODO:
     * If possible, move to EPDShip as OwnShip is only relevant to EPDShip.
     */
    private double msiRelevanceFromOwnShipRange = 40.0d;

    /**
     * TODO: Specify the semantics of this setting.
     */
    private double msiRelevanceGpsUpdateRange = 0.5d;

    /**
     * Gets the setting that specifies if MSI filter is in use.
     * 
     * @return {@code true} if filter is in use, {@code false} otherwise.
     */
    public boolean isMsiFilter() {
        try {
            this.settingLock.readLock().lock();
            return this.msiFilter;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies if MSI filter is in use.
     * 
     * @param msiFilter
     *            {@code true} if filter is in use, {@code false} otherwise.
     */
    public void setMsiFilter(final boolean msiFilter) {
        try {
            this.settingLock.writeLock().lock();
            if (this.msiFilter == msiFilter) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.msiFilter = msiFilter;
            for (OBSERVER obs : this.observers) {
                obs.useMsiFilterChanged(msiFilter);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies the MSI poll interval. Unit: seconds.
     * 
     * @return The MSI poll interval. Unit: seconds.
     */
    public int getMsiPollInterval() {
        try {
            this.settingLock.readLock().lock();
            return this.msiPollInterval;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the MSI poll interval. Unit: seconds.
     * 
     * @param msiPollInterval
     *            The MSI poll interval. Unit: seconds.
     */
    public void setMsiPollInterval(final int msiPollInterval) {
        try {
            this.settingLock.writeLock().lock();
            if (this.msiPollInterval == msiPollInterval) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.msiPollInterval = msiPollInterval;
            for (OBSERVER obs : this.observers) {
                obs.msiPollIntervalChanged(msiPollInterval);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies the maximum distance from MSI to own ship
     * for MSI to be relevant. TODO: unit?
     * 
     * @return The maximum distance between MSI and own ship for MSI to be
     *         relevant.
     */
    public double getMsiRelevanceFromOwnShipRange() {
        try {
            this.settingLock.readLock().lock();
            return this.msiRelevanceFromOwnShipRange;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the maximum distance from MSI to own
     * ship for MSI to be relevant. TODO: unit?
     * 
     * @param relevanceRange
     *            The maximum distance between MSI and own ship for MSI to be
     *            relevant.
     */
    public void setMsiRelevanceFromOwnShipRange(final double relevanceRange) {
        try {
            this.settingLock.writeLock().lock();
            if (this.msiRelevanceFromOwnShipRange == relevanceRange) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.msiRelevanceFromOwnShipRange = relevanceRange;
            for (OBSERVER obs : this.observers) {
                obs.msiRelevanceFromOwnShipRangeChanged(relevanceRange);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * TODO: Specify the semantics of this setting.
     * 
     * @return
     */
    public double getMsiRelevanceGpsUpdateRange() {
        try {
            this.settingLock.readLock().lock();
            return this.msiRelevanceGpsUpdateRange;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * TODO: Specify the semantics of this setting.
     * 
     * @param msiRelevanceGpsUpdateRange
     */
    public void setMsiRelevanceGpsUpdateRange(
            final double msiRelevanceGpsUpdateRange) {
        try {
            this.settingLock.writeLock().lock();
            if (this.msiRelevanceGpsUpdateRange == msiRelevanceGpsUpdateRange) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.msiRelevanceGpsUpdateRange = msiRelevanceGpsUpdateRange;
            for (OBSERVER obs : this.observers) {
                obs.msiRelevanceGpsUpdateRangeChanged(msiRelevanceGpsUpdateRange);
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
    public void useMsiFilterChanged(boolean msiFilter) {
        // Obey to change in observed instance.
        this.setMsiFilter(msiFilter);
    }

    @Override
    public void msiPollIntervalChanged(int pollInterval) {
        // Obey to change in observed instance.
        this.setMsiPollInterval(pollInterval);
    }

    @Override
    public void msiRelevanceFromOwnShipRangeChanged(
            double relevanceFromOwnShipRange) {
        // Obey to change in observed instance.
        this.setMsiRelevanceFromOwnShipRange(relevanceFromOwnShipRange);
    }

    @Override
    public void msiRelevanceGpsUpdateRangeChanged(double relevanceGpsUpdateRange) {
        // Obey to change in observed instance.
        this.setMsiRelevanceGpsUpdateRange(relevanceGpsUpdateRange);
    }

    /*
     * End: Listener methods that are only used if this instance observes
     * another instance of this class.
     */

}

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


/**
 * This class is used to maintain settings for a METOC layer.
 * 
 * @author Janus Varmarken
 */
public class MetocLayerCommonSettings<OBSERVER extends IMetocLayerCommonSettingsObserver>
        extends LayerSettings<OBSERVER> {

    /**
     * The default value for the wind warning limit property used in a METOC
     * request. Unit: m/s.
     */
    private double defaultWindWarnLimit = 10.0;

    /**
     * The default value for the current warning limit property used in a METOC
     * request. Unit: m/s. TODO: Should this be knots? See
     * {@link #defaultCurrentLow} and {@link #defaultCurrentMedium}.
     */
    private double defaultCurrentWarnLimit = 4.0;

    /**
     * The default value for the wave warning limit property used in a METOC
     * request. Unit: meters.
     */
    private double defaultWaveWarnLimit = 3.0;

    /**
     * Default metoc-symbol level: The default value for the current low
     * property used in a METOC request. Unit: knots.
     */
    private double defaultCurrentLow = 1.0;

    /**
     * Default metoc-symbol level: The default value for the current medium
     * property used in a METOC request. Unit: knots.
     */
    private double defaultCurrentMedium = 2.0;

    /**
     * Default metoc-symbol level: The default value for the wave low property
     * used in a METOC request. Unit: meters.
     */
    private double defaultWaveLow = 1.0;

    /**
     * Default metoc-symbol level: The default value for the wave medium
     * property used in a METOC request. Unit: meters.
     */
    private double defaultWaveMedium = 2.0;

    /**
     * Gets the default value for the wind warning limit property used in a
     * METOC request. Unit: m/s.
     * 
     * @return The default value for the wind warning limit property used in a
     *         METOC request.
     */
    public double getDefaultWindWarnLimit() {
        try {
            this.settingLock.readLock().lock();
            return this.defaultWindWarnLimit;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the default value for the wind warning limit property used in a
     * METOC request. Unit: m/s.
     * 
     * @param defaultWindWarnLimit
     *            The new default value.
     */
    public void setDefaultWindWarnLimit(final double defaultWindWarnLimit) {
        try {
            this.settingLock.writeLock().lock();
            if (this.defaultWindWarnLimit == defaultWindWarnLimit) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.defaultWindWarnLimit = defaultWindWarnLimit;
            for (OBSERVER obs : this.observers) {
                obs.defaultWindWarnLimitChanged(defaultWindWarnLimit);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the default value for the current warning limit property used in a
     * METOC request. TODO: Update with unit. See
     * {@link #defaultCurrentWarnLimit}.
     * 
     * @return The default value for the current warning limit property used in
     *         a METOC request.
     */
    public double getDefaultCurrentWarnLimit() {
        try {
            this.settingLock.readLock().lock();
            return this.defaultCurrentWarnLimit;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the default value for the current warning limit property used in
     * a METOC request. TODO: Update with unit. See
     * {@link #defaultCurrentWarnLimit}.
     * 
     * @param defaultCurrentWarnLimit
     *            The new default value.
     */
    public void setDefaultCurrentWarnLimit(final double defaultCurrentWarnLimit) {
        try {
            this.settingLock.writeLock().lock();
            if (this.defaultCurrentWarnLimit == defaultCurrentWarnLimit) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.defaultCurrentWarnLimit = defaultCurrentWarnLimit;
            for (OBSERVER obs : this.observers) {
                obs.defaultCurrentWarnLimitChanged(defaultCurrentWarnLimit);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the default value for the wave warning limit property used in a
     * METOC request. Unit: meters.
     * 
     * @return The default value for the wave warning limit property used in a
     *         METOC request.
     */
    public double getDefaultWaveWarnLimit() {
        try {
            this.settingLock.readLock().lock();
            return this.defaultWaveWarnLimit;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the default value for the wave warning limit property used in a
     * METOC request. Unit: meters.
     * 
     * @param defaultWaveWarnLimit
     *            The new default value.
     */
    public void setDefaultWaveWarnLimit(final double defaultWaveWarnLimit) {
        try {
            this.settingLock.writeLock().lock();
            if (this.defaultWaveWarnLimit == defaultWaveWarnLimit) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.defaultWaveWarnLimit = defaultWaveWarnLimit;
            for (OBSERVER obs : this.observers) {
                obs.defaultWaveWarnLimitChanged(defaultWaveWarnLimit);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the default value for the current low property used in a METOC
     * request. Unit: knots.
     * 
     * @return The default value for the current low property used in a METOC
     *         request.
     */
    public double getDefaultCurrentLow() {
        try {
            this.settingLock.readLock().lock();
            return this.defaultCurrentLow;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the default value for the current low property used in a METOC
     * request. Unit: knots.
     * 
     * @param defaultCurrentLow
     *            The new default value.
     */
    public void setDefaultCurrentLow(final double defaultCurrentLow) {
        try {
            this.settingLock.writeLock().lock();
            if (this.defaultCurrentLow == defaultCurrentLow) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.defaultCurrentLow = defaultCurrentLow;
            for (OBSERVER obs : this.observers) {
                obs.defaultCurrentLowChanged(defaultCurrentLow);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the default value for the current medium property used in a METOC
     * request. Unit: knots.
     * 
     * @return The default value for the current medium property used in a METOC
     *         request.
     */
    public double getDefaultCurrentMedium() {
        try {
            this.settingLock.readLock().lock();
            return this.defaultCurrentMedium;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the default value for the current medium property used in a METOC
     * request. Unit: knots.
     * 
     * @param defaultCurrentMedium
     *            The new default value.
     */
    public void setDefaultCurrentMedium(final double defaultCurrentMedium) {
        try {
            this.settingLock.writeLock().lock();
            if (this.defaultCurrentMedium == defaultCurrentMedium) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.defaultCurrentMedium = defaultCurrentMedium;
            for (OBSERVER obs : this.observers) {
                obs.defaultCurrentMediumChanged(defaultCurrentMedium);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the default value for the wave low property used in a METOC request.
     * Unit: meters.
     * 
     * @return the defaultWaveLow The default value for the wave low property
     *         used in a METOC request.
     */
    public double getDefaultWaveLow() {
        try {
            this.settingLock.readLock().lock();
            return this.defaultWaveLow;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the default value for the wave low property used in a METOC
     * request. Unit: meters.
     * 
     * @param defaultWaveLow
     *            The new default value.
     */
    public void setDefaultWaveLow(final double defaultWaveLow) {
        try {
            this.settingLock.writeLock().lock();
            if (this.defaultWaveLow == defaultWaveLow) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.defaultWaveLow = defaultWaveLow;
            for (OBSERVER obs : this.observers) {
                obs.defaultWaveLowChanged(defaultWaveLow);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the default value for the wave medium property used in a METOC
     * request. Unit: meters.
     * 
     * @return The default value for the wave medium property used in a METOC
     *         request. Unit: meters.
     */
    public double getDefaultWaveMedium() {
        try {
            this.settingLock.readLock().lock();
            return this.defaultWaveMedium;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the default value for the wave medium property used in a METOC
     * request. Unit: meters.
     * 
     * @param defaultWaveMedium
     *            The new default value.
     */
    public void setDefaultWaveMedium(final double defaultWaveMedium) {
        try {
            this.settingLock.writeLock().lock();
            if (this.defaultWaveMedium == defaultWaveMedium) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.defaultWaveMedium = defaultWaveMedium;
            for (OBSERVER obs : this.observers) {
                obs.defaultWaveMediumChanged(defaultWaveMedium);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

}

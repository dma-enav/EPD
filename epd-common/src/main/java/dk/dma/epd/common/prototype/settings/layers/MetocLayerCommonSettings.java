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

import dk.dma.epd.common.prototype.settings.observers.MetocLayerCommonSettingsListener;

/**
 * This class is used to maintain settings that defines how METOC data is to be
 * displayed.
 * 
 * @author Janus Varmarken
 */
public class MetocLayerCommonSettings<OBSERVER extends MetocLayerCommonSettingsListener>
        extends LayerSettings<OBSERVER> implements MetocLayerCommonSettingsListener {

    /**
     * The wind warning limit. Unit: m/s.
     */
    private double windWarnLimit = 10.0;

    /**
     * The current warning limit. Unit: m/s. TODO: Should this be knots? See
     * {@link #currentLow} and {@link #currentMedium}.
     */
    private double currentWarnLimit = 4.0;

    /**
     * The wave warning limit. Unit: meters.
     */
    private double waveWarnLimit = 3.0;

    /**
     * METOC-symbol level for low current. Unit: knots.
     */
    private double currentLow = 1.0;

    /**
     * METOC-symbol level for medium current. Unit: knots.
     */
    private double currentMedium = 2.0;

    /**
     * METOC-symbol level for low waves. Unit: meters.
     */
    private double waveLow = 1.0;

    /**
     * METOC-symbol level for medium waves. Unit: meters.
     */
    private double waveMedium = 2.0;

    @Override
    public MetocLayerCommonSettings<OBSERVER> copy() {
        // TODO Auto-generated method stub
        return (MetocLayerCommonSettings<OBSERVER>) super.copy();
    }
    
    /**
     * Gets the wind warning limit. Unit: m/s.
     * 
     * @return The wind warning limit.
     */
    public double getDefaultWindWarnLimit() {
        try {
            this.settingLock.readLock().lock();
            return this.windWarnLimit;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the wind warning limit. Unit: m/s.
     * 
     * @param windWarnLimit
     *            The new wind warning limit.
     */
    public void setDefaultWindWarnLimit(final double windWarnLimit) {
        try {
            this.settingLock.writeLock().lock();
            if (this.windWarnLimit == windWarnLimit) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.windWarnLimit = windWarnLimit;
            for (OBSERVER obs : this.observers) {
                obs.defaultWindWarnLimitChanged(windWarnLimit);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the current warning limit. TODO: Update with unit. See
     * {@link #currentWarnLimit}.
     * 
     * @return The current warning limit.
     */
    public double getDefaultCurrentWarnLimit() {
        try {
            this.settingLock.readLock().lock();
            return this.currentWarnLimit;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the current warning limit. TODO: Update with unit. See
     * {@link #currentWarnLimit}.
     * 
     * @param currentWarnLimit
     *            The new current warning limit.
     */
    public void setDefaultCurrentWarnLimit(final double currentWarnLimit) {
        try {
            this.settingLock.writeLock().lock();
            if (this.currentWarnLimit == currentWarnLimit) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.currentWarnLimit = currentWarnLimit;
            for (OBSERVER obs : this.observers) {
                obs.defaultCurrentWarnLimitChanged(currentWarnLimit);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the wave warning limit. Unit: meters.
     * 
     * @return The wave warning limit.
     */
    public double getDefaultWaveWarnLimit() {
        try {
            this.settingLock.readLock().lock();
            return this.waveWarnLimit;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the wave warning limit. Unit: meters.
     * 
     * @param waveWarnLimit
     *            The new wave warning limit.
     */
    public void setDefaultWaveWarnLimit(final double waveWarnLimit) {
        try {
            this.settingLock.writeLock().lock();
            if (this.waveWarnLimit == waveWarnLimit) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.waveWarnLimit = waveWarnLimit;
            for (OBSERVER obs : this.observers) {
                obs.defaultWaveWarnLimitChanged(waveWarnLimit);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets low current threshold. Unit: knots.
     * 
     * @return The low current threshold.
     */
    public double getDefaultCurrentLow() {
        try {
            this.settingLock.readLock().lock();
            return this.currentLow;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the low current threshold. Unit: knots.
     * 
     * @param currentLow
     *            The new low current threshold.
     */
    public void setDefaultCurrentLow(final double currentLow) {
        try {
            this.settingLock.writeLock().lock();
            if (this.currentLow == currentLow) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.currentLow = currentLow;
            for (OBSERVER obs : this.observers) {
                obs.defaultCurrentLowChanged(currentLow);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the medium current threshold. Unit: knots.
     * 
     * @return The medium current threshold.
     */
    public double getDefaultCurrentMedium() {
        try {
            this.settingLock.readLock().lock();
            return this.currentMedium;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the medium current threshold. Unit: knots.
     * 
     * @param currentMedium
     *            The new medium current threshold.
     */
    public void setDefaultCurrentMedium(final double currentMedium) {
        try {
            this.settingLock.writeLock().lock();
            if (this.currentMedium == currentMedium) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.currentMedium = currentMedium;
            for (OBSERVER obs : this.observers) {
                obs.defaultCurrentMediumChanged(currentMedium);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the low waves threshold. Unit: meters.
     * 
     * @return The low waves threshold.
     */
    public double getDefaultWaveLow() {
        try {
            this.settingLock.readLock().lock();
            return this.waveLow;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the low waves threshold. Unit: meters.
     * 
     * @param waveLow
     *            The new low waves threshold.
     */
    public void setDefaultWaveLow(final double waveLow) {
        try {
            this.settingLock.writeLock().lock();
            if (this.waveLow == waveLow) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.waveLow = waveLow;
            for (OBSERVER obs : this.observers) {
                obs.defaultWaveLowChanged(waveLow);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the medium waves threshold. Unit: meters.
     * 
     * @return The medium waves threshold Unit: meters.
     */
    public double getDefaultWaveMedium() {
        try {
            this.settingLock.readLock().lock();
            return this.waveMedium;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the medium waves threshold. Unit: meters.
     * 
     * @param waveMedium
     *            The new medium waves threshold.
     */
    public void setDefaultWaveMedium(final double waveMedium) {
        try {
            this.settingLock.writeLock().lock();
            if (this.waveMedium == waveMedium) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.waveMedium = waveMedium;
            for (OBSERVER obs : this.observers) {
                obs.defaultWaveMediumChanged(waveMedium);
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
    public void defaultWindWarnLimitChanged(double windWarnLimit) {
        // Obey to change to observed instance.
        this.setDefaultWindWarnLimit(windWarnLimit);
    }

    @Override
    public void defaultCurrentWarnLimitChanged(double currentWarnLimit) {
        // Obey to change to observed instance.
        this.setDefaultCurrentWarnLimit(currentWarnLimit);
    }

    @Override
    public void defaultWaveWarnLimitChanged(double defaultWaveWarnLimit) {
        // Obey to change to observed instance.
        this.setDefaultWaveWarnLimit(defaultWaveWarnLimit);
    }

    @Override
    public void defaultCurrentLowChanged(double defaultCurrentLow) {
        // Obey to change to observed instance.
        this.setDefaultCurrentLow(defaultCurrentLow);
    }
    
    @Override
    public void defaultCurrentMediumChanged(double defaultCurrentMedium) {
        // Obey to change to observed instance.
        this.setDefaultCurrentMedium(defaultCurrentMedium);
    }

    @Override
    public void defaultWaveLowChanged(double defaultWaveLow) {
        // Obey to change to observed instance.
        this.setDefaultWaveLow(defaultWaveLow);
    }

    @Override
    public void defaultWaveMediumChanged(double defaultWaveMedium) {
        // Obey to change to observed instance.
        this.setDefaultWaveMedium(defaultWaveMedium);
    }
    
    /*
     * End: Listener methods that are only used if this instance observes
     * another instance of this class.
     */
}

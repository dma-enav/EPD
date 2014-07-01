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

import java.util.Arrays;

import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.settings.layers.PastTrackSettings;
import dk.dma.epd.common.prototype.settings.observers.AisHandlerCommonSettingsListener;

/**
 * Maintains settings for an {@link AisHandlerCommon}. NB: Past track settings
 * were moved to {@link PastTrackSettings}.
 * 
 * @author Janus Varmarken
 */
public class AisHandlerCommonSettings<OBSERVER extends AisHandlerCommonSettingsListener>
        extends HandlerSettings<OBSERVER> implements
        AisHandlerCommonSettingsListener {

    /**
     * Specifies the prefix to use for a Search and Rescue Target (SART).
     */
    private int sartPrefix = 970;

    /**
     * Comma-separated list of MMSIs that are to be treated as Search and Rescue
     * Targets (SART).
     */
    private String[] simulatedSartMmsi = {};

    /**
     * Setting specifying if strict timeout rules are to be used.
     */
    private boolean strict = true;

    /**
     * Is the AIS transponder allowed to send data?
     */
    private boolean allowSending = true;

    /**
     * Gets the setting that specifies the prefix for a Search and Rescue Target
     * (SART).
     * 
     * @return The prefix for a SART.
     */
    public String getSartPrefix() {
        try {
            this.settingLock.readLock().lock();
            return Integer.toString(this.sartPrefix);
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the prefix for a Search and Rescue
     * Target (SART).
     * 
     * @param sartPrefix
     *            The new prefix to use for a SART.
     */
    public void setSartPrefix(final int sartPrefix) {
        try {
            this.settingLock.writeLock().lock();
            if (this.sartPrefix == sartPrefix) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.sartPrefix = sartPrefix;
            for (OBSERVER obs : this.observers) {
                obs.sartPrefixChanged(sartPrefix);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies a list of MMSIs that are to be treated as
     * Search and Rescue Targets (SART).
     * 
     * @return A list of MMSIs that are to be treated as SARTs. The returned
     *         array is a copy of the original data in order to prevent
     *         reference leaks.
     */
    public String[] getSimulatedSartMmsi() {
        try {
            this.settingLock.readLock().lock();
            return Arrays.copyOfRange(this.simulatedSartMmsi, 0,
                    this.simulatedSartMmsi.length);
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies a list of MMSIs that are to be treated
     * as Search and Rescue Targets (SART).
     * 
     * @param simulatedSartMmsi
     *            A list of MMSIs that are to be treated as SARTs. This setter
     *            copies the given array in order to prevent reference leaks.
     */
    public void setSimulatedSartMmsi(final String[] simulatedSartMmsi) {
        try {
            this.settingLock.writeLock().lock();
            if (Arrays.equals(this.simulatedSartMmsi, simulatedSartMmsi)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            // First make a copy to avoid reference leak.
            String[] copy = Arrays.copyOfRange(simulatedSartMmsi, 0,
                    simulatedSartMmsi.length);
            // Update.
            this.simulatedSartMmsi = copy;
            for (OBSERVER obs : this.observers) {
                /*
                 * We need to make a new copy for each observer, as we are not
                 * in control of how each individual observer may chose to
                 * modify the array.
                 */
                String[] argCopy = Arrays.copyOfRange(simulatedSartMmsi, 0,
                        simulatedSartMmsi.length);
                obs.simulatedSartMmsiChanged(argCopy);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the setting that specifies if strict timeout rules are to be used.
     * 
     * @return {@code true} if strict timeout rules are to be used,
     *         {@code false} otherwise.
     */
    public boolean isStrict() {
        try {
            this.settingLock.readLock().lock();
            return this.strict;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies if strict timeout rules are to be
     * used.
     * 
     * @param strict
     *            {@code true} if strict timeout rules are to be used,
     *            {@code false} otherwise.
     */
    public void setStrict(final boolean strict) {
        try {
            this.settingLock.writeLock().lock();
            if (this.strict == strict) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.strict = strict;
            for (OBSERVER obs : this.observers) {
                obs.strictChanged(strict);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Is the AIS transponder allowed to send data?
     * 
     * @return {@code true} if sending is allowed, {@code false} otherwise.
     */
    public boolean isAllowSending() {
        try {
            this.settingLock.readLock().lock();
            return this.allowSending;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Sets if the AIS transponder is allowed to send data.
     * 
     * @param allowSending
     *            {@code true} if sending is allowed, {@code false} otherwise.
     */
    public void setAllowSending(final boolean allowSending) {
        try {
            this.settingLock.writeLock().lock();
            if (this.allowSending == allowSending) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.allowSending = allowSending;
            for (OBSERVER obs : this.observers) {
                obs.allowSendingChanged(allowSending);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /*
     * Begin: Listener methods that are only used if this instance observes
     * another instance of this class.
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void sartPrefixChanged(int sartPrefix) {
        // Obey to change in observed instance.
        this.setSartPrefix(sartPrefix);
    }

    @Override
    public void simulatedSartMmsiChanged(String[] simulatedSartMmsi) {
        // Obey to change in observed instance.
        this.setSimulatedSartMmsi(simulatedSartMmsi);
    }

    @Override
    public void strictChanged(boolean strict) {
        // Obey to change in observed instance.
        this.setStrict(strict);
    }

    @Override
    public void allowSendingChanged(boolean allowSending) {
        // Obey to change in observed instance.
        this.setAllowSending(allowSending);
    }

    /*
     * End: Listener methods that are only used if this instance observes
     * another instance of this class.
     */
}

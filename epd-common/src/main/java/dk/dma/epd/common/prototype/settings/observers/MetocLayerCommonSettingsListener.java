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
package dk.dma.epd.common.prototype.settings.observers;

import dk.dma.epd.common.prototype.settings.layers.MetocLayerCommonSettings;

/**
 * Interface for observing a {@link MetocLayerCommonSettings} for changes.
 * 
 * @author Janus Varmarken
 */
public interface MetocLayerCommonSettingsListener extends LayerSettingsListener {
    /**
     * Invoked when {@link MetocLayerCommonSettings#getDefaultWindWarnLimit()}
     * has changed.
     * 
     * @param windWarnLimit
     *            The updated value. Refer to
     *            {@link MetocLayerCommonSettings#getDefaultWindWarnLimit()} for
     *            its interpretation.
     */
    void defaultWindWarnLimitChanged(double windWarnLimit);

    /**
     * Invoked when
     * {@link MetocLayerCommonSettings#getDefaultCurrentWarnLimit()} has
     * changed.
     * 
     * @param currentWarnLimit
     *            The updated value. Refer to
     *            {@link MetocLayerCommonSettings#getDefaultCurrentWarnLimit()}
     *            for its interpretation.
     */
    void defaultCurrentWarnLimitChanged(double currentWarnLimit);

    /**
     * Invoked when {@link MetocLayerCommonSettings#getDefaultWaveWarnLimit()}
     * has changed.
     * 
     * @param defaultWaveWarnLimit
     *            The updated value. Refer to
     *            {@link MetocLayerCommonSettings#getDefaultWaveWarnLimit()} for
     *            its interpretation.
     */
    void defaultWaveWarnLimitChanged(double defaultWaveWarnLimit);

    /**
     * Invoked when {@link MetocLayerCommonSettings#getDefaultCurrentLow()} has
     * changed.
     * 
     * @param defaultCurrentLow
     *            The updated value. Refer to
     *            {@link MetocLayerCommonSettings#getDefaultCurrentLow()} for
     *            its interpretation.
     */
    void defaultCurrentLowChanged(double defaultCurrentLow);

    /**
     * Invoked when {@link MetocLayerCommonSettings#getDefaultCurrentMedium()}
     * has changed.
     * 
     * @param defaultCurrentMedium
     *            The updated value. Refer to
     *            {@link MetocLayerCommonSettings#getDefaultCurrentMedium()} for
     *            its interpretation.
     */
    void defaultCurrentMediumChanged(double defaultCurrentMedium);

    /**
     * Invoked when {@link MetocLayerCommonSettings#getDefaultWaveLow()} has
     * changed.
     * 
     * @param defaultWaveLow
     *            The updated value. Refer to
     *            {@link MetocLayerCommonSettings#getDefaultWaveLow()} for its
     *            interpretation.
     */
    void defaultWaveLowChanged(double defaultWaveLow);

    /**
     * Invoked when {@link MetocLayerCommonSettings#getDefaultWaveMedium()} has
     * changed.
     * 
     * @param defaultWaveMedium
     *            The updated value. Refer to
     *            {@link MetocLayerCommonSettings#getDefaultWaveMedium()} for
     *            its interpretation.
     */
    void defaultWaveMediumChanged(double defaultWaveMedium);
}

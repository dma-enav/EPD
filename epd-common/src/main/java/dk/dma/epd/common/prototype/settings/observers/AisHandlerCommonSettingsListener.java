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

import dk.dma.epd.common.prototype.settings.handlers.AisHandlerCommonSettings;

/**
 * Interface for observing an {@link AisHandlerCommonSettings} for changes.
 * 
 * @author Janus Varmarken
 * 
 */
public interface AisHandlerCommonSettingsListener extends
        HandlerSettingsListener {

    /**
     * Invoked when {@link AisHandlerCommonSettings#getSartPrefix()} has
     * changed.
     * 
     * @param sartPrefix
     *            The new SART prefix. See
     *            {@link AisHandlerCommonSettings#getSartPrefix()} for more
     *            details.
     */
    void sartPrefixChanged(int sartPrefix);

    /**
     * Invoked when {@link AisHandlerCommonSettings#getSimulatedSartMmsi()} has
     * changed.
     * 
     * @param simulatedSartMmsi
     *            The new set of simulated SARTs. See
     *            {@link AisHandlerCommonSettings#getSimulatedSartMmsi()} for
     *            more details.
     */
    void simulatedSartMmsiChanged(String[] simulatedSartMmsi);

    /**
     * Invoked when {@link AisHandlerCommonSettings#isStrict()} has changed.
     * 
     * @param strict
     *            The new strict value. See
     *            {@link AisHandlerCommonSettings#isStrict()} for more details.
     */
    void strictChanged(boolean strict);

    /**
     * Invoked when {@link AisHandlerCommonSettings#isAllowSending()} has
     * changed.
     * 
     * @param allowSending
     *            The updated value. See
     *            {@link AisHandlerCommonSettings#isAllowSending()}.
     */
    void allowSendingChanged(boolean allowSending);

}

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

import dk.dma.epd.common.prototype.settings.handlers.RouteManagerCommonSettings;

/**
 * Interface for observing a {@link RouteManagerCommonSettings} for changes.
 * 
 * @author Janus Varmarken
 * 
 */
public interface RouteManagerCommonSettingsListener extends
        HandlerSettingsListener {
    
    /**
     * Invoked when {@link RouteManagerCommonSettings#getDefaultSpeed()} has
     * changed.
     * 
     * @param defaultSpeed
     *            The new default speed. See
     *            {@link RouteManagerCommonSettings#getDefaultSpeed()} for
     *            more details, e.g. unit.
     */
    void onDefaultSpeedChanged(double defaultSpeed);

    /**
     * Invoked when {@link RouteManagerCommonSettings#getDefaultTurnRad()}
     * has changed.
     * 
     * @param defaultTurnRad
     *            The new default turn rad. See
     *            {@link RouteManagerCommonSettings#getDefaultTurnRad()} for
     *            more details, e.g. unit.
     */
    void onDefaultTurnRadChanged(double defaultTurnRad);

    /**
     * Invoked when {@link RouteManagerCommonSettings#getDefaultXtd()} has
     * changed.
     * 
     * @param defaultXtd
     *            The new default XTD. See
     *            {@link RouteManagerCommonSettings#getDefaultXtd()} for
     *            more details, e.g. unit.
     */
    void onDefaultXtdChanged(double defaultXtd);
    
}

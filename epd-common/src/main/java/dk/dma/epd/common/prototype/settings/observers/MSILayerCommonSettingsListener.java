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

import dk.dma.epd.common.prototype.settings.layers.MSILayerCommonSettings;

/**
 * Interface for observing a {@link MSILayerCommonSettings} for changes.
 * 
 * @author Janus Varmarken
 */
public interface MSILayerCommonSettingsListener extends LayerSettingsListener {
    
    /**
     * Invoked when
     * {@link MSILayerCommonSettings#getMsiTextboxesVisibleAtScale()} has
     * changed.
     * 
     * @param scale
     *            The updated value. Refer to
     *            {@link MSILayerCommonSettings#getMsiTextboxesVisibleAtScale()}
     *            for its interpretation.
     */
    void msiTextboxesVisibleAtScaleChanged(int scale);

    /**
     * Invoked when
     * {@link MSILayerCommonSettings#getMsiVisibilityFromNewWaypoint()} has
     * changed.
     * 
     * @param newValue
     *            The updated value. Refer to
     *            {@link MSILayerCommonSettings#getMsiVisibilityFromNewWaypoint()}
     *            for its interpretation.
     */
    void msiVisibilityFromNewWaypointChanged(double newValue);
}

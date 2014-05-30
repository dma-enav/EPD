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

import dk.dma.epd.common.prototype.settings.layers.VesselLayerSettings;

/**
 * Interface for observing a {@link VesselLayerSettings} for changes. This
 * interface contains callbacks for changes to settings that are relevant to
 * all layers that visualize one or more vessels.
 * 
 * @author Janus Varmarken
 */
public interface VesselLayerSettingsListener extends LayerSettingsListener {
    /**
     * Invoked when display of vessel name labels has been toggled on/off.
     * 
     * @param source
     *            The {@link VesselLayerSettings} instance that fired this
     *            event.
     * 
     * @param show
     *            {@code true} if vessel name labels display was toggled on,
     *            {@code false} if vessel name label display was toggled
     *            off.
     */
    void showVesselNameLabelsChanged(VesselLayerSettings<?> source,
            boolean show);

    /**
     * Invoked when the setting specifying the minimum length (in minutes)
     * of the movement vector has been changed.
     * 
     * @param newMinLengthMinutes
     *            The new minimum length (in minutes).
     */
    void movementVectorLengthMinChanged(int newMinLengthMinutes);

    /**
     * Invoked when the setting specifying the maximum length (in minutes)
     * of the movement vector has been changed.
     * 
     * @param newMaxLengthMinutes
     *            The new maximum length (in minutes).
     */
    void movementVectorLengthMaxChanged(int newMaxLengthMinutes);

    /**
     * Invoked when the setting specifying the scale difference between two
     * successive length values for the movement vector has been changed.
     * 
     * @param newStepSize
     *            The new difference in scale between two successive values
     *            for the length of the movement vector.
     */
    void movementVectorLengthStepSizeChanged(float newStepSize);

    /**
     * Invoked when the setting specifying the minimum speed a vessel must
     * travel with for its movement vector to be displayed has changed.
     * 
     * @param newMinSpeed
     *            The new minimum speed a vessel must travel with for its
     *            speed vector to be displayed (in nautical miles per hour).
     */
    void movementVectorHideBelowChanged(float newMinSpeed);
}

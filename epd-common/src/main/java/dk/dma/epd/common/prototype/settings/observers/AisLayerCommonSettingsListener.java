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

import dk.dma.epd.common.prototype.settings.layers.AisLayerCommonSettings;

/**
 * Interface for clients that want to listen for changes to an instance of
 * {@link AisLayerCommonSettings}.
 * 
 * @author Janus Varmarken
 */
public interface AisLayerCommonSettingsListener extends
        VesselLayerSettingsListener {

    /**
     * Invoked when the setting specifying whether to show all past tracks has
     * been changed on the observed instance.
     * 
     * @param newValue
     *            The updated value of the setting.
     */
    void showAllPastTracksChanged(boolean newValue);

    /**
     * Invoked when the setting specifying how often the layer should repaint
     * itself has been changed on the observed instance.
     * 
     * @param newValue
     *            The updated value of the setting.
     */
    void layerRedrawIntervalChanged(int newValue);

}

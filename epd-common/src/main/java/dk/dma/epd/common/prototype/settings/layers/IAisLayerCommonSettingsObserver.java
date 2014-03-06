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
 * Interface for clients that want to listen for changes to an instance of
 * {@link AisLayerCommonSettings}.
 * 
 * @author Janus Varmarken
 */
public interface IAisLayerCommonSettingsObserver extends ILayerSettingsObserver {

    /**
     * Invoked when the setting specifying whether to show all AIS name labels
     * has been changed on the observed instance.
     * 
     * @param oldValue
     *            The value of the setting prior to this change.
     * @param newValue
     *            The updated value of the setting.
     */
    void showAllAisNameLabelsChanged(boolean oldValue, boolean newValue);

    /**
     * Invoked when the setting specifying whether to show all past tracks has
     * been changed on the observed instance.
     * 
     * @param oldValue
     *            The value of the setting prior to this change.
     * @param newValue
     *            The updated value of the setting.
     */
    void showAllPastTracksChanged(boolean oldValue, boolean newValue);
}

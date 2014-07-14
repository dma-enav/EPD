/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     * @param source
     *            The settings instance that fired this event.
     * @param newValue
     *            The updated value of the setting.
     */
    void showAllPastTracksChanged(AisLayerCommonSettings<?> source,
            boolean newValue);

    /**
     * Invoked when the setting specifying how often the layer should repaint
     * itself has been changed on the observed instance.
     * 
     * @param source
     *            The settings instance that fired this event.
     * @param newValue
     *            The updated value of the setting.
     */
    void layerRedrawIntervalChanged(AisLayerCommonSettings<?> source,
            int newValue);

}

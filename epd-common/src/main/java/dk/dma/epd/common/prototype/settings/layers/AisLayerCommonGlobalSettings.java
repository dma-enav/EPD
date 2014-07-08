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
package dk.dma.epd.common.prototype.settings.layers;

import java.io.IOException;

/**
 * <p>
 * A singleton that manages the <i>global</i> settings for AIS layers (global as
 * in "applies to all AIS layers"). I.e. if one wants to change a particular
 * setting and have this apply to <b>all</b> instances of
 * {@link AisLayerCommonSettings}, one should change it on the singleton
 * instance of this class (accessed via {@link #getInstance()}). Any instance of
 * a subclass of {@link AisLayerCommonSettings} (excluding this one) should
 * register itself as an observer of this singleton to be notified of changes to
 * the global settings.
 * </p>
 * 
 * @author Janus Varmarken
 */
public final class AisLayerCommonGlobalSettings extends
        AisLayerCommonSettings<IAisLayerCommonSettingsObserver> {

    /**
     * The singleton instance that maintains the global AIS layer settings.
     */
    private static AisLayerCommonGlobalSettings singleton;

    /**
     * Constructor is private due to singleton pattern.
     */
    private AisLayerCommonGlobalSettings() {
        super();
    }
    
    /**
     * Get the singleton instance that manages the global AIS layer settings.
     * 
     * @return the singleton instance that manages the global AIS layer
     *         settings.
     */
    public static synchronized AisLayerCommonGlobalSettings getInstance() {
        // TODO need to supply arg with path to settings file for first load
        if (singleton == null) {
            singleton = new AisLayerCommonGlobalSettings();
        }
        return singleton;
    }
    
    @Override
    protected void onLoadFailure(IOException error) {
        // TODO figure out how to cope with read error.
    }
    
    @Override
    protected void onSaveFailure(IOException error) {
        // TODO possibly log save error or simply ignore it.
    }
}

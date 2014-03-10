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
}

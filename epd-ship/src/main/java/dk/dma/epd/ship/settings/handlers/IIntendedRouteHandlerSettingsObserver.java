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
package dk.dma.epd.ship.settings.handlers;

import dk.dma.epd.common.prototype.settings.handlers.IIntendedRouteHandlerCommonSettingsObserver;

/**
 * Interface for clients that want to listen for changes to an instance of
 * {@link AisLayerCommonSettings}.
 * 
 */
public interface IIntendedRouteHandlerSettingsObserver extends IIntendedRouteHandlerCommonSettingsObserver {

    
    void sendIntendedRouteChanged(boolean value);
    
    void broadcastTimeChanged(long value);
    
    void adaptiveBroadcastTimeChanged(int value);
    
    
//    /**
//     * Invoked when the setting specifying whether to show all AIS name labels
//     * has been changed on the observed instance.
//     * 
//     * @param oldValue
//     *            The value of the setting prior to this change.
//     * @param newValue
//     *            The updated value of the setting.
//     */
//    void showAllAisNameLabelsChanged(boolean oldValue, boolean newValue);
//
//    /**
//     * Invoked when the setting specifying whether to show all past tracks has
//     * been changed on the observed instance.
//     * 
//     * @param oldValue
//     *            The value of the setting prior to this change.
//     * @param newValue
//     *            The updated value of the setting.
//     */
//    void showAllPastTracksChanged(boolean oldValue, boolean newValue);
}

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

import dk.dma.epd.common.prototype.settings.ISettingsObserver;

/**
 * Base interface used to observe a {@link LayerSettings} for changes. I.e.
 * <i>this interface should only contain callbacks for changes to settings that
 * are relevant to all layer types.</i>
 * 
 * @author Janus Varmarken
 */
public interface ILayerSettingsObserver extends ISettingsObserver {
    /*
     * Specify setting-changed callbacks that are relevant to all layer types
     * here.
     */
}

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
package dk.dma.epd.common.prototype.settings;

import dk.dma.epd.common.prototype.settings.layers.ILayerSettingsObserver;
import dk.dma.epd.common.prototype.settings.layers.LayerSettings;

/**
 * Base interface for classes that want to observe an {@link ObservedSettings}
 * instance for changes. Extend this interface in parallel to sub classing
 * {@link ObservedSettings} if your sub class of {@link ObservedSettings} has
 * new notifications to fire which are specific to this new sub class of
 * {@link ObservedSettings}. Example usage: {@link LayerSettings} and
 * {@link ILayerSettingsObserver}.
 * 
 * @author Janus Varmarken
 */
public interface ISettingsObserver {

}

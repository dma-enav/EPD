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
package dk.dma.epd.common.prototype.settings.handlers;

import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.observers.HandlerSettingsListener;

/**
 * <p>
 * A base class for maintaining settings that apply to an individual handlers.
 * I.e. this class should be used as an abstract base class when writing classes
 * that store settings that are specifically targeted at a given type of
 * handler.
 * </p>
 * <p>
 * If you discover a setting that is relevant to <b>all</b> handler types, you
 * should place that setting in this class.
 * </p>
 * <p>
 * This class inherits from {@link ObservedSettings} which allows clients to
 * register for notifications of changes to any setting maintained by this
 * class.
 * </p>
 */
public abstract class HandlerSettings<OBSERVER extends HandlerSettingsListener>
        extends ObservedSettings<OBSERVER> implements HandlerSettingsListener {

    /*
     * Add settings that are relevant to all handler types here.
     */

}

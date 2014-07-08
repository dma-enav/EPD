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

import java.util.Properties;

import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;
import dk.dma.epd.common.prototype.settings.ObservedSettings;

/**
 * Maintains settings relevant to an {@link IntendedRouteHandlerCommon} or any of its subclasses. This class inherits from
 * {@link ObservedSettings} allowing clients to register for notifications of changes to any setting maintained by this class.
 */
public abstract class IntendedRouteHandlerCommonSettings<OBSERVER extends IIntendedRouteHandlerCommonSettingsObserver> extends
        HandlerSettings<OBSERVER> {

    @Override
    protected void onLoadSuccess(Properties settings) {
        // TODO init settings variables based on the provided Properties instance.
    }

    @Override
    protected Properties onSaveSettings() {
        Properties savedVars = new Properties();
        // TODO store instance fields in savedVars
        return savedVars;
    }
}

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
package dk.dma.epd.shore.ais;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.settings.AisSettings;

/**
 * Class for handling incoming AIS messages on a vessel and maintainer of AIS target tables.
 * <p>
 * This specialization of the {@link AisHandlerCommon} class contains Shore specific functionality.
 */
public class AisHandler extends AisHandlerCommon {

    /**
     * Empty constructor not used
     */
    public AisHandler(AisSettings aisSettings) {
        super(aisSettings);
    }

    /**
     * Should be implemented by specialized versions of the AisHandlerCommon class
     * 
     * @param pos
     *            the position to check
     * @return if the position is within range
     */
    @Override
    protected boolean isWithinRange(Position pos) {
        return true;
    }

}

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
package dk.dma.epd.common.prototype.layers.ais;

import dk.dma.epd.common.prototype.ais.VesselTarget;

/**
 * Abstract base class for graphics that displays a {@link VesselTarget} and its
 * associated meta data (e.g. COG vector, past track etc.)
 * 
 * @author Janus Varmarken
 */
@SuppressWarnings("serial")
public abstract class VesselGraphicComponent extends TargetGraphic {
    /**
     * Get the {@link VesselGraphic} that this {@code VesselGraphicComponent}
     * uses to display the vessel.
     * 
     * @return The {@link VesselGraphic} that this
     *         {@code VesselGraphicComponent} uses to display the vessel.
     */
    abstract VesselGraphic getVesselGraphic();
}

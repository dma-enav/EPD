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
package dk.dma.epd.shore.ais;

import java.util.Map;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.settings.handlers.AisHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.PastTrackSettings;

/**
 * Class for handling incoming AIS messages on a vessel and maintainer of AIS target tables.
 * <p>
 * This specialization of the {@link AisHandlerCommon} class contains Shore specific functionality.
 */
public class AisHandler extends AisHandlerCommon {

    /**
     * Empty constructor not used
     */
    public AisHandler(AisHandlerCommonSettings<?> aisHandlerSettings, PastTrackSettings<?> pastTrackSettings) {
        super(aisHandlerSettings, pastTrackSettings);
    }

    /**
     * Should be implemented by specialized versions of the AisHandlerCommon class
     * 
     * @param pos the position to check
     * @return if the position is within range
     */
    @Override
    protected boolean isWithinRange(Position pos) {
        return true;
    }

    public Map<Long, VesselTarget> getVesselTargets() {
        return vesselTargets;
    }
}

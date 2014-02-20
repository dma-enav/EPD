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
package dk.dma.epd.common.prototype.layers.ais;

import com.bbn.openmap.omGraphics.OMGraphicConstants;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.NavSettings;

/**
 * Class that draws a vessel as a circle.
 * 
 * @author Janus Varmarken
 */
@SuppressWarnings("serial")
public class VesselDotGraphic extends TargetGraphic {

    /**
     * Paints a circle representing the vessel.
     */
    private VesselDot vessel;

    /**
     * Paints a COG vector.
     */
    private RotationalPoly cogVec;

    @Override
    public void update(AisTarget aisTarget, AisSettings aisSettings,
            NavSettings navSettings, float mapScale) {
        if (aisTarget instanceof VesselTarget) {
            VesselTarget vesselTarget = (VesselTarget) aisTarget;
            Position newLocation = vesselTarget.getPositionData().getPos();
            if (this.vessel == null) {
                // lazy initialization
                this.vessel = new VesselDot();
                this.add(this.vessel);
            }
            // Update vessel graphic
            this.vessel.updateGraphic(vesselTarget, mapScale);
            if (this.cogVec == null) {
                // lazy initialization
                int[] headingX = { 0, 0 };
                int[] headingY = { 0, -15 };
                cogVec = new RotationalPoly(headingX, headingY, null,
                        ColorConstants.VESSEL_COLOR);
                this.add(cogVec);

            }
            // Update cog vector
            this.cogVec.setLocation(newLocation.getLatitude(),
                    newLocation.getLongitude(),
                    OMGraphicConstants.DECIMAL_DEGREES,
                    Math.toRadians(vesselTarget.getPositionData().getCog()));
        }
    }
}

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

import com.bbn.openmap.omGraphics.OMGraphicConstants;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;

/**
 * A concrete implementation of {@link VesselGraphicComponent} that displays a
 * {@link VesselTarget} as a dot (using a {@link VesselDot}) and vessel meta
 * data such as the vessel's COG vector.
 * 
 * @author Janus Varmarken et al.
 */
@SuppressWarnings("serial")
public class VesselDotGraphicComponent extends VesselGraphicComponent {

    /**
     * Paints a circle representing the vessel.
     */
    private VesselDot vessel;

    /**
     * Paints a COG vector.
     */
    private RotationalPoly cogVec;

    /**
     * Update this {@link VesselDotGraphicComponent} with new AIS data.
     */
    @Override
    public void update(AisTarget aisTarget, float mapScale) {
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

    /**
     * Get the {@link VesselDot} that this {@code VesselDotGraphicComponent} uses to
     * display the vessel.
     * 
     * @return The {@link VesselDot} that this {@code VesselDotGraphicComponent} uses to
     *         display the vessel.
     */
    @Override
    VesselDot getVesselGraphic() {
        return this.vessel;
    }
}

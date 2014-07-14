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
package dk.dma.epd.ship.layers.ownship;

import java.awt.Paint;

import com.bbn.openmap.omGraphics.OMGraphicConstants;

import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.layers.ais.SpeedVectorGraphic;
import dk.dma.epd.common.prototype.settings.layers.VesselLayerSettings;

public class OwnShipSpeedVectorGraphic extends SpeedVectorGraphic {

    private static final long serialVersionUID = 1L;
    private RotationalPoly frontShipArrow;
    private RotationalPoly backShipArrow;

    public OwnShipSpeedVectorGraphic(VesselLayerSettings<?> layerSettings, Paint lineColour) {
        super(layerSettings, lineColour);
    }

    protected void init() {
        super.init();

        int[] frontArrowX = { 5, 0, -5 };
        int[] frontArrowY = { 10, 0, 10 };
        frontShipArrow = new RotationalPoly(frontArrowX, frontArrowY, stroke, paintUsed);
        int[] backArrowX = { 5, 0, -5 };
        int[] backArrowY = { 20, 10, 20 };
        backShipArrow = new RotationalPoly(backArrowX, backArrowY, stroke, paintUsed);

        this.add(frontShipArrow);
        this.add(backShipArrow);
    }

    @Override
    public void update(VesselPositionData posData, float currentMapScale) {
        super.update(posData, currentMapScale);
        // add arrow heads

        Double cogRadian = Math.toRadians(posData.getCog());
        // Position pos = posData.getPos();
        this.frontShipArrow.setLocation(endPos.getLatitude(), endPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, cogRadian);
        this.backShipArrow.setLocation(endPos.getLatitude(), endPos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, cogRadian);
    }
}

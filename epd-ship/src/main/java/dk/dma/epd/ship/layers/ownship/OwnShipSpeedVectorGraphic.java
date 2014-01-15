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
package dk.dma.epd.ship.layers.ownship;

import java.awt.Paint;

import com.bbn.openmap.omGraphics.OMGraphicConstants;

import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.layers.ais.SpeedVectorGraphic;

public class OwnShipSpeedVectorGraphic extends SpeedVectorGraphic {

    private static final long serialVersionUID = 1L;
    private RotationalPoly frontShipArrow;
    private RotationalPoly backShipArrow;

    public OwnShipSpeedVectorGraphic(Paint lineColour) {
        super(lineColour);
        // TODO Auto-generated constructor stub
    }

    protected void init() {
        super.init();

        int[] frontArrowX = { 5, 0, -5 };
        int[] frontArrowY = { 10, 0, 10 };
        frontShipArrow = new RotationalPoly(frontArrowX, frontArrowY, stroke, null);
        int[] backArrowX = { 5, 0, -5 };
        int[] backArrowY = { 20, 10, 20 };
        backShipArrow = new RotationalPoly(backArrowX, backArrowY, stroke, null);

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

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

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;

import com.bbn.openmap.omGraphics.OMGraphicConstants;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;
import dk.dma.epd.common.prototype.layers.CircleSelectionGraphic;

/**
 * <p>
 * Graphic for displaying a vessel target. This graphic class displays the
 * vessel using a fixed size triangle (i.e. map scale and ship size is not taken
 * into account in this display).
 * </p>
 */
@SuppressWarnings("serial")
public class VesselTriangle extends VesselGraphic {

    /**
     * A triangle used to display the vessel.
     */
    private RotationalPoly vessel;

    /**
     * Color of the edges of the triangle used to display the vessel.
     */
    private Paint paint = ColorConstants.VESSEL_COLOR;

    /**
     * Stroke defining the thickness of the edges of the triangle used to
     * display the vessel.
     */
    private Stroke stroke = new BasicStroke(2.0f);

    /**
     * Manages visualization of selection of this graphic.
     */
    private CircleSelectionGraphic circleSelectionGraphic;

    /**
     * Creates a new {@code VesselTriangle} at a default location. Clients
     * should subsequently call {@link #updateGraphic(VesselTarget, float)} to
     * update the position of the new {@code VesselTriangle}.
     */
    public VesselTriangle() {
        super();
        int[] vesselX = { 0, 5, -5, 0 };
        int[] vesselY = { -10, 5, 5, -10 };
        this.vessel = new RotationalPoly(vesselX, vesselY, stroke, paint);
        this.add(vessel);
        this.circleSelectionGraphic = new CircleSelectionGraphic(this);
    }

    /**
     * Updates the color used to draw the edges of the triangle that displays
     * the vessel.
     */
    @Override
    public void setLinePaint(Paint paint) {
        vessel.setLinePaint(paint);
    }

    /**
     * <p>
     * Updates the position of this {@code VesselTriangle}.
     * </p>
     * This method calls the super implementation as part of the update process:<br/>
     * {@inheritDoc}
     */
    @Override
    public void updateGraphic(VesselTarget vesselTarget, float mapScale) {
        super.updateGraphic(vesselTarget, mapScale);
        VesselPositionData posData = vesselTarget.getPositionData();
        if (posData == null) {
            return;
        }
        Position pos = posData.getPos();
        if (pos == null) {
            return;
        }
        float trueHeading = posData.getTrueHeading();
        if (trueHeading == 511f) {
            trueHeading = vesselTarget.getPositionData().getCog();
        }
        double hdgR = Math.toRadians(trueHeading);
        vessel.setLocation(pos.getLatitude(), pos.getLongitude(),
                OMGraphicConstants.DECIMAL_DEGREES, hdgR);
        // update selection marker with new vessel position
        this.circleSelectionGraphic.updatePosition(pos);
    }

    /**
     * {@inheritDoc} Selection of this {@code VesselTriangle} is visualized using a
     * {@link CircleSelectionGraphic}.
     */
    @Override
    public void setSelectionStatus(boolean selected) {
        super.setSelectionStatus(selected);
        // TODO consider if locking is needed - add a dummy Object instance as mutex if it is
        VesselTarget vt = this.getMostRecentVesselTarget();
        VesselPositionData posData = vt != null ? vt.getPositionData()
                : null;
        Position centerPos = posData != null ? posData.getPos() : null;
        // Add or remove the selection marker from this graphic based on
        // value of selected
        this.circleSelectionGraphic.updateSelection(selected, centerPos);
    }
}

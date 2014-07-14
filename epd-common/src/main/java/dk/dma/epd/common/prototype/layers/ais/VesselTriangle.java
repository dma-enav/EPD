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
import dk.dma.epd.common.prototype.layers.predictor.VesselPortrayalData;

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
        
        /*
         *  Triangle display does not make use of vessel's width and/or length, so simply use 0 for distance values.
         */
        VesselPortrayalData portrayalData = new VesselPortrayalData(pos, trueHeading, 0f, 0f, 0f, 0f);
        this.updateGraphic(portrayalData);
    }
    
    @Override
    public void updateGraphic(VesselPortrayalData data) {
        Position pos = data.getPos();
        float trueHeading = data.getHeading();
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

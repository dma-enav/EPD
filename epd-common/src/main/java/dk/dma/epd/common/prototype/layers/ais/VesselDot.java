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

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;
import dk.dma.epd.common.prototype.layers.CircleSelectionGraphic;
import dk.dma.epd.common.prototype.layers.predictor.VesselPortrayalData;

/**
 * @author Janus Varmarken
 */
@SuppressWarnings("serial")
public class VesselDot extends VesselGraphic {

    /**
     * Diameter of the circle graphic (in pixels) that represents the Vessel's
     * location on the map.
     */
    private static final int CIRCLE_PIXEL_DIAMETER = 7;

    /**
     * Manages visualization of selection of this graphic.
     */
    private CircleSelectionGraphic circleSelectionGraphic;

    /**
     * The graphical representation of the vessel.
     */
    private OMCircle vesselMarker;

    @Override
    public void updateGraphic(VesselTarget vesselTarget, float mapScale) {
        super.updateGraphic(vesselTarget, mapScale);
        VesselPositionData posData = vesselTarget.getPositionData();
        Position pos = posData != null ? posData.getPos() : null;
        if (pos == null) {
            return;
        }
        
        /*
         *  Dot display does not make use of vessel's width and/or length, so simply use 0 for distance values.
         */
        VesselPortrayalData vpd = new VesselPortrayalData(pos, posData.getTrueHeading(), 0f, 0f, 0f, 0f);
        this.updateGraphic(vpd);
    }
    
    @Override
    public void updateGraphic(VesselPortrayalData data) {
        Position pos = data.getPos();
        if (this.vesselMarker == null) {
            // lazy initialization
            this.vesselMarker = new OMCircle(pos.getLatitude(),
                    pos.getLongitude(), CIRCLE_PIXEL_DIAMETER,
                    CIRCLE_PIXEL_DIAMETER);
            this.vesselMarker.setLinePaint(ColorConstants.VESSEL_COLOR);
            this.vesselMarker.setFillPaint(ColorConstants.VESSEL_COLOR);
            this.add(this.vesselMarker);
        }
        // update circle position
        this.vesselMarker.setCenter(new LatLonPoint.Double(pos.getLatitude(),
                pos.getLongitude()));
        if(this.circleSelectionGraphic == null) {
            this.circleSelectionGraphic = new CircleSelectionGraphic(this);
        }
        // update selection graphic
        this.circleSelectionGraphic.updatePosition(pos);
    }

    @Override
    public void setSelectionStatus(boolean selected) {
        super.setSelectionStatus(selected);
        if(this.circleSelectionGraphic == null) {
            this.circleSelectionGraphic = new CircleSelectionGraphic(this);
        }
        // TODO consider if locking is needed
        VesselTarget vt = this.getMostRecentVesselTarget();
        this.circleSelectionGraphic.updateSelection(selected, vt.getPositionData().getPos());
    }

}

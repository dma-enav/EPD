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

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;
import dk.dma.epd.common.prototype.layers.CircleSelectionGraphic;

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
        Position pos = vesselTarget.getPositionData().getPos();
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
    public void setSelection(boolean selected) {
        if(this.circleSelectionGraphic == null) {
            this.circleSelectionGraphic = new CircleSelectionGraphic(this);
        }
        VesselTarget vt = null;
        synchronized(vt = this.getMostRecentVesselTarget()) {
            this.circleSelectionGraphic.updateSelection(selected, vt.getPositionData().getPos());
        }
        
    }

}

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
import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.ISelectableGraphic;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;
import dk.dma.epd.common.prototype.layers.CircleSelectionGraphic;

/**
 * Class that draws a vessel as a circle.
 * @author Janus Varmarken
 */
public class VesselDotGraphic extends OMGraphicList implements ISelectableGraphic {

    /**
     * Diameter of the circle graphic (in pixels) that represents the Vessel's location on the map.
     */
    public static final int CIRCLE_PIXEL_DIAMETER = 7;
    
    /**
     * Default.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The graphical representation of the vessel drawn by this VesselDotGraphic.
     */
    private OMCircle vesselMarker;
    private RotationalPoly cogVec;
    
    /**
     * Manages visualization of selection of this graphic.
     */
    private CircleSelectionGraphic circleSelectionGraphic;
    
    /**
     * The most recent position data.
     */
    private Position mostRecentPos;
    
    public void updateLocation(VesselPositionData posData) {
        Position newLocation = posData.getPos();
        this.mostRecentPos = newLocation;
        if(this.vesselMarker == null) {
            // lazy initialization
            this.vesselMarker = new OMCircle(newLocation.getLatitude(), newLocation.getLongitude(), CIRCLE_PIXEL_DIAMETER, CIRCLE_PIXEL_DIAMETER);
            this.vesselMarker.setLinePaint(ColorConstants.EPD_SHIP_VESSEL_COLOR);
            this.vesselMarker.setFillPaint(ColorConstants.EPD_SHIP_VESSEL_COLOR);
            this.add(this.vesselMarker);
            
            int[] headingX = { 0, 0 };
            int[] headingY = { 0, -15 };
            cogVec = new RotationalPoly(headingX, headingY, null, ColorConstants.EPD_SHIP_VESSEL_COLOR);
            this.add(cogVec);
            
        }
        // update circle position
        this.vesselMarker.setCenter(new LatLonPoint.Double(newLocation.getLatitude(), newLocation.getLongitude()));
        // Update cog vector
        this.cogVec.setLocation(newLocation.getLatitude(), newLocation.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, Math.toRadians(posData.getCog()));
    }

    @Override
    public void setSelection(boolean selected) {
        if(this.circleSelectionGraphic == null) {
            this.circleSelectionGraphic = new CircleSelectionGraphic(this);
        }
        this.circleSelectionGraphic.updateSelection(selected, this.mostRecentPos);
    }
}

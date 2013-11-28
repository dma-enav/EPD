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

import java.awt.Color;

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;

/**
 * Class that draws a vessel as a circle.
 * @author Janus Varmarken
 */
public class VesselDotGraphic extends OMGraphicList {

    /**
     * Default.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The graphical representation of the vessel drawn by this VesselDotGraphic.
     */
    private OMCircle vesselMarker;
    
    /**
     * Diameter of the circle graphic (in pixels) that represents the Vessel's location on the map.
     */
    public static final int CIRCLE_PIXEL_DIAMETER = 10;
    
    public void updateLocation(Position newLocation) {
        if(this.vesselMarker == null) {
            // lazy initialization
            this.vesselMarker = new OMCircle(newLocation.getLatitude(), newLocation.getLongitude(), CIRCLE_PIXEL_DIAMETER, CIRCLE_PIXEL_DIAMETER);
            this.vesselMarker.setFillPaint(Color.GREEN);
            this.add(this.vesselMarker);
        }
        // update circle position
        this.vesselMarker.setCenter(new LatLonPoint.Double(newLocation.getLatitude(), newLocation.getLongitude()));
    }
}

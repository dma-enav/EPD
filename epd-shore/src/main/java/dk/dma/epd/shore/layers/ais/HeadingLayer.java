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
package dk.dma.epd.shore.layers.ais;

import com.bbn.openmap.omGraphics.OMPoly;


/**
 * Creates a heading vector on the vessels by extending OMPoly
 * @author Claes N. Ladefoged, claesnl@gmail.com
 */
public class HeadingLayer extends OMPoly {

    private static final long serialVersionUID = 1L;
    private double heading;
    private int[] origXPoints;
    private int[] origYPoints;
    private int[] xPoints;
    private int[] yPoints;
    private long MMSI;

    /**
     * Initializes the heading with a OMPoly
     * @param MMSI Key of vessel
     * @param origXPoints X-Endpoints of vector
     * @param origYPoints Y-Endpoints of vector
     */
    public HeadingLayer(long MMSI, int[] origXPoints, int[] origYPoints) {
        super();
        this.MMSI = MMSI;
        this.origXPoints = origXPoints;
        this.origYPoints = origYPoints;
        this.xPoints = new int[origXPoints.length];
        this.yPoints = new int[origYPoints.length];
        this.heading = 0;
    }

    /**
     * Updates the location and direction of the heading
     * @param latPoint Latitude of vector
     * @param lonPoint Longitude of vector
     * @param units Radians or decimal degrees.
     * @param heading Direction of vector
     */
    public void setLocation(double latPoint, double lonPoint, int units, double heading) {
        if (this.heading != heading) {
            for (int i = 0; i < origXPoints.length; i++) {
                xPoints[i] = (int) (origXPoints[i] * Math.cos(heading) - origYPoints[i] * Math.sin(heading));
                yPoints[i] = (int) (origXPoints[i] * Math.sin(heading) + origYPoints[i] * Math.cos(heading));
            }
            this.heading = heading;
        }
        super.setLocation(latPoint, lonPoint, units, xPoints, yPoints);
    }

    /**
     * Get the MMSI attached to the layer
     * @return MMSI Key of vessel
     */
    public long getMMSI() {
        return MMSI;
    }

}

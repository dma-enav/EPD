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
package dk.dma.epd.ship.layers.areanotice;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.proj.GreatCircle;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.coords.LatLonPoint;

public class ASRectangle extends OMPoly {

    private static final long serialVersionUID = 1L;

    private double[] lpoints = new double[10];

    public ASRectangle(int scaleFactor, int precision, double latitude, double longitude, int eDimension, int nDimension,
            double angle, double[] array) {
        super(array, OMGraphicConstants.DECIMAL_DEGREES, OMGraphicConstants.LINETYPE_STRAIGHT);
        this.lpoints = array;
        double alfa = angle;

        LatLonPoint p1, p2, p3, p4, p5;
        p1 = new LatLonPoint.Double(latitude, longitude);
        p2 = new LatLonPoint.Double(GreatCircle.sphericalBetween(Length.DECIMAL_DEGREE.toRadians(p1.getLatitude()),
                Length.DECIMAL_DEGREE.toRadians(p1.getLongitude()),
                Length.METER.toRadians(eDimension * (float) Math.pow(10, scaleFactor)),
                Length.DECIMAL_DEGREE.toRadians(alfa + 90)));
        p3 = new LatLonPoint.Double(GreatCircle.sphericalBetween(Length.DECIMAL_DEGREE.toRadians(p2.getLatitude()),
                Length.DECIMAL_DEGREE.toRadians(p2.getLongitude()),
                Length.METER.toRadians(nDimension * (float) Math.pow(10, scaleFactor)),
                Length.DECIMAL_DEGREE.toRadians(alfa)));
        p4 = new LatLonPoint.Double(GreatCircle.sphericalBetween(Length.DECIMAL_DEGREE.toRadians(p1.getLatitude()),
                Length.DECIMAL_DEGREE.toRadians(p1.getLongitude()),
                Length.METER.toRadians(nDimension * (float) Math.pow(10, scaleFactor)),
                Length.DECIMAL_DEGREE.toRadians(alfa)));
        p5 = new LatLonPoint.Double(p1);

        this.lpoints[0] = latitude;
        this.lpoints[1] = longitude;
        this.lpoints[2] = p2.getLatitude();
        this.lpoints[3] = p2.getLongitude();
        this.lpoints[4] = p3.getLatitude();
        this.lpoints[5] = p3.getLongitude();
        this.lpoints[6] = p4.getLatitude();
        this.lpoints[7] = p4.getLongitude();
        this.lpoints[8] = p5.getLatitude();
        this.lpoints[9] = p5.getLongitude();

        super.setLocation(this.lpoints, OMGraphicConstants.DECIMAL_DEGREES);
        // super.setIsPolygon(true);
    }

}

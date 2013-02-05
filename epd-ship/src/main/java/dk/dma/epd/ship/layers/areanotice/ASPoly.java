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

/**
 * Area Shape polyline/polygon generated with the values given in area
 * notice message the distances are calculated in meters with use of
 * scalefactor and p1D,p2D,p3D,p4D values getEndLatitude and getEndLongitude
 * methods could be used to calculate the position of the last point of the
 * polyline The specific point angles (p1A,p2A,p3A,p4A) should be given in
 * degrees float[] lpoints should be an one dimensional size of ten float
 * array
 */
public class ASPoly extends OMPoly {

    private static final long serialVersionUID = 1L;

    private double latitude;
    private double longitude;
    private double endlatitude;
    private double endlongitude;

    private double[] lpoints = new double[10];

    public ASPoly(int scaleFactor, int precision, double latitude, double longitude, int p1A, int p1D, int p2A, int p2D, int p3A,
            int p3D, int p4A, int p4D, double[] lpoints) {
        super(lpoints, OMGraphicConstants.DECIMAL_DEGREES, OMGraphicConstants.LINETYPE_STRAIGHT);
        this.lpoints = lpoints;
        this.latitude = latitude;
        this.longitude = longitude;
        LatLonPoint p1, p2, p3, p4, p5;
        
        p1 = new LatLonPoint.Double(this.latitude, this.longitude = longitude);
        p2 = new LatLonPoint.Double(GreatCircle.sphericalBetween(Length.DECIMAL_DEGREE.toRadians(p1.getLatitude()),
                Length.DECIMAL_DEGREE.toRadians(p1.getLongitude()),
                Length.METER.toRadians(p1D * (float) Math.pow(10,  scaleFactor)), Length.DECIMAL_DEGREE.toRadians(p1A)));
        p3 = new LatLonPoint.Double(GreatCircle.sphericalBetween(Length.DECIMAL_DEGREE.toRadians(p2.getLatitude()),
                Length.DECIMAL_DEGREE.toRadians(p2.getLongitude()),
                Length.METER.toRadians(p2D * (float) Math.pow(10,  scaleFactor)), Length.DECIMAL_DEGREE.toRadians(p2A)));
        p4 = new LatLonPoint.Double(GreatCircle.sphericalBetween(Length.DECIMAL_DEGREE.toRadians(p3.getLatitude()),
                Length.DECIMAL_DEGREE.toRadians(p3.getLongitude()),
                Length.METER.toRadians(p3D * (float) Math.pow(10, scaleFactor)), Length.DECIMAL_DEGREE.toRadians(p3A)));
        p5 = new LatLonPoint.Double(GreatCircle.sphericalBetween(Length.DECIMAL_DEGREE.toRadians(p4.getLatitude()),
                Length.DECIMAL_DEGREE.toRadians(p4.getLongitude()),
                Length.METER.toRadians(p4D * (float) Math.pow(10, scaleFactor)), Length.DECIMAL_DEGREE.toRadians(p4A)));

        this.lpoints[0] = this.latitude;
        this.lpoints[1] = this.longitude;
        this.lpoints[2] = p2.getLatitude();
        this.lpoints[3] = p2.getLongitude();
        this.lpoints[4] = p3.getLatitude();
        this.lpoints[5] = p3.getLongitude();
        this.lpoints[6] = p4.getLatitude();
        this.lpoints[7] = p4.getLongitude();
        this.lpoints[8] = p5.getLatitude();
        this.lpoints[9] = p5.getLongitude();
        this.endlatitude = this.lpoints[8];
        this.endlongitude = this.lpoints[9];
        super.setLocation(this.lpoints, OMGraphicConstants.DECIMAL_DEGREES);
        super.setIsPolygon(false);
    }
    
    public double getEndlatitude() {
        return endlatitude;
    }
    
    public double getEndlongitude() {
        return endlongitude;
    }

}

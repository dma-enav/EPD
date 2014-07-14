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

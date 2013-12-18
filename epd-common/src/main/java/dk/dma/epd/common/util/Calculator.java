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
package dk.dma.epd.common.util;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;

/**
 * Class for doing different common calculations
 */
public class Calculator {

    /**
     * Find range between two points given heading
     * 
     * @param pos1
     * @param pos2
     * @param heading
     */
    public static double range(Position pos1, Position pos2,
            Heading heading) {
        double meters;
        if (heading == Heading.RL) {
            meters = pos1.rhumbLineDistanceTo(pos2);
        } else {
            meters = pos1.geodesicDistanceTo(pos2);
        }
        return Converter.metersToNm(meters);
    }

    /**
     * Calculate bearing between two points given heading
     * 
     * @param pos1
     * @param pos2
     * @param heading
     * @return
     */
    public static double bearing(Position pos1, Position pos2,
            Heading heading) {
        if (heading == Heading.RL) {
            return pos1.rhumbLineBearingTo(pos2);
        } else {
            return pos1.geodesicInitialBearingTo(pos2);
        }

    }

    public static double distanceAfterTimeMph(Double mph, long seconds) {
//        System.out.println("Travelling at: " + mph + "mph for " + seconds + " Seconds");
        Double milesPrSecond = mph / 60 / 60;
        return seconds * milesPrSecond;
    }

    public static Position findPosition(Position startingLocation,
            double bearing, double distanceTravelled) {
        // Starting point
        // Bearing
        // Distance
        Ellipsoid reference = Ellipsoid.WGS84;
        double startBearing = bearing;
        double distance = distanceTravelled;
        double[] endBearing = new double[1];

        Position dest = calculateEndingGlobalCoordinates(reference,
                startingLocation, startBearing, distance, endBearing);

        return dest;
    }

    public static Position calculateEndingGlobalCoordinates(
            Ellipsoid ellipsoid, Position start, double startBearing,
            double distance, double[] endBearing) {
        double a = ellipsoid.getSemiMajorAxis();
        double b = ellipsoid.getSemiMinorAxis();
        double aSquared = a * a;
        double bSquared = b * b;
        double f = ellipsoid.getFlattening();
        double phi1 = Math.toRadians(start.getLatitude());
        double alpha1 = Math.toRadians(startBearing);
        double cosAlpha1 = Math.cos(alpha1);
        double sinAlpha1 = Math.sin(alpha1);
        double s = distance;
        double tanU1 = (1.0 - f) * Math.tan(phi1);
        double cosU1 = 1.0 / Math.sqrt(1.0 + tanU1 * tanU1);
        double sinU1 = tanU1 * cosU1;

        // eq. 1
        double sigma1 = Math.atan2(tanU1, cosAlpha1);

        // eq. 2
        double sinAlpha = cosU1 * sinAlpha1;

        double sin2Alpha = sinAlpha * sinAlpha;
        double cos2Alpha = 1 - sin2Alpha;
        double uSquared = cos2Alpha * (aSquared - bSquared) / bSquared;

        // eq. 3
        double A = 1
                + uSquared / 16384
                * (4096 + uSquared * (-768 + uSquared * (320 - 175 * uSquared)));

        // eq. 4
        double B = uSquared / 1024
                * (256 + uSquared * (-128 + uSquared * (74 - 47 * uSquared)));

        // iterate until there is a negligible change in sigma
        double deltaSigma;
        double sOverbA = s / (b * A);
        double sigma = sOverbA;
        double sinSigma;
        double prevSigma = sOverbA;
        double sigmaM2;
        double cosSigmaM2;
        double cos2SigmaM2;

        for (;;) {
            // eq. 5
            sigmaM2 = 2.0 * sigma1 + sigma;
            cosSigmaM2 = Math.cos(sigmaM2);
            cos2SigmaM2 = cosSigmaM2 * cosSigmaM2;
            sinSigma = Math.sin(sigma);
            double cosSignma = Math.cos(sigma);

            // eq. 6
            deltaSigma = B
                    * sinSigma
                    * (cosSigmaM2 + B / 4.0
                            * (cosSignma * (-1 + 2 * cos2SigmaM2) - B / 6.0
                                    * cosSigmaM2
                                    * (-3 + 4 * sinSigma * sinSigma)
                                    * (-3 + 4 * cos2SigmaM2)));

            // eq. 7
            sigma = sOverbA + deltaSigma;

            // break after converging to tolerance
            if (Math.abs(sigma - prevSigma) < 0.0000000000001) {
                break;
            }

            prevSigma = sigma;
        }

        sigmaM2 = 2.0 * sigma1 + sigma;
        cosSigmaM2 = Math.cos(sigmaM2);
        cos2SigmaM2 = cosSigmaM2 * cosSigmaM2;

        double cosSigma = Math.cos(sigma);
        sinSigma = Math.sin(sigma);

        // eq. 8
        double phi2 = Math.atan2(
                sinU1 * cosSigma + cosU1 * sinSigma * cosAlpha1,
                (1.0 - f)
                        * Math.sqrt(sin2Alpha
                                + Math.pow(sinU1 * sinSigma - cosU1 * cosSigma
                                        * cosAlpha1, 2.0)));

        // eq. 9
        // This fixes the pole crossing defect spotted by Matt Feemster. When a
        // path passes a pole and essentially crosses a line of latitude twice -
        // once in each direction - the longitude calculation got messed up.
        // Using
        // atan2 instead of atan fixes the defect. The change is in the next 3
        // lines.
        // double tanLambda = sinSigma * sinAlpha1 / (cosU1 * cosSigma - sinU1 *
        // sinSigma * cosAlpha1);
        // double lambda = Math.atan(tanLambda);
        double lambda = Math.atan2(sinSigma * sinAlpha1,
                cosU1 * cosSigma - sinU1 * sinSigma * cosAlpha1);

        // eq. 10
        double C = f / 16 * cos2Alpha * (4 + f * (4 - 3 * cos2Alpha));

        // eq. 11
        double L = lambda
                - (1 - C)
                * f
                * sinAlpha
                * (sigma + C * sinSigma
                        * (cosSigmaM2 + C * cosSigma * (-1 + 2 * cos2SigmaM2)));

        // eq. 12
        double alpha2 = Math.atan2(sinAlpha, -sinU1 * sinSigma + cosU1
                * cosSigma * cosAlpha1);

        // build result
        double latitude = Math.toDegrees(phi2);
        double longitude = start.getLongitude() + Math.toDegrees(L);

        if (endBearing != null && endBearing.length > 0) {
            endBearing[0] = Math.toDegrees(alpha2);
        }

        return Position.create(latitude, longitude);
    }
    
    
    public static double turn90Plus(double direction){
        double newDirection = direction + 90;
        
        
        if (newDirection > 360){
            newDirection = newDirection - 360;
        }
        
        if (newDirection == 360){
            newDirection = 0;
        }
        
        return newDirection;
        
    }
    
    public static double turn90Minus(double direction){
        double newDirection = direction - 90;
        
        
        if (newDirection < 0){
            newDirection = newDirection + 360;
        }
        
        if (newDirection == 360){
            newDirection = 0;
        }
        
        
        return newDirection;
    }

}

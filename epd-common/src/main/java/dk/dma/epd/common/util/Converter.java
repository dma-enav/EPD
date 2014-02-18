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

import dk.dma.epd.common.util.TypedValue.Dist;
import dk.dma.epd.common.util.TypedValue.DistType;

/**
 * Class for doing different conversions
 */
public class Converter {

    private static final double M_IN_FEET = 3.2808;

    public static double metersToNm(double meters) {
        return new Dist(DistType.METERS, meters).in(DistType.NAUTICAL_MILES).doubleValue();
    }

    public static double nmToMeters(double nm) {
        return new Dist(DistType.NAUTICAL_MILES, nm).in(DistType.METERS).doubleValue();
    }

    public static double milesToNM(double m) {
        return new Dist(DistType.MILES, m).in(DistType.NAUTICAL_MILES).doubleValue();
    }

    public static double metersToFeet(double m) {
        return m * M_IN_FEET;
    }

}

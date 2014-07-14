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
    
    public static double millisToHours(long millis) {
        return (double)millis / 3600000.0;
    }

}

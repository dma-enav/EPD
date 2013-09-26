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
package dk.dma.epd.common.prototype.model.voct;

import java.util.HashMap;

public class WeatherCorrectionFactors {

    private static HashMap<Integer, Double> PIWAndSmallBoats = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> otherObjects = new HashMap<Integer, Double>();
    
    static {
        PIWAndSmallBoats.put(0, 1.0);
        PIWAndSmallBoats.put(1, 0.5);
        PIWAndSmallBoats.put(2, 0.25);
        
        otherObjects.put(0, 1.0);
        otherObjects.put(1, 0.9);
        otherObjects.put(2, 0.9);
    }

    /**
     * @return the pIWAndSmallBoats
     */
    public static HashMap<Integer, Double> getPIWAndSmallBoats() {
        return PIWAndSmallBoats;
    }

    /**
     * @return the otherObjects
     */
    public static HashMap<Integer, Double> getOtherObjects() {
        return otherObjects;
    }
    
    
}

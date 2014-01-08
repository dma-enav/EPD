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
package dk.dma.epd.common.prototype.zoom;

/**
 * Class that provides access to values that depend on the map scale.
 * @author Janus Varmarken
 */
public final class ScaleDependentValues {
    
    private static float ONE_MINUTE_MAX_SCALE = 5000.0f;
    private static float TWO_MINUTE_MAX_SCALE = 10000.0f;
    private static float THREE_MINUTE_MAX_SCALE = 15000.0f;
    private static float FOUR_MINUTE_MAX_SCALE = 20000.0f;
    private static float FIVE_MINUTE_MAX_SCALE = 25000.0f;
    private static float SIX_MINUTE_MAX_SCALE = 30000.0f;
    private static float SEVEN_MINUTE_MAX_SCALE = 35000.0f;
    
    /**
     * Get the length of the COG & speed vector based on map scale.
     * @param mapScale The map scale to base the length of the COG & speed vector on.
     * @return The length of the COG & speed vector in minutes.
     */
    public static int getCogVectorLength(float mapScale) {
        if(mapScale <= ONE_MINUTE_MAX_SCALE) {
            return 1;
        }
        else if(mapScale <= TWO_MINUTE_MAX_SCALE) {
            return 2;
        }
        else if(mapScale <= THREE_MINUTE_MAX_SCALE) {
            return 3;
        }
        else if(mapScale <= FOUR_MINUTE_MAX_SCALE) {
            return 4;
        }
        else if(mapScale <= FIVE_MINUTE_MAX_SCALE) {
            return 5;
        }
        else if(mapScale <= SIX_MINUTE_MAX_SCALE) {
            return 6;
        }
        else if(mapScale <= SEVEN_MINUTE_MAX_SCALE) {
            return 7;
        }
        else {
            return 8;
        }
    }
    
    /**
     * Constructor is private as this class should not be instantiated.
     */
    private ScaleDependentValues() {
        
    }
}

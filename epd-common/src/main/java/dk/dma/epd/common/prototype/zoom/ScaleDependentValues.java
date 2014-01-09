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

import dk.dma.epd.common.prototype.settings.AisSettings;

/**
 * Class that provides access to values that depend on the map scale.
 * @author Janus Varmarken
 */
public final class ScaleDependentValues {
    
    /**
     * The AIS settings that hold information about the COG vector length and more.
     */
    private static AisSettings AIS_SETTINGS;
    
    /**
     * Set the AisSettings that will be used to compute scale dependent values.
     * @param aisSettings The AisSettings that holds the values to use in calculations of scale dependent values.
     */
    public static void setAIS_SETTINGS(AisSettings aisSettings) {
        AIS_SETTINGS = aisSettings;
    }
    
    /**
     * Get the length (in minutes) of the COG & speed vector based on map scale.
     * @param mapScale The map scale to base the length of the COG & speed vector on.
     * @return The length of the COG & speed vector in minutes.
     */
    public static int getCogVectorLength(float mapScale) {
        // 2nd version (in order to accommodate configurable values)
        float iMaxScale = AIS_SETTINGS.getCogVectorLengthScaleInterval();
        for(int i = AIS_SETTINGS.getCogVectorLengthMin(); i < AIS_SETTINGS.getCogVectorLengthMax(); i++) {
            if(mapScale <= iMaxScale) {
                // found the proper minute length
                System.out.println("Returning CogVectorLength = " + i + " (iMaxScale = " + iMaxScale + ")");
                return i;
            }
            else {
                iMaxScale += AIS_SETTINGS.getCogVectorLengthScaleInterval();
            }
        }
        // no matching scale, use max value
        // TODO consider adding extra check if we are to disable markers entirely if zoomed out to a given scale
        return AIS_SETTINGS.getCogVectorLengthMax();
    }

    /**
     * Constructor is private as this class should not be instantiated.
     */
    private ScaleDependentValues() {
        
    }
}

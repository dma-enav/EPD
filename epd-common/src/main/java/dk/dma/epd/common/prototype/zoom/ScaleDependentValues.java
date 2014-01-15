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

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.settings.AisSettings;

/**
 * Class that provides access to values that depend on the map scale.
 * @author Janus Varmarken
 */
public final class ScaleDependentValues {
    
    /**
     * Get the length (in minutes) of the COG & speed vector based on map scale.
     * @param mapScale The map scale to base the length of the COG & speed vector on.
     * @return The length of the COG & speed vector in minutes.
     */
    public static int getCogVectorLength(float mapScale) {
        AisSettings aisSettings = EPD.getInstance().getSettings().getAisSettings();
        float iMaxScale = aisSettings.getCogVectorLengthScaleInterval();
        for(int i = aisSettings.getCogVectorLengthMin(); i < aisSettings.getCogVectorLengthMax(); i++) {
            if(mapScale <= iMaxScale) {
                // found the proper minute length
                return i;
            }
            else {
                iMaxScale += aisSettings.getCogVectorLengthScaleInterval();
            }
        }
        // no matching scale, use max value
        // TODO consider adding extra check if we are to disable markers entirely if zoomed out to a given scale
        return aisSettings.getCogVectorLengthMax();
    }

    /**
     * Constructor is private as this class should not be instantiated.
     */
    private ScaleDependentValues() {
        
    }
}

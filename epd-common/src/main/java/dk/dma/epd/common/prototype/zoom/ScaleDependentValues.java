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
package dk.dma.epd.common.prototype.zoom;

import dk.dma.epd.common.prototype.settings.layers.VesselLayerSettings;

/**
 * Class that provides access to values that depend on the map scale.
 * @author Janus Varmarken
 */
public final class ScaleDependentValues {
    
    /**
     * Get the length (in minutes) of the COG & speed vector based on map scale.
     * @param mapScale The map scale to base the length of the COG & speed vector on.
     * @param vesselLayerSettings The appearance settings for the layer where the computed COG/Speed vector length is to be drawn.
     * @return The length of the COG & speed vector in minutes.
     */
    public static int getCogVectorLength(float mapScale, VesselLayerSettings<?> vesselLayerSettings) {
        float stepSize = vesselLayerSettings.getMovementVectorLengthStepSize();
        float iMaxScale = vesselLayerSettings.getMovementVectorLengthStepSize();
        for(int i = vesselLayerSettings.getMovementVectorLengthMin(); i < vesselLayerSettings.getMovementVectorLengthMax(); i++) {
            if(mapScale <= iMaxScale) {
                // found the proper minute length
                return i;
            }
            else {
                // No fit, check next interval.
                iMaxScale += stepSize;
            }
        }
        // no matching scale, use max value
        // TODO consider adding extra check if we are to disable markers entirely if zoomed out to a given scale
        return vesselLayerSettings.getMovementVectorLengthMax();
    }

    /**
     * Constructor is private as this class should not be instantiated.
     */
    private ScaleDependentValues() {
        
    }
}

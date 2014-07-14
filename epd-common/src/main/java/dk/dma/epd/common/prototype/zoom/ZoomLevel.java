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

/**
 * Enum used to differentiate between the different levels of zoom.
 * @author Janus Varmarken
 */
public enum ZoomLevel {
    
    VESSEL_OUTLINE(0.0f, 30000.0f) {
        
    },
    
    VESSEL_TRIANGLE(30000.0f, 250000.0f) {
        
    },
    
    VESSEL_DOT(250000.0f, Float.MAX_VALUE) {
        
    };
    
    
    ZoomLevel(float minScale, float maxScale) {
        this.minScale = minScale;
        this.maxScale = maxScale;
    }

    /**
     * Min scale for this zoom level (inclusive)
     */
    private float minScale;
    
    /**
     * Max scale for this zoom level (exclusive)
     */
    private float maxScale;
    
    /**
     * Get the min scale for this zoom level (min scale is inclusive).
     * @return The min scale for this zoom level.
     */
    public float getMinScale() {
        return this.minScale;
    }
    
    /**
     * Get the max scale for this zoom level (max scale is exclusive).
     * @return The max scale for this zoom level.
     */
    public float getMaxScale() {
        return this.maxScale;
    }

    /**
     * Get the ZoomLevel that corresponds to a given scale value.
     * @param scale The scale value.
     * @return A ZoomLevel that contains this scale value or null if this scale value is not supported.
     */
    public static ZoomLevel getFromScale(float scale) {
        if(scale < 0.0f) {
            throw new IllegalArgumentException("Negative scale value not allowed.");
        }
        for(ZoomLevel zl : values()) {
            if(scale >= zl.getMinScale() && scale < zl.getMaxScale()) {
                return zl;
            }
        }
        return null;
    }
    
    
}

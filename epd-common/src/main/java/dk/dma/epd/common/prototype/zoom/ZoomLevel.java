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
 * Enum used to differentiate between the different levels of zoom.
 * @author Janus Varmarken
 */
public enum ZoomLevel {
    
    VESSEL_OUTLINE(0.0f, 10000.0f) {
        
    },
    
    VESSEL_TRIANGLE(10000.0f, 250000.0f) {
        
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

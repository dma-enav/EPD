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
package dk.dma.epd.shore.layers.voyage;

import com.bbn.openmap.omGraphics.OMCircle;

/**
 * Graphic for a WP circle 
 */
public class VoyageWpCircle extends OMCircle {
    private static final long serialVersionUID = 1L;
    
    public VoyageWpCircle() {
        super(0, 0, 0, 0, 10, 10);
    }
    
    public VoyageWpCircle(double latitude, double longitude, int offX1, int offY1, int w, int h) {
        super(latitude, longitude, offX1, offY1, w, h);
    }

    @Override
    public float distance(double x, double y) {
        float distance = Float.POSITIVE_INFINITY;

        if (getNeedToRegenerate() || shape == null) {
            return distance;
        }
        
        float dist = (float)Math.sqrt(Math.pow(x-x1, 2) + Math.pow(y-y1, 2));
        
        double rad = getWidth() / 2.0; 
        
        if (dist <= rad) {
            dist = 0;
        } else {
            dist -= rad;
        }
        
        return dist;
    }

}

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
package dk.dma.epd.shore.layers.ais;

import com.bbn.openmap.omGraphics.OMCircle;

import dk.dma.epd.common.prototype.layers.common.WpCircle;

/**
 * Graphic for intended route WP circle
 */
public class PastTrackWpCircle extends OMCircle {
    private static final long serialVersionUID = 1L;

    private PastTrackGraphic intendedRouteGraphic;
    private int index;
    
    public PastTrackWpCircle(){
        super(0, 0, 0.1);

    }

    public PastTrackWpCircle(PastTrackGraphic intendedRouteGraphic, int index, double latitude, double longitude, int offX1, int offY1, int w, int h) {
        super(latitude, longitude, offX1, offY1, w, h);
        this.index = index;
        this.intendedRouteGraphic = intendedRouteGraphic;
    }

    public int getIndex() {
        return index;
    }

    public PastTrackGraphic getIntendedRouteGraphic() {
        return intendedRouteGraphic;
    }
    
}

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
package dk.dma.epd.common.prototype.layers.intendedroute;

import java.awt.BasicStroke;
import java.awt.Color;

import dk.dma.epd.common.prototype.layers.common.WpCircle;

/**
 * Graphic for intended route WP circle
 */
public class IntendedRouteWpCircle extends WpCircle {
    private static final long serialVersionUID = 1L;

    private IntendedRouteGraphic intendedRouteGraphic;
    private int index;

    public IntendedRouteWpCircle(IntendedRouteGraphic intendedRouteGraphic, int index, double latitude, double longitude, Color color, float scale) {
        super(latitude, longitude, 0, 0, (int)(18.0 * scale), (int)(18.0 * scale));
        setStroke(new BasicStroke(3.0f * scale));
        setLinePaint(color);
        this.index = index;
        this.intendedRouteGraphic = intendedRouteGraphic;
    }

    public int getIndex() {
        return index;
    }

    public IntendedRouteGraphic getIntendedRouteGraphic() {
        return intendedRouteGraphic;
    }
    
}

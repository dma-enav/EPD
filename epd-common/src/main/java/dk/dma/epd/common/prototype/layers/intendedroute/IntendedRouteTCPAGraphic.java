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

import com.bbn.openmap.omGraphics.OMLine;

import dk.dma.enav.model.geometry.Position;

/**
 * Graphic for intended route leg graphic
 */
public class IntendedRouteTCPAGraphic extends OMLine {
    
    private static final long serialVersionUID = 1L;
    

    public IntendedRouteTCPAGraphic(
            Position start,
            Position end, 
            float scale) {
        
        super(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude(), 0);
    
            setStroke(new BasicStroke(2.0f * scale, // Width
                    BasicStroke.CAP_SQUARE, // End cap
                    BasicStroke.JOIN_MITER, // Join style
                    10.0f * scale, // Miter limit
                    new float[] { 10.0f * scale, 8.0f * scale }, // Dash pattern
                    0.0f)); // Dash phase)
       
        
        setLinePaint(Color.YELLOW);        
    }

}

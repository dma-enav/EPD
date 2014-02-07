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

import com.bbn.openmap.omGraphics.OMArrowHead;
import com.bbn.openmap.omGraphics.OMLine;

import dk.dma.enav.model.geometry.Position;

/**
 * Graphic for intended route leg graphic
 */
public class IntendedRouteLegGraphic extends OMLine {
    
    private static final long serialVersionUID = 1L;
    
    private IntendedRouteGraphic intendedRouteGraphic;
    private OMArrowHead arrow;
    private int index;

    public IntendedRouteLegGraphic(int index, IntendedRouteGraphic intendedRouteGraphic, boolean activeWaypoint, Position start,
            Position end, Color legColor, float scale) {
        
        super(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude(), LINETYPE_RHUMB);
        this.index = index;
        this.intendedRouteGraphic = intendedRouteGraphic;
        if(activeWaypoint){
            setStroke(new BasicStroke(2.0f * scale, // Width
                    BasicStroke.CAP_SQUARE, // End cap
                    BasicStroke.JOIN_MITER, // Join style
                    10.0f * scale, // Miter limit
                    new float[] { 3.0f * scale, 10.0f * scale }, // Dash pattern
                    0.0f)); // Dash phase)
        } else {
            setStroke(new BasicStroke(2.0f * scale, // Width
                    BasicStroke.CAP_SQUARE, // End cap
                    BasicStroke.JOIN_MITER, // Join style
                    10.0f * scale, // Miter limit
                    new float[] { 10.0f * scale, 8.0f * scale }, // Dash pattern
                    0.0f)); // Dash phase)
        }
        
        arrow = new OMArrowHead(
                OMArrowHead.ARROWHEAD_DIRECTION_FORWARD, 
                55, 
                (int)(5.0 * scale), 
                (int)(15 * scale));
        
        setLinePaint(legColor);        
    }

    public IntendedRouteGraphic getIntendedRouteGraphic() {
        return intendedRouteGraphic;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setArrows(boolean arrowsVisible){
        if(!arrowsVisible) {
            this.setArrowHead(null);
        } else {
            this.setArrowHead(arrow);
        }
    }
}

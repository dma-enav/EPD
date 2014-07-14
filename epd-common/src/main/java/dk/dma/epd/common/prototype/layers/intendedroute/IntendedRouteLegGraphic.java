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
package dk.dma.epd.common.prototype.layers.intendedroute;

import java.awt.BasicStroke;
import java.awt.Color;

import com.bbn.openmap.omGraphics.OMArrowHead;
import com.bbn.openmap.omGraphics.OMLine;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;

/**
 * Graphic for intended route leg graphic
 */
public class IntendedRouteLegGraphic extends OMLine {
    
    private static final long serialVersionUID = 1L;
    
    private IntendedRouteGraphic intendedRouteGraphic;
    private OMArrowHead arrow;
    private int index;

    public IntendedRouteLegGraphic(
            int index, 
            IntendedRouteGraphic intendedRouteGraphic, 
            boolean activeWaypoint, 
            Position start,
            Position end, 
            Heading heading,
            Color legColor, 
            float scale) {
        
        super(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude(), heading.getOMLineType());
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

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
package dk.dma.epd.common.prototype.layers.ais;

import java.awt.BasicStroke;
import java.awt.Color;

import com.bbn.openmap.omGraphics.OMArrowHead;
import com.bbn.openmap.omGraphics.OMLine;

import dk.dma.enav.model.geometry.Position;

/**
 * Graphic for intended route leg graphic
 * <p>
 * 16-12-2013: Class moved to epd-common from epd-shore
 */
public class PastTrackLegGraphic extends OMLine {
    
    private static final long serialVersionUID = 1L;
    
    private PastTrackGraphic pastTrackGraphic;
    private OMArrowHead arrow = new OMArrowHead(OMArrowHead.ARROWHEAD_DIRECTION_FORWARD, 55, 5, 15);
    private int index;

    /**
     * Constructor
     */
    public PastTrackLegGraphic(int index, PastTrackGraphic pastTrackGraphic, boolean activeWaypoint, Position start,
            Position end, Color legColor) {
        
        super(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude(), LINETYPE_RHUMB);
        this.index = index;
        this.pastTrackGraphic = pastTrackGraphic;
 
        setStroke(new BasicStroke());        
        setLinePaint(legColor);        
    }

    
    /**
     * Return the parent PastTrackGraphic object
     * @return the parent PastTrackGraphic object
     */
    public PastTrackGraphic getPastTrackGraphic() {
        return pastTrackGraphic;
    }
    
    /**
     * Returns the past-track point index of this element
     * @return the past-track point index of this element
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * Sets whether to include arrows or not
     * @param arrowsVisible whether to include arrows or not
     */
    public void setArrows(boolean arrowsVisible){
        if(!arrowsVisible) {
            this.setArrowHead(null);
        } else {
            this.setArrowHead(arrow);
        }
    }
}

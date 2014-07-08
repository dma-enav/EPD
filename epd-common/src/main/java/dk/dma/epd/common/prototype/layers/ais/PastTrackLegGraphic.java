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

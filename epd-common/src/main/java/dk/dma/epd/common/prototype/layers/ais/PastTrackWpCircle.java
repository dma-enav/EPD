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

import java.util.Date;

import com.bbn.openmap.omGraphics.OMCircle;

/**
 * Graphic for intended route WP circle
 * <p>
 * 16-12-2013: Class moved to epd-common from epd-shore
 */
public class PastTrackWpCircle extends OMCircle {
    private static final long serialVersionUID = 1L;

    private PastTrackGraphic pastTrackGraphic;
    private final int index;
    private final Date date;
    
    /**
     * No-arg constructor
     */
    public PastTrackWpCircle(){
        super(0, 0, 0.1);
        index = -1;
        date = new Date(0);
    }

    /**
     * Constructor
     */
    public PastTrackWpCircle(PastTrackGraphic pastTrackGraphic, int index, double latitude, double longitude, int offX1, int offY1, int w, int h, Date date) {
        super(latitude, longitude, offX1, offY1, w, h);
        this.index = index;
        this.pastTrackGraphic = pastTrackGraphic;
        this.date = date;
    }

    /**
     * Returns the past-track point index of this element
     * @return the past-track point index of this element
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the date of this PastTrackWpCircle
     */
    public Date getDate() {
        return date;
    }

    /**
     * Return the parent PastTrackGraphic object
     * @return the parent PastTrackGraphic object
     */
    public PastTrackGraphic getPastTrackGraphic() {
        return pastTrackGraphic;
    }
}

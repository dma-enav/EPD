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

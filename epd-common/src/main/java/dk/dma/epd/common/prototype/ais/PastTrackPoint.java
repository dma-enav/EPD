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
package dk.dma.epd.common.prototype.ais;

import java.io.Serializable;
import java.util.Date;

import dk.dma.enav.model.geometry.Position;

/**
 * Models a past-track position in time.
 * <p>
 * 131213: Class moved from epd-shore to epd-common, so that it may be used in epd-ship
 */
public class PastTrackPoint implements Serializable, Comparable<PastTrackPoint>{

    private static final long serialVersionUID = -7999557080005240087L;
    
    private final Date date;
    private final Position position;
    private boolean gone;

    /**
     * Constructor
     * 
     * @param date time of position
     * @param position position
     */
    public PastTrackPoint(Date date, Position position) {
        this.date = date;
        this.position = position;
        this.gone = false;
    }

    /**
     * Returns the time of the position
     * @return the time of the position
     */
    public final Date getDate() {
        return date;
    }

    /**
     * Returns the position
     * @return the position
     */
    public final Position getPosition() {
        return position;
    }

    /**
     * Returns a string representation of this position
     * @return a string representation of this position
     */
    @Override
    public String toString() {
        return "Date " + date + " Position: " + position;
    }
    
    /**
     * Checks if the position is dead, i.e. older than ttl seconds
     * @param ttl the time-to-live in seconds
     * @return if the position is dead
     */
    public boolean isDead(int ttl) {
        int elapsed = (int) ((System.currentTimeMillis() - getDate().getTime()) / 1000);
        return elapsed > ttl;
    }
    
    /**
     * Flags that the mobile target was gone at some point of time
     * while this was the active past track point
     */
    public void flagGone() {
        this.gone = true;
    }
    
    /**
     * Returns if the mobile target was gone at some point of time
     * while this was the active past track point
     * @return if the mobile target was gone at some point of time
     */
    public boolean hasGone() {
        return gone;
    }
    
    /**
     * Compares with the given past-track point
     * @param o the point to compare with
     * @return the comparison
     */
    @Override
    public int compareTo(PastTrackPoint o) {
        return this.getDate().compareTo(o.getDate());
    }
}

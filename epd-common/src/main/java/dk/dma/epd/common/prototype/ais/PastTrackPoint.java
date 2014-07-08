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

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentSkipListSet;

import net.jcip.annotations.GuardedBy;
import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;

/**
 * Ripping off PastTrackSortedSet from dk.dma.ais.data.PastTrackSortedSet, refactor into one common codebase later
 * <p>
 * 131213: Class moved from epd-shore to epd-common, so that it may be used in epd-ship
 * 
 * @author jtj-sfs
 *
 */
public class PastTrackSortedSet extends ConcurrentSkipListSet<PastTrackPoint> implements IPastTrack, Serializable {
    
    private static final long serialVersionUID = 1L;
    @GuardedBy("this") private long lastChangeTime;
    
    /**
     * No-arg constructor
     */
    public PastTrackSortedSet() {
        super();
        updateLastChangeTime();
    }
    
    /**
     * Updates the lastChangeTime to the current time
     */
    private synchronized void updateLastChangeTime() {
        lastChangeTime = System.currentTimeMillis();
    }
    
    /**
     * Returns the last change time 
     * @return the last change time 
     */
    public synchronized long getLastChangeTime() {
        return lastChangeTime;
    }
    
    /**
     * Copy-constructor
     * @param pastTrackSortedSet the past-track set to copy
     */
    public PastTrackSortedSet(PastTrackSortedSet pastTrackSortedSet) {
        super(pastTrackSortedSet);
    }

    /**
     * Adds a past-track position to the list, if it is further away
     * from the last point than minDist
     * 
     * @param vesselPosition the new position to add
     * @param minDist the minimum distance required to add the position
     */
    @Override
    public synchronized void addPosition(Position vesselPosition, int minDist) {
        PastTrackPoint last = null;
        try {
            last = this.last();
        } catch (NoSuchElementException e) {            
        }
        
        if (last == null) {
            this.add(new PastTrackPoint(new Date(), vesselPosition));
            updateLastChangeTime();
        }
        else if (last.getPosition().distanceTo(vesselPosition, CoordinateSystem.CARTESIAN) > minDist) {
            this.add(new PastTrackPoint(new Date(), vesselPosition));
            updateLastChangeTime();
        }
    }

    /**
     * Cleans up old past-track points that have timed out according to the TTL parameter
     * @param ttl the time to live in seconds
     */
    @Override
    public synchronized void cleanup(int ttl) {
        while (size() > 0 && first().isDead(ttl)) {
            pollFirst();
            updateLastChangeTime();
        }
    }
    
    /**
     * Flags that the mobile target was gone.
     * Updates the last past-track point with this state.
     */
    public synchronized void flagGone() {
        try {
            this.last().flagGone();
        } catch (NoSuchElementException e) {            
        }
    }

    /**
     * Collection for use with outside iterators
     */
    @Override
    public Collection<PastTrackPoint> getPoints() {
        return this;
    }

    /**
     * Returns the points newer than the given {@code time}
     * @param time the time
     */
    public synchronized Collection<PastTrackPoint> getPointsNewerThan(Date time) {
        Collection<PastTrackPoint> result = new ArrayList<PastTrackPoint>(this.size() / 2);
        for (PastTrackPoint point : this) {
            if (point.getDate().after(time)) {
                result.add(point);
            }
        }
        return result;
    }
}

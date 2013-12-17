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
     * Collection for use with outside iterators
     */
    @Override
    public Collection<PastTrackPoint> getPoints() {
        return this;
    }

}

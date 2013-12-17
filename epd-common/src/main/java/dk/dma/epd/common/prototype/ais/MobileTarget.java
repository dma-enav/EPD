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

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;


/**
 * Abstract base class for mobile AIS targets, i.e. {@link VesselTarget} and 
 * {@link SarTarget}.
 * <p>
 * Keeps track of position, statistics and past-track data
 */
@ThreadSafe
public abstract class MobileTarget extends AisTarget {

    private static final long serialVersionUID = 3281118946528623593L;
    
    @GuardedBy("this") VesselPositionData positionData;
    @GuardedBy("this") VesselStaticData staticData;
    // NB: We do not want to persist past-track data
    @GuardedBy("this") transient PastTrackSortedSet pastTrackData = new PastTrackSortedSet();
   
    /**
     * Empty constructor
     */
    public MobileTarget() {
        super();
    }
    
    /**
     * Copy constructor
     * @param mobileTarget target to copy
     */
    public MobileTarget(MobileTarget mobileTarget) {
        super(mobileTarget);
        
        if (mobileTarget.positionData != null) {
            this.positionData = new VesselPositionData(mobileTarget.positionData);
        }
        if (mobileTarget.staticData != null) {
            this.staticData = new VesselStaticData(mobileTarget.staticData);
        }
        if (mobileTarget.pastTrackData != null) {
            this.pastTrackData = new PastTrackSortedSet(mobileTarget.pastTrackData);
        }
    }
    
    public synchronized VesselPositionData getPositionData() {
        return positionData;
    }

    public synchronized void setPositionData(VesselPositionData positionData) {
        this.positionData = positionData;
    }

    public synchronized VesselStaticData getStaticData() {
        return staticData;
    }

    public synchronized void setStaticData(VesselStaticData staticData) {
        this.staticData = staticData;
    }

    public synchronized PastTrackSortedSet getPastTrackData() {
        if (pastTrackData == null) {
            this.pastTrackData = new PastTrackSortedSet();
        }
        return pastTrackData;
    }

    public synchronized void setPastTrackData(PastTrackSortedSet pastTrackData) {
        this.pastTrackData = pastTrackData;
    }
    
    /**
     * Override implementation to flag the past-track point gone
     * @status the new status of the AisTarget
     */
    @Override
    public synchronized void setStatus(Status status) {
        super.setStatus(status);
        
        if (status == AisTarget.Status.GONE) {
            // Flag the last past-track point gone
            getPastTrackData().flagGone();
        }
    }
    
}

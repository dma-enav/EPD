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

import java.util.Date;


/**
 * Class representing an AIS SART
 */
public class SarTarget extends AisTarget {
    
    private static final long serialVersionUID = 1L;
    
    private static final long OLD_TTL = 720; // 12 min
    private static final long GONE_TTL = 1800; // 30 min
        
    private VesselPositionData positionData;
    private VesselStaticData staticData;
    private boolean old;
    private Date firstReceived;

    /**
     * Copy constructor
     * @param sarTarget
     */
    public SarTarget(SarTarget sarTarget) {
        super(sarTarget);
        if (sarTarget.positionData != null) {
            this.positionData = new VesselPositionData(sarTarget.positionData);
        }
        if (sarTarget.staticData != null) {
            this.staticData = new VesselStaticData(sarTarget.staticData);
        }
    }
    
    /**
     * Empty constructor
     */
    public SarTarget() {
        super();
    }

    /**
     * Determines if the target should be considered gone
     * @return if the target has gone
     */
    @Override
    public boolean hasGone(Date now, boolean strict) {        
        long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;        
        // Determine if gone
        return elapsed > GONE_TTL;
    }
    
    /**
     * Determine if the target has changed state to old
     * @param now
     * @return changed to old
     */
    public boolean hasGoneOld(Date now) {
        long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;
        boolean newOld = elapsed > OLD_TTL;
        if (newOld != old) {
            old = newOld;
            return true;
        }        
        return false;
    }
    
    public VesselPositionData getPositionData() {
        return positionData;
    }

    public void setPositionData(VesselPositionData positionData) {
        this.positionData = positionData;
    }

    public VesselStaticData getStaticData() {
        return staticData;
    }

    public void setStaticData(VesselStaticData staticData) {
        this.staticData = staticData;
    }

    public boolean isOld() {
        return old;
    }
    
    public void setOld(boolean old) {
        this.old = old;
    }
    
    public Date getFirstReceived() {
        return firstReceived;
    }
    
    public void setFirstReceived(Date firstReceived) {
        this.firstReceived = firstReceived;
    }

}

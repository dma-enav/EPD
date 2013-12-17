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

import net.jcip.annotations.ThreadSafe;

/**
 * Class representing an AIS SART
 */
@ThreadSafe
public class SarTarget extends MobileTarget {
    
    private static final long serialVersionUID = -7367277293793654525L;
    
    private static final long OLD_TTL = 720; // 12 min
    private static final long GONE_TTL = 1800; // 30 min
        
    private boolean old;
    private Date firstReceived;

    /**
     * Copy constructor
     * @param sarTarget
     */
    public SarTarget(SarTarget sarTarget) {
        super(sarTarget);
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
    public synchronized boolean hasGone(Date now, boolean strict) {        
        long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;        
        // Determine if gone
        return elapsed > GONE_TTL;
    }
    
    /**
     * Determine if the target has changed state to old
     * @param now
     * @return changed to old
     */
    public synchronized boolean hasGoneOld(Date now) {
        long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;
        boolean newOld = elapsed > OLD_TTL;
        if (newOld != old) {
            old = newOld;
            return true;
        }        
        return false;
    }
    
    public synchronized boolean isOld() {
        return old;
    }
    
    public synchronized void setOld(boolean old) {
        this.old = old;
    }
    
    public synchronized Date getFirstReceived() {
        return firstReceived;
    }
    
    public synchronized void setFirstReceived(Date firstReceived) {
        this.firstReceived = firstReceived;
    }

}

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

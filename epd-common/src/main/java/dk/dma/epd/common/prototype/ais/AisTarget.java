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

import net.jcip.annotations.ThreadSafe;

/**
 * Abstract base class for AIS targets
 */
@ThreadSafe
public abstract class AisTarget implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Status of target can either be OK og GONE
     */
    public enum Status {OK, GONE};
    
    protected Date lastReceived;
    protected long mmsi;
    protected Status status;
    
    public AisTarget() {
        status = Status.OK;
    }

    /**
     * Copy constructor
     * @param aisTarget
     */
    public AisTarget(AisTarget aisTarget) {
        lastReceived = aisTarget.lastReceived;
        mmsi = aisTarget.mmsi;
        status = aisTarget.status;
    }
    
    /**
     * Returns true if target has gone
     * @param now
     * @param strict
     * @return
     */
    public abstract boolean hasGone(Date now, boolean strict);
    
    /**
     * Determine if target is dead given ttl (time-to-live)
     * @param ttl
     * @param now
     * @return
     */
    public synchronized boolean isDeadTarget(long ttl, Date now) {
        return now.getTime() - lastReceived.getTime() > ttl;        
    }
    
    public synchronized void setLastReceived(Date lastReceived) {
        this.lastReceived = lastReceived;
    }
    
    public synchronized Date getLastReceived() {
        return lastReceived;
    }
    
    public synchronized long getMmsi() {
        return mmsi;
    }
    
    public synchronized void setMmsi(long mmsi) {
        this.mmsi = mmsi;
    }
    
    public synchronized Status getStatus() {
        return status;
    }
    
    public synchronized void setStatus(Status status) {
        this.status = status;
    }
    
    public synchronized boolean isGone() {
        return status == Status.GONE;
    }
    
    @Override
    public synchronized int hashCode() {
        return (int)mmsi;
    }

    @Override
    public synchronized String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AisTarget [lastReceived=");
        builder.append(lastReceived);
        builder.append(", mmsi=");
        builder.append(mmsi);
        builder.append(", status=");
        builder.append(status);
        builder.append("]");
        return builder.toString();
    }
    
}

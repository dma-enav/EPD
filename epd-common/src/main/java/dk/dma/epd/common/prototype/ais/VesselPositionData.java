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

import net.jcip.annotations.ThreadSafe;
import dk.dma.ais.message.AisMessage18;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.voyage.NavigationalStatus;

/**
 * Class representing position data for an AIS vessel target
 */
@ThreadSafe
public class VesselPositionData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Position pos;
    private NavigationalStatus navEnumStatus = NavigationalStatus.UNDEFINED;
    private int navStatus = -1;
    private float rot;
    private float sog;
    private int posAcc;
    private float cog;
    private float trueHeading;
    
    /**
     * No-argument constructor
     */
    public VesselPositionData() {
    }
    
    /**
     * Copy constructor
     * @param vesselPositionData
     */
    public VesselPositionData(VesselPositionData vesselPositionData) {
        if (vesselPositionData.pos != null) {
            pos = vesselPositionData.pos;
        }
        navStatus = vesselPositionData.navStatus;
        rot = vesselPositionData.rot;
        sog = vesselPositionData.sog;
        posAcc = vesselPositionData.posAcc;
        cog = vesselPositionData.cog;
        trueHeading = vesselPositionData.trueHeading;
    }

    
    /**
     * Constructor given an AIS position message #1, #2 or #3
     * @param aisPositionMessage
     */
    public VesselPositionData(AisPositionMessage aisPositionMessage) {
        pos = aisPositionMessage.getPos().getGeoLocation();
        navStatus = aisPositionMessage.getNavStatus();
        navEnumStatus = NavigationalStatus.fromAIS(aisPositionMessage.getNavStatus());
        Float rawRot = aisPositionMessage.getSensorRot();
        rot = rawRot == null ? 0.0f : rawRot;
        sog = aisPositionMessage.getSog() / (float)10.0;
        posAcc = aisPositionMessage.getPosAcc();
        cog = aisPositionMessage.getCog() / (float)10.0;
        trueHeading = aisPositionMessage.getTrueHeading();
        
        validate();
    }
    
    /**
     * Constructor given AIS message #18
     * @param aisPositionMessage18
     */
    public VesselPositionData(AisMessage18 aisPositionMessage18) {
        pos = aisPositionMessage18.getPos().getGeoLocation();
        cog = aisPositionMessage18.getCog() / (float)10.0;
        posAcc = aisPositionMessage18.getPosAcc();
        sog = aisPositionMessage18.getSog() / (float)10.0;
        trueHeading = aisPositionMessage18.getTrueHeading();
        
        validate();
    }
    
    /**
     * Validate the current position data
     */    
    private synchronized void validate() {
        // Handle unavailable speed and cog
        if (sog > 100) {
            sog = 0;
        }
        if (cog >= 360) {
            cog = 0;
        }
        
    }
        
    public synchronized Position getPos() {
        return pos;
    }

    public synchronized void setPos(Position pos) {
        this.pos = pos;
    }
    
    public synchronized boolean hasPos() {
        return pos != null;
    }

    public synchronized int getNavStatus() {
        return navStatus;
    }

    public synchronized void setNavStatus(int navStatus) {
        this.navStatus = navStatus;
    }
    
    public synchronized NavigationalStatus getEnumNavStatus() {
        return navEnumStatus;
    }

    public synchronized void setEnumNavStatus(NavigationalStatus navEnumStatus) {
        this.navEnumStatus = navEnumStatus;
    }    


    public synchronized float getRot() {
        return rot;
    }

    public synchronized void setRot(float rot) {
        this.rot = rot;
    }

    public synchronized float getSog() {
        return sog;
    }

    public synchronized void setSog(float sog) {
        this.sog = sog;
    }

    public synchronized int getPosAcc() {
        return posAcc;
    }

    public synchronized void setPosAcc(int posAcc) {
        this.posAcc = posAcc;
    }

    public synchronized float getCog() {
        return cog;
    }

    public synchronized void setCog(float cog) {
        this.cog = cog;
    }

    /**
     * Returns the heading in degrees. Heading 511 means the target is not available.
     * @return Heading in degrees
     */
    public synchronized float getTrueHeading() {
        return trueHeading;
    }

    public synchronized void setTrueHeading(float trueHeading) {
        this.trueHeading = trueHeading;
    }

    @Override
    public synchronized String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VesselPositionData [cog=");
        builder.append(cog);
        builder.append(", navStatus=");
        builder.append(navStatus);
        builder.append(", pos=");
        builder.append(pos);
        builder.append(", posAcc=");
        builder.append(posAcc);
        builder.append(", rot=");
        builder.append(rot);
        builder.append(", sog=");
        builder.append(sog);
        builder.append(", trueHeading=");
        builder.append(trueHeading);
        builder.append("]");
        return builder.toString();
    }
    
}

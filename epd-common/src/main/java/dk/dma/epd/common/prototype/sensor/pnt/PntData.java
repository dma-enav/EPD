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
package dk.dma.epd.common.prototype.sensor.pnt;

import java.io.Serializable;
import java.util.Date;

import net.jcip.annotations.NotThreadSafe;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.sensor.nmea.PntSource;

/**
 * Class representing PNT data 
 */
@NotThreadSafe
public class PntData implements Serializable {
        
    private static final long serialVersionUID = 1L;

    private PntSource pntSource;
    private Date lastUpdated = new Date(0);
    private Position position;
    private Double cog;
    private Double sog;
    private Long time;
    private boolean badPosition = true;
    
    public PntData() {
        
    }
    
    /**
     * Copy constructor
     */
    public PntData(PntData pntData) {
        this.pntSource = pntData.pntSource;
        this.lastUpdated = new Date(pntData.lastUpdated.getTime());
        this.position = pntData.position;
        if (pntData.cog != null) {
            this.cog = new Double(pntData.cog);
        }
        if (pntData.sog != null) {
            this.sog = new Double(pntData.sog);
        }
        if (pntData.time != null) {
            this.time = new Long(pntData.time);
        }
        this.badPosition = pntData.badPosition;        
    }

    public PntSource getPntSource() {
        return pntSource;
    }
    
    public void setPntSource(PntSource pntSource) {
        this.pntSource = pntSource;
    }
    
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Double getCog() {
        return cog;
    }

    public void setCog(Double cog) {
        this.cog = cog;
    }

    public Double getSog() {
        return sog;
    }

    public void setSog(Double sog) {
        this.sog = sog;
    }
    
    public Long getTime() {
        return time;
    }
    
    public void setTime(Long time) {
        this.time = time;
    }
    
    /**
     * Is the current position valid
     * @return
     */
    public boolean isBadPosition() {
        return badPosition;
    }
    
    public void setBadPosition(boolean badPosition) {
        this.badPosition = badPosition;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PntData [badPosition=");
        builder.append(badPosition);
        builder.append(", cog=");
        builder.append(cog);
        builder.append(", lastUpdated=");
        builder.append(lastUpdated);
        builder.append(", position=");
        builder.append(position);
        builder.append(", sog=");
        builder.append(sog);
        builder.append(", time=");
        builder.append(time);
        builder.append(", pntSource=");
        builder.append(pntSource);
        builder.append("]");
        return builder.toString();
    }
    
}

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
package dk.dma.epd.common.prototype.sensor.pnt;

import java.io.Serializable;
import java.util.Date;

import net.jcip.annotations.NotThreadSafe;
import dk.dma.enav.model.geometry.Position;

/**
 * Class representing PNT data 
 */
@NotThreadSafe
public class PntData implements Serializable {
        
    private static final long serialVersionUID = 1L;
    
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
        builder.append("]");
        return builder.toString();
    }
    
}

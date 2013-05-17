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
package dk.dma.epd.common.prototype.sensor.gps;

import java.io.Serializable;
import java.util.Date;

import net.jcip.annotations.NotThreadSafe;

import dk.dma.enav.model.geometry.Position;

/**
 * Class representing GPS data position, speed over ground and course over ground. 
 */
@NotThreadSafe
public class GpsData implements Serializable {
        
    private static final long serialVersionUID = 1L;
    
    private Date lastUpdated = new Date(0);
    private Position position;
    private Double cog;
    private Double sog;
    private boolean badPosition = true;
    
    public GpsData() {
        
    }
    
    /**
     * Copy constructor
     */
    public GpsData(GpsData gpsData) {
        this.lastUpdated = new Date(gpsData.lastUpdated.getTime());
        this.position = gpsData.position;
        if (gpsData.cog != null) {
            this.cog = new Double(gpsData.cog);
        }
        if (gpsData.sog != null) {
            this.sog = new Double(gpsData.sog);
        }
        this.badPosition = gpsData.badPosition;        
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
        builder.append("GpsData [badPosition=");
        builder.append(badPosition);
        builder.append(", cog=");
        builder.append(cog);
        builder.append(", lastUpdated=");
        builder.append(lastUpdated);
        builder.append(", position=");
        builder.append(position);
        builder.append(", sog=");
        builder.append(sog);
        builder.append("]");
        return builder.toString();
    }
    
}

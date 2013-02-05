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
package dk.dma.epd.common.prototype.sensor.nmea;

import dk.dma.enav.model.geometry.Position;

/**
 * Class representing a GPS message
 */
public class GpsMessage {
    
    private Position pos;
    private Double sog;
    private Double cog;
    
    public GpsMessage() {        
    }
    
    public Position getPos() {
        return pos;
    }
    
    public void validateFields() {
        if (cog != null && cog >= 360) {
            cog = null;
        }
        if (sog != null && sog >= 102.2) {
            sog = null;
        }
    }
    
    public void setPos(Position pos) {
        this.pos = pos;
    }
    
    public boolean isValidPosition() {
        return pos != null && pos.getLatitude() <= 90 && pos.getLongitude() <= 180;
    }
    
    public Double getSog() {
        return sog;
    }

    public void setSog(Double sog) {
        this.sog = sog;
    }

    public Double getCog() {
        return cog;
    }

    public void setCog(Double cog) {
        this.cog = cog;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GpsMessage [cog=");
        builder.append(cog);
        builder.append(", pos=");
        builder.append(pos);
        builder.append(", sog=");
        builder.append(sog);
        builder.append(", time=");
        builder.append("]");
        return builder.toString();
    }
    
    

}

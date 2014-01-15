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

import net.jcip.annotations.Immutable;
import dk.dma.enav.model.geometry.Position;

/**
 * Class representing PNT data
 */
@Immutable
public class PntMessage {

    private final PntSource pntSource;
    private final Position pos;
    private final Double sog;
    private final Double cog;
    private final Long time;
    private MessageType messageType = MessageType.PNT;

    /**
     * Constructor
     * 
     * @param pntSource the PNT source
     * @param pos the GPS position
     * @param sog the speed over ground value
     * @param cog the course over ground
     * @param time the time
     */
    public PntMessage(PntSource pntSource, Position pos, Double sog, Double cog, Long time) {
        this.pntSource = pntSource;
        this.pos = pos;
        this.sog = sog;
        this.cog = cog;
        this.time = time;
    }
    
    /**
     * Constructor
     * <p>
     * This constructor should be used for time-onlye PNT messages.
     * 
     * @param pntSource the PNT source
     * @param cog the course over ground
     */
    public PntMessage(PntSource pntSource, Long time) {
        this(pntSource, null, null, null, time);
        messageType = MessageType.TIME;
    }
    
    public Position getPos() {
        return pos;
    }

    private boolean isValidSog() {
        return cog != null && cog < 360;
    }

    private boolean isValidCog() {
        return sog != null && sog < 102.2;
    }

    public boolean isValidPosition() {
        return pos != null && pos.getLatitude() <= 90 && pos.getLongitude() <= 180;
    }

    public Double getSog() {
        return isValidSog() ? sog : null;
    }

    public Double getCog() {
        return isValidCog() ? cog : null;
    }
    
    public Long getTime() {
        return time;
    }
    
    public PntSource getPntSource() {
        return pntSource;
    }
    
    public MessageType getMessageType() {
        return messageType;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PntMessage [cog=");
        builder.append(cog);
        builder.append(", pos=");
        builder.append(pos);
        builder.append(", sog=");
        builder.append(sog);
        builder.append(", time=");
        builder.append(time);
        builder.append(", source=");
        builder.append(pntSource);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Defines the message type
     */
    public enum MessageType {
        PNT,    // Message contains position, navigation and time information
        TIME;   // Message contains only time information
    }
}

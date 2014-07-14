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

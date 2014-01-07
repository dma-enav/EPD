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

import dk.dma.epd.common.util.EnumUtils;

/**
 * Enumerates the possible PNT sources.
 * <p>
 * The keys associated with the enumeration values correspond
 * to the source mapping of the NMEA $PRPNT sentence
 */
public enum PntSource implements EnumUtils.KeyedEnum<Integer> {
    NONE(0),
    GPS(1),
    ELORAN(2),
    RADAR(3);
    
    private Integer key;
    
    /**
     * Constructor
     * @param key
     */
    private PntSource(Integer key) { 
        this.key = key; 
    }
    
    /**
     * Returns the key associated with the enum value
     * @return the key associated with the enum value
     */
    @Override 
    public Integer getKey() { 
        return key; 
    }
}


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
    RADAR(3),
    AIS(-1);  // NB: AIS not defined by $PRPNT
    
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


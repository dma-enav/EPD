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
package dk.dma.epd.common.prototype.voct;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Different events for VOCT events
 */
public enum VOCTUpdateEvent {
    NEW_SAR, SAR_CANCEL, SAR_READY, SAR_DISPLAY, EFFORT_ALLOCATION_READY, EFFORT_ALLOCATION_DISPLAY, SEARCH_PATTERN_GENERATED, SAR_RECEIVED_CLOUD
    , EFFORT_ALLOCATION_SERIALIZED;
    
    public boolean is(VOCTUpdateEvent... events) {
        return EnumSet.copyOf(Arrays.asList(events)).contains(this);
    }
};

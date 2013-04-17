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
package dk.dma.epd.shore.voyage;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Different events for routes
 */
public enum VoyageUpdateEvent {
    VOYAGE_CHANGED, VOYAGE_ADDED, VOYAGE_REMOVED, VOYAGE_VISIBILITY_CHANGED, VOYAGE_WAYPOINT_DELETED, VOYAGE_WAYPOINT_APPENDED, VOYAGE_WAYPOINT_MOVED;

    public boolean is(VoyageUpdateEvent... events) {
        return EnumSet.copyOf(Arrays.asList(events)).contains(this);
    }
};

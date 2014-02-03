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
package dk.dma.epd.common.prototype.model.route;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Different events for routes
 */
public enum RoutesUpdateEvent {
    ROUTE_ACTIVATED, 
    ROUTE_DEACTIVATED, 
    ACTIVE_ROUTE_UPDATE, 
    ROUTE_CHANGED, 
    ROUTE_ADDED, 
    ROUTE_REMOVED, 
    ROUTE_VISIBILITY_CHANGED, 
    ACTIVE_ROUTE_FINISHED, 
    METOC_SETTINGS_CHANGED, 
    SUGGESTED_ROUTES_CHANGED, 
    ROUTE_METOC_CHANGED, 
    ROUTE_WAYPOINT_DELETED, 
    ROUTE_WAYPOINT_APPENDED, 
    ROUTE_WAYPOINT_MOVED, 
    ROUTE_MSI_UPDATE;
    
    public boolean is(RoutesUpdateEvent... events) {
        return EnumSet.copyOf(Arrays.asList(events)).contains(this);
    }
};

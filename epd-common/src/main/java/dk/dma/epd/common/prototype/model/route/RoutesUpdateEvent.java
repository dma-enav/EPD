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
    ROUTE_METOC_CHANGED, 
    ROUTE_WAYPOINT_DELETED, 
    ROUTE_WAYPOINT_APPENDED, 
    ROUTE_WAYPOINT_MOVED, 
    ROUTE_MSI_UPDATE;
    
    public boolean is(RoutesUpdateEvent... events) {
        return EnumSet.copyOf(Arrays.asList(events)).contains(this);
    }
};

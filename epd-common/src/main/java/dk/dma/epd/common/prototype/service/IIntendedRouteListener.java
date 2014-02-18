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
package dk.dma.epd.common.prototype.service;

import dk.dma.epd.common.prototype.model.route.IntendedRoute;


/**
 * Interface to implement for classes wanting to receive Intended Route updates
 */
public interface IIntendedRouteListener {

//    /**
//     * Called when an intended route has been added
//     * @param intendedRoute the intended route
//     */
//    void intendedRouteAdded(IntendedRouteMessage intendedRoute);
//    
//    /**
//     * Called when an intended route has been updated
//     * @param intendedRoute the intended route
//     */
//    void intendedRouteUpdated(IntendedRouteMessage intendedRoute);
//    
//    /**
//     * Called when an intended route has been removed
//     * @param intendedRoute the intended route
//     */
//    void intendedRouteRemoved(IntendedRouteMessage intendedRoute);
    
    
    
    
    
    /**
     * Called when an event regarding intended routes has occured
     * such as added, removed or updated
     * @param intendedRoute the intended route
     */
    void intendedRouteEvent(IntendedRoute intendedRoute);
}

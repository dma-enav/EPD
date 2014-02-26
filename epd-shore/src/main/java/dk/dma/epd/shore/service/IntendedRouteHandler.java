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
package dk.dma.epd.shore.service;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import dk.dma.epd.common.prototype.model.intendedroute.FilteredIntendedRoute;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;

/**
 * Shore specific intended route service implementation.
 */
public class IntendedRouteHandler extends IntendedRouteHandlerCommon {

    /**
     * Constructor
     */
    public IntendedRouteHandler() {
        super();
    }

    /****************************************/
    /** Intended route filtering           **/
    /****************************************/

    /**
     * Update all filters
     */
    @Override
    protected void updateFilter() {

        // Recalculate everything
        // Compare all routes to current active route
        
        ConcurrentHashMap<Long, FilteredIntendedRoute> filteredIntendedRoutes = new ConcurrentHashMap<>();

        // Compare all intended routes against all other intended routes

        Iterator<Entry<Long, IntendedRoute>> outerIterator = intendedRoutes.entrySet().iterator();
        while (outerIterator.hasNext()) {
            Entry<Long, IntendedRoute> intendedRoute = outerIterator.next();

            IntendedRoute route1 = intendedRoute.getValue();

            Iterator<Entry<Long, IntendedRoute>> innerIterator = intendedRoutes.entrySet().iterator();
            while (innerIterator.hasNext()) {
                Entry<Long, IntendedRoute> intendedRoute2 = innerIterator.next();

                IntendedRoute route2 = intendedRoute2.getValue();

                if (route1.getMmsi() != route2.getMmsi()) {
                    FilteredIntendedRoute filter = compareRoutes(route1, route2);

                    // No warnings, ignore it
                    if (filter.include()) {

                        // Add the intended route to the filter
                        filter.setIntendedRoute(route2);
                        // Add the filtered route to the list
                        filteredIntendedRoutes.put(route2.getMmsi(), filter);

                    }
                }

            }
        }
        
        // Check if we need to raise any alerts
        checkGenerateNotifications(this.filteredIntendedRoutes, filteredIntendedRoutes);
        
        // Override the old set of filtered intended route
        this.filteredIntendedRoutes = filteredIntendedRoutes;
    }

    /**
     * Update filter with new intended route
     * 
     * @param route
     */
    @Override
    protected void applyFilter(IntendedRoute route) {
        updateFilter();
    }

}

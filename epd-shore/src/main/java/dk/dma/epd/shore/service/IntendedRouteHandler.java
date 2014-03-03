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

import dk.dma.epd.common.prototype.model.intendedroute.FilteredIntendedRoute;
import dk.dma.epd.common.prototype.model.intendedroute.FilteredIntendedRoutes;
import dk.dma.epd.common.prototype.model.intendedroute.IntendedRouteFilterMessage;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Converter;

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
    /** Intended route filtering **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    protected String formatNotificationDescription(FilteredIntendedRoute filteredIntendedRoute) {
        IntendedRouteFilterMessage msg = filteredIntendedRoute.getMinimumDistanceMessage();
        return String.format("The routes of MMSI %d and %d come within %s nautical miles of each other at %s.", 
                filteredIntendedRoute.getMmsi1(),
                filteredIntendedRoute.getMmsi2(),
                Formatter.formatDistNM(Converter.metersToNm(msg.getDistance())),
                Formatter.formatYodaTime(msg.getTime1()));
    }
    
    /**
     * Update all filters
     */
    @Override
    protected void updateFilter() {

        // Recalculate everything
        // Compare all routes to current active route

        FilteredIntendedRoutes filteredIntendedRoutes = new FilteredIntendedRoutes();

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
                    FilteredIntendedRoute filter = findTCPA(route1, route2);

                    // No warnings, ignore it
                    if (filter.include()) {
                        // Add the filtered route to the list
                        filteredIntendedRoutes.add(filter);
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

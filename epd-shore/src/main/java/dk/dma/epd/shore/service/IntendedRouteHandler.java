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
package dk.dma.epd.shore.service;

import java.util.Iterator;
import java.util.Map.Entry;

import dk.dma.epd.common.prototype.model.intendedroute.FilteredIntendedRoute;
import dk.dma.epd.common.prototype.model.intendedroute.FilteredIntendedRoutes;
import dk.dma.epd.common.prototype.model.intendedroute.IntendedRouteFilterMessage;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;
import dk.dma.epd.common.prototype.settings.handlers.IntendedRouteHandlerCommonSettings;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Converter;

/**
 * Shore specific intended route service implementation.
 */
public class IntendedRouteHandler extends IntendedRouteHandlerCommon {

    /**
     * Constructor
     */
    public IntendedRouteHandler(IntendedRouteHandlerCommonSettings<?> settings) {
        super(settings);
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
        return String.format("The routes of MMSI %d and %d come within %s of each other at %s.", 
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

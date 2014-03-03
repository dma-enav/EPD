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
package dk.dma.epd.common.prototype.model.intendedroute;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import dk.dma.epd.common.prototype.model.intendedroute.FilteredIntendedRoute.FilteredIntendedRouteKey;

/**
 * Contains the full list of filtered intended routes
 * indexed by MMSI.
 * <p>
 * Most methods exists in two variant: One variant takes two MMSI parameters
 * which represents the combined <i>key</i> of a filtered intended route.<br>
 * The other set takes one MMSI parameter which represents the list of filtered
 * intended routes with one MMSI being identical to the parameter.
 * <p>
 * The two-MMSI methods of this class have been constructed such that
 * the order of the MMSI parameters has no effect.
 */
public class FilteredIntendedRoutes implements Serializable {

    private static final long serialVersionUID = 1L;

    private ConcurrentHashMap<FilteredIntendedRouteKey, FilteredIntendedRoute> filteredIntendedRoutes = new ConcurrentHashMap<>();
    
    /**
     * Adds a new filtered intended route to the list
     * @param filteredIntendedRoute the route to add
     */
    public void add(FilteredIntendedRoute filteredIntendedRoute) {
        // Sanity checks
        if (filteredIntendedRoute == null) {
            return;
        }
        
        filteredIntendedRoutes.put(
                new FilteredIntendedRouteKey(filteredIntendedRoute.getMmsi1(), filteredIntendedRoute.getMmsi2()), 
                filteredIntendedRoute);
    }
    
    /**
     * Returns if the list contains a filtered intended route for the given MMSI's
     * 
     * @param mmsi1 the first MMSI
     * @param mmsi2 the second MMSI
     * @return if the list contains a filtered intended route for the given MMSI's
     */
    public synchronized boolean containsKey(Long mmsi1, Long mmsi2) {
        if (mmsi1 == null || mmsi2 == null) {
            return false;
        }
        return filteredIntendedRoutes.containsKey(
                new FilteredIntendedRouteKey(mmsi1, mmsi2));
    }
    
    /**
     * Returns the filtered intended route for the given MMSI's
     * or null if not found
     * 
     * @param mmsi1 the first MMSI
     * @param mmsi2 the second MMSI
     * @return the filtered intended route for the given MMSI's
     */
    public synchronized FilteredIntendedRoute get(Long mmsi1, Long mmsi2) {
        if (mmsi1 == null || mmsi2 == null) {
            return null;
        }
        return filteredIntendedRoutes.get(
                new FilteredIntendedRouteKey(mmsi1, mmsi2));
    }
    
    /**
     * Removes the filtered intended route for the given MMSI's.
     * Returns the removed route or null if not found.
     * 
     * @param mmsi1 the first MMSI
     * @param mmsi2 the second MMSI
     * @return the filtered intended route being removed
     */
    public synchronized FilteredIntendedRoute remove(Long mmsi1, Long mmsi2) {
        if (mmsi1 == null || mmsi2 == null) {
            return null;
        }
        return filteredIntendedRoutes.remove(
                new FilteredIntendedRouteKey(mmsi1, mmsi2));
    }

    
    /**
     * Returns if the list contains a filtered intended route for the given MMSI
     * 
     * @param mmsi the MMSI
     * @return if the list contains a filtered intended route for the given MMSI
     */
    public synchronized boolean containsKey(Long mmsi) {
        Set<FilteredIntendedRoute> matches = get(mmsi);
        return matches.size() > 0;
    }
    
    /**
     * Returns the filtered intended route for the given MMSI
     * 
     * @param mmsi the MMSI
     * @return the filtered intended routes for the given MMSI
     */
    public synchronized Set<FilteredIntendedRoute> get(Long mmsi) {
        Set<FilteredIntendedRoute> result = new HashSet<>();
        for (FilteredIntendedRoute filteredIntendedRoute : filteredIntendedRoutes.values()) {
            if (filteredIntendedRoute.getMmsi1().equals(mmsi) || 
                    filteredIntendedRoute.getMmsi2().equals(mmsi)) {
                result.add(filteredIntendedRoute);
            }
        }
        return result;
    }
    
    /**
     * Removes all the filtered intended route for the given MMSI.
     * Returns the removed routes.
     * 
     * @param mmsi the MMSI
     * @return the filtered intended routes being removed
     */
    public synchronized Set<FilteredIntendedRoute> remove(Long mmsi) {
        Set<FilteredIntendedRoute> result = new HashSet<>();
        for(Iterator<Map.Entry<FilteredIntendedRouteKey, FilteredIntendedRoute>> it 
                = filteredIntendedRoutes.entrySet().iterator(); it.hasNext(); ) {
            FilteredIntendedRoute filteredIntendedRoute = it.next().getValue();
            if(filteredIntendedRoute.getMmsi1().equals(mmsi) || 
                    filteredIntendedRoute.getMmsi2().equals(mmsi)) {
              it.remove();
              result.add(filteredIntendedRoute);
            }
          }
        return result;
    }
    
    /**
     * Returns the number of filtered intended routes held by this entity
     * @return the number of filtered intended routes held by this entity
     */
    public synchronized int size() {
        return filteredIntendedRoutes.size();
    }
    
    /**
     * Returns the list of filtered intended routes held by this entity
     * @return the list of filtered intended routes held by this entity
     */
    public synchronized Collection<FilteredIntendedRoute> values() {
        return filteredIntendedRoutes.values();
    }    
}

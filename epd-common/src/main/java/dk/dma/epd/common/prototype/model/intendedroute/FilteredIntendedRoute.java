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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The class contains all the TCPA data between two routes
 */
public class FilteredIntendedRoute {

    List<IntendedRouteFilterMessage> filterMessages;
    boolean generatedNotification;
    Long mmsi1;
    Long mmsi2;
    
    public FilteredIntendedRoute(Long mmsi1, Long mmsi2) {
        filterMessages = new ArrayList<>();
        this.mmsi1 = mmsi1;
        this.mmsi2 = mmsi2;
    }
    
    public List<IntendedRouteFilterMessage> getFilterMessages() {
        return filterMessages;
    }
    
    public void setFilterMessages(List<IntendedRouteFilterMessage> filterMessages) {
        this.filterMessages = filterMessages;
    }
    
    /**
     * Returns if this filtered intended route should be included in the filter
     * @return if this filtered intended route should be included in the filter
     */
    public boolean include() {
        return filterMessages.size() > 0;
    }

    /**
     * Returns the MMSI of one of the routes
     * @return the MMSI of one of the routes
     */
    public Long getMmsi1() {
        return mmsi1;
    }

    /**
     * Returns the MMSI of the other route
     * @return the MMSI of the other route
     */
    public Long getMmsi2() {
        return mmsi2;
    }

    /**
     * Returns a composite MMSI key for this entity
     * @return a composite MMSI key for this entity
     */
    public FilteredIntendedRouteKey getKey() {
        return new FilteredIntendedRouteKey(mmsi1, mmsi2);
    }
    
    /**
     * Returns if any of the CPA positions are within the given distance in nautical miles
     * and the given time in minutes
     * 
     * @param distance the distance in nautical miles
     * @param minutes the time in minutes
     * @return if any of the CPA positions are within the given distance and time
     */
    public boolean isWithinRange(double distance, int minutes) {
        for (IntendedRouteFilterMessage message : filterMessages) {
            if (message.isWithinRange(distance, minutes)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns filter message representing the minimum distance between the routes.
     * 
     * @return the filter message representing the minimum distance between the routes
     */
    public IntendedRouteFilterMessage getMinimumDistanceMessage() {
        double minDist = Double.MAX_VALUE;
        IntendedRouteFilterMessage minDistMessage = null;
        for (IntendedRouteFilterMessage message : filterMessages) {
            double dist = message.getDistance();
            if (dist < minDist) {
                minDist = dist;
                minDistMessage = message;
            }
        } 
        return minDistMessage;
    }

    /**
     * Returns if this filtered intended route has generated a notification
     * @return if this filtered intended route has generated a notification
     */
    public boolean hasGeneratedNotification() {
        return generatedNotification;
    }

    /**
     * Sets if this filtered intended route has generated a notification
     * @param generatedNotification if this filtered intended route has generated a notification
     */
    public void setGeneratedNotification(boolean generatedNotification) {
        this.generatedNotification = generatedNotification;
    }

    /**
     * A composite two-valued MMSI key used for storing a filtered intended route.
     * <p>
     * By ordering the MMSI's, the key will ensure that:
     * <pre>
     *  new FilteredIntendedRouteKey(m1, m2).equals(new FilteredIntendedRouteKey(m2, m1));
     * </pre>
     */
    public static class FilteredIntendedRouteKey implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Long mmsi1;
        private Long mmsi2;
        
        /**
         * Constructor
         * @param mmsi1 the first MMSI
         * @param mmsi2 the second MMSI
         */
        public FilteredIntendedRouteKey(Long mmsi1, Long mmsi2) {
            Objects.requireNonNull(mmsi1);
            Objects.requireNonNull(mmsi2);
            
            this.mmsi1 = Math.min(mmsi1, mmsi2);
            this.mmsi2 = Math.max(mmsi1, mmsi2);
        }
        
        public Long getMmsi1() {
            return mmsi1;
        }

        public Long getMmsi2() {
            return mmsi2;
        }

        /**
         * Returns a string representation of this key
         * @return a string representation of this key
         */
        @Override
        public String toString() {
            return String.format("%d_%d", mmsi1, mmsi2);
        }
        
        /**
         * Returns the hash code for this object
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((mmsi1 == null) ? 0 : mmsi1.hashCode());
            result = prime * result + ((mmsi2 == null) ? 0 : mmsi2.hashCode());
            return result;
        }

        /**
         * Check for equality
         * @param obj the object to compare with
         * @return if {@code obj} equals {@code this}
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            else if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            FilteredIntendedRouteKey other = (FilteredIntendedRouteKey) obj;
            return this.mmsi1.equals(other.mmsi1) && this.mmsi2.equals(other.mmsi2);
        }
    }
}

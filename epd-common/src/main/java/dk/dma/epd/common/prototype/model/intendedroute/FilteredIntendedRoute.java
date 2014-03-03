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

import java.util.ArrayList;
import java.util.List;

import dk.dma.epd.common.prototype.model.route.IntendedRoute;

public class FilteredIntendedRoute {

    IntendedRoute intendedRoute;
    List<IntendedRouteFilterMessage> filterMessages;
    boolean generatedNotification;
    
    public FilteredIntendedRoute(){
        filterMessages = new ArrayList<>();
    }
    
    public IntendedRoute getIntendedRoute() {
        return intendedRoute;
    }
    
    public void setIntendedRoute(IntendedRoute intendedRoute) {
        this.intendedRoute = intendedRoute;
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

}

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
package dk.dma.epd.common.prototype.model.intendedroute;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.util.Converter;
import org.joda.time.DateTime;

public class IntendedRouteFilterMessage {

    Route route1;
    Route route2;
    Position position1;
    Position position2;
    String message;
    int legStartIndex;
    int legEndIndex;
    DateTime time1;
    DateTime time2;
    IntendedRouteFilterType filterType;
    
    public enum IntendedRouteFilterType {
        ALERT, FILTERONLY, ENC, ALERTANDENC
    }
    
    public IntendedRouteFilterMessage(Route route1, Route route2, Position position1, Position position2, String message, int legStartIndex, int legEndIndex, IntendedRouteFilterType filterType) {
        this.route1 = route1;
        this.route2 = route2;
        this.legStartIndex = legStartIndex;
        this.legEndIndex = legEndIndex;
        this.position1 = position1;
        this.position2 = position2;
        this.message = message;
        this.filterType = filterType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Position getPosition1() {
        return position1;
    }

    public void setPosition1(Position position1) {
        this.position1 = position1;
    }

    public Position getPosition2() {
        return position2;
    }

    public void setPosition2(Position position2) {
        this.position2 = position2;
    }

    public DateTime getTime1() {
        return time1;
    }

    public void setTime1(DateTime time1) {
        this.time1 = time1;
    }

    public DateTime getTime2() {
        return time2;
    }

    public void setTime2(DateTime time2) {
        this.time2 = time2;
    }

    /**
     * Returns if the CPA position is within the given distance in nautical miles
     * 
     * @param distance
     *            the distance in nautical miles
     * @return if the CPA position is within the given distance
     */
    public boolean isWithinDistance(double distance) {
        return Converter.metersToNm(getDistance()) < distance;
    }

    /**
     * Returns the distance in meters between the two points of this message.
     * <p>
     * As we are looking for short distances, a CARTESIAN calculation is used.
     * 
     * @return the distance between the two points
     */
    public double getDistance() {
        return getPosition1().distanceTo(getPosition2(), CoordinateSystem.CARTESIAN);
    }



    public boolean routesVisible() {
        return route1.isVisible() && route2.isVisible();
    }

    /**
     * @return the filterType
     */
    public IntendedRouteFilterType getFilterType() {
        return filterType;
    }

    /**
     * @param filterType the filterType to set
     */
    public void setFilterType(IntendedRouteFilterType filterType) {
        this.filterType = filterType;
    }


    
    
    
    
    // Severity?
    // Type?
    // Possibly options is Intersection or Proximity Alert?

}

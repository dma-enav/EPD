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

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.util.Converter;

public class IntendedRouteFilterMessage {

    Position position1;
    Position position2;
    String message;
    int legStartIndex;
    int legEndIndex;
    DateTime time1;
    DateTime time2;
    
    public IntendedRouteFilterMessage(Position position1, Position position2, String message, int legStartIndex, int legEndIndex) {
        this.legStartIndex = legStartIndex;
        this.legEndIndex = legEndIndex;
        this.position1 = position1;
        this.position2 = position2;
        this.message = message;
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
     * and the given time in minutes
     * 
     * @param distance the distance in nautical miles
     * @param minutes the time in minutes
     * @return if the CPA position is within the given distance and time
     */
    public boolean isWithinRange(double distance, int minutes) {
        if (Converter.metersToNm(getDistance()) < distance
                && getTime1().isAfter(getTime2().minusMinutes(minutes))
                && getTime1().isBefore(getTime2().plusMinutes(minutes))) {
            return true;
        }
        return false;
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
    
    // Severity?
    // Type?
    // Possibly options is Intersection or Proximity Alert?
    
}

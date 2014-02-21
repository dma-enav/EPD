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

import dk.dma.enav.model.geometry.Position;

public class IntendedRouteFilterMessage {

    Position position1;
    Position position2;
    String message;
    int legStartIndex;
    int legEndIndex;
    
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

    // Severity?
    // Type?
    // Possibly options is Intersection or Proximity Alert?

    
    
}

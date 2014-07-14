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
package dk.dma.epd.common.prototype.model.identity;

import java.util.ArrayList;

import dk.dma.enav.model.geometry.Position;

/**
 * Maritime Cloud Service description
 * 
 * @author David Andersen Camre
 * 
 */
public class MCService {

    /**
     * Name of the service, eg. Strategic Route Exchange
     */
    String name;

    /**
     * Short description of the service
     */
    String description;

    /**
     * Set of coordinates for a polygon covered by the service
     */
    ArrayList<Position> area = new ArrayList<>();

    /**
     * Initialize a Maritime Cloud Service description
     * 
     * @param name
     * @param description
     * @param area
     */
    public MCService(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public void addPosition(double lat, double lon) {
        area.add(Position.create(lat, lon));
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the area
     */
    public ArrayList<Position> getArea() {
        return area;
    }

    
    
}

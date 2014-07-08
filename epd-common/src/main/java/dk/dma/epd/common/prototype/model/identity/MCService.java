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

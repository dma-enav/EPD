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

package dk.dma.epd.shore.voyage;

import java.io.Serializable;

import dk.dma.epd.common.prototype.model.route.Route;

public class Voyage implements Serializable{

    private static final long serialVersionUID = 1L;
    private Route route;
    private long id;
    long mmsi;

    
    public Voyage(long mmsi, Route route, long id) {
        this.id = id;
        this.route = route;
        this.mmsi = mmsi;
    }
    
    public Route getRoute() {
        return route;
    }
    public void setRoute(Route route) {
        this.route = route;
    }
    public long getMmsi() {
        return mmsi;
    }
    public void setMmsi(long mmsi) {
        this.mmsi = mmsi;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    
    
    
}

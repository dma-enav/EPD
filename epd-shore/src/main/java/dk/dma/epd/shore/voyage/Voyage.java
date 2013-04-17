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

package dk.dma.epd.shore.voyage;

import java.io.Serializable;

import dk.dma.epd.common.prototype.model.route.Route;

public class Voyage implements Serializable{

    private static final long serialVersionUID = 1L;
    private Route route;
    
    public Voyage(long mmsi, Route route) {
        this.route = route;
        this.mmsi = mmsi;
    }
    
    
    long mmsi;
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
    
    
}

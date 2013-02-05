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
package dk.dma.epd.shore.service.ais;

public class RouteSuggestionKey {

    private long mmsi;
    private int id;

    public RouteSuggestionKey(long mmsi, int id) {
        this.mmsi = mmsi;
        this.id = id;
    }

    public long getMmsi() {
        return mmsi;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object key) {

        RouteSuggestionKey routeKey = (RouteSuggestionKey) key;
        
        return routeKey.getId() == this.id && routeKey.getMmsi() == this.mmsi;
    }
    
    public int hashCode(){
        return super.hashCode();
    }

    public String toString(){
        return "mmsi: " + this.mmsi + " id: " + this.id;
    }
}

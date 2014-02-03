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
package dk.dma.epd.shore.service;

import net.jcip.annotations.Immutable;

@Immutable
public class RouteSuggestionKey {

    private long mmsi;
    private long id;

    public RouteSuggestionKey(long mmsi, long l) {
        this.mmsi = mmsi;
        this.id = l;
    }

    public long getMmsi() {
        return mmsi;
    }

    public long getId() {
        return id;
    }


    public String toString() {
        return "mmsi: " + this.mmsi + " id: " + this.id;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (int) (mmsi ^ (mmsi >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RouteSuggestionKey other = (RouteSuggestionKey) obj;
        return id == other.id && mmsi == other.mmsi;
    }
}

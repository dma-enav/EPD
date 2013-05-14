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
package dk.dma.epd.shore.ais;

import java.util.Date;

import dk.dma.enav.model.geometry.Position;

public class PastTrackPoint {

    Date date;
    Position position;

    public PastTrackPoint(Date date, Position position) {
        this.date = date;
        this.position = position;
    }

    public Date getDate() {
        return date;
    }

    public Position getPosition() {
        return position;
    }

    public String toString() {
        return "Date " + date + " Position: " + position;
    }

}

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
package dk.dma.epd.ship.util.route;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Date;

import dk.dma.enav.model.geometry.Position;

/**
 * Geographic location with time.  
 */
public class TimePoint implements Comparable<TimePoint>, Serializable {
    
    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private Date time;
    
    private Position position;
    /**
     * @return
     * @see dk.dma.enav.model.geometry.Position#getLatitude()
     */
    public double getLatitude() {
        return position.getLatitude();
    }

    /**
     * @return
     * @see dk.dma.enav.model.geometry.Position#getLongitude()
     */
    public double getLongitude() {
        return position.getLongitude();
    }

    public TimePoint(Position pos, Date time) {
        this.time = requireNonNull(time);
        this.position= requireNonNull(pos);
    }
    
    public Date getTime() {
        return time;
    }

    @Override
    public int compareTo(TimePoint tp) {
        if (time.getTime() == tp.getTime().getTime()) {
            return 0;
        }
        return time.getTime() < tp.getTime().getTime() ? -1 : 1;
    }

    @Override
    public String toString() {
        return "TimePoint [lat=" + getLatitude() + " lon=" + getLongitude() + " time=" + time + "]";
    }    

}

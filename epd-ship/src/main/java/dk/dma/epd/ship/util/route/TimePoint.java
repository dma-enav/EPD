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

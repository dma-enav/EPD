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
package dk.dma.epd.util.route;

import java.util.Date;

import dk.dma.enav.model.geometry.Position;

/**
 * Geographic location with time.
 */
public class TimePoint implements Comparable<TimePoint> {
    private final Date time;
    private final Position pos;

    public TimePoint(Position pos, Date time) {
        this.pos = Position.create(pos.getLatitude(), pos.getLongitude());
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public int compareTo(TimePoint tp) {
        if (time.getTime() == tp.getTime().getTime()) {
            return 0;
        }
        return (time.getTime() < tp.getTime().getTime()) ? -1 : 1;
    }

    @Override
    public String toString() {
        return "TimePoint [lat=" + pos.getLatitude() + " lon=" + pos.getLongitude() + " time=" + time + "]";
    }

    public double getLatitude() {
        return pos.getLatitude();
    }

    public double getLongitude() {
        return pos.getLongitude();
    }
    
    public Position getPos() {
        return pos;
    }

}

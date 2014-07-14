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
package dk.dma.epd.util.route;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Generate routes from tracks using a simple algorithm
 */
public class SimpleRouteGenerator implements IRouteGenerator {

    protected List<TimePoint> track;
    protected List<TimePoint> route = new ArrayList<TimePoint>();

    @Override
    public List<TimePoint> generateRoute(List<TimePoint> track) {
        this.track = track;
        if (track.size() == 0) {
            return route;
        }

        // Simply just make a waypoint every minute
        Date lastWpTime = track.get(0).getTime();
        for (int i = 0; i < track.size(); i++) {
            TimePoint point = track.get(i);
            long elapsed = point.getTime().getTime() - lastWpTime.getTime();
            if (elapsed > 60000) {
                route.add(point);
                lastWpTime = point.getTime();
            }
        }

        return route;
    }

}

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

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
import java.util.List;

import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.proj.Length;

/**
 * Divide and conquer algorithm for generating route from track.
 */
public class ApproxRouteGenerator extends SimpleRouteGenerator {

    private double tolleranceMeters = 185.2; // 1/10 nm
    private List<Geo> geoPoints = new ArrayList<Geo>();

    @Override
    public List<TimePoint> generateRoute(List<TimePoint> track) {
        if (track.size() == 0) {
            return null;
        }
        this.track = track;
        // Create geo points for each track points
        System.out.println("\tCreating geo points");
        for (TimePoint point : track) {
            geoPoints.add(new Geo(point.getLatitude(), point.getLongitude()));
        }

        setTolleranceMeters(100);

        // Generate route by divide and conquer
        route.add(track.get(0));
        _generate(0, track.size() - 1);
        route.add(track.get(track.size() - 1));

        return route;
    }

    private void _generate(int start, int end) {
        if (end - start == 1) {
            return;
        }
        if (end - start < 1) {
            System.out.println("ERROR: Should not happen");
            return;
        }
        // Find point with maximum distance to line from start to end
        Geo x1 = geoPoints.get(start);
        Geo x2 = geoPoints.get(end);
        double x1x2Length = x1.distance(x2);

        double maxDistance = -1;
        int maxPoint = -1;
        for (int i = start; i <= end; i++) {
            Geo x0 = geoPoints.get(i);
            Geo a = x0.subtract(x1);
            Geo b = x0.subtract(x2);
            double distMeters = Length.KM.fromRadians(a.crossLength(b) / x1x2Length) * 1000.0;
            if (distMeters > maxDistance) {
                maxDistance = distMeters;
                maxPoint = i;
            }
        }

        if (maxDistance > tolleranceMeters) {
            _generate(start, maxPoint);
            route.add(track.get(maxPoint));
            _generate(maxPoint, end);
        }
    }

    public double getTolleranceMeters() {
        return tolleranceMeters;
    }

    public void setTolleranceMeters(double tolleranceMeters) {
        this.tolleranceMeters = tolleranceMeters;
    }

}

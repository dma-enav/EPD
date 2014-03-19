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

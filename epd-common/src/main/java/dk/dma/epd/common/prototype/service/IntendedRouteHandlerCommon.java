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
package dk.dma.epd.common.prototype.service;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.broadcast.BroadcastListener;
import net.maritimecloud.net.broadcast.BroadcastMessageHeader;

import org.joda.time.DateTime;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.Intersection;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.enavcloud.IntendedRouteBroadcast;
import dk.dma.epd.common.prototype.model.intendedroute.FilteredIntendedRoute;
import dk.dma.epd.common.prototype.model.intendedroute.IntendedRouteFilterMessage;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.util.Converter;

/**
 * Intended route service implementation.
 * <p>
 * Listens for intended route broadcasts, and updates the vessel target when one is received.
 */
public class IntendedRouteHandlerCommon extends EnavServiceHandlerCommon {

    /**
     * Time an intended route is considered valid without update
     */
    public static final long ROUTE_TTL = 10 * 60 * 1000; // 10 min

    protected ConcurrentHashMap<Long, IntendedRoute> intendedRoutes = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, FilteredIntendedRoute> filteredIntendedRoutes = new ConcurrentHashMap<>();

    protected List<IIntendedRouteListener> listeners = new CopyOnWriteArrayList<>();

    private List<Position> intersectPositions = new ArrayList<Position>();

    private AisHandlerCommon aisHandler;
    private MapBean mapBean;

    /**
     * Constructor
     */
    public IntendedRouteHandlerCommon() {
        super();

        // Checks and remove stale intended routes every minute
        getScheduler().scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                checkForInactiveRoutes();
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Returns the intended route associated with the given MMSI
     * 
     * @param mmsi
     *            the MMSI of the intended route
     * @return the intended route or null if not present
     */
    public IntendedRoute getIntendedRoute(long mmsi) {
        return intendedRoutes.get(mmsi);
    }

    /**
     * Returns a copy of the current list of intended routes
     * 
     * @return a copy of the current list of intended routes
     */
    public List<IntendedRoute> fetchIntendedRoutes() {
        List<IntendedRoute> list = new ArrayList<>();
        list.addAll(intendedRoutes.values());
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {

        // Hook up as a broadcast listener
        connection.broadcastListen(IntendedRouteBroadcast.class, new BroadcastListener<IntendedRouteBroadcast>() {
            public void onMessage(BroadcastMessageHeader l, IntendedRouteBroadcast r) {

                getStatus().markCloudReception();
                int id = MaritimeCloudUtils.toMmsi(l.getId());
                updateIntendedRoute(id, r);
            }
        });
    }

    /**
     * Update intended route of vessel target
     * 
     * @param mmsi
     * @param r
     */
    private synchronized void updateIntendedRoute(long mmsi, IntendedRouteBroadcast r) {

        IntendedRoute intendedRoute = new IntendedRoute(r.getIntendedRoute());
        intendedRoute.setMmsi(mmsi);
        intendedRoute.setActiveWpIndex(r.getActiveWPIndex());
        intendedRoute.setPlannedEtas(r.getOriginalEtas());

        IntendedRoute oldIntendedRoute = intendedRoutes.get(mmsi);
        if (oldIntendedRoute != null) {
            intendedRoute.setVisible(oldIntendedRoute.isVisible());
        }

        // Check if this is a real intended route or one that signals a removal
        if (!intendedRoute.hasRoute()) {
            if (intendedRoutes.containsKey(mmsi)) {
                intendedRoutes.remove(mmsi);
                // fireIntendedRouteRemoved(intendedRoute);
            }
            if (filteredIntendedRoutes.containsKey(mmsi)) {
                filteredIntendedRoutes.remove(mmsi);
            }
            // return;
        } else {

            // The intended route is valid
            intendedRoutes.put(mmsi, intendedRoute);

            // Apply the filter to the route
            applyFilter(intendedRoute);

            // Sanity check
            if (aisHandler != null) {
                // Try to find exiting target
                VesselTarget vesselTarget = aisHandler.getVesselTarget(mmsi);
                if (vesselTarget != null) {
                    intendedRoute.update(vesselTarget.getPositionData());
                }
            }

        }

        // Fire event
        fireIntendedEvent(intendedRoute);

        System.out.println("Did the route get put into the filter? " + filteredIntendedRoutes.size());
    }

    /**
     * Remove stale intended routes.
     */
    private synchronized void checkForInactiveRoutes() {
        Date now = PntTime.getInstance().getDate();
        for (Iterator<Map.Entry<Long, IntendedRoute>> it = intendedRoutes.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Long, IntendedRoute> entry = it.next();
            if (now.getTime() - entry.getValue().getReceived().getTime() > ROUTE_TTL) {
                // Remove the intended route
                it.remove();
                fireIntendedEvent(entry.getValue());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (obj instanceof AisHandlerCommon) {
            aisHandler = (AisHandlerCommon) obj;
        }
        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        }
    }

    /**
     * Hide all intended routes
     */
    public void hideAllIntendedRoutes() {
        for (IntendedRoute intendedRoute : intendedRoutes.values()) {
            intendedRoute.setVisible(false);
            fireIntendedEvent(intendedRoute);
        }
    }

    /**
     * Show all intended routes
     */
    public void showAllIntendedRoutes() {
        for (IntendedRoute intendedRoute : intendedRoutes.values()) {
            intendedRoute.setVisible(true);
            fireIntendedEvent(intendedRoute);
        }
    }

    /****************************************/
    /** Listener functions **/
    /****************************************/

    /**
     * Adds an intended route listener
     * 
     * @param listener
     *            the listener to add
     */
    public void addListener(IIntendedRouteListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes an intended route listener
     * 
     * @param listener
     *            the listener to remove
     */
    public void removeListener(IIntendedRouteListener listener) {
        listeners.remove(listener);
    }

    /**
     * Called when an intended route event has been recieved
     * 
     * @param intendedRoute
     *            the intended route
     */
    public void fireIntendedEvent(IntendedRoute intendedRoute) {
        for (IIntendedRouteListener listener : listeners) {
            listener.intendedRouteEvent(intendedRoute);
        }
    }

    /**
     * Update all filters
     */
    protected void updateFilter() {

    }

    /**
     * Update filter with new intendedroute
     * 
     * @param route
     */
    protected void applyFilter(IntendedRoute route) {

    }

    /**
     * Apply Filter on the two routes
     * 
     * @param route1
     * @param route2
     */
    protected FilteredIntendedRoute compareRoutes(Route route1, Route route2) {

        intersectPositions.clear();
        
        System.out.println("Comparing routes");

        // The returned FilteredIntendedRoute is connected to route2
        FilteredIntendedRoute filteredIntendedRoute = new FilteredIntendedRoute();

        // First route (active route in ship)
        LinkedList<RouteWaypoint> route1Waypoints = route1.getWaypoints();

        // Second route list
        LinkedList<RouteWaypoint> route2Waypoints = route2.getWaypoints();

        Position previousPositionActiveRoute = route1Waypoints.get(0).getPos();
        for (int i = 1; i < route1Waypoints.size(); i++) {

            Position route1Waypoint1 = previousPositionActiveRoute;
            Position route1Waypoint2 = route1Waypoints.get(i).getPos();

            Position previousPositionIntendedRoute = route2Waypoints.get(0).getPos();
            for (int j = 1; j < route2Waypoints.size(); j++) {

                // Line segment
                Position route2Waypoint1 = previousPositionIntendedRoute;
                Position route2Waypoint2 = route2Waypoints.get(j).getPos();

                // This is where we apply the actual filter, for now only checking on intersections
                IntendedRouteFilterMessage intersectionResultMessage = intersectionFilter(route1, route2, i, j, route1Waypoint1,
                        route1Waypoint2, route2Waypoint1, route2Waypoint2);

                if (intersectionResultMessage != null) {
                    filteredIntendedRoute.getFilterMessages().add(intersectionResultMessage);
                }else{
                    
                    // Region filter - do not apply if we have an intersection of line segments/
                    IntendedRouteFilterMessage regionResultMessage = proxmityFilter(route1, route2, i, j, route1Waypoint1,
                            route1Waypoint2, route2Waypoint1, route2Waypoint2, 1.0);

                    if (regionResultMessage != null) {
                        filteredIntendedRoute.getFilterMessages().add(regionResultMessage);
                    }
                }

                
                


                // Add more filters

                previousPositionIntendedRoute = route2Waypoint2;
            }

            previousPositionActiveRoute = route1Waypoint2;

            // it.remove(); // avoids a ConcurrentModificationException
        }

        return filteredIntendedRoute;

    }

    private Position intersection(Position A1, Position A2, Position B1, Position B2) {
        Geo intersectionPoint = null;

        Geo a1 = new Geo(A1.getLatitude(), A1.getLongitude());
        Geo a2 = new Geo(A2.getLatitude(), A2.getLongitude());

        Geo b1 = new Geo(B1.getLatitude(), B1.getLongitude());
        Geo b2 = new Geo(B2.getLatitude(), B2.getLongitude());

        intersectionPoint = Intersection.segmentsIntersect(a1, a2, b1, b2);

        if (intersectionPoint != null) {
            // System.out.println("We have interesection at " + intersectionPoint);
            return Position.create(intersectionPoint.getLatitude(), intersectionPoint.getLongitude());
        } else {
            return null;

        }

    }

    public ConcurrentHashMap<Long, IntendedRoute> getIntendedRoutes() {
        return intendedRoutes;
    }

    public ConcurrentHashMap<Long, FilteredIntendedRoute> getFilteredIntendedRoutes() {
        return filteredIntendedRoutes;
    }

    private Point2D nearestPointOnLine(double ax, double ay, double bx, double by, double px, double py, boolean clampToSegment) {
        Double dest = new Point2D.Double();

        double apx = px - ax;
        double apy = py - ay;
        double abx = bx - ax;
        double aby = by - ay;

        double ab2 = abx * abx + aby * aby;
        double ap_ab = apx * abx + apy * aby;
        double t = ap_ab / ab2;
        if (clampToSegment) {
            if (t < 0) {
                t = 0;
            } else if (t > 1) {
                t = 1;
            }
        }
        dest.setLocation(ax + abx * t, ay + aby * t);
        return dest;
    }

    private IntendedRouteFilterMessage proxmityFilter(Route route1, Route route2, int i, int j, Position route1Waypoint1,
            Position route1Waypoint2, Position route2Waypoint1, Position route2Waypoint2, double epsilon) {

        Position A = route1Waypoint1;
        Position B = route1Waypoint2;

        Position C = route2Waypoint1;
        Position D = route2Waypoint2;

        
        
        //If all headins are RL use first method
        //Otherwise us GC method
        //If they are different - use hybrid
        if (route1.getWaypoints().get(i).getOutLeg().getHeading() == Heading.RL){
            
        }
        
        
        
        
        
        
        
        Projection projection = mapBean.getProjection();

        // projection = new LambertConformal((LatLonPoint) projection.getCenter(), projection.getScale(), projection.getWidth(),
        // projection.getHeight(), 1, 1, 1, 1, 1, 1, Ellipsoid.WGS_84);

        // projection = new LambertConformalLoader().create(new Properties());

        Point2D pointA = projection.forward(A.getLatitude(), A.getLongitude());
        Point2D pointB = projection.forward(B.getLatitude(), B.getLongitude());
        Point2D pointC = projection.forward(C.getLatitude(), C.getLongitude());
        Point2D pointD = projection.forward(D.getLatitude(), D.getLongitude());

        // Calculations when looking at route going from A to B with external point C
        Point2D first = nearestPointOnLine(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY(), pointC.getX(),
                pointC.getY(), true);
        LatLonPoint firstProjected = projection.inverse(first);
        Position posWP1Segment1 = Position.create(firstProjected.getLatitude(), firstProjected.getLongitude());
        double distanceWP1Segment1 = C.distanceTo(posWP1Segment1, CoordinateSystem.CARTESIAN);

        // Calculations when looking at route going from A to B with external point D
        Point2D second = nearestPointOnLine(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY(), pointD.getX(),
                pointD.getY(), true);
        LatLonPoint secondProjected = projection.inverse(second);
        Position posWP2Segment1 = Position.create(secondProjected.getLatitude(), secondProjected.getLongitude());
        double distanceWP2Segment1 = D.distanceTo(posWP2Segment1, CoordinateSystem.CARTESIAN);

        // Calculations when looking at route going from C to D with external point A
        Point2D third = nearestPointOnLine(pointC.getX(), pointC.getY(), pointD.getX(), pointD.getY(), pointA.getX(),
                pointA.getY(), true);
        LatLonPoint thirdProjected = projection.inverse(third);
        Position posWP1Segment2 = Position.create(thirdProjected.getLatitude(), thirdProjected.getLongitude());
        double distanceWP1Segment2 = A.distanceTo(posWP1Segment2, CoordinateSystem.CARTESIAN);

        // Calculations when looking at route going from C to D with external point B
        Point2D fourth = nearestPointOnLine(pointC.getX(), pointC.getY(), pointD.getX(), pointD.getY(), pointB.getX(),
                pointB.getY(), true);
        LatLonPoint fourthProjected = projection.inverse(fourth);
        Position posWP2Segment2 = Position.create(fourthProjected.getLatitude(), fourthProjected.getLongitude());
        double distanceWP2Segment2 = B.distanceTo(posWP2Segment2, CoordinateSystem.CARTESIAN);

        //
        // intersectPositions.add(Position.create(firstProjected.getLatitude(), firstProjected.getLongitude()));
        // intersectPositions.add(Position.create(secondProjected.getLatitude(), secondProjected.getLongitude()));
        // intersectPositions.add(Position.create(thirdProjected.getLatitude(), thirdProjected.getLongitude()));
        // intersectPositions.add(Position.create(fouthProjected.getLatitude(), fouthProjected.getLongitude()));

//        System.out.println("distanceWP1Segment1 is " + distanceWP1Segment1);
//        System.out.println("distanceWP2Segment1 is " + distanceWP2Segment1);
//        System.out.println("distanceWP1Segment2 is " + distanceWP1Segment2);
//        System.out.println("distanceWP2Segment2 is " + distanceWP2Segment2);

        double shorestDistanceSegment1 = distanceWP1Segment1;
        double shorestDistanceSegment2 = distanceWP1Segment2;

        Position shortestDistanceSegment1Position = posWP1Segment1;
        Position shortestDistanceSegment2Position = posWP1Segment2;

        if (distanceWP1Segment1 > distanceWP2Segment1) {
            shorestDistanceSegment1 = distanceWP2Segment1;
            shortestDistanceSegment1Position = posWP2Segment1;
        }

        if (distanceWP1Segment2 > distanceWP2Segment2) {
            shorestDistanceSegment2 = distanceWP2Segment2;
            shortestDistanceSegment2Position = posWP2Segment2;
        }

        double shortestDistance = shorestDistanceSegment1;
        // Position shortestDistancePosition = shortestDistanceSegment1Position;

        if (shorestDistanceSegment1 > shorestDistanceSegment2) {
            shortestDistance = shorestDistanceSegment2;
            // shortestDistancePosition = shortestDistanceSegment2Position;
        }

        System.out.println("The shorest distance is " +  Converter.metersToNm(shortestDistance));
        
        
        // intersectPositions.add(shortestDistancePosition);

        if (Converter.metersToNm(shortestDistance) <= epsilon) {

            System.out.println("Checking time");
            
            if (checkDateInterval(shortestDistanceSegment1Position, shortestDistanceSegment2Position, route1, route2, i, j)) {

                System.out.println("Adding shortest distance is " + Converter.metersToNm(shortestDistance) + " at " + shortestDistanceSegment2Position);        
                
                intersectPositions.add(shortestDistanceSegment2Position);

                IntendedRouteFilterMessage message = new IntendedRouteFilterMessage(shortestDistanceSegment2Position,
                        "Route Segments proximity warning", j - 1, j);
                return message;
            }

        }

//         intersectPositions.add(Calculator.findCenterPosition(A, B, CoordinateSystem.GEODETIC));
//         intersectPositions.add(Calculator.findCenterPosition(C, D, CoordinateSystem.GEODETIC));

        
        
        
        // U = (x3-x1) * (x2 -x1 ) + (y3 - y1) * (y2 - y1)

        //
        // //Define first line from pointA and pointB
        // //Find slope
        // double m = (pointB.getY() - pointA.getY()) / (pointB.getX() - pointA.getX());
        //
        // //y = m(x-x1)+y1
        // //y = m*x-x1*m+y1
        // double x;
        // double y = m*x-pointB.getX()*m+pointB.getY();
        //
        //

        // route1Waypoint1
        // route1Waypoint2

        // route2Waypoint1
        // route2Waypoint2

        // Calculate shorest path between two lines. The one of the end points must be the closest

        // double distanceWP1Segment1 = Calculator.crossTrackDistance(route1Waypoint1, route1Waypoint2, route2Waypoint1);
        // double distanceWP2Segment1 = Calculator.crossTrackDistance(route1Waypoint1, route1Waypoint2, route2Waypoint2);
        //
        // double distanceWP1Segment2 = Calculator.crossTrackDistance(route2Waypoint1, route2Waypoint2, route1Waypoint1);
        // double distanceWP2Segment2 = Calculator.crossTrackDistance(route2Waypoint1, route2Waypoint2, route1Waypoint2);
        //
        // System.out.println("distanceWP1Segment1 is " + distanceWP1Segment1);
        // System.out.println("distanceWP2Segment1 is " + distanceWP2Segment1);
        // System.out.println("distanceWP1Segment2 is " + distanceWP1Segment2);
        // System.out.println("distanceWP2Segment2 is " + distanceWP2Segment2);
        //
        // double shorestDistanceSegment1 = distanceWP1Segment1;
        // double shorestDistanceSegment2 = distanceWP1Segment2;
        //
        // if (distanceWP1Segment1 > distanceWP2Segment1) {
        // shorestDistanceSegment1 = distanceWP2Segment1;
        // }
        //
        // if (distanceWP1Segment2 > distanceWP2Segment2) {
        // shorestDistanceSegment2 = distanceWP2Segment2;
        // }
        //
        // double shortestDistance = shorestDistanceSegment1;
        //
        // if (shorestDistanceSegment1 > shorestDistanceSegment2) {
        // shortestDistance = shorestDistanceSegment2;
        // }

        
        //
        // if (shortestDistance <= epsilon){
        // IntendedRouteFilterMessage message = new IntendedRouteFilterMessage(null,
        // "Intersection occurs within 2 hour of eachother", j - 1, j);
        //
        // return message;
        // }

        // Binary Search

        // Find midpoint of one line
        // Searching from route segment 1

        // Find center position of segment2

        // Start Calculating for route1Waypoint1

        // d13 is distance from start point to third point
        // θ13 is (initial) bearing from start point to third point
        // θ12 is (initial) bearing from start point to end point
        // R is the earth’s radius
        // double d13 = route2Waypoint1.distanceTo(route1Waypoint1, CoordinateSystem.GEODETIC);
        // double R = 6371000; // in meters
        // double brng13 = Math.toRadians(route2Waypoint1.geodesicInitialBearingTo(route1Waypoint1));
        // double brng12 = Math.toRadians(route2Waypoint1.geodesicInitialBearingTo(route2Waypoint2));
        //
        // double distance = Math.asin( //
        // Math.sin(d13 / R) //
        // * Math.sin(brng13 - brng12) //
        // ) * R; //
        //
        // // double alongTrackDistance = Math.acos(Math.cos(Math.toRadians(d13/R))/Math.cos(distance/R)) * R;
        //
        // System.out.println("Distance 1 is " + Converter.metersToNm(distance) + " NM");
        //
        // d13 = route2Waypoint1.distanceTo(route1Waypoint2, CoordinateSystem.GEODETIC);
        // brng13 = Math.toRadians(route2Waypoint1.geodesicInitialBearingTo(route1Waypoint2));
        // brng12 = Math.toRadians(route2Waypoint1.geodesicInitialBearingTo(route2Waypoint2));
        //
        // distance = Math.asin( //
        // Math.sin(d13 / R) //
        // * Math.sin(brng13 - brng12) //
        // ) * R; //
        //
        // // alongTrackDistance = Math.acos(Math.cos(Math.toRadians(d13/R))/Math.cos(distance/R)) * R;
        //
        // System.out.println("Distance 2 is " + Converter.metersToNm(distance) + " NM");
        //
        // System.out.println("Distances from point 1: "
        // + Converter.metersToNm(route1Waypoint1.distanceTo(route2Waypoint1, CoordinateSystem.GEODETIC)) + " and "
        // + Converter.metersToNm(route1Waypoint1.distanceTo(route2Waypoint2, CoordinateSystem.GEODETIC)));
        //
        // System.out.println("Distances from point 2: "
        // + Converter.metersToNm(route1Waypoint2.distanceTo(route2Waypoint1, CoordinateSystem.GEODETIC)) + " and "
        // + Converter.metersToNm(route1Waypoint2.distanceTo(route2Waypoint2, CoordinateSystem.GEODETIC)));

        // Geodesic geod = Geodesic.WGS84;
        // double
        // lat1 = 40.640, lon1 = -73.779, // JFK
        // lat2 = 1.359, lon2 = 103.989; // SIN
        // GeodesicData g = geod.Inverse(lat1, lon1, lat2, lon2,
        // GeodesicMask.DISTANCE | GeodesicMask.AZIMUTH);
        // GeodesicLine line = new GeodesicLine(geod, lat1, lon1, g.azi1,
        // GeodesicMask.DISTANCE_IN | GeodesicMask.LONGITUDE);
        // double
        // s12 = g.s12,
        // a12 = g.a12,
        // ds0 = 500e3; // Nominal distance between points = 500 km
        // int num = (int)(Math.ceil(s12 / ds0)); // The number of intervals
        // {
        // // Use intervals of equal length
        // double ds = s12 / num;
        // for (int i = 0; i <= num; ++i) {
        // g = line.Position(i * ds,
        // GeodesicMask.LATITUDE | GeodesicMask.LONGITUDE);
        // System.out.println(i + " " + g.lat2 + " " + g.lon2);
        // }
        // }
        // {
        // // Slightly faster, use intervals of equal arc length
        // double da = a12 / num;
        // for (int i = 0; i <= num; ++i) {
        // g = line.ArcPosition(i * da,
        // GeodesicMask.LATITUDE | GeodesicMask.LONGITUDE);
        // System.out.println(i + " " + g.lat2 + " " + g.lon2);
        // }
        // }

        return null;
    }

    private boolean checkDateInterval(Position positionRoute1, Position positionRoute2, Route route1, Route route2, int i, int j) {

        DateTime routeSegment1StartDate = new DateTime(route1.getEtas().get(i - 1));
        DateTime routeSegment1EndDate = new DateTime(route1.getEtas().get(i));

        DateTime routeSegment2StartDate = new DateTime(route2.getEtas().get(j - 1));
        DateTime routeSegment2EndDate = new DateTime(route2.getEtas().get(j));

        // Segment 1
        double routeSegment1Length = Converter.metersToNm(route1.getWaypoints().get(i - 1).getPos()
                .distanceTo(route1.getWaypoints().get(i).getPos(), CoordinateSystem.CARTESIAN));

        long routeSegment1MilisecondsLength = routeSegment1EndDate.getMillis() - routeSegment1StartDate.getMillis();

        double routeSegment1SpeedMiliPrNm = routeSegment1Length / routeSegment1MilisecondsLength;

        // Distance travelled in nautical miles
        double distanceTravelledSegment1 = Converter.metersToNm(route1.getWaypoints().get(i).getPos()
                .distanceTo(positionRoute1, CoordinateSystem.CARTESIAN));

        long timeTravelledSegment1 = (long) (distanceTravelledSegment1 / routeSegment1SpeedMiliPrNm);
        DateTime segment1IntersectionTime = routeSegment1StartDate.plus(timeTravelledSegment1);

        // Segment 2
        double routeSegment2Length = Converter.metersToNm(route2.getWaypoints().get(j - 1).getPos()
                .distanceTo(route2.getWaypoints().get(j).getPos(), CoordinateSystem.CARTESIAN));
        long routeSegment2MilisecondsLength = routeSegment2EndDate.getMillis() - routeSegment2StartDate.getMillis();

        double routeSegment2SpeedMiliPrNm = routeSegment2Length / routeSegment2MilisecondsLength;

        // Distance travelled in nautical miles
        double distanceTravelledSegment2 = Converter.metersToNm(route2.getWaypoints().get(j).getPos()
                .distanceTo(positionRoute2, CoordinateSystem.CARTESIAN));

        long timeTravelledSegment2 = (long) (distanceTravelledSegment2 / routeSegment2SpeedMiliPrNm);
        DateTime segment2IntersectionTime = routeSegment2StartDate.plus(timeTravelledSegment2);

        
        if (segment2IntersectionTime.isAfter(segment1IntersectionTime.minusHours(2))
                && segment2IntersectionTime.isBefore(segment1IntersectionTime.plusHours(2))) {
            return true;
        }
        
        System.out.println("Time check failed Segment1: " + segment1IntersectionTime + " vs Segment2: "+ segment2IntersectionTime );
        return false;
    }

    private IntendedRouteFilterMessage intersectionFilter(Route route1, Route route2, int i, int j, Position route1Waypoint1,
            Position route1Waypoint2, Position route2Waypoint1, Position route2Waypoint2) {

        Position intersection = intersection(route1Waypoint1, route1Waypoint2, route2Waypoint1, route2Waypoint2);

        if (intersection != null) {

            System.out.println("We have an intersection");

            if (checkDateInterval(intersection, intersection, route1, route2, i, j)) {
                IntendedRouteFilterMessage message = new IntendedRouteFilterMessage(intersection,
                        "Intersection occurs within 2 hour of eachother", j - 1, j);
                return message;
            }

            // We have an intersection at the following indexes:
            // i and i-1
            // j and j-1

            // Now check if time is a factor

            // Compare two date ranges

            // Do we have an overlap?

            // DateTime routeSegment1StartDate = new DateTime(route1.getEtas().get(i - 1));
            // DateTime routeSegment1EndDate = new DateTime(route1.getEtas().get(i));
            //
            // DateTime routeSegment2StartDate = new DateTime(route2.getEtas().get(j - 1));
            // DateTime routeSegment2EndDate = new DateTime(route2.getEtas().get(j));
            //
            // // If route1EndDate is before route2StartDate it's not an issue
            // // Added 2 hours thus if route1 + 2 hours is before route2starts we can move on
            // // if (routeSegment1EndDate.plusHours(2).isBefore(routeSegment2StartDate)) {
            // // System.out.println("routesegment 1 end date plus 2 hours is before route sengment 2 start date");
            // // return null;
            // // }
            // //
            // // // Opposite way
            // //
            // // if (routeSegment2EndDate.plusHours(2).isBefore(routeSegment1StartDate)) {
            // // System.out.println("route segment 2 end date plus 2 hours is before route segment 1 start date");
            // // return null;
            // // }
            //
            // // Determine when intersection occurs
            // // Interpolate the time for both route segments
            //
            // // Determine the speed used between the waypoints, as we cannot know how the sender is calculating their ETAS
            // (planned
            // // vs. actual speed) we can't rely on leg speed
            //
            // // Segment 1
            // double routeSegment1Length = Converter.metersToNm(route1.getWaypoints().get(i - 1).getPos()
            // .distanceTo(route1.getWaypoints().get(i).getPos(), CoordinateSystem.CARTESIAN));
            //
            // long routeSegment1MilisecondsLength = routeSegment1EndDate.getMillis() - routeSegment1StartDate.getMillis();
            //
            // double routeSegment1SpeedMiliPrNm = routeSegment1Length / routeSegment1MilisecondsLength;
            //
            // // Distance travelled in nautical miles
            // double distanceTravelledSegment1 = Converter.metersToNm(route1.getWaypoints().get(i).getPos()
            // .distanceTo(intersection, CoordinateSystem.CARTESIAN));
            //
            // long timeTravelledSegment1 = (long) (distanceTravelledSegment1 / routeSegment1SpeedMiliPrNm);
            // DateTime segment1IntersectionTime = routeSegment1StartDate.plus(timeTravelledSegment1);
            //
            // // Segment 2
            // double routeSegment2Length = Converter.metersToNm(route2.getWaypoints().get(j - 1).getPos()
            // .distanceTo(route2.getWaypoints().get(j).getPos(), CoordinateSystem.CARTESIAN));
            // long routeSegment2MilisecondsLength = routeSegment2EndDate.getMillis() - routeSegment2StartDate.getMillis();
            //
            // double routeSegment2SpeedMiliPrNm = routeSegment2Length / routeSegment2MilisecondsLength;
            //
            // // Distance travelled in nautical miles
            // double distanceTravelledSegment2 = Converter.metersToNm(route2.getWaypoints().get(j).getPos()
            // .distanceTo(intersection, CoordinateSystem.CARTESIAN));
            //
            // long timeTravelledSegment2 = (long) (distanceTravelledSegment2 / routeSegment2SpeedMiliPrNm);
            // DateTime segment2IntersectionTime = routeSegment2StartDate.plus(timeTravelledSegment2);
            //
            // // Are segment1IntersectionTime and segment2IntersectionTime within X hour of each other in either way
            //
            // System.out.println("segment1IntersectionTime is" + segment1IntersectionTime);
            // System.out.println("segment2IntersectionTime is " + segment2IntersectionTime);
            //
            // // Is segment2 within +&- x hours
            //
            // // Is segment2 larger than segment1 minus 2 and smaller than segment1 plus 2
            //
            // if (segment2IntersectionTime.isAfter(segment1IntersectionTime.minusHours(2))
            // && segment2IntersectionTime.isBefore(segment1IntersectionTime.plusHours(2))) {
            //
            // IntendedRouteFilterMessage message = new IntendedRouteFilterMessage(intersection,
            // "Intersection occurs within 2 hour of eachother", j - 1, j);
            //
            // System.out.println("Intersection occurs within 2 hour of eachother");
            //
            // // filteredIntendedRoute.getFilterMessages().add(message);
            // // this.intersectPositions.add(intersection);
            // return message;
            // }

        }

        return null;
    }

    public List<Position> getIntersectPositions() {
        return intersectPositions;
    }

}

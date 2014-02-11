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

import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.Intersection;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
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

    private AisHandlerCommon aisHandler;

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
                }

                
                //Apply different filters here
                
                
                
                
                
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

    private IntendedRouteFilterMessage intersectionFilter(Route route1, Route route2, int i, int j, Position route1Waypoint1,
            Position route1Waypoint2, Position route2Waypoint1, Position route2Waypoint2) {

        Position intersection = intersection(route1Waypoint1, route1Waypoint2, route2Waypoint1, route2Waypoint2);

        if (intersection != null) {

            System.out.println("We have an intersection");

            // We have an intersection at the following indexes:
            // i and i-1
            // j and j-1

            // Now check if time is a factor

            // Compare two date ranges

            // Do we have an overlap?

            DateTime routeSegment1StartDate = new DateTime(route1.getEtas().get(i - 1));
            DateTime routeSegment1EndDate = new DateTime(route1.getEtas().get(i));

            DateTime routeSegment2StartDate = new DateTime(route2.getEtas().get(j - 1));
            DateTime routeSegment2EndDate = new DateTime(route2.getEtas().get(j));

            // If route1EndDate is before route2StartDate it's not an issue
            // Added 2 hours thus if route1 + 2 hours is before route2starts we can move on
//            if (routeSegment1EndDate.plusHours(2).isBefore(routeSegment2StartDate)) {
//                System.out.println("routesegment 1 end date plus 2 hours is before route sengment 2 start date");
//                return null;
//            }
//
//            // Opposite way
//
//            if (routeSegment2EndDate.plusHours(2).isBefore(routeSegment1StartDate)) {
//                System.out.println("route segment 2 end date plus 2 hours is before route segment 1 start date");
//                return null;
//            }

            System.out.println("Time may be of importance");

            // Determine when intersection occurs
            // Interpolate the time for both route segments

            // Segment 1 - Determine what time the intersection happens
            // Speed for that segment in nautical miles pr. hour
            double segment1Speed = route1.getWaypoints().get(i - 1).getOutLeg().getSpeed();

            // Distance travelled in meters
            double distanceTravelledSegment1 = route1.getWaypoints().get(i).getPos()
                    .distanceTo(intersection, CoordinateSystem.CARTESIAN);

            double hoursTravelledSegment1 = Converter.metersToNm(distanceTravelledSegment1) / segment1Speed;

            // Converter hours into miliseconds long (hours to minutes to seconds to miliseconds)
            long milisecondsTravelledSegment1 = ((long) hoursTravelledSegment1) * 60 * 60 * 1000;

            DateTime segment1IntersectionTime = routeSegment1StartDate.plus(milisecondsTravelledSegment1);

            // Segment 2 - Determine what time the intersection happens
            // Speed for that segment in nautical miles pr. hour
            double segment2Speed = route2.getWaypoints().get(j - 1).getOutLeg().getSpeed();

            // Distance travelled in meters
            double distanceTravelledSegment2 = route2.getWaypoints().get(j).getPos()
                    .distanceTo(intersection, CoordinateSystem.CARTESIAN);

            double hoursTravelledSegment2 = Converter.metersToNm(distanceTravelledSegment2) / segment2Speed;

            // Converter hours into miliseconds long (hours to minutes to seconds to miliseconds)
            long milisecondsTravelledSegment2 = ((long) hoursTravelledSegment2) * 60 * 60 * 1000;

            DateTime segment2IntersectionTime = routeSegment2StartDate.plus(milisecondsTravelledSegment2);

            // Are segment1IntersectionTime and segment2IntersectionTime within three hour of each other in either way

            System.out.println("segment1IntersectionTime is" + segment1IntersectionTime);
            System.out.println("segment2IntersectionTime is " + segment2IntersectionTime);

            if (segment1IntersectionTime.plusHours(2).isBefore(segment2IntersectionTime)
                    || segment1IntersectionTime.minusHours(2).isBefore(segment2IntersectionTime)) {

                IntendedRouteFilterMessage message = new IntendedRouteFilterMessage(intersection,
                        "Intersection occurs within 2 hour of eachother", j - 1, j);

                System.out.println("Intersection occurs within 2 hour of eachother");
                System.out.println("segment1IntersectionTime.plusHours(2).isBefore(segment2IntersectionTime) " + segment1IntersectionTime.plusHours(2).isBefore(segment2IntersectionTime));
                System.out.println("segment1IntersectionTime.minusHours(2).isBefore(segment2IntersectionTime) " + segment1IntersectionTime.minusHours(2).isBefore(segment2IntersectionTime));

                // filteredIntendedRoute.getFilterMessages().add(message);
                // this.intersectPositions.add(intersection);
                return message;
            }

        }

        return null;
    }

}

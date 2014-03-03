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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.broadcast.BroadcastListener;
import net.maritimecloud.net.broadcast.BroadcastMessageHeader;

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.enavcloud.intendedroute.IntendedRouteBroadcast;
import dk.dma.epd.common.prototype.model.intendedroute.FilteredIntendedRoute;
import dk.dma.epd.common.prototype.model.intendedroute.FilteredIntendedRoutes;
import dk.dma.epd.common.prototype.model.intendedroute.IntendedRouteFilterMessage;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.notification.GeneralNotification;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dk.dma.epd.common.prototype.notification.NotificationAlert.AlertType;
import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Converter;

/**
 * Intended route service implementation.
 * <p>
 * Listens for intended route broadcasts, and updates the vessel target when one is received.
 */
public abstract class IntendedRouteHandlerCommon extends EnavServiceHandlerCommon {

    /**
     * Time an intended route is considered valid without update
     */
    public static final long ROUTE_TTL = 10 * 60 * 1000; // 10 min

    /**
     * In nautical miles - distance between two lines for it to be put in filter
     */
    public static final double FILTER_DISTANCE_EPSILON = 0.5;

    /**
     * In minutes - how close should the warning point be in time
     */
    public static final int FILTER_TIME_EPSILON = 120;

    public static final double NOTIFICATION_DISTANCE_EPSILON = 1; // Nautical miles
    public static final int NOTIFICATION_TIME_EPSILON = 30; // Minutes
    public static final double ALERT_DISTANCE_EPSILON = 0.5; // Nautical miles
    public static final int ALERT_TIME_EPSILON = 10; // Minutes

    protected ConcurrentHashMap<Long, IntendedRoute> intendedRoutes = new ConcurrentHashMap<>();
    protected FilteredIntendedRoutes filteredIntendedRoutes = new FilteredIntendedRoutes();

    protected List<IIntendedRouteListener> listeners = new CopyOnWriteArrayList<>();

    private List<Position> intersectPositions = new ArrayList<Position>();

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

        IntendedRoute intendedRoute = new IntendedRoute(r.getRoute());
        intendedRoute.setMmsi(mmsi);

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

    /****************************************/
    /** Intended route filtering **/
    /****************************************/

    /**
     * Update all filters
     */
    protected abstract void updateFilter();

    /**
     * Update filter with new intended route
     * 
     * @param route
     */
    protected abstract void applyFilter(IntendedRoute route);

    /**
     * Check if notifications should be generated based on a re-computed set of filtered intended routes
     * 
     * @param oldFilteredRoutes
     *            the old set of filtered routes
     * @param newFilteredRoutes
     *            the new set of filtered routes
     */
    protected void checkGenerateNotifications(FilteredIntendedRoutes oldFilteredRoutes,
            FilteredIntendedRoutes newFilteredRoutes) {
        for (FilteredIntendedRoute filteredIntendedRoute : newFilteredRoutes.values()) {
            checkGenerateNotifications(oldFilteredRoutes, filteredIntendedRoute);
        }
    }

    /**
     * Check if a notification should be generated based on a new filtered intended route
     * 
     * @param oldFilteredRoutes
     *            the old set of filtered routes
     * @param newFilteredRoute
     *            the new filtered route
     */
    protected void checkGenerateNotifications(FilteredIntendedRoutes oldFilteredRoutes,
            FilteredIntendedRoute newFilteredRoute) {
        FilteredIntendedRoute oldFilteredRoute = oldFilteredRoutes.get(newFilteredRoute.getMmsi1(), newFilteredRoute.getMmsi2());

        // NB: For now, we add a notification when a new filtered intended route surfaces
        // and it is within a certain amount of time and distance.
        // In the future add a more fine-grained comparison
        boolean sendNotification;
        if (oldFilteredRoute == null) {
            sendNotification = true;
        } else {
            newFilteredRoute.setGeneratedNotification(oldFilteredRoute.hasGeneratedNotification());
            sendNotification = !newFilteredRoute.hasGeneratedNotification()
                    && newFilteredRoute.isWithinRange(NOTIFICATION_DISTANCE_EPSILON, NOTIFICATION_TIME_EPSILON);
        }

        if (sendNotification) {
            newFilteredRoute.setGeneratedNotification(true);
            GeneralNotification notification = new GeneralNotification(newFilteredRoute, 
                    String.format("IntendedRouteNotificaiton_%s_%d",
                            newFilteredRoute.getKey(), System.currentTimeMillis()));
            notification.setTitle("TCPA Warning");
            notification.setDescription(formatNotificationDescription(newFilteredRoute));
            if (newFilteredRoute.isWithinRange(ALERT_DISTANCE_EPSILON, ALERT_TIME_EPSILON)) {
                notification.setSeverity(NotificationSeverity.ALERT);
                notification.addAlerts(new NotificationAlert(AlertType.POPUP, AlertType.BEEP));
            } else {
                notification.setSeverity(NotificationSeverity.WARNING);
                notification.addAlerts(new NotificationAlert(AlertType.POPUP));
            }
            notification.setLocation(newFilteredRoute.getFilterMessages().get(0).getPosition1());
            EPD.getInstance().getNotificationCenter().addNotification(notification);
        }
    }

    /**
     * Composes the description to include in a TCPA warning notification
     * 
     * @param filteredIntendedRoute the filtered route to format the description for
     * @return the notification description
     */
    protected abstract String formatNotificationDescription(FilteredIntendedRoute filteredIntendedRoute);
    
    /**
     * Returns the MMSI associated with the given route.
     * <p>
     * The default implementation assumes that only intended routes passed along,
     * but the ship-implementation will override to handle active routes.
     * 
     * @param route the route to return the MMSI for
     * @return the MMSI associated with the given route
     */
    public Long getMmsi(Route route) {
       return ((IntendedRoute)route).getMmsi(); 
    }
    
    /**
     * Finds the TCPA for two routes and returns the corresponding {@linkplain FilteredIntendedRoute}.
     * <p>
     * This method is only valid if the current start way point of route 1 is before route 2.
     * 
     * @param route1
     * @param route2
     * @return
     */
    protected FilteredIntendedRoute findTCPA(Route route1, Route route2) {

        // Focus on time
        FilteredIntendedRoute filteredIntendedRoute = new FilteredIntendedRoute(
                getMmsi(route1), 
                getMmsi(route2));

        // We need to check if there's a previous waypoint, ie. we are either starting navigating or are between two waypoints
        // int route1StartWp = route1.getActiveWpIndex();
        // int route2StartWp = route2.getActiveWpIndex();

        int route1StartWp = 0;
        int route2StartWp = 0;

        int route1ActiveWp = 0;

        if (route1 instanceof IntendedRoute) {
            route1StartWp = ((IntendedRoute) route1).getActiveWpIndex();
            route1ActiveWp = ((IntendedRoute) route1).getActiveWpIndex();
        }

        if (route2 instanceof IntendedRoute) {
            route2StartWp = ((IntendedRoute) route2).getActiveWpIndex();

        }
        
        
        
        if (route1 instanceof ActiveRoute) {
            route1StartWp = ((ActiveRoute) route1).getActiveWaypointIndex();
            route1ActiveWp = ((ActiveRoute) route1).getActiveWaypointIndex();
        }

        if (route2 instanceof ActiveRoute) {
            route2StartWp = ((ActiveRoute) route2).getActiveWaypointIndex();

        }
        
        

        if (route1StartWp > 0) {
            route1StartWp = route1StartWp - 1;
        }

        if (route2StartWp > 0) {
            route2StartWp = route2StartWp - 1;
        }

        // Should the comparison even be made

        DateTime route1Start = new DateTime(route1.getEtas().get(route1StartWp));
        DateTime route1End = new DateTime(route1.getEtas().get(route1.getEtas().size() - 1));

        DateTime route2Start = new DateTime(route2.getEtas().get(route2StartWp));
        DateTime route2End = new DateTime(route2.getEtas().get(route2.getEtas().size() - 1));

        // The route dates does not overlap, return immediately
        if (route2Start.isAfter(route1End) || route1Start.isAfter(route2End)) {
            System.out.println("The route dates does not overlap, return immediately");

            System.out.println("Route 1 Start: " + route1Start + " and end: " + route1End);
            System.out.println("Route 2 Start: " + route2Start + " and end: " + route2End);

            return filteredIntendedRoute;
        }

        // Find first point in time that they have in common
        if (route1Start.isBefore(route2Start)) {
            // Route1 starts first
            // Thus route2 start must be first common timeslot

            // Find location for both at route2start time
            // route2Start

            // Location for route2 is given from
            Position route2StartPos = route2.getWaypoints().get(route2StartWp).getPos();

            DateTime route1WpStart = null;
            DateTime route1WpEnd;

            boolean foundSegment = false;

            int i;
            for (i = route1ActiveWp; i < route1.getWaypoints().size(); i++) {
                if (i > 0) {
                    route1WpStart = new DateTime(route1.getEtas().get(i - 1));
                    route1WpEnd = new DateTime(route1.getEtas().get(i));

                    if (route1WpStart.isBefore(route2Start) && route1WpEnd.isAfter(route2Start)) {
                        // We have the found the segment we need to start from

                        System.out.println("Found segment");
                        foundSegment = true;
                        break;
                    }
                }
            }

            if (foundSegment) {

                // Now find position at time of route2Start

                System.out.println("Route 1 WP Start is at " + route1.getEtas().get(i - 1));
                System.out.println("Route 2 Start is at " + route2.getEtas().get(route2StartWp));

                // How long will we have travelled along our route (route 1)
                long timeTravelledSeconds = (route2Start.getMillis() - route1WpStart.getMillis()) / 1000;

                double speedInLeg = route1.getWaypoints().get(i - 1).getOutLeg().getSpeed();

                System.out.println("We have travelled for how many minutes " + timeTravelledSeconds / 60 + " at speed "
                        + speedInLeg);

                double distanceTravelled = Calculator.distanceAfterTimeMph(speedInLeg, timeTravelledSeconds);

                System.out.println("We have travelled " + distanceTravelled + " nautical miles in direction: "
                        + route1.getWaypoints().get(i - 1).calcBrg());

                Position position = Calculator.findPosition(route1.getWaypoints().get(i - 1).getPos(),
                        route1.getWaypoints().get(i - 1).calcBrg(), Converter.nmToMeters(distanceTravelled));

                System.out.println("Difference start pos" + route1.getWaypoints().get(i - 1).getPos() + " vs " + position);

                System.out.println("The distance between points is "
                        + Converter.metersToNm(position.distanceTo(route2StartPos, CoordinateSystem.CARTESIAN)));

                // intersectPositions.add(position);
                //
                // intersectPositions.add(route2StartPos);

                // In nautical miles
                // double route1SegmentTraversed = distanceTravelled;
                // double route2SegmentTraversed = 0;

                // Okay so we are in position and in route2StartPos
                // We must start traversing the route now, assume straight lines, for each traversing check the distance between
                // points

                // Adaptive traversing, start with time slots of 10 minutes, if distance between points is smaller than x
                // Or if distance is simply decreasing, reduce time traversing.
                // If the distance becomes below the threshold locate point where it starts

                // Ensure that we do not traverse beyond the length of the route

                // We need to switch segments at some point - wait with that

                Position route1CurrentPosition = position;
                Position route2CurrentPosition = route2StartPos;

                int route1CurrentWaypoint = i - 1;
                int route2CurrentWaypoint = route2StartWp;

                double route1SegmentSpeed = route1.getWaypoints().get(route1CurrentWaypoint).getOutLeg().getSpeed();
                double route2SegmentSpeed = route2.getWaypoints().get(route2CurrentWaypoint).getOutLeg().getSpeed();

                double route1Bearing = route1.getWaypoints().get(route1CurrentWaypoint).calcBrg();
                double route2Bearing = route2.getWaypoints().get(route2CurrentWaypoint).calcBrg();

                DateTime traverseTime = route2Start;

                DateTime route1SegmentEnd = new DateTime(route1.getEtas().get(route1CurrentWaypoint + 1));
                DateTime route2SegmentEnd = new DateTime(route2.getEtas().get(route2CurrentWaypoint + 1));

                while (true) {

                    double currentDistance = Converter.metersToNm(route1CurrentPosition.distanceTo(route2CurrentPosition,
                            CoordinateSystem.CARTESIAN));

                    if (currentDistance < FILTER_DISTANCE_EPSILON) {
                        IntendedRouteFilterMessage filterMessage = new IntendedRouteFilterMessage(route1CurrentPosition,
                                route2CurrentPosition, "Warning stuff", 0, 0);

                        filterMessage.setTime1(traverseTime);
                        filterMessage.setTime2(traverseTime);

                        filteredIntendedRoute.getFilterMessages().add(filterMessage);
                        System.out.println("Adding warning");
                    } else {
                        System.out.println("Found distance of " + currentDistance + " at " + traverseTime);
                    }

                    // We start in this position

                    // route1CurrentPosition
                    // route2CurrentPosition
                    // At this time
                    // route2Start

                    // System.out.println("We start at time " + traverseTime);
                    traverseTime = traverseTime.plusMinutes(1);

                    // System.out.println("And traverse until " + traverseTime);

                    double route1DistanceToTravel = Calculator.distanceAfterTimeMph(route1SegmentSpeed, 60);

                    // System.out.println("We travel " + route1DistanceToTravel + " miles for route 1");

                    double route2DistanceToTravel = Calculator.distanceAfterTimeMph(route2SegmentSpeed, 60);

                    // Position route1NextPosition = traverseLine(route1CurrentPosition, route1Bearing, distanceTravelledRoute1);
                    // Position route2NextPosition = traverseLine(route2CurrentPosition, route2Bearing, distanceTravelledRoute2);

                    if (route1.getWaypoints().get(route1CurrentWaypoint).getHeading() == Heading.RL) {
                        route1CurrentPosition = traverseLine(route1CurrentPosition, route1Bearing, route1DistanceToTravel);
                    }

                    if (route2.getWaypoints().get(route2CurrentWaypoint).getHeading() == Heading.RL) {
                        route2CurrentPosition = traverseLine(route2CurrentPosition, route2Bearing, route2DistanceToTravel);
                    }

                    if (route1.getWaypoints().get(route1CurrentWaypoint).getHeading() == Heading.GC) {
                        route1CurrentPosition = traverseLine(route1CurrentPosition,
                                route1.getWaypoints().get(route1CurrentWaypoint + 1).getPos(), route1DistanceToTravel);
                    }

                    if (route2.getWaypoints().get(route2CurrentWaypoint).getHeading() == Heading.GC) {
                        route2CurrentPosition = traverseLine(route2CurrentPosition,
                                route2.getWaypoints().get(route2CurrentWaypoint + 1).getPos(), route2DistanceToTravel);
                    }

                    // route1SegmentTraversed = route1SegmentTraversed + route1DistanceToTravel;
                    // route2SegmentTraversed = route2SegmentTraversed + route2DistanceToTravel;

                    // if (route1SegmentTraversed > Converter.milesToNM(route1.getWaypoints().get(route1CurrentWaypoint).calcRng()))
                    // {
                    if (traverseTime.isAfter(route1SegmentEnd)) {

                        // System.out.println("We have traversed " + route1SegmentTraversed + " nautical miles");
                        System.out.println("We are at waypoint id  " + route1CurrentWaypoint + " and the route has a total of "
                                + route1.getWaypoints().size() + " waypoints");
                        // We are done with current leg, is there a next one?

                        // No more waypoints - terminate zero indexing and last waypoint does not have an out leg thus -2
                        if (route1CurrentWaypoint == route1.getWaypoints().size() - 2) {
                            System.out.println("We are breaking - route 1 is done");
                            break;
                        } else {
                            System.out.println("SWITCHING LEG FOR ROUTE 1, current bearing is ");
                            // Switch to next leg
                            route1CurrentWaypoint++;

                            System.out.println("We are now at waypoint " + route1CurrentWaypoint);

                            route1CurrentPosition = route1.getWaypoints().get(route1CurrentWaypoint).getPos();
                            route1SegmentSpeed = route1.getWaypoints().get(route1CurrentWaypoint).getOutLeg().getSpeed();
                            route1Bearing = route1.getWaypoints().get(route1CurrentWaypoint).calcBrg();
                            route1SegmentEnd = new DateTime(route1.getEtas().get(route1CurrentWaypoint + 1));

                            // Skip to next WP start traverse
                            // traverseTime = new DateTime(route1.getEtas().get(route1CurrentWaypoint));
                        }
                    }

                    // if (route2SegmentTraversed > Converter.milesToNM(route2.getWaypoints().get(route2CurrentWaypoint).calcRng()))
                    // {
                    if (traverseTime.isAfter(route2SegmentEnd)) {

                        // System.out.println("ROUTE 2: We have traversed " + route2SegmentTraversed + " nautical miles out of "
                        // + Converter.milesToNM(route2.getWaypoints().get(route2CurrentWaypoint).calcRng()));
                        System.out.println("We are at waypoint id  " + route2CurrentWaypoint + " and the route has a total of "
                                + route2.getWaypoints().size() + " waypoints");

                        // No more waypoints - terminate
                        if (route2CurrentWaypoint == route2.getWaypoints().size() - 2) {
                            System.out.println("We are breaking - route 2 is done");
                            break;
                        } else {
                            System.out.println("SWITCHING LEG FOR ROUTE 1");

                            // Switch to next leg
                            route2CurrentWaypoint++;

                            route2CurrentPosition = route2.getWaypoints().get(route2CurrentWaypoint).getPos();
                            route2SegmentSpeed = route2.getWaypoints().get(route2CurrentWaypoint).getOutLeg().getSpeed();
                            route2Bearing = route2.getWaypoints().get(route2CurrentWaypoint).calcBrg();
                            route2SegmentEnd = new DateTime(route2.getEtas().get(route2CurrentWaypoint + 1));

                            // Skip to next WP start traverse
                            // traverseTime = new DateTime(route2.getEtas().get(route2CurrentWaypoint));
                        }

                    }

                }
            } else {
                System.out.println("No segment was found - not sure how we reached this point...");
            }

        } else {
            // Route2 starts first
            // Thus route1 start must be first common timeslot

        }

        // findPosition(Position startingLocation, Position endLocation, double distanceTravelled){

        return filteredIntendedRoute;
    }

    /**
     * Rhumb line traversing
     * 
     * @param startPosition
     * @param bearing
     * @param distanceTravelled
     * @return
     */
    private Position traverseLine(Position startPosition, double bearing, double distanceTravelled) {

        // How long will we have travelled along our route (route 1)
        // long timeTravelledSeconds = minutes * 60;

        // double distanceTravelled = Calculator.distanceAfterTimeMph(speed, timeTravelledSeconds);

        Position position = Calculator.findPosition(startPosition, bearing, Converter.nmToMeters(distanceTravelled));

        return position;
    }

    /**
     * Great Circle Traversing
     * 
     * @param startPosition
     * @param bearing
     * @param distanceTravelled
     * @return
     */
    private Position traverseLine(Position startPosition, Position endPosition, double distanceTravelled) {

        // How long will we have travelled along our route (route 1)
        // long timeTravelledSeconds = minutes * 60;

        // double distanceTravelled = Calculator.distanceAfterTimeMph(speed, timeTravelledSeconds);

        Position position = Calculator.findPosition(startPosition, endPosition, Converter.nmToMeters(distanceTravelled));

        return position;
    }


    public ConcurrentHashMap<Long, IntendedRoute> getIntendedRoutes() {
        return intendedRoutes;
    }

    public FilteredIntendedRoutes getFilteredIntendedRoutes() {
        return filteredIntendedRoutes;
    }

    public List<Position> getIntersectPositions() {
        return intersectPositions;
    }

}

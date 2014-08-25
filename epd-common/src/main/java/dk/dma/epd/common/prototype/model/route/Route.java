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
package dk.dma.epd.common.prototype.model.route;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.voyage.Waypoint;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.util.Converter;
import dk.frv.enav.common.xml.metoc.MetocForecast;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Route class
 */
public class Route implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * List of waypoints for route
     */
    protected LinkedList<RouteWaypoint> waypoints = new LinkedList<>();
    /**
     * Optional name for route
     */
    protected String name;
    /**
     * Name of departure
     */
    protected String departure;
    /**
     * Name of destination
     */
    protected String destination;
    /**
     * Route visible
     */
    protected boolean visible = true;
    /**
     * Start time for route
     */
    protected Date starttime;
    /**
     * List of calculated TTG's for each leg
     */
    protected long[] ttgs;
    /**
     * List of calculated DTG's for each leg
     */
    protected double[] dtgs;
    /**
     * Total TTG
     */
    protected Long totalTtg;
    /**
     * Total DTG
     */
    protected Double totalDtg;
    /**
     * 
     */
    protected List<Date> etas;
    /**
     * METOC forecast for the route
     */
    protected MetocForecast metocForecast;
    /**
     * The starttime used for the current metoc
     */
    protected Date metocStarttime;
    /**
     * The eta used for the current metoc
     */
    protected Date metocEta;

    /**
     * Settings for the route metoc
     */
    protected RouteMetocSettings routeMetocSettings;

    protected boolean safeHaven = true;

    protected boolean stccApproved;

    protected long strategicRouteId;

    protected EtaCalculationType etaCalculationType = EtaCalculationType.PLANNED_SPEED;

    public Route() {

    }

    /**
     * Copy constructor, performs a shallow copy.
     * 
     * @param orig
     *            Original route to copy
     */
    public Route(Route orig) {
        this.waypoints = new LinkedList<>(orig.waypoints);
        this.name = orig.name;
        this.departure = orig.departure;
        this.destination = orig.destination;
        this.visible = orig.visible;
        this.starttime = orig.starttime;
        this.ttgs = orig.ttgs;
        this.dtgs = orig.dtgs;
        this.totalTtg = orig.totalTtg;
        this.totalDtg = orig.totalDtg;
        this.etas = orig.etas;
        this.metocForecast = orig.metocForecast;
        this.metocStarttime = orig.metocStarttime;
        this.metocEta = orig.metocEta;
        this.routeMetocSettings = orig.routeMetocSettings;
        this.strategicRouteId = orig.strategicRouteId;
    }

    public Route(dk.dma.enav.model.voyage.Route cloudRouteData) {
        parseRoute(cloudRouteData);
    }

    // Methods

    public void setSpeed(double SOG) {
        for (int i = 0; i < waypoints.size(); i++) {
            waypoints.get(i).setSpeed(SOG);
        }
        this.calcAllWpEta();
    }

    /**
     * Performs a deep copy of a route.
     */
    public Route copy() {

        Route newRoute = new Route();
        
        LinkedList<RouteWaypoint> waypoints = new LinkedList<>();
        for (RouteWaypoint routeWaypoint : this.waypoints) {
            RouteWaypoint newRouteWaypoint = routeWaypoint.copy();
            waypoints.add(newRouteWaypoint);
        }
        // Iterates through the list of waypoints beginning at the second
        // waypoint in the route, while
        // creating route legs in a backward fashion. The values of the original
        // leg is copied also.
        // Perhaps this can be done more efficiently with a copy constructor (or
        // method) for route legs, but
        // forward referencing a waypoint which has not been created has to be
        // solved in some way...
        for (int i = 0; i < waypoints.size(); i++) {
            RouteWaypoint currWaypoint = waypoints.get(i);

            RouteWaypoint nextWaypoint = null;

            if (i + 1 < waypoints.size()) {
                nextWaypoint = waypoints.get(i + 1);
            }

            RouteLeg routeLeg;

            if (this.waypoints.get(i).getOutLeg() != null) {
                routeLeg = this.waypoints.get(i).getOutLeg();
            } else {
                routeLeg = this.waypoints.get(i).getInLeg();
            }

            RouteLeg newRouteLeg = new RouteLeg();

            newRouteLeg.setSpeed(routeLeg.getSpeed());
            newRouteLeg.setHeading(routeLeg.getHeading());
            newRouteLeg.setXtdStarboard(routeLeg.getXtdStarboard());
            newRouteLeg.setXtdPort(routeLeg.getXtdPort());
            newRouteLeg.setSFLen(routeLeg.getSFLen());

            newRouteLeg.setStartWp(currWaypoint);
            newRouteLeg.setEndWp(nextWaypoint);

            currWaypoint.setOutLeg(newRouteLeg);

            if (nextWaypoint != null) {
                nextWaypoint.setInLeg(newRouteLeg);
            }
        }

        newRoute.setWaypoints(waypoints);
        // Immutable objects are safe to copy this way?
        newRoute.name = this.name;
        newRoute.departure = this.departure;
        newRoute.destination = this.destination;
        newRoute.visible = this.visible;
        newRoute.stccApproved = this.stccApproved;
        newRoute.safeHaven = this.safeHaven;
        newRoute.setEtaCalculationType(etaCalculationType);

        newRoute.starttime = this.starttime == null ? null : new Date(this.starttime.getTime());

        newRoute.etas = (etas != null) ? new ArrayList<Date>(etas) : new ArrayList<Date>();
        newRoute.ttgs = (this.ttgs != null) ? Arrays.copyOf(this.ttgs, this.ttgs.length) : null;
        newRoute.dtgs = (this.dtgs != null) ? Arrays.copyOf(this.dtgs, this.dtgs.length) : null;
        newRoute.totalTtg = this.totalTtg;
        newRoute.totalDtg = this.totalDtg;
        
        return newRoute;
    }

    /**
     * Performs a deep reverse of a route.
     */
    public Route reverse() {
        Route newRoute = new Route();
        LinkedList<RouteWaypoint> waypoints = new LinkedList<>();

        int routeSize = this.waypoints.size() - 1;
        int j = 0;

        for (int i = routeSize; i > -1; i--) {
            RouteWaypoint newRouteWaypoint = this.waypoints.get(i).copy();
            newRouteWaypoint.setName(this.waypoints.get(j).getName()); // Do we
                                                                       // want
                                                                       // to
                                                                       // reverse
                                                                       // the
                                                                       // name
                                                                       // too?
            waypoints.add(newRouteWaypoint);
            j++;
        }

        // Iterates through the list of waypoints beginning at the second
        // waypoint in the route, while
        // creating route legs in a backward fashion. The values of the original
        // leg is copied also.
        // Perhaps this can be done more efficiently with a copy constructor (or
        // method) for route legs, but
        // forward referencing a waypoint which has not been created has to be
        // solved in some way...
        for (int i = 1; i < waypoints.size(); i++) {
            RouteWaypoint currWaypoint = waypoints.get(i);
            RouteWaypoint prevWaypoint = waypoints.get(i - 1);
            RouteLeg routeLeg = this.waypoints.get(i).getInLeg();

            RouteLeg newRouteLeg = new RouteLeg();
            newRouteLeg.setSpeed(routeLeg.getSpeed());
            newRouteLeg.setHeading(routeLeg.getHeading());
            newRouteLeg.setXtdStarboard(routeLeg.getXtdStarboard());
            newRouteLeg.setXtdPort(routeLeg.getXtdPort());

            newRouteLeg.setStartWp(prevWaypoint);
            newRouteLeg.setEndWp(currWaypoint);

            prevWaypoint.setOutLeg(newRouteLeg);
            currWaypoint.setInLeg(newRouteLeg);
        }
        newRoute.setWaypoints(waypoints);

        // Immutable objects are safe to copy this way?
        newRoute.name = this.name;
        newRoute.departure = this.destination;
        newRoute.destination = this.departure;
        newRoute.visible = this.visible;
        newRoute.safeHaven = this.safeHaven;
        newRoute.setEtaCalculationType(etaCalculationType);

        adjustStartTime();
        calcValues(true);

        return newRoute;
    }

    // Calculated measures

    public Double getWpRng(int index) {
        calcValues();
        if (isLastWaypoint(index)) {
            return null;
        }

        return dtgs[index];
    }

    /**
     * @return the stccApproved
     */
    public boolean isStccApproved() {
        return stccApproved;
    }

    /**
     * @param stccApproved
     *            the stccApproved to set
     */
    public void setStccApproved(boolean stccApproved) {
        this.stccApproved = stccApproved;

        if (!stccApproved) {
            strategicRouteId = -1;
        }
    }

    /**
     * @return the strategic route id
     */
    public long getStrategicRouteId() {
        return strategicRouteId;
    }

    /**
     * @param strategicRouteId
     *            the strategic route id
     */
    public void setStrategicRouteId(long strategicRouteId) {
        this.strategicRouteId = strategicRouteId;
    }

    public double getWpRngSum(int index) {
        double sum = 0;
        for (int i = 0; i < index && i < dtgs.length; i++) {
            sum += dtgs[i];
        }
        return sum;
    }

    public Double getWpBrg(RouteWaypoint routeWaypoint) {
        return routeWaypoint.calcBrg();
    }

    public Long getWpTtg(int index) {
        calcValues();
        if (index <= 0) {
            return null;
        }
        return ttgs[index - 1];
    }

    public Date getWpEta(int index) {
        calcValues();
        return etas.get(index);
    }

    public List<Date> getEtas() {
        if (etas == null) {
            calcValues();
        }

        return etas;
    }

    // Getters and setters from here

    public LinkedList<RouteWaypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(LinkedList<RouteWaypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
        calcValues(true);
    }

    public void adjustStartTime() {
        if (starttime == null) {
            setStarttime(PntTime.getDate());
        }
    }

    public MetocForecast getMetocForecast() {
        return metocForecast;
    }

    public void setMetocForecast(MetocForecast metocForecast) {
        this.metocForecast = metocForecast;
        this.metocStarttime = getStarttime();
        this.metocEta = getEta();
    }

    /**
     * Returns true if not drifted to far from plan
     * 
     * @param tolerance
     *            in minutes
     * @return
     */
    public boolean isMetocValid(long tolerance) {
        Date eta = getEta();
        return isMetocValid(eta, tolerance);
    }

    protected boolean isMetocValid(Date eta, long tolerance) {

        if (metocStarttime == null || metocEta == null || starttime == null || eta == null) {
            System.out.println("Missing fields for isMetocValid");
            return false;
        }

        // Difference in starttime
        long startimeDiff = Math.abs(starttime.getTime() - metocStarttime.getTime()) / 1000 / 60;
        if (startimeDiff > tolerance) {
            return false;
        }
        // Difference in eta
        long etaDiff = Math.abs(eta.getTime() - metocEta.getTime()) / 1000 / 60;
        if (etaDiff > tolerance) {
            return false;
        }

        return true;
    }

    public void removeMetoc() {
        this.metocForecast = null;
        if (routeMetocSettings != null) {
            routeMetocSettings.setShowRouteMetoc(false);
        }
        this.metocStarttime = null;
        this.metocEta = null;
    }

    public RouteMetocSettings getRouteMetocSettings() {
        return routeMetocSettings;
    }

    public void setRouteMetocSettings(RouteMetocSettings routeMetocSettings) {
        this.routeMetocSettings = routeMetocSettings;
    }

    public Long getRouteTtg() {
        calcValues();
        return totalTtg;
    }

    public Date getEta(Date starttime) {
        // Calculate ETA based on given starttime
        Long ttg = getRouteTtg();
        if (ttg == null) {
            return null;
        }
        return new Date(starttime.getTime() + ttg);
    }

    /**
     * Get distance to go i NM
     * 
     * @return
     */
    public Double getRouteDtg() {
        calcValues();
        return totalDtg;
    }

    public Date getEta() {
        if (starttime == null) {
            return null;
        }
        return getEta(starttime);
    }

    protected boolean isLastWaypoint(int index) {
        return index == waypoints.size() - 1;
    }

    public void calcValues(boolean force) {

        if (!force && ttgs != null && etas != null) {
            return;
        }

        totalTtg = 0L;
        totalDtg = 0.0;

        if (waypoints.size() > 0) {

            // Create array TTG's and DTG's array
            ttgs = new long[waypoints.size() - 1];
            dtgs = new double[waypoints.size() - 1];
            // Iterate through legs
            for (int i = 0; i < waypoints.size() - 1; i++) {
                ttgs[i] = waypoints.get(i).getOutLeg().calcTtg();
                totalTtg += ttgs[i];
                dtgs[i] = waypoints.get(i).getOutLeg().calcRng();
                totalDtg += dtgs[i];
            }
            // Calculate ETA for each waypoint
            calcAllWpEta();

        }
    }

    protected void calcValues() {
        calcValues(false);
    }

    public void calcAllWpEta() {
        etas = new ArrayList<>();
        Date etaStart = starttime;
        if (etaStart == null) {
            etaStart = PntTime.getDate();
        }
        long eta = etaStart.getTime();
        etas.add(new Date(eta));
        for (int i = 0; i < waypoints.size() - 1; i++) {
            eta += ttgs[i];
            etas.add(new Date(eta));
        }
    }

    public void adjustEta(int wpIndex, EtaAdjust etaAdjust) {
        Date newEta = etaAdjust.getEta();
        RouteWaypoint wp = waypoints.get(wpIndex);
        Date etaBefore = null;
        Date etaAfter = null;
        if (wpIndex > 0) {
            etaBefore = etas.get(wpIndex - 1);
        }
        if (wpIndex < waypoints.size() - 1) {
            etaAfter = etas.get(wpIndex + 1);
        }

        if (etaAdjust.getType() == EtaAdjustType.ADJUST_ADJACENT_LEG_SPEEDS) {
            // Check that new ETA is between previous and next ETA
            if (etaBefore != null) {
                if (!newEta.after(etaBefore)) {
                    throw new IllegalArgumentException("New ETA is not after previous WP ETA");
                }
            }
            if (etaAfter != null) {
                if (!newEta.before(etaAfter)) {
                    throw new IllegalArgumentException("New ETA is not before next WP ETA");
                }
            }

            // Calculate new speed on incoming leg
            if (etaBefore != null) {
                double t = Converter.millisToHours(newEta.getTime() - etaBefore.getTime());
                double speed = dtgs[wpIndex - 1] / t;
                wp.getInLeg().setSpeed(speed);
            } else {
                // First waypoint, we need to adjust starttime
                this.starttime = newEta;
            }

            // Calculate new speed on outgoing leg
            if (etaAfter != null) {
                double t = Converter.millisToHours(etaAfter.getTime() - newEta.getTime());
                double speed = dtgs[wpIndex] / t;
                wp.getOutLeg().setSpeed(speed);
            }
        } else if (etaAdjust.getType() == EtaAdjustType.ADJUST_ALL_ETA) {
            // Find starttime resulting in wanted ETA
            long deltaT = newEta.getTime() - etas.get(wpIndex).getTime();
            this.starttime = new Date(this.starttime.getTime() + deltaT);
        } else {
            // New must be between ETD and ETA
            if (wpIndex != 0 && !newEta.after(starttime)) {
                throw new IllegalArgumentException("New ETA is not after start time");
            }
            if (!newEta.before(getEta())) {
                throw new IllegalArgumentException("New ETA is not after route ETA");
            }

            // Distance to and from wp
            double distBefore = 0;
            double distAfter = 0;
            for (int i = 0; i < wpIndex; i++) {
                distBefore += dtgs[i];
            }
            for (int i = wpIndex; i < dtgs.length; i++) {
                distAfter += dtgs[i];
            }

            // Time before and after
            double timeBefore = Converter.millisToHours(newEta.getTime() - this.starttime.getTime());
            double timeAfter = Converter.millisToHours(getEta().getTime() - newEta.getTime());

            double speedBefore = distBefore / timeBefore;
            double speedAfter = distAfter / timeAfter;

            // Set leg speeds
            for (int i = 0; i < waypoints.size() - 1; i++) {
                RouteLeg leg = waypoints.get(i).getOutLeg();
                if (i < wpIndex) {
                    leg.setSpeed(speedBefore);
                } else {
                    leg.setSpeed(speedAfter);
                }
            }

            // If first waypoint we have to change starttime
            if (wpIndex == 0) {
                this.starttime = newEta;
            }
        }

        calcValues(true);

    }

    public boolean saveToFile(File file) {
        // TODO
        return false;
    }

    public boolean deleteWaypoint(int index) {
        if (waypoints.size() > 2) {
            for (int i = 0; i < waypoints.size(); i++) {
                if (i == index) {
                    if (isLastWaypoint(i)) {
                        RouteWaypoint before = waypoints.get(i - 1);
                        before.setOutLeg(null);

                        waypoints.remove(i);
                    } else if (i == 0) {
                        RouteWaypoint after = waypoints.get(i + 1);
                        after.setInLeg(null);

                        waypoints.remove(i);
                    } else {
                        RouteWaypoint before = waypoints.get(i - 1);
                        RouteWaypoint after = waypoints.get(i + 1);
                        RouteLeg keeper = before.getOutLeg();

                        keeper.setEndWp(after);
                        after.setInLeg(keeper);

                        waypoints.remove(i);
                    }
                }
            }
            calcValues(true);
            // Update waypoint names to reflect deleted waypoint
            this.renameWayPoints();
        } else {
            // Do nothing
        }
        return false;
    }

    /**
     * Create a waypoint by splitting a RouteLeg
     * 
     * @param routeLeg
     *            Route leg to be split
     * @param position
     *            Geographical position of the new waypoint
     * @param waypointIndex
     *            Index of the legs start waypoint
     */
    public void createWaypoint(RouteLeg routeLeg, Position position) {
        RouteWaypoint previousWaypoint = routeLeg.getStartWp();
        RouteWaypoint nextWaypoint = routeLeg.getEndWp();
        RouteWaypoint newWaypoint = new RouteWaypoint(previousWaypoint);
        RouteLeg newRouteLeg = new RouteLeg(routeLeg);

        // set up legs
        routeLeg.setEndWp(newWaypoint);
        newRouteLeg.setStartWp(newWaypoint);

        // set up waypoints
        newWaypoint.setInLeg(routeLeg);
        newWaypoint.setOutLeg(newRouteLeg);
        newWaypoint.setPos(position);
        newWaypoint.calcRot();

        nextWaypoint.setInLeg(newRouteLeg);

        // find current waypoint index
        RouteWaypoint count = routeLeg.getStartWp();

        int i = 1;
        while (count.getInLeg() != null) {
            i++;
            count = count.getInLeg().getStartWp();
        }

        newWaypoint.setName("WP_" + i);

        // add the waypoint to the linked list in the right position
        waypoints.add(i, newWaypoint);

        renameWayPoints();

        calcValues(true);
    }

    private void renameWayPoints() {

        for (int i = 0; i < waypoints.size(); i++) {

            String name = waypoints.get(i).getName();

            int count = i + 1;

            if (name.contains("WP_")) {
                String wpcountTxt = "";

                if (count < 10) {
                    wpcountTxt = "00" + count;
                }
                if (count >= 10) {
                    wpcountTxt = "0" + count;
                }
                if (count >= 100) {
                    wpcountTxt = "" + count;
                }

                waypoints.get(i).setName("WP_" + wpcountTxt);
            }

        }

    }

    /**
     * Create a waypoint by appending the waypoint to current waypoint
     * 
     * @param waypoint
     *            Waypoint being appended
     * @param position
     *            Geographical position of the new waypoint
     * @return New appended waypoint
     */
    public RouteWaypoint createWaypoint(RouteWaypoint waypoint, Position position) {
        // Is the last waypoint
        RouteWaypoint wp = null;
        RouteLeg leg = null;
        if (waypoint.getOutLeg() == null) {
            wp = new RouteWaypoint(waypoint);
            leg = new RouteLeg(waypoint.getInLeg());
            leg.setStartWp(waypoint);
            leg.setEndWp(wp);
            waypoint.setOutLeg(leg);
            wp.setInLeg(leg);
            wp.setOutLeg(null);
        } else if (waypoint.getInLeg() == null) {
            // TODO: Maybe a prepend functionality?
        } else {
            // TODO: Add new waypoint between two waypoints
        }

        if (position != null) {

            wp.setPos(position);

            // Calculate rot
            wp.calcRot();

        }

        return wp;
    }

    public void appendWaypoint() {
        RouteWaypoint lastWaypoint = waypoints.get(waypoints.size() - 1);
        RouteWaypoint nextLastWaypoint = waypoints.get(waypoints.size() - 2);
        Position startPoint = nextLastWaypoint.getPos();
        Position endPoint = lastWaypoint.getPos();

        double slope = (endPoint.getLatitude() - startPoint.getLatitude()) / (endPoint.getLongitude() - startPoint.getLongitude());
        double dx = endPoint.getLongitude() - startPoint.getLongitude();

        double newX = endPoint.getLongitude() + dx;
        double newY = endPoint.getLatitude() + dx * slope;

        RouteWaypoint newWaypoint = createWaypoint(lastWaypoint, Position.create(newY, newX));
        waypoints.add(newWaypoint);
        // Update waypoint names
        newWaypoint.setName("WP_" + this.waypoints.size());
        this.renameWayPoints();

        calcValues(true);
    }

    public boolean isPointWithingBBox(Position point) {
        if (waypoints == null || waypoints.size() == 0) {
            return false;
        }
        double minLat = 90;
        double maxLat = -90;
        double minLon = 180;
        double maxLon = -180;
        for (RouteWaypoint waypoint : waypoints) {
            Position location = waypoint.getPos();
            if (location.getLatitude() < minLat) {
                minLat = location.getLatitude();
            }
            if (location.getLatitude() > maxLat) {
                maxLat = location.getLatitude();
            }
            if (location.getLongitude() < minLon) {
                minLon = location.getLongitude();
            }
            if (location.getLongitude() > maxLon) {
                maxLon = location.getLongitude();
            }
        }

        double pointLongitude = point.getLongitude();
        double pointLatitude = point.getLatitude();
        if (pointLongitude >= minLon && pointLongitude <= maxLon && pointLatitude >= minLat && pointLatitude <= maxLat) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Route [departure=");
        builder.append(departure);
        builder.append(", destination=");
        builder.append(destination);
        builder.append(", name=");
        builder.append(name);
        builder.append(", visible=");
        builder.append(visible);
        builder.append(", waypoints=");
        builder.append(waypoints);
        builder.append("]");
        return builder.toString();
    }

    public long[] getTtgs() {
        return ttgs;
    }

    public void setTtgs(long[] ttgs) {
        this.ttgs = ttgs;
    }

    public double[] getDtgs() {
        return dtgs;
    }

    public void setDtgs(double[] dtgs) {
        this.dtgs = dtgs;
    }

    public Long geRouteTtg() {
        return totalTtg;
    }

    public void setTotalTtg(Long totalTtg) {
        this.totalTtg = totalTtg;
    }

    public void setTotalDtg(Double totalDtg) {
        this.totalDtg = totalDtg;
    }

    public Date getMetocStarttime() {
        return metocStarttime;
    }

    public void setMetocStarttime(Date metocStarttime) {
        this.metocStarttime = metocStarttime;
    }

    public Date getMetocEta() {
        return metocEta;
    }

    public void setMetocEta(Date metocEta) {
        this.metocEta = metocEta;
    }

    public boolean isSafeHaven() {
        return safeHaven;
    }

    public void setSafeHaven(boolean safeHaven) {
        this.safeHaven = safeHaven;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public void setEtas(List<Date> etas) {
        this.etas = etas;
    }

    public dk.dma.enav.model.voyage.Route getFullRouteData() {

        dk.dma.enav.model.voyage.Route voyageRoute = new dk.dma.enav.model.voyage.Route();

        voyageRoute.setName(this.name);

        for (int i = 0; i < getWaypoints().size(); i++) {

            dk.dma.enav.model.voyage.Waypoint voyageWaypoint = new dk.dma.enav.model.voyage.Waypoint();
            RouteWaypoint currentWaypoint = getWaypoints().get(i);

            voyageWaypoint.setName(currentWaypoint.getName());
            voyageWaypoint.setEta(etas.get(i));
            voyageWaypoint.setLatitude(currentWaypoint.getPos().getLatitude());
            voyageWaypoint.setLongitude(currentWaypoint.getPos().getLongitude());

            voyageWaypoint.setRot(currentWaypoint.getRot());
            voyageWaypoint.setTurnRad(currentWaypoint.getTurnRad());

            if (currentWaypoint.getOutLeg() != null) {
                dk.dma.enav.model.voyage.RouteLeg routeLeg = new dk.dma.enav.model.voyage.RouteLeg();
                routeLeg.setSpeed(currentWaypoint.getOutLeg().getSpeed());
                routeLeg.setXtdPort(currentWaypoint.getOutLeg().getXtdPort());
                routeLeg.setXtdStarboard(currentWaypoint.getOutLeg().getXtdStarboard());
                routeLeg.setSFLen(currentWaypoint.getOutLeg().getSFLen());

                voyageWaypoint.setRouteLeg(routeLeg);
            }
            voyageRoute.getWaypoints().add(voyageWaypoint);
        }

        return voyageRoute;
    }

    private void parseRoute(dk.dma.enav.model.voyage.Route cloudRouteData) {
        this.setName(cloudRouteData.getName());
        List<Waypoint> cloudRouteWaypoints = cloudRouteData.getWaypoints();
        LinkedList<RouteWaypoint> routeWaypoints = this.getWaypoints();

        for (int i = 0; i < cloudRouteWaypoints.size(); i++) {

            RouteWaypoint waypoint = new RouteWaypoint();
            Waypoint cloudWaypoint = cloudRouteWaypoints.get(i);

            waypoint.setName(cloudWaypoint.getName());

            // Handle leg
            if (i > 0) {
                RouteWaypoint prevWaypoint = routeWaypoints.get(i - 1);
                RouteLeg leg = new RouteLeg();
                waypoint.setInLeg(leg);
                prevWaypoint.setOutLeg(leg);
                leg.setStartWp(prevWaypoint);
                leg.setEndWp(waypoint);
            }

            Position position = Position.create(cloudWaypoint.getLatitude(), cloudWaypoint.getLongitude());
            waypoint.setPos(position);

            routeWaypoints.add(waypoint);

        }

        if (routeWaypoints.size() > 1) {
            for (int i = 0; i < routeWaypoints.size(); i++) {

                RouteWaypoint waypoint = routeWaypoints.get(i);
                Waypoint cloudWaypoint = cloudRouteWaypoints.get(i);

                // Leg
                if (cloudWaypoint.getRouteLeg() != null && waypoint.getOutLeg() != null) {

                    // SOG
                    if (cloudWaypoint.getRouteLeg().getSpeed() != null) {
                        waypoint.setSpeed(cloudWaypoint.getRouteLeg().getSpeed());
                    }

                    // XTDS
                    if (cloudWaypoint.getRouteLeg().getXtdStarboard() != null) {
                        waypoint.getOutLeg().setXtdStarboard(cloudWaypoint.getRouteLeg().getXtdStarboard());
                    }

                    // XTDP
                    if (cloudWaypoint.getRouteLeg().getXtdPort() != null) {
                        waypoint.getOutLeg().setXtdPort(cloudWaypoint.getRouteLeg().getXtdPort());
                    }

                    // SF Len
                    if (cloudWaypoint.getRouteLeg().getSFLen() != null) {
                        waypoint.getOutLeg().setSFLen(cloudWaypoint.getRouteLeg().getSFLen());
                    }

                    // Heading
                    if (cloudWaypoint.getRouteLeg().getHeading() == dk.dma.enav.model.voyage.RouteLeg.Heading.GC) {
                        waypoint.getOutLeg().setHeading(Heading.GC);
                    } else {
                        waypoint.getOutLeg().setHeading(Heading.RL);
                    }
                }

                if (cloudWaypoint.getTurnRad() != null) {
                    waypoint.setTurnRad(cloudWaypoint.getTurnRad());
                }

                if (cloudWaypoint.getRot() != null) {
                    waypoint.setRot(cloudWaypoint.getRot());
                }
            }
        }

        etas = new ArrayList<>();
        // this.calcAllWpEta();
        for (int i = 0; i < cloudRouteWaypoints.size(); i++) {
            etas.add(cloudRouteWaypoints.get(i).getEta());
        }

        starttime = cloudRouteWaypoints.get(0).getEta();

    }

    public EtaCalculationType getEtaCalculationType() {
        return etaCalculationType;
    }

    public void setEtaCalculationType(EtaCalculationType etaCalculationType) {
        this.etaCalculationType = etaCalculationType;
    }
    
    
    public double getSpeed(int id) {
        if (waypoints.get(id).getOutLeg() != null) {
            return waypoints.get(id).getOutLeg().getSpeed();
        }

        return 0;
    }

    /**
     * Defines the ETA calculation type
     */
    public enum EtaCalculationType {
        PLANNED_SPEED("Planned speed"), DYNAMIC_SPEED("Current speed"), HYBRID("Hybrid");

        private String title;

        private EtaCalculationType(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    public enum EtaAdjustType {
        ADJUST_ADJACENT_LEG_SPEEDS, ADJUST_ALL_ETA, ADJUST_FIXED_START_AND_END;
    }

    public static class EtaAdjust {
        final Date eta;
        final EtaAdjustType type;

        public EtaAdjust(Date eta, EtaAdjustType type) {
            super();
            this.eta = eta;
            this.type = type;
        }

        public Date getEta() {
            return eta;
        }

        public EtaAdjustType getType() {
            return type;
        }

    }



}

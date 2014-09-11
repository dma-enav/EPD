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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.voyage.Waypoint;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.enavcloud.intendedroute.IntendedRouteBroadcast;
import dk.dma.epd.common.prototype.enavcloud.intendedroute.IntendedRouteMessage;
import dk.dma.epd.common.prototype.model.route.PartialRouteFilter.FilterType;
import dk.dma.epd.common.prototype.model.voct.sardata.SearchPatternRoute;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Converter;
import dk.frv.enav.common.xml.metoc.MetocForecast;

/**
 * Class representing an active route
 */
public class ActiveRoute extends Route {

    public enum ActiveWpSelectionResult {
        NO_CHANGE, CHANGED, ROUTE_FINISHED
    };

    private static final long serialVersionUID = 1L;

    /**
     * The index of active waypoint
     */
    protected int activeWaypointIndex;
    /**
     * The current leg
     */
    protected RouteLeg currentLeg;
    /**
     * The current PNT data
     */
    protected PntData currentPntData;
    /**
     * Current speed
     */
    protected double speed;
    /**
     * Range to active waypoint
     */
    protected Double activeWpRng;
    /**
     * Bearing to active waypoint
     */
    protected Double activeWpBrg;
    /**
     * TTG to active waypoint based on current speed
     */
    protected Long speedActiveWpTtg;
    /**
     * A special TTG that cannot be null Assumes that intended speed will be reached soon
     */
    protected Long plannedActiveWpTtg;
    /**
     * Time of original activation
     */
    protected Date origStarttime;
    /**
     * The minimum radius of wp you have to be inside to change active waypoint
     */
    protected double wpCircleMin = 0.2; // 0.2 nm = 365 m

    /**
     * Should relaxed WP change be used
     */
    protected boolean relaxedWpChange = true;

    protected int lastWpCounter;

    // Computed safe haven attributes
    private Position safeHavenLocation;
    private double safeHavenLength;
    private double safeHavenWidth;
    private double safeHavenSpeed;
    protected double safeHavenBearing;

    private Route originalRoute;

    private boolean searchPattern;

    public ActiveRoute(Route route, PntData pntData) {
        super();
        this.waypoints = route.getWaypoints();
        this.name = route.getName();
        this.visible = route.isVisible();
        this.departure = route.getDeparture();
        this.destination = route.getDestination();
        this.starttime = route.getStarttime();

        this.origStarttime = PntTime.getDate();
        this.origStarttime = route.getStarttime();

        this.routeMetocSettings = route.getRouteMetocSettings();
        this.metocForecast = route.getMetocForecast();
        this.originalRoute = route.copy();
        this.etaCalculationType = route.etaCalculationType;

        this.safeHavenLocation = waypoints.get(0).getPos();

        super.calcValues(true);
        super.calcAllWpEta();
        calcValues(true);

        int activeWp = 0;

        if (route instanceof SearchPatternRoute) {
            searchPattern = true;
        } else {
            activeWp = getBestWaypoint(route, pntData);
        }

        changeActiveWaypoint(activeWp);

    }

    /**
     * Performs a deep copy of a route.
     */
    @Override
    public Route copy() {
        Route newRoute = super.copy();

        newRoute.starttime = origStarttime;
        newRoute.etas = originalRoute.getEtas();
        newRoute.etas = new ArrayList<Date>(etas);
        return newRoute;
    }

    /**
     * Get's the most optimal route choice Wwe take bearing and distance into account and select the best match. It will never
     * select a waypoint behind itself.
     */
    private int getBestWaypoint(Route route, PntData pntData) {

        if (pntData == null || pntData.isBadPosition()) {
            return 0;
        }

        double smallestDist = 99999999.0;
        int index = 0;
        for (int i = 0; i <= route.getWaypoints().size() - 1; i++) {
            Position wpPos = route.getWaypoints().get(i).getPos();
            double distance = pntData.getPosition().rhumbLineDistanceTo(wpPos);
            double angle = Math.abs(pntData.getCog() - pntData.getPosition().rhumbLineBearingTo(wpPos));
            if (angle >= 90) {
                continue;
            }
            double weight = Math.cos(Math.toRadians(angle));
            if (weight < 0.1) {
                continue;
            }
            double weightedDistance = distance / weight;
            if (weightedDistance < smallestDist) {
                smallestDist = weightedDistance;
                index = i;
            }
        }
        return index;
    }

    public double getSafeHavenBearing() {
        return safeHavenBearing;
    }

    public double getSafeHavenSpeed() {
        return safeHavenSpeed;
    }

    /**
     * Computes the bearing of the route leg using its start and end position and its heading
     * 
     * @param leg
     *            the leg to compute the bearing for
     * @return the bearing
     */
    private double computeBearing(RouteLeg leg) {
        // Sanity check
        if (leg == null || leg.getStartWp() == null || leg.getEndWp() == null) {
            return 0.0;
        }
        return Calculator.bearing(leg.getStartWp().getPos(), leg.getEndWp().getPos(), leg.getHeading());
    }

    public synchronized Position getSafeHavenLocation() {

        long currentTime = PntTime.getDate().getTime();

        // We haven't begun sailing on the route yet, putting box at first
        // waypoint

        if (currentTime < originalRoute.getStarttime().getTime()) {
            RouteWaypoint wp = originalRoute.getWaypoints().get(0);
            safeHavenBearing = computeBearing(wp.getOutLeg());
            safeHavenLength = wp.getOutLeg().getSFLen();
            safeHavenWidth = wp.getOutLeg().getXtdPortMeters() + wp.getOutLeg().getXtdStarboardMeters();
            safeHavenSpeed = 0;
            safeHavenLocation = wp.getPos();

            return safeHavenLocation;
        } else {

            for (int i = 0; i < originalRoute.getWaypoints().size(); i++) {

                // We haven't found the match so we must be at the end of the
                // route
                if (i == originalRoute.getWaypoints().size() - 1) {
                    safeHavenBearing = computeBearing(originalRoute.getWaypoints().getLast().getInLeg());
                    safeHavenLength = getWaypoints().get(i - 1).getOutLeg().getSFLen();
                    safeHavenWidth = getWaypoints().get(i - 1).getOutLeg().getXtdPortMeters()
                            + getWaypoints().get(i - 1).getOutLeg().getXtdStarboardMeters();
                    safeHavenSpeed = 0;
                    safeHavenLocation = originalRoute.getWaypoints().get(i).getPos();
                    return safeHavenLocation;
                } else {

                    // We should be beyond this
                    if (currentTime > originalRoute.getEtas().get(i).getTime()
                            && currentTime < originalRoute.getEtas().get(i + 1).getTime()) {

                        // How long have we been sailing between these way points?
                        long secondsSailTime = (currentTime - originalRoute.getEtas().get(i).getTime()) / 1000;

                        double distanceTravelledNauticalMiles = Converter.milesToNM(Calculator.distanceAfterTimeMph(originalRoute
                                .getWaypoints().get(i).getOutLeg().getSpeed(), secondsSailTime));

                        if (this.getWaypoints().get(i).getOutLeg().getHeading() == Heading.GC) {
                            safeHavenLocation = Calculator.findPosition(this.getWaypoints().get(i).getPos(), this.getWaypoints()
                                    .get(i + 1).getPos(), Converter.nmToMeters(distanceTravelledNauticalMiles));
                        } else {
                            safeHavenLocation = Calculator.findPosition(this.getWaypoints().get(i).getPos(), this.getWaypoints()
                                    .get(i).getOutLeg().calcBrg(), Converter.nmToMeters(distanceTravelledNauticalMiles));
                        }

                        safeHavenBearing = computeBearing(originalRoute.getWaypoints().get(i).getOutLeg());
                        safeHavenLength = getWaypoints().get(i).getOutLeg().getSFLen();
                        safeHavenWidth = getWaypoints().get(i).getOutLeg().getXtdPortMeters()
                                + getWaypoints().get(i).getOutLeg().getXtdStarboardMeters();
                        safeHavenSpeed = originalRoute.getWaypoints().get(i).getOutLeg().getSpeed();

                        return safeHavenLocation;
                    }
                }
            }
        }
        // An error must have occured
        safeHavenLocation = null;
        return safeHavenLocation;
    }

    public synchronized void update(PntData pntData) {
        if (pntData.isBadPosition() || pntData.getSog() == null) {
            return;
        }

        // Get active waypoint
        RouteWaypoint activeWaypoint = waypoints.get(activeWaypointIndex);
        // Set current PNT data
        currentPntData = pntData;
        // Get speed
        speed = pntData.getSog();

        // Calculate brg and rng to active waypoint
        activeWpRng = Calculator.range(pntData.getPosition(), activeWaypoint.getPos(), currentLeg.getHeading());
        activeWpBrg = Calculator.bearing(pntData.getPosition(), activeWaypoint.getPos(), currentLeg.getHeading());

        // Calculate planned TTG to active waypoint
        plannedActiveWpTtg = Math.round(activeWpRng / currentLeg.getSpeed() * 60 * 60 * 1000);

        // Calculate TTG to active waypoint based on current speed
        // We use a sensible min speed
        if (speed < 0.1) {
            speed = 0.1;
        }
        speedActiveWpTtg = Math.round(activeWpRng / speed * 60 * 60 * 1000);

        // Update ttg, dtg and eta
        calcValues(true);

    }

    @Override
    public synchronized void calcAllWpEta() {
        // Do not recalculate eta for an active route
    }

    @Override
    public synchronized void calcValues(boolean force) {
        if (!force && ttgs != null && etas != null) {
            return;
        }

        if (plannedActiveWpTtg == null || speedActiveWpTtg == null) {
            return;
        }

        // Determine TTG to active waypoint
        long ttg = (this.etaCalculationType == EtaCalculationType.PLANNED_SPEED) ? plannedActiveWpTtg : speedActiveWpTtg;

        totalTtg = ttg;
        totalDtg = activeWpRng;

        for (int i = this.activeWaypointIndex; i < this.waypoints.size() - 1; i++) {
            // Dynamic speed
            if (this.etaCalculationType == EtaCalculationType.DYNAMIC_SPEED
                    || ((this.etaCalculationType == EtaCalculationType.HYBRID) && i == this.activeWaypointIndex)) {
                this.ttgs[i] = this.waypoints.get(i).getOutLeg().calcTtg(this.speed);
            } else {
                // Planned speed
                this.ttgs[i] = this.waypoints.get(i).getOutLeg().calcTtg();
            }
            totalDtg += dtgs[i];
            totalTtg += ttgs[i];
        }

        reCalcRemainingWpEta();
    }

    @Override
    public void setMetocForecast(MetocForecast metocForecast) {
        this.metocForecast = metocForecast;
        this.metocStarttime = getStarttime();
        this.metocEta = getEta();
    }

    @Override
    public boolean isMetocValid(long tolerance) {
        return super.isMetocValid(getEta(), tolerance);
    }

    public synchronized ActiveWpSelectionResult chooseActiveWp() {

        // Calculate if in Wp circle
        boolean inWpCircle = false;
        double xtd = currentLeg.getMaxXtd() == null ? 0.0 : currentLeg.getMaxXtd();
        double radius = Math.max(xtd, wpCircleMin);
        if (activeWpRng != null) {

            if (activeWpRng < radius) {
                inWpCircle = true;
            }

            // If heading for last wp and in circle, we finish route - hack for
            // waiting 1 cycle to check if in circle
            if (isLastWp()) {
                if (lastWpCounter > 0) {
                    if (inWpCircle) {
                        return ActiveWpSelectionResult.ROUTE_FINISHED;
                    } else {
                        return ActiveWpSelectionResult.NO_CHANGE;
                    }
                } else {
                    lastWpCounter++;
                }
                return ActiveWpSelectionResult.NO_CHANGE;
            }

            // Calculate distance from ship to next waypoint
            RouteLeg nextLeg = getActiveWp().getOutLeg();
            double nextWpRng = Calculator.range(currentPntData.getPosition(), nextLeg.getEndWp().getPos(), nextLeg.getHeading());

            if (inWpCircle) {
                // If closer to next wp than the dist between wp's, we change
                if (nextWpRng < getWpRng(activeWaypointIndex)) {
                    changeActiveWaypoint(activeWaypointIndex + 1);
                    return ActiveWpSelectionResult.CHANGED;
                }
            } else {
                // Some temporary fallback when we are really off course and not doing sar
                if (!searchPattern && relaxedWpChange) {
                    if (2 * nextWpRng < getWpRng(activeWaypointIndex)) {
                        changeActiveWaypoint(activeWaypointIndex + 1);
                        return ActiveWpSelectionResult.CHANGED;
                    }
                }
            }
        }
        return ActiveWpSelectionResult.NO_CHANGE;
    }

    public synchronized void changeActiveWaypoint(int index) {
        // Save actual ETA
        etas.set(activeWaypointIndex, PntTime.getDate());
        // Change active waypoint
        activeWaypointIndex = index;
        // Set current leg
        if (index == 0) {
            // When the first waypoint is active, the first leg is used as
            // current leg
            this.currentLeg = waypoints.get(0).getOutLeg();
        } else {
            this.currentLeg = waypoints.get(index).getInLeg();
        }
        reCalcRemainingWpEta();
    }

    public synchronized Date getActiveWaypointEta() {
        Long ttg = (this.etaCalculationType == EtaCalculationType.PLANNED_SPEED) ? plannedActiveWpTtg : speedActiveWpTtg;
        if (ttg == null) {
            return null;
        }

        // If we have just activated the route ie. wp 0, and the route start date is in the future do not recalculate the first eta
        if (getActiveWaypointIndex() == 0) {
            DateTime start = new DateTime(origStarttime);
            if (start.isAfterNow()) {
                return origStarttime;
            }
        }

        return new Date(PntTime.getDate().getTime() + ttg);
    }

    @Override
    public synchronized Long getRouteTtg() {
        return totalTtg;
    }

    @Override
    public synchronized Double getRouteDtg() {
        return totalDtg;
    }

    public synchronized int getActiveWaypointIndex() {
        return activeWaypointIndex;
    }

    public synchronized RouteWaypoint getActiveWp() {
        return waypoints.get(activeWaypointIndex);
    }

    public synchronized boolean reCalcRemainingWpEta() {
        int aw = getActiveWaypointIndex();
        Date eta = getActiveWaypointEta();

        if (eta == null) {
            return false;
        }

        // Set eta at active waypoint
        etas.set(aw, eta);
        long etaTime = eta.getTime();
        for (int i = aw; i < waypoints.size() - 1; i++) {
            etaTime += ttgs[i];
            etas.set(i + 1, new Date(etaTime));
        }
        return true;
    }

    @Override
    public synchronized Date getEta() {
        if (!reCalcRemainingWpEta()) {
            return null;
        }
        return etas.get(etas.size() - 1);
    }

    public synchronized Date getOrigStarttime() {
        return origStarttime;
    }

    @Override
    public void adjustStartTime() {
        // Do not change starttime for active route

    }

    public synchronized boolean isLastWp() {
        return isLastWaypoint(activeWaypointIndex);
    }

    public synchronized Double getActiveWpRng() {
        return activeWpRng;
    }

    public synchronized Double getActiveWpBrg() {
        return activeWpBrg;
    }

    public synchronized Long getActiveWpTtg() {
        return (this.etaCalculationType == EtaCalculationType.PLANNED_SPEED) ? plannedActiveWpTtg : speedActiveWpTtg;
    }

    public synchronized RouteLeg getCurrentLeg() {
        return currentLeg;
    }

    public void setWpCircleMin(double wpCircleMin) {
        this.wpCircleMin = wpCircleMin;
    }

    public void setRelaxedWpChange(boolean relaxedWpChange) {
        this.relaxedWpChange = relaxedWpChange;
    }

    public synchronized dk.dma.enav.model.voyage.Route getFullRouteData() {

        dk.dma.enav.model.voyage.Route voyageRoute = new dk.dma.enav.model.voyage.Route();

        int startingWP = this.getActiveWaypointIndex();

        for (int i = startingWP; i < getWaypoints().size(); i++) {

            dk.dma.enav.model.voyage.Waypoint voyageWaypoint = new dk.dma.enav.model.voyage.Waypoint();
            RouteWaypoint currentWaypoint = getWaypoints().get(i);

            voyageWaypoint.setEta(etas.get(i));
            voyageWaypoint.setLatitude(currentWaypoint.getPos().getLatitude());
            voyageWaypoint.setLongitude(currentWaypoint.getPos().getLongitude());
            voyageWaypoint.setRot(currentWaypoint.getRot());
            voyageWaypoint.setTurnRad(currentWaypoint.getTurnRad());

            // Leg related stored in the waypoint
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

    public double getSafeHavenLength() {
        return safeHavenLength;
    }

    public double getSafeHavenWidth() {
        return safeHavenWidth;
    }

    /**
     * Returns the intended route broadcast, based on the parameters passed along
     * 
     * @param filter
     *            the filter to apply to extract the partial route
     * @param broadcast
     *            the result to update. If null, a new instance is created.
     * @return the partial route
     */
    public synchronized IntendedRouteBroadcast getPartialRouteData(PartialRouteFilter filter, IntendedRouteBroadcast broadcast) {

        dk.dma.enav.model.voyage.Route voyageRoute = new dk.dma.enav.model.voyage.Route();
        List<Date> originalEtas = new ArrayList<>();
        int activeWpIndex = 0;

        // Pre-compute the start and end ETA's for the partial route
        Date startDate = null, endDate = null;
        if (filter.getType() == FilterType.MINUTES) {
            startDate = new Date(getActiveWaypointEta().getTime() - filter.getBackward() * 1000L * 60L);
            endDate = new Date(getActiveWaypointEta().getTime() + filter.getForward() * 1000L * 60L);
        }

        // Pre-compute the ranges to the active way point measured along the route
        double[] distanceToActiveWaypoints = null;
        if (filter.getType() == FilterType.METERS) {
            distanceToActiveWaypoints = new double[waypoints.size()];
            distanceToActiveWaypoints[activeWaypointIndex] = 0.0;
            for (int i = activeWaypointIndex - 1; i >= 0; i--) {
                double dist = waypoints.get(i).getPos().rhumbLineDistanceTo(waypoints.get(i + 1).getPos());
                distanceToActiveWaypoints[i] = distanceToActiveWaypoints[i + 1] + dist;
            }
            for (int i = activeWaypointIndex + 1; i < waypoints.size(); i++) {
                double dist = waypoints.get(i).getPos().rhumbLineDistanceTo(waypoints.get(i - 1).getPos());
                distanceToActiveWaypoints[i] = distanceToActiveWaypoints[i - 1] + dist;
            }
        }

        for (int i = 0; i < getWaypoints().size(); i++) {

            RouteWaypoint currentWaypoint = getWaypoints().get(i);

            // Check if the way point should be included
            if (filter.getType() == FilterType.MINUTES) {
                Date currentWaypointEta = etas.get(i);
                if ((i < activeWaypointIndex - 1 && currentWaypointEta.before(startDate))
                        || (i > activeWaypointIndex + 1 && currentWaypointEta.after(endDate))) {
                    continue;
                }

            } else if (filter.getType() == FilterType.METERS) {
                if ((i < activeWaypointIndex - 1 && distanceToActiveWaypoints[i] > filter.getBackward())
                        || (i > activeWaypointIndex + 1 && distanceToActiveWaypoints[i] > filter.getForward())) {
                    continue;
                }

            } else if (filter.getType() == FilterType.COUNT) {
                if ((i < activeWaypointIndex - Math.max(1, filter.getBackward()))
                        || (i > activeWaypointIndex + Math.max(1, filter.getForward()))) {
                    continue;
                }
            }

            // Check if we have reached the active way point
            if (i == activeWaypointIndex) {
                activeWpIndex = voyageRoute.getWaypoints().size();
            }
            // Add the original ETA for the current way point
            originalEtas.add(originalRoute.getEtas().get(i));

            // Add the way point
            dk.dma.enav.model.voyage.Waypoint voyageWaypoint = new dk.dma.enav.model.voyage.Waypoint();

            voyageWaypoint.setEta(etas.get(i));
            voyageWaypoint.setLatitude(currentWaypoint.getPos().getLatitude());
            voyageWaypoint.setLongitude(currentWaypoint.getPos().getLongitude());
            voyageWaypoint.setRot(currentWaypoint.getRot());
            voyageWaypoint.setTurnRad(currentWaypoint.getTurnRad());

            // Leg related stored in the way point
            if (currentWaypoint.getOutLeg() != null) {
                dk.dma.enav.model.voyage.RouteLeg routeLeg = new dk.dma.enav.model.voyage.RouteLeg();
                routeLeg.setSpeed(currentWaypoint.getOutLeg().getSpeed());
                routeLeg.setXtdPort(currentWaypoint.getOutLeg().getXtdPort());
                routeLeg.setXtdStarboard(currentWaypoint.getOutLeg().getXtdStarboard());
                routeLeg.setSFLen(currentWaypoint.getOutLeg().getSFLen());
                if (currentWaypoint.getOutLeg().getHeading() == Heading.GC) {
                    routeLeg.setHeading(dk.dma.enav.model.voyage.RouteLeg.Heading.GC);
                } else {
                    routeLeg.setHeading(dk.dma.enav.model.voyage.RouteLeg.Heading.RL);
                }

                voyageWaypoint.setRouteLeg(routeLeg);
            }
            voyageRoute.getWaypoints().add(voyageWaypoint);
        }

        // Since this may be a partial route, make sure the last out-leg is null
        if (!voyageRoute.getWaypoints().isEmpty()) {
            Waypoint lastWaypoint = voyageRoute.getWaypoints().get(voyageRoute.getWaypoints().size() - 1);
            if (lastWaypoint.getRouteLeg() != null) {
                lastWaypoint.setRouteLeg(null);
            }
        }

        // Make the broadcast message
        if (broadcast == null) {
            broadcast = new IntendedRouteBroadcast();
        }
        IntendedRouteMessage irm = IntendedRoute.fromRoute(voyageRoute);
        irm.setPlannedEtas(originalEtas);
        irm.setActiveWpIndex(activeWpIndex);
        broadcast.setRoute(irm);

        return broadcast;
    }
}

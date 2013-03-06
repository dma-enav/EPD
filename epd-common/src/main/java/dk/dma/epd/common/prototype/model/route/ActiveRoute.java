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
package dk.dma.epd.common.prototype.model.route;

import java.util.Date;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
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
     * The current GPS data
     */
    protected GpsData currentGpsData;
    /**
     * Average speed over an appropriate time period
     */
    protected double avgSpeed;
    /**
     * Range to active waypoint
     */
    protected Double activeWpRng;
    /**
     * Bearing to active waypoint
     */
    protected Double activeWpBrg;
    /**
     * TTG to active waypoint
     */
    protected Long activeWpTtg;
    /**
     * A special TTG that cannot be null Assumes that intended speed will be
     * reached soon
     */
    protected Long niceActiveWpTtg;
    /**
     * Time of original activation
     */
    protected Date origStarttime;
    /**
     * TTG from the active waypoint
     */
    protected long remainTtg;
    /**
     * DTG from the active waypoint
     */
    protected double remainDtg;

    /**
     * The minimum radius of wp you have to be inside to change active waypoint
     */
    protected double wpCircleMin = 0.2; // 0.2 nm = 365 m

    /**
     * Should relaxed WP change be used
     */
    protected boolean relaxedWpChange = true;

    protected int lastWpCounter;

    private Position safeHavenLocation;
    private Route originalRoute;
    
    protected double safeHavenBearing;

    public ActiveRoute(Route route, GpsData gpsData) {
        super();
        this.waypoints = route.getWaypoints();
        this.name = route.getName();
        this.visible = route.isVisible();
        this.departure = route.getDeparture();
        this.destination = route.getDestination();
        this.starttime = route.getStarttime();
        this.origStarttime = GnssTime.getInstance().getDate();
        this.routeMetocSettings = route.getRouteMetocSettings();
        this.metocForecast = route.getMetocForecast();
        this.originalRoute = route.copy();

        this.safeHavenLocation = waypoints.get(0).getPos();
        calcValues(true);
        changeActiveWaypoint(getBestWaypoint(route, gpsData));
    }

    /*
     * Get's the most optimal route choice If speed is lower than 3 we start at
     * point 0, otherwise we take bearing and distance into account and select
     * the best match. It will never select a waypoint behind itself.
     */
    private int getBestWaypoint(Route route, GpsData gpsData) {
        // LinkedList<Double> weightedDistance = new LinkedList<Double>();
        if (gpsData.isBadPosition() || gpsData.getSog() < 3) {
            return 0;
        } else {
            double smallestDist = 99999999.0;
            int index = 0;
            for (int i = 0; i <= route.getWaypoints().size() - 1; i++) {
                Position wpPos = route.getWaypoints().get(i).getPos();
                double distance = gpsData.getPosition().rhumbLineDistanceTo(
                        wpPos);
                double angleToWpDeg = gpsData.getPosition().rhumbLineBearingTo(wpPos);
                double weight = 1 - (Math.toRadians(gpsData.getCog()) - Math
                        .toRadians(angleToWpDeg));
                double result = Math.abs(weight) * (0.5 * Converter
                        .metersToNm(distance));
                double upper = gpsData.getCog() + 90;
                double lower = gpsData.getCog() - 90;

                if (result < smallestDist
                        && angleToWpDeg < upper && angleToWpDeg > lower) {
                    smallestDist = result;
                    index = i;
                }

            }
            // System.out.println(smallestDist);
            // System.out.println(weightedDistance);
            return index;

        }

    }
    
    public double getSafeHavenBearing(){
        return safeHavenBearing;
    }

    public Position getSafeHavenLocation() {

        long currentTime = GnssTime.getInstance().getDate().getTime();

        // We haven't begun sailing on the route yet, putting box at first
        // waypoint
        if (currentTime < originalRoute.getStarttime().getTime()) {
            safeHavenBearing = Calculator.bearing(originalRoute.getWaypoints().get(0).getPos(), originalRoute.getWaypoints().get(1).getPos(), Heading.RL);
            return originalRoute.getWaypoints().get(0).getPos();
        } else {

            for (int i = 0; i < originalRoute.getWaypoints().size(); i++) {

                //We haven't found the match so we must be at the end of the route
                if (i == originalRoute.getWaypoints().size()-1) {
                    safeHavenBearing = Calculator.bearing(originalRoute.getWaypoints().get(originalRoute.getWaypoints().size()-2).getPos(), originalRoute.getWaypoints().get(originalRoute.getWaypoints().size()-1).getPos(), Heading.RL);
                    
                    return originalRoute.getWaypoints().get(i).getPos();
                } else {

                    // We should be beyond this
                    if (currentTime > originalRoute.getEtas().get(i).getTime()
                            && currentTime < originalRoute.getEtas().get(i + 1)
                                    .getTime()) {
                        //How long have we been sailing between these waypoints?
                        long secondsSailTime = (currentTime - originalRoute.getEtas().get(i).getTime()) / 1000;
                        double distanceTravelledNauticalMiles = Converter.milesToNM(Calculator
                                .distanceAfterTimeMph(originalRoute.getWaypoints().get(i).getOutLeg().getSpeed(),
                                        secondsSailTime));
                        
//                        System.out.println("Travelled: " + distanceTravelledNauticalMiles
//                                + " nautical miles total to travel: "
//                                + this.currentLeg.calcRng() + " nautical miles");
//                        
                        
                        safeHavenLocation = Calculator.findPosition(this.getWaypoints()
                                .get(i).getPos(), this.getWaypoints().get(i).getOutLeg()
                                .calcBrg(),
                                Converter.nmToMeters(distanceTravelledNauticalMiles));
                        
                        safeHavenBearing = Calculator.bearing(originalRoute.getWaypoints().get(i).getPos(), originalRoute.getWaypoints().get(i+1).getPos(), Heading.RL);
                        
                        
//                        System.out.println("At waypoint: " + i);
                        return safeHavenLocation;
                    }
                }

                // if (originalRoute.getWaypoints().get(i).get)

            }

        }
        //An error must have occured
        return null;
//
//        System.out.println("Hi?");
//        
//        // How long have the route been active
//        long secondsSailTime = ((GnssTime.getInstance().getDate().getTime() - origStarttime
//                .getTime()) / (1000));
//
//        // How long have should we have sailed in that time period?
//        double distanceTravelledNauticalMiles = Converter.milesToNM(Calculator
//                .distanceAfterTimeMph(this.currentLeg.getSpeed(),
//                        secondsSailTime));
//
//        System.out.println("Travelled: " + distanceTravelledNauticalMiles
//                + " nautical miles total to travel: "
//                + this.currentLeg.calcRng() + " nautical miles");
//        // We have leg total before we recalculate which leg we are on and total
//        // total if we are beyond the total size of the route?
//
//        if (distanceTravelledNauticalMiles >= this.currentLeg.calcRng()) {
//            this.safeHavenLocation = waypoints.get(1).getPos();
//        } else {
//            safeHavenLocation = Calculator.findPosition(this.getWaypoints()
//                    .get(0).getPos(), this.getWaypoints().get(0).getOutLeg()
//                    .calcBrg(),
//                    Converter.nmToMeters(distanceTravelledNauticalMiles));
//        }
//
//        return safeHavenLocation;
    }

    public synchronized void update(GpsData gpsData) {

        if (gpsData.isBadPosition()) {
            return;
        }

        // Get active waypoint
        RouteWaypoint activeWaypoint = waypoints.get(activeWaypointIndex);
        // Set current GPS data
        currentGpsData = gpsData;
        // TODO calculate avg speed
        avgSpeed = gpsData.getSog();

        // Calculate brg and rng
        activeWpRng = Calculator.range(gpsData.getPosition(),
                activeWaypoint.getPos(), currentLeg.getHeading());
        activeWpBrg = Calculator.bearing(gpsData.getPosition(),
                activeWaypoint.getPos(), currentLeg.getHeading());

        // Calculate nice TTG
        niceActiveWpTtg = Math
                .round(activeWpRng / currentLeg.getSpeed() * 60 * 60 * 1000);

        // Calculate TTG to active waypoint
        if (avgSpeed > 0.1) {
            activeWpTtg = Math.round(activeWpRng / avgSpeed * 60 * 60 * 1000);
            if (activeWpTtg < niceActiveWpTtg) {
                niceActiveWpTtg = activeWpTtg;
            } else {
                double pctOff = (niceActiveWpTtg - activeWpTtg) / niceActiveWpTtg * 100.0;
                if (pctOff < 50.0) {
                    niceActiveWpTtg = activeWpTtg;
                }
            }
        } else {
            activeWpTtg = null;
        }

    }

    @Override
    public void setMetocForecast(MetocForecast metocForecast) {
        this.metocForecast = metocForecast;
        this.metocStarttime = getStarttime();
        this.metocEta = getNiceEta();
    }

    @Override
    public boolean isMetocValid(long tolerance) {
        return super.isMetocValid(getNiceEta(), tolerance);
    }

    public synchronized ActiveWpSelectionResult chooseActiveWp() {
        // Calculate if in Wp circle
        boolean inWpCircle = false;
        double xtd = currentLeg.getMaxXtd() == null ? 0.0 : currentLeg
                .getMaxXtd();
        double radius = Math.max(xtd, wpCircleMin);
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
        double nextWpRng = Calculator.range(currentGpsData.getPosition(),
                nextLeg.getEndWp().getPos(), nextLeg.getHeading());

        if (inWpCircle) {
            // If closer to next wp than the dist between wp's, we change
            if (nextWpRng < getWpRng(activeWaypointIndex)) {
                changeActiveWaypoint(activeWaypointIndex + 1);
                return ActiveWpSelectionResult.CHANGED;
            }
        } else {
            // Some temporary fallback when we are really of course
            if (relaxedWpChange) {
                if (2 * nextWpRng < getWpRng(activeWaypointIndex)) {
                    changeActiveWaypoint(activeWaypointIndex + 1);
                    return ActiveWpSelectionResult.CHANGED;
                }
            }
        }

        return ActiveWpSelectionResult.NO_CHANGE;
    }

    public synchronized void changeActiveWaypoint(int index) {
        // Save actual ETA
        etas.set(activeWaypointIndex, GnssTime.getInstance().getDate());
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
        // Calculate remaining DTG, TTG and ETA
        remainDtg = 0;
        remainTtg = 0;
        for (int i = index; i < waypoints.size() - 1; i++) {
            remainDtg += dtgs[i];
            remainTtg += ttgs[i];
        }

        reCalcRemainingWpEta();
    }

    @Override
    public synchronized Date calculateEta() {
        if (activeWpTtg == null) {
            return null;
        }
        return new Date(GnssTime.getInstance().getDate().getTime() + remainTtg
                + activeWpTtg);
    }

    public synchronized Date getActiveWaypointEta() {
        if (activeWpTtg == null) {
            return null;
        }
        return new Date(GnssTime.getInstance().getDate().getTime()
                + activeWpTtg);
    }

    public synchronized Date getNiceActiveWaypointEta() {
        if (niceActiveWpTtg == null) {
            return null;
        }
        return new Date(GnssTime.getInstance().getDate().getTime()
                + niceActiveWpTtg);
    }

    @Override
    public synchronized Long calcTtg() {
        if (activeWpTtg == null) {
            return null;
        }
        return activeWpTtg + remainTtg;
    }

    @Override
    public synchronized Double calcDtg() {
        if (activeWpRng == null) {
            return null;
        }
        return activeWpRng + remainDtg;
    }

    public synchronized int getActiveWaypointIndex() {
        return activeWaypointIndex;
    }

    public synchronized RouteWaypoint getActiveWp() {
        return waypoints.get(activeWaypointIndex);
    }

    public synchronized boolean reCalcRemainingWpEta() {
        int aw = getActiveWaypointIndex();
        Date eta = getNiceActiveWaypointEta();

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

    public synchronized Date getNiceEta() {
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
        return activeWpTtg;
    }

    public synchronized Long getNiceActiveWpTtg() {
        return niceActiveWpTtg;
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

    public dk.dma.enav.model.voyage.Route getVoyageRoute(){
        
        dk.dma.enav.model.voyage.Route voyageRoute = new dk.dma.enav.model.voyage.Route();

        int startingWP = this.getActiveWaypointIndex();
        int wayPointsToSend = getWaypoints().size() - startingWP;

        System.out.println("Amount of waypoints to send: " + wayPointsToSend);
        System.out.println("Starting at " + startingWP);
        for (int i = startingWP; i < getWaypoints().size(); i++) {
        
            System.out.println("Packing waypoint: " + i);
            dk.dma.enav.model.voyage.Waypoint voyageWaypoint = new dk.dma.enav.model.voyage.Waypoint(); 
             RouteWaypoint currentWaypoint = getWaypoints().get(i);

             voyageWaypoint.setEta(etas.get(i));
             voyageWaypoint.setLatitude(currentWaypoint.getPos().getLatitude());
             voyageWaypoint.setLongitude(currentWaypoint.getPos().getLongitude());
             voyageWaypoint.setRot(currentWaypoint.getRot());
             voyageWaypoint.setTurnRad(currentWaypoint.getTurnRad());
             
             //Leg related stored in the waypoint
             if (currentWaypoint.getOutLeg() != null){
                 voyageWaypoint.setSpeed(currentWaypoint.getOutLeg().getSpeed());
                 voyageWaypoint.setXtdPort(currentWaypoint.getOutLeg().getXtdPort());
                 voyageWaypoint.setXtdStarboard(currentWaypoint.getOutLeg().getXtdStarboard());
             }
             voyageRoute.getWaypoints().add(voyageWaypoint);
        }
        
        voyageRoute.setActiveWaypoint(this.getActiveWaypointIndex());
        
        return voyageRoute;
    }
    
}

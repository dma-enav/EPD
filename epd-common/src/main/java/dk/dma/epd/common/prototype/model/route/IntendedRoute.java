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
import java.util.LinkedList;
import java.util.List;

import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.voyage.Waypoint;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.enavcloud.intendedroute.HeadingType;
import dk.dma.epd.common.prototype.enavcloud.intendedroute.IntendedRouteMessage;
import dk.dma.epd.common.prototype.enavcloud.intendedroute.Leg;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Converter;

/**
 * Defines an intended route.
 */
public class IntendedRoute extends Route {

    private static final long serialVersionUID = 1L;
    protected Double routeRange;
    protected Date received;
    protected long duration;
    protected Date etaFirst;
    protected Date etaLast;
    protected Double activeWpRange;
    protected boolean visible = true;
    protected long mmsi;
    protected int activeWpIndex;

    protected List<Double> ranges = new ArrayList<>();
    protected List<Date> plannedEtas;

    protected double plannedPositionBearing;

    /**
     * Initializes the intended route from route data received over the cloud
     * 
     * @param intendedRouteMessage
     *            route data received over the cloud
     */
    public IntendedRoute(IntendedRouteMessage intendedRouteMessage) {
        super();
        received = PntTime.getDate();
        parseRoute(intendedRouteMessage);
        setActiveWpIndex(intendedRouteMessage.getActiveWpIndex());
        setPlannedEtas(intendedRouteMessage.getPlannedEtas());
    }

    /**
     * Parses the route data received over the cloud as an EPD route
     * 
     * @param intendedRouteMessage
     *            route data received over the cloud
     */
    private void parseRoute(IntendedRouteMessage intendedRouteMessage) {
        
        List<Waypoint> cloudRouteWaypoints = wpsFromIntendedRouteMessage(intendedRouteMessage);
        
        
        LinkedList<RouteWaypoint> routeWaypoints = this.getWaypoints();

        for (int i = 0; i < cloudRouteWaypoints.size(); i++) {

            RouteWaypoint waypoint = new RouteWaypoint();
            Waypoint cloudWaypoint = cloudRouteWaypoints.get(i);

            waypoint.setName(cloudWaypoint.getName());
            Position position = Position.create(cloudWaypoint.getLatitude(), cloudWaypoint.getLongitude());
            waypoint.setPos(position);

            // Handle leg
            if (i > 0) {
                RouteWaypoint prevWaypoint = routeWaypoints.get(i - 1);
                RouteLeg leg = new RouteLeg();
                waypoint.setInLeg(leg);
                prevWaypoint.setOutLeg(leg);
                leg.setStartWp(prevWaypoint);
                leg.setEndWp(waypoint);
            }

            routeWaypoints.add(waypoint);

        }

        if (routeWaypoints.size() > 1) {
            for (int i = 0; i < routeWaypoints.size(); i++) {

                RouteWaypoint waypoint = routeWaypoints.get(i);
                Waypoint cloudWaypoint = cloudRouteWaypoints.get(i);

                if (cloudWaypoint.getTurnRad() != null) {
                    waypoint.setTurnRad(cloudWaypoint.getTurnRad());
                }

                if (cloudWaypoint.getRot() != null) {
                    waypoint.setRot(cloudWaypoint.getRot());
                }

                // Leg

                if (cloudWaypoint.getRouteLeg() != null) {

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

                    // SF Width
                    if (cloudWaypoint.getRouteLeg().getSFWidth() != null) {
                        waypoint.getOutLeg().setSFWidth(cloudWaypoint.getRouteLeg().getSFWidth());
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

            }
        }

        etas = new ArrayList<>();
        // this.calcAllWpEta();
        for (int i = 0; i < cloudRouteWaypoints.size(); i++) {
            etas.add(cloudRouteWaypoints.get(i).getEta());
        }

        // Find ranges on each leg
        routeRange = 0.0;
        ranges.add(routeRange);
        for (int i = 0; i < waypoints.size() - 1; i++) {
            double dist = waypoints.get(i).getPos().rhumbLineDistanceTo(waypoints.get(i + 1).getPos()) / 1852.0;
            routeRange += dist;
            ranges.add(routeRange);
        }

    }

    public Double getRouteRange() {
        return routeRange;
    }

    public Double getRange(int index) {
        if (activeWpRange == null) {
            return null;
        }
        return activeWpRange + ranges.get(index);
    }

    public void setRouteRange(Double routeRange) {
        this.routeRange = routeRange;
    }

    public Date getReceived() {
        return received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Date getEtaFirst() {
        return etaFirst;
    }

    public void setEtaFirst(Date etaFirst) {
        this.etaFirst = etaFirst;
    }

    public Date getEtaLast() {
        return etaLast;
    }

    public void setEtaLast(Date etaLast) {
        this.etaLast = etaLast;
    }

    public List<Double> getRanges() {
        return ranges;
    }

    public void setRanges(List<Double> ranges) {
        this.ranges = ranges;
    }

    /**
     * Update range to active WP given the targets new position
     * 
     * @param posData
     */
    public void update(VesselPositionData posData) {
        if (posData == null || posData.getPos() == null || waypoints.size() == 0) {
            return;
        }

        // Range to first wp

        // activeWpRange =
        // posData.getPos().rhumbLineDistanceTo(waypoints.get(0)) / 1852.0;
    }

    public double getSpeed(int id) {
        if (waypoints.get(id).getOutLeg() != null) {
            return waypoints.get(id).getOutLeg().getSpeed();
        }

        return 0;
    }

    /**
     * Returns whether the route is non-empty or not
     * 
     * @return whether the route is non-empty or not
     */
    public synchronized boolean hasRoute() {
        return waypoints != null && waypoints.size() > 0;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public long getMmsi() {
        return mmsi;
    }

    public void setMmsi(long mmsi) {
        this.mmsi = mmsi;
    }

    public Double getActiveWpRange() {
        return activeWpRange;
    }

    public void setActiveWpRange(Double activeWpRange) {
        this.activeWpRange = activeWpRange;
    }

    public int getActiveWpIndex() {
        return activeWpIndex;
    }

    public void setActiveWpIndex(int activeWpIndex) {
        this.activeWpIndex = activeWpIndex;
    }

    public RouteWaypoint getActiveWaypoint() {
        return getWaypoints().get(activeWpIndex);
    }

    public Position getPlannedPosition() {

        long currentTime = PntTime.getDate().getTime();

        // Is the ship on the route yet? or where it was planning to be?
        // If not, don't draw anything as nothing is planned
        if (plannedEtas == null || plannedEtas.size() != getWaypoints().size() || currentTime < plannedEtas.get(0).getTime()) {
            return null;
        } else {

            for (int i = 0; i < getWaypoints().size(); i++) {

                // We haven't found the match so the ship must have finished it's
                // route - Display the box at the end
                if (i == getWaypoints().size() - 1) {
                    plannedPositionBearing = 0;
                    return null;

                } else {

                    // We should be beyond this
                    if (currentTime > plannedEtas.get(i).getTime() && currentTime < plannedEtas.get(i + 1).getTime()) {

                        Position plannedPosition;

                        // How long have we been sailing between these
                        // waypoints?
                        long secondsSailTime = (currentTime - plannedEtas.get(i).getTime()) / 1000;

                        double distanceTravelledNauticalMiles = Converter.milesToNM(Calculator.distanceAfterTimeMph(getWaypoints()
                                .get(i).getOutLeg().getSpeed(), secondsSailTime));

                        if (this.getWaypoints().get(i).getOutLeg().getHeading() == Heading.GC) {
                            plannedPosition = Calculator.findPosition(this.getWaypoints().get(i).getPos(),
                                    this.getWaypoints().get(i + 1).getPos(), Converter.nmToMeters(distanceTravelledNauticalMiles));
                        } else {
                            plannedPosition = Calculator.findPosition(this.getWaypoints().get(i).getPos(),
                                    this.getWaypoints().get(i).getOutLeg().calcBrg(),
                                    Converter.nmToMeters(distanceTravelledNauticalMiles));
                        }

                        RouteLeg leg = getWaypoints().get(i).getOutLeg();
                        plannedPositionBearing = Calculator.bearing(leg.getStartWp().getPos(), leg.getEndWp().getPos(),
                                leg.getHeading());

                        return plannedPosition;
                    }
                }

            }
        }

        // An error must have occured
        return null;
    }

    public double getPlannedPositionBearing() {
        return plannedPositionBearing;
    }

    public void setPlannedEtas(List<Date> plannedEtas) {
        this.plannedEtas = plannedEtas;
    }

    /**
     * Create an IntendedRouteMessage from a route model instance
     * @param route
     * @return
     */
    public static IntendedRouteMessage fromRoute(dk.dma.enav.model.voyage.Route route) {
        IntendedRouteMessage irm = new IntendedRouteMessage();
        ArrayList<dk.dma.epd.common.prototype.enavcloud.intendedroute.Waypoint> wps = new ArrayList<>();
        for (Waypoint wp : route.getWaypoints()) {
            dk.dma.epd.common.prototype.enavcloud.intendedroute.Waypoint iwp = new dk.dma.epd.common.prototype.enavcloud.intendedroute.Waypoint();
            iwp.setLatitude(wp.getLatitude());
            iwp.setLongitude(wp.getLongitude());
            iwp.setEta(wp.getEta());
            iwp.setRot(wp.getRot());
            iwp.setTurnRad(wp.getTurnRad());
            if (wp.getRouteLeg() != null) {
                Leg leg = new Leg();
                leg.setSpeed(wp.getRouteLeg().getSpeed());
                leg.setXtdStarboard(wp.getRouteLeg().getXtdStarboard());
                leg.setXtdPort(wp.getRouteLeg().getXtdPort());
                if (wp.getRouteLeg().getHeading() == dk.dma.enav.model.voyage.RouteLeg.Heading.RL) {
                    leg.setHeadingType(HeadingType.RL);
                } else {
                    leg.setHeadingType(HeadingType.GC);
                }
                iwp.setOutLeg(leg);
            }
            wps.add(iwp);            
        }
        irm.setWaypoints(wps);
        return irm;
    }
    
    public static List<Waypoint> wpsFromIntendedRouteMessage(IntendedRouteMessage message) {
        List<Waypoint> wps = new ArrayList<>();        
        for (dk.dma.epd.common.prototype.enavcloud.intendedroute.Waypoint iwp : message.getWaypoints()) {
            Waypoint wp = new Waypoint();
            wp.setLatitude(iwp.getLatitude());
            wp.setLongitude(iwp.getLongitude());
            wp.setEta(iwp.getEta());
            wp.setRot(iwp.getRot());
            wp.setTurnRad(iwp.getTurnRad());
            if (iwp.getOutLeg() != null) {
                Leg leg = iwp.getOutLeg();
                dk.dma.enav.model.voyage.RouteLeg rleg = new dk.dma.enav.model.voyage.RouteLeg();                
                rleg.setSpeed(leg.getSpeed());
                rleg.setXtdStarboard(leg.getXtdStarboard());
                rleg.setXtdPort(leg.getXtdPort());
                if (leg.getHeadingType() == HeadingType.GC) {
                    rleg.setHeading(dk.dma.enav.model.voyage.RouteLeg.Heading.GC);
                } else {
                    rleg.setHeading(dk.dma.enav.model.voyage.RouteLeg.Heading.RL);
                }
                wp.setRouteLeg(rleg);
            }
            wps.add(wp);            
        }        
        return wps;        
    }

    public List<Date> getPlannedEtas() {
        return plannedEtas;
    }
    
}

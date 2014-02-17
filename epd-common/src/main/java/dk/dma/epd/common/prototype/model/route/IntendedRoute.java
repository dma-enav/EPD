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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.voyage.Waypoint;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
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
     * @param cloudRouteData
     *            route data received over the cloud
     */
    public IntendedRoute(dk.dma.enav.model.voyage.Route cloudRouteData) {
        super();
        received = PntTime.getInstance().getDate();
        parseRoute(cloudRouteData);

        // Temporary hack - needs to be seperate
        plannedEtas = etas;
    }

    /**
     * Parses the route data received over the cloud as an EPD route
     * 
     * @param cloudRouteData
     *            route data received over the cloud
     */
    private void parseRoute(dk.dma.enav.model.voyage.Route cloudRouteData) {
        this.setName(cloudRouteData.getName());
        List<Waypoint> cloudRouteWaypoints = cloudRouteData.getWaypoints();
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

        long currentTime = PntTime.getInstance().getDate().getTime();

        // Is the ship on the route yet? or where it was planning to be?
        // If not, don't draw anything as nothing is planned
        if (currentTime < plannedEtas.get(0).getTime()) {
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

                        // plannedPosition = Calculator.findPosition(this.getWaypoints().get(i).getPos(), this.getWaypoints().get(i)
                        // .getOutLeg().calcBrg(), Converter.nmToMeters(distanceTravelledNauticalMiles));

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

}

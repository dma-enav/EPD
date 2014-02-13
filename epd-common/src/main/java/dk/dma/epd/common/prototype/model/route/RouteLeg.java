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

import java.io.Serializable;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.common.util.TypedValue.Dist;
import dk.dma.epd.common.util.TypedValue.DistType;
import dk.dma.epd.common.util.TypedValue.Speed;
import dk.dma.epd.common.util.TypedValue.SpeedType;
import dk.dma.epd.common.util.TypedValue.Time;
import dk.dma.epd.common.util.TypedValue.TimeType;

/**
 * Route leg class
 */
public class RouteLeg implements Serializable {

    private static final int R = 6371; // earths radius in km
    private static final long serialVersionUID = 1L;

    /**
     * Planned leg speed
     */
    protected double speed;
    /**
     * Sail heading rhumb line or great circle
     */
    protected Heading heading = Heading.RL;
    /**
     * XTD starboard nm
     */
    protected Double xtdStarboard;
    /**
     * XTD port nm
     */
    protected Double xtdPort;
    /**
     * The starting wp of leg
     */
    protected RouteWaypoint startWp;
    /**
     * The end wp of leg
     */
    protected RouteWaypoint endWp;

    protected double SFWidth = 1000;
    protected double SFLen = 500;

    public RouteLeg() {

    }

    public RouteLeg(RouteLeg rll) {
        this.speed = rll.getSpeed();
        this.heading = rll.getHeading();
        this.xtdStarboard = rll.getXtdStarboard();
        this.xtdPort = rll.getXtdPort();
        this.startWp = rll.getStartWp();
        this.endWp = rll.getEndWp();
        this.SFWidth = rll.getSFWidth();
        this.SFLen = rll.getSFLen();
    }

    public RouteLeg(RouteWaypoint startWp, RouteWaypoint endWp) {
        this.startWp = startWp;
        this.endWp = endWp;
    }

    public double getSFWidth() {
        return SFWidth;
    }

    public void setSFWidth(double d) {
        SFWidth = d;
    }

    public double getSFLen() {
        return SFLen;
    }

    public void setSFLen(double sFLen) {
        SFLen = sFLen;
    }

    public long getSFLenInMilleseconds() {
        // Handle speed == 0 (approximately)
        if (speed < 0.000000001) {
            return 0;
        }
        
        return new Dist(DistType.METERS, getSFLen())
            .withSpeed(new Speed(SpeedType.KNOTS, getSpeed()))
                .in(TimeType.MILLISECONDS).longValue();   
    }
    
    public void setSFLenInMilliseconds(long lenMs) {
        // Handle speed == 0 (approximately)
        if (speed < 0.000000001) {
            return;
        }
        
        setSFLen(
            new Time(TimeType.MILLISECONDS, lenMs)
                .withSpeed(new Speed(SpeedType.KNOTS, getSpeed()))
                    .in(DistType.METERS).doubleValue());        
    }
    
    /**
     * Speed in nautical miles pr. hour
     * @param speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Speed in nautical miles pr. hour
     * @param speed
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public Double getXtdStarboard() {
        return xtdStarboard;
    }

    public Double getXtdStarboardMeters() {
        if (xtdStarboard == null) {
            return null;
        }
        return Converter.nmToMeters(xtdStarboard);
    }

    public void setXtdStarboard(Double xtdStarboard) {
        this.xtdStarboard = xtdStarboard;
    }

    public Double getXtdPort() {
        return xtdPort;
    }

    public Double getXtdPortMeters() {
        if (xtdPort == null) {
            return null;
        }
        return Converter.nmToMeters(xtdPort);
    }

    public void setXtdPort(Double xtdPort) {
        this.xtdPort = xtdPort;
    }

    public Double getMaxXtd() {
        if (xtdPort == null) {
            return xtdStarboard;
        }
        if (xtdStarboard == null) {
            return xtdPort;
        }
        return Math.max(xtdPort, xtdStarboard);
    }

    public RouteWaypoint getStartWp() {
        return startWp;
    }

    public void setStartWp(RouteWaypoint startWp) {
        this.startWp = startWp;
    }

    public RouteWaypoint getEndWp() {
        return endWp;
    }

    public void setEndWp(RouteWaypoint endWp) {
        this.endWp = endWp;
    }

    public double calcRng() {

        return Calculator.range(startWp.getPos(), endWp.getPos(), heading);
    }

    public double calcBrg() {
        if (endWp != null) {
            return Calculator
                    .bearing(startWp.getPos(), endWp.getPos(), heading);
        }
        return 0.0;

    }

    /**
     * Ttg in milliseconds
     * 
     * @return
     */
    public long calcTtg() {
        if (speed < 0.1) {
            return -1L;
        }
        return Math.round(calcRng() * 3600.0 / speed * 1000.0);
    }
    
    /**
     * Calculate TTG with an alternative speed value instead of
     * the default speed value (which is the planned speed).
     * @param altSpeed The alternative speed to be used in the TTG calculation.
     * @return The calculated TTG in milliseconds.
     */
    public long calcTtg(double altSpeed) {
        if(altSpeed < 0.1) {
            altSpeed = 0.1;
        }
        return Math.round(calcRng() * 3600.0 / altSpeed * 1000.0);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RouteLeg [heading=");
        builder.append(heading);
        builder.append(", speed=");
        builder.append(speed);
        builder.append(", xtdPort=");
        builder.append(xtdPort);
        builder.append(", xtdStarboard=");
        builder.append(xtdStarboard);
        builder.append(", SFWidth=");
        builder.append(SFWidth);
        builder.append(", SFLen=");
        builder.append(SFLen);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Calculate the great circle cross track distance from a route leg to a
     * given geographical location. Formula from <a
     * href="http://www.movable-type.co.uk/scripts/latlong.html"
     * >http://www.movable-type.co.uk/scripts/latlong.html</a>
     * 
     * @param crossTrackPoint
     *            Geographical location
     * @return Distance
     */
    public double calculateCrossTrackDist(Position crossTrackPoint) {
        if (heading.equals(Heading.GC)) {
            System.out.println("GC");
        } else {
            System.out.println("RL");
        }
        double d13 = Calculator.range(startWp.getPos(), crossTrackPoint,
                Heading.GC);
        double brng13 = Calculator.bearing(startWp.getPos(), crossTrackPoint,
                Heading.GC);
        double brng12 = Calculator.bearing(startWp.getPos(), endWp.getPos(),
                heading);

        /*
         * Formula: dxt = asin(sin(d13/R)*sin(θ13−θ12)) * R
         * 
         * where d13 is distance from start point to third point θ13 is
         * (initial) bearing from start point to third point θ12 is (initial)
         * bearing from start point to end point R is the earth’s radius
         */

        double dXt = Math.asin(Math.sin(d13 / R) * Math.sin(brng13 - brng12))
                * R;
        return dXt;
    }

    public RouteLeg copy() {

        RouteLeg newRouteLeg = new RouteLeg();

        newRouteLeg.setSpeed(getSpeed());
        newRouteLeg.setHeading(getHeading());
        newRouteLeg.setXtdStarboard(getXtdStarboard());
        newRouteLeg.setXtdPort(getXtdPort());
        newRouteLeg.setSFLen(getSFLen());
        newRouteLeg.setSFWidth(getSFWidth());

        return newRouteLeg;
    }
}

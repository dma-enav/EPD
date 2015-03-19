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

import java.io.Serializable;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;

/**
 * A route waypoint
 */
public class RouteWaypoint implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Optional name for waypoint
     */
    protected String name;
    /**
     * Position
     */
    protected Position pos;
    /**
     * Optional turn radius
     */
    protected Double turnRad;
    /**
     * Optional rate of turn
     */
    protected Double rot;
    /**
     * Leg going from waypoint
     */
    protected RouteLeg outLeg;
    /**
     * Leg going to this waypoint
     */
    protected RouteLeg inLeg;

    public RouteWaypoint(RouteWaypoint rw) {
        this.name = rw.getName();
        this.pos = rw.getPos();
        this.turnRad = rw.getTurnRad();
        this.rot = rw.getRot();
        this.outLeg = rw.getOutLeg();
        this.inLeg = rw.getInLeg();
    }

    public RouteWaypoint() {

    }

    /**
     * Performs a deep copy of this RouteWaypoint. The copy constructor above is
     * used to perform shallow copy on route creation and editing, when a back
     * reference to route leg is needed.
     * 
     * @return Copy of routeWaypoint.
     */
    public RouteWaypoint copy() {
        RouteWaypoint copy = new RouteWaypoint();
        copy.setName(this.getName());
        copy.setPos(this.getPos());
        copy.setTurnRad(this.getTurnRad());
        return copy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public Double getTurnRad() {
        return turnRad;
    }

    public RouteLeg getOutLeg() {
        return outLeg;
    }

    public void setOutLeg(RouteLeg leg) {
        this.outLeg = leg;
    }

    public RouteLeg getInLeg() {
        return inLeg;
    }

    public void setInLeg(RouteLeg inLeg) {
        this.inLeg = inLeg;
    }

    public Double getRot() {
        return rot;
    }

    public void setRot(Double rot) {
        if (inLeg == null || outLeg == null) {
            rot = null;
            return;
        }

        this.rot = rot;
        // Calculate radius from fixed speed and rot
        // Speed in nm / minute
        double speed = outLeg.getSpeed() / 60;
        // TODO This is probably not entirely correct
        this.turnRad = speed / rot;
    }

    public void setTurnRad(Double turnRad) {
        if (inLeg == null /* || outLeg == null */) {
            turnRad = null;
            return;
        }
        // TODO: Parser complains if last waypoint doesn't have turnrad, is this
        // correct behavior?
        if (outLeg == null) {
            this.turnRad = inLeg.getStartWp().getTurnRad();
            this.rot = inLeg.getStartWp().getRot();
            return;
        }
        this.turnRad = turnRad;
        // Calculate rot from fixed speed and rot
        // Speed in nm / minute
        double speed = outLeg.getSpeed() / 60;
        // TODO This is probably not entirely correct
        this.rot = speed / turnRad;
    }

    public void setSpeed(double speed) {
        if (outLeg == null) {
            return;
        }
        outLeg.setSpeed(speed);
        if (turnRad == null) {
            return;
        }
        // Calculate rot from fixed speed and rot
        // Speed in nm / minute
        speed /= 60;
        // TODO This is probably not entirely correct
        this.rot = speed / turnRad;
    }

    /**
     * Calc range to next waypoint
     * 
     * @return
     */
    public Double calcRng() {
        return outLeg == null ? null : outLeg.calcRng();
    }

    public Double calcBrg() {
        return outLeg == null ? null : outLeg.calcBrg();
    }

    public Heading getHeading() {
        return outLeg == null ? null : outLeg.getHeading();
    }

    public Double calcRot() {
        if (turnRad == null || outLeg == null || inLeg == null) {
            return null;
        }
        // Set speed will tricker calculation
        setSpeed(outLeg.getSpeed());
        return rot;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RouteWaypoint [name=" + name + ", pos=" + pos + ", turnRad="
                + turnRad + ", rot=" + rot + ", outLeg=" + outLeg + ", inLeg="
                + inLeg + "]";
    }

}

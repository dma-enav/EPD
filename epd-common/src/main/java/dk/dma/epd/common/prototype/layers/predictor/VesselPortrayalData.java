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
package dk.dma.epd.common.prototype.layers.predictor;

import dk.dma.enav.model.geometry.Position;

/**
 * Wraps the basic data needed for vessel portrayal.
 * 
 * @author Janus Varmarken
 */
public class VesselPortrayalData {

    /**
     * Vessel's true heading (i.e. the angle the bow points to).
     */
    private final float heading;

    /**
     * Fixed reference point on vessel that denotes the vessel's position. This
     * can for example be the position of the GPS device or the vessel's center.
     */
    private final Position pos;

    /**
     * Distance from {@link #pos} to bow in meters.
     */
    private final float distBow;

    /**
     * Distance from {@link #pos} to stern in meters.
     */
    private final float distStern;

    /**
     * Distance from {@link #pos} to vessel's port edge in meters.
     */
    private final float distPort;

    /**
     * Distance from {@link #pos} to vessel's starboard edge in meters.
     */
    private final float distStarboard;

    /**
     * Creates a new {@link VesselPortrayalData}.
     * 
     * @param pos
     *            Fixed reference point on vessel that denotes the vessel's
     *            position. This can for example be the position of the GPS
     *            device or the vessel's center.
     * @param heading
     *            Vessel's true heading (i.e. the angle the bow points to).
     * @param distBow
     *            Distance from {@code pos} to bow in meters.
     * @param distStern
     *            Distance from {@code pos} to stern in meters.
     * @param distPort
     *            Distance from {@code pos} to vessel's port edge in meters.
     * @param distStarboard
     *            Distance from {@code pos} to vessel's startboard edge in
     *            meters.
     */
    public VesselPortrayalData(Position pos, float heading, float distBow,
            float distStern, float distPort, float distStarboard) {
        this.pos = pos;
        this.heading = heading;
        this.distBow = distBow;
        this.distStern = distStern;
        this.distPort = distPort;
        this.distStarboard = distStarboard;
    }
    
    public float getHeading() {
        return heading;
    }

    public Position getPos() {
        return pos;
    }

    public float getDistBow() {
        return distBow;
    }

    public float getDistStern() {
        return distStern;
    }

    public float getDistPort() {
        return distPort;
    }

    public float getDistStarboard() {
        return distStarboard;
    }

}

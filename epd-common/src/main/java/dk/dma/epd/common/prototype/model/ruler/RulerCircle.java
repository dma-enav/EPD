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
package dk.dma.epd.common.prototype.model.ruler;

import dk.dma.enav.model.geometry.Position;

/**
 * This class models a ruler circle (for measuring a circular distance around a
 * point on the map).
 * 
 * @author Janus Varmarken
 */
public class RulerCircle {

    /**
     * Center of this circle.
     */
    private Position center;

    /**
     * A point in this circle's periphery.
     */
    private Position pointInPeriphery;

    public RulerCircle(Position position) {
        this.center = position;
    }

    /**
     * Set a point that this circle's periphery contains.
     * 
     * @param point
     *            The point contained in the circle's periphery.
     */
    public void setPointInPeriphery(Position point) {
        this.pointInPeriphery = point;
    }

    public Position getCircleCenter() {
        return this.center;
    }

    public Position getPointInPeriphery() {
        return this.pointInPeriphery;
    }
}

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

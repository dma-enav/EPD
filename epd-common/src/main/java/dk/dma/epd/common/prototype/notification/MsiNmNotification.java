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
package dk.dma.epd.common.prototype.notification;

import dk.dma.enav.model.geometry.BoundingBox;
import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.util.Calculator;
import dma.msinm.MCLocation;
import dma.msinm.MCLocationType;
import dma.msinm.MCMessage;
import dma.msinm.MCPoint;

import java.util.Date;
import java.util.List;

/**
 * An MSI specific notification class
 */
public class MsiNmNotification extends Notification<MCMessage, Integer> {

    private static final long serialVersionUID = 1L;

    boolean filtered = true;

    /**
     * Constructor
     *
     * @param message the MSI-NM message
     */
    public MsiNmNotification(MCMessage message) {
        super(message, message.getId(), NotificationType.MSI);

        // Update the notification data from the MSI message
        title = message.getDescs().get(0).getTitle();
        severity = NotificationSeverity.MESSAGE;
        date = new Date(message.getUpdated().getTime());

        BoundingBox bbox = getBoundingBox();
        if (bbox != null) {
            location = bbox.getCenterPoint();
        }
    }

    /**
     * Returns if the message is valid at the given date
     * @param date the date
     * @return if the message is valid at the given date
     */
    public boolean isValidAt(Date date) {
        return date != null && get().getValidFrom() != null && get().getValidFrom().getTime() < date.getTime();
    }

    /**
     * Computes a bounding box for the MSI-NM or null if location is undefined
     * @return the bounding box for the MSI-NM
     */
    public BoundingBox getBoundingBox() {
        if (get().getLocations() != null && get().getLocations().size() > 0) {
            double minLat = 90, maxLat = -90, minLon = 180, maxLon = -180;

            for (MCLocation loc : get().getLocations()) {
                for (MCPoint pt : loc.getPoints()) {
                    minLat = Math.min(minLat, pt.getLat());
                    maxLat = Math.max(maxLat, pt.getLat());
                    minLon = Math.min(minLon, pt.getLon());
                    maxLon = Math.max(maxLon, pt.getLon());
                }
            }
            return BoundingBox.create(Position.create(minLat, minLon), Position.create(minLon, maxLon), CoordinateSystem.CARTESIAN);
        }
        return null;
    }

    /**
     * Computes the distance between the MSI-NM and the given position.
     * NB: Test method - not precise for Polygon and Polylines
     * @param pos the pos
     * @return the distance in NM
     */
    public Double getDistanceToPosition(Position pos) {
        if (pos != null && get().getLocations() != null && get().getLocations().size() > 0) {
            Double minDist = Double.MAX_VALUE;

            for (MCLocation loc : get().getLocations()) {

                // Circle
                if (loc.getType() == MCLocationType.CIRCLE && loc.getRadius() != null && loc.getPoints().size() == 1) {
                    double dist = Calculator.range(pos, toPos(loc.getPoints().get(0)), Heading.GC);
                    minDist = Math.min(minDist, Math.max(0, dist - loc.getRadius().doubleValue()));


                } else if (loc.getType() == MCLocationType.POINT && loc.getPoints().size() == 1) {
                    // Point
                    minDist = Math.min(minDist, Calculator.range(pos, toPos(loc.getPoints().get(0)), Heading.GC));

                } else if (loc.getType() == MCLocationType.POLYGON) {
                    // Polygon
                    for (MCPoint pt : loc.getPoints()) {
                        minDist = Math.min(minDist, Calculator.range(pos, toPos(pt), Heading.GC));
                    }

                } else if (loc.getType() == MCLocationType.POLYLINE) {
                    // Polyline
                    for (MCPoint pt : loc.getPoints()) {
                        minDist = Math.min(minDist, Calculator.range(pos, toPos(pt), Heading.GC));
                    }
                }

            }
            return minDist;
        }
        return null;
    }

    /**
     * Returns if the the MSI-NM is close to any of the routes.
     * NB: Test method - Very imprecise
     * @param routes the routes to test
     * @return if the the MSI-NM is close to any of the routes
     */
    public boolean nearRoute(List<Route> routes) {
        for (Route route : routes) {
            for (MCLocation loc : get().getLocations()) {
                for (MCPoint pt : loc.getPoints()) {
                    if (route.isPointWithingBBox(toPos(pt))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Converts an MC position to a Position
     * @param pt the MC position
     * @return the position
     */
    private Position toPos(MCPoint pt) {
        return Position.create(pt.getLat(), pt.getLon());
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

}

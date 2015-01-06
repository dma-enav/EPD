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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.TimeUtils;
import dma.msinm.MCArea;
import dma.msinm.MCLocation;
import dma.msinm.MCLocationType;
import dma.msinm.MCMessage;
import dma.msinm.MCPoint;
import dma.msinm.MCSeriesIdType;
import dma.msinm.MCSeriesIdentifier;

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
        super(message, message.getId(), NotificationType.MSI_NM);

        // Update the notification data from the MSI message
        title = message.getDescs().get(0).getTitle();
        severity = NotificationSeverity.MESSAGE;
        date = new Date(message.getUpdated().getTime());

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
            location = Position.create((minLat + maxLat) / 2.0, (minLon + maxLon) / 2.0);
        }

    }

    /**
     * Returns if the message is valid at the given date and time
     * @param date the date
     * @return if the message is valid at the given date and time
     */
    public boolean isValidAt(Date date) {
        return date != null && get().getValidFrom() != null && get().getValidFrom().getTime() <= date.getTime();
    }

    /**
     * Returns if the message is valid at the given date
     * @param date the date
     * @return if the message is valid at the given date
     */
    public boolean isValidAtDate(Date date) {
        return date != null && get().getValidFrom() != null &&
                TimeUtils.resetTime(new Date(get().getValidFrom().getTime())).getTime() <= TimeUtils.resetTime(date).getTime();
    }

    /**
     * Returns a full id for the series identifier with the format MSI-DK-184-14
     * @return a full id for the series identifier
     */
    public String getSeriesId() {
        return formatSeriesId(get().getSeriesIdentifier());
    }

    /**
     * Formats an ID into a full id for the series identifier with the format MSI-DK-184-14
     * @return a full id for the series identifier
     */
    public String formatSeriesId(MCSeriesIdentifier id) {
        String shortId = (id.getNumber() != null)
                ? String.format("%s-%03d-%02d", id.getAuthority(), id.getNumber(), id.getYear() - 2000)
                : String.format("%s-?-%02d", id.getAuthority(), id.getYear() - 2000);
        return String.format("%s-%s", id.getMainType(), shortId);
    }

    /**
     * Returns if this message is an MSI
     * @return if this message is an MSI
     */
    public boolean isMsi() {
        return get().getSeriesIdentifier().getMainType() == MCSeriesIdType.MSI;
    }

    /**
     * Returns if this message is an NM
     * @return if this message is an NM
     */
    public boolean isNm() {
        return !isMsi();
    }

    /**
     * Returns the message area lineage from parent-most area and down.
     * @param startIndex start with the startIndex'th parent-most area
     * @param maxParts the max lineage number
     * @return the area lineage
     */
    public String getAreaLineage(int startIndex, int maxParts) {
        List<String> parts = new ArrayList<>();
        for (MCArea area = get().getArea(); area != null; area = area.getParent()) {
            if (area.getDescs().size() > 0 && StringUtils.isNotBlank(area.getDescs().get(0).getName())) {
                parts.add(0, area.getDescs().get(0).getName());
            }
        }
        if (get().getDescs().size() > 0 && StringUtils.isNotBlank(get().getDescs().get(0).getVicinity())) {
            parts.add(get().getDescs().get(0).getVicinity());
        }
        if (parts.size() <= startIndex) {
            return "";
        }
        parts = parts.subList(startIndex, Math.min(parts.size(), startIndex + maxParts));
        return StringUtils.join(parts, " - ");
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
     * Returns if the MSI-NM is close the the new route
     *
     * NB: This code was from the old MsiLayer. Should be optimized!
     *
     * @param route the new route
     * @param mousePosition the mouse position
     * @param projection the projection
     * @return if the MSI-NM is close the the new route
     */
    public boolean nearNewRoute(Route route, Position mousePosition, Projection projection) {

        double visibilityFromNewWaypoint = EPD.getInstance().getSettings().getEnavSettings().getMsiVisibilityFromNewWaypoint();

        // Check if MSI messages should be visible on route.
        boolean visibleOnRoute = false;

        // Go through each waypoint of the route to check if the MSI message should be visible.
        for (int i = 0; !visibleOnRoute && i < route.getWaypoints().size(); i++) {

            RouteWaypoint rWaypoint = route.getWaypoints().get(i);
            Point2D pointA = null;
            Point2D pointB = null;
            Point2D pnt;

            // If the waypoint is not the last placed waypoint compare it to the next in line.
            // Else compare it to the mouse location.
            if (rWaypoint == route.getWaypoints().getLast()) {
                pointA = projection.forward(rWaypoint.getPos().getLatitude(), rWaypoint.getPos().getLongitude());
                pointB = projection.forward(mousePosition.getLatitude(), mousePosition.getLongitude());
            } else if (rWaypoint != route.getWaypoints().getLast()) {
                RouteWaypoint nWaypoint = route.getWaypoints().get(i+1);
                pointA = projection.forward(rWaypoint.getPos().getLatitude(), rWaypoint.getPos().getLongitude());
                pointB = projection.forward(nWaypoint.getPos().getLatitude(), nWaypoint.getPos().getLongitude());
            }

            // The slope of the line.
            double slope = Math.round(
                    ((pointB.getY() - pointA.getY()) / (pointB.getX() - pointA.getX())) * visibilityFromNewWaypoint);

            // If the value of slope is more than the value of visibilityFromNewWaypoint,
            // change the slop reverse the x and y axis.
            if (Math.abs(slope) > visibilityFromNewWaypoint) {
                double dy = Math.abs(pointB.getY()-pointA.getY());
                slope = Math.round(((pointB.getX() - pointA.getX()) / (pointB.getY() - pointA.getY())) * visibilityFromNewWaypoint);
                for (int j = 0; j*visibilityFromNewWaypoint < dy; j++) {
                    pnt = pointA;

                    // The first point should be placed a point where the mouse was clicked.
                    if (j == 0) {
                        visibleOnRoute = setMessageVisible(visibilityFromNewWaypoint, visibleOnRoute, projection, pnt);
                        continue;
                    }

                    //Mouse placed on the right side of the last placed waypoint.
                    if (pointA.getX() <= pointB.getX()) {

                        if (slope > 0) {
                            pnt.setLocation(pointA.getX()+slope, pointA.getY()+visibilityFromNewWaypoint);
                        } else if (slope < 0) {
                            double posSlope = Math.abs(slope);
                            pnt.setLocation(pointA.getX()+posSlope, pointA.getY()-visibilityFromNewWaypoint);
                        }

                        // mouse placed on the left side.
                    } else if (pointA.getX() > pointB.getX()) {

                        if (slope > 0) {
                            pnt.setLocation(pointA.getX()-slope, pointA.getY()-visibilityFromNewWaypoint);
                        } else if (slope < 0) {
                            double posSlope = Math.abs(slope);
                            pnt.setLocation(pointA.getX()-posSlope, pointA.getY()+visibilityFromNewWaypoint);
                        }
                    }

                    // Handles placing of point on a vertical line.
                    if (pointA.getY() < pointB.getY() && slope == 0) {
                        pnt.setLocation(pointA.getX(), pointA.getY()+visibilityFromNewWaypoint);
                    } else if (pointA.getY() > pointB.getY() && slope == 0) {
                        pnt.setLocation(pointA.getX(), pointA.getY()-visibilityFromNewWaypoint);
                    }

                    visibleOnRoute = setMessageVisible(visibilityFromNewWaypoint, visibleOnRoute, projection, pnt);
                }
            } else {
                double dx = Math.abs(pointB.getX()-pointA.getX());
                for (int j = 0; j*visibilityFromNewWaypoint < dx; j++) {
                    pnt = pointA;

                    if (j == 0) {
                        visibleOnRoute = setMessageVisible(visibilityFromNewWaypoint, visibleOnRoute, projection, pnt);
                        continue;
                    }

                    // Mouse placed on the right side of the last placed waypoint.
                    if (pointA.getX() <= pointB.getX()) {

                        if (slope > 0) {
                            pnt.setLocation(pointA.getX()+visibilityFromNewWaypoint, pointA.getY()+slope);
                        } else if (slope < 0) {
                            double posSlope = Math.abs(slope);
                            pnt.setLocation(pointA.getX()+visibilityFromNewWaypoint, pointA.getY()-posSlope);
                        }

                        // Mouse placed on the left side of the last placed waypoint.
                    } else if (pointA.getX() > pointB.getX()) {

                        if (slope > 0) {
                            pnt.setLocation(pointA.getX()-visibilityFromNewWaypoint, pointA.getY()-slope);
                        } else if (slope < 0) {
                            double posSlope = Math.abs(slope);
                            pnt.setLocation(pointA.getX()-visibilityFromNewWaypoint, pointA.getY()+posSlope);
                        }
                    }

                    if (pointA.getX() < pointB.getX() &&
                            slope == 0) {
                        pnt.setLocation(pointA.getX()+visibilityFromNewWaypoint, pointA.getY());
                    } else if (pointA.getX() > pointB.getX() &&
                            slope == 0) {
                        pnt.setLocation(pointA.getX()-visibilityFromNewWaypoint, pointA.getY());
                    }

                    visibleOnRoute = setMessageVisible(visibilityFromNewWaypoint, visibleOnRoute, projection, pnt);
                }
            }
        }

        return visibleOnRoute;
    }

    private boolean setMessageVisible(double visibilityFromNewWaypoint, boolean visibleOnRoute,  Projection projection, Point2D pnt) {
        LatLonPoint llpnt = projection.inverse(pnt);
        Position position = Position.create(llpnt.getLatitude(), llpnt.getLongitude());
        Double dist = getDistanceToPosition(position);
        if (dist != null && dist <= visibilityFromNewWaypoint) {
            visibleOnRoute = true;
        }
        return visibleOnRoute;
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

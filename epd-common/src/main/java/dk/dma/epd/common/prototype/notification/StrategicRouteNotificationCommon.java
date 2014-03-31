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
package dk.dma.epd.common.prototype.notification;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.text.Formatter;

/**
 * An common strategic route implementation of the {@linkplain Notification} class
 */
public abstract class StrategicRouteNotificationCommon extends Notification<StrategicRouteNegotiationData, Long> {

    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor
     * 
     * @param routeData the strategic route data
     */
    public StrategicRouteNotificationCommon(StrategicRouteNegotiationData routeData) {
        super(routeData, routeData.getId(), NotificationType.STRATEGIC_ROUTE);

        acknowledged = read = routeData.isHandled();
        date = routeData.getLatestSentDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAcknowledge() {
        // No Dismiss button in pop-up notifications
        return false;
    }
    
    /**
     * Returns the name of the caller
     * @return the name of the caller
     */
    public abstract String getCallerlName();
    
    /**
     * Compares two routes and outlines the differences
     * @param originalRoute the original route
     * @param newRoute the new route to compare with the original
     * @return the differences
     */
    public static String findChanges(Route originalRoute, Route newRoute) {

        StringBuilder changes = new StringBuilder();
        List<RouteWaypoint> wp1 = originalRoute.getWaypoints();
        List<RouteWaypoint> wp2 = newRoute.getWaypoints();

        if (!StringUtils.equals(originalRoute.getName(), newRoute.getName())) {
            changes.append(String.format("Name changed from '%s' to '%s'\n", originalRoute.getName(), newRoute.getName()));
        }
        
        if (wp1.size() == wp2.size()) {
            
            for (int i = 0; i < wp1.size(); i++) {
                if (!equals(wp1.get(i).getPos(), wp2.get(i).getPos())) {
                    changes.append("Wp " + (i + 1) + " new position\n");
                }

                if (newRoute.getEtas().get(i).getTime() != originalRoute.getEtas().get(i).getTime()) {
                    changes.append("Wp " + (i + 1) + " ETA Changed from "
                            + Formatter.formatShortDateTimeNoTz(originalRoute.getEtas().get(i))
                            + " to "
                            + Formatter.formatShortDateTimeNoTz(newRoute.getEtas().get(i)) 
                            + "\n");
                }
            }

        } else {
            if (wp2.size() > wp1.size()) {
                changes.append((wp2.size() - wp1.size()) + " new waypoints added");
            } else {
                changes.append((wp1.size() - wp2.size()) + " new waypoints removed");
            }
        }

        if (changes.length() == 0) {
            changes.append("No changes in receieved route");
        }

        return changes.toString();
    }
    
    /**
     * Utility method to compare two positions and see if they are almost identical
     * @param d1 the first double value
     * @param d2 the second double value
     * @return if they are almost identical
     */
    private static boolean equals(Position p1, Position p2) {
        return Math.abs(p1.getLatitude() - p2.getLatitude()) < 0.0000001 &&
                Math.abs(p1.getLongitude() - p2.getLongitude()) < 0.0000001;
    }    
 }

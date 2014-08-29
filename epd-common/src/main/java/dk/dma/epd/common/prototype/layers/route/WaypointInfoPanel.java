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
package dk.dma.epd.common.prototype.layers.route;

import java.awt.geom.Point2D;
import java.util.Date;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Calculator;

/**
 * Mouse over info for waypoint.
 */
public class WaypointInfoPanel extends InfoPanel {
    private static final long serialVersionUID = 1L;

    public WaypointInfoPanel() {
        super();
    }

    public void showWpInfo(Route route, int wpIndex) {
        RouteWaypoint wp = route.getWaypoints().get(wpIndex);

        ActiveRoute activeRoute = null;
        if (route instanceof ActiveRoute) {
            activeRoute = (ActiveRoute) route;
        } else {
            route.adjustStartTime();
        }

        Date eta = null;
        Long ttg = null;
        if (activeRoute != null) {
            activeRoute.reCalcRemainingWpEta();
            eta = activeRoute.getWpEta(wpIndex);
        } else {
            eta = route.getWpEta(wpIndex);
        }

        if (eta != null) {
            ttg = eta.getTime() - PntTime.getDate().getTime();
            if (ttg < 0) {
                ttg = null;
            }
        }

        Double dtg = null;
        if (activeRoute == null) {
            dtg = route.getWpRngSum(wpIndex);
        } else {
            if (activeRoute.getActiveWaypointIndex() <= wpIndex) {
                dtg = activeRoute.getActiveWpRng();
                if (dtg != null) {
                    for (int i = activeRoute.getActiveWaypointIndex(); i < wpIndex; i++) {
                        dtg += activeRoute.getWpRng(i);
                    }
                }
            }
        }

        StringBuilder str = new StringBuilder();
        str.append("<html>");
        str.append(wp.getName() + "<br/>");
        str.append(Formatter.latToPrintable(wp.getPos().getLatitude()) + " - "
                + Formatter.lonToPrintable(wp.getPos().getLongitude()) + "<br/>");
        str.append("<table border='0' cellpadding='2'>");
        if (ttg != null) {
            str.append("<tr><td>TTG:</td><td>" + Formatter.formatTime(ttg) + "</td></tr>");
        }
        if (dtg != null) {
            str.append("<tr><td>DTG:</td><td>" + Formatter.formatDistNM(dtg, 2) + "</td></tr>");
        }
        str.append("<tr><td>ETA:</td><td>" + Formatter.formatShortDateTime(eta) + "</td></tr>");

        if (wp.getOutLeg() != null) {
            str.append("<tr><td>SPD:</td><td>" + Formatter.formatSpeed(wp.getOutLeg().getSpeed()) + "</td></tr>");
        }
        str.append("</table>");
        str.append("</html>");
        showText(str.toString());
    }

    public void showLegInfo(RouteLegGraphic legGraphic, Point2D worldLocation) {
        int legIndex = legGraphic.getLegIndex();
        Route routeData = legGraphic.getRouteGraphic().getRoute();

        RouteWaypoint startWp = legGraphic.getRouteLeg().getStartWp();
        RouteLeg leg = startWp.getOutLeg();

        Position startPos = startWp.getPos();
        Position midPos = Position.create(worldLocation.getY(), worldLocation.getX());
        Position endPos = legGraphic.getRouteLeg().getEndWp().getPos();
        double range = Calculator.range(startPos, endPos, legGraphic.getRouteLeg().getHeading());
        double midRange = Calculator.range(startPos, midPos, legGraphic.getRouteLeg().getHeading());
        double hdg = Calculator.bearing(startPos, endPos, legGraphic.getRouteLeg().getHeading());
        Date startEta = routeData.getEtas().get(legIndex);

        Date midEta = new Date((long) (midRange / leg.getSpeed() * 3600000 + startEta.getTime()));

        StringBuilder str = new StringBuilder();
        str.append("<html>");
        str.append("<b>Active route leg</b><br/>");
        str.append("<table border='0' cellpadding='2'>");
        str.append("<tr><td>Length:</td><td>" + Formatter.formatDistNM(range) + "</td></tr>");
        str.append("<tr><td>Heading:</td><td>" + Formatter.formatDegrees(hdg, 0) + "</td></tr>");
        str.append("<tr><td>Speed:</td><td>" + Formatter.formatSpeed(routeData.getSpeed(legIndex)) + "</td></tr>");
        str.append("<tr><td>ETA here:</td><td>" + Formatter.formatShortDateTime(midEta) + "</td></tr>");
        str.append("</table>");
        str.append("</html>");

        showText(str.toString());
    }
}

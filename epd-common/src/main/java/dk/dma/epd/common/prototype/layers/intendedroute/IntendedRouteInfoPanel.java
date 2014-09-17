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
package dk.dma.epd.common.prototype.layers.intendedroute;

import java.awt.geom.Point2D;
import java.util.Date;

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Calculator;

public class IntendedRouteInfoPanel extends InfoPanel {
    private static final long serialVersionUID = 1L;

    public IntendedRouteInfoPanel() {
        super();
    }

    public void showWpInfo(IntendedRouteWpCircle wpCircle) {
        IntendedRoute routeData = wpCircle.getIntendedRouteGraphic()
                .getIntendedRoute();
        if (routeData == null) {
            showText("");
            return;
        }
        StringBuilder str = new StringBuilder();
        str.append("<html>");
        str.append("<b>Intended route waypoint</b><br/>");
        str.append(wpCircle.getIntendedRouteGraphic().getName() + "<br/>");
        str.append("<table border='0' cellpadding='2'>");
        str.append("<tr><td>Route age:</td><td>"
                + getAge(routeData.getReceived()) + "</td></tr>");
        str.append("<tr><td>ETA:</td><td>"
                + Formatter.formatShortDateTime(routeData.getEtas().get(
                        wpCircle.getIndex())) + "</td></tr>");
        str.append("</table>");
        str.append("</html>");

        showText(str.toString());

    }

    private String getAge(Date received) {
        return Formatter.formatTimeShort(PntTime.getDate().getTime()
                - received.getTime())
                + " (mm:ss)";
    }

    public void showLegInfo(IntendedRouteLegGraphic legGraphic,
            Point2D worldLocation) {
        IntendedRoute routeData = ((IntendedRouteLegGraphic) legGraphic)
                .getIntendedRouteGraphic().getIntendedRoute();

        RouteWaypoint startWp;

        Position startPos;
        Position midPos;
        double midRange;
        Date startEta;

        DateTime intendedRouteETA = null;

        if (legGraphic.isActiveWpLine()) {

            startPos = EPD.getInstance().getAisHandler()
                    .getVesselTarget(routeData.getMmsi()).getPositionData()
                    .getPos();

            midPos = Position
                    .create(worldLocation.getY(), worldLocation.getX());

            midRange = Calculator.range(startPos, midPos, Heading.RL);

            startEta = PntTime.getDate();

            Date endEta = routeData.getEtas().get(routeData.getActiveWpIndex());

            long milisecondsTravelTime = endEta.getTime() - startEta.getTime();

            Position endPosition = routeData.getWaypoints()
                    .get(routeData.getActiveWpIndex()).getPos();

            double lengthToTravel = Calculator.range(startPos, endPosition,
                    Heading.RL);

            double speed = (lengthToTravel / milisecondsTravelTime) * 1000 * 60 * 60;

            intendedRouteETA = new DateTime(
                    (long) (midRange / speed * 3600000 + startEta.getTime()));

        } else {
            startWp = routeData.getWaypoints()
                    .get(routeData.getActiveWpIndex());
            RouteLeg leg;
            leg = startWp.getOutLeg();

            startPos = startWp.getPos();

            midPos = Position
                    .create(worldLocation.getY(), worldLocation.getX());
            midRange = Calculator.range(startPos, midPos, leg.getHeading());

            startEta = routeData.getEtas().get(routeData.getActiveWpIndex());

            intendedRouteETA = new DateTime((long) (midRange / leg.getSpeed()
                    * 3600000 + startEta.getTime()));

        }

        // double range = Calculator.range(startPos, endPos, leg.getHeading());
        // double midRange = Calculator.range(startPos, midPos,
        // leg.getHeading());
        // double hdg = Calculator.bearing(startPos, endPos, leg.getHeading());

        // Date midEta = new Date(
        // (long) (midRange / leg.getSpeed() * 3600000 + startEta
        // .getTime()));

        StringBuilder str = new StringBuilder();
        str.append("<html>");
        str.append("<b>Intended route leg</b><br/>");
        str.append(legGraphic.getIntendedRouteGraphic().getName() + "<br/>");
        str.append("<table border='0' cellpadding='2'>");

        str.append("<tr><td>ETA here:</td><td>"
                + Formatter.formatShortDateTime(new Date(intendedRouteETA
                        .getMillis())) + "</td></tr>");

        str.append("</table>");
        str.append("</html>");

        showText(str.toString());
    }

    public void showLegInfo(IntendedRouteLegGraphic legGraphic,
            Point2D worldLocation, Position intendedRoutePosition) {
        int legIndex = legGraphic.getIndex();
        IntendedRoute routeData = ((IntendedRouteLegGraphic) legGraphic)
                .getIntendedRouteGraphic().getIntendedRoute();

        if (legIndex - 1 < routeData.getActiveWpIndex()
                && !legGraphic.isActiveWpLine()) {
            showText("");
            return;
        }

        RouteWaypoint startWp;

        Position startPos;
        Position midPos;
        double midRange;
        Date startEta;

        DateTime intendedRouteETA = null;

        if (legGraphic.isActiveWpLine()) {
            // System.out.println("Active WP Line");
            // leg = startWp.getOutLeg();

            // if (EPD.getInstance().getAisHandler()
            // .getVesselTarget(routeData.getMmsi()).getPositionData()
            // .getSog() <= 0) {
            //
            // return;
            //
            // }

            startPos = EPD.getInstance().getAisHandler()
                    .getVesselTarget(routeData.getMmsi()).getPositionData()
                    .getPos();

            midPos = Position
                    .create(worldLocation.getY(), worldLocation.getX());

            midRange = Calculator.range(startPos, midPos, Heading.RL);

            startEta = PntTime.getDate();

            Date endEta = routeData.getEtas().get(routeData.getActiveWpIndex());

            long milisecondsTravelTime = endEta.getTime() - startEta.getTime();

            Position endPosition = routeData.getWaypoints()
                    .get(routeData.getActiveWpIndex()).getPos();

            double lengthToTravel = Calculator.range(startPos, endPosition,
                    Heading.RL);

            double speed = (lengthToTravel / milisecondsTravelTime) * 1000 * 60 * 60;

            intendedRouteETA = new DateTime(
                    (long) (midRange / speed * 3600000 + startEta.getTime()));

        } else {
            startWp = routeData.getWaypoints()
                    .get(routeData.getActiveWpIndex());
            RouteLeg leg;
            leg = startWp.getOutLeg();

            startPos = startWp.getPos();
            midPos = Position
                    .create(worldLocation.getY(), worldLocation.getX());
            midRange = Calculator.range(startPos, midPos, leg.getHeading());

            startEta = routeData.getEtas().get(routeData.getActiveWpIndex());

            intendedRouteETA = new DateTime((long) (midRange / leg.getSpeed()
                    * 3600000 + startEta.getTime()));

        }

        // double range = Calculator.range(startPos, endPos, leg.getHeading());
        // double midRange = Calculator.range(startPos, midPos,
        // leg.getHeading());
        // double hdg = Calculator.bearing(startPos, endPos, leg.getHeading());

        double distanceRoutes = Calculator.range(midPos, intendedRoutePosition,
                Heading.RL);

        // Date midEta = new Date(
        // (long) (midRange / leg.getSpeed() * 3600000 + startEta
        // .getTime()));

        StringBuilder str = new StringBuilder();
        str.append("<html>");
        str.append("<b>Intended route leg</b><br/>");
        str.append(legGraphic.getIntendedRouteGraphic().getName() + "<br/>");
        str.append("<table border='0' cellpadding='2'>");
        str.append("<tr><td>ETA here:</td><td>"
                + Formatter.formatShortDateTime(new Date(intendedRouteETA
                        .getMillis())) + "</td></tr>");
        str.append("<tr><td>Distance:</td><td>"
                + Formatter.formatDistNM(distanceRoutes) + "</td></tr>");
        str.append("</table>");
        str.append("</html>");

        showText(str.toString());
    }

}

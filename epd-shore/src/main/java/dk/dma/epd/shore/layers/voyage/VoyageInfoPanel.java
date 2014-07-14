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
package dk.dma.epd.shore.layers.voyage;

import java.awt.geom.Point2D;
import java.util.Date;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLegGraphic;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.shore.voyage.Voyage;

public class VoyageInfoPanel extends InfoPanel {
    private static final long serialVersionUID = 1L;

    public VoyageInfoPanel() {
        super();
    }

    public void showVoyageInfo(Voyage voyage, String shipname) {
        if (voyage == null) {
            showText("");
            return;
        }
        
        try{
            Route route = voyage.getRoute();
            
            StringBuilder str = new StringBuilder();
            str.append("<html>");
            str.append("<b>Accepted Voyage from:</b><br/>");
            str.append(shipname + "<br/>");
//            str.append(Formatter.latToPrintable(wp.getLatitude()) + " - " + Formatter.lonToPrintable(wp.getLongitude()) + "<br/>");
//            str.append("<table border='0' cellpadding='2'>");
//            str.append("<tr><td>RNG:</td><td>" + Formatter.formatDistNM(routeData.getRange(wpCircle.getIndex())) + "</td></tr>");
            str.append("<tr><td>Between:</td><td>" + Formatter.formatShortDateTime(route.getEtas().get(0)) + "</td></tr>");
            str.append("<tr><td>And:</td><td>" +  Formatter.formatShortDateTime(route.getEtas().get(route.getEtas().size()-1)) + "</td></tr>");
            str.append("</table>");
            str.append("</html>");

            showText(str.toString());
        }catch(Exception e){
            StringBuilder str = new StringBuilder();
            showText(str.toString());
        }
        


    }

    public void showLegInfo(IntendedRouteLegGraphic legGraphic, Point2D worldLocation) {
        int legIndex = legGraphic.getIndex();
        if (legIndex == 0) {
            return;
        }
//        AisIntendedRoute routeData = legGraphic.getIntendedRouteGraphic().getVesselTarget().getAisRouteData();
        IntendedRoute routeData = legGraphic.getIntendedRouteGraphic().getIntendedRoute();
        RouteWaypoint startWp = routeData.getWaypoints().get(legIndex - 1);
        RouteLeg leg = startWp.getOutLeg();
        
        Position startPos = startWp.getPos();
        Position midPos = Position.create(worldLocation.getY(), worldLocation.getX());
        Position endPos = leg.getEndWp().getPos();
        double range = Calculator.range(startPos,endPos, leg.getHeading());
        double midRange = Calculator.range(startPos, midPos, leg.getHeading());
        double hdg = Calculator.bearing(startPos, endPos, leg.getHeading());
        Date startEta = routeData.getEtas().get(legIndex - 1);
        
        Date midEta = new Date((long)(midRange / routeData.getSpeed(legIndex) * 3600000 + startEta.getTime()));
        Date endEta = routeData.getEtas().get(legIndex);
        
        StringBuilder str = new StringBuilder();
        str.append("<html>");
        str.append("<b>Intended route leg</b><br/>");
        str.append(legGraphic.getIntendedRouteGraphic().getName() + "<br/>");
        str.append("<table border='0' cellpadding='2'>");
        str.append("<tr><td>DST:</td><td>" + Formatter.formatDistNM(range) + "  HDG " + Formatter.formatDegrees(hdg, 0) + "</td></tr>");
        str.append("<tr><td>START:</td><td>" + Formatter.formatShortDateTime(startEta) + "</td></tr>");
        str.append("<tr><td>ETA here:</td><td>" + Formatter.formatShortDateTime(midEta) + "</td></tr>");
        str.append("<tr><td>END:</td><td>" + Formatter.formatShortDateTime(endEta) + "");
        str.append("<tr><td>AVG SPD:</td><td>" + Formatter.formatSpeed(routeData.getSpeed(legIndex)) + "</td></tr>");
        str.append("</table>");
        str.append("</html>");

        showText(str.toString());
    }

}

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
package dk.dma.epd.shore.layers.ais;

import java.awt.geom.Point2D;
import java.util.Date;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.enavcloud.CloudIntendedRoute;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.ais.IntendedRouteLegGraphic;
import dk.dma.epd.common.prototype.layers.ais.IntendedRouteWpCircle;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Calculator;

public class IntendedRouteInfoPanel extends InfoPanel {
    private static final long serialVersionUID = 1L;

    public IntendedRouteInfoPanel() {
        super();
    }

    public void showWpInfo(IntendedRouteWpCircle wpCircle) {
//        AisIntendedRoute routeData = wpCircle.getIntendedRouteGraphic().getVesselTarget().getAisRouteData();
        CloudIntendedRoute routeData = wpCircle.getIntendedRouteGraphic().getVesselTarget().getIntendedRoute();
        if (routeData == null) {
            showText("");
            return;
        }
        
        try{
            Position wp = wpCircle.getIntendedRouteGraphic().getVesselTarget().getIntendedRoute().getWaypoints().get(wpCircle.getIndex()).getPos();
            StringBuilder str = new StringBuilder();
            str.append("<html>");
            str.append("<b>Intended route waypoint</b><br/>");
            str.append(wpCircle.getIntendedRouteGraphic().getName() + "<br/>");
            str.append(Formatter.latToPrintable(wp.getLatitude()) + " - " + Formatter.lonToPrintable(wp.getLongitude()) + "<br/>");
            str.append("<table border='0' cellpadding='2'>");
            str.append("<tr><td>RNG:</td><td>" + Formatter.formatDistNM(routeData.getRange(wpCircle.getIndex())) + "</td></tr>");
            str.append("<tr><td>ETA:</td><td>" + Formatter.formatShortDateTime(routeData.getEtas().get(wpCircle.getIndex())) + "</td></tr>");
//            str.append("<tr><td>AVG SPD:</td><td>" + Formatter.formatSpeed(routeData.getSpeed()) + "</td></tr>");
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
        CloudIntendedRoute routeData = legGraphic.getIntendedRouteGraphic().getVesselTarget().getIntendedRoute();
        Position startPos = routeData.getWaypoints().get(legIndex - 1).getPos();
        Position midPos = Position.create(worldLocation.getY(), worldLocation.getX());
        Position endPos = routeData.getWaypoints().get(legIndex).getPos();
        double range = Calculator.range(startPos,endPos, Heading.RL);
        double midRange = Calculator.range(startPos, midPos, Heading.RL);
        double hdg = Calculator.bearing(startPos, endPos, Heading.RL);
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

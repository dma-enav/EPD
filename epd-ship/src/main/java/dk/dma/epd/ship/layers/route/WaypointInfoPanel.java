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
package dk.dma.epd.ship.layers.route;

import java.util.Date;

import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.gui.InfoPanel;

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
            activeRoute = (ActiveRoute)route;
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
            ttg = eta.getTime() - GnssTime.getInstance().getDate().getTime();
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
        str.append(Formatter.latToPrintable(wp.getPos().getLatitude()) + " - " + Formatter.lonToPrintable(wp.getPos().getLongitude()) + "<br/>");
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
    
}

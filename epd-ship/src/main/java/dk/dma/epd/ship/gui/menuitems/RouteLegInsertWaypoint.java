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
package dk.dma.epd.ship.gui.menuitems;

import java.awt.Point;

import javax.swing.JMenuItem;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.math.Vector2D;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.ship.route.RouteManager;

public class RouteLegInsertWaypoint extends JMenuItem implements IMapMenuAction {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private RouteLeg routeLeg;
    private Point point;
    private MapBean mapBean;
    private RouteManager routeManager;
    private int routeIndex;

    public RouteLegInsertWaypoint(String text) {
        super();
        setText(text);
    }
    
    @Override
    public void doAction() {
        Position startWaypoint = routeLeg.getStartWp().getPos();
        Position endWaypoint = routeLeg.getEndWp().getPos();
        Projection projection = mapBean.getProjection();
        LatLonPoint newPoint = projection.inverse(point);
        
        Vector2D routeLegVector = new Vector2D(startWaypoint.getLongitude(), 
                startWaypoint.getLatitude(), 
                endWaypoint.getLongitude(), 
                endWaypoint.getLatitude());
        
        Vector2D newVector = new Vector2D(startWaypoint.getLongitude(), 
                startWaypoint.getLatitude(), 
                newPoint.getLongitude(), 
                newPoint.getLatitude());
        
        Vector2D projectedVector = routeLegVector.projection(newVector);
        
        Position newGeoLocation = Position.create(projectedVector.getY2(), projectedVector.getX2());
        
        Route route = routeManager.getRoute(routeIndex);
        route.createWaypoint(routeLeg, newGeoLocation);
        routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_APPENDED);
    }
    
    public void setRouteLeg(RouteLeg routeLeg) {
        this.routeLeg = routeLeg;
    }
    
    public void setPoint(Point point) {
        this.point = point;
    }
    
    public void setMapBean(MapBean mapBean) {
        this.mapBean = mapBean;
    }
    
    public void setRouteIndex(int routeIndex) {
        this.routeIndex = routeIndex;
    }
    
    public void setRouteManager(RouteManager routeManager) {
        this.routeManager = routeManager;
    }

}

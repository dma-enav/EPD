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
package dk.dma.epd.common.prototype.gui.menuitems;

import java.awt.Point;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.math.Vector2D;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;

public class RouteLegInsertWaypoint extends RouteMenuItem {
    
    private static final long serialVersionUID = 1L;
    
    private RouteLeg routeLeg;
    private Point point;
    private MapBean mapBean;

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
        
        Route route = EPD.getInstance().getRouteManager().getRoute(routeIndex);
        route.createWaypoint(routeLeg, newGeoLocation);
        EPD.getInstance().getRouteManager().notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_APPENDED);
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
}

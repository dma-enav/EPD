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

import javax.swing.JMenuItem;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.math.Vector2D;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.voyage.VoyageEventDispatcher;
import dk.dma.epd.common.prototype.model.voyage.VoyageUpdateEvent;

public class VoyageHandlingLegInsertWaypoint extends JMenuItem implements
        IMapMenuAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private RouteLeg routeLeg;
    private Point point;
    private MapBean mapBean;
    private Route route;
    private int routeIndex;
    private VoyageEventDispatcher voyageEventDispatcher;

    /**
     * Creates a VoyageHandlingLegInsertWaypoint menu item.
     * 
     * @param text
     *            The text displayed by the menu item.
     * @param voyageEventDispatcher
     *            The event dispatcher used to notify listeners of voyage
     *            updates.
     */
    public VoyageHandlingLegInsertWaypoint(String text,
            VoyageEventDispatcher voyageEventDispatcher) {
        super(text);
        this.voyageEventDispatcher = voyageEventDispatcher;
    }

    @Override
    public void doAction() {
        Position startWaypoint = routeLeg.getStartWp().getPos();
        Position endWaypoint = routeLeg.getEndWp().getPos();
        Projection projection = mapBean.getProjection();
        LatLonPoint newPoint = projection.inverse(point);

        Vector2D routeLegVector = new Vector2D(startWaypoint.getLongitude(),
                startWaypoint.getLatitude(), endWaypoint.getLongitude(),
                endWaypoint.getLatitude());

        Vector2D newVector = new Vector2D(startWaypoint.getLongitude(),
                startWaypoint.getLatitude(), newPoint.getLongitude(),
                newPoint.getLatitude());

        Vector2D projectedVector = routeLegVector.projection(newVector);

        Position newGeoLocation = Position.create(projectedVector.getY2(),
                projectedVector.getX2());

        route.createWaypoint(routeLeg, newGeoLocation);
        // Notify listeners of new voyage waypoint
        this.voyageEventDispatcher.notifyListenersOfVoyageUpdate(
                VoyageUpdateEvent.WAYPOINT_INSERTED, this.route,
                this.routeIndex);
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

    /**
     * @param route
     *            the route to set
     */
    public void setRoute(Route route) {
        this.route = route;
    }

    /**
     * Set the route index that specifies the "type" of the route associated
     * with this menu item (e.g. if it is a modified STCC route)
     * 
     * @param routeIndex
     *            The new route index.
     */
    public void setRouteIndex(int routeIndex) {
        this.routeIndex = routeIndex;
    }
}

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

import java.awt.Color;
import java.awt.Stroke;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.route.Route;

/**
 * Graphic for active route
 */
public class ActiveRouteGraphic extends RouteGraphic {

    private static final long serialVersionUID = 1L;
    private ActiveRouteLegGraphic activeWpLine;

    public ActiveRouteGraphic(Route route, int routeIndex,
            boolean arrowsVisible, Stroke stroke, Color color) {
        super(route, routeIndex, arrowsVisible, stroke, color);
    }

    public ActiveRouteGraphic(Route route, int routeIndex,
            boolean arrowsVisible, Stroke stroke, Color color,
            Color backgroundColor) {
        super(route, routeIndex, arrowsVisible, stroke, color, backgroundColor,
                false, false);
    }

    public void updateActiveWpLine(Position vesselPos) {
        // if (activeWpLine != null) {
        // graphics.remove(activeWpLine);
        // }

        activeWpLine = new ActiveRouteLegGraphic(this, vesselPos,
                routeWaypoints.get(
                        EPD.getInstance().getRouteManager().getActiveRoute()
                                .getActiveWaypointIndex()).getPos(),
                Heading.RL, Color.red, SCALE);

        add(activeWpLine);

    }

}

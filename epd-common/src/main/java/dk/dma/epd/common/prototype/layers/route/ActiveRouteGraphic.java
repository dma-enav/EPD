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
package dk.dma.epd.common.prototype.layers.route;

import java.awt.Color;
import java.awt.Stroke;

import dk.dma.epd.common.prototype.model.route.Route;

/**
 * Graphic for active route
 */
public class ActiveRouteGraphic extends RouteGraphic {

    private static final long serialVersionUID = 1L;

//    protected List<RouteLegGraphic> routeLegs = new ArrayList<>();

    public ActiveRouteGraphic(Route route, int routeIndex, boolean arrowsVisible, Stroke stroke, Color color) {
        super(route, routeIndex, arrowsVisible, stroke, color);
    }

    public ActiveRouteGraphic(Route route, int routeIndex, boolean arrowsVisible, Stroke stroke, Color color, Color backgroundColor) {
        super(route, routeIndex, arrowsVisible, stroke, color, backgroundColor, false, false);
    }

}

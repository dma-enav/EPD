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
package dk.dma.epd.common.prototype.gui.menuitems;

import dk.dma.epd.common.prototype.route.RouteManagerCommon;

/**
 * Used to hide routes.
 * <p>
 * If the {@code routeIndex} equals {@code ALL_INACTIVE_ROUTES} then all inactive routes
 * are hidden. Otherwise, the route with the given index is hidden.
 */
public class RouteHide extends RouteMenuItem<RouteManagerCommon> {
    
    private static final long serialVersionUID = 1L;
    public static final int ALL_INACTIVE_ROUTES = -1;
    
    public RouteHide(String text) {
        super();
        setText(text);
    }
    
    @Override
    public void doAction() {
        if (routeIndex == ALL_INACTIVE_ROUTES) {
            routeManager.hideInactiveRoutes();
        } else {
            routeManager.hideRoute(routeIndex);
        }
    }
}

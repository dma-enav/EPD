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

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;

/**
 * Common super class for route-related menu items
 */
public abstract class RouteMenuItem<RM extends RouteManagerCommon> extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;

    protected int routeIndex;
    protected RM routeManager;
    
    /**
     * Sets the current route index
     * @param routeIndex the current route index
     */
    public void setRouteIndex(int routeIndex) {
        this.routeIndex = routeIndex;
    }
    
    /**
     * Sets the current route manager
     * @param routeManager the current route manager
     */
    public void setRouteManager(RM routeManager) {
        this.routeManager = routeManager;
    }
}

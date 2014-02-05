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
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.service.IIntendedRouteListener;

/**
 * Toggle displaying the intended route for a vessel target
 */
public class IntendedRouteToggle extends JMenuItem implements
        IMapMenuAction {

    private static final long serialVersionUID = 1L;
    
    private IntendedRoute intendedRoute;
    private IIntendedRouteListener listener;

    /**
     * Constructor
     */
    public IntendedRouteToggle() {
        super();
    }
    
    /**
     * Called when the menu item is enacted
     */
    @Override
    public void doAction() {
        intendedRoute.setVisible(!intendedRoute.isVisible());
        listener.intendedRouteUpdated(intendedRoute);
    }
    
    /**
     * Sets the intended route
     * @param intendedRoute the intended route
     */
    public void setIntendedRoute(IntendedRoute intendedRoute) {
        this.intendedRoute = intendedRoute;
    }
    
    /**
     * Sets the listener that should be notified when the route visibility changes
     * @param listener the listener that should be notified when the route visibility changes
     */
    public void setIntendedRouteListener(IIntendedRouteListener listener) {
        this.listener = listener;
    }
}

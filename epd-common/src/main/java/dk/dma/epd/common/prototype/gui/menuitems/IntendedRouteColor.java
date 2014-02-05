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

import java.awt.Color;

import dk.dma.epd.common.prototype.gui.ColorMenuItem;
import dk.dma.epd.common.prototype.gui.MapMenuCommon;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteGraphic;
import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;

/**
 * Map menu item for setting the color of an intended route
 */
public class IntendedRouteColor extends ColorMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;

    private IntendedRouteGraphic routeGraphics;
    private IntendedRouteHandlerCommon intendedRouteHandler;
    
    /**
     * Constructor
     */
    public IntendedRouteColor() {
        super();
        
        addListener(new ColorMenuItem.ColorMenuItemListener() {
            @Override public void colorSelected(Color color) {
                if (routeGraphics != null) {
                    routeGraphics.setRouteColor(color);
                    intendedRouteHandler.fireIntendedRouteUpdated(routeGraphics.getIntendedRoute());
                }
            }});
        }

    /**
     * Called when the menu item is enacted
     */
    @Override
    public void doAction() {
        // Handled by the ColorMenuItemListener in the constructor
    }

    /**
     * Initializes the color menu
     * @param mapMenu the map menu
     * @param routeGraphics the route graphic to change color on
     */
    public void init(MapMenuCommon mapMenu, IntendedRouteGraphic routeGraphics, IntendedRouteHandlerCommon intendedRouteHandler) {
        this.routeGraphics = routeGraphics;
        this.intendedRouteHandler = intendedRouteHandler;
        init(mapMenu, IntendedRouteGraphic.COLORS, routeGraphics.getRouteColor());
    }
}

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
                    intendedRouteHandler.fireIntendedEvent(routeGraphics.getIntendedRoute());
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

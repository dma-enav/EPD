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

import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;

/**
 * Hides all intended routes
 */
public class HideAllIntendedRoutes extends JMenuItem implements IMapMenuAction {
    
    private static final long serialVersionUID = 1L;
    
    private AisHandlerCommon aisHandler;

    /**
     * Constructor
     * @param text
     */
    public HideAllIntendedRoutes(String text) {
        super();
        setText(text);
    }
    
    /**
     * Called when the menu item is enacted
     */
    @Override
    public void doAction() {
        aisHandler.hideAllIntendedRoutes();
    }

    /**
     * Sets the associated AIS handler
     * @param aisHandler
     */
    public void setAisHandler(AisHandlerCommon aisHandler) {
        this.aisHandler = aisHandler;
    }
}

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
package dk.dma.epd.ship.layers.intendedroute;

import java.awt.event.MouseEvent;

import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLayerCommon;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLegGraphic;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteWpCircle;
import dk.dma.epd.ship.gui.MapMenu;

/**
 * Layer for displaying intended routes in EPDShip
 */
public class IntendedRouteLayer extends IntendedRouteLayerCommon {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public IntendedRouteLayer() {
        super();
        
        // Register the classes the will trigger the map menu
        registerMapMenuClasses(IntendedRouteWpCircle.class, IntendedRouteLegGraphic.class);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void initMapMenu(OMGraphic clickedGraphics, MouseEvent evt) {        
        if (clickedGraphics instanceof IntendedRouteWpCircle) {
            
            IntendedRouteWpCircle wpCircle = (IntendedRouteWpCircle) clickedGraphics;
            ((MapMenu)mapMenu).intendedRouteMenu(wpCircle.getIntendedRouteGraphic(), this);
            
        } else if (clickedGraphics instanceof IntendedRouteLegGraphic) {

            IntendedRouteLegGraphic wpLeg = (IntendedRouteLegGraphic) clickedGraphics;
            ((MapMenu)mapMenu).intendedRouteMenu(wpLeg.getIntendedRouteGraphic(), this);
        }
    }
}

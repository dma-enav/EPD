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
package dk.dma.epd.shore.layers.intendedroute;

import java.awt.event.MouseEvent;

import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLayerCommon;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLegGraphic;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteWpCircle;
import dk.dma.epd.shore.gui.views.MapMenu;

/**
 * 
 * Layer for displaying intended routes in EPDShore
 */
public class IntendedRouteLayer extends IntendedRouteLayerCommon {

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(MouseEvent e) {
        
        OMGraphic newClosest = getSelectedGraphic(
                e, 
                IntendedRouteWpCircle.class,
                IntendedRouteLegGraphic.class);

        if (e.getButton() == MouseEvent.BUTTON3 && newClosest != null) {
            
            if (newClosest instanceof IntendedRouteWpCircle) {
    
                IntendedRouteWpCircle wpCircle = (IntendedRouteWpCircle) newClosest;
                VesselTarget vesselTarget = wpCircle.getIntendedRouteGraphic().getVesselTarget();
                getGlassPanel().setVisible(false);
                ((MapMenu)mapMenu).aisSuggestedRouteMenu(vesselTarget, wpCircle.getIntendedRouteGraphic());
                mapMenu.setVisible(true);
                mapMenu.show(this, e.getX() - 2, e.getY() - 2);
                return true;
                
            } else if (newClosest instanceof IntendedRouteLegGraphic) {
    
                IntendedRouteLegGraphic wpLeg = (IntendedRouteLegGraphic) newClosest;
                VesselTarget vesselTarget = wpLeg.getIntendedRouteGraphic().getVesselTarget();
                getGlassPanel().setVisible(false);
                ((MapMenu)mapMenu).aisSuggestedRouteMenu(vesselTarget, wpLeg.getIntendedRouteGraphic());
                mapMenu.setVisible(true);
                mapMenu.show(this, e.getX() - 2, e.getY() - 2);
                return true;
            }
        }
        return false;
    }
}

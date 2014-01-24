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

import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;

/**
 * Toggle displaying the intended route for a vessel target
 */
public class IntendedRouteToggle extends JMenuItem implements
        IMapMenuAction {

    private static final long serialVersionUID = 1L;
    
    private VesselTarget vesselTarget;
    private IAisTargetListener listener;

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
        vesselTarget.getSettings().setShowRoute(!vesselTarget.getSettings().isShowRoute());
        listener.targetUpdated(vesselTarget);
    }
    
    /**
     * Sets the vessel target
     * @param vesselTarget the vessel target
     */
    public void setVesselTarget(VesselTarget vesselTarget) {
        this.vesselTarget = vesselTarget;
    }
    
    /**
     * Sets the listener that should be notified when the route visibility changes
     * @param listener the listener that should be notified when the route visibility changes
     */
    public void setAisTargetListener(IAisTargetListener listener) {
        this.listener = listener;
    }
}

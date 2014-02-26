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
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.layers.ais.VesselGraphicComponentSelector;

/**
 * Creates a menu item for the MapMenuCommon, which will enable visibility of the 
 * target vessel name label.
 * @author adamduehansen
 */
public class ToggleAisTargetName extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;
    private VesselGraphicComponentSelector vesselTargetGraphic;
    private IAisTargetListener iAisTargetListener;
    
    public ToggleAisTargetName() {
        super("Hide AIS target label");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doAction() {

        // Toggle the visbility of the vessel name label.
        this.vesselTargetGraphic.setShowNameLabel(!vesselTargetGraphic.getShowNameLabel());
        
        // Update the ais layer.
        this.iAisTargetListener.targetUpdated(this.vesselTargetGraphic.getVesselTarget());

//        this.aisLayer.doPrepare();
    }
    
    /**
     * Sets the {@link VesselGraphicComponentSelector} of this class.
     * @param vesselGraphicComponentSelector
     */
    public void setVesselTargetGraphic(VesselGraphicComponentSelector vesselGraphicComponentSelector) {
        this.vesselTargetGraphic = vesselGraphicComponentSelector;
    }
    
    /**
     * Sets the 
     * @param iAisTargetListener
     */
    public void setIAisTargetListener(IAisTargetListener iAisTargetListener) {
        this.iAisTargetListener = iAisTargetListener;
    }
}

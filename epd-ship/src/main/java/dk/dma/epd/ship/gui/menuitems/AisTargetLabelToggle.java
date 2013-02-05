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
package dk.dma.epd.ship.gui.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.layers.ais.VesselTargetGraphic;
import dk.dma.epd.ship.layers.ais.AisLayer;

public class AisTargetLabelToggle extends JMenuItem implements IMapMenuAction {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private VesselTargetGraphic vesselTargetGraphic;
    private AisLayer aisLayer;

    public AisTargetLabelToggle() {
        super();
    }
    
    @Override
    public void doAction() {
        vesselTargetGraphic.setShowNameLabel(!vesselTargetGraphic.getShowNameLabel());
        aisLayer.targetUpdated(vesselTargetGraphic.getVesselTarget());
        aisLayer.doPrepare();
    }
    
    public void setVesselTargetGraphic(VesselTargetGraphic vesselTargetGraphic) {
        this.vesselTargetGraphic = vesselTargetGraphic;
    }

    public void setAisLayer(AisLayer aisLayer) {
        this.aisLayer = aisLayer;
    }

}

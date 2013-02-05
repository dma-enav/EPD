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
package dk.dma.epd.shore.gui.views.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.ais.VesselTargetSettings;
import dk.dma.epd.shore.layers.ais.AisLayer;


public class AisIntendedRouteToggle extends JMenuItem implements
        IMapMenuAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private VesselTargetSettings vesselTargetSettings;
    private VesselTarget vesselTarget;
    @SuppressWarnings("unused")
    private AisLayer aisLayer;

    public AisIntendedRouteToggle() {
        super();
    }

    @Override
    public void doAction() {
        vesselTargetSettings.setShowRoute(!vesselTarget.getSettings().isShowRoute());

        //Target updated function - to do
//        aisLayer.targetUpdated(vesselTarget);
    }

    public void setVesselTarget(VesselTarget vesselTarget) {
        this.vesselTarget = vesselTarget;
    }

    public void setVesselTargetSettings(VesselTargetSettings vesselTargetSettings) {
        this.vesselTargetSettings = vesselTargetSettings;
    }

    public void setAisLayer(AisLayer aisLayer) {
        this.aisLayer = aisLayer;
    }
}

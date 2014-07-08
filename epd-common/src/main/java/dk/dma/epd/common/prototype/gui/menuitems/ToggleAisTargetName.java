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

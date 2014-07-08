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

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.layers.msi.MsiLayerCommon;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;

/**
 * Zooms to an MSI thingy
 */
public class MsiZoomTo extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;
    private MsiLayerCommon msiLayer;
    private MsiMessageExtended msiMessageExtended;

    /**
     * Constructor
     * @param text
     */
    public MsiZoomTo(String text) {
        super();
        setText(text);
    }

    /**
     * Called when the menu item is enacted
     */
    @Override
    public void doAction() {
        msiLayer.zoomTo(msiMessageExtended.msiMessage);
    }

    /**
     * Sets the MSI layer
     * @param msiLayer the MSI layer
     */
    public void setMsiLayer(MsiLayerCommon msiLayer) {
        this.msiLayer = msiLayer;
    }

    /**
     * Sets the MSI message
     * @param msiMessageExtended the MSI message
     */
    public void setMsiMessageExtended(MsiMessageExtended msiMessageExtended) {
        this.msiMessageExtended = msiMessageExtended;
    }

}

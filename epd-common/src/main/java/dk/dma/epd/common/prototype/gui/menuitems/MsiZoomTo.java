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

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
package dk.dma.epd.ship.gui.setuptabs;

import dk.dma.epd.common.prototype.gui.settings.MapSettingsPanelCommon;
import javax.swing.JCheckBox;

public class ShipMapSettingsPanel extends MapSettingsPanelCommon {

    private static final long serialVersionUID = 1L;

    public ShipMapSettingsPanel() {
        this.getGenerelPanel().setSize(438, 220);
        this.getApperancePanel().setBounds(this.getApperancePanel().getBounds().x, this.getApperancePanel().getBounds().y+70, 
                this.getApperancePanel().getWidth(), (int) this.getApperancePanel().getBounds().getHeight());
        
        JCheckBox chckbxUseEnc = new JCheckBox("Use ENC");
        chckbxUseEnc.setBounds(6, 139, 85, 23);
        getGenerelPanel().add(chckbxUseEnc);
        
        JCheckBox chckbxUseWms = new JCheckBox("Use WMS");
        chckbxUseWms.setBounds(103, 139, 88, 23);
        getGenerelPanel().add(chckbxUseWms);
        
        JCheckBox chckbxDragWmsdisable = new JCheckBox("Drag WMS (disable for performance)");
        chckbxDragWmsdisable.setBounds(6, 174, 257, 23);
        getGenerelPanel().add(chckbxDragWmsdisable);
    }
}

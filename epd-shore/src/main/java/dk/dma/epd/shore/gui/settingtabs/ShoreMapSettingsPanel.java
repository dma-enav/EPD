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
package dk.dma.epd.shore.gui.settingtabs;

import dk.dma.epd.common.prototype.gui.settings.CommonMapSettingsPanel;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.settings.EPDMapSettings;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 * 
 * @author adamduehansen
 *
 */
public class ShoreMapSettingsPanel extends CommonMapSettingsPanel {

    private static final long serialVersionUID = 1L;
    private JCheckBox chckbxMapsStartWith;
    private JCheckBox chckbxWmsIsUsed;
    private EPDMapSettings settings;
    
    public ShoreMapSettingsPanel() {
        
        
        /************** WMS settings ***************/
        
        getLblWMSURL().setLocation(16, 70);
        getTextFieldWMSURL().setLocation(16, 95);
        getWMSPanel().setBounds(6, 163, 438, 170);
        
        chckbxMapsStartWith = new JCheckBox("Maps start with WMS enabled");
        chckbxMapsStartWith.setBounds(16, 20, 215, 20);
        getWMSPanel().add(chckbxMapsStartWith);
        
        chckbxWmsIsUsed = new JCheckBox("WMS is used when dragging (disable for performance)");
        chckbxWmsIsUsed.setBounds(16, 45, 369, 20);
        getWMSPanel().add(chckbxWmsIsUsed);
        
        JLabel lblenterTheUrl = new JLabel("<html><body>Enter the URL to the WMS service you wish to use,<br>enter everything except BBOX and height/width options.");
        lblenterTheUrl.setBounds(16, 120, 353, 32);
        getWMSPanel().add(lblenterTheUrl);
    }
    
    public void doLoadSettings() {
        
        // Load settings for the common components
        super.doLoadSettings();
        
        settings = EPDShore.getInstance().getSettings().getMapSettings();
        
        // Load the extended WMS settings.
        this.chckbxMapsStartWith.setSelected(this.settings.isUseWms());
        this.chckbxWmsIsUsed.setSelected(this.settings.isUseWmsDragging());
    }
    
    public void doSaveSettings() {
        
        // Save settings for common components.
        super.doSaveSettings();
        
        // Save settings for extended WMS settings.
        this.settings.setUseWms(this.chckbxMapsStartWith.isSelected());
        this.settings.setUseWmsDragging(this.chckbxWmsIsUsed.isSelected());
    }
    
    public boolean checkSettingsChanged() {
        
        // First check if changes were made in common components.
        boolean changesWereMade = super.checkSettingsChanged();
        
        // Only check if changes were made in ship components if super.checkSettingsChanged
        // return false:
        // Consider a change were made to the common components but not the ship components. It
        // would result in "changesWereMade" to be false, and the changes in common components
        // would not be saved!
        if (!changesWereMade) {
            changesWereMade = 
                    // Check for changes in extended WMS settings.
                    changed(this.settings.isUseWms(), this.chckbxMapsStartWith.isSelected()) ||
                    changed(this.settings.isUseWmsDragging(), this.chckbxWmsIsUsed.isSelected());
        }
        
        return changesWereMade;
    }
}

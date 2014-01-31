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
package dk.dma.epd.shore.gui.views;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import dk.dma.epd.common.prototype.gui.SetupDialogCommon;
import dk.dma.epd.common.prototype.settings.Settings;
import dk.dma.epd.shore.gui.settingtabs.ShoreAisSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.ShoreCloudSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.ShoreConnectionSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.ShoreMapSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.ShoreMapFramesSettingsPanel;

public class SetupDialogShore extends SetupDialogCommon {
    
    private static final long serialVersionUID = 1L;
    private ShoreCloudSettingsPanel shoreSettings;
    private ShoreMapSettingsPanel mapSettings;
    private ShoreAisSettingsPanel aisSettings;
    private ShoreMapFramesSettingsPanel windowsSettings;
    private ShoreConnectionSettingsPanel connectionPanel;
    
    public SetupDialogShore(JFrame mainFrame) {
        
        super(mainFrame, "Setup", JTabbedPane.LEFT);
        
        // Resize the dialog to make more room for tabs on the right side.
        this.setSize(800, super.getHeight()-200);
        
        this.connectionPanel = new ShoreConnectionSettingsPanel();
        this.shoreSettings   = new ShoreCloudSettingsPanel();
        this.mapSettings     = new ShoreMapSettingsPanel();
        this.windowsSettings = new ShoreMapFramesSettingsPanel();
        this.aisSettings     = new ShoreAisSettingsPanel();
        
        // Register the panels for shore setup.
        super.registerSettingsPanels(
                this.connectionPanel,
                this.shoreSettings,
                this.mapSettings,
                this.windowsSettings,
                this.aisSettings
                );
    }
    
    /**
     * {@inheritDoc}
     */
    public void loadSettings(Settings settings) {
        super.loadSettings(settings);
    }
    
    
}

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
import dk.dma.epd.common.prototype.gui.settings.CommonMapSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.ShoreAisSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.ShoreCloudSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.ShoreEnavSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.ShoreMapFramesSettingsPanel;

/**
 * Shore-specific implementation of the {@linkplain SetupDialogCommon}
 */
public class SetupDialogShore extends SetupDialogCommon {

    /**
     * Private fields.
     */
    private static final long serialVersionUID = 1L;
    private ShoreCloudSettingsPanel shoreSettings;
    private ShoreAisSettingsPanel aisSettings;
    private ShoreMapFramesSettingsPanel windowsSettings;

    /**
     * Constructor
     * @param mainFrame The mainframe.
     */
    public SetupDialogShore(JFrame mainFrame) {

        super(mainFrame, "Setup", JTabbedPane.LEFT);

        // Resize the dialog to make more room for tabs on the right side.
        this.setSize(800, super.getHeight() - 50);

        this.shoreSettings = new ShoreCloudSettingsPanel();
        this.windowsSettings = new ShoreMapFramesSettingsPanel();
        this.aisSettings = new ShoreAisSettingsPanel();

        // Register the panels for shore setup.
        super.registerSettingsPanels(new ShoreEnavSettingsPanel(),
                new CommonMapSettingsPanel(),
                this.shoreSettings, 
                this.windowsSettings,
                this.aisSettings);

        super.resizePanelsToFitContainer(this);
        super.addTabs();
        super.setActivePanel(1);
    }

    /**
     * Sets the panel tabs to show map frames settings and sets the tabbed pane to a specific map frame.
     * 
     * @param activeMapWindow
     */
    public void goToSpecifMapSettings(JMapFrame activeMapWindow) {

        // Go to the map frames settings.
        super.setActivePanel(4);
        this.windowsSettings.showSettingsFor(activeMapWindow);

    }
}

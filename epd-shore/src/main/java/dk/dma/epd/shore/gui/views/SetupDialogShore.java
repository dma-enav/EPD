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

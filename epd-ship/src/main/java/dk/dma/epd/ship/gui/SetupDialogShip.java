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
package dk.dma.epd.ship.gui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import dk.dma.epd.common.prototype.gui.SetupDialogCommon;
import dk.dma.epd.common.prototype.gui.settings.CommonCloudSettingsPanel;
import dk.dma.epd.common.prototype.settings.handlers.AisHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.AisLayerCommonSettings;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.setuptabs.ShipAisSettingsPanel;
import dk.dma.epd.ship.gui.setuptabs.ShipNavigationSettingsPanel;
import dk.dma.epd.ship.gui.setuptabs.ShipSensorSettingsPanel;
import dk.dma.epd.ship.gui.setuptabs.ShipServicesSettingsPanel;
import dk.dma.epd.ship.settings.handlers.IntendedRouteHandlerSettings;

/**
 * This is the setup dialog made specific for ship, by
 * extending the common setup dialog.
 * @author adamduehansen
 *
 */
public class SetupDialogShip extends SetupDialogCommon {

    /**
     * private fields.
     */
    private static final long serialVersionUID = 1L;
    private CommonCloudSettingsPanel cloudSettings;
    private ShipAisSettingsPanel aisSettings;
    private ShipNavigationSettingsPanel navigationSettings;
    private ShipSensorSettingsPanel sensorSettings;
    private ShipServicesSettingsPanel serviceSettings;

    /**
     * Constructs a new SetDialogShip object.
     * @param mainFrame The frame which the setup dialog should be on top of.
     */
    public SetupDialogShip(JFrame mainFrame) {
        // Constructs the common dialog.
        super(mainFrame, "Ship Setup", JTabbedPane.TOP);
        super.setSize(super.getWidth()+69, super.getHeight());

        // Constructs some panels for the setup dialog.
        this.cloudSettings      = new CommonCloudSettingsPanel(EPDShip.getInstance().getSettings().getMaritimeCloudHttpSettings());
        AisHandlerCommonSettings<?> aisHandlerSettings = EPDShip.getInstance().getSettings().getAisHandlerSettings();
        AisLayerCommonSettings<?> aisLayerSettings = EPDShip.getInstance().getSettings().getPrimaryAisLayerSettings();
        this.aisSettings        = new ShipAisSettingsPanel(aisHandlerSettings, aisLayerSettings);
        this.navigationSettings = new ShipNavigationSettingsPanel();
        this.sensorSettings     = new ShipSensorSettingsPanel();
        IntendedRouteHandlerSettings intendedRouteHandlerSettings = EPDShip.getInstance().getSettings().getIntendedRouteHandlerSettings();
        this.serviceSettings    = new ShipServicesSettingsPanel(intendedRouteHandlerSettings);
        
        // Register the panels in the setup dialog.
        super.registerSettingsPanels( 
                navigationSettings,
                cloudSettings,
                serviceSettings,
                aisSettings, 
                sensorSettings 
                );
        
        super.resizePanelsToFitContainer(this);
        super.addTabs();
    }
}

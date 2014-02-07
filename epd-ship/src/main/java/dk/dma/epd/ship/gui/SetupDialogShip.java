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
package dk.dma.epd.ship.gui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import dk.dma.epd.common.prototype.gui.SetupDialogCommon;
import dk.dma.epd.common.prototype.gui.settings.CommonCloudSettingsPanel;
import dk.dma.epd.common.prototype.settings.Settings;
import dk.dma.epd.ship.gui.setuptabs.ShipAisSettingsPanel;
import dk.dma.epd.ship.gui.setuptabs.ShipMapSettingsPanel;
import dk.dma.epd.ship.gui.setuptabs.ShipNavigationSettingsPanel;
import dk.dma.epd.ship.gui.setuptabs.ShipSensorSettingsPanel;
import dk.dma.epd.ship.gui.setuptabs.ShipServicesSettingsPanel;

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
    private ShipMapSettingsPanel mapSettings;
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
        super.setSize(super.getWidth()+98, super.getHeight());

        // Constructs some panels for the setup dialog.
        this.cloudSettings      = new CommonCloudSettingsPanel();
        this.mapSettings        = new ShipMapSettingsPanel();
        this.aisSettings        = new ShipAisSettingsPanel();
        this.navigationSettings = new ShipNavigationSettingsPanel();
        this.sensorSettings     = new ShipSensorSettingsPanel();
        this.serviceSettings    = new ShipServicesSettingsPanel();
        
        // Register the panels in the setup dialog.
        super.registerSettingsPanels( 
                navigationSettings,
                cloudSettings,
                serviceSettings,
                mapSettings,
                aisSettings, 
                sensorSettings 
                );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(Settings settings) {
        super.loadSettings(settings);
    }
}

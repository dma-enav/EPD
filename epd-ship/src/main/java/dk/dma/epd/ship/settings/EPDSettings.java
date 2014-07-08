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
package dk.dma.epd.ship.settings;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

import dk.dma.epd.common.prototype.settings.Settings;


/**
 * Settings class
 */
public class EPDSettings extends Settings implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String settingsFile = "settings.properties";

    private final EPDGuiSettings guiSettings = new EPDGuiSettings();
    private final EPDMapSettings mapSettings = new EPDMapSettings();
    private final EPDSensorSettings sensorSettings = new EPDSensorSettings();
    private final EPDNavSettings navSettings = new EPDNavSettings();
    private final EPDAisSettings aisSettings = new EPDAisSettings();
    private final EPDEnavSettings enavSettings = new EPDEnavSettings();
    private final EPDS57LayerSettings s57Settings = new EPDS57LayerSettings();
    private final EPDCloudSettings cloudSettings = new EPDCloudSettings();

    public EPDSettings() {
        super();
    }

    /**
     * Load the settings files as well as the workspace files
     */
    @Override
    public void loadFromFile() {
        // Open properties file
        Properties props = new Properties();
        loadProperties(props, settingsFile);

        aisSettings.readProperties(props);
        enavSettings.readProperties(props);
        guiSettings.readProperties(props);
        mapSettings.readProperties(props);
        navSettings.readProperties(props);
        sensorSettings.readProperties(props);
        cloudSettings.readProperties(props);
        
        s57Settings.readSettings(resolve("s57Props.properties").toString());
    }

    /**
     * Save the settings to the files
     */
    @Override
    public void saveToFile() {
        Properties props = new Properties();
        aisSettings.setProperties(props);
        enavSettings.setProperties(props);
        guiSettings.setProperties(props);
        mapSettings.setProperties(props);
        navSettings.setProperties(props);
        sensorSettings.setProperties(props);
        cloudSettings.setProperties(props);
        
        saveProperties(props, settingsFile, "# EPD-ship settings saved: " + new Date());
        
        s57Settings.saveSettings(resolve("s57Props.properties").toString());
    }

    @Override
    public EPDGuiSettings getGuiSettings() {
        return guiSettings;
    }

    @Override
    public EPDMapSettings getMapSettings() {
        return mapSettings;
    }

    @Override
    public EPDSensorSettings getSensorSettings() {
        return sensorSettings;
    }

    @Override
    public EPDNavSettings getNavSettings() {
        return navSettings;
    }

    @Override
    public EPDAisSettings getAisSettings() {
        return aisSettings;
    }

    @Override
    public EPDEnavSettings getEnavSettings() {
        return enavSettings;
    }

    @Override
    public EPDS57LayerSettings getS57Settings() {
        return s57Settings;
    }

    @Override
    public EPDCloudSettings getCloudSettings() {
        return cloudSettings;
    }
    
    public String getSettingsFile() {
        return settingsFile;
    }
}

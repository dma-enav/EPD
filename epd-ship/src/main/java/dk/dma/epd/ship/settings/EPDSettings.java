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
package dk.dma.epd.ship.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Settings class
 */
public class EPDSettings implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(EPDSettings.class);

    private String settingsFile = "settings.properties";

    private final EPDGuiSettings guiSettings = new EPDGuiSettings();
    private final EPDMapSettings mapSettings = new EPDMapSettings();
    private final EPDSensorSettings sensorSettings = new EPDSensorSettings();
    private final EPDNavSettings navSettings = new EPDNavSettings();
    private final EPDAisSettings aisSettings = new EPDAisSettings();
    private final EPDEnavSettings enavSettings = new EPDEnavSettings();

    public EPDSettings() {

    }

    public EPDSettings(String settingsFile) {
        this.settingsFile = settingsFile;
    }

    public void loadFromFile() {
        // Open properties file
        Properties props = new Properties();
        //
        try {
            props.load(new FileInputStream(settingsFile));
        } catch (FileNotFoundException e) {
            LOG.error("No settings file found");
            return;
        } catch (IOException e) {
            LOG.error("Settings file could not be loaded");
            return;
        }
        LOG.info("Settings file loaded, path=" + settingsFile);

        aisSettings.readProperties(props);
        enavSettings.readProperties(props);
        guiSettings.readProperties(props);
        mapSettings.readProperties(props);
        navSettings.readProperties(props);
        sensorSettings.readProperties(props);
    }

    public void saveToFile() {
        Properties props = new Properties();
        aisSettings.setProperties(props);
        enavSettings.setProperties(props);
        guiSettings.setProperties(props);
        mapSettings.setProperties(props);
        navSettings.setProperties(props);
        sensorSettings.setProperties(props);
        try (
            FileWriter outFile = new FileWriter(settingsFile);
            PrintWriter out = new PrintWriter(outFile);) {
            out.println("# ee-INS settings saved: " + new Date());
            TreeSet<String> keys = new TreeSet<>();
            for (Object key : props.keySet()) {
                keys.add((String) key);
            }
            for (String key : keys) {
                out.println(key + "=" + props.getProperty(key));
            }
            LOG.error("Settings file updated, path=" + settingsFile);
        } catch (IOException e) {
            LOG.error("Failed to save settings file");
        }
    }

    public EPDGuiSettings getGuiSettings() {
        return guiSettings;
    }

    public EPDMapSettings getMapSettings() {
        return mapSettings;
    }

    public EPDSensorSettings getSensorSettings() {
        return sensorSettings;
    }

    public EPDNavSettings getNavSettings() {
        return navSettings;
    }

    public EPDAisSettings getAisSettings() {
        return aisSettings;
    }

    public EPDEnavSettings getEnavSettings() {
        return enavSettings;
    }

    public String getSettingsFile() {
        return settingsFile;
    }

}

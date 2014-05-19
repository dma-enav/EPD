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

import java.io.Serializable;

import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.Settings;
import dk.dma.epd.common.prototype.settings.gui.MapCommonSettings;
import dk.dma.epd.common.prototype.settings.sensor.ExternalSensorsCommonSettings;
import dk.dma.epd.ship.settings.gui.GUISettings;
import dk.dma.epd.ship.settings.gui.MapSettings;
import dk.dma.epd.ship.settings.handlers.RouteManagerSettings;


/**
 * Settings class
 */
public class EPDSettings extends Settings implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String settingsFile = "settings.properties";

//    private final EPDNavSettings navSettings = new EPDNavSettings();
//    private final EPDEnavSettings enavSettings = new EPDEnavSettings();
//    private final EPDCloudSettings cloudSettings = new EPDCloudSettings();
    
    private GUISettings<GUISettings.IObserver> guiSettings;
    
    private MapSettings<MapSettings.IObserver> mapSettings;
    
    private RouteManagerSettings<RouteManagerSettings.IObserver> routeManagerSettings;
    
    private ExternalSensorsCommonSettings<ExternalSensorsCommonSettings.IObserver> externalSensorsSettings;
    
    public EPDSettings() {
        super();
    }
    
    /**
     * Load the settings files as well as the workspace files
     */
    @Override
    public void loadFromFile() {
        // Do work in super to load non-specialized settings.
        super.loadFromFile();
        
        // Load general gui settings.
        GUISettings<GUISettings.IObserver> gui = ObservedSettings.loadFromFile(GUISettings.class, resolve(guiSettingsFile).toFile());
        // Create new instance if no saved instance found.
        guiSettings = gui != null ? gui : new GUISettings<>();
        
        // Load map settings.
        MapSettings<MapSettings.IObserver> map = ObservedSettings.loadFromFile(MapSettings.class, resolve(mapSettingsFile).toFile());
        // Create new instance if no saved instance found.
        mapSettings = map != null ? map : new MapSettings<>();
        
        // Load route manager settings.
        RouteManagerSettings<RouteManagerSettings.IObserver> rms = ObservedSettings.loadFromFile(RouteManagerSettings.class, resolve(routeManagerSettingsFile).toFile());
        // Create new instance if no saved instance found.
        routeManagerSettings = rms != null ? rms : new RouteManagerSettings<>();
        
        /*
         *  Load external sensors settings.
         *  Even though ship uses common version, we need to load it here instead of in super class as shore uses specific version.
         */
        ExternalSensorsCommonSettings<ExternalSensorsCommonSettings.IObserver> ext = ObservedSettings.loadFromFile(ExternalSensorsCommonSettings.class, resolve(externalSensorsSettingsFile).toFile());
        // Use loaded instance or create new if the file was not found.
        this.externalSensorsSettings = ext != null ? ext : new ExternalSensorsCommonSettings<ExternalSensorsCommonSettings.IObserver>();
        
//        // Open properties file
//        Properties props = new Properties();
//        loadProperties(props, settingsFile);
//
//
//        enavSettings.readProperties(props);
//        guiSettings.readProperties(props);
//        mapSettings.readProperties(props);
//        navSettings.readProperties(props);
//        sensorSettings.readProperties(props);
//        cloudSettings.readProperties(props);
    }

    /**
     * Save the settings to the files
     */
    @Override
    public void saveToFile() {
//        Properties props = new Properties();
//        enavSettings.setProperties(props);
//        guiSettings.setProperties(props);
//        mapSettings.setProperties(props);
//        navSettings.setProperties(props);
//        sensorSettings.setProperties(props);
//        cloudSettings.setProperties(props);
//        
//        saveProperties(props, settingsFile, "# EPD-ship settings saved: " + new Date());
//        
//        s57Settings.saveSettings(resolve("s57Props.properties").toString());
    }

    @Override
    public GUISettings<GUISettings.IObserver> getGuiSettings() {
        return this.guiSettings;
    }

    @Override
    public MapCommonSettings<MapSettings.IObserver> getMapSettings() {
        return this.mapSettings;
    }
    
    @Override
    public RouteManagerSettings<RouteManagerSettings.IObserver> getRouteManagerSettings() {
        return this.routeManagerSettings;
    }
    
    @Override
    public ExternalSensorsCommonSettings<ExternalSensorsCommonSettings.IObserver> getExternalSensorsSettings() {
        return this.externalSensorsSettings;
    }
    
//    @Override
//    public EPDSensorSettings getSensorSettings() {
//        return sensorSettings;
//    }
//
//    @Override
//    public EPDNavSettings getNavSettings() {
//        return navSettings;
//    }
//
//    @Override
//    public EPDAisSettings getAisSettings() {
//        return aisSettings;
//    }
//
//    @Override
//    public EPDEnavSettings getEnavSettings() {
//        return enavSettings;
//    }
//
//    @Override
//    public EPDCloudSettings getCloudSettings() {
//        return cloudSettings;
//    }
    
    public String getSettingsFile() {
        return settingsFile;
    }
}

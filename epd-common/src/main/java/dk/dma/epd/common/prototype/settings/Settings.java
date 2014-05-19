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
package dk.dma.epd.common.prototype.settings;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.settings.gui.GUICommonSettings;
import dk.dma.epd.common.prototype.settings.gui.MapCommonSettings;
import dk.dma.epd.common.prototype.settings.handlers.RouteManagerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.AisLayerCommonSettings;

/**
 * Abstract parent class the encapsulates the 
 * list of specialized settings 
 */
public abstract class Settings {

    private static final Logger LOG = LoggerFactory.getLogger(Settings.class);
    
    /**
     * Filename for the file with AIS layer settings.
     */
    protected final String aisLayerSettingsFile = "AIS-Layer_settings.yaml";

    /**
     * Filename for the file with general gui settings.
     */
    protected final String guiSettingsFile = "gui_settings.yaml";
    
    /**
     * Filename for the file with map settings.
     */
    protected final String mapSettingsFile = "map_settings.yaml";
    
    /**
     * Filename for the file with route manager settings.
     */
    protected final String routeManagerSettingsFile = "route-manager_settings.yaml";
    
    /**
     * Filename for the file with S57 layer settings.
     */
    protected final String s57LayerSettingsFile = "s57Props.properties";
    
    /**
     * The primary/global AIS layer settings.
     * If more AIS layers are to coexists, each with individual settings, these local settings instances may register as observers of this instance in order to "obey" to changes to global settings.
     */
    protected AisLayerCommonSettings<?> primaryAisLayerSettings;
    
    protected S57LayerSettings s57LayerSettings;
    
    public abstract GUICommonSettings<?> getGuiSettings();
    
    public abstract MapCommonSettings<?> getMapSettings();
    
    public abstract RouteManagerCommonSettings<?> getRouteManagerSettings();
    
    /**
     * Gets the primary (global) AIS layer settings.
     * If more AIS layers are to coexists, each with individual settings, these local settings instances may register as observers of this instance in order to "obey" to changes to global settings.
     * @return The primary (global) AIS layer settings.
     */
    public AisLayerCommonSettings<?> getPrimaryAisLayerSettings() {
        return primaryAisLayerSettings;
    }
    
    public S57LayerSettings getS57LayerSettings() {
        return this.s57LayerSettings;
    }
    
//    public abstract GuiSettings getGuiSettings();

//    public abstract MapSettings getMapSettings();

//    public abstract SensorSettings getSensorSettings();

//    public abstract NavSettings getNavSettings();

//    public abstract EnavSettings getEnavSettings();

    public abstract S57LayerSettings getS57Settings();
    
//    public abstract CloudSettings getCloudSettings();

    /**
     * Resolves the given file in the current home folder
     * @param file the file to resolve
     * @return the resolved file
     */
    public Path resolve(String file) {
        return EPD.getInstance().getHomePath().resolve(file);
    }
    
//    /**
//     * Loads the given properties file
//     * @param props the properties to load the file into
//     * @param file the properties file to load
//     * @return success or failure
//     */
//    protected boolean loadProperties(Properties props, String file) {
//        if (file.startsWith("/")) {
//            file = file.substring(1);
//        }
//        try {
//            props.load(new FileInputStream(resolve(file).toFile()));
//        } catch (FileNotFoundException e) {
//            LOG.error("No settings file found: " + resolve(file));
//            return false;
//        } catch (IOException e) {
//            LOG.error("Settings file could not be loaded: " + resolve(file));
//            return false;
//        }
//        
//        LOG.info("Settings file loaded, path=" + resolve(file));
//        return true;
//    }
    
//    /**
//     * Saves the properties to the given file
//     * @param props the properties to save
//     * @param file the file to save the properties to
//     * @return success or failure
//     */
//    protected boolean saveProperties(Properties props, String file, String header) {
//        if (file.startsWith("/")) {
//            file = file.substring(1);
//        }
//        try (
//                FileWriter outFile = new FileWriter(resolve(file).toFile());
//                PrintWriter out = new PrintWriter(outFile);) {
//                if (header != null) {
//                    out.println(header);
//                }
//                TreeSet<String> keys = new TreeSet<>();
//                for (Object key : props.keySet()) {
//                    keys.add((String) key);
//                }
//                for (String key : keys) {
//                    out.println(key + "=" + props.getProperty(key));
//                }
//        } catch (IOException e) {
//            LOG.error("Failed to save settings file " + resolve(file) + ": " + e.getMessage());
//            return false;
//        }
//        
//        LOG.info("Settings file updated, path=" + resolve(file));
//        return true;
//    }
    
    /**
     * Load the settings files as well as the workspace files
     */
    public void loadFromFile() {
        /*
         * Load primary AIS layer settings.
         * If ship/shore specific AIS layer settings are added later, move this to subclass.
         */
        AisLayerCommonSettings<AisLayerCommonSettings.IObserver> ais = ObservedSettings.loadFromFile(AisLayerCommonSettings.class, resolve(aisLayerSettingsFile).toFile());
        // Use loaded instance or create new if the file was not found.
        this.primaryAisLayerSettings = ais != null ? ais : new AisLayerCommonSettings<AisLayerCommonSettings.IObserver>();
        /*
         * Load S57 layer settings.
         * If ship/shore specific S57 layer settings are added later, move this to subclass.
         */
        this.s57LayerSettings = new S57LayerSettings();
        this.s57LayerSettings.readSettings(resolve(s57LayerSettingsFile).toString());
    }

    /**
     * Save the settings to the files
     */
    public abstract void saveToFile();        
}
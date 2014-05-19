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
import dk.dma.epd.common.prototype.settings.handlers.MSIHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.handlers.MetocHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.handlers.RouteManagerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.AisLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.ENCLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.MSILayerCommonSettings;
import dk.dma.epd.common.prototype.settings.network.NetworkSettings;
import dk.dma.epd.common.prototype.settings.sensor.ExternalSensorsCommonSettings;

/**
 * Abstract parent class the encapsulates the 
 * list of specialized settings 
 */
public abstract class Settings {

    private static final Logger LOG = LoggerFactory.getLogger(Settings.class);
    
    /**
     * Filename for the file with AIS layer settings.
     */
    protected final String aisLayerSettingsFile = "ais-layer_settings.yaml";

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
     * Filename for the file with external sensors settings.
     */
    protected final String externalSensorsSettingsFile = "external-sensors_settings.yaml";
    
    /**
     * Filename for the file with MSI handler settings.
     */
    protected final String msiHandlerSettingsFile = "msi-handler_settings.yaml";
    
    /**
     * Filename for the file with MSI layer settings.
     */
    protected final String msiLayerSettingsFile = "msi-layer_settings.yaml";
    
    /**
     * Filename for the file with e-Nav services HTTP settings.
     */
    protected final String enavServicesHttpSettingsFile = "enav-services-http_settings.yaml";
    
    /**
     * Filename for the file with METOC handler settings.
     */
    protected final String metocHandlerSettingsFile = "metoc-handler_settings.yaml";
    
    /**
     * Filename for the file with ENC layer settings.
     */
    protected final String encLayerSettingsFile = "enc-layer_settings.yaml";
    
    /**
     * The primary/global AIS layer settings.
     * If more AIS layers are to coexist, each with individual settings, these local settings instances may register as observers of this instance in order to "obey" to changes to global settings.
     */
    protected AisLayerCommonSettings<AisLayerCommonSettings.IObserver> primaryAisLayerSettings;
    
    /**
     * The primary/global MSI layer settings.
     * If more MSI layers are to coexist, each with individual settings, these local settings instances may register as observers of this instance in order to "obey" to changes to global settings.
     */
    protected MSILayerCommonSettings<MSILayerCommonSettings.IObserver> msiLayerSettings;
    
    protected S57LayerSettings s57LayerSettings;
    
    protected MSIHandlerCommonSettings<MSIHandlerCommonSettings.IObserver> msiHandlerSettings;
    
    /**
     * Connection parameters used when connecting to e-Nav services.
     */
    protected NetworkSettings<NetworkSettings.IObserver> enavServicesHttpSettings;
    
    protected MetocHandlerCommonSettings<MetocHandlerCommonSettings.IObserver> metocHandlerSettings;
    
    public abstract GUICommonSettings<? extends GUICommonSettings.IObserver> getGuiSettings();
    
    public abstract MapCommonSettings<? extends MapCommonSettings.IObserver> getMapSettings();
    
    public abstract RouteManagerCommonSettings<? extends RouteManagerCommonSettings.IObserver> getRouteManagerSettings();
    
    public abstract ExternalSensorsCommonSettings<? extends ExternalSensorsCommonSettings.IObserver> getExternalSensorsSettings();
    
    public abstract ENCLayerCommonSettings<? extends ENCLayerCommonSettings.IObserver> getENCLayerSettings();
    
    /**
     * Gets the primary (global) AIS layer settings.
     * If more AIS layers are to coexist, each with individual settings, these local settings instances may register as observers of this instance in order to "obey" to changes to global settings.
     * @return The primary (global) AIS layer settings.
     */
    public AisLayerCommonSettings<AisLayerCommonSettings.IObserver> getPrimaryAisLayerSettings() {
        return primaryAisLayerSettings;
    }
    
    public S57LayerSettings getS57LayerSettings() {
        return this.s57LayerSettings;
    }
    
    public MSIHandlerCommonSettings<MSIHandlerCommonSettings.IObserver> getMsiHandlerSettings() {
        return this.msiHandlerSettings;
    }
    
    /**
     * Gets the primary (global) MSI layer settings.
     * If more MSI layers are to coexist, each with individual settings, these local settings instances may register as observers of this instance in order to "obey" to changes to global settings.
     * @return The primary (global) MSI layer settings.
     */
    public MSILayerCommonSettings<MSILayerCommonSettings.IObserver> getPrimaryMsiLayerSettings() {
        return this.msiLayerSettings;
    }
    
    /**
     * Get settings specifying connection parameters for the e-Nav services connection.
     * @return Settings specifying connection parameters for the e-Nav services connection
     */
    public NetworkSettings<NetworkSettings.IObserver> getEnavServicesHttpSettings() {
        return this.enavServicesHttpSettings;
    }
    
    public MetocHandlerCommonSettings<MetocHandlerCommonSettings.IObserver> getMetocHandlerSettings() {
        return this.metocHandlerSettings;
    }

//    public abstract NavSettings getNavSettings();

//    public abstract EnavSettings getEnavSettings();
    
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
         * Load primary/global AIS layer settings.
         * If ship/shore specific AIS layer settings are added later, move this to subclass.
         */
        AisLayerCommonSettings<AisLayerCommonSettings.IObserver> ais = ObservedSettings.loadFromFile(AisLayerCommonSettings.class, resolve(aisLayerSettingsFile).toFile());
        // Use loaded instance or create new if the file was not found.
        this.primaryAisLayerSettings = ais != null ? ais : new AisLayerCommonSettings<>();
        
        /*
         * Load S57 layer settings.
         * If ship/shore specific S57 layer settings are added later, move this to subclass.
         */
        this.s57LayerSettings = new S57LayerSettings();
        this.s57LayerSettings.readSettings(resolve(s57LayerSettingsFile).toString());
        
        /*
         * Load MSI handler settings.
         * If ship/shore specific MSI handler settings are added later, move this to subclass.
         */
        MSIHandlerCommonSettings<MSIHandlerCommonSettings.IObserver> msiHandlerSett = ObservedSettings.loadFromFile(MSIHandlerCommonSettings.class, resolve(msiHandlerSettingsFile).toFile());
        this.msiHandlerSettings = msiHandlerSett != null ? msiHandlerSett : new MSIHandlerCommonSettings<>();
        
        /*
         * Load primary/global MSI layer settings.
         * If ship/shore specific MSI layer settings are added later, move this to subclass.
         */
        MSILayerCommonSettings<MSILayerCommonSettings.IObserver> msiLayerSett = ObservedSettings.loadFromFile(MSILayerCommonSettings.class, resolve(msiLayerSettingsFile).toFile());
        this.msiLayerSettings = msiLayerSett != null ? msiLayerSett : new MSILayerCommonSettings<>();
        
        /*
         * Load e-Nav services connection settings.
         * If ship/shore specific e-Nav services connection settings are added later, move this to subclass.
         */
        NetworkSettings<NetworkSettings.IObserver> enavServices = ObservedSettings.loadFromFile(NetworkSettings.class, resolve(enavServicesHttpSettingsFile).toFile());
        if(enavServices == null) {
            // Create new instance if no saved instance present.
            enavServices = new NetworkSettings<>();
            /*
             *  Default network settings connect to localhost.
             *  Update to use external server by default.
             */
            enavServices.setHost("service.e-navigation.net");
            enavServices.setPort(80);
        }
        // Loaded or new instance no ready for use.
        this.enavServicesHttpSettings = enavServices;
        
        MetocHandlerCommonSettings<MetocHandlerCommonSettings.IObserver> metoc = ObservedSettings.loadFromFile(MetocHandlerCommonSettings.class, resolve(metocHandlerSettingsFile).toFile());
        this.metocHandlerSettings = metoc != null ? metoc : new MetocHandlerCommonSettings<>();
    }

    /**
     * Save the settings to the files
     */
    public abstract void saveToFile();        
}
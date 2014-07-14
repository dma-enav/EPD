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

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.settings.gui.GUICommonSettings;
import dk.dma.epd.common.prototype.settings.gui.MapCommonSettings;
import dk.dma.epd.common.prototype.settings.handlers.AisHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.handlers.IntendedRouteHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.handlers.MSIHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.handlers.MetocHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.handlers.RouteManagerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.AisLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.DynamicPredictorLayerSettings;
import dk.dma.epd.common.prototype.settings.layers.ENCLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.IntendedRouteLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.MSILayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.MetocLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.PastTrackSettings;
import dk.dma.epd.common.prototype.settings.layers.RouteLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.VoyageLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.WMSLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.network.NetworkSettings;
import dk.dma.epd.common.prototype.settings.observers.AisHandlerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.AisLayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.ENCLayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.ExternalSensorsCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.GUICommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.IntendedRouteHandlerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.IntendedRouteLayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.MSIHandlerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.MSILayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.MapCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.MetocHandlerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.MetocLayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.NetworkSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.PastTrackSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.RouteLayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.RouteManagerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.VoyageLayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.WMSLayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.sensor.ExternalSensorsCommonSettings;

/**
 * Abstract parent class the encapsulates the 
 * list of specialized settings 
 */
public abstract class Settings {
    
    public static final String SETTINGS_FOLDER_NAME = "settings";
    
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
     * Filename for the file with WMS layer settings.
     */
    protected final String wmsLayerSettingsFile = "wms-layer_settings.yaml";
    
    /**
     * Filename for the file with METOC layer settings.
     */
    protected final String metocLayerSettingsFile = "metoc-layer_settings.yaml";
    
    /**
     * Filename for the file with route layer settings.
     */
    protected final String routeLayerSettingsFile = "route-layer_settings.yaml";
    
    /**
     * Filename for the file with MonaLisa HTTP settings.
     */
    protected final String monaLisaHttpSettingsFile = "mona-lisa-http_settings.yaml";
    
    /**
     * Filename for the file with intended route layer settings.
     */
    protected final String intendedRouteLayerSettingsFile = "intended-route-layer_settings.yaml";
    
    /**
     * Filename for the file with Maritime Cloud HTTP settings.
     */
    protected final String maritimeCloudHttpSettingsFile = "maritime-cloud-http_settings.yaml";
    
    /**
     * Filename for the file with AIS handler settings.
     */
    protected final String aisHandlerSettingsFile = "ais-handler_settings.yaml";
    
    /**
     * Filename for the file with intended route handler settings.
     */
    protected final String intendedRouteHandlerSettingsFile = "intended-route-handler_settings.yaml";
    
    /**
     * Filename for the file with past track settings.
     */
    protected final String pastTrackSettingsFile = "past-track_settings.yaml";
    
    /**
     * Filename for the file with voyage layer settings.
     */
    protected final String voyageLayerSettingsFile = "voyage-layer_settings.yaml";
    
    /**
     * Filename for the file with dynamic predictor layer settings.
     */
    protected final String dynamicPredictorLayerSettingsFile = "dynamic-predictor-layer_settings.yaml";
    
    /**
     * The primary/global AIS layer settings.
     * If more AIS layers are to coexist, each with individual settings, these local settings instances may register as observers of this instance in order to "obey" to changes to global settings.
     */
    protected AisLayerCommonSettings<AisLayerCommonSettingsListener> primaryAisLayerSettings;
    
    /**
     * The primary/global MSI layer settings.
     * If more MSI layers are to coexist, each with individual settings, these local settings instances may register as observers of this instance in order to "obey" to changes to global settings.
     */
    protected MSILayerCommonSettings<MSILayerCommonSettingsListener> msiLayerSettings;
    
    protected S57LayerSettings s57LayerSettings;
    
    /**
     * The primary/global WMS layer settings.
     * If more WMS layers are to coexist, each with individual settings, these local settings instances may register as observers of this instance in order to "obey" to changes to global settings.
     */
    protected WMSLayerCommonSettings<WMSLayerCommonSettingsListener> primaryWmsLayerSettings;
    
    /**
     * The primary/global METOC layer settings.
     * If more METOC layers are to coexist, each with individual settings, these local settings instances may register as observers of this instance in order to "obey" to changes to global settings.
     */
    protected MetocLayerCommonSettings<MetocLayerCommonSettingsListener> primaryMetocLayerSettings;
    
    /**
     * The primary/global route layer settings.
     * If more route layers are to coexist, each with individual settings, these local settings instances may register as observers of this instance in order to "obey" to changes to global settings.
     */
    protected RouteLayerCommonSettings<RouteLayerCommonSettingsListener> primaryRouteLayerSettings;

    /**
     * The primary/global intended route layer settings.
     * If more intended route layers are to coexist, each with individual settings, these local settings instances may register as observers of this instance in order to "obey" to changes to global settings.
     */
    protected IntendedRouteLayerCommonSettings<IntendedRouteLayerCommonSettingsListener> primaryIntendedRouteLayerSettings;
    
    protected MSIHandlerCommonSettings<MSIHandlerCommonSettingsListener> msiHandlerSettings;
    
    /**
     * Connection parameters used when connecting to e-Nav services.
     */
    protected NetworkSettings<NetworkSettingsListener> enavServicesHttpSettings;
    
    /**
     * Connection parameters used when connecting to MonaLisa services.
     */
    protected NetworkSettings<NetworkSettingsListener> monaLisaHttpSettings;
    
    /**
     * The primary/global dynamic predictor layer settings.
     * If more dynamic predictor layers are to coexist, each with individual settings, these local settings instances may register as observers of this instance in order to "obey" to changes to global settings.
     */
    protected DynamicPredictorLayerSettings primaryDynamicPredictorLayerSettings;
    
    /**
     * Connection parameters used when connecting to Maritime Cloud services.
     */
    protected NetworkSettings<NetworkSettingsListener> maritimeCloudHttpSettings;
    
    protected MetocHandlerCommonSettings<MetocHandlerCommonSettingsListener> metocHandlerSettings;
    
    protected AisHandlerCommonSettings<AisHandlerCommonSettingsListener> aisHandlerSettings;
    
    protected PastTrackSettings<PastTrackSettingsListener> pastTrackSettings;
    
    protected VoyageLayerCommonSettings<VoyageLayerCommonSettingsListener> voyageLayerSettings;
    
    public abstract GUICommonSettings<? extends GUICommonSettingsListener> getGuiSettings();
    
    public abstract MapCommonSettings<? extends MapCommonSettingsListener> getMapSettings();
    
    public abstract RouteManagerCommonSettings<? extends RouteManagerCommonSettingsListener> getRouteManagerSettings();
    
    public abstract ExternalSensorsCommonSettings<? extends ExternalSensorsCommonSettingsListener> getExternalSensorsSettings();
    
    public abstract ENCLayerCommonSettings<? extends ENCLayerCommonSettingsListener> getENCLayerSettings();
    
    public abstract IntendedRouteHandlerCommonSettings<? extends IntendedRouteHandlerCommonSettingsListener> getIntendedRouteHandlerSettings();
    
    /**
     * Gets the primary (global) AIS layer settings.
     * If more AIS layers are to coexist, each with individual settings, these local settings instances may register as observers of the returned instance in order to "obey" to changes to global settings.
     * @return The primary (global) AIS layer settings.
     */
    public AisLayerCommonSettings<AisLayerCommonSettingsListener> getPrimaryAisLayerSettings() {
        return primaryAisLayerSettings;
    }
    
    public S57LayerSettings getS57LayerSettings() {
        return this.s57LayerSettings;
    }
    
    public MSIHandlerCommonSettings<MSIHandlerCommonSettingsListener> getMsiHandlerSettings() {
        return this.msiHandlerSettings;
    }
    
    /**
     * Gets the primary (global) MSI layer settings.
     * If more MSI layers are to coexist, each with individual settings, these local settings instances may register as observers of the returned instance in order to "obey" to changes to global settings.
     * @return The primary (global) MSI layer settings.
     */
    public MSILayerCommonSettings<MSILayerCommonSettingsListener> getPrimaryMsiLayerSettings() {
        return this.msiLayerSettings;
    }
    
    /**
     * Gets the primary (global) WMS layer settings.
     * If more WMS layers are to coexist, each with individual settings, these local settings instances may register as observers of the returned instance in order to "obey" to changes to global settings.
     * @return The primary (global) WMS layer settings.
     */
    public WMSLayerCommonSettings<WMSLayerCommonSettingsListener> getPrimaryWMSLayerSettings() {
        return this.primaryWmsLayerSettings;
    }
    
    /**
     * Gets the primary (global) METOC layer settings.
     * If more METOC layers are to coexist, each with individual settings, these local settings instances may register as observers of the returned instance in order to "obey" to changes to global settings.
     * @return The primary (global) METOC layer settings.
     */
    public MetocLayerCommonSettings<MetocLayerCommonSettingsListener> getPrimaryMetocLayerSettings() {
        return this.primaryMetocLayerSettings;
    }
    
    /**
     * Gets the primary (global) route layer settings.
     * If more route layers are to coexist, each with individual settings, these local settings instances may register as observers of the returned instance in order to "obey" to changes to global settings.
     * @return The primary (global) route layer settings.
     */
    public RouteLayerCommonSettings<RouteLayerCommonSettingsListener> getPrimaryRouteLayerSettings() {
        return this.primaryRouteLayerSettings;
    }
    
    /**
     * Gets the primary (global) intended route layer settings.
     * If more intended route layers are to coexist, each with individual settings, these local settings instances may register as observers of the returned instance in order to "obey" to changes to global settings.
     * @return The primary (global) intended route layer settings.
     */
    public IntendedRouteLayerCommonSettings<IntendedRouteLayerCommonSettingsListener> getPrimaryIntendedRouteLayerSettings() {
        return this.primaryIntendedRouteLayerSettings;
    }
    
    /**
     * Gets the primary (global) dynamic predictor layer settings.
     * If more dynamic predictor layers are to coexist, each with individual settings, these local settings instances may register as observers of the returned instance in order to "obey" to changes to global settings.
     * @return The primary (global) dynamic predictor layer settings.
     */
    public DynamicPredictorLayerSettings getPrimaryDynamicPredictorLayerSettings() {
        return this.primaryDynamicPredictorLayerSettings;
    }
    
    /**
     * Get settings specifying connection parameters for the e-Nav services connection.
     * @return Settings specifying connection parameters for the e-Nav services connection
     */
    public NetworkSettings<NetworkSettingsListener> getEnavServicesHttpSettings() {
        return this.enavServicesHttpSettings;
    }
    
    /**
     * Get settings specifying connection parameters for the MonaLisa services connection.
     * @return Settings specifying connection parameters for the MonaLisa services connection
     */
    public NetworkSettings<NetworkSettingsListener> getMonaLisaHttpSettings() {
        return this.monaLisaHttpSettings;
    }
    
    /**
     * Get settings specifying connection parameters for the Maritime Cloud services connection.
     * @return Settings specifying connection parameters for the Maritime Cloud services connection
     */
    public NetworkSettings<NetworkSettingsListener> getMaritimeCloudHttpSettings() {
        return this.maritimeCloudHttpSettings;
    }
    
    public MetocHandlerCommonSettings<MetocHandlerCommonSettingsListener> getMetocHandlerSettings() {
        return this.metocHandlerSettings;
    }
    
    public AisHandlerCommonSettings<AisHandlerCommonSettingsListener> getAisHandlerSettings() {
        return this.aisHandlerSettings;
    }
    
    public PastTrackSettings<PastTrackSettingsListener> getPastTrackSettings() {
        return this.pastTrackSettings;
    }

    public VoyageLayerCommonSettings<VoyageLayerCommonSettingsListener> getVoyageLayerSettings() {
        return this.voyageLayerSettings;
    }
    
    /**
     * Resolves the given file in the current home settings folder
     * @param file the file to resolve
     * @return the resolved file
     */
    public Path resolve(String file) {
        file = file.startsWith("/") ? file.substring(1) : file;
        return EPD.getInstance().getHomePath().resolve(SETTINGS_FOLDER_NAME + "/" + file);
    }
    
    /**
     * Load the settings files as well as the workspace files
     */
    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        /*
         * Load primary/global AIS layer settings.
         * If ship/shore specific AIS layer settings are added later, move this to subclass.
         */
        AisLayerCommonSettings<AisLayerCommonSettingsListener> ais = ObservedSettings.loadFromFile(AisLayerCommonSettings.class, resolve(aisLayerSettingsFile).toFile());
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
        MSIHandlerCommonSettings<MSIHandlerCommonSettingsListener> msiHandlerSett = ObservedSettings.loadFromFile(MSIHandlerCommonSettings.class, resolve(msiHandlerSettingsFile).toFile());
        this.msiHandlerSettings = msiHandlerSett != null ? msiHandlerSett : new MSIHandlerCommonSettings<>();
        
        /*
         * Load primary/global MSI layer settings.
         * If ship/shore specific MSI layer settings are added later, move this to subclass.
         */
        MSILayerCommonSettings<MSILayerCommonSettingsListener> msiLayerSett = ObservedSettings.loadFromFile(MSILayerCommonSettings.class, resolve(msiLayerSettingsFile).toFile());
        this.msiLayerSettings = msiLayerSett != null ? msiLayerSett : new MSILayerCommonSettings<>();
        
        /*
         * Load e-Nav services connection settings.
         * If ship/shore specific e-Nav services connection settings are added later, move this to subclass.
         */
        NetworkSettings<NetworkSettingsListener> enavServices = ObservedSettings.loadFromFile(NetworkSettings.class, resolve(enavServicesHttpSettingsFile).toFile());
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
        // Loaded or new instance now ready for use.
        this.enavServicesHttpSettings = enavServices;
        
        /*
         * Load METOC handler settings.
         * If ship/shore specific METOC handler settings are added later, move this to subclass.
         */
        MetocHandlerCommonSettings<MetocHandlerCommonSettingsListener> metoc = ObservedSettings.loadFromFile(MetocHandlerCommonSettings.class, resolve(metocHandlerSettingsFile).toFile());
        this.metocHandlerSettings = metoc != null ? metoc : new MetocHandlerCommonSettings<>();
        
        /*
         * Load primary/global WMS layer settings.
         * If ship/shore specific WMS layer settings are added later, move this to subclass.
         */
        WMSLayerCommonSettings<WMSLayerCommonSettingsListener> wms = ObservedSettings.loadFromFile(WMSLayerCommonSettings.class, resolve(wmsLayerSettingsFile).toFile());
        this.primaryWmsLayerSettings = wms != null ? wms : new WMSLayerCommonSettings<>();
        
        /*
         * Load primary/global METOC layer settings.
         * If ship/shore specific METOC layer settings are added later, move this to subclass.
         */
        MetocLayerCommonSettings<MetocLayerCommonSettingsListener> metocLayer = ObservedSettings.loadFromFile(MetocLayerCommonSettings.class, resolve(metocLayerSettingsFile).toFile());
        this.primaryMetocLayerSettings = metocLayer != null ? metocLayer : new MetocLayerCommonSettings<>();
        
        /*
         * Load primary/global route layer settings.
         * If ship/shore specific route layer settings are added later, move this to subclass.
         */
        RouteLayerCommonSettings<RouteLayerCommonSettingsListener> routeLayer = ObservedSettings.loadFromFile(RouteLayerCommonSettings.class, resolve(routeLayerSettingsFile).toFile());
        this.primaryRouteLayerSettings = routeLayer != null ? routeLayer : new RouteLayerCommonSettings<>();
        
        /*
         * Load MonaLisa services connection settings.
         * If ship/shore specific MonaLisa services connection settings are added later, move this to subclass.
         */
        NetworkSettings<NetworkSettingsListener> monaLisaHttp = ObservedSettings.loadFromFile(NetworkSettings.class, resolve(monaLisaHttpSettingsFile).toFile());
        if(monaLisaHttp == null) {
            // Create new instance if no saved instance present.
            monaLisaHttp = new NetworkSettings<>();
            /*
             *  Default network settings connect to localhost.
             *  Update with proper defaults.
             */
            monaLisaHttp.setHost("www.optiroute.se/RouteRequest");
            monaLisaHttp.setPort(80);
        }
        // Loaded or new instance now ready for use.
        this.monaLisaHttpSettings = monaLisaHttp;
        
        /*
         * Load primary/global intended route layer settings.
         * If ship/shore specific intended route layer settings are added later, move this to subclass.
         */
        IntendedRouteLayerCommonSettings<IntendedRouteLayerCommonSettingsListener> intendedRouteLayer = ObservedSettings.loadFromFile(IntendedRouteLayerCommonSettings.class, resolve(intendedRouteLayerSettingsFile).toFile());
        this.primaryIntendedRouteLayerSettings = intendedRouteLayer != null ? intendedRouteLayer : new IntendedRouteLayerCommonSettings<>();
        
        NetworkSettings<NetworkSettingsListener> maritimeCloud = ObservedSettings.loadFromFile(NetworkSettings.class, resolve(maritimeCloudHttpSettingsFile).toFile());
        if(maritimeCloud == null) {
            // Create new instance if no saved instance present.
            maritimeCloud = new NetworkSettings<>();
            /*
             *  Default network settings connect to localhost.
             *  Update with proper defaults.
             */
            maritimeCloud.setHost("test.maritimecloud.net");
            maritimeCloud.setPort(43234);
        }
        // Loaded or new instance now ready for use.
        this.maritimeCloudHttpSettings = maritimeCloud;
        
        /*
         * Load AIS handler settings.
         * If ship/shore specific AIS handler settings are added later, move this to subclass.
         */
        AisHandlerCommonSettings<AisHandlerCommonSettingsListener> aisHandler = ObservedSettings.loadFromFile(AisHandlerCommonSettings.class, resolve(aisHandlerSettingsFile).toFile());
        this.aisHandlerSettings = ais != null ? aisHandler : new AisHandlerCommonSettings<>();
        
        /*
         * Load past track settings.
         * If ship/shore specific past track settings are added later, move this to subclass.
         */
        PastTrackSettings<PastTrackSettingsListener> pastTrack = ObservedSettings.loadFromFile(PastTrackSettings.class, resolve(pastTrackSettingsFile).toFile());
        this.pastTrackSettings = pastTrack != null ? pastTrack : new PastTrackSettings<>();
        
        /*
         * Load Voyage layer settings.
         * If ship/shore specific voyage layer settings are added later, move this to subclass.
         */
        VoyageLayerCommonSettings<VoyageLayerCommonSettingsListener> voyage = ObservedSettings.loadFromFile(VoyageLayerCommonSettings.class, resolve(voyageLayerSettingsFile).toFile());
        this.voyageLayerSettings = voyage != null ? voyage : new VoyageLayerCommonSettings<>();
        
        /*
         * Load Dynamic Predictor Layer settings.
         * If ship/shore specific dynamic predictor layer settings are added later, move this to subclasses.
         */
        DynamicPredictorLayerSettings dplSettings = ObservedSettings.loadFromFile(DynamicPredictorLayerSettings.class, resolve(dynamicPredictorLayerSettingsFile).toFile());
        this.primaryDynamicPredictorLayerSettings = dplSettings != null ? dplSettings : new DynamicPredictorLayerSettings();
    }

    /**
     * Save the settings to the files
     */
    public void saveToFile() {
        this.getAisHandlerSettings().saveToYamlFile(resolve(aisHandlerSettingsFile).toFile());
        this.getEnavServicesHttpSettings().saveToYamlFile(resolve(enavServicesHttpSettingsFile).toFile());
        this.getENCLayerSettings().saveToYamlFile(resolve(encLayerSettingsFile).toFile());
        this.getExternalSensorsSettings().saveToYamlFile(resolve(externalSensorsSettingsFile).toFile());
        this.getGuiSettings().saveToYamlFile(resolve(guiSettingsFile).toFile());
        this.getIntendedRouteHandlerSettings().saveToYamlFile(resolve(intendedRouteHandlerSettingsFile).toFile());
        this.getMapSettings().saveToYamlFile(resolve(mapSettingsFile).toFile());
        this.getMaritimeCloudHttpSettings().saveToYamlFile(resolve(maritimeCloudHttpSettingsFile).toFile());
        this.getMetocHandlerSettings().saveToYamlFile(resolve(metocHandlerSettingsFile).toFile());
        this.getMonaLisaHttpSettings().saveToYamlFile(resolve(monaLisaHttpSettingsFile).toFile());
        this.getMsiHandlerSettings().saveToYamlFile(resolve(msiHandlerSettingsFile).toFile());
        this.getPastTrackSettings().saveToYamlFile(resolve(pastTrackSettingsFile).toFile());
        this.getPrimaryAisLayerSettings().saveToYamlFile(resolve(aisLayerSettingsFile).toFile());
        this.getPrimaryIntendedRouteLayerSettings().saveToYamlFile(resolve(intendedRouteLayerSettingsFile).toFile());
        this.getPrimaryMetocLayerSettings().saveToYamlFile(resolve(metocLayerSettingsFile).toFile());
        this.getPrimaryMsiLayerSettings().saveToYamlFile(resolve(msiLayerSettingsFile).toFile());
        this.getPrimaryRouteLayerSettings().saveToYamlFile(resolve(routeLayerSettingsFile).toFile());
        this.getPrimaryWMSLayerSettings().saveToYamlFile(resolve(wmsLayerSettingsFile).toFile());
        this.getRouteManagerSettings().saveToYamlFile(resolve(routeManagerSettingsFile).toFile());
        this.getVoyageLayerSettings().saveToYamlFile(resolve(voyageLayerSettingsFile).toFile());
        this.getPrimaryDynamicPredictorLayerSettings().saveToYamlFile(resolve(dynamicPredictorLayerSettingsFile).toFile());
        
        // Save S57 settings
        this.getS57LayerSettings().saveSettings(resolve(s57LayerSettingsFile).toString());
    }
    
}

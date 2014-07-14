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

import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.Settings;
import dk.dma.epd.common.prototype.settings.layers.ENCLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.PastTrackSettings;
import dk.dma.epd.common.prototype.settings.observers.ENCLayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.PastTrackSettingsListener;
import dk.dma.epd.ship.settings.gui.GUISettings;
import dk.dma.epd.ship.settings.gui.MapSettings;
import dk.dma.epd.ship.settings.handlers.IntendedRouteHandlerSettings;
import dk.dma.epd.ship.settings.handlers.RouteManagerSettings;
import dk.dma.epd.ship.settings.layers.OwnShipLayerSettings;
import dk.dma.epd.ship.settings.observers.GUISettingsListener;
import dk.dma.epd.ship.settings.observers.RouteManagerSettingsListener;
import dk.dma.epd.ship.settings.sensor.ExternalSensorsSettings;


/**
 * Settings class
 */
public class EPDSettings extends Settings {
    
    /**
     * Filename for the file with own ship layer settings.
     */
    protected final String ownShipLayerSettingsFile = "own-ship-layer_settings.yaml";
    
    /**
     * Filename for the file with own ship past track settings.
     */
    protected final String ownShipPastTrackSettingsFile = "own-ship-past-track_settings.yaml";
    
    private GUISettings<GUISettingsListener> guiSettings;
    
    private MapSettings mapSettings;
    
    private RouteManagerSettings<RouteManagerSettingsListener> routeManagerSettings;
    
    private ExternalSensorsSettings externalSensorsSettings;
    
    private ENCLayerCommonSettings<ENCLayerCommonSettingsListener> encLayerSettings;
    
    private IntendedRouteHandlerSettings intendedRouteHandlerSettings;
    
    private OwnShipLayerSettings ownShipLayerSettings;
    
    /**
     * Past track settings for own ship.
     */
    private PastTrackSettings<PastTrackSettingsListener> ownShipPastTrackSettings;
    
    public EPDSettings() {
        super();
    }
    
    /**
     * Load the settings files as well as the workspace files
     */
    @SuppressWarnings("unchecked")
    @Override
    public void loadFromFile() {
        // Do work in super to load non-specialized settings.
        super.loadFromFile();
        
        // Load general gui settings.
        GUISettings<GUISettingsListener> gui = ObservedSettings.loadFromFile(GUISettings.class, resolve(guiSettingsFile).toFile());
        // Create new instance if no saved instance found.
        guiSettings = gui != null ? gui : new GUISettings<>();
        
        // Load map settings.
        MapSettings map = ObservedSettings.loadFromFile(MapSettings.class, resolve(mapSettingsFile).toFile());
        // Create new instance if no saved instance found.
        mapSettings = map != null ? map : new MapSettings();
        
        // Load route manager settings.
        RouteManagerSettings<RouteManagerSettingsListener> rms = ObservedSettings.loadFromFile(RouteManagerSettings.class, resolve(routeManagerSettingsFile).toFile());
        // Create new instance if no saved instance found.
        routeManagerSettings = rms != null ? rms : new RouteManagerSettings<>();
        
        /*
         *  Load external sensors settings.
         */
        ExternalSensorsSettings ext = ObservedSettings.loadFromFile(ExternalSensorsSettings.class, resolve(externalSensorsSettingsFile).toFile());
        // Use loaded instance or create new if the file was not found.
        this.externalSensorsSettings = ext != null ? ext : new ExternalSensorsSettings();
        
        /*
         *  Load ENC layer settings.
         *  Even though ship uses common version, we need to load it here instead of in super class as shore uses specific version.
         */
        ENCLayerCommonSettings<ENCLayerCommonSettingsListener> enc = ObservedSettings.loadFromFile(ENCLayerCommonSettings.class, resolve(encLayerSettingsFile).toFile());
        // Use loaded instance or create new if the file was not found.
        this.encLayerSettings = enc != null ? enc : new ENCLayerCommonSettings<>();
        
        // Load intended route handler settings.
        IntendedRouteHandlerSettings intendedRouteHandler = ObservedSettings.loadFromFile(IntendedRouteHandlerSettings.class, resolve(intendedRouteHandlerSettingsFile).toFile());
        // Use loaded instance or create new if the file was not found.
        this.intendedRouteHandlerSettings = intendedRouteHandler != null ? intendedRouteHandler : new IntendedRouteHandlerSettings();
        
        // Load own ship layer settings.
        OwnShipLayerSettings ownShipLayer = ObservedSettings.loadFromFile(OwnShipLayerSettings.class, resolve(ownShipLayerSettingsFile).toFile());
        // Use loaded instance or create new if the file was not found.
        this.ownShipLayerSettings = ownShipLayer != null ? ownShipLayer : new OwnShipLayerSettings();
        
        // Load own ship past track settings.
        PastTrackSettings<PastTrackSettingsListener> ownShipPastTrack = ObservedSettings.loadFromFile(PastTrackSettings.class, resolve(ownShipPastTrackSettingsFile).toFile());
        // Use loaded instance or create new if the file was not found.
        this.ownShipPastTrackSettings = ownShipPastTrack != null ? ownShipPastTrack : new PastTrackSettings<>();

    }

    /**
     * Save the settings to the files
     */
    @Override
    public void saveToFile() {
        super.saveToFile();
        this.ownShipLayerSettings.saveToYamlFile(resolve(ownShipLayerSettingsFile).toFile());
        this.ownShipPastTrackSettings.saveToYamlFile(resolve(ownShipPastTrackSettingsFile).toFile());
    }

    @Override
    public GUISettings<GUISettingsListener> getGuiSettings() {
        return this.guiSettings;
    }

    @Override
    public MapSettings getMapSettings() {
        return this.mapSettings;
    }
    
    @Override
    public RouteManagerSettings<RouteManagerSettingsListener> getRouteManagerSettings() {
        return this.routeManagerSettings;
    }
    
    @Override
    public ExternalSensorsSettings getExternalSensorsSettings() {
        return this.externalSensorsSettings;
    }
    
    @Override
    public ENCLayerCommonSettings<ENCLayerCommonSettingsListener> getENCLayerSettings() {
        return this.encLayerSettings;
    }
    
    @Override
    public IntendedRouteHandlerSettings getIntendedRouteHandlerSettings() {
        return this.intendedRouteHandlerSettings;
    }
    
    public OwnShipLayerSettings getOwnShipLayerSettings() {
        return this.ownShipLayerSettings;
    }
    
    
    public PastTrackSettings<PastTrackSettingsListener> getOwnShipPastTrackSettings() {
        return this.ownShipPastTrackSettings;
    }
}

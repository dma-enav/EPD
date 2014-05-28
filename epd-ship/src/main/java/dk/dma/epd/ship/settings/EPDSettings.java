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

import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.Settings;
import dk.dma.epd.common.prototype.settings.layers.ENCLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.PastTrackSettings;
import dk.dma.epd.common.prototype.settings.sensor.ExternalSensorsCommonSettings;
import dk.dma.epd.ship.settings.gui.GUISettings;
import dk.dma.epd.ship.settings.gui.MapSettings;
import dk.dma.epd.ship.settings.handlers.IntendedRouteHandlerSettings;
import dk.dma.epd.ship.settings.handlers.RouteManagerSettings;
import dk.dma.epd.ship.settings.layers.OwnShipLayerSettings;


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
    
    private GUISettings<GUISettings.IObserver> guiSettings;
    
    private MapSettings mapSettings;
    
    private RouteManagerSettings<RouteManagerSettings.IObserver> routeManagerSettings;
    
    private ExternalSensorsCommonSettings<ExternalSensorsCommonSettings.IObserver> externalSensorsSettings;
    
    private ENCLayerCommonSettings<ENCLayerCommonSettings.IObserver> encLayerSettings;
    
    private IntendedRouteHandlerSettings intendedRouteHandlerSettings;
    
    private OwnShipLayerSettings ownShipLayerSettings;
    
    /**
     * Past track settings for own ship.
     */
    private PastTrackSettings<PastTrackSettings.IObserver> ownShipPastTrackSettings;
    
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
        GUISettings<GUISettings.IObserver> gui = ObservedSettings.loadFromFile(GUISettings.class, resolve(guiSettingsFile).toFile());
        // Create new instance if no saved instance found.
        guiSettings = gui != null ? gui : new GUISettings<>();
        
        // Load map settings.
        MapSettings map = ObservedSettings.loadFromFile(MapSettings.class, resolve(mapSettingsFile).toFile());
        // Create new instance if no saved instance found.
        mapSettings = map != null ? map : new MapSettings();
        
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
        
        /*
         *  Load ENC layer settings.
         *  Even though ship uses common version, we need to load it here instead of in super class as shore uses specific version.
         */
        ENCLayerCommonSettings<ENCLayerCommonSettings.IObserver> enc = ObservedSettings.loadFromFile(ENCLayerCommonSettings.class, resolve(encLayerSettingsFile).toFile());
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
        PastTrackSettings<PastTrackSettings.IObserver> ownShipPastTrack = ObservedSettings.loadFromFile(PastTrackSettings.class, resolve(ownShipPastTrackSettingsFile).toFile());
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
    public GUISettings<GUISettings.IObserver> getGuiSettings() {
        return this.guiSettings;
    }

    @Override
    public MapSettings getMapSettings() {
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
    
    @Override
    public ENCLayerCommonSettings<ENCLayerCommonSettings.IObserver> getENCLayerSettings() {
        return this.encLayerSettings;
    }
    
    @Override
    public IntendedRouteHandlerSettings getIntendedRouteHandlerSettings() {
        return this.intendedRouteHandlerSettings;
    }
    
    public OwnShipLayerSettings getOwnShipLayerSettings() {
        return this.ownShipLayerSettings;
    }
    
    
    public PastTrackSettings<PastTrackSettings.IObserver> getOwnShipPastTrackSettings() {
        return this.ownShipPastTrackSettings;
    }
}

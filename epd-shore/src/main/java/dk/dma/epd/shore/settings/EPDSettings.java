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
package dk.dma.epd.shore.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.Settings;
import dk.dma.epd.common.prototype.settings.gui.MapCommonSettings;
import dk.dma.epd.common.prototype.settings.handlers.IntendedRouteHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.handlers.RouteManagerCommonSettings;
import dk.dma.epd.common.prototype.settings.observers.IntendedRouteHandlerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.MapCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.RouteManagerCommonSettingsListener;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.settings.gui.ENCLayerSettings;
import dk.dma.epd.shore.settings.gui.GUISettings;
import dk.dma.epd.shore.settings.sensor.ExternalSensorsSettings;

/**
 * Settings class
 */
public class EPDSettings extends Settings implements Serializable {

    protected final String shoreIdentitySettingsFile = "shore-identity_settings.yaml";
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(EPDSettings.class);

    private String settingsFile = "settings.properties";
    private String defaultWorkSpace ="workspaces/default.workspace";
    private String workspaceFile = "";
    
    private Workspace workspace = new Workspace();

    private GUISettings guiSettings;

    private MapCommonSettings<MapCommonSettingsListener> mapSettings;
    
    private ExternalSensorsSettings externalSensorsSettings;
    
    private ENCLayerSettings encLayerSettings;
    
    private IntendedRouteHandlerCommonSettings<IntendedRouteHandlerCommonSettingsListener> intendedRouteHandlerSettings;
    
    private IdentitySettings shoreIdentitySettings;

    private RouteManagerCommonSettings<RouteManagerCommonSettingsListener> routeManagerSettings;
    
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
        
        // Load general GUI settings
        GUISettings gui = ObservedSettings.loadFromFile(GUISettings.class, resolve(guiSettingsFile).toFile());
        // Create new instance if no saved instance found.
        this.guiSettings = gui != null ? gui : new GUISettings();
        
        /*
         * Load map settings.
         * Even though Shore uses common version, we need to load it here instead of in super class as Ship uses specific version.
         */
        MapCommonSettings<MapCommonSettingsListener> map = ObservedSettings.loadFromFile(MapCommonSettings.class, resolve(mapSettingsFile).toFile());
        this.mapSettings = map != null ? map : new MapCommonSettings<>();
        
        // Load external sensors settings. 
        ExternalSensorsSettings ext = ObservedSettings.loadFromFile(ExternalSensorsSettings.class, resolve(externalSensorsSettingsFile).toFile());
        // Use loaded instance or create new if the file was not found.
        this.externalSensorsSettings = ext != null ? ext : new ExternalSensorsSettings();
        
        // Load ENC layer settings.
        ENCLayerSettings enc = ObservedSettings.loadFromFile(ENCLayerSettings.class, resolve(encLayerSettingsFile).toFile());
        this.encLayerSettings = enc != null ? enc : new ENCLayerSettings();
        
        /*
         *  Load intended route handler settings.
         *  Even though Shore uses common version, we need to load it here instead of in super class as Ship uses specific version.
         */
        IntendedRouteHandlerCommonSettings<IntendedRouteHandlerCommonSettingsListener> intendedRouteHandler = ObservedSettings.loadFromFile(IntendedRouteHandlerCommonSettings.class, resolve(intendedRouteHandlerSettingsFile).toFile());
        this.intendedRouteHandlerSettings = intendedRouteHandler != null ? intendedRouteHandler : new IntendedRouteHandlerCommonSettings<>();
        
        // Load shore identity settings
        IdentitySettings idSettings = ObservedSettings.loadFromFile(IdentitySettings.class, resolve(shoreIdentitySettingsFile).toFile());
        this.shoreIdentitySettings = idSettings != null ? idSettings : new IdentitySettings();
        
        /*
         *  Load Route Manager settings.
         *  Even though Shore uses common version, we need to load it here instead of in super class as Ship uses specific version.
         */
        RouteManagerCommonSettings<RouteManagerCommonSettingsListener> routeMgr = ObservedSettings.loadFromFile(RouteManagerCommonSettings.class, resolve(routeManagerSettingsFile).toFile());
        this.routeManagerSettings = routeMgr != null ? routeMgr : new RouteManagerCommonSettings<>();
        
        workspaceFile = guiSettings.getWorkspace();

        if (workspaceFile != null) {

            // Load default workspace - will ALWAYS load from workspaces folder
            Properties workspaceProp = new Properties();
            
            if (!loadProperties(workspaceProp, workspaceFile)) {
                // LOG.info("No workspace file found - reverting to default");
                LOG.error("No workspace file found - reverting to default - "
                        + workspaceFile + " was invalid");
                loadProperties(workspaceProp, defaultWorkSpace);
                guiSettings.setWorkspace(defaultWorkSpace);
            }
            workspace.readProperties(workspaceProp);
        }
    }

    @Override
    public GUISettings getGuiSettings() {
        return this.guiSettings;
    }
    
    @Override
    public MapCommonSettings<MapCommonSettingsListener> getMapSettings() {
        return this.mapSettings;
    }
    
    @Override
    public ExternalSensorsSettings getExternalSensorsSettings() {
        return this.externalSensorsSettings;
    }
    
    @Override
    public ENCLayerSettings getENCLayerSettings() {
        return this.encLayerSettings;
    }
    
    @Override
    public IntendedRouteHandlerCommonSettings<IntendedRouteHandlerCommonSettingsListener> getIntendedRouteHandlerSettings() {
        return this.intendedRouteHandlerSettings;
    }
    
    public IdentitySettings getShoreIdentitySettings() {
        return shoreIdentitySettings;
    }
    
    @Override
    public RouteManagerCommonSettings<RouteManagerCommonSettingsListener> getRouteManagerSettings() {
        return this.routeManagerSettings;
    }
    
    /**
     * Load a workspace
     * 
     * @param parent
     * @param filename
     * @return
     */
    public Workspace loadWorkspace(String parent, String filename) {
        Properties workspaceProp = new Properties();
        if (!PropUtils.loadProperties(workspaceProp, parent, filename)) {
            LOG.info("No workspace file found - reverting to default");
            System.out
                    .println("No workspace file found - reverting to default - "
                            + parent + filename + " was invalid");
            PropUtils.loadProperties(workspaceProp, EPDShore.getInstance().getHomePath()
                    .toString(), defaultWorkSpace);
        }
        guiSettings.setWorkspace("workspaces/" + filename);
        workspace = new Workspace();
        workspace.readProperties(workspaceProp);
        return workspace;
    }

    /**
     * Save the current workspace
     * 
     * @param mapWindows
     * @param filename
     */
    public void saveCurrentWorkspace(List<JMapFrame> mapWindows, String filename) {
        Properties props = new Properties();
        workspace.setProperties(props, mapWindows);
        saveProperties(props, "workspaces/" + filename, "# workspace settings saved: " + new Date());
        guiSettings.setWorkspace("/workspaces/" + filename);
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public String getSettingsFile() {
        return settingsFile;
    }
    
    /**
     * Loads the given properties file.
     * @deprecated Only intended for use during {@link Workspace} initialization.
     * @param props the properties to load the file into
     * @param file the properties file to load
     * @return success or failure
     */
    @Deprecated
    protected boolean loadProperties(Properties props, String file) {
        if (file.startsWith("/")) {
            file = file.substring(1);
        }
        try {
            props.load(new FileInputStream(resolve(file).toFile()));
        } catch (FileNotFoundException e) {
            LOG.error("No settings file found: " + resolve(file));
            return false;
        } catch (IOException e) {
            LOG.error("Settings file could not be loaded: " + resolve(file));
            return false;
        }
        
        LOG.info("Settings file loaded, path=" + resolve(file));
        return true;
    }
    
    /**
     * Saves the properties to the given file.
     * @deprecated Only intended for use during {@link Workspace} serialization.
     * @param props the properties to save
     * @param file the file to save the properties to
     * @return success or failure
     */
    @Deprecated
    protected boolean saveProperties(Properties props, String file, String header) {
        if (file.startsWith("/")) {
            file = file.substring(1);
        }
        try (
                FileWriter outFile = new FileWriter(resolve(file).toFile());
                PrintWriter out = new PrintWriter(outFile);) {
                if (header != null) {
                    out.println(header);
                }
                TreeSet<String> keys = new TreeSet<>();
                for (Object key : props.keySet()) {
                    keys.add((String) key);
                }
                for (String key : keys) {
                    out.println(key + "=" + props.getProperty(key));
                }
        } catch (IOException e) {
            LOG.error("Failed to save settings file " + resolve(file) + ": " + e.getMessage());
            return false;
        }
        
        LOG.info("Settings file updated, path=" + resolve(file));
        return true;
    }
}

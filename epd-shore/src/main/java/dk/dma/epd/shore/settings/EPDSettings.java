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
package dk.dma.epd.shore.settings;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.common.prototype.settings.S57LayerSettings;
import dk.dma.epd.common.prototype.settings.Settings;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.JMapFrame;

/**
 * Settings class
 */
public class EPDSettings extends Settings implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(EPDSettings.class);

    private String settingsFile = "settings.properties";
    private String defaultWorkSpace ="workspaces/default.workspace";
    private String workspaceFile = "";

    private EPDGuiSettings guiSettings = new EPDGuiSettings();

    private EPDMapSettings mapSettings = new EPDMapSettings();
    private EPDSensorSettings sensorSettings = new EPDSensorSettings();
    private EPDNavSettings navSettings = new EPDNavSettings();

    private EPDAisSettings aisSettings = new EPDAisSettings();
    private EPDEnavSettings enavSettings = new EPDEnavSettings();
    private EPDCloudSettings cloudSettings = new EPDCloudSettings();
    
    private Workspace workspace = new Workspace();

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

        saveProperties(props, settingsFile, "# esd settings saved: " + new Date());
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

    @Override
    public EPDGuiSettings getGuiSettings() {
        return guiSettings;
    }

    @Override
    public EPDMapSettings getMapSettings() {
        return mapSettings;
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
    public EPDNavSettings getNavSettings() {
        return navSettings;
    }

    @Override
    public EPDSensorSettings getSensorSettings() {
        return sensorSettings;
    }

    @Override
    public S57LayerSettings getS57Settings() {
        return null;
    }
    
    @Override
    public EPDCloudSettings getCloudSettings() {
        return cloudSettings;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public String getSettingsFile() {
        return settingsFile;
    }
}

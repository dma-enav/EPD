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

import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.JMapFrame;

/**
 * Settings class
 */
public class ESDSettings implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(ESDSettings.class);

    private String settingsFile = "\\settings.properties";
    private String defaultWorkSpace = "\\workspaces\\default.workspace";
    private String workspaceFile = "";

    private ESDGuiSettings guiSettings = new ESDGuiSettings();

    private ESDMapSettings mapSettings = new ESDMapSettings();
    private ESDSensorSettings sensorSettings = new ESDSensorSettings();
    private ESDNavSettings navSettings = new ESDNavSettings();

    private ESDAisSettings aisSettings = new ESDAisSettings();
    private ESDEnavSettings enavSettings = new ESDEnavSettings();
    private Workspace workspace = new Workspace();

    public ESDSettings() {

    }

    public ESDSettings(String settingsFile) {
        this.settingsFile = settingsFile;
    }

    /**
     * Load the settings files as well as the workspace files
     */
    public void loadFromFile() {
        // Open properties file
        Properties props = new Properties();
        LOG.info("Trying to load " + EPDShore.getHomePath().toString() + "\\"
                + settingsFile);
        if (!PropUtils.loadProperties(props, EPDShore.getHomePath().toString()
                + "\\", settingsFile)) {
            LOG.info("No settings file found");
            return;
        }
        aisSettings.readProperties(props);
        enavSettings.readProperties(props);
        guiSettings.readProperties(props);
        mapSettings.readProperties(props);
        navSettings.readProperties(props);
        sensorSettings.readProperties(props);

        workspaceFile = guiSettings.getWorkspace();

        if (workspaceFile != null) {

            // Load default workspace - will ALWAYS load from workspaces folder
            Properties workspaceProp = new Properties();
            if (!PropUtils.loadProperties(workspaceProp, EPDShore.getHomePath()
                    .toString(), workspaceFile)) {
                // LOG.info("No workspace file found - reverting to default");
                LOG.error("No workspace file found - reverting to default - "
                        + workspaceFile + " was invalid");
                PropUtils.loadProperties(workspaceProp, EPDShore.getHomePath()
                        .toString(), defaultWorkSpace);
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
            PropUtils.loadProperties(workspaceProp, EPDShore.getHomePath()
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
    public void saveToFile() {
        Properties props = new Properties();

        aisSettings.setProperties(props);
        enavSettings.setProperties(props);
        guiSettings.setProperties(props);
        mapSettings.setProperties(props);
        navSettings.setProperties(props);
        sensorSettings.setProperties(props);

        // navSettings.setProperties(props);

        try {
            FileWriter outFile = new FileWriter(EPDShore.getHomePath()
                    .toString() + settingsFile);
            PrintWriter out = new PrintWriter(outFile);
            out.println("# esd settings saved: " + new Date());
            TreeSet<String> keys = new TreeSet<String>();
            for (Object key : props.keySet()) {
                keys.add((String) key);
            }
            for (String key : keys) {
                out.println(key + "=" + props.getProperty(key));
            }
            out.close();
        } catch (IOException e) {
            LOG.error("Failed to save settings file");
        }
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
        try {
            filename = EPDShore.getHomePath().toString() + "/workspaces/"
                    + filename;
            // System.out.println("Trying to save to: " + filename);
            FileWriter outFile = new FileWriter(filename);
            PrintWriter out = new PrintWriter(outFile);
            out.println("# workspace settings saved: " + new Date());
            TreeSet<String> keys = new TreeSet<String>();
            for (Object key : props.keySet()) {
                keys.add((String) key);
            }
            for (String key : keys) {
                out.println(key + "=" + props.getProperty(key));
            }
            out.close();
            guiSettings.setWorkspace(filename);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            LOG.error("Failed to save settings file");
        }
    }

    public ESDGuiSettings getGuiSettings() {
        return guiSettings;
    }

    public ESDMapSettings getMapSettings() {
        return mapSettings;
    }

    public String getSettingsFile() {
        return settingsFile;
    }

    public ESDAisSettings getAisSettings() {
        return aisSettings;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public ESDEnavSettings getEnavSettings() {
        return enavSettings;
    }

    public ESDNavSettings getNavSettings() {
        return navSettings;
    }

    public ESDSensorSettings getSensorSettings() {
        return sensorSettings;
    }

}

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
package dk.dma.epd.common.prototype.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Properties;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.EPD;

/**
 * Abstract parent class the encapsulates the 
 * list of specialized settings 
 */
public abstract class Settings {

    private static final Logger LOG = LoggerFactory.getLogger(Settings.class);
    
    public abstract GuiSettings getGuiSettings();

    public abstract MapSettings getMapSettings();

    public abstract SensorSettings getSensorSettings();

    public abstract NavSettings getNavSettings();

    public abstract AisSettings getAisSettings();

    public abstract EnavSettings getEnavSettings();

    public abstract S57LayerSettings getS57Settings();
    
    public abstract CloudSettings getCloudSettings();

    /**
     * Resolves the given file in the current home folder
     * @param file the file to resolve
     * @return the resolved file
     */
    public Path resolve(String file) {
        return EPD.getInstance().getHomePath().resolve(file);
    }
    
    /**
     * Loads the given properties file
     * @param props the properties to load the file into
     * @param file the properties file to load
     * @return success or failure
     */
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
     * Saves the properties to the given file
     * @param props the properties to save
     * @param file the file to save the properties to
     * @return success or failure
     */
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
       
    /**
     * Load the settings files as well as the workspace files
     */
    public abstract void loadFromFile();

    /**
     * Save the settings to the files
     */
    public abstract void saveToFile();        
}

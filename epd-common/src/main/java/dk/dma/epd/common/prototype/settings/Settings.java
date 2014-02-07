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

/**
 * Abstract parent class the encapsulates the 
 * list of specialized settings 
 */
public abstract class Settings {

    private static final String READ_ONLY = "dma.settings.readonly";
    
    public abstract GuiSettings getGuiSettings();

    public abstract MapSettings getMapSettings();

    public abstract SensorSettings getSensorSettings();

    public abstract NavSettings getNavSettings();

    public abstract AisSettings getAisSettings();

    public abstract EnavSettings getEnavSettings();

    public abstract S57LayerSettings getS57Settings();
    
    public abstract CloudSettings getCloudSettings();
    
    /**
     * Load the settings files as well as the workspace files
     */
    public abstract void loadFromFile();

    /**
     * Save the settings to the files
     */
    public abstract void doSaveToFile();
        
    /**
     * Save the settings to the files unless the system is started 
     * in read-only mode
     */
    public final void saveToFile() {
        if (!isReadOnly()) {
            doSaveToFile();
        }
    }
    
    /**
     * Returns if the updated settings are persisted to disk
     * @return if the updated settings are persisted to disk
     */
    public static boolean isReadOnly() {
        return "true".equals(System.getProperty(READ_ONLY));
    }
    
    /**
     * Sets if the updated settings are persisted to disk
     * @param readOnly if the updated settings are persisted to disk
     */
    public static void setReadOnly(boolean readOnly) {
        System.setProperty(READ_ONLY, String.valueOf(readOnly));
    }
}

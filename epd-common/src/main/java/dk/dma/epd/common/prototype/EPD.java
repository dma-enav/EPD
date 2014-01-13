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
package dk.dma.epd.common.prototype;

import java.nio.file.Path;
import java.util.Properties;

import dk.dma.epd.common.prototype.settings.Settings;


public abstract class EPD<S extends Settings> {
    
    protected static EPD<?> instance;    
    protected S settings;
    protected Properties properties = new Properties();
    
    /**
     * Constructor
     */
    protected EPD() {
        instance = this;
    }
    
    public static EPD<?> getInstance() {
        return instance;
    }
    
    public abstract Path getHomePath();
    
    /**
     * Returns the settings associated with the EPD system
     * @return the settings associated with the EPD system
     */
    public S getSettings() {
        return settings;
    }
    
    /**
     * Returns the properties
     * @return - properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Load the properties file
     */
    public abstract Properties loadProperties();

    /**
     * Function used to create a thread
     * 
     * @param t
     *            - class to create thread on
     * @param name
     *            - Thread name
     */
    public static Thread startThread(Runnable t, String name) {
        Thread thread = new Thread(t);
        thread.setName(name);
        thread.start();
        return thread;
    }

    public Object getMainFrame() {
        return null;
    }
}

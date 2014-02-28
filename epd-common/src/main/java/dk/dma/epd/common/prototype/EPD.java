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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.Properties;

import javax.swing.ImageIcon;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.Resources;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.gui.MainFrameCommon;
import dk.dma.epd.common.prototype.gui.SystemTrayCommon;
import dk.dma.epd.common.prototype.gui.notification.NotificationCenterCommon;
import dk.dma.epd.common.prototype.gui.settings.ISettingsListener;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSensor;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon;
import dk.dma.epd.common.prototype.settings.SensorSettings;
import dk.dma.epd.common.prototype.settings.Settings;

/**
 * Abstract super class for the main system, i.e either 
 * {@code EPDShore} or {@code EPDShip}
 */
public abstract class EPD implements ISettingsListener {
    
    protected static EPD instance;    
    protected Settings settings;
    protected SystemTrayCommon systemTray;
    protected Properties properties = new Properties();
    
    // Common services
    protected ChatServiceHandlerCommon chatServiceHandler;
    protected AisHandlerCommon aisHandler;
    protected MsiHandler msiHandler;
    protected NotificationCenterCommon notificationCenter;
    
    /**
     * Constructor
     */
    protected EPD() {
        instance = this;
    }
    
    /**
     * Factory method that returns a reference to the current {@code EPD}
     * system, i.e. either {@code EPDShore} or {@code EPDShip}.
     * 
     * @return a reference to the current {@code EPD} system
     */
    public static EPD getInstance() {
        return instance;
    }
    
    /**
     * Returns a {@linkplain Resource} instance which loads resource from
     * the same class-loader/jar-file as the {@code EPD} class.
     * 
     * @return a new {@linkplain Resource} instance
     */
    public static Resources res() {
       return Resources.get(EPD.class); 
    }
    
    /**
     * Returns the path to the home folder, used for settings,
     * persisted data, etc.
     * @return the path to the home folder
     */
    public abstract Path getHomePath();
    
    /**
     * Returns the settings associated with the EPD system
     * @return the settings associated with the EPD system
     */
    public Settings getSettings() {
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

    /**
     * Starts the sensors as defined in the {@linkplain SensorSettings} and hook up listeners
     */
    protected abstract void startSensors();
    
    /**
     * Stops all sensors and remove listeners
     */
    protected abstract void stopSensors();
    
    /**
     * Stop {@code sensor} and wait at most {@code timeout} ms for it to terminate.
     * 
     * @param sensor the sensor to stop
     * @param timeout the time in ms to wait for the sensor to termine
     * @return if the sensor was terminated
     */
    protected boolean stopSensor(NmeaSensor sensor, long timeout) {
        // Sanity check
        if (sensor == null) {
            return true;
        }
    
        sensor.stop();
        long t0 = System.currentTimeMillis();
        while (!sensor.hasTerminated() && System.currentTimeMillis() - t0 < timeout) {
            try { 
                Thread.sleep(100); 
            } catch (Exception ex) {
            }
        }
        return sensor.hasTerminated();
    }
   
    /**
     * Returns a reference to the main frame of the application
     * @return a reference to the main frame of the application
     */
    public abstract MainFrameCommon getMainFrame();
    
    /**
     * Returns the system tray
     * @return the system tray
     */
    public SystemTrayCommon getSystemTray() {
        return systemTray;
    }

    /**
     * Returns a reference to the chat service
     * @return a reference to the chat service
     */
    public ChatServiceHandlerCommon getChatServiceHandler() {
        return chatServiceHandler;
    }

    /**
     * Returns a reference to the AIS handler
     * @return a reference to the AIS handler
     */
    public AisHandlerCommon getAisHandler() {
        return aisHandler;
    }

    /**
     * Return the msiHandker
     * @return - MsiHandler
     */
    public MsiHandler getMsiHandler() {
        return msiHandler;
    }
    
    /**
     * Returns a reference to the notification center
     * @return a reference to the notification center
     */
    public NotificationCenterCommon getNotificationCenter() {
        return notificationCenter;
    }
    
    /**
     * Returns the current position of the EPD system
     * @return the current position of the EPD system
     */
    public abstract Position getPosition();
    
    /**
     * Returns the default shore mouse mode service list
     * @return the default shore mouse mode service list
     */
    public abstract String[] getDefaultMouseModeServiceList();
    
    /**
     * Call this method to terminate the application
     * @param restart whether to restart or not
     */
    public abstract void closeApp(boolean restart);
    
    /**
     * Returns the application icon
     * @return the application icon
     */
    public Image getAppIcon() {
        ImageIcon icon =  Resources.get(getInstance().getClass()).getCachedImageIcon("/images/appicon.png");
        if (icon != null) {
            return icon.getImage();
        }
        return null;
    }    

    /**
     * Returns the application icon scaled to the given size
     * @param size the size of the app icon
     * @return the application icon scaled to the given size
     */
    public Image getAppIcon(int size) {
        Image appIcon = getAppIcon();
        if (appIcon != null) {
            return appIcon.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
        }
        return null;
    }    
    
    /**
     * If Key caps is pressed during start up, and if so,
     * asks the user for a home path.<br>
     * Otherwise, the {@code defaultHomePath} is returned
     * 
     * @param defaultHomePath the default home path
     * @return the chosen home path
     */
    protected Path determineHomePath(Path defaultHomePath) {
        if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
            return HomePathDialog.determineHomePath(defaultHomePath);
        }
        // Caps-lock not on, return default home path
        return defaultHomePath;  
    }
}

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

package dk.dma.epd.common.prototype;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.Resources;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.fal.FALManagerCommon;
import dk.dma.epd.common.prototype.gui.MainFrameCommon;
import dk.dma.epd.common.prototype.gui.SystemTrayCommon;
import dk.dma.epd.common.prototype.gui.notification.NotificationCenterCommon;
import dk.dma.epd.common.prototype.gui.settings.ISettingsListener;
import dk.dma.epd.common.prototype.model.identity.IdentityHandler;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSensor;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon;
import dk.dma.epd.common.prototype.service.FALHandlerCommon;
import dk.dma.epd.common.prototype.service.MaritimeCloudService;
import dk.dma.epd.common.prototype.service.MsiNmServiceHandlerCommon;
import dk.dma.epd.common.prototype.service.RouteSuggestionHandlerCommon;
import dk.dma.epd.common.prototype.service.StrategicRouteHandlerCommon;
import dk.dma.epd.common.prototype.service.VoctHandlerCommon;
import dk.dma.epd.common.prototype.settings.SensorSettings;
import dk.dma.epd.common.prototype.settings.Settings;
import net.maritimecloud.core.id.MaritimeId;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;


import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Abstract super class for the main system, i.e either {@code EPDShore} or {@code EPDShip}
 */
public abstract class EPD implements ISettingsListener {

    protected static EPD instance;
    protected Settings settings;
    protected SystemTrayCommon systemTray;
    protected Properties properties = new Properties();
    protected volatile boolean restart;
    protected volatile Path homePath;
    

    // Common services
    protected RouteManagerCommon routeManager;
    protected FALManagerCommon falManager;
    protected MaritimeCloudService maritimeCloudService;
    protected ChatServiceHandlerCommon chatServiceHandler;
    protected AisHandlerCommon aisHandler;
    protected MsiNmServiceHandlerCommon msiNmHandler;
    protected NotificationCenterCommon notificationCenter;
    protected StrategicRouteHandlerCommon strategicRouteHandler;
    protected RouteSuggestionHandlerCommon routeSuggestionHandler;
    protected IdentityHandler identityHandler;
    protected VoctHandlerCommon voctHandler;
    protected FALHandlerCommon falHandler;

    /**
     * Constructor
     */
    protected EPD() {
        instance = this;

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (restart) {
                    restartApp();
                }
            }
        });
    }

    /**
     * Factory method that returns a reference to the current {@code EPD} system, i.e. either {@code EPDShore} or {@code EPDShip}.
     * 
     * @return a reference to the current {@code EPD} system
     */
    public static EPD getInstance() {
        return instance;
    }

    /**
     * Returns a {@code Resource} instance which loads resource from the same class-loader/jar-file as the {@code EPD} class.
     * 
     * @return a new {@code Resource} instance
     */
    public static Resources res() {
        return Resources.get(EPD.class);
    }

    /**
     * Returns the path to the home folder, used for settings, persisted data, etc.
     * 
     * @return the path to the home folder
     */
    public abstract Path getHomePath();

    /**
     * Returns the settings associated with the EPD system
     * 
     * @return the settings associated with the EPD system
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Returns the properties
     * 
     * @return - properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Load the properties file.
     * Loads the property file specified in {@link #getPropertyFileName()}
     * from the homepath.
     */
    public Properties loadProperties() {
        String propertyFileName = getPropertyFileName();
        Path propertyFile = getHomePath().resolve(propertyFileName);
        try {
            InputStream in = Files.newInputStream(propertyFile);
            if (in == null) {
                throw new IOException("Properties file not found");
            }
            properties.load(in);
            in.close();
        } catch (IOException e) {
            propertyLoadError("Failed to load resources", e);
        }
        return properties;
    }

    /**
     * The name of the property file to load.
     * Specify the name without any location
     * @See {@link #loadProperties()}
     * @return
     */
    protected abstract String getPropertyFileName();

    protected abstract void propertyLoadError(String msg, IOException e);

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
     * @param sensor
     *            the sensor to stop
     * @param timeout
     *            the time in ms to wait for the sensor to termine
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
     * 
     * @return a reference to the main frame of the application
     */
    public abstract MainFrameCommon getMainFrame();

    /**
     * Returns the system tray
     * 
     * @return the system tray
     */
    public SystemTrayCommon getSystemTray() {
        return systemTray;
    }

    /**
     * Returns a reference to the chat service
     * 
     * @return a reference to the chat service
     */
    public ChatServiceHandlerCommon getChatServiceHandler() {
        return chatServiceHandler;
    }

    /**
     * Returns a reference to the AIS handler
     * 
     * @return a reference to the AIS handler
     */
    public AisHandlerCommon getAisHandler() {
        return aisHandler;
    }

    public RouteManagerCommon getRouteManager() {
        return routeManager;
    }

    public StrategicRouteHandlerCommon getStrategicRouteHandler() {
        return strategicRouteHandler;
    }

    public RouteSuggestionHandlerCommon getRouteSuggestionHandler() {
        return routeSuggestionHandler;
    }

    public MsiNmServiceHandlerCommon getMsiNmHandler() {
        return msiNmHandler;
    }

    public MaritimeCloudService getMaritimeCloudService() {
        return maritimeCloudService;
    }

    public IdentityHandler getIdentityHandler() {
        return identityHandler;
    }

    /**
     * Returns a reference to the notification center
     * 
     * @return a reference to the notification center
     */
    public NotificationCenterCommon getNotificationCenter() {
        return notificationCenter;
    }

    /**
     * Returns the current position of the EPD system
     * 
     * @return the current position of the EPD system
     */
    public abstract Position getPosition();

    /**
     * Returns the MMSI of the EPD system
     * 
     * @return the MMSI of the EPD system
     */
    public abstract Long getMmsi();

    /**
     * Returns the maritime id of the EPD system
     * 
     * @return the maritime id of the EPD system
     */
    public abstract MaritimeId getMaritimeId();

    /**
     * Returns the default shore mouse mode service list
     * 
     * @return the default shore mouse mode service list
     */
    public abstract String[] getDefaultMouseModeServiceList();
    


    

    /**
     * Call this method to terminate the application
     * 
     * @param restart
     *            whether to restart or not
     */
    public abstract void closeApp(boolean restart);

    /**
     * Restarts the application. This is called from the shutdown hook
     */
    private void restartApp() {
        List<String> cmd = new ArrayList<>();
        cmd.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cmd.add(jvmArg);
        }
        cmd.add("-cp");
        cmd.add(ManagementFactory.getRuntimeMXBean().getClassPath());
        cmd.add(getClass().getName());

        // If homePath is defined, add it as the runtime argument
        if (homePath != null) {
            cmd.add(homePath.toString());
        }

        try {
            System.out.println("Re-launching using command:\n  " + cmd);
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the user that EPD is already running. Prompts whether to restart or bail
     */
    protected void handleEpdAlreadyRunning() {
        int result = JOptionPane.showOptionDialog(null, "One application instance already running.\n"
                + "Either close or restart with caps-lock on\nto select a different home folder.", "Error",
                JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[] { "Close", "Restart" }, null);
        if (result == JOptionPane.NO_OPTION) {
            // Restart
            restart = true;
            // But do not re-use current home path
            homePath = null;
        }
        System.exit(1);
    }

    /**
     * Returns the application icon
     * 
     * @return the application icon
     */
    public Image getAppIcon() {
        ImageIcon icon = Resources.get(getInstance().getClass()).getCachedImageIcon("/images/appicon.png");
        if (icon != null) {
            return icon.getImage();
        }
        return null;
    }

    /**
     * Returns the application icon scaled to the given size
     * 
     * @param size
     *            the size of the app icon
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
     * If Key caps is pressed during start up, and if so, asks the user for a home path.<br>
     * Otherwise, the {@code defaultHomePath} is returned
     * 
     * @param defaultHomePath
     *            the default home path
     * @return the chosen home path
     */
    protected Path determineHomePath(Path defaultHomePath) {
        if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
            return HomePathDialog.determineHomePath(defaultHomePath);
        }
        // Caps-lock not on, return default home path
        return defaultHomePath;
    }

    /**
     * @return the voctHandler
     */
    public VoctHandlerCommon getVoctHandler() {
        return voctHandler;
    }

    /**
     * @return the falManager
     */
    public FALManagerCommon getFalManager() {
        return falManager;
    }

    /**
     * @return the falHandler
     */
    public FALHandlerCommon getFalHandler() {
        return falHandler;
    }
    
    public void setRestart(boolean restart) {
        this.restart = restart;
    }

}

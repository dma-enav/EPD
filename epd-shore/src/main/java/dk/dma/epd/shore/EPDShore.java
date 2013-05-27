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
package dk.dma.epd.shore;

import java.beans.beancontext.BeanContextServicesSupport;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.PropertyConsumer;

import dk.dma.ais.reader.AisReader;
import dk.dma.ais.virtualnet.transponder.gui.TransponderFrame;
import dk.dma.commons.app.OneInstanceGuard;
import dk.dma.enav.communication.PersistentConnection;
import dk.dma.enav.communication.PersistentConnection.State;
import dk.dma.epd.common.ExceptionHandler;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.prototype.sensor.gps.GpsHandler;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaFileSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSerialSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaStdinSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaTcpSensor;
import dk.dma.epd.common.prototype.sensor.nmea.SensorType;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.common.util.VersionInfo;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.utils.StaticImages;
import dk.dma.epd.shore.gui.views.MainFrame;
import dk.dma.epd.shore.route.RouteManager;
import dk.dma.epd.shore.service.EnavServiceHandler;
import dk.dma.epd.shore.service.MonaLisaHandler;
import dk.dma.epd.shore.service.MonaLisaRouteOptimization;
import dk.dma.epd.shore.service.ais.AisServices;
import dk.dma.epd.shore.services.shore.ShoreServices;
import dk.dma.epd.shore.settings.ESDSensorSettings;
import dk.dma.epd.shore.settings.ESDSettings;
import dk.dma.epd.shore.voyage.VoyageManager;

/**
 * Main class with main method.
 * 
 * Starts up components, bean context and GUI.
 * 
 */
public class EPDShore extends EPD {

    private static Logger LOG;
    private static MainFrame mainFrame;
    private static BeanContextServicesSupport beanHandler;
    private static ESDSettings settings;
    static Properties properties = new Properties();
    private static NmeaSensor aisSensor;
    private static NmeaSensor gpsSensor;
    private static AisHandler aisHandler;
    private static GpsHandler gpsHandler;
    private static MsiHandler msiHandler;
    private static MonaLisaHandler monaLisaHandler;
    private static AisServices aisServices;
    private static AisReader aisReader;
    private static ShoreServicesCommon shoreServicesCommon;
    private static StaticImages staticImages;
    private static TransponderFrame transponderFrame;
    private static MonaLisaRouteOptimization monaLisaRouteExchange;
    
    
    private static RouteManager routeManager;
    private static VoyageManager voyageManager;
    private static EnavServiceHandler enavServiceHandler;

    /**
     * Starts the program by initializing the various threads and spawning the main GUI
     * 
     * @param args
     */
    public static void main(String[] args) throws IOException {
        
        home = Paths.get(System.getProperty("user.home"), ".epd-shore");

        new Bootstrap().run();

        // Set up log4j logging
        LOG = LoggerFactory.getLogger(EPDShore.class);

        // Set default exception handler
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        LOG.info("Starting eNavigation Prototype Display Shore - version " + VersionInfo.getVersion());
        LOG.info("Copyright (C) 2012 Danish Maritime Authority");
        LOG.info("This program comes with ABSOLUTELY NO WARRANTY.");
        LOG.info("This is free software, and you are welcome to redistribute it under certain conditions.");
        LOG.info("For details see LICENSE file.");

        // Create the bean context (map handler)
        // mapHandler = new MapHandler();
        beanHandler = new BeanContextServicesSupport();

        // Load settings or get defaults and add to bean context
        if (args.length > 0) {
            settings = new ESDSettings(args[0]);
        } else {
            settings = new ESDSettings();
        }
        LOG.info("Using settings file: " + settings.getSettingsFile());
        settings.loadFromFile();
        beanHandler.add(settings);

        // Determine if instance already running and if that is allowed

        OneInstanceGuard guard = new OneInstanceGuard(home.resolve("esd.lock").toString());
        if (guard.isAlreadyRunning()) {
            JOptionPane.showMessageDialog(null, "One application instance already running. Stop instance or restart computer.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Start sensors
        startSensors();

        // Enable GPS timer by adding it to bean context
        GnssTime.init();
        beanHandler.add(GnssTime.getInstance());

        // Start position handler and add to bean context
        gpsHandler = new GpsHandler();
        beanHandler.add(gpsHandler);

        // aisHandler = new AisHandlerCommon();
        aisHandler = new AisHandler(settings.getAisSettings());
        aisHandler.loadView();
        EPD.startThread(aisHandler, "AisHandler");
        beanHandler.add(aisHandler);

        // Add StaticImages handler
        staticImages = new StaticImages();
        beanHandler.add(staticImages);

        // Load routeManager
        routeManager = RouteManager.loadRouteManager();
        beanHandler.add(routeManager);

        // To be changed to load similar to routeManager
        // voyageManager = new VoyageManager();
        voyageManager = VoyageManager.loadVoyageManager();
        beanHandler.add(voyageManager);


        
        // Create AIS services
        aisServices = new AisServices();
        beanHandler.add(aisServices);
        
        // Create shore services
        shoreServicesCommon = new ShoreServices(getSettings().getEnavSettings());
        beanHandler.add(shoreServicesCommon);

        // Create mona lisa route exchange
        monaLisaRouteExchange = new MonaLisaRouteOptimization();
        beanHandler.add(monaLisaRouteExchange);
        
        // Create EnavServiceHandler
        enavServiceHandler = new EnavServiceHandler(getSettings().getEnavSettings());
        beanHandler.add(enavServiceHandler);
        enavServiceHandler.start();

        // Create Mona Lisa Handler;
        monaLisaHandler = new MonaLisaHandler();
        beanHandler.add(monaLisaHandler);

        // Create MSI handler
        msiHandler = new MsiHandler(getSettings().getEnavSettings());
        beanHandler.add(msiHandler);

        createPluginComponents();

        final CountDownLatch guiCreated = new CountDownLatch(1);

        // Create and show GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
                guiCreated.countDown();
            }
        });

        // Wait for gui to be created
        try {
            guiCreated.await();
        } catch (InterruptedException e) {
            LOG.error("Interrupted while waiting for GUI to be created", e);
        }

        // Create embedded transponder frame
        transponderFrame = new TransponderFrame(home.resolve("transponder.xml").toString(), true, mainFrame);
        mainFrame.getTopMenu().setTransponderFrame(transponderFrame);
        beanHandler.add(transponderFrame);

        if (settings.getSensorSettings().isStartTransponder()) {
            transponderFrame.startTransponder();
        }

    }

    public static EnavServiceHandler getEnavServiceHandler() {
        return enavServiceHandler;
    }

    /**
     * Function called on shutdown
     */
    public static void closeApp() {
        closeApp(false);
    }

    public static MonaLisaHandler getMonaLisaHandler() {
        return monaLisaHandler;
    }

    /**
     * Close app routine with possibility for restart - not implemented
     * 
     * @param restart
     *            - boolean value for program restart
     */
    public static void closeApp(boolean restart) {
        // Shutdown routine

        // Chart panels

        // Window state

        // Window state has a
        // Name, Size, Location, Locked status, on top status
        // Chart panel has a zoom level, position

        // Main application

        mainFrame.saveSettings();
        settings.saveToFile();

        PersistentConnection connection = enavServiceHandler.getConnection();

        if (connection != null) {
            connection.close();
        }

        // GuiSettings
        // Handler settings
        voyageManager.saveToFile();
        routeManager.saveToFile();
        msiHandler.saveToFile();
        aisHandler.saveView();

        if (connection != null) {
            try {
                enavServiceHandler.getConnection().awaitState(State.TERMINATED, 2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOG.info("Failed to close connection - Terminatnig");
            }
        }

        transponderFrame.shutdown();

        LOG.info("Closing ESD");
        System.exit(restart ? 2 : 0);
    }

    /**
     * Creates and shows the GUI
     */
    private static void createAndShowGUI() {
        // Set the look and feel.
        initLookAndFeel();

        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the main window
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);

    }

    /**
     * Create the plugin components and initialize the beanhandler
     */
    private static void createPluginComponents() {
        Properties props = getProperties();
        String componentsValue = props.getProperty("esd.plugin_components");
        if (componentsValue == null) {
            return;
        }
        String[] componentNames = componentsValue.split(" ");
        for (String compName : componentNames) {
            String classProperty = compName + ".class";
            String className = props.getProperty(classProperty);
            if (className == null) {
                LOG.error("Failed to locate property " + classProperty);
                continue;
            }
            // Create it if you do...
            try {
                Object obj = java.beans.Beans.instantiate(null, className);
                if (obj instanceof PropertyConsumer) {
                    PropertyConsumer propCons = (PropertyConsumer) obj;
                    propCons.setProperties(compName, props);
                }
                beanHandler.add(obj);
            } catch (IOException e) {
                LOG.error("IO Exception instantiating class \"" + className + "\"");
            } catch (ClassNotFoundException e) {
                LOG.error("Component class not found: \"" + className + "\"");
            }
        }
    }

    /**
     * Function used to measure time
     * 
     * @param start
     *            - Startime
     * @return - Elapsed time
     */
    public static double elapsed(long start) {
        double elapsed = System.nanoTime() - start;
        return elapsed / 1000000.0;
    }

    /**
     * Return the AisHandlerCommon
     * 
     * @return - aisHandler
     */
    public static AisHandler getAisHandler() {
        return aisHandler;
    }

    /**
     * BeanHandler for program structure
     * 
     * @return - beanHandler
     */
    public static BeanContextServicesSupport getBeanHandler() {
        return beanHandler;
    }

    // /**
    // * Return the GpsHandler
    // * @return - GpsHandler
    // */
    // public static GpsHandler getGpsHandler() {
    // return gpsHandler;
    // }

    /**
     * Return the mainFrame gui element
     * 
     * @return - mainframe gui
     */
    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static RouteManager getRouteManager() {
        return routeManager;
    }

    /**
     * Return the msiHandker
     * 
     * @return - MsiHandler
     */
    public static MsiHandler getMsiHandler() {
        return msiHandler;
    }

    /**
     * Returns the properties
     * 
     * @return - properties
     */
    public static Properties getProperties() {
        return properties;
    }

    /**
     * Return the settings
     * 
     * @return - settings
     */
    public static ESDSettings getSettings() {
        return settings;
    }

    /**
     * Return the shoreService used in shore connections like MSI
     * 
     * @return - shoreServicesCommon
     */
    public static ShoreServicesCommon getShoreServices() {
        return shoreServicesCommon;
    }

    /**
     * Set the used theme using lookAndFeel
     */
    private static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOG.error("Failed to set look and feed: " + e.getMessage());
        }

        // Uncomment for fancy look and feel
        /**
         * try { for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) { if ("Nimbus".equals(info.getName())) {
         * UIManager.setLookAndFeel(info.getClassName()); break; } } } catch (Exception e) { // If Nimbus is not available, you can
         * set the GUI to another look and feel. }
         **/

    }

    /**
     * Load the properties file
     */
    static void loadProperties() {
        InputStream in = EPDShore.class.getResourceAsStream("/epd-shore.properties");
        try {
            if (in == null) {
                throw new IOException("Properties file not found");
            }
            properties.load(in);
            in.close();
        } catch (IOException e) {
            LOG.error("Failed to load resources: " + e.getMessage());
        }
    }

    private static void startSensors() {
        ESDSensorSettings sensorSettings = settings.getSensorSettings();
        switch (sensorSettings.getAisConnectionType()) {
        case NONE:
            aisSensor = new NmeaStdinSensor();
            break;
        case TCP:
            aisSensor = new NmeaTcpSensor(sensorSettings.getAisHostOrSerialPort(), sensorSettings.getAisTcpPort());
            break;
        case SERIAL:
            aisSensor = new NmeaSerialSensor(sensorSettings.getAisHostOrSerialPort());
            break;
        case FILE:
            aisSensor = new NmeaFileSensor(sensorSettings.getAisFilename(), sensorSettings);
            break;
        default:
            LOG.error("Unknown sensor connection type: " + sensorSettings.getAisConnectionType());
        }

        if (aisSensor != null) {
            aisSensor.addSensorType(SensorType.AIS);
        }

        switch (sensorSettings.getGpsConnectionType()) {
        case NONE:
            gpsSensor = new NmeaStdinSensor();
            break;
        case TCP:
            gpsSensor = new NmeaTcpSensor(sensorSettings.getGpsHostOrSerialPort(), sensorSettings.getGpsTcpPort());
            break;
        case SERIAL:
            gpsSensor = new NmeaSerialSensor(sensorSettings.getGpsHostOrSerialPort());
            break;
        case FILE:
            gpsSensor = new NmeaFileSensor(sensorSettings.getGpsFilename(), sensorSettings);
            break;
        case AIS_SHARED:
            gpsSensor = aisSensor;
            break;
        default:
            LOG.error("Unknown sensor connection type: " + sensorSettings.getAisConnectionType());
        }

        if (gpsSensor != null) {
            gpsSensor.addSensorType(SensorType.GPS);
        }

        if (aisSensor != null) {
            aisSensor.start();
            // Add ais sensor to bean context
            beanHandler.add(aisSensor);
        }
        if (gpsSensor != null && gpsSensor != aisSensor) {
            gpsSensor.start();
            // Add gps sensor to bean context
            beanHandler.add(gpsSensor);
        }

    }

    public static GpsHandler getGpsHandler() {
        return gpsHandler;
    }

    public static StaticImages getStaticImages() {
        return staticImages;
    }

    /**
     * Function used to call sleep on a thread
     * 
     * @param ms
     *            - time in ms of how long to sleep
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

    public static AisReader getAisReader() {
        return aisReader;
    }

    public static AisServices getAisServices() {
        return aisServices;
    }

    public static VoyageManager getVoyageManager() {
        return voyageManager;
    }

    /**
     * @return the monaLisaRouteExchange
     */
    public static MonaLisaRouteOptimization getMonaLisaRouteExchange() {
        return monaLisaRouteExchange;
    }
    
    

}

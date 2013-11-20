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
package dk.dma.epd.ship;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandler;
import com.bbn.openmap.PropertyConsumer;
import com.jtattoo.plaf.hifi.HiFiLookAndFeel;

import dk.dma.ais.virtualnet.transponder.gui.TransponderFrame;
import dk.dma.commons.app.OneInstanceGuard;
import dk.dma.enav.communication.PersistentConnection;
import dk.dma.enav.communication.PersistentConnection.State;
import dk.dma.epd.common.ExceptionHandler;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.voyage.VoyageEventDispatcher;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaFileSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSerialSensorFactory;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaTcpSensor;
import dk.dma.epd.common.prototype.sensor.pnt.MultiSourcePntHandler;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.settings.SensorSettings.PntSource;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.common.util.VersionInfo;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.gui.route.RouteManagerDialog;
import dk.dma.epd.ship.monalisa.MonaLisaRouteOptimization;
import dk.dma.epd.ship.nogo.DynamicNogoHandler;
import dk.dma.epd.ship.nogo.NogoHandler;
import dk.dma.epd.ship.risk.RiskHandler;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.route.strategic.StrategicRouteExchangeHandler;
import dk.dma.epd.ship.service.EnavServiceHandler;
import dk.dma.epd.ship.service.communication.ais.AisServices;
import dk.dma.epd.ship.service.voct.VOCTManager;
import dk.dma.epd.ship.settings.EPDSensorSettings;
import dk.dma.epd.ship.settings.EPDSettings;

/**
 * Main class with main method.
 * 
 * Starts up components, bean context and GUI.
 * 
 */
public class EPDShip extends EPD {

    private static Logger LOG;
    static MainFrame mainFrame;
    private static MapHandler mapHandler;
    private static EPDSettings settings;
    static Properties properties = new Properties();
    private static NmeaSensor aisSensor;
    private static NmeaSensor gpsSensor;
    private static NmeaSensor msPntSensor;
    private static PntHandler pntHandler;
    private static MultiSourcePntHandler msPntHandler;
    private static AisHandler aisHandler;
    private static RiskHandler riskHandler;
    private static RouteManager routeManager;
    private static ShoreServicesCommon shoreServices;
    private static StrategicRouteExchangeHandler strategicRouteExchangeHandler;
    private static MonaLisaRouteOptimization monaLisaRouteExchange;
    private static AisServices aisServices;
    private static MsiHandler msiHandler;
    private static NogoHandler nogoHandler;
    private static EnavServiceHandler enavServiceHandler;
    private static DynamicNogoHandler dynamicNoGoHandler;
    private static TransponderFrame transponderFrame;

    private static VOCTManager voctManager;


    private static VoyageEventDispatcher voyageEventDispatcher;


    public static void main(String[] args) throws IOException {

        home = Paths.get(System.getProperty("user.home"), ".epd-ship");

        new Bootstrap().run();

        // Set up log4j logging
        LOG = LoggerFactory.getLogger(EPDShip.class);

        // Set default exception handler
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        // Determine version
        LOG.info("Starting eNavigation Prototype Display Ship - version " + VersionInfo.getVersionAndBuild());
        LOG.info("Copyright (C) 2011 Danish Maritime Authority");
        LOG.info("This program comes with ABSOLUTELY NO WARRANTY.");
        LOG.info("This is free software, and you are welcome to redistribute it under certain conditions.");
        LOG.info("For details see LICENSE file.");

        // Create the bean context (map handler)
        mapHandler = new MapHandler();

        // Load settings or get defaults and add to bean context
        if (args.length > 0) {
            settings = new EPDSettings(args[0]);
        } else {

            settings = new EPDSettings(home.resolve("settings.properties").toString());
        }
        LOG.info("Using settings file: " + settings.getSettingsFile());
        settings.loadFromFile();
        mapHandler.add(settings);

        // Determine if instance already running and if that is allowed
        OneInstanceGuard guard = new OneInstanceGuard(home.resolve("eeins.lock").toString());
        if (!settings.getGuiSettings().isMultipleInstancesAllowed() && guard.isAlreadyRunning()) {
            JOptionPane.showMessageDialog(null, "One application instance already running. Stop instance or restart computer.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // start riskHandler
        startRiskHandler();

        // Enable PNT timer by adding it to bean context
        PntTime.init();
        mapHandler.add(PntTime.getInstance());

        // Start position handler and add to bean context
        pntHandler = new PntHandler();
        mapHandler.add(pntHandler);

        // Start AIS target monitoring
        aisHandler = new AisHandler(settings.getSensorSettings(), settings.getAisSettings());
        aisHandler.loadView();
        EPD.startThread(aisHandler, "AisHandler");
        mapHandler.add(aisHandler);

        // Load routeManager and register as GPS data listener
        routeManager = RouteManager.loadRouteManager();
        mapHandler.add(routeManager);

        
        voctManager = VOCTManager.loadVOCTManager();
        mapHandler.add(voctManager);
        
        // Create shore services
        shoreServices = new ShoreServicesCommon(getSettings().getEnavSettings());
        mapHandler.add(shoreServices);

        // Create mona lisa route exchange
        monaLisaRouteExchange = new MonaLisaRouteOptimization();
        mapHandler.add(monaLisaRouteExchange);

        // Create AIS services
        aisServices = new AisServices();
        mapHandler.add(aisServices);

        // Create MSI handler
        msiHandler = new MsiHandler(getSettings().getEnavSettings());
        mapHandler.add(msiHandler);

        // Create NoGo handler
        nogoHandler = new NogoHandler(getSettings().getEnavSettings());
        mapHandler.add(nogoHandler);

        // Create dynamic NoGo handler
        // Create NoGo handler
        dynamicNoGoHandler = new DynamicNogoHandler(getSettings().getEnavSettings());
        mapHandler.add(dynamicNoGoHandler);

        // Create EnavServiceHandler
        enavServiceHandler = new EnavServiceHandler(getSettings().getEnavSettings());
        mapHandler.add(enavServiceHandler);
        enavServiceHandler.start();

        strategicRouteExchangeHandler = new StrategicRouteExchangeHandler();
        mapHandler.add(strategicRouteExchangeHandler);
        // // Create cloud handler
        // enavCloudHandler = new EnavCloudHandler(settings.getEnavSettings());
        // mapHandler.add(enavCloudHandler);

        // Create voyage event dispatcher
        voyageEventDispatcher = new VoyageEventDispatcher();

        // Start sensors
        startSensors();

        // Create plugin components
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
        mapHandler.add(transponderFrame);

        if (settings.getSensorSettings().isStartTransponder()) {
            transponderFrame.startTransponder();
        }

    }

    private static void startSensors() {
        EPDSensorSettings sensorSettings = settings.getSensorSettings();
        switch (sensorSettings.getAisConnectionType()) {
        case NONE:
            aisSensor = null;
            break;
        case TCP:
            aisSensor = new NmeaTcpSensor(sensorSettings.getAisHostOrSerialPort(), sensorSettings.getAisTcpPort());
            break;
        case SERIAL:
            // aisSensor = new NmeaSerialSensor(sensorSettings.getAisHostOrSerialPort());
            aisSensor = NmeaSerialSensorFactory.create(sensorSettings.getAisHostOrSerialPort());
            break;
        case FILE:
            aisSensor = new NmeaFileSensor(sensorSettings.getAisFilename(), sensorSettings);
            break;
        default:
            LOG.error("Unknown sensor connection type: " + sensorSettings.getAisConnectionType());
        }

        switch (sensorSettings.getGpsConnectionType()) {
        case NONE:
            gpsSensor = null;
            break;
        case TCP:
            gpsSensor = new NmeaTcpSensor(sensorSettings.getGpsHostOrSerialPort(), sensorSettings.getGpsTcpPort());
            break;
        case SERIAL:
            gpsSensor = NmeaSerialSensorFactory.create(sensorSettings.getGpsHostOrSerialPort());
            break;
        case FILE:
            gpsSensor = new NmeaFileSensor(sensorSettings.getGpsFilename(), sensorSettings);
            break;
        default:
            LOG.error("Unknown sensor connection type: " + sensorSettings.getGpsConnectionType());
        }

        switch (sensorSettings.getMsPntConnectionType()) {
        case NONE:
            msPntSensor = null;
            break;
        case TCP:
            msPntSensor = new NmeaTcpSensor(sensorSettings.getMsPntHostOrSerialPort(), sensorSettings.getMsPntTcpPort());
            break;
        case SERIAL:
            msPntSensor = NmeaSerialSensorFactory.create(sensorSettings.getMsPntHostOrSerialPort());
            break;
        case FILE:
            msPntSensor = new NmeaFileSensor(sensorSettings.getMsPntFilename(), sensorSettings);
            break;
        default:
            LOG.error("Unknown sensor connection type: " + sensorSettings.getMsPntConnectionType());
        }

        if (aisSensor != null) {
            aisSensor.addAisListener(aisHandler);
            aisSensor.start();
            mapHandler.add(aisSensor);
        }
        if (gpsSensor != null) {
            gpsSensor.start();
            mapHandler.add(gpsSensor);
        }
        if (msPntSensor != null) {
            msPntSensor.start();
            mapHandler.add(msPntSensor);
        }

        // Hook pnt handler to sensor
        PntSource pntSource = sensorSettings.getPntSource();
        if (pntSource == PntSource.AUTO) {
            if (msPntSensor != null) {
                pntSource = PntSource.MSPNT;
            } else if (gpsSensor != null) {
                pntSource = PntSource.GPS;
            } else if (aisSensor != null) {
                pntSource = PntSource.AIS;
            }
        }

        switch (pntSource) {
        case AIS:
            aisSensor.addPntListener(pntHandler);
            aisSensor.addPntTimeListener(PntTime.getInstance());
            break;
        case GPS:
            gpsSensor.addPntListener(pntHandler);
            gpsSensor.addPntTimeListener(PntTime.getInstance());
            break;
        case MSPNT:
            msPntHandler = new MultiSourcePntHandler(pntHandler);
            msPntSensor.addMsPntListener(msPntHandler);
            break;
        default:
            break;
        }

    }

    public static void startRiskHandler() {
        riskHandler = new RiskHandler();
    }

    static void loadProperties() {
        InputStream in = EPDShip.class.getResourceAsStream("/epd-ship.properties");
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

    static void createAndShowGUI() {
        // Set the look and feel.
        initLookAndFeel();

        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(false);

        // Create and set up the main window
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);

        // Create keybinding shortcuts
        makeKeyBindings();

    }

    private static void makeKeyBindings() {
        JPanel content = (JPanel) mainFrame.getContentPane();
        InputMap inputMap = content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        @SuppressWarnings("serial")
        Action zoomIn = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().doZoom(0.5f);
            }
        };

        @SuppressWarnings("serial")
        Action zoomOut = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().doZoom(2f);
            }
        };

        @SuppressWarnings("serial")
        Action centreOnShip = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().centreOnShip();
            }
        };

        @SuppressWarnings("serial")
        Action newRoute = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // newRouteBtn.requestFocusInWindow();
                mainFrame.getTopPanel().activateNewRouteButton();
            }
        };

        @SuppressWarnings("serial")
        Action routes = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                RouteManagerDialog routeManagerDialog = new RouteManagerDialog(mainFrame);
                routeManagerDialog.setVisible(true);
            }
        };

        @SuppressWarnings("serial")
        Action msi = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getTopPanel().getMsiDialog().setVisible(true);
            }
        };

        @SuppressWarnings("serial")
        Action ais = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getTopPanel().getAisDialog().setVisible(true);
            }
        };

        @SuppressWarnings("serial")
        Action panUp = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().pan(1);
            }
        };
        @SuppressWarnings("serial")
        Action panDown = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().pan(2);
            }
        };

        @SuppressWarnings("serial")
        Action panLeft = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().pan(3);
            }
        };
        @SuppressWarnings("serial")
        Action panRight = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().pan(4);
            }
        };

        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, 0), "ZoomIn");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SUBTRACT, 0), "ZoomOut");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, 0), "centre");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, 0), "panUp");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, 0), "panDown");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, 0), "panLeft");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, 0), "panRight");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_UP, 0), "panUp");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_DOWN, 0), "panDown");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_LEFT, 0), "panLeft");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_RIGHT, 0), "panRight");
        inputMap.put(KeyStroke.getKeyStroke("control N"), "newRoute");
        inputMap.put(KeyStroke.getKeyStroke("control R"), "routes");
        inputMap.put(KeyStroke.getKeyStroke("control M"), "msi");
        inputMap.put(KeyStroke.getKeyStroke("control A"), "ais");

        content.getActionMap().put("ZoomOut", zoomOut);
        content.getActionMap().put("ZoomIn", zoomIn);
        content.getActionMap().put("centre", centreOnShip);
        content.getActionMap().put("newRoute", newRoute);
        content.getActionMap().put("routes", routes);
        content.getActionMap().put("msi", msi);
        content.getActionMap().put("ais", ais);
        content.getActionMap().put("panUp", panUp);
        content.getActionMap().put("panDown", panDown);
        content.getActionMap().put("panLeft", panLeft);
        content.getActionMap().put("panRight", panRight);

    }

    private static void initLookAndFeel() {
        try {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            Properties props = new Properties();
            props.put("logoString", "EPD-Ship");
            props.put("backgroundPattern", "false");
            props.put("textAntiAliasingMode", "TEXT_ANTIALIAS_VBGR");
            // props.put("menuOpaque", "true");
            // props.put("tooltipCastShadow", "true");

            // small font
            props.setProperty("controlTextFont", "Dialog 10");
            props.setProperty("systemTextFont", "Dialog 10");
            props.setProperty("userTextFont", "Dialog 10");
            props.setProperty("menuTextFont", "Dialog 10");
            props.setProperty("windowTitleFont", "Dialog bold 10");
            props.setProperty("subTextFont", "Dialog 8");

            // props.put("tooltipBorderSize", "15");
            // props.put("tooltipShadowSize", "15");

            // NoireLookAndFeel laf = new NoireLookAndFeel();
            HiFiLookAndFeel laf = new HiFiLookAndFeel();
            // NoireLookAndFeel.setCurrentTheme(props);
            HiFiLookAndFeel.setCurrentTheme(props);

            UIManager.setLookAndFeel(laf);

        } catch (Exception e) {
            LOG.error("Failed to set look and feed: " + e.getMessage());
        }

    }

    public static void closeApp() {
        closeApp(false);
    }

    public static void closeApp(boolean restart) {
        // Shutdown routine

        PersistentConnection connection = enavServiceHandler.getConnection();

        if (connection != null) {
            connection.close();
        }

        mainFrame.saveSettings();
        settings.saveToFile();
        routeManager.saveToFile();
        msiHandler.saveToFile();
        aisHandler.saveView();
        transponderFrame.shutdown();

        if (connection != null) {
            try {
                enavServiceHandler.getConnection().awaitState(State.TERMINATED, 2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOG.info("Failed to close connection - Terminatnig");
            }
        }

        LOG.info("Closing EPD-ship");
        System.exit(restart ? 2 : 0);

    }

    private static void createPluginComponents() {
        Properties props = getProperties();
        String componentsValue = props.getProperty("eeins.plugin_components");
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
                mapHandler.add(obj);
            } catch (IOException e) {
                LOG.error("IO Exception instantiating class \"" + className + "\"");
            } catch (ClassNotFoundException e) {
                LOG.error("Component class not found: \"" + className + "\"");
            }
        }
    }

    public static Properties getProperties() {
        return properties;
    }

    public static EPDSettings getSettings() {
        return settings;
    }

    public static NmeaSensor getAisSensor() {
        return aisSensor;
    }

    public static NmeaSensor getGpsSensor() {
        return gpsSensor;
    }

    public static PntHandler getPntHandler() {
        return pntHandler;
    }

    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static AisHandler getAisHandler() {
        return aisHandler;
    }

    public static RouteManager getRouteManager() {
        return routeManager;
    }

    public static MapHandler getMapHandler() {
        return mapHandler;
    }

    public static ShoreServicesCommon getShoreServices() {
        return shoreServices;
    }

    public static MonaLisaRouteOptimization getMonaLisaRouteExchange() {
        return monaLisaRouteExchange;
    }

    public static EnavServiceHandler getEnavServiceHandler() {
        return enavServiceHandler;
    }

    public static double elapsed(long start) {
        double elapsed = System.nanoTime() - start;
        return elapsed / 1000000.0;
    }

    public static RiskHandler getRiskHandler() {
        return riskHandler;
    }

    /**
     * @return the monaLisaHandler
     */
    public static StrategicRouteExchangeHandler getStrategicRouteExchangeHandler() {
        return strategicRouteExchangeHandler;
    }

    /**
     * Get the system wide voyage event dispatcher.
     * 
     * @return the voyageEventDispatcher
     */
    public static VoyageEventDispatcher getVoyageEventDispatcher() {
        return voyageEventDispatcher;
    }

    @Override
    public Path getSettingsPath() {
        return Paths.get(System.getProperty("user.home"), ".epd-ship");
    }

}

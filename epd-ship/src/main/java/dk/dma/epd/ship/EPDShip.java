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
package dk.dma.epd.ship;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandler;
import com.bbn.openmap.PropertyConsumer;
import com.jtattoo.plaf.hifi.HiFiLookAndFeel;

import dk.dma.ais.virtualnet.transponder.gui.TransponderFrame;
import dk.dma.commons.app.OneInstanceGuard;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.ExceptionHandler;
import dk.dma.epd.common.graphics.Resources;
import dk.dma.epd.common.prototype.Bootstrap;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.SystemTrayCommon;
import dk.dma.epd.common.prototype.model.identity.IdentityHandler;
import dk.dma.epd.common.prototype.model.voyage.VoyageEventDispatcher;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaFileSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSerialSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSerialSensorFactory;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaTcpSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaUdpSensor;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.sensor.rpnt.MultiSourcePntHandler;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon;
import dk.dma.epd.common.prototype.service.MaritimeCloudService;
import dk.dma.epd.common.prototype.settings.SensorSettings;
import dk.dma.epd.common.prototype.settings.SensorSettings.PntSourceSetting;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.common.util.VersionInfo;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.fal.FALManager;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.gui.notification.NotificationCenter;
import dk.dma.epd.ship.gui.route.RouteManagerDialog;
import dk.dma.epd.ship.monalisa.MonaLisaRouteOptimization;
import dk.dma.epd.ship.nogo.NogoHandler;
import dk.dma.epd.ship.ownship.IOwnShipListener;
import dk.dma.epd.ship.ownship.OwnShipHandler;
import dk.dma.epd.ship.predictor.DynamicPredictor;
import dk.dma.epd.ship.predictor.DynamicPredictorHandler;
import dk.dma.epd.ship.predictor.DynamicPredictorSentenceParser;
import dk.dma.epd.ship.risk.RiskHandler;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.service.IntendedRouteHandler;
import dk.dma.epd.ship.service.RouteSuggestionHandler;
import dk.dma.epd.ship.service.StrategicRouteHandler;
import dk.dma.epd.ship.service.VoctHandler;
import dk.dma.epd.ship.service.shore.ShoreServices;
import dk.dma.epd.ship.service.voct.VOCTManager;
import dk.dma.epd.ship.settings.EPDSensorSettings;
import dk.dma.epd.ship.settings.EPDSettings;

/**
 * Main class with main method.
 * 
 * Starts up components, bean context and GUI.
 * 
 */
public final class EPDShip extends EPD implements IOwnShipListener {

    private static Logger LOG;

    MainFrame mainFrame;
    private MapHandler mapHandler;
    private NmeaSensor aisSensor;
    private NmeaSensor gpsSensor;
    private NmeaSensor msPntSensor;
    private NmeaSensor dynamicPredictorSensor;
    private PntHandler pntHandler;
    private MultiSourcePntHandler msPntHandler;
    private OwnShipHandler ownShipHandler;
    private DynamicPredictorHandler dynamicPredictorHandler;
    private RiskHandler riskHandler;
    private ShoreServicesCommon shoreServices;
    private MonaLisaRouteOptimization monaLisaRouteExchange;
    private NogoHandler nogoHandler;
    private TransponderFrame transponderFrame;
    private VoyageEventDispatcher voyageEventDispatcher;
    private VOCTManager voctManager;
    private DynamicPredictor dynamicPredictor;
    private DynamicPredictorSentenceParser dynamicPredictorParser;

    // Maritime Cloud services
    private IntendedRouteHandler intendedRouteHandler;

    // private VoctHandler voctHandler;

    /**
     * Starts the program by initializing the various threads and spawning the main GUI
     * 
     * @param args
     */

    public static void main(String[] args) throws IOException {
        // Check if the home path has been specified via the command line
        String homePath = (args.length > 0) ? args[0] : null;
        new EPDShip(homePath);
    }

    /**
     * Constructor
     * 
     * @param path
     *            the home path to use
     */
    private EPDShip(String path) throws IOException {
        super();

        if (!StringUtils.isEmpty(path)) {
            homePath = Paths.get(path);
        } else {
            homePath = determineHomePath(Paths.get(System.getProperty("user.home"), ".epd-ship"));
        }

        new Bootstrap().run(this, new String[] { "epd-ship.properties", "enc_navicon.properties", "settings.properties",
                "transponder.xml" }, new String[] { "routes", "layout/static", "shape/GSHHS_shp", "identities" });

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
        settings = new EPDSettings();
        LOG.info("Using settings file: " + getSettings().getSettingsFile());
        settings.loadFromFile();
        mapHandler.add(settings);

        // Determine if instance already running and if that is allowed
        OneInstanceGuard guard = new OneInstanceGuard(getHomePath().resolve("epd.lock").toString());
        if (guard.isAlreadyRunning()) {
            handleEpdAlreadyRunning();
        }

        // start riskHandler
        startRiskHandler();

        // Enable PNT timer by adding it to bean context
        PntTime.init();
        mapHandler.add(PntTime.getInstance());

        // Start position handler and add to bean context
        pntHandler = new PntHandler();
        mapHandler.add(pntHandler);

        // Start the multi-source PNT handler and add to bean context
        msPntHandler = new MultiSourcePntHandler();
        msPntHandler.addPntListener(pntHandler);
        mapHandler.add(msPntHandler);

        // Start AIS target monitoring
        aisHandler = new AisHandler(settings.getSensorSettings(), settings.getAisSettings());
        aisHandler.loadView();
        EPD.startThread(aisHandler, "AisHandler");
        mapHandler.add(aisHandler);

        // Start own-ship handler
        ownShipHandler = new OwnShipHandler(settings.getAisSettings());
        ownShipHandler.loadView();
        ownShipHandler.addListener(this);
        mapHandler.add(ownShipHandler);

        // Start dynamic predictor handler
        dynamicPredictorHandler = new DynamicPredictorHandler();
        mapHandler.add(dynamicPredictorHandler);

        // Start dynamic predictor sensor sentence parser
        dynamicPredictorParser = new DynamicPredictorSentenceParser();
        mapHandler.add(dynamicPredictorParser);

        // Maybe start dynamic prediction generator
        if (settings.getSensorSettings().isStartPredictionGenerator()) {
            dynamicPredictor = new DynamicPredictor();
            mapHandler.add(dynamicPredictor);
        }

        // Load routeManager and register as GPS data listener
        routeManager = RouteManager.loadRouteManager();
        mapHandler.add(routeManager);

        falManager = FALManager.loadFALManager();
        mapHandler.add(falManager);

        voctManager = VOCTManager.loadVOCTManager();
        mapHandler.add(voctManager);

        // Create shore services
        shoreServices = new ShoreServices(getSettings().getEnavSettings());
        mapHandler.add(shoreServices);

        // Create mona lisa route exchange
        monaLisaRouteExchange = new MonaLisaRouteOptimization();
        mapHandler.add(monaLisaRouteExchange);

        // Create MSI handler
        msiHandler = new MsiHandler(getSettings().getEnavSettings());
        mapHandler.add(msiHandler);

        // Create NoGo handler
        nogoHandler = new NogoHandler(getSettings().getEnavSettings());
        mapHandler.add(nogoHandler);

        // Create Maritime Cloud service
        maritimeCloudService = new MaritimeCloudService();
        mapHandler.add(maritimeCloudService);
        maritimeCloudService.start();

        strategicRouteHandler = new StrategicRouteHandler();
        mapHandler.add(strategicRouteHandler);

        // Create intended route handler
        intendedRouteHandler = new IntendedRouteHandler();
        intendedRouteHandler.updateSettings(settings.getEnavSettings());
        mapHandler.add(intendedRouteHandler);

        // Create the route suggestion handler
        // routeSuggestionHandler = new RouteSuggestionHandler();
        routeSuggestionHandler = RouteSuggestionHandler.loadRouteSuggestionHandler();
        mapHandler.add(routeSuggestionHandler);

        // Create a chat service handler
        chatServiceHandler = new ChatServiceHandlerCommon();
        mapHandler.add(chatServiceHandler);

        // Create voyage event dispatcher
        voyageEventDispatcher = new VoyageEventDispatcher();

        // Create identity handler
        identityHandler = new IdentityHandler();
        mapHandler.add(identityHandler);

        // Create VOCT handler
        voctHandler = new VoctHandler();
        mapHandler.add(voctHandler);

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
        transponderFrame = new TransponderFrame(getHomePath().resolve("transponder.xml").toString(), true, mainFrame);
        mapHandler.add(transponderFrame);

        if (settings.getSensorSettings().isStartTransponder()) {
            transponderFrame.startTransponder();
        }

    }

    /**
     * Returns the current {@code EPDShore} instance
     * 
     * @return the current {@code EPDShore} instance
     */
    public static EPDShip getInstance() {
        return (EPDShip) instance;
    }

    /**
     * Returns the settings associated with the EPD system
     * 
     * @return the settings associated with the EPD system
     */
    @Override
    public EPDSettings getSettings() {
        return (EPDSettings) settings;
    }

    /**
     * Returns the default shore mouse mode service list
     * 
     * @return the default shore mouse mode service list
     */
    @Override
    public String[] getDefaultMouseModeServiceList() {
        String[] ret = new String[2];
        ret[0] = NavigationMouseMode.MODE_ID; // "Gestures"
        ret[1] = DragMouseMode.MODE_ID;
        return ret;
    }

    /**
     * Starts the sensors defined in the {@linkplain SensorSettings} and hook up listeners
     */
    @Override
    protected void startSensors() {
        EPDSensorSettings sensorSettings = getSettings().getSensorSettings();
        switch (sensorSettings.getAisConnectionType()) {
        case NONE:
            aisSensor = null;
            break;
        case TCP:
            aisSensor = new NmeaTcpSensor(sensorSettings.getAisHostOrSerialPort(), sensorSettings.getAisTcpOrUdpPort());
            break;
        case UDP:
            aisSensor = new NmeaUdpSensor(sensorSettings.getAisTcpOrUdpPort());
            break;
        case SERIAL:
            // aisSensor = new NmeaSerialSensor(sensorSettings.getAisHostOrSerialPort());
            aisSensor = NmeaSerialSensorFactory.create(sensorSettings.getAisHostOrSerialPort(),
                    sensorSettings.getAisSerialPortBaudRate());
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
            gpsSensor = new NmeaTcpSensor(sensorSettings.getGpsHostOrSerialPort(), sensorSettings.getGpsTcpOrUdpPort());
            break;
        case UDP:
            gpsSensor = new NmeaUdpSensor(sensorSettings.getGpsTcpOrUdpPort());
            break;
        case SERIAL:
            gpsSensor = NmeaSerialSensorFactory.create(sensorSettings.getGpsHostOrSerialPort(),
                    sensorSettings.getGpsSerialPortBaudRate());
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
            msPntSensor = new NmeaTcpSensor(sensorSettings.getMsPntHostOrSerialPort(), sensorSettings.getMsPntTcpOrUdpPort());
            break;
        case UDP:
            msPntSensor = new NmeaUdpSensor(sensorSettings.getMsPntTcpOrUdpPort());
            break;
        case SERIAL:
            msPntSensor = NmeaSerialSensorFactory.create(sensorSettings.getMsPntHostOrSerialPort(),
                    sensorSettings.getMsPntSerialPortBaudRate());
            break;
        case FILE:
            msPntSensor = new NmeaFileSensor(sensorSettings.getMsPntFilename(), sensorSettings);
            break;
        default:
            LOG.error("Unknown sensor connection type: " + sensorSettings.getMsPntConnectionType());
        }
        // Only init sensor if dynamic prediction has been enabled.
        if (getSettings().getNavSettings().isDynamicPrediction()) {
            switch (sensorSettings.getDynamicPredictorConnectionType()) {
            case TCP:
                dynamicPredictorSensor = new NmeaTcpSensor(sensorSettings.getDynamicPredictorHostOrSerialPort(),
                        sensorSettings.getDynamicPredictorTcpOrUdpPort());
                break;
            case UDP:
                dynamicPredictorSensor = new NmeaUdpSensor(sensorSettings.getDynamicPredictorTcpOrUdpPort());
                break;
            case SERIAL:
                dynamicPredictorSensor = new NmeaSerialSensor(sensorSettings.getDynamicPredictorHostOrSerialPort(),
                        sensorSettings.getDynamicPredictorSerialPortBaudRate());
            default:
                dynamicPredictorSensor = null;
                break;
            }
        } else {
            dynamicPredictorSensor = null;
        }
        if (aisSensor != null) {
            aisSensor.addAisListener(aisHandler);
            aisSensor.addAisListener(ownShipHandler);
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
        if (dynamicPredictorSensor != null) {
            dynamicPredictorSensor.addDynamicPredictorListener(dynamicPredictorParser);
            dynamicPredictorSensor.start();
            mapHandler.add(dynamicPredictorSensor);
        }

        // Hook pnt handler to sensor
        PntSourceSetting pntSource = sensorSettings.getPntSource();
        if (pntSource == PntSourceSetting.AUTO) {
            if (msPntSensor != null) {
                pntSource = PntSourceSetting.MSPNT;
            } else if (gpsSensor != null) {
                pntSource = PntSourceSetting.GPS;
            } else if (aisSensor != null) {
                pntSource = PntSourceSetting.AIS;
            }
        }

        if (pntSource == PntSourceSetting.AIS && aisSensor != null) {
            aisSensor.addPntListener(pntHandler);
            aisSensor.addPntListener(PntTime.getInstance());
        } else if (pntSource == PntSourceSetting.GPS && gpsSensor != null) {
            gpsSensor.addPntListener(pntHandler);
            gpsSensor.addPntListener(PntTime.getInstance());
        } else if (pntSource == PntSourceSetting.MSPNT && msPntHandler != null) {
            msPntSensor.addMsPntListener(msPntHandler);
            msPntSensor.addPntListener(msPntHandler);
            msPntSensor.addPntListener(PntTime.getInstance());
        }

    }

    /**
     * Stops all sensors and remove listeners
     */
    @Override
    protected void stopSensors() {
        // Stop AIS sensor
        if (aisSensor != null) {
            mapHandler.remove(aisSensor);
            aisSensor.removeAisListener(aisHandler);
            aisSensor.removeAisListener(ownShipHandler);
            aisSensor.removePntListener(pntHandler);
            aisSensor.removePntListener(PntTime.getInstance());
            stopSensor(aisSensor, 3000L);
            aisSensor = null;
        }

        // Stop GPS sensor
        if (gpsSensor != null) {
            mapHandler.remove(gpsSensor);
            gpsSensor.removePntListener(pntHandler);
            gpsSensor.removePntListener(PntTime.getInstance());
            stopSensor(gpsSensor, 3000L);
            gpsSensor = null;
        }

        // Stop multi-source PNT sensor
        if (msPntSensor != null) {
            mapHandler.remove(msPntSensor);
            msPntSensor.removeMsPntListener(msPntHandler);
            msPntSensor.removePntListener(msPntHandler);
            msPntSensor.removePntListener(PntTime.getInstance());
            stopSensor(msPntSensor, 3000L);
            msPntSensor = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void settingsChanged(Type type) {
        if (type == Type.SENSOR) {
            LOG.warn("Restarting all sensors");
            stopSensors();
            startSensors();

        } else if (type == Type.CLOUD) {
            LOG.warn("Restarting Maritime Cloud connection");
            maritimeCloudService.stop();
            maritimeCloudService.start();

            // Update intended route filter settings.
            LOG.warn("Updating intended route filter settings.");
            this.intendedRouteHandler.updateSettings(this.settings.getEnavSettings());
        }
    }

    public void startRiskHandler() {
        riskHandler = new RiskHandler();
    }

    @Override
    public Properties loadProperties() {
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
        return properties;
    }

    void createAndShowGUI() {
        // Set the look and feel.
        initLookAndFeel();

        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(false);

        // Create and set up the main window
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);

        // Create the system tray
        systemTray = new SystemTrayCommon();
        mapHandler.add(systemTray);

        // Create the notification center
        notificationCenter = new NotificationCenter(getMainFrame());
        mapHandler.add(notificationCenter);

        // Create keybinding shortcuts
        makeKeyBindings();

    }

    private void makeKeyBindings() {
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
                mainFrame.saveCentreOnShip();
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
        content.getActionMap().put("ais", ais);
        content.getActionMap().put("panUp", panUp);
        content.getActionMap().put("panDown", panDown);
        content.getActionMap().put("panLeft", panLeft);
        content.getActionMap().put("panRight", panRight);

    }

    private void initLookAndFeel() {
        try {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            Properties props = new Properties();
            props.put("logoString", "EPD-Ship");
            props.put("backgroundPattern", "false");
            props.put("textAntiAliasingMode", "TEXT_ANTIALIAS_VBGR");
            // props.put("menuOpaque", "true");
            // props.put("tooltipCastShadow", "true");

            // small font
            props.setProperty("controlTextFont", "Dialog 12");
            props.setProperty("systemTextFont", "Dialog 12");
            props.setProperty("userTextFont", "Dialog 12");
            props.setProperty("menuTextFont", "Dialog 12");
            props.setProperty("windowTitleFont", "Dialog bold 12");
            props.setProperty("subTextFont", "Dialog 10");

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

    public void closeApp(boolean restart) {
        // Shutdown routine

        mainFrame.saveSettings();
        settings.saveToFile();
        routeManager.saveToFile();
        msiHandler.saveToFile();
        aisHandler.saveView();
        ownShipHandler.saveView();
        transponderFrame.shutdown();
        falManager.saveToFile();

        // Stop the Maritime Cloud connection
        strategicRouteHandler.shutdown();
        routeSuggestionHandler.shutdown();
        intendedRouteHandler.shutdown();
        chatServiceHandler.shutdown();
        maritimeCloudService.stop();

        // Stop the system tray
        systemTray.shutdown();

        // Stop sensors
        stopSensors();

        LOG.info("Closing EPD-ship");
        this.restart = restart;
        System.exit(restart ? 2 : 0);

    }

    private void createPluginComponents() {
        Properties props = getProperties();
        String componentsValue = props.getProperty("epd.plugin_components");
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

    /**
     * Returns the MMSI of the own-ship, or null if not defined
     * 
     * @return the MMSI of the own-ship
     */
    @Override
    public Long getMmsi() {
        return ownShipHandler != null ? ownShipHandler.getMmsi() : null;
    }

    /**
     * Returns the maritime id of the own-ship, or null if not defined
     * 
     * @return the maritime id of the own-ship
     */
    @Override
    public MaritimeId getMaritimeId() {
        Long mmsi = getMmsi();
        return mmsi != null ? new MmsiId(mmsi.intValue()) : null;
    }

    /**
     * Returns the current position of the ship
     * 
     * @return the current position of the ship
     */
    @Override
    public Position getPosition() {
        return getPntHandler().getCurrentData().getPosition();
    }

    public NmeaSensor getAisSensor() {
        return aisSensor;
    }

    public NmeaSensor getGpsSensor() {
        return gpsSensor;
    }

    public PntHandler getPntHandler() {
        return pntHandler;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    @Override
    public FALManager getFalManager() {
        return (FALManager) falManager;
    }

    /**
     * Returns a reference to the AIS handler
     * 
     * @return a reference to the AIS handler
     */
    @Override
    public AisHandler getAisHandler() {
        return (AisHandler) aisHandler;
    }

    public OwnShipHandler getOwnShipHandler() {
        return ownShipHandler;
    }

    @Override
    public RouteManager getRouteManager() {
        return (RouteManager) routeManager;
    }

    public MapHandler getMapHandler() {
        return mapHandler;
    }

    public ShoreServicesCommon getShoreServices() {
        return shoreServices;
    }

    public MonaLisaRouteOptimization getMonaLisaRouteExchange() {
        return monaLisaRouteExchange;
    }

    public double elapsed(long start) {
        double elapsed = System.nanoTime() - start;
        return elapsed / 1000000.0;
    }

    public RiskHandler getRiskHandler() {
        return riskHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StrategicRouteHandler getStrategicRouteHandler() {
        return (StrategicRouteHandler) strategicRouteHandler;
    }

    /**
     * {@inheritDoc}
     */
    public RouteSuggestionHandler getRouteSuggestionHandler() {
        return (RouteSuggestionHandler) routeSuggestionHandler;
    }

    /**
     * Get the system wide voyage event dispatcher.
     * 
     * @return the voyageEventDispatcher
     */
    public VoyageEventDispatcher getVoyageEventDispatcher() {
        return voyageEventDispatcher;
    }

    /**
     * 
     * @return the voctManager
     */
    public VOCTManager getVoctManager() {
        return voctManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationCenter getNotificationCenter() {
        return (NotificationCenter) super.getNotificationCenter();
    }

    @Override
    public Path getHomePath() {
        return homePath;
    }

    /**
     * Returns a {@linkplain Resource} instance which loads resource from the same class-loader/jar-file as the {@code EPDShip}
     * class.
     * 
     * @return a new {@linkplain Resource} instance
     */
    public static Resources res() {
        return Resources.get(EPDShip.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ownShipUpdated(OwnShipHandler ownShipHandler) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ownShipChanged(VesselTarget oldValue, VesselTarget newValue) {

        // Broadcast a message to remove the intended route
        if (getRouteManager().isRouteActive()) {
            intendedRouteHandler.broadcastIntendedRoute(null, false);
        }

        // Restart maritime cloud
        LOG.warn("Restarting Maritime Cloud connection");
        maritimeCloudService.stop();
        maritimeCloudService.start();

        // Broadcast a message to add the intended route
        if (getRouteManager().isRouteActive()) {
            intendedRouteHandler.broadcastIntendedRoute(getRouteManager().getActiveRoute(), true);
        }
    }
}

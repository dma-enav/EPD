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
package dk.dma.epd.ship.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.bbn.openmap.MapHandler;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.event.HistoryListener;
import dk.dma.epd.common.prototype.gui.IMapFrame;
import dk.dma.epd.common.prototype.gui.MainFrameCommon;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;
import dk.dma.epd.common.util.VersionInfo;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.ais.AisDialog;
import dk.dma.epd.ship.gui.component_panels.ActiveWaypointComponentPanel;
import dk.dma.epd.ship.gui.component_panels.AisComponentPanel;
import dk.dma.epd.ship.gui.component_panels.CursorComponentPanel;
import dk.dma.epd.ship.gui.component_panels.DynamicNoGoComponentPanel;
import dk.dma.epd.ship.gui.component_panels.MultiSourcePntComponentPanel;
import dk.dma.epd.ship.gui.component_panels.NoGoComponentPanel;
import dk.dma.epd.ship.gui.component_panels.OwnShipComponentPanel;
import dk.dma.epd.ship.gui.component_panels.PntComponentPanel;
import dk.dma.epd.ship.gui.component_panels.SARComponentPanel;
import dk.dma.epd.ship.gui.component_panels.STCCCommunicationComponentPanel;
import dk.dma.epd.ship.gui.component_panels.ScaleComponentPanel;
import dk.dma.epd.ship.gui.route.strategic.SendStrategicRouteDialog;
import dk.dma.epd.ship.settings.EPDGuiSettings;

/**
 * The main frame containing map and panels
 */
public class MainFrame extends MainFrameCommon implements IMapFrame {

    private static final long serialVersionUID = 1L;
    
    private static final String TITLE = "EPD-ship " + VersionInfo.getVersion();
    protected static final int SENSOR_PANEL_WIDTH = 190;

    private TopPanel topPanel;
    private ChartPanel chartPanel;

    private BottomPanel bottomPanel;

    private ScaleComponentPanel scalePanel;
    private OwnShipComponentPanel ownShipPanel;
    private PntComponentPanel gpsPanel;
    private CursorComponentPanel cursorPanel;
    private ActiveWaypointComponentPanel activeWaypointPanel;
    private AisComponentPanel aisComponentPanel;
    private DynamicNoGoComponentPanel dynamicNoGoPanel;
    private NoGoComponentPanel nogoPanel;
    private SARComponentPanel sarPanel;
    private MultiSourcePntComponentPanel msPntComponentPanel;
    private STCCCommunicationComponentPanel stccComponentPanel;

    private AisDialog aisDialog;
    private SendStrategicRouteDialog sendStrategicRouteDialog;

    private DockableComponents dockableComponents;

    private MapMenu mapMenu;
    private MenuBar menuBar;

    private BottomPanelStatusDialog bottomStatusDialog;

    /**
     * Constructor
     */
    public MainFrame() {
        super(TITLE);
        initGUI();
    }

    /**
     * Initializes the glass pane of the frame
     */
    @Override
    protected void initGlassPane() {
        glassPanel = (JPanel) getGlassPane();
        glassPanel.setLayout(null);
        glassPanel.setVisible(false);
    }

    /**
     * Returns a reference to the map frame cast as a component
     * 
     * @return a reference to the map frame cast as a component
     */
    @Override
    public Component asComponent() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChartPanelCommon getActiveChartPanel() {
        return getChartPanel();
    }

    /**
     * Initializes the GUI
     */
    private void initGUI() {
        MapHandler mapHandler = EPDShip.getInstance().getMapHandler();
        // Get settings
        EPDGuiSettings guiSettings = EPDShip.getInstance().getSettings().getGuiSettings();

        // Set location and size
        if (guiSettings.isMaximized()) {
            setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
        } else {
            setLocation(guiSettings.getAppLocation());
            setSize(guiSettings.getAppDimensions());
        }

        // Create panels
        Container pane = getContentPane();
        topPanel = new TopPanel();

        // Movable service panels
        scalePanel = new ScaleComponentPanel();
        ownShipPanel = new OwnShipComponentPanel();
        gpsPanel = new PntComponentPanel();
        cursorPanel = new CursorComponentPanel();
        activeWaypointPanel = new ActiveWaypointComponentPanel();
        chartPanel = new ChartPanel(activeWaypointPanel);
        aisComponentPanel = new AisComponentPanel();
        dynamicNoGoPanel = new DynamicNoGoComponentPanel();
        nogoPanel = new NoGoComponentPanel();
        sarPanel = new SARComponentPanel();
        msPntComponentPanel = new MultiSourcePntComponentPanel();
        stccComponentPanel = new STCCCommunicationComponentPanel();

        // Unmovable panels
        bottomPanel = new BottomPanel();

        // Create the dockable layouts
        dockableComponents = new DockableComponents(this);

        dockableComponents.lock();

        // Add panels
        topPanel.setPreferredSize(new Dimension(0, 30));
        pane.add(topPanel, BorderLayout.PAGE_START);

        pane.add(bottomPanel, BorderLayout.PAGE_END);

        // Set up the chart panel with layers etc
        chartPanel.initChart();

        // Add top panel to map handler
        mapHandler.add(topPanel);

        // Add bottom panel to map handler
        mapHandler.add(bottomPanel);

        // Add chart panel to map handler
        mapHandler.add(chartPanel);

        // Add scale panel to bean context
        mapHandler.add(scalePanel);
        mapHandler.add(ownShipPanel);
        mapHandler.add(gpsPanel);
        mapHandler.add(cursorPanel);
        mapHandler.add(activeWaypointPanel);
        mapHandler.add(aisComponentPanel);
        mapHandler.add(dynamicNoGoPanel);
        mapHandler.add(nogoPanel);
        mapHandler.add(stccComponentPanel);

        mapHandler.add(sarPanel);

        mapHandler.add(msPntComponentPanel);

        // Create top menubar
        menuBar = new MenuBar();
        this.setJMenuBar(menuBar);

        // Init glass pane
        initGlassPane();

        // Add self to map map handler
        mapHandler.add(this);

        // Add menubar to map handler
        mapHandler.add(menuBar);

        // Init AIS dialog
        aisDialog = new AisDialog(this);
        mapHandler.add(aisDialog);
        
        bottomStatusDialog = new BottomPanelStatusDialog();

        // Init Send Strategic Route dialog
        sendStrategicRouteDialog = new SendStrategicRouteDialog(this);
        mapHandler.add(sendStrategicRouteDialog);

        // Init the map right click menu
        mapMenu = new MapMenu();
        mapHandler.add(mapMenu);

        // Add a history listener to the chart panel.
        this.chartPanel.setHistoryListener(new HistoryListener(this.chartPanel));
        this.chartPanel.getMap().addProjectionListener(this.chartPanel.getHistoryListener());
        chartPanel.getHistoryListener().setNavigationPanel(topPanel);

        if (EPDShip.getInstance().getSettings().getGuiSettings().isFullscreen()) {
            // Enter fullscreen, but do not save frame bounds
            doFullScreen(false);
        } else {
            doNormal();
        }
    }

    /**
     * Called when the window is closing
     */
    @Override
    public void onWindowClosing() {
        // Close routine
        dockableComponents.saveLayout();

        super.onWindowClosing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings() {
        super.saveSettings();
        
        // Save map settings
        chartPanel.saveSettings();
    }

    /**
     * Save the centering of the ship in history.
     */
    public void saveCentreOnShip() {
        // Save the centering of ship to history.
        getChartPanel().getProjectChangeListener().setShouldSave(true);
        getChartPanel().getProjectChangeListener().saveToHistoryBeforeMoving();

        // Move view to centre on ship.
        getChartPanel().centreOnShip();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SetupDialogShip openSetupDialog() {
        SetupDialogShip setupDialog = new SetupDialogShip(this);
        setupDialog.loadSettings(EPD.getInstance().getSettings());
        setupDialog.setVisible(true);
        return setupDialog;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Action getAboutAction() {
        Action aboutEpdShip = new AbstractAction("About EPD-ship", new ImageIcon(EPD.getInstance().getAppIcon(16))) {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                final ImageIcon icon = new ImageIcon(EPD.getInstance().getAppIcon(45));
                
                final StringBuilder aboutText = new StringBuilder();
                aboutText.append("The E-navigation Prototype Display Ship (EPD-ship) is developed by the Danish Maritime Authority (www.dma.dk).\n");
                aboutText.append("The user manual is available from service.e-navigation.net\n\n");
                aboutText.append("Version   : " + VersionInfo.getVersion() + "\n");
                aboutText.append("Build ID  : " + VersionInfo.getBuildId() + "\n");
                aboutText.append("Build date: " + VersionInfo.getBuildDate() + "\n");
                aboutText.append("Home path: " + EPD.getInstance().getHomePath());
                
                JOptionPane
                .showMessageDialog(
                        MainFrame.this,
                        aboutText.toString(),
                        "About the EPD-ship", JOptionPane.OK_OPTION, icon);
            }
        };
        return aboutEpdShip;
    }

    /*******************************/
    /** Getters and setters       **/
    /*******************************/
    
    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public TopPanel getTopPanel() {
        return topPanel;
    }

    public BottomPanelStatusDialog getBottomPanelStatusDialog() {
        return this.bottomStatusDialog;
    }
    
    public ScaleComponentPanel getScalePanel() {
        return scalePanel;
    }

    public OwnShipComponentPanel getOwnShipPanel() {
        return ownShipPanel;
    }

    public PntComponentPanel getGpsPanel() {
        return gpsPanel;
    }

    public CursorComponentPanel getCursorPanel() {
        return cursorPanel;
    }

    public ActiveWaypointComponentPanel getActiveWaypointPanel() {
        return activeWaypointPanel;
    }

    public DockableComponents getDockableComponents() {
        return dockableComponents;
    }

    
    
    /**
     * @return the stccComponentPanel
     */
    public STCCCommunicationComponentPanel getStccComponentPanel() {
        return stccComponentPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MenuBar getJMenuBar() {
        return menuBar;
    }

    public AisComponentPanel getAisComponentPanel() {
        return aisComponentPanel;
    }

    public DynamicNoGoComponentPanel getDynamicNoGoPanel() {
        return dynamicNoGoPanel;
    }

    public NoGoComponentPanel getNogoPanel() {
        return nogoPanel;
    }

    public MultiSourcePntComponentPanel getMsPntComponentPanel() {
        return msPntComponentPanel;
    }

    public SendStrategicRouteDialog getSendStrategicRouteDialog() {
        return sendStrategicRouteDialog;
    }
    
    public SARComponentPanel getSarPanel() {
        return sarPanel;
    }
}

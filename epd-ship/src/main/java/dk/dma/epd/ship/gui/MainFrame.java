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

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.bbn.openmap.MapHandler;

import dk.dma.epd.common.prototype.event.HistoryListener;
import dk.dma.epd.common.prototype.gui.MainFrameCommon;
import dk.dma.epd.common.prototype.gui.MapContainer;
import dk.dma.epd.common.util.VersionInfo;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.ais.AisDialog;
import dk.dma.epd.ship.gui.component_panels.ActiveWaypointComponentPanel;
import dk.dma.epd.ship.gui.component_panels.AisComponentPanel;
import dk.dma.epd.ship.gui.component_panels.CursorComponentPanel;
import dk.dma.epd.ship.gui.component_panels.DynamicNoGoComponentPanel;
import dk.dma.epd.ship.gui.component_panels.MultiSourcePntComponentPanel;
import dk.dma.epd.ship.gui.component_panels.PntComponentPanel;
import dk.dma.epd.ship.gui.component_panels.MSIComponentPanel;
import dk.dma.epd.ship.gui.component_panels.NoGoComponentPanel;
import dk.dma.epd.ship.gui.component_panels.OwnShipComponentPanel;
import dk.dma.epd.ship.gui.component_panels.ScaleComponentPanel;
import dk.dma.epd.ship.gui.msi.MsiDialog;
import dk.dma.epd.ship.gui.route.RouteSuggestionDialog;
import dk.dma.epd.ship.gui.route.strategic.RequestStrategicRouteDialog;
import dk.dma.epd.ship.settings.EPDGuiSettings;

/**
 * The main frame containing map and panels
 */
public class MainFrame extends MainFrameCommon implements MapContainer {

    private static final String TITLE = "EPD-ship " + VersionInfo.getVersion();

    private static final long serialVersionUID = 1L;

    protected static final int SENSOR_PANEL_WIDTH = 190;

    private TopPanel topPanel;
    private ChartPanel chartPanel;

    private BottomPanel bottomPanel;

    private ScaleComponentPanel scalePanel;
    private OwnShipComponentPanel ownShipPanel;
    private PntComponentPanel gpsPanel;
    private CursorComponentPanel cursorPanel;
    private ActiveWaypointComponentPanel activeWaypointPanel;
    private MSIComponentPanel msiComponentPanel;
    private AisComponentPanel aisComponentPanel;
    private DynamicNoGoComponentPanel dynamicNoGoPanel;
    private NoGoComponentPanel nogoPanel;
    private MultiSourcePntComponentPanel msPntComponentPanel;
//    private MonaLisaCommunicationComponentPanel monaLisaPanel;
    
    private MsiDialog msiDialog;
    private AisDialog aisDialog;
    private RouteSuggestionDialog routeSuggestionDialog;

    private DockableComponents dockableComponents;

    private MapMenu mapMenu;
    private MenuBar menuBar;

    private RequestStrategicRouteDialog monaLisaSTCCDialog;
        
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
     * Returns a reference to the map container cast as a component
     * @return a reference to the map container cast as a component
     */
    @Override
    public Component asComponent() {
        return this;
    }
    
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
        msiComponentPanel = new MSIComponentPanel();
        aisComponentPanel = new AisComponentPanel();
        dynamicNoGoPanel = new DynamicNoGoComponentPanel();
        nogoPanel = new NoGoComponentPanel();
        msPntComponentPanel = new MultiSourcePntComponentPanel();
//        monaLisaPanel = new MonaLisaCommunicationComponentPanel();
        
        //Mona Lisa Dialog
        monaLisaSTCCDialog = new RequestStrategicRouteDialog(this);
        
        // Unmovable panels
        bottomPanel = new BottomPanel();

        // Create the dockable layouts
        dockableComponents = new DockableComponents(this);

        dockableComponents.lock();

        // Add panels
        topPanel.setPreferredSize(new Dimension(0, 30));
        pane.add(topPanel, BorderLayout.PAGE_START);
        
        bottomPanel.setPreferredSize(new Dimension(0, 25));
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
        mapHandler.add(msiComponentPanel);
        mapHandler.add(aisComponentPanel);
        mapHandler.add(dynamicNoGoPanel);
        mapHandler.add(nogoPanel);
        mapHandler.add(msPntComponentPanel);
//        mapHandler.add(monaLisaPanel);
        
        // Create top menubar
        menuBar = new MenuBar();
        this.setJMenuBar(menuBar);
        
        // Add self to map map handler
        mapHandler.add(this);
        
        //Add menubar to map handler
        mapHandler.add(menuBar);

        // Init MSI dialog
        msiDialog = new MsiDialog(this);
        mapHandler.add(msiDialog);

        // Init MSI dialog
        aisDialog = new AisDialog(this);
        mapHandler.add(aisDialog);

        // Init Route suggestion dialog
        routeSuggestionDialog = new RouteSuggestionDialog(this);
        mapHandler.add(routeSuggestionDialog);

        // Init the map right click menu
        mapMenu = new MapMenu();
        mapHandler.add(mapMenu);

        // Add a history listener to the chart panel.
        this.chartPanel.setHistoryListener(new HistoryListener(this.chartPanel));
        this.chartPanel.getMap().addProjectionListener(this.chartPanel.getHistoryListener());
        chartPanel.getHistoryListener().setNavigationPanel(topPanel);
        
        if (EPDShip.getInstance().getSettings().getGuiSettings().isFullscreen()){
            doFullScreen();
        }else{
            doNormal();
        }
    }

    public RequestStrategicRouteDialog getMonaLisaSTCCDialog() {
        return monaLisaSTCCDialog;
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

    public void saveSettings() {
        // Save gui settings
        EPDGuiSettings guiSettings = EPDShip.getInstance().getSettings().getGuiSettings();
        guiSettings.setMaximized((getExtendedState() & MAXIMIZED_BOTH) > 0);
        guiSettings.setAppLocation(getLocation());
        guiSettings.setAppDimensions(getSize());
        // Save map settings
        chartPanel.saveSettings();
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public TopPanel getTopPanel() {
        return topPanel;
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

    public MSIComponentPanel getMsiComponentPanel() {
        return msiComponentPanel;
    }

    public MenuBar getEeINSMenuBar() {
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

//    public MonaLisaCommunicationComponentPanel getMonaLisaPanel() {
//        return monaLisaPanel;
//    }

    
    
    public void doFullScreen() {
        setVisible(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        dispose();
        setUndecorated(true);
        setVisible(true);
        EPDShip.getInstance().getSettings().getGuiSettings().setFullscreen(true);
    }
    
    public void doNormal(){
      setVisible(false);
      setExtendedState(JFrame.NORMAL);

      EPDShip.getInstance().getSettings().getGuiSettings().setFullscreen(true);
      setSize(new Dimension(1000, 700));

      dispose();
      setUndecorated(false);
      setVisible(true);
      
      EPDShip.getInstance().getSettings().getGuiSettings().setFullscreen(false);
    }
    
    /**
     * Save the centering of the ship in history.
     */
    public void saveCentreOnShip() {
        // Save the centering of ship to history.
        // ----------------------------

        
        
        EPDShip.getInstance().getMainFrame().getChartPanel().getProjectChangeListener().setShouldSave(true);
        EPDShip.getInstance().getMainFrame().getChartPanel().getProjectChangeListener().saveToHistoryBeforeMoving();

        // Move view to centre on ship.
        this.getChartPanel().centreOnShip();
    }
}

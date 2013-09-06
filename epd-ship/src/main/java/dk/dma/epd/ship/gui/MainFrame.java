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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandler;

import dk.dma.epd.common.util.VersionInfo;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.ais.AisDialog;
import dk.dma.epd.ship.gui.component_panels.ActiveWaypointComponentPanel;
import dk.dma.epd.ship.gui.component_panels.AisComponentPanel;
import dk.dma.epd.ship.gui.component_panels.CursorComponentPanel;
import dk.dma.epd.ship.gui.component_panels.DynamicNoGoComponentPanel;
import dk.dma.epd.ship.gui.component_panels.GpsComponentPanel;
import dk.dma.epd.ship.gui.component_panels.MSIComponentPanel;
import dk.dma.epd.ship.gui.component_panels.MonaLisaCommunicationComponentPanel;
import dk.dma.epd.ship.gui.component_panels.NoGoComponentPanel;
import dk.dma.epd.ship.gui.component_panels.OwnShipComponentPanel;
import dk.dma.epd.ship.gui.component_panels.SARComponentPanel;
import dk.dma.epd.ship.gui.component_panels.ScaleComponentPanel;
import dk.dma.epd.ship.gui.monalisa.MonaLisaSTCCDialog;
import dk.dma.epd.ship.gui.msi.MsiDialog;
import dk.dma.epd.ship.gui.route.RouteSuggestionDialog;
import dk.dma.epd.ship.gui.voct.EffortAllocationWindow;
import dk.dma.epd.ship.gui.voct.SARInput;
import dk.dma.epd.ship.gui.voct.SARInvitationRequest;
import dk.dma.epd.ship.settings.EPDGuiSettings;

/**
 * The main frame containing map and panels
 */
public class MainFrame extends JFrame implements WindowListener {

    private static final String TITLE = "EPD-ship " + VersionInfo.getVersion();

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MainFrame.class);

    protected static final int SENSOR_PANEL_WIDTH = 190;

    private TopPanel topPanel;
    private ChartPanel chartPanel;

    private BottomPanel bottomPanel;

    private ScaleComponentPanel scalePanel;
    private OwnShipComponentPanel ownShipPanel;
    private GpsComponentPanel gpsPanel;
    private CursorComponentPanel cursorPanel;
    private ActiveWaypointComponentPanel activeWaypointPanel;
    private MSIComponentPanel msiComponentPanel;
    private AisComponentPanel aisComponentPanel;
    private DynamicNoGoComponentPanel dynamicNoGoPanel;
    private NoGoComponentPanel nogoPanel;
    private MonaLisaCommunicationComponentPanel monaLisaPanel;
    private SARComponentPanel sarPanel;
    
    private JPanel glassPanel;
    private MsiDialog msiDialog;
    private AisDialog aisDialog;
    private RouteSuggestionDialog routeSuggestionDialog;

    private DockableComponents dockableComponents;

    private MapMenu mapMenu;
    private MenuBar menuBar;

    private MonaLisaSTCCDialog monaLisaSTCCDialog;
    
    public MainFrame() {
        super();
        initGUI();
    }

    private void initGUI() {
        MapHandler mapHandler = EPDShip.getMapHandler();
        // Get settings
        EPDGuiSettings guiSettings = EPDShip.getSettings().getGuiSettings();

        setTitle(TITLE);
        // Set location and size
        if (guiSettings.isMaximized()) {
            setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
        } else {
            setLocation(guiSettings.getAppLocation());
            setSize(guiSettings.getAppDimensions());
        }
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(getAppIcon());
        addWindowListener(this);

        // Create panels
        Container pane = getContentPane();
        topPanel = new TopPanel();

        // Movable service panels
        scalePanel = new ScaleComponentPanel();
        ownShipPanel = new OwnShipComponentPanel();
        gpsPanel = new GpsComponentPanel();
        cursorPanel = new CursorComponentPanel();
        activeWaypointPanel = new ActiveWaypointComponentPanel();
        chartPanel = new ChartPanel(activeWaypointPanel);
        msiComponentPanel = new MSIComponentPanel();
        aisComponentPanel = new AisComponentPanel();
        dynamicNoGoPanel = new DynamicNoGoComponentPanel();
        nogoPanel = new NoGoComponentPanel();
        monaLisaPanel = new MonaLisaCommunicationComponentPanel();
        sarPanel = new SARComponentPanel();
        
        //Mona Lisa Dialog
        monaLisaSTCCDialog = new MonaLisaSTCCDialog(this);
        
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
        mapHandler.add(monaLisaPanel);
        mapHandler.add(sarPanel);
        
        // Create top menubar
        menuBar = new MenuBar();
        this.setJMenuBar(menuBar);
        
        // Init glass pane
        initGlassPane();

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
        
        if (EPDShip.getSettings().getGuiSettings().isFullscreen()){
            doFullScreen();
        }else{
            doNormal();
        }
        
        
        

        
//        EffortAllocationWindow dialog = new EffortAllocationWindow();
//        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//        dialog.setVisible(true);
      
//        SARInvitationRequest dialog = new SARInvitationRequest(this);
//        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//        dialog.setVisible(true);
        
    }

    public MonaLisaSTCCDialog getMonaLisaSTCCDialog() {
        return monaLisaSTCCDialog;
    }

    private void initGlassPane() {
        glassPanel = (JPanel) getGlassPane();
        glassPanel.setLayout(null);
        glassPanel.setVisible(false);
    }

    public static Image getAppIcon() {
        java.net.URL imgURL = EPDShip.class.getResource("/images/appicon.png");
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        }
        LOG.error("Could not find app icon");
        return null;
    }

    @Override
    public void windowActivated(WindowEvent we) {
    }

    @Override
    public void windowClosed(WindowEvent we) {
    }

    @Override
    public void windowClosing(WindowEvent we) {

        // Close routine
        dockableComponents.saveLayout();
        
        
        EPDShip.closeApp();
    }

    public void saveSettings() {
        // Save gui settings
        EPDGuiSettings guiSettings = EPDShip.getSettings().getGuiSettings();
        guiSettings.setMaximized((getExtendedState() & MAXIMIZED_BOTH) > 0);
        guiSettings.setAppLocation(getLocation());
        guiSettings.setAppDimensions(getSize());
        // Save map settings
        chartPanel.saveSettings();
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
    }

    @Override
    public void windowIconified(WindowEvent we) {
    }

    @Override
    public void windowOpened(WindowEvent we) {
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public JPanel getGlassPanel() {
        return glassPanel;
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

    public GpsComponentPanel getGpsPanel() {
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

    public MonaLisaCommunicationComponentPanel getMonaLisaPanel() {
        return monaLisaPanel;
    }

    
    
    public void doFullScreen() {
        setVisible(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        dispose();
        setUndecorated(true);
        // setVisible(true);
        setVisible(true);
//        
//        if (EPDShip.getSettings().getGuiSettings().isFullscreen()) {
//            setVisible(false);
//            setExtendedState(JFrame.MAXIMIZED_BOTH);
//            dispose();
//            setUndecorated(true);
//            // setVisible(true);
//            setVisible(true);
//            EPDShip.getSettings().getGuiSettings().setFullscreen(false);
//        } else {
//
//            setVisible(false);
//            setExtendedState(JFrame.NORMAL);
//
            EPDShip.getSettings().getGuiSettings().setFullscreen(true);
//            setSize(new Dimension(1000, 700));
//
//            dispose();
//            setUndecorated(false);
//            setVisible(true);
//        }
    }
    
    public void doNormal(){
      setVisible(false);
      setExtendedState(JFrame.NORMAL);

      EPDShip.getSettings().getGuiSettings().setFullscreen(true);
      setSize(new Dimension(1000, 700));

      dispose();
      setUndecorated(false);
      setVisible(true);
      
      EPDShip.getSettings().getGuiSettings().setFullscreen(false);
    }

    /**
     * @return the sarPanel
     */
    public SARComponentPanel getSarPanel() {
        return sarPanel;
    }
    
    
}

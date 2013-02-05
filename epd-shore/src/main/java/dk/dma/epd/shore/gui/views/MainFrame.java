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
package dk.dma.epd.shore.gui.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.beans.PropertyVetoException;
import java.beans.beancontext.BeanContextServicesSupport;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.route.RouteManagerDialog;
import dk.dma.epd.shore.settings.ESDGuiSettings;
import dk.dma.epd.shore.settings.Workspace;

/**
 * The main frame containing map and panels
 *
 * @author David A. Camre (davidcamre@gmail.com)
 */
public class MainFrame extends JFrame implements WindowListener {

    private static final String TITLE = "eNav Shore Display System ";

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MainFrame.class);

    private static Image getAppIcon() {
        java.net.URL imgURL = EPDShore.class.getResource("/images/appicon.png");
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        }
        LOG.error("Could not find app icon");
        return null;
    }

    private int windowCount;
    private Dimension size = new Dimension(1000, 700);
    private Point location;
    private JMenuWorkspaceBar topMenu;
    private boolean fullscreen;
    private int mouseMode = 2;
    private boolean wmsLayerEnabled;
    private boolean msiLayerEnabled = true;

    private BeanContextServicesSupport beanHandler;
    private List<JMapFrame> mapWindows;
    private JMainDesktopPane desktop;

    private JScrollPane scrollPane;
    private boolean toolbarsLocked;
    private ToolBar toolbar = new ToolBar(this);
    private NotificationArea notificationArea = new NotificationArea(this);
    private NotificationCenter notificationCenter = new NotificationCenter();
    private JSettingsWindow settingsWindow = new JSettingsWindow();
    private RouteManagerDialog routeManagerDialog = new RouteManagerDialog(this);
    private SendRouteDialog sendRouteDialog = new SendRouteDialog();

    private StatusArea statusArea = new StatusArea(this);
    private JMapFrame activeMapWindow;
    private long selectedMMSI = -1;

    /**
     * Constructor
     */
    public MainFrame() {
        super();
        // System.out.println("before init gui");
        initGUI();

    }



    public NotificationCenter getNotificationCenter() {
        return notificationCenter;
    }



    public JMapFrame getActiveMapWindow() {
        return activeMapWindow;
    }

    public void setActiveMapWindow(JMapFrame activeMapWindow) {
        this.activeMapWindow = activeMapWindow;
    }

    /**
     * Create and add a new map window
     *
     * @return
     */
    public JMapFrame addMapWindow() {
        windowCount++;
        JMapFrame window = new JMapFrame(windowCount, this);

        desktop.add(window);

        mapWindows.add(window);
        // window.toFront();

        topMenu.addMap(window, false, false);
        if (!wmsLayerEnabled) {
            // System.out.println("wmslayer is not enabled");
            window.getChartPanel().getWmsLayer().setVisible(false);
            window.getChartPanel().getBgLayer().setVisible(true);
        } else {
            // System.out.println("wmslayer is enabled");
            window.getChartPanel().getWmsLayer().setVisible(true);
            window.getChartPanel().getBgLayer().setVisible(false);
        }

        if (!msiLayerEnabled) {
            window.getChartPanel().getMsiLayer().setVisible(false);

        }

        if (windowCount == 1) {
            beanHandler.add(window.getChartPanel().getWmsLayer().getWmsService());
        }

        return window;
    }

    /**
     * Add a new mapWindow with specific parameters, usually called when loading
     * a workspace
     *
     * @param workspace
     * @param locked
     * @param alwaysInFront
     * @param center
     * @param scale
     * @return
     */
    public JMapFrame addMapWindow(boolean workspace, boolean locked, boolean alwaysInFront, Point2D center, float scale) {
        windowCount++;

        JMapFrame window = new JMapFrame(windowCount, this, center, scale);
        desktop.add(window, workspace);
        mapWindows.add(window);
        window.toFront();
        topMenu.addMap(window, locked, alwaysInFront);
        window.getChartPanel().getMsiLayer().setVisible(isMsiLayerEnabled());

        if (!wmsLayerEnabled) {
            window.getChartPanel().getWmsLayer().setVisible(false);
            window.getChartPanel().getBgLayer().setVisible(true);
        } else {
            window.getChartPanel().getBgLayer().setVisible(false);
        }

        if (windowCount == 1) {
            beanHandler.add(window.getChartPanel().getWmsLayer().getWmsService());
        }

        return window;
    }

    /**
     * Return the desktop
     *
     * @return
     */
    public JMainDesktopPane getDesktop() {
        return desktop;
    }

    /**
     * Return a list of all active mapwindows
     *
     * @return
     */
    public List<JMapFrame> getMapWindows() {
        return mapWindows;
    }

    /**
     * Get the route manager dialog frame
     * @return
     */
    public RouteManagerDialog getRouteManagerDialog() {
        return routeManagerDialog;
    }

    /**
     * Return the max resolution possible across all monitors
     *
     * @return
     */
    public Dimension getMaxResolution() {
        int width = 0;
        int height = 0;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        for (GraphicsDevice curGs : gs) {
            DisplayMode mode = curGs.getDisplayMode();
            width += mode.getWidth();

            // System.out.println("Width: " + width);

            if (height < mode.getHeight()) {
                height = mode.getHeight();
            }

        }
        return new Dimension(width, height);

    }

    /**
     * Return current active mouseMode
     *
     * @return
     */
    public int getMouseMode() {
        return mouseMode;
    }

    /**
     * Return the notification area
     *
     * @return
     */
    public NotificationArea getNotificationArea() {
        return notificationArea;
    }

    /**
     * Return the status area
     *
     * @return
     */
    public StatusArea getStatusArea() {
        return statusArea;
    }

    /**
     * Return the toolbar
     *
     * @return
     */
    public ToolBar getToolbar() {
        return toolbar;
    }

    /**
     * Initialize the GUI
     */
    private void initGUI() {

        beanHandler = EPDShore.getBeanHandler();
        // Get settings
        ESDGuiSettings guiSettings = EPDShore.getSettings().getGuiSettings();

        // System.out.println("Setting wmslayer enabled to:" +
        // guiSettings.useWMS());
        wmsLayerEnabled = guiSettings.useWMS();

        Workspace workspace = EPDShore.getSettings().getWorkspace();

        setTitle(TITLE);

        // Set location and size
        if (guiSettings.isMaximized()) {
            setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
        } else {
            setLocation(guiSettings.getAppLocation());
        }
        if (guiSettings.isFullscreen()) {
            toggleFullScreen();
        } else {
            setSize(guiSettings.getAppDimensions());
        }

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(getAppIcon());
        addWindowListener(this);

        desktop = new JMainDesktopPane(this);
        scrollPane = new JScrollPane();

        scrollPane.getViewport().add(desktop);
        this.setContentPane(scrollPane);

        desktop.setBackground(new Color(39, 39, 39));

        mapWindows = new ArrayList<JMapFrame>();

        topMenu = new JMenuWorkspaceBar(this);
        this.setJMenuBar(topMenu);

        // Initiate the permanent window elements
        desktop.getManager().setStatusArea(statusArea);
        desktop.getManager().setNotificationArea(notificationArea);
        desktop.getManager().setToolbar(toolbar);
        desktop.getManager().setNotCenter(notificationCenter);
        desktop.getManager().setSettings(settingsWindow);
        desktop.getManager().setRouteManager(routeManagerDialog);
        desktop.getManager().setRouteExchangeDialog(sendRouteDialog);

        desktop.add(statusArea, true);
        desktop.add(notificationCenter, true);
        desktop.add(toolbar, true);
        desktop.add(notificationArea, true);
        desktop.add(settingsWindow, true);
        desktop.add(sendRouteDialog, true);

        beanHandler.add(notificationArea);
        beanHandler.add(settingsWindow);
        beanHandler.add(sendRouteDialog);
        // dtp.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

        // Add self to bean handler
        beanHandler.add(this);
        beanHandler.add(notificationCenter);

        desktop.add(routeManagerDialog, true);
        beanHandler.add(routeManagerDialog);
        beanHandler.add(routeManagerDialog.getRouteManager());
//        routeManagerDialog.setVisible(true);


        setWorkSpace(workspace);

    }

    /**
     * Return the status on toolbars
     *
     * @return
     */
    public boolean isToolbarsLocked() {
        return toolbarsLocked;
    }

    /**
     * Load and setup a new workspace from a file
     *
     * @param parent
     * @param filename
     */
    public void loadNewWorkspace(String parent, String filename) {
        Workspace workspace = EPDShore.getSettings().loadWorkspace(parent, filename);
        setWorkSpace(workspace);
    }

    /**
     * Close a mapWindow
     *
     * @param window
     */
    public void removeMapWindow(JMapFrame window) {
        topMenu.removeMapMenu(window);
        mapWindows.remove(window);
    }

    /**
     * Rename a mapwindow
     *
     * @param window
     */
    public void renameMapWindow(JMapFrame window) {
        topMenu.renameMapMenu(window);
    }

    /**
     * Lock a window in the top menu bar
     *
     * @param window
     *            the window
     */
    public void lockMapWindow(JMapFrame window, boolean locked) {
        topMenu.lockMapMenu(window, locked);
    }

    /**
     * Set a window always on top in top menu
     *
     * @param window
     *            the window
     */
    public void onTopMapWindow(JMapFrame window, boolean locked) {
        topMenu.onTopMapMenu(window, locked);
    }

    /**
     * Save the window settings
     */
    public void saveSettings() {
        // Save gui settings
        ESDGuiSettings guiSettings = EPDShore.getSettings().getGuiSettings();
        guiSettings.setFullscreen(fullscreen);
        guiSettings.setMaximized((getExtendedState() & MAXIMIZED_BOTH) > 0);
        guiSettings.setAppLocation(getLocation());
        guiSettings.setAppDimensions(getSize());

        // Save map settings
        // chartPanel.saveSettings();

    }

    /**
     * Save the workspace with a given name
     *
     * @param filename
     */
    public void saveWorkSpace(String filename) {
        EPDShore.getSettings().getWorkspace().setToolbarPosition(toolbar.getLocation());
        EPDShore.getSettings().getWorkspace().setNotificationAreaPosition(notificationArea.getLocation());
        EPDShore.getSettings().getWorkspace().setStatusPosition(statusArea.getLocation());
        EPDShore.getSettings().saveCurrentWorkspace(mapWindows, filename);
    }

    /**
     * Set the mouse mode
     *
     * @param mouseMode
     */
    public void setMouseMode(int mouseMode) {
        this.mouseMode = mouseMode;
    }

    /**
     * Set a workspace as active
     *
     * @param workspace
     */
    public void setWorkSpace(Workspace workspace) {

        getDesktop().getManager().clearToFront();

        while (mapWindows.size() != 0) {
            try {
                mapWindows.get(0).setClosed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }

        // Reset the workspace
        windowCount = 0;
        mapWindows = new ArrayList<JMapFrame>();

        if (workspace.isValidWorkspace()) {
            for (int i = 0; i < workspace.getName().size(); i++) {
                JMapFrame window = addMapWindow(true, workspace.isLocked().get(i), workspace.getAlwaysInFront().get(i),
                        workspace.getCenter().get(i), workspace.getScale().get(i));

                window.setTitle(workspace.getName().get(i));
                topMenu.renameMapMenu(window);
                window.setSize(workspace.getSize().get(i));
                window.setLocation(workspace.getPosition().get(i));
                toolbar.setLocation(workspace.getToolbarPosition());
                notificationArea.setLocation(workspace.getNotificationAreaPosition());
                statusArea.setLocation(workspace.getStatusPosition());

                if (workspace.isMaximized().get(i)) {
                    window.setSize(600, 600);
                    window.setMaximizedIcon();
                    try {
                        window.setMaximum(workspace.isMaximized().get(i));
                    } catch (PropertyVetoException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (workspace.isLocked().get(i)) {
                    window.lockUnlockWindow();
                }

                if (workspace.getAlwaysInFront().get(i)) {
                    window.alwaysFront();
                }

                // window.getChartPanel().getMap().setScale(0.001f);
                // window.getChartPanel().getMap().setCenter(workspace.getCenter().get(i));
            }

        }

        // Bring toolbar elements to the front
        statusArea.toFront();
        toolbar.toFront();
        notificationArea.toFront();
    }

    /**
     * Toggle the toolbars as locked
     *
     * This function is never called in the current version.
     */
    public void toggleBarsLock() {
        toolbarsLocked = !toolbarsLocked;

        // if (toolbarsLocked) {
        // toolbarsLocked = false;
        // } else {
        // toolbarsLocked = true;
        // }

        toolbar.toggleLock();
        notificationArea.toggleLock();
        statusArea.toggleLock();
    }

    /**
     * Set the maindow in fullscreen mode
     */
    public void toggleFullScreen() {

        // System.out.println(this.getLocationOnScreen());
        // System.out.println("fullscreen toggle");

        if (!fullscreen) {
            location = this.getLocation();
            // System.out.println("Size is: " + size);

            this.setSize(getMaxResolution());
            // setLocationRelativeTo(null);
            this.setLocation(0, 0);
            // setExtendedState(JFrame.MAXIMIZED_BOTH);
            dispose();
            this.setUndecorated(true);
            setVisible(true);
            fullscreen = true;
        } else {
            // setExtendedState(JFrame.NORMAL);
            fullscreen = false;
            if (size.getHeight() != 0 && size.getWidth() != 0) {
                size = Toolkit.getDefaultToolkit().getScreenSize();
                // size = new Dimension(1000, 700);
            }
            this.setSize(size);
            this.setLocation(location);
            dispose();
            this.setUndecorated(false);
            setVisible(true);
        }
    }

    /**
     * Show or hide the notificationCenter
     */
    public void toggleNotificationCenter() {
        notificationCenter.toggleVisibility();
    }

    public void toggleNotificationCenter(int service) {
        notificationCenter.toggleVisibility(service);
    }

    @Override
    public void windowActivated(WindowEvent we) {
    }

    @Override
    public void windowClosed(WindowEvent we) {
    }

    /**
     * Function called on close event, saves the settings on close
     */
    @Override
    public void windowClosing(WindowEvent we) {

        // Close routine
        EPDShore.closeApp();
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

    /**
     * Get if the WMS status is enabled
     *
     * @return boolean detailing if the layer is enabled
     */
    public boolean isWmsLayerEnabled() {
        return wmsLayerEnabled;
    }

    /**
     * set the WMS layers enabled/disabled
     *
     * @param wmsLayerEnabled
     */
    public void setWmsLayerEnabled(boolean wmsLayerEnabled) {
        this.wmsLayerEnabled = wmsLayerEnabled;
    }

    /**
     * Get if the MSI status is enabled
     *
     * @return boolean detailing if the layer is enabled
     */
    public boolean isMsiLayerEnabled() {
        return msiLayerEnabled;
    }

    /**
     * set the MSI layers enabled/disabled
     *
     * @param wmsLayerEnabled
     */
    public void setMSILayerEnabled(boolean msiLayerEnabled) {
        this.msiLayerEnabled = msiLayerEnabled;
    }

    public JSettingsWindow getSettingsWindow() {
        return settingsWindow;
    }

    public synchronized long getSelectedMMSI() {
        return selectedMMSI;
    }

    public synchronized void setSelectedMMSI(long selectedMMSI) {
        this.selectedMMSI = selectedMMSI;
    }



    public SendRouteDialog getSendRouteDialog() {
        return sendRouteDialog;
    }



}

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
package dk.dma.epd.shore.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.event.mouse.CommonDistanceCircleMouseMode;
import dk.dma.epd.common.prototype.layers.routeedit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.settings.Settings;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.event.DragMouseMode;
import dk.dma.epd.shore.event.NavigationMouseMode;
import dk.dma.epd.shore.event.RouteEditMouseMode;
import dk.dma.epd.shore.event.SelectMouseMode;
import dk.dma.epd.shore.event.ToolbarMoveMouseListener;
import dk.dma.epd.shore.gui.utils.ToolItemGroup;

/**
 * Class for setting up the toolbar of the application
 * 
 */
public class ToolBar extends JInternalFrame {

    private static final long serialVersionUID = 1L;
    private Boolean locked = false;
    private JLabel moveHandler;
    private JPanel masterPanel;
    private JPanel buttonPanel;
    private static int moveHandlerHeight = 18;
    private static int toolItemSize = 35;
    private static int toolItemColumns = 2;
    private static int buttonPanelOffset = 4;
    private ArrayList<ToolItemGroup> toolItemGroups = new ArrayList<ToolItemGroup>();
    public int width;
    public int height;
    private static int iconWidth = 16;
    private static int iconHeight = 16;
    private Border toolPaddingBorder = BorderFactory.createMatteBorder(3, 3, 3,
            3, new Color(83, 83, 83));
    private Border toolInnerEtchedBorder = BorderFactory.createEtchedBorder(
            EtchedBorder.LOWERED, new Color(37, 37, 37), new Color(52, 52, 52));

    final JLabel enc = new JLabel(toolbarIcon("images/toolbar/map-medium.png"));

    private boolean routeCreation;
    private final JLabel newRoute;
    private final ToolItemGroup routeToolItems;
    private MainFrame mainFrame;
    private final ToolItemGroup mapToolItems;
    private JLabel select;
    private JLabel drag;
    private JLabel zoom;

    /**
     * Constructor for setting up the toolbar
     * 
     * @param mainFrame
     *            reference to the mainframe
     */
    public ToolBar(final MainFrame mainFrame) {

        this.mainFrame = mainFrame;
        final Settings settings = EPD.getInstance().getSettings();

        // Setup location
        this.setLocation(10 + moveHandlerHeight, 10);
        this.setVisible(true);
        this.setResizable(false);

        // Strip off window looks
        setRootPaneCheckingEnabled(false);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI())
                .setNorthPane(null);
        this.setBorder(null);

        // Create the top movehandler (for dragging)
        moveHandler = new JLabel("Toolbar", SwingConstants.CENTER);
        moveHandler.setForeground(new Color(200, 200, 200));
        moveHandler.setOpaque(true);
        moveHandler.setBackground(Color.DARK_GRAY);
        moveHandler.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                new Color(30, 30, 30)));
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setPreferredSize(new Dimension(toolItemSize
                * toolItemColumns, moveHandlerHeight));
        ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this,
                mainFrame);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);

        // Create the grid for the toolitems
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        buttonPanel.setBackground(new Color(83, 83, 83));

        // Setup toolitems (add here for more toolitems)
        // Tool group: Map tools
        mapToolItems = new ToolItemGroup();

        this.select = new JLabel(
                toolbarIcon("images/toolbar/select.png"));
        getSelectBtn().addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setActiveToolItem(getSelectBtn(), mapToolItems);
                for (JMapFrame mapFrame : mainFrame.getMapWindows()) {
                    mapFrame.getChartPanel().setMouseMode(SelectMouseMode.MODEID);
                }
                mainFrame.setMouseMode(SelectMouseMode.MODEID);
            }
        });
        getSelectBtn().setToolTipText("Select mouse mode");
        mapToolItems.addToolItem(getSelectBtn());

        this.drag = new JLabel(toolbarIcon("images/toolbar/drag.png"));
        drag.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {

                for (JMapFrame mapFrame : mainFrame.getMapWindows()) {
                    mapFrame.getChartPanel().setMouseMode(DragMouseMode.MODEID);
                }
                mainFrame.setMouseMode(DragMouseMode.MODEID);
            }
        });
        drag.setToolTipText("Drag mouse mode");
        mapToolItems.addToolItem(drag);

        this.zoom = new JLabel(toolbarIcon("images/toolbar/zoom.png"));
        zoom.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {

                for (JMapFrame mapFrame : mainFrame.getMapWindows()) {
                    mapFrame.getChartPanel().setMouseMode(NavigationMouseMode.MODEID);
                }
                mainFrame.setMouseMode(NavigationMouseMode.MODEID);
            }
        });
        zoom.setToolTipText("Zoom mouse mode");
        mapToolItems.addToolItem(zoom);
        
        // Tool: Distance Circle
        final JLabel distanceCircle = new JLabel(toolbarIcon("images/toolbar/ruler-triangle.png"));
        distanceCircle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                setActiveToolItem(distanceCircle, mapToolItems);
                
                for (JMapFrame frame : mainFrame.getMapWindows()) {
                    frame.getChartPanel().setMouseMode(CommonDistanceCircleMouseMode.MODE_ID);
                }
                mainFrame.setMouseMode(CommonDistanceCircleMouseMode.MODE_ID);
            }
        });
        distanceCircle.setToolTipText("Enable range circles mode.");
        this.mapToolItems.addToolItem(distanceCircle);

        // Set that the map tools only can have 1 active tool item at a time
        mapToolItems.setSingleEnable(true);

        // Set default active tool item for this group
        setActiveToolItem(getSelectBtn(), mapToolItems);

        toolItemGroups.add(mapToolItems);

        // Tool group: Layer tools
        final ToolItemGroup layerToolItems = new ToolItemGroup();

        // Tool: WMS layer
        final JLabel wms = new JLabel(
                toolbarIcon("images/toolbar/wms_small.png"));
        wms.setName("wms");
        wms.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (settings.getMapSettings().isWmsVisible()) {
                    settings.getMapSettings().setWmsVisible(false);
                    for (JMapFrame mapFrame : mainFrame.getMapWindows()) {
                        if (mapFrame.getChartPanel().getWmsLayer() != null) {
                            mapFrame.getChartPanel().getWmsLayer().setVisible(false);
                        }
                    }
                    setInactiveToolItem(wms);

                } else {
                    settings.getMapSettings().setWmsVisible(true);
                    for (JMapFrame mapFrame : mainFrame.getMapWindows()) {
                        if (mapFrame.getChartPanel().getWmsLayer() != null) {
                            mapFrame.getChartPanel().getWmsLayer().setVisible(true);
                        }
                    }
                    setActiveToolItem(wms, layerToolItems);
                }
            }
        });
        wms.setToolTipText("Show/hide WMS seacharts");
        layerToolItems.addToolItem(wms);
        if (!settings.getMapSettings().isUseWms()) {
            wms.setEnabled(false);
        }
        if (settings.getMapSettings().isWmsVisible()) {
            setActiveToolItem(wms, layerToolItems);
        }

        // Tool: MSI layer
        final JLabel msi = new JLabel(
                toolbarIcon("images/toolbar/msi_symbol_16.png"));
        msi.setName("msi");
        msi.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {

                if (mainFrame.isMsiLayerEnabled()) {
                    mainFrame.setMSILayerEnabled(false);
                    for (JMapFrame mapFrame : mainFrame.getMapWindows()) {
                        mapFrame.getChartPanel().getMsiLayer().setVisible(false);
                    }
                    setInactiveToolItem(msi);
                } else {
                    mainFrame.setMSILayerEnabled(true);
                    for (JMapFrame mapFrame : mainFrame.getMapWindows()) {
                        mapFrame.getChartPanel().getMsiLayer().setVisible(true);
                    }
                    setActiveToolItem(msi, layerToolItems);
                }
            }
        });
        msi.setToolTipText("Show/hide maritime safety information");
        layerToolItems.addToolItem(msi);
        
        // Button which will toggle vessel names on or off.
        final JLabel aisToggle = new JLabel(toolbarIcon("images/toolbar/edit-letter-spacing.png"));
        aisToggle.addMouseListener(new MouseAdapter() {
            
            // Initialize with ship visibility.
            private boolean isPressed = settings.getAisSettings().isShowNameLabels();
            
            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                // If names are show -
                if (isPressed) {
                    // Set button to off.
                    setInactiveToolItem(aisToggle);
                    // Name labels should not be seen.
                    isPressed = false;
                    // Update visibility of the vessel names.
                    toggleVesselNames(isPressed);
                    
                // If names are not shown.
                } else if (!isPressed) {
                    // set button to on.
                    setActiveToolItem(aisToggle, layerToolItems);
                    // Name labels should now be seen.
                    isPressed = true;
                    // Update visibility of the vessel names.
                    toggleVesselNames(isPressed);
                }
            }

            /**
             * Updates the visibility of vessel names.
             * @param showLabels
             *          Sets visibility of vessel names to
             *          the passed value. 
             */
            private void toggleVesselNames(boolean showLabels) {
                // For each JMapFrame which is used in the mainFrame.
                for (JMapFrame map : EPDShore.getInstance().getMainFrame().getMapWindows()) {
                    // Updates the right click menu option to set visibility of vessel names.
                    map.getMapMenu().getAisNames().setNamesShouldBeVisible(showLabels);
                }
                // Update the settings file. PropertyChangeListeners of AisSettings will be notified as part of this call.
                settings.getAisSettings().setShowNameLabels(showLabels);
            }
        });
        
        if (settings.getAisSettings().isShowNameLabels()) {
            setActiveToolItem(aisToggle, layerToolItems);            
        }
        aisToggle.setToolTipText("Show/hide AIS names");
        layerToolItems.addToolItem(aisToggle);

        try {

            if (settings.getMapSettings().isUseEnc()) {

                // Tool: ENC layer

                enc.setName("enc");
                enc.addMouseListener(new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        if (enc.isEnabled()) {

                            if (settings.getMapSettings().isEncVisible()) {
                                settings.getMapSettings().setEncVisible(false);
                                for (JMapFrame mapFrame : mainFrame.getMapWindows()) {
                                    mapFrame.getChartPanel().encVisible(false);
                                }
                                setInactiveToolItem(enc);

                            } else {
                                settings.getMapSettings().setEncVisible(true);
                                for (JMapFrame mapFrame : mainFrame.getMapWindows()) {
                                    mapFrame.getChartPanel().encVisible(true);
                                }
                                setActiveToolItem(enc, layerToolItems);
                            }
                        }
                    }
                });
                enc.setToolTipText("Show/hide ENC");

                // is visible vs. is active
                // disable bg or wms or enc?

                layerToolItems.addToolItem(enc);
                if (settings.getMapSettings().isEncVisible()) {
                    setActiveToolItem(enc, layerToolItems);
                }

                enc.setEnabled(false);
            }
        } catch (Exception e) {
            System.out.println("failed to load enc dongle");
        }
        
        // Tool: MSI layer
        final JLabel intendedRoutes = new JLabel(
                toolbarIcon("images/toolbar/direction.png"));
        intendedRoutes.setName("intended routes");
        intendedRoutes.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                boolean intendedRoutesVisible = settings.getCloudSettings().isShowIntendedRoute();
                settings.getCloudSettings().setShowIntendedRoute(!intendedRoutesVisible);
                for (JMapFrame mapFrame : mainFrame.getMapWindows()) {
                    mapFrame.getChartPanel().intendedRouteLayerVisible(!intendedRoutesVisible);
                }
                if (intendedRoutesVisible) {
                    setInactiveToolItem(intendedRoutes);
                } else {
                    setActiveToolItem(intendedRoutes, layerToolItems);
                }
            }
        });
        intendedRoutes.setToolTipText("Show/hide intended routes");
        layerToolItems.addToolItem(intendedRoutes);
        if (settings.getCloudSettings().isShowIntendedRoute()) {
            setActiveToolItem(intendedRoutes, layerToolItems);
        }
        

        // Set that the layer tools can have more than 1 active tool item at a
        // time
        layerToolItems.setSingleEnable(false);

        // Set default active tool(s) for this group
        setActiveToolItem(msi, layerToolItems);

        toolItemGroups.add(layerToolItems);

        // Tool group: Route tools
        routeToolItems = new ToolItemGroup();

        // Tool: Routes
        final JLabel routes = new JLabel(
                toolbarIcon("images/toolbar/routes.png"));
        routes.setName("routes");
        routes.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                setActiveToolItem(routes, routeToolItems);
            }

            public void mouseReleased(MouseEvent e) {
                setInactiveToolItem(routes);
                mainFrame.getRouteManagerDialog().setVisible(
                        !mainFrame.getRouteManagerDialog().isVisible());
            }
        });
        routes.setToolTipText("Routes Manager");
        routeToolItems.addToolItem(routes);

        // Tool: New route
        newRoute = new JLabel(toolbarIcon("images/toolbar/routes_new.png"));
        newRoute.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                if (routeCreation) {
                    setInactiveToolItem(newRoute);
                } else {
                    setActiveToolItem(newRoute, routeToolItems);
                }
            }

            public void mouseReleased(MouseEvent e) {
                newRoute();
            }
        });
        newRoute.setToolTipText("Add route");
        routeToolItems.addToolItem(newRoute);

        // Set that the layer tools can have more than 1 active tool item at a
        // time
        routeToolItems.setSingleEnable(false);

        toolItemGroups.add(routeToolItems);

        // Create the masterpanel for aligning
        masterPanel = new JPanel(new BorderLayout());
        masterPanel.add(moveHandler, BorderLayout.NORTH);
        masterPanel.add(buttonPanel, BorderLayout.SOUTH);
        masterPanel.setBorder(BorderFactory.createEtchedBorder(
                EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45,
                        45)));
        this.getContentPane().add(masterPanel);

        // And finally refresh the toolbar
        repaintToolbar();

    }

    public void enableEncButton() {
        enc.setEnabled(true);
    }

    public void newRoute() {

        if (routeCreation) {
            setInactiveToolItem(newRoute);
        } else {
            setActiveToolItem(newRoute, routeToolItems);
        }

        if (routeCreation) {

            // Deactivate
            routeCreation = false;

            for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
                mainFrame.getMapWindows().get(i).getChartPanel()
                        .setMouseMode(mainFrame.getMouseMode());
            }

            // Save route?
            endRoute();

            // Re activate the tool options

            for (int j = 0; j < mapToolItems.getToolItems().size(); j++) {
                JLabel label = mapToolItems.getToolItems().get(j);
                label.setEnabled(true);
            }

        } else {
            routeCreation = true;

            for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
                mainFrame.getMapWindows().get(i).getChartPanel()
                        .setMouseMode(RouteEditMouseMode.MODEID);
            }

            // Deactivate other map tools

            for (int j = 0; j < mapToolItems.getToolItems().size(); j++) {
                JLabel label = mapToolItems.getToolItems().get(j);
                label.setEnabled(false);
            }

        }
    }

    public void endRoute() {

        NewRouteContainerLayer newRouteLayer = EPDShore.getInstance()
                .getMainFrame().getActiveChartPanel()
                .getNewRouteContainerLayer();

        // Route saved
        if (newRouteLayer.getRoute().getWaypoints().size() > 1) {
            Route route = new Route(newRouteLayer.getRoute());
            route.setName("New route");
            int i = 1;
            LinkedList<RouteWaypoint> waypoints = route.getWaypoints();
            for (RouteWaypoint routeWaypoint : waypoints) {
                if (routeWaypoint.getOutLeg() != null) {
                    RouteLeg outLeg = routeWaypoint.getOutLeg();

                    double xtd = EPDShore.getInstance().getSettings()
                            .getNavSettings().getDefaultXtd();
                    outLeg.setXtdPort(xtd);
                    outLeg.setXtdStarboard(xtd);
                    outLeg.setHeading(Heading.RL);
                    outLeg.setSpeed(EPDShore.getInstance().getSettings()
                            .getNavSettings().getDefaultSpeed());
                }
                routeWaypoint.setTurnRad(EPDShore.getInstance().getSettings()
                        .getNavSettings().getDefaultTurnRad());
                routeWaypoint.setName(String.format("WP_%03d", i));
                i++;
            }
            route.calcValues(true);

            EPDShore.getInstance().getMainFrame().getRouteManagerDialog()
                    .getRouteManager().addRoute(route);
            EPDShore.getInstance().getMainFrame().getRouteManagerDialog()
                    .getRouteManager().notifyListeners(null);
        }

        for (int i = 0; i < EPDShore.getInstance().getMainFrame()
                .getMapWindows().size(); i++) {
            EPDShore.getInstance().getMainFrame().getMapWindows().get(i)
                    .getChartPanel().getRouteEditLayer().doPrepare();
            EPDShore.getInstance().getMainFrame().getMapWindows().get(i)
                    .getChartPanel().getNewRouteContainerLayer().getWaypoints()
                    .clear();
            EPDShore.getInstance().getMainFrame().getMapWindows().get(i)
                    .getChartPanel().getNewRouteContainerLayer()
                    .getRouteGraphics().clear();
            EPDShore.getInstance().getMainFrame().getMapWindows().get(i)
                    .getChartPanel().getNewRouteContainerLayer().doPrepare();
        }

    }

    /**
     * Function for setting the active tool item in the toolbar
     * 
     * @param tool
     *            reference to the active tool
     */
    public void setActiveToolItem(JLabel toolItem, ToolItemGroup toolItems) {

        if (toolItems.isSingleEnable()) {
            ArrayList<JLabel> items = toolItems.getToolItems();

            for (int i = 0; i < items.size(); i++) {
                items.get(i).setBorder(toolPaddingBorder);
                items.get(i).setOpaque(false);
            }
        }

        // Set active tool
        toolItem.setBackground(new Color(55, 55, 55));
        toolItem.setBorder(BorderFactory.createCompoundBorder(
                toolPaddingBorder, toolInnerEtchedBorder));
        toolItem.setOpaque(true);
    }

    public void setInactiveToolItem(JLabel toolItem) {
        toolItem.setBorder(toolPaddingBorder);
        toolItem.setOpaque(false);
    }

    /**
     * Function for resizing the icons for the toolbar
     * 
     * @param imgpath
     *            path of the image
     * @return newimage the newly created and resized image
     */
    public ImageIcon toolbarIcon(String imgpath) {

        ImageIcon icon = EPDShore.res().getCachedImageIcon(imgpath);
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(iconWidth, iconHeight,
                java.awt.Image.SCALE_DEFAULT);
        ImageIcon newImage = new ImageIcon(newimg);
        return newImage;
    }

    /**
     * Function for locking/unlocking the toolbar
     */
    public void toggleLock() {
        if (locked) {
            masterPanel.add(moveHandler, BorderLayout.NORTH);
            locked = false;
            repaintToolbar();

            // Align the toolbar according to the height of the movehandler
            int newX = (int) this.getLocation().getX();
            int newY = (int) this.getLocation().getY();
            Point new_location = new Point(newX, newY - moveHandlerHeight);
            this.setLocation(new_location);

        } else {
            masterPanel.remove(moveHandler);
            locked = true;
            repaintToolbar();

            // Align the toolbar according to the height of the movehandler
            int newX = (int) this.getLocation().getX();
            int newY = (int) this.getLocation().getY();
            Point new_location = new Point(newX, newY + moveHandlerHeight);
            this.setLocation(new_location);
        }
    }

    /**
     * Function for refreshing the toolbar after editing toolitems
     */
    public void repaintToolbar() {

        buttonPanel.removeAll();
        buttonPanel.updateUI();

        width = toolItemSize * toolItemColumns;
        height = 0;

        for (int i = 0; i < toolItemGroups.size(); i++) {

            // Add the tool item group
            JPanel group = new JPanel();
            group.setLayout(new GridLayout(0, toolItemColumns));
            group.setOpaque(false);

            double temp = (double) toolItemGroups.get(i).getToolItems().size()
                    / (double) toolItemColumns;
            int innerHeight = (int) (Math.ceil(temp) * (toolItemSize - 3));

            height = height + (int) (Math.ceil(temp) * (toolItemSize - 1));

            group.setSize(width, innerHeight);
            group.setPreferredSize(new Dimension(width, innerHeight));

            // Populate it with tool items
            ArrayList<JLabel> items = toolItemGroups.get(i).getToolItems();
            for (int t = 0; t < items.size(); t++) {
                JLabel item = items.get(t);
                group.add(item);
            }

            buttonPanel.add(group);

            // Add a separator
            if (i < toolItemGroups.size() - 1) {
                JSeparator sep = new JSeparator();
                sep.setForeground(new Color(65, 65, 65));
                sep.setBackground(new Color(83, 83, 83));
                buttonPanel.add(sep);

                height = height + 7;
            }
        }

        int innerHeight = height;

        if (!locked) {
            height = height + moveHandlerHeight;
        }

        // And finally set the size and repaint it
        buttonPanel.setSize(width, innerHeight - buttonPanelOffset);
        buttonPanel.setPreferredSize(new Dimension(width, innerHeight
                - buttonPanelOffset));
        this.setSize(width, height);
        this.revalidate();
        this.repaint();
    }

    /**
     * Function for getting the width of the toolbar
     * 
     * @return width width of the toolbar
     */
    public int getWidth() {
        return width;
    }

    /**
     * Function for getting the height of the toolbar
     * 
     * @return height height of the toolbar
     */
    public int getHeight() {
        return height;
    }

    public boolean isEncButtonEnabled() {
        return enc.isEnabled();
    }

    public JLabel getSelectBtn() {
        return this.select;
    }
    
    public JLabel getZoomBtn() {
        return this.zoom;
    }
    
    public JLabel getDragBtn() {
        return this.drag;
    }
    
    public ToolItemGroup getMapToolItems() {
        return this.mapToolItems;
    }
}

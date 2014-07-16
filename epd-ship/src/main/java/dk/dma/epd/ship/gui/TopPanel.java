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
package dk.dma.epd.ship.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;

import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.common.prototype.event.HistoryNavigationPanelInterface;
import dk.dma.epd.common.prototype.event.mouse.CommonDistanceCircleMouseMode;
import dk.dma.epd.common.prototype.gui.GoBackButton;
import dk.dma.epd.common.prototype.gui.GoForwardButton;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLayerCommon;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.event.DistanceCircleMouseMode;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.event.RouteEditMouseMode;
import dk.dma.epd.ship.gui.ais.AisDialog;
import dk.dma.epd.ship.gui.route.RouteManagerDialog;
import dk.dma.epd.ship.layers.route.RouteLayer;

/**
 * The top buttons panel
 */
public class TopPanel extends OMComponentPanel implements ActionListener,
        MouseListener, HistoryNavigationPanelInterface {

    private static final long serialVersionUID = 1L;

    private final ButtonLabel zoomInBtn = new ButtonLabel(
            toolbarIcon("magnifier-zoom-in.png"));
    private final ButtonLabel zoomOutBtn = new ButtonLabel(
            toolbarIcon("magnifier-zoom-out.png"));
    private final ButtonLabel centreBtn = new ButtonLabel(
            toolbarIcon("arrow-in.png"));
    private final ToggleButtonLabel autoFollowBtn = new ToggleButtonLabel(
            toolbarIcon("arrow-curve-000-double.png"));
    private final ButtonLabel setupBtn = new ButtonLabel(
            toolbarIcon("wrench.png"));
    private final ButtonLabel routeManagerBtn = new ButtonLabel(
            toolbarIcon("marker.png"));
    private final ButtonLabel aisDialogButton = new ButtonLabel(
            toolbarIcon("radar.png"));
    private final ToggleButtonLabel aisToggleName = new ToggleButtonLabel(
            toolbarIcon("edit-letter-spacing.png"));
    private final ToggleButtonLabel aisLayerBtn = new ToggleButtonLabel(
            toolbarIcon("board-game.png"));
    private final ToggleButtonLabel encBtn = new ToggleButtonLabel(
            toolbarIcon("map-medium.png"));
    private final ToggleButtonLabel wmsBtn = new ToggleButtonLabel(
            toolbarIcon("map-medium.png"));
    private final ToggleButtonLabel newRouteBtn = new ToggleButtonLabel(
            toolbarIcon("marker--plus.png"));
    private final ToggleButtonLabel toggleSafeHaven = new ToggleButtonLabel(
            toolbarIcon("document-resize-actual.png"));
    private final ToggleButtonLabel navigationMouseMode = new ToggleButtonLabel(
            toolbarIcon("zoom.png"));
    private final ToggleButtonLabel dragMouseMode = new ToggleButtonLabel(
            toolbarIcon("drag.png"));
    private final ToggleButtonLabel toggleIntendedRoute = new ToggleButtonLabel(
            toolbarIcon("direction.png"));
    private final ToggleButtonLabel toggleIntendedRouteFilter = new ToggleButtonLabel(
            toolbarIcon("road-sign.png"));
    private final ToggleButtonLabel toggleDynamicPredictorLayer = new ToggleButtonLabel(
            toolbarIcon("dynamic-predictor.png"));

    /**
     * Toggle button to enable distance circle mode.
     */
    private final ToggleButtonLabel toggleDistanceCircleMode = new ToggleButtonLabel(
            toolbarIcon("ruler-triangle.png"));

    private MainFrame mainFrame;
    private AisDialog aisDialog;
    private MenuBar menuBar;
    private RouteLayer routeLayer;
    private IntendedRouteLayerCommon intendedRouteLayer;

    private MouseDelegator mouseDelegator;
    private final GoBackButton goBackBtn = new GoBackButton();
    private final GoForwardButton goForwardBtn = new GoForwardButton();
        
    private static int iconWidth = 16;
    private static int iconHeight = 16;
    
    /**
     * A slightly hacked way of simulating a click on the aisToggleName label
     */
    private IMapMenuAction hideAisNamesAction = new IMapMenuAction() {
        @Override public void doAction() {
            if (aisToggleName.isSelected()) {
                aisToggleName.setSelected(false);
                mouseReleased(new MouseEvent(aisToggleName, 0, 0L, 0, 0, 0, 0, false));
            }
        }};


    public TopPanel() {
        super();

        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

        this.setMinimumSize(new Dimension(0, 24));
        this.setPreferredSize(new Dimension(0, 24));

        zoomInBtn.setToolTipText("Zoom in : Shortcut Numpad +");
        zoomOutBtn.setToolTipText("Zoom out : Shortcut Numpad -");
        centreBtn.setToolTipText("Centre on ship : Shortcut C");

        navigationMouseMode.setToolTipText("Navigation Mouse Mode");
        dragMouseMode.setToolTipText("Drag mouse mode");

        autoFollowBtn.setToolTipText("Auto follow own ship");
        setupBtn.setToolTipText("Setup");
        newRouteBtn.setToolTipText("Add a new route : Shortcut Ctrl N");
        routeManagerBtn.setToolTipText("Routes Manager : Shortcut Ctrl R");
        aisDialogButton.setToolTipText("Show nearby vessels : Shortcut Ctrl A");
        aisLayerBtn.setToolTipText("Show/hide AIS targets");
        aisToggleName.setToolTipText("Show/hide AIS Name Labels");
        encBtn.setToolTipText("Show/hide ENC");
        encBtn.setEnabled(EPDShip.getInstance().getSettings().getMapSettings().isUseEnc());
        toggleIntendedRoute.setToolTipText("Show/hide intended routes");
        toggleIntendedRouteFilter.setToolTipText("Toggle Intended Route Filter");
        
        goBackBtn.setToolTipText("Go back");
        goForwardBtn.setToolTipText("Go forward");
        goBackBtn.setEnabled(false);
        goForwardBtn.setEnabled(false);

        wmsBtn.setToolTipText("Show/hide WMS seacharts");

        toggleSafeHaven.setToolTipText("Show/hide SafeHaven guidelines");

        this.toggleDistanceCircleMode
                .setToolTipText("Enable range circles mode.");
        
        this.toggleDynamicPredictorLayer.setToolTipText("Toggle dynamic predictor layer");
        // disable button if dynamic predictor disabled
        this.toggleDynamicPredictorLayer.setEnabled(EPDShip.getInstance().getSettings().getNavSettings().isDynamicPrediction());
        
        add(goBackBtn);
        add(goForwardBtn);
        add(zoomInBtn);
        add(zoomOutBtn);
        add(navigationMouseMode);
        add(dragMouseMode);
        // add the toggle button to the component
        add(this.toggleDistanceCircleMode);
        add(centreBtn);
        add(autoFollowBtn);
        add(setupBtn);
        add(newRouteBtn);
        add(routeManagerBtn);
        add(aisDialogButton);
        add(new JSeparator());
        add(aisLayerBtn);
        add(aisToggleName);
        add(encBtn);
        add(wmsBtn);
        add(toggleSafeHaven);
        add(toggleIntendedRoute);
        add(toggleIntendedRouteFilter);
        add(this.toggleDynamicPredictorLayer);
        
        Component horizontalStrut = Box.createHorizontalStrut(5);
        horizontalStrut = Box.createHorizontalStrut(5);

        ImageIcon[] msiAnim = new ImageIcon[2];
        msiAnim[0] = EPDShip.res().getCachedImageIcon("/images/toppanel/msi_symbol_64x20.png");
        msiAnim[1] = EPDShip.res().getCachedImageIcon("/images/toppanel/blank64x20.png");

        add(horizontalStrut);

        zoomInBtn.addMouseListener(this);

        zoomOutBtn.addMouseListener(this);
        centreBtn.addMouseListener(this);
        autoFollowBtn.addMouseListener(this);
        setupBtn.addMouseListener(this);
        newRouteBtn.addMouseListener(this);
        routeManagerBtn.addMouseListener(this);
        aisDialogButton.addMouseListener(this);
        aisLayerBtn.addMouseListener(this);
        encBtn.addMouseListener(this);
        wmsBtn.addMouseListener(this);
        aisToggleName.addMouseListener(this);
        goBackBtn.addMouseListener(this);
        goForwardBtn.addMouseListener(this);
        toggleSafeHaven.addMouseListener(this);
        navigationMouseMode.addMouseListener(this);
        dragMouseMode.addMouseListener(this);
        toggleDistanceCircleMode.addMouseListener(this);
        toggleIntendedRoute.addMouseListener(this);
        toggleIntendedRouteFilter.addMouseListener(this);
        toggleDynamicPredictorLayer.addMouseListener(this);
        
        updateButtons();
    }

    public void updateButtons() {
        autoFollowBtn.setSelected(EPDShip.getInstance().getSettings().getNavSettings()
                .isAutoFollow());
        aisLayerBtn.setSelected(EPDShip.getInstance().getSettings().getAisSettings().isVisible());
        encBtn.setSelected(EPDShip.getInstance().getSettings().getMapSettings()
                .isEncVisible());
        wmsBtn.setSelected(EPDShip.getInstance().getSettings().getMapSettings()
                .isWmsVisible());
        aisToggleName.setSelected(EPDShip.getInstance().getSettings().getAisSettings()
                .isShowNameLabels());

        navigationMouseMode.setSelected(true);
        // range circles mode is disabled by default.
        toggleDistanceCircleMode.setSelected(false);
        
        toggleIntendedRoute.setSelected(EPDShip.getInstance().getSettings().getCloudSettings().isShowIntendedRoute());
        
        // toggle button according to value stored in settings
        toggleDynamicPredictorLayer.setSelected(EPDShip.getInstance().getSettings().getNavSettings().isDynamicPrediction());
    }

    public void disableAutoFollow() {
        EPDShip.getInstance().getSettings().getNavSettings().setAutoFollow(false);
        if (autoFollowBtn.isSelected()) {
            autoFollowBtn.setSelected(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void newRoute() {
        if (mouseDelegator.getActiveMouseModeID() == NavigationMouseMode.MODE_ID
                || mouseDelegator.getActiveMouseModeID() == DragMouseMode.MODE_ID) {
            mainFrame.getChartPanel().setMouseMode(RouteEditMouseMode.MODE_ID);
        } else {
            mainFrame.getChartPanel().setMouseMode(NavigationMouseMode.MODE_ID);
        }
    }

    public void activateNewRouteButton() {
        newRoute();
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MainFrame) {
            mainFrame = (MainFrame) obj;
        }
        if (obj instanceof MouseDelegator) {
            mouseDelegator = (MouseDelegator) obj;
        }
        if (obj instanceof AisDialog) {
            aisDialog = (AisDialog) obj;
        }
        if (obj instanceof MenuBar) {
            menuBar = (MenuBar) obj;
        }
        if (obj instanceof RouteLayer) {
            routeLayer = (RouteLayer) obj;
        }
        if (obj instanceof IntendedRouteLayerCommon) {
            intendedRouteLayer = (IntendedRouteLayerCommon) obj;
        }
    }
    
    public GoBackButton getGoBackButton() {
        return this.goBackBtn;
    }
    
    public GoForwardButton getGoForwardButton() {
        return this.goForwardBtn;
    }

    public AisDialog getAisDialog() {
        return aisDialog;
    }

    public void setEncDisabled() {
        encBtn.setEnabled(false);
        encBtn.setSelected(false);
    }

    public void setWMSDisabled() {
        wmsBtn.setEnabled(false);
        wmsBtn.setSelected(false);
    }

    public ToggleButtonLabel getNewRouteBtn() {
        return newRouteBtn;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (e.getSource() == autoFollowBtn) {
            EPDShip.getInstance().getSettings().getNavSettings()
                    .setAutoFollow(autoFollowBtn.isSelected());
            if (autoFollowBtn.isSelected()) {
                mainFrame.getChartPanel().autoFollow();
            }
            menuBar.getAutoFollow().setSelected(
                    EPDShip.getInstance().getSettings().getNavSettings().isAutoFollow());
            
        }
        else if (e.getSource() == zoomInBtn) {
            mainFrame.getChartPanel().doZoom(0.5f);
            
        } else if (e.getSource() == zoomOutBtn) {
            mainFrame.getChartPanel().doZoom(2f);
            
        } else if (e.getSource() == aisLayerBtn) {
            mainFrame.getChartPanel().aisVisible(aisLayerBtn.isSelected());
            menuBar.getAisLayer().setSelected(aisLayerBtn.isSelected());

        } else if (e.getSource() == encBtn) {
            mainFrame.getChartPanel().encVisible(encBtn.isSelected());
            menuBar.getEncLayer().setSelected(encBtn.isSelected());

        } else if (e.getSource() == wmsBtn) {
            mainFrame.getChartPanel().wmsVisible(wmsBtn.isSelected());
            
        } else if (e.getSource() == routeManagerBtn) {
            RouteManagerDialog routeManagerDialog = new RouteManagerDialog(
                    mainFrame);
            routeManagerDialog.setVisible(true);
            
        } else if (e.getSource() == setupBtn) {
            mainFrame.openSetupDialog();
            
        } else if (e.getSource() == aisDialogButton) {
            aisDialog.setVisible(true);
            aisDialog.setSelection(-1, true);
            
        } else if (e.getSource() == newRouteBtn) {
            if (mouseDelegator.getActiveMouseModeID() == NavigationMouseMode.MODE_ID
                    || mouseDelegator.getActiveMouseModeID() == DragMouseMode.MODE_ID
                    || mouseDelegator.getActiveMouseModeID() == DistanceCircleMouseMode.MODE_ID) {
                menuBar.getNewRoute().setSelected(true);
                mainFrame.getChartPanel().setMouseMode(
                        RouteEditMouseMode.MODE_ID);
            } else {
                mainFrame.getChartPanel().setMouseMode(
                        NavigationMouseMode.MODE_ID);
                menuBar.getNewRoute().setSelected(false);
            }
            
        } else if (e.getSource() == newRouteBtn) {
            newRoute();
            
        } else if (e.getSource() == aisToggleName) {
            boolean showNameLabels = aisToggleName.isSelected();
            EPDShip.getInstance().getSettings().getAisSettings().setShowNameLabels(showNameLabels);    
            
        } else if (e.getSource() == toggleSafeHaven) {
            routeLayer.toggleSafeHaven();
            
        } else if (e.getSource() == dragMouseMode) {
            mainFrame.getChartPanel().setMouseMode(DragMouseMode.MODE_ID);
            
        } else if (e.getSource() == navigationMouseMode) {
            mainFrame.getChartPanel().setMouseMode(NavigationMouseMode.MODE_ID);
            
        } else if (e.getSource() == centreBtn) {
            mainFrame.saveCentreOnShip();
            
        }
        // react on mouse click on "toggle distance circles mode"
        else if (e.getSource() == this.toggleDistanceCircleMode) {
            if (this.toggleDistanceCircleMode.isSelected()) {
                this.mainFrame.getChartPanel().setMouseMode(
                        CommonDistanceCircleMouseMode.MODE_ID);
            } else {
                // go back to previously active mouse mode
                this.mainFrame.getChartPanel().setMouseMode(
                        ((CommonDistanceCircleMouseMode) this.mainFrame.getChartPanel().getMouseDelegator().getActiveMouseMode())
                                .getPreviousMouseMode());
            }
            
        } else if (e.getSource() == toggleIntendedRoute) {
            boolean visible = toggleIntendedRoute.isSelected();
            mainFrame.getChartPanel().intendedRouteLayerVisible(visible);
            menuBar.getIntendedRouteLayer().setSelected(visible);
        
        }        
        else if (e.getSource() == toggleIntendedRouteFilter) {
            boolean visible = toggleIntendedRouteFilter.isSelected();
            intendedRouteLayer.toggleFilter(visible);
        
        } else if(e.getSource() == this.toggleDynamicPredictorLayer) {
            boolean visible = toggleDynamicPredictorLayer.isSelected();
            mainFrame.getChartPanel().setDynamicPredictorLayerVisibility(visible);
        }
    }

    public ToggleButtonLabel getNavigationMouseMode() {
        return navigationMouseMode;
    }

    public ToggleButtonLabel getDragMouseMode() {
        return dragMouseMode;
    }

    public ToggleButtonLabel getToggleButtonDistanceCircleMouseMode() {
        return this.toggleDistanceCircleMode;
    }

    public ToggleButtonLabel getAisBtn() {
        return aisLayerBtn;
    }

    public ToggleButtonLabel getEncBtn() {
        return encBtn;
    }
    
    public ToggleButtonLabel getIntendedRouteButton() {
        return toggleIntendedRoute;
    }

    public ToggleButtonLabel getAutoFollowBtn() {
        return autoFollowBtn;
    }

    public void zoomIn() {
        mainFrame.getChartPanel().doZoom(0.5f);
    }
    
    public IMapMenuAction getHideAisNamesAction() {
        return hideAisNamesAction;
    }

    /**
     * Function for resizing the icons for the toolbar
     * 
     * @param imgpath
     *            path of the image
     * @return newimage the newly created and resized image
     */
    public ImageIcon toolbarIcon(String imgpath) {
        ImageIcon icon = EPDShip.res().folder("images/toolbar/").getCachedImageIcon(imgpath);

        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(iconWidth, iconHeight,
                java.awt.Image.SCALE_DEFAULT);
        ImageIcon newImage = new ImageIcon(newimg);
        return newImage;
    }

}

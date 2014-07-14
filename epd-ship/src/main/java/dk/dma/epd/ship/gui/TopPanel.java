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
import dk.dma.epd.common.prototype.settings.layers.ENCLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.ENCLayerCommonSettings.ENCColorScheme;
import dk.dma.epd.common.prototype.settings.layers.AisLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.DynamicPredictorLayerSettings;
import dk.dma.epd.common.prototype.settings.layers.IntendedRouteLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.LayerSettings;
import dk.dma.epd.common.prototype.settings.layers.VesselLayerSettings;
import dk.dma.epd.common.prototype.settings.layers.WMSLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.observers.AisLayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.DynamicPredictorLayerSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.ENCLayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.IntendedRouteLayerCommonSettingsListener;
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
        MouseListener, HistoryNavigationPanelInterface, AisLayerCommonSettingsListener, IntendedRouteLayerCommonSettingsListener, 
        ENCLayerCommonSettingsListener, DynamicPredictorLayerSettingsListener {

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
        
        // Register self as observer of global AIS layer settings
        EPDShip.getInstance().getSettings().getPrimaryAisLayerSettings().addObserver(this);
        // Observe global intended route layer settings for changes
        EPDShip.getInstance().getSettings().getPrimaryIntendedRouteLayerSettings().addObserver(this);
        // Observer global dynamic predictor layer settings for changes.
        EPDShip.getInstance().getSettings().getPrimaryDynamicPredictorLayerSettings().addObserver(this);
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
        encBtn.setEnabled(EPDShip.getInstance().getSettings().getENCLayerSettings().isEncInUse());
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
        autoFollowBtn.setSelected(EPDShip.getInstance().getSettings().getMapSettings()
                .isAutoFollow());
        aisLayerBtn.setSelected(EPDShip.getInstance().getSettings().getPrimaryAisLayerSettings().isVisible());
        ENCLayerCommonSettings<?> encLayerSettings = EPDShip.getInstance().getSettings().getENCLayerSettings();
        WMSLayerCommonSettings<?> wmsLayerSettings = EPDShip.getInstance().getSettings().getPrimaryWMSLayerSettings();
        // Updated these two to also check use (previous version only checked layer visibility)
        encBtn.setSelected(encLayerSettings.isEncInUse() && encLayerSettings.isVisible());
        wmsBtn.setSelected(wmsLayerSettings.isUseWms() && wmsLayerSettings.isVisible());
        aisToggleName.setSelected(EPDShip.getInstance().getSettings().getPrimaryAisLayerSettings()
                .isShowVesselNameLabels());

        navigationMouseMode.setSelected(true);
        // range circles mode is disabled by default.
        toggleDistanceCircleMode.setSelected(false);
        
        toggleIntendedRoute.setSelected(EPDShip.getInstance().getSettings().getPrimaryIntendedRouteLayerSettings().isVisible());
        // toggle button according to value stored in settings
        toggleDynamicPredictorLayer.setEnabled(EPDShip.getInstance().getSettings().getPrimaryDynamicPredictorLayerSettings().isEnabled());
        toggleDynamicPredictorLayer.setSelected(EPDShip.getInstance().getSettings().getPrimaryDynamicPredictorLayerSettings().isVisible());
    }

    public void disableAutoFollow() {
        EPDShip.getInstance().getSettings().getMapSettings().setAutoFollow(false);
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
            EPDShip.getInstance().getSettings().getMapSettings()
                    .setAutoFollow(autoFollowBtn.isSelected());
            if (autoFollowBtn.isSelected()) {
                mainFrame.getChartPanel().autoFollow();
            }
            menuBar.getAutoFollow().setSelected(
                    EPDShip.getInstance().getSettings().getMapSettings().isAutoFollow());
            
        }
        else if (e.getSource() == zoomInBtn) {
            mainFrame.getChartPanel().doZoom(0.5f);
            
        } else if (e.getSource() == zoomOutBtn) {
            mainFrame.getChartPanel().doZoom(2f);
            
        } else if (e.getSource() == aisLayerBtn) {
            mainFrame.getChartPanel().aisVisible(aisLayerBtn.isSelected());

        } else if (e.getSource() == encBtn) {
//            mainFrame.getChartPanel().encVisible(encBtn.isSelected());
//            menuBar.getEncLayer().setSelected(encBtn.isSelected());
            // Toggle global ENC layer settings
            EPDShip.getInstance().getSettings().getENCLayerSettings().setVisible(!EPDShip.getInstance().getSettings().getENCLayerSettings().isVisible());

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
            EPDShip.getInstance().getSettings().getPrimaryAisLayerSettings().setShowVesselNameLabels(showNameLabels);    
            
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
            mainFrame.getChartPanel().intendedRouteLayerVisible(toggleIntendedRoute.isSelected());    
        }        
        else if (e.getSource() == toggleIntendedRouteFilter) {
            intendedRouteLayer.getSettings().setIntendedRouteFilterInUse(toggleIntendedRouteFilter.isSelected());
        
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

    public ToggleButtonLabel getEncBtn() {
        return encBtn;
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

    /*
     * [Begin settings listener methods]
     */
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void showVesselNameLabelsChanged(VesselLayerSettings<?> source, boolean show) {
        if (source == EPDShip.getInstance().getSettings().getPrimaryAisLayerSettings()) {
            // Name labels toggled for AIS layer.
            // Update toggle button to reflect this.
            this.aisToggleName.setSelected(show);
        }
    }

    @Override
    public void movementVectorLengthMinChanged(VesselLayerSettings<?> source, int newMinLengthMinutes) {
        // Not relevant for TopPanel.
    }

    @Override
    public void movementVectorLengthMaxChanged(VesselLayerSettings<?> source, int newMaxLengthMinutes) {
        // Not relevant for TopPanel.
    }

    @Override
    public void movementVectorLengthStepSizeChanged(VesselLayerSettings<?> source, float newStepSize) {
        // Not relevant for TopPanel.
    }

    @Override
    public void movementVectorHideBelowChanged(VesselLayerSettings<?> source, float newMinSpeed) {
        // Not relevant for TopPanel.
    }

    @Override
    public void isVisibleChanged(LayerSettings<?> source, boolean newValue) {
        if (source == EPDShip.getInstance().getSettings().getPrimaryAisLayerSettings()) {
            /*
             * AIS layer visibility toggled.
             * Update toggle button accordingly.
             */
            aisLayerBtn.setSelected(newValue);
        } else if (source == EPDShip.getInstance().getSettings().getPrimaryIntendedRouteLayerSettings()) {
            /*
             * Intended route layer visibility toggled.
             * Update toggle button accordingly.
             */
            toggleIntendedRoute.setSelected(newValue);
        } else if (source == EPDShip.getInstance().getSettings().getENCLayerSettings()) {
            /*
             * ENC layer visibility toggled.
             * Update toggle button accordingly.
             */
            encBtn.setSelected(newValue);
        } else if (source == EPDShip.getInstance().getSettings().getPrimaryDynamicPredictorLayerSettings()) {
            /*
             * Dynamic predictor layer visibility toggled.
             * Update toggle button accordingly.
             */
            toggleDynamicPredictorLayer.setSelected(newValue);
        }
    }

    @Override
    public void onEnabledChanged(DynamicPredictorLayerSettings source,
            boolean enabled) {
        if (source == EPDShip.getInstance().getSettings().getPrimaryDynamicPredictorLayerSettings()) {
            toggleDynamicPredictorLayer.setEnabled(enabled);
        }
    }
    
    @Override
    public void showAllPastTracksChanged(AisLayerCommonSettings<?> source, boolean newValue) {
        // Not relevant for TopPanel.   
    }

    @Override
    public void layerRedrawIntervalChanged(AisLayerCommonSettings<?> source, int newValue) {
        // Not relevant for TopPanel.
    }

    @Override
    public void showArrowScaleChanged(float maxScaleForArrowDisplay) {
        // Not relevant for TopPanel.
    }

    @Override
    public void routeWidthChanged(float routeWidth) {
        // Not relevant for TopPanel.
    }

    @Override
    public void isIntendedRouteFilterInUseChanged(IntendedRouteLayerCommonSettings<?> source, boolean useFilter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void isEncInUseChanged(boolean useEnc) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void isS52ShowTextChanged(boolean showText) {
     // Not relevant for TopPanel.
    }

    @Override
    public void isS52ShallowPatternChanged(boolean useShallowPattern) {
     // Not relevant for TopPanel.
    }

    @Override
    public void s52ShallowContourChanged(int shallowContour) {
     // Not relevant for TopPanel.
    }

    @Override
    public void s52SafetyDepthChanged(int safetyDepth) {
     // Not relevant for TopPanel.
    }

    @Override
    public void s52SafetyContourChanged(int safetyContour) {
     // Not relevant for TopPanel.
    }

    @Override
    public void s52DeepContourChanged(int deepContour) {
     // Not relevant for TopPanel.
    }

    @Override
    public void isUseSimplePointSymbolsChanged(boolean useSimplePointSymbols) {
     // Not relevant for TopPanel.
    }

    @Override
    public void isUsePlainAreasChanged(boolean usePlainAreas) {
     // Not relevant for TopPanel.
    }

    @Override
    public void isS52TwoShadesChanged(boolean s52TwoShades) {
     // Not relevant for TopPanel.
    }

    @Override
    public void encColorSchemeChanged(ENCColorScheme newScheme) {
     // Not relevant for TopPanel.
    }

    /*
     * [End settings listener methods]
     */
    
}

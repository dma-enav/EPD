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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import dk.dma.epd.common.prototype.event.HistoryListener;
import dk.dma.epd.common.prototype.event.HistoryNavigationPanelInterface;
import dk.dma.epd.common.prototype.gui.GoBackButton;
import dk.dma.epd.common.prototype.gui.GoForwardButton;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.layers.ais.AisLayerCommon;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLayerCommon;
import dk.dma.epd.common.prototype.layers.wms.WMSLayer;
import dk.dma.epd.common.prototype.settings.layers.AisLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.IntendedRouteLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.LayerSettings;
import dk.dma.epd.common.prototype.settings.layers.VesselLayerSettings;
import dk.dma.epd.common.prototype.settings.layers.WMSLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.observers.AisLayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.IntendedRouteLayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.MSILayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.VoyageLayerCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.observers.WMSLayerCommonSettingsListener;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.utils.ToolItemGroup;
import dk.dma.epd.shore.layers.ais.AisLayer;
import dk.dma.epd.shore.layers.msi.MsiLayer;
import dk.dma.epd.shore.layers.route.RouteLayer;
import dk.dma.epd.shore.layers.voyage.EmbeddedInfoPanelMoveMouseListener;
import dk.dma.epd.shore.layers.voyage.VoyageLayer;

public class LayerTogglingPanel extends JPanel implements MouseListener,
        HistoryNavigationPanelInterface, AisLayerCommonSettingsListener, IntendedRouteLayerCommonSettingsListener,
        WMSLayerCommonSettingsListener, MSILayerCommonSettingsListener, VoyageLayerCommonSettingsListener {

    private static final long serialVersionUID = 1L;
    private JPanel moveHandler;
    private JPanel masterPanel;

    private JPanel buttonPanel;
    private static int moveHandlerHeight = 18;
    private static int toolItemSize = 35;
    private static int toolItemColumns = 3;
    private static int buttonPanelOffset = 4;
    public int width;
    public int height;
    private static int iconWidth = 16;
    private static int iconHeight = 16;
    private Border toolPaddingBorder = BorderFactory.createMatteBorder(3, 3, 3, 3, new Color(83, 83, 83));
    private Border toolInnerEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(37, 37, 37), new Color(
            52, 52, 52));

    // private JPanel notificationPanel;

    JLabel lblLayerTitle;
    JLabel lblETA;
    JLabel OpenVpDetalsBtn;
    JLabel HideOtherVoyagesBtn;

    ChartPanel chartPanel;

    private ToolItemGroup toolItemGroups = new ToolItemGroup();
    private ToolItemGroup functionItemGroups = new ToolItemGroup();

    JMapFrame parent;

    JLabel wms;
    JLabel enc;
    JLabel msi;
    JLabel ais;
    JLabel aisNameLabels;
    JLabel intendedRoutes;
    JLabel intendedRoutesFilter;
    JLabel routes;
    JLabel voyages;
    private GoBackButton goBckBtn;
    private GoForwardButton goFrwrdBtn;

    /**
     * Create the panel.
     * 
     * @param voyage
     */
    public LayerTogglingPanel() {
        super();

        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45, 45)));

        setBackground(new Color(83, 83, 83));
        setLayout(null);

        // Create the top movehandler (for dragging)
        moveHandler = new JPanel(new BorderLayout());
        moveHandler.add(new JLabel("Layer Functions", SwingConstants.CENTER), BorderLayout.CENTER);
        moveHandler.setForeground(new Color(200, 200, 200));
        moveHandler.setOpaque(true);
        moveHandler.setBackground(Color.DARK_GRAY);
        moveHandler.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 30, 30)));
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setPreferredSize(new Dimension(208, moveHandlerHeight));

        JLabel close = new JLabel(EPDShore.res().getCachedImageIcon("images/window/close.png"));
        close.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setVisible(false);
            }});
        close.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        moveHandler.add(close, BorderLayout.EAST);
        
        // Create the grid for the toolitems
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        buttonPanel.setBackground(new Color(83, 83, 83));
        
        goBckBtn = new GoBackButton();
        goFrwrdBtn = new GoForwardButton();
        
        this.addMouseListener(this);

        // Create the masterpanel for aligning
        masterPanel = new JPanel(new BorderLayout());

        // masterPanel.setBounds(0, 0, 208, 300);

        masterPanel.add(moveHandler, BorderLayout.NORTH);
        masterPanel.add(buttonPanel, BorderLayout.SOUTH);
        masterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45, 45)));

        add(masterPanel);

    }

//    private void toggleLayerButton(Layer layer, JLabel label) {
//        if (layer.isVisible()) {
//            setActiveToolItem(label);
//        } else {
//            setInactiveToolItem(label);
//        }
//
//    }
    
    private void toggleLayerButton(boolean toggled, JLabel label) {
        if (toggled) {
            setActiveToolItem(label);
        } else {
            setInactiveToolItem(label);
        }
    }

    private void addWMS(final WMSLayer wmsLayer) {
        // Tool: WMS layer
        wms = new JLabel(toolbarIcon("images/toolbar/wms_small.png"));
        wms.setName("wms");
        wms.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                // Toggle WMS layer visibility.
                // Toggle button is toggled on/off in listener method.
                wmsLayer.getSettings().setVisible(!wmsLayer.getSettings().isVisible());
            }
        });
        wms.setToolTipText("Show/hide WMS seacharts");
        toggleLayerButton(wmsLayer.getSettings().isVisible(), this.wms);
        // group.add(wms);
        toolItemGroups.addToolItem(wms);
    }

    private void addEnc() {

        enc = new JLabel(toolbarIcon("images/toolbar/map-medium.png"));

        enc.setName("enc");
        enc.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (enc.isEnabled()) {

                    chartPanel.getEncLayerSettings().setVisible(!chartPanel.getEncLayerSettings().isVisible());
//                    Layer encLayer = chartPanel.getEncLayer();
//
//                    if (encLayer != null) {
//                        // Set the visibility, but do not persist the settings
//                        chartPanel.encVisible(!chartPanel.isEncVisible(), false);
//                    }

                }
            }
        });
        enc.setToolTipText("Show/hide ENC");

        enc.setEnabled(chartPanel.getEncLayerSettings().isEncInUse());
        // TODO this check may not be correct for default values when there is no dongle....
        if (chartPanel.getEncLayerSettings().isVisible()) {
            setActiveToolItem(enc);
        }
        toolItemGroups.addToolItem(enc);
        
        
        repaintToolbar();
        
        
        
    }

    private void addMSI(final MsiLayer msiLayer) {
        // Tool: MSI layer
        msi = new JLabel(toolbarIcon("images/toolbar/msi_symbol_16.png"));
        msi.setName("msi");
        msi.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                // Toggle MSI layer visibility
                // Toggle button is toggled on/off in listener method.
                msiLayer.getSettings().setVisible(!msiLayer.getSettings().isVisible());
            }
        });
        msi.setToolTipText("Show/hide maritime safety information");

        toggleLayerButton(msiLayer.getSettings().isVisible(), this.msi);

        toolItemGroups.addToolItem(msi);
    }

    private void addAIS(final AisLayer aisLayer) {
        // Tool: AIS Layer
        ais = new JLabel(toolbarIcon("images/toolbar/board-game.png"));
        ais.setName("ais");
        ais.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                // Toggle visibility on settings.
                // Toggle button is toggled on/off as part of settings listener method.
                aisLayer.getSettings().setVisible(!aisLayer.getSettings().isVisible());
            }
        });
        ais.setToolTipText("Show/hide AIS Layer");

        toggleLayerButton(aisLayer.getSettings().isVisible(), this.ais);
        toolItemGroups.addToolItem(ais);
    }

    private void addIntendedRoutes(final IntendedRouteLayerCommon intendedRouteLayer) {
        // Tool: Intended Routes Layer
        intendedRoutes = new JLabel(toolbarIcon("images/toolbar/direction.png"));
        intendedRoutes.setName("intendedroutes");
        intendedRoutes.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                // Toggle visibility on settings.
                // Toggle button is toggled on/off as part of settings listener method.
                intendedRouteLayer.getSettings().setVisible(!intendedRouteLayer.getSettings().isVisible());
            }
        });
        intendedRoutes.setToolTipText("Show/hide intended routes");

        // group.add(wms);
        toolItemGroups.addToolItem(intendedRoutes);
        toggleLayerButton(intendedRouteLayer.getSettings().isVisible(), this.intendedRoutes);
    }

    private void addRoutes(final RouteLayer routeLayer) {
        // Tool: Routes Layer
        routes = new JLabel(toolbarIcon("images/toolbar/marker.png"));
        routes.setName("routes");
        routes.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                // Toggle visibility on settings
                // Toggle button is toggled on/off as part of settings listener method.
                routeLayer.getSettings().setVisible(!routeLayer.getSettings().isVisible());
            }
        });
        routes.setToolTipText("Show/hide Routes");
        toggleLayerButton(routeLayer.getSettings().isVisible(), this.routes);

        // group.add(wms);
        toolItemGroups.addToolItem(routes);
    }

    private void addVoyages(final VoyageLayer voyageLayer) {
        // Tool: voyage Layer
        voyages = new JLabel(toolbarIcon("images/toolbar/marker_green.png"));
        voyages.setName("voyages");
        voyages.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                // Toggle visibility on settings.
                // Toggle button is toggled on/off as part of settings listener method.
                voyageLayer.getSettings().setVisible(!voyageLayer.getSettings().isVisible());
            }
        });
        voyages.setToolTipText("Show/hide Strategic Voyages");
        toggleLayerButton(voyageLayer.getSettings().isVisible(), voyages);

        // group.add(wms);
        toolItemGroups.addToolItem(voyages);
    }

    private void addAISNameLabels(final AisLayer layer) {
        // Tool: ais name labels
        aisNameLabels = new JLabel(toolbarIcon("images/toolbar/edit-letter-spacing.png"));
        aisNameLabels.setName("aisnamelabels");
        aisNameLabels.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                // Toggle namelabels on settings
                // Toggle button is toggled on/off as part of the settings listener method
                layer.getSettings().setShowVesselNameLabels(!layer.getSettings().isShowVesselNameLabels());
            }
        });
        aisNameLabels.setToolTipText("Show/hide AIS Name Labels");

        toggleLayerButton(layer.getSettings().isShowVesselNameLabels(), aisNameLabels);

        functionItemGroups.addToolItem(aisNameLabels);
    }

    private void addIntendedRoutesFilter(final IntendedRouteLayerCommon layer) {
        // Tool: Intended Route Layer
        this.intendedRoutesFilter = new JLabel(toolbarIcon("images/toolbar/road-sign.png"));
        intendedRoutesFilter.setName("intendedroutesfilter");
        intendedRoutesFilter.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                // Toggle setting.
                // Toggle button is toggled on/off as part of settings listener method.
                layer.getSettings().setIntendedRouteFilterInUse(!layer.getSettings().isIntendedRouteFilterInUse());
            }
        });
        intendedRoutesFilter.setToolTipText("Toggle Intended Route Filter");
        // update button to reflect initial value of setting
        this.toggleLayerButton(layer.getSettings().isIntendedRouteFilterInUse(), intendedRoutesFilter);
        functionItemGroups.addToolItem(intendedRoutesFilter);
    }

    private void addPastTrack(final AisLayer aisLayer) {
        // Tool: AIS past tracks toggle
        final JLabel aisPastTrack = new JLabel(toolbarIcon("images/toolbar/pasttrack.png"));
        aisPastTrack.setName("pastrack");
        aisPastTrack.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                // TODO why is this empty?
            }
        });
        aisPastTrack.setToolTipText("Show/hide Past Track");
        // TODO why inactive + disabled?
        setInactiveToolItem(aisPastTrack);
        aisPastTrack.setEnabled(false);

        functionItemGroups.addToolItem(aisPastTrack);
    }

    public void checkPosition() {
        if (parent != null) {

            if (parent.getWidth() != 0 || parent.getHeight() != 0) {
                if (this.getLocation().getX() > parent.getWidth()) {
                    this.setLocation(parent.getWidth() - this.getWidth() + 2, (int) this.getLocation().getY());
                }

                if (this.getLocation().getY() + this.getHeight() > parent.getHeight()) {
                    int y = (int) parent.getHeight() - this.getHeight() + 2;
                    if (y < 0) {
                        y = 18;
                    }

                    this.setLocation((int) this.getLocation().getX(), y);
                }
            }
        }
    }


    public void setParent(JMapFrame parent) {
        this.parent = parent;

        EmbeddedInfoPanelMoveMouseListener mml = new EmbeddedInfoPanelMoveMouseListener(this, parent);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);

    }

    public void setChartPanel(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
        this.chartPanel.setHistoryListener(new HistoryListener(this.chartPanel));
        this.chartPanel.getMap().addProjectionListener(this.chartPanel.getHistoryListener());
        this.chartPanel.getHistoryListener().setNavigationPanel(this);
        
        if (chartPanel.getEncLayer() != null) {
//            chartPanel.getEncVisibilityAdapter().addVisibilityListener(this);
            addEnc();
        }
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent arg0) {

    }

    @Override
    public void mousePressed(MouseEvent arg0) {

    }
    
    public void addLayerFunctionality(EPDLayerCommon layer) {
        if (layer instanceof AisLayerCommon) {
            addAIS((AisLayer) layer);
            addAISNameLabels((AisLayer) layer);
            addPastTrack((AisLayer) layer);
            ((AisLayer)layer).getSettings().addObserver(this);
        } else if (layer instanceof WMSLayer) {
            addWMS((WMSLayer)layer);
            ((WMSLayer)layer).getSettings().addObserver(this);
//            layer.addVisibilityListener(this);
        } else if (layer instanceof MsiLayer) {
            addMSI((MsiLayer)layer);
            ((MsiLayer)layer).getSettings().addObserver(this);
//            layer.addVisibilityListener(this);
        } else if (layer instanceof RouteLayer) {
            addRoutes((RouteLayer)layer);
            ((RouteLayer)layer).getSettings().addObserver(this);
//            layer.addVisibilityListener(this);
        } else if (layer instanceof IntendedRouteLayerCommon) {
            addIntendedRoutes((IntendedRouteLayerCommon)layer);
            addIntendedRoutesFilter((IntendedRouteLayerCommon) layer);
            ((IntendedRouteLayerCommon)layer).getSettings().addObserver(this);
//            layer.addVisibilityListener(this);
        } else if (layer instanceof VoyageLayer) {
            addVoyages((VoyageLayer)layer);
            ((VoyageLayer)layer).getSettings().addObserver(this);
//            layer.addVisibilityListener(this);
        }

        repaintToolbar();
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
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
        Image newimg = img.getScaledInstance(iconWidth, iconHeight, java.awt.Image.SCALE_DEFAULT);
        ImageIcon newImage = new ImageIcon(newimg);
        return newImage;
    }

    /**
     * Function for refreshing the toolbar after editing toolitems
     */
    public void repaintToolbar() {

        buttonPanel.removeAll();
        buttonPanel.updateUI();

        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new GridLayout(0, 2));
        navigationPanel.setOpaque(false);

        this.goBckBtn.setEnabled(false);
        this.goFrwrdBtn.setEnabled(false);
        
        navigationPanel.add(this.goBckBtn);
        navigationPanel.add(this.goFrwrdBtn);
        
        buttonPanel.add(navigationPanel);
        
        JSeparator sep1 = new JSeparator();
        buttonPanel.add(sep1);
        
        width = toolItemSize * toolItemColumns;
        height = 0;

        // Add the tool item group
        JPanel group = new JPanel();
        group.setLayout(new GridLayout(0, toolItemColumns));
        group.setOpaque(false);

        double temp = (double) toolItemGroups.getToolItems().size() / (double) toolItemColumns;
        int innerHeight = (int) (Math.ceil(temp) * (toolItemSize - 3));

        height = height + (int) (Math.ceil(temp) * (toolItemSize - 1));

        group.setSize(width, innerHeight);
        group.setPreferredSize(new Dimension(width, innerHeight));

        // Populate it with tool items
        ArrayList<JLabel> items = toolItemGroups.getToolItems();
        for (int t = 0; t < items.size(); t++) {
            JLabel item = items.get(t);
            group.add(item);
        }

        buttonPanel.add(group);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(65, 65, 65));
        sep.setBackground(new Color(83, 83, 83));
        buttonPanel.add(sep);

        height = height + 7;

        // Add the function item group
        JPanel groupFunctions = new JPanel();
        groupFunctions.setLayout(new GridLayout(0, toolItemColumns));
        groupFunctions.setOpaque(false);

        temp = (double) functionItemGroups.getToolItems().size() / (double) toolItemColumns;
        innerHeight = (int) (Math.ceil(temp) * (toolItemSize - 3));

        height = height + (int) (Math.ceil(temp) * (toolItemSize - 1));

        groupFunctions.setSize(width, innerHeight);
        groupFunctions.setPreferredSize(new Dimension(width, innerHeight));

        // Populate it with function items
        ArrayList<JLabel> itemsFunction = functionItemGroups.getToolItems();
        for (int t = 0; t < itemsFunction.size(); t++) {
            JLabel item = itemsFunction.get(t);
            groupFunctions.add(item);
        }

        buttonPanel.add(groupFunctions);
        

        int innerHeight3 = height + toolItemSize + 7;

        // And finally set the size and repaint it
        buttonPanel.setSize(width, innerHeight3 - buttonPanelOffset);
        buttonPanel.setPreferredSize(new Dimension(width, innerHeight3 - buttonPanelOffset));
        buttonPanel.setLocation(0, moveHandlerHeight);

        height = height + moveHandlerHeight + toolItemSize + 7;

        masterPanel.setSize(width, height);
        this.setBounds(0, 200, width, height);
        
        this.revalidate();
        this.repaint();
    }

    public void setInactiveToolItem(JLabel toolItem) {
        toolItem.setBorder(toolPaddingBorder);
        toolItem.setOpaque(false);
    }

    /**
     * Function for setting the active tool item in the toolbar
     * 
     * @param tool
     *            reference to the active tool
     */
    public void setActiveToolItem(JLabel toolItem) {
        // Set active tool
        toolItem.setBackground(new Color(55, 55, 55));
        toolItem.setBorder(BorderFactory.createCompoundBorder(toolPaddingBorder, toolInnerEtchedBorder));
        toolItem.setOpaque(true);
    }

//    @Override
//    public void visibilityChanged(Layer layer) {
//        // Handle the toggling of buttons based on global changes
//        if (layer instanceof WMSLayer) {
//            toggleLayerButton(layer, wms);
//        }
//
//        if (layer instanceof MsiLayer) {
//            toggleLayerButton(layer, msi);
//        }
//        if (layer instanceof AisLayer) {
//            toggleLayerButton(layer, ais);
//        }
//        if (layer instanceof IntendedRouteLayerCommon) {
//            toggleLayerButton(layer, intendedRoutes);
//        }
//
//        if (layer instanceof RouteLayer) {
//            toggleLayerButton(layer, routes);
//        }
//        if (layer instanceof VoyageLayer) {
//            toggleLayerButton(layer, voyages);
//        }
//
//        if (layer != null && chartPanel != null && layer == chartPanel.getEncLayer()) {
//            if (chartPanel.isEncVisible()) {
//                setActiveToolItem(enc);
//            } else {
//                setInactiveToolItem(enc);
//            }
//        }
//    }

    @Override
    public GoBackButton getGoBackButton() {
        return this.goBckBtn;
    }

    @Override
    public GoForwardButton getGoForwardButton() {
        return this.goFrwrdBtn;
    }
    
    public HistoryListener getHistoryListener() {
        return this.chartPanel.getHistoryListener();
    }

    /*
     * Begin [Settings listener methods]
     */
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void showVesselNameLabelsChanged(VesselLayerSettings<?> source,
            boolean show) {
        if(source instanceof AisLayerCommonSettings<?>) {
            // Observed AIS layer had name labels toggled on/off
            if (show) {
                setActiveToolItem(aisNameLabels);
            } else {
                setInactiveToolItem(aisNameLabels);
            }
        }
    }

    @Override
    public void movementVectorLengthMinChanged(VesselLayerSettings<?> source,
            int newMinLengthMinutes) {
        // Not relevant for this class.
    }

    @Override
    public void movementVectorLengthMaxChanged(VesselLayerSettings<?> source,
            int newMaxLengthMinutes) {
        // Not relevant for this class.
    }

    @Override
    public void movementVectorLengthStepSizeChanged(
            VesselLayerSettings<?> source, float newStepSize) {
        // Not relevant for this class.
    }

    @Override
    public void movementVectorHideBelowChanged(VesselLayerSettings<?> source,
            float newMinSpeed) {
        // Not relevant for this class.
    }

    @Override
    public void isVisibleChanged(LayerSettings<?> source, boolean newValue) {
        if(this.chartPanel == null) {
            return;
        }
        // Find out what observed layer settings had its visibility toggled
        if (source == chartPanel.getAisLayer().getSettings()) {
            // Observed AIS layer had visibility toggled on/off
            this.toggleLayerButton(newValue, this.ais);
        } else if (chartPanel.getWmsLayer() != null && source == chartPanel.getWmsLayer().getSettings()) {
            // Observed WMS layer had visibility toggled on/off
            this.toggleLayerButton(newValue, this.wms);
        } else if (source == chartPanel.getMsiLayer().getSettings()) {
            // Observed MSI layer had visibility toggled on/off
            this.toggleLayerButton(newValue, this.msi);
        } else if (source == chartPanel.getIntendedRouteLayer().getSettings()) {
            // Observed Intended Route Layer had visibility toggled on/off
            this.toggleLayerButton(newValue, this.intendedRoutes);
        } else if (source == chartPanel.getRouteLayer().getSettings()) {
            // Observed route layer had visibility toggled on/off
            this.toggleLayerButton(newValue, this.routes);
        } else if (source == chartPanel.getVoyageLayer().getSettings()) {
            // Observed voyage layer had visibility toggled on/off
            this.toggleLayerButton(newValue, this.voyages);
        }
        // TODO add instanceof checks for other layers
    }

    @Override
    public void showAllPastTracksChanged(AisLayerCommonSettings<?> source, boolean newValue) {
        // TODO implement to toggle past track display toggle button.        
    }

    @Override
    public void layerRedrawIntervalChanged(AisLayerCommonSettings<?> source, int newValue) {
        // Not relevant for this class.
    }

    @Override
    public void showArrowScaleChanged(float maxScaleForArrowDisplay) {
        // Not relevant for this class.
    }

    @Override
    public void routeWidthChanged(float routeWidth) {
        // Not relevant for this class.
    }

    @Override
    public void isIntendedRouteFilterInUseChanged(IntendedRouteLayerCommonSettings<?> source, boolean useFilter) {
        // TODO implement source check, toggle button according to useFilter
        if(chartPanel == null || chartPanel.getIntendedRouteLayer() == null) {
            return;
        }
        
        if (source == chartPanel.getIntendedRouteLayer().getSettings()) {
            // Observed intended route layer had its filter setting toggled
            this.toggleLayerButton(useFilter, this.intendedRoutesFilter);
        }
    }

    @Override
    public void isUseWmsChanged(WMSLayerCommonSettings<?> source, boolean useWms) {
        // TODO enable/disable WMS layer toggle button?
    }

    @Override
    public void wmsQueryChanged(WMSLayerCommonSettings<?> source,
            String wmsQuery) {
        // Not relevant for this class.
    }

    @Override
    public void msiTextboxesVisibleAtScaleChanged(int scale) {
        // Not relevant for this class.
    }

    @Override
    public void msiVisibilityFromNewWaypointChanged(double newValue) {
        // Not relevant for this class.
    }
    
    /*
     * End [Settings listener methods]
     */
}

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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;

import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.gui.ais.AisDialog;
import dk.dma.epd.ship.gui.msi.MsiDialog;
import dk.dma.epd.ship.gui.route.RouteManagerDialog;
import dk.dma.epd.ship.layers.ais.AisLayer;
import dk.dma.epd.ship.layers.route.RouteLayer;

/**
 * The top buttons panel
 */
public class TopPanel extends OMComponentPanel implements ActionListener, MouseListener {

    private static final long serialVersionUID = 1L;

    private final ButtonLabel zoomInBtn = new ButtonLabel(toolbarIcon("images/toolbar/magnifier-zoom-in.png"));
    private final ButtonLabel zoomOutBtn = new ButtonLabel(toolbarIcon("images/toolbar/magnifier-zoom-out.png"));
    private final ButtonLabel centreBtn = new ButtonLabel(toolbarIcon("images/toolbar/arrow-in.png"));
    private final ToggleButtonLabel autoFollowBtn = new ToggleButtonLabel(
            toolbarIcon("images/toolbar/arrow-curve-000-double.png"));
    private final ButtonLabel setupBtn = new ButtonLabel(toolbarIcon("images/toolbar/wrench.png"));
    // private ToggleButtonLabel routeBtn = new
    // ToggleButtonLabel(toolbarIcon("images/toolbar/marker--plus.png"));
    private final ButtonLabel routeManagerBtn = new ButtonLabel(toolbarIcon("images/toolbar/marker.png"));
    private final ButtonLabel msiButton = new ButtonLabel(toolbarIcon("images/toolbar/msi_symbol_16.png"));
    private final ButtonLabel aisButton = new ButtonLabel(toolbarIcon("images/toolbar/radar.png"));
    private final ToggleButtonLabel aisToggleName = new ToggleButtonLabel(
            toolbarIcon("images/toolbar/edit-letter-spacing.png"));
    // private ToggleButtonLabel nogoButton = new
    // ToggleButtonLabel("Toggle NoGo");
    private final ToggleButtonLabel aisBtn = new ToggleButtonLabel(toolbarIcon("images/toolbar/board-game.png"));
    // private ToggleButtonLabel riskBtn = new ToggleButtonLabel("Risk");
    private final ToggleButtonLabel encBtn = new ToggleButtonLabel(toolbarIcon("images/toolbar/map-medium.png"));
    private final ToggleButtonLabel wmsBtn = new ToggleButtonLabel(toolbarIcon("images/toolbar/map-medium.png"));
    private final ToggleButtonLabel newRouteBtn = new ToggleButtonLabel(toolbarIcon("images/toolbar/marker--plus.png"));
    private final ToggleButtonLabel toggleSafeHaven = new ToggleButtonLabel(
            toolbarIcon("images/toolbar/document-resize-actual.png"));

    
    private final ToggleButtonLabel navigationMouseMode = new ToggleButtonLabel(toolbarIcon("images/toolbar/zoom.png"));
    private final ToggleButtonLabel dragMouseMode = new ToggleButtonLabel(
            toolbarIcon("images/toolbar/drag.png"));
    // private final ToggleButtonLabel tglbtnMsiFilter = new ToggleButtonLabel(
    // "MSI filter");

    // private ToggleButtonLabel lockFrames = new ToggleButtonLabel(
    // "Lock/Unlock UI");

    private MainFrame mainFrame;
    private MsiDialog msiDialog;
    private AisDialog aisDialog;
    private MenuBar menuBar;
    private AisLayer aisLayer;
    private RouteLayer routeLayer;

    private MouseDelegator mouseDelegator;

    // private MsiHandler msiHandler;
    // private NogoHandler nogoHandler;
    // private BlinkingLabel msiIcon;
    // private int notifyMsgId = -1;

    private static int iconWidth = 16;
    private static int iconHeight = 16;

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
        // routeBtn.setToolTipText("New route");
        // routeBtn.setVisible(false);
        newRouteBtn.setToolTipText("Add a new route : Shortcut Ctrl N");
        routeManagerBtn.setToolTipText("Routes Manager : Shortcut Ctrl R");
        msiButton.setToolTipText("Maritime Safety Information : Shortcut Ctrl M");
        aisButton.setToolTipText("Show nearby vessels : Shortcut Ctrl A");
        // nogoButton.setToolTipText("Show/hide NoGo area");
        aisBtn.setToolTipText("Show/hide AIS targets");
        aisToggleName.setToolTipText("Show/hide AIS Name Labels");
        // riskBtn.setToolTipText("Show/hide risk info");
        encBtn.setToolTipText("Show/hide ENC");
        
        wmsBtn.setToolTipText("Show/hide WMS seacharts");
        // tglbtnMsiFilter
        // .setToolTipText("Enable/disable MSI message filtering based on position and routes");

        toggleSafeHaven.setToolTipText("Show/hide SafeHaven guidelines");

        // Temporary
        // boolean showRiskAndNogo = !EeINS.getSettings().getGuiSettings()
        // .isRiskNogoDisabled();

        add(zoomInBtn);
        add(zoomOutBtn);
        add(navigationMouseMode);
        add(dragMouseMode);
        add(centreBtn);
        add(autoFollowBtn);
        add(setupBtn);
        // add(routeBtn);
        add(newRouteBtn);
        add(routeManagerBtn);
        add(msiButton);
        add(aisButton);
        add(new JSeparator());
        add(aisBtn);
        add(aisToggleName);
        add(encBtn);
        add(wmsBtn);
        add(toggleSafeHaven);
        // add(tglbtnMsiFilter);
        // if (showRiskAndNogo)
        // add(riskBtn);
        // if (showRiskAndNogo)
        // add(nogoButton);

        // add(lockFrames);

        Component horizontalStrut = Box.createHorizontalStrut(5);
        // JSeparator separator = new JSeparator();
        // separator.setOrientation(SwingConstants.VERTICAL);
        horizontalStrut = Box.createHorizontalStrut(5);

        ImageIcon[] msiAnim = new ImageIcon[2];
        msiAnim[0] = new ImageIcon(EPDShip.class.getResource("/images/toppanel/msi_symbol_64x20.png"));
        msiAnim[1] = new ImageIcon(EPDShip.class.getResource("/images/toppanel/blank64x20.png"));
        // msiIcon = new BlinkingLabel(400, msiAnim);

        add(horizontalStrut);
        // add(separator);
        // add(horizontalStrut);
        // add(msiIcon);
        // msiIcon.setVisible(false);

        // msiIcon.addMouseListener(this);
        zoomInBtn.addMouseListener(this);

        zoomOutBtn.addMouseListener(this);
        centreBtn.addMouseListener(this);
        autoFollowBtn.addMouseListener(this);
        setupBtn.addMouseListener(this);
        // routeBtn.addMouseListener(this);
        newRouteBtn.addMouseListener(this);
        routeManagerBtn.addMouseListener(this);
        msiButton.addMouseListener(this);
        aisButton.addMouseListener(this);
        // nogoButton.addMouseListener(this);
        aisBtn.addMouseListener(this);
        // riskBtn.addMouseListener(this);
        encBtn.addMouseListener(this);
        wmsBtn.addMouseListener(this);
        aisToggleName.addMouseListener(this);
        // tglbtnMsiFilter.addMouseListener(this);
        // lockFrames.addMouseListener(this);

        // lockFrames.setSelected(true);
        // nogoButton.setSelected(true);

        toggleSafeHaven.addMouseListener(this);
        
        navigationMouseMode.addMouseListener(this);
        dragMouseMode.addMouseListener(this);

        
        
        
        updateButtons();
    }

    public void updateButtons() {
        autoFollowBtn.setSelected(EPDShip.getSettings().getNavSettings().isAutoFollow());
        aisBtn.setSelected(EPDShip.getSettings().getAisSettings().isVisible());
        encBtn.setSelected(EPDShip.getSettings().getMapSettings().isEncVisible());
        // tglbtnMsiFilter.setSelected(EeINS.getSettings().getEnavSettings()
        // .isMsiFilter());
        aisToggleName.setSelected(EPDShip.getSettings().getAisSettings().isShowNameLabels());

        navigationMouseMode.setSelected(true);
    }

    public void disableAutoFollow() {
        EPDShip.getSettings().getNavSettings().setAutoFollow(false);
        if (autoFollowBtn.isSelected()) {
            autoFollowBtn.setSelected(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void newRoute() {
        if (mouseDelegator.getActiveMouseModeID() == NavigationMouseMode.MODE_ID || mouseDelegator.getActiveMouseModeID() == DragMouseMode.MODE_ID)  {
            mainFrame.getChartPanel().setMouseMode(0);
        } else {
            mainFrame.getChartPanel().setMouseMode(1);
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
        if (obj instanceof MsiDialog) {
            msiDialog = (MsiDialog) obj;
        }
        if (obj instanceof MouseDelegator) {
            mouseDelegator = (MouseDelegator) obj;
        }
        // if (obj instanceof MsiHandler) {
        // msiHandler = (MsiHandler) obj;
        // }
        if (obj instanceof AisDialog) {
            aisDialog = (AisDialog) obj;
        }
        if (obj instanceof AisLayer) {
            aisLayer = (AisLayer) obj;
        }
        if (obj instanceof MenuBar) {
            menuBar = (MenuBar) obj;
        }
        if (obj instanceof RouteLayer) {
            routeLayer = (RouteLayer) obj;
        }
    }

    public MsiDialog getMsiDialog() {
        return msiDialog;
    }

    public AisDialog getAisDialog() {
        return aisDialog;
    }

    public void setEncDisabled() {
        encBtn.setEnabled(false);
        encBtn.setSelected(false);
    }

    public ToggleButtonLabel getNewRouteBtn() {
        return newRouteBtn;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // if (e.getSource() == msiIcon) {
        // if (notifyMsgId > 0) {
        // msiDialog.showMessage(notifyMsgId);
        // } else {
        // msiDialog.setVisible(true);
        // }
        // } else
        
        if (e.getSource() == autoFollowBtn) {
            EPDShip.getSettings().getNavSettings().setAutoFollow(autoFollowBtn.isSelected());
            if (autoFollowBtn.isSelected()) {
                mainFrame.getChartPanel().autoFollow();
            }
            menuBar.getAutoFollow().setSelected(EPDShip.getSettings().getNavSettings().isAutoFollow());

        } else if (e.getSource() == centreBtn) {
            mainFrame.getChartPanel().centreOnShip();
        } else if (e.getSource() == zoomInBtn) {
            mainFrame.getChartPanel().doZoom(0.5f);
        } else if (e.getSource() == zoomOutBtn) {
            mainFrame.getChartPanel().doZoom(2f);
        } else if (e.getSource() == aisBtn) {
            EPDShip.getSettings().getAisSettings().setVisible(aisBtn.isSelected());
            mainFrame.getChartPanel().aisVisible(aisBtn.isSelected());

            menuBar.getAisLayer().setSelected(EPDShip.getSettings().getAisSettings().isVisible());

            // } else if (e.getSource() == riskBtn) {
            // EeINS.getRiskHandler().toggleRiskHandler(riskBtn.isSelected());
        } else if (e.getSource() == encBtn) {
            EPDShip.getSettings().getMapSettings().setEncVisible(encBtn.isSelected());
            mainFrame.getChartPanel().encVisible(encBtn.isSelected());
            menuBar.getEncLayer().setSelected(EPDShip.getSettings().getMapSettings().isEncVisible());
           
        } else if (e.getSource() == wmsBtn) {
            mainFrame.getChartPanel().getWmsLayer().setVisible(wmsBtn.isSelected());
        } else if (e.getSource() == routeManagerBtn) {
            RouteManagerDialog routeManagerDialog = new RouteManagerDialog(mainFrame);
            routeManagerDialog.setVisible(true);
        } else if (e.getSource() == setupBtn) {
            SetupDialog setupDialog = new SetupDialog(mainFrame);
            setupDialog.loadSettings(EPDShip.getSettings());
            setupDialog.setVisible(true);
        } else if (e.getSource() == msiButton) {
            msiDialog.setVisible(true);
        } else if (e.getSource() == aisButton) {
            aisDialog.setVisible(true);
            aisDialog.setSelection(-1, true);
        } else if (e.getSource() == newRouteBtn) {
            if (mouseDelegator.getActiveMouseModeID() == NavigationMouseMode.MODE_ID || mouseDelegator.getActiveMouseModeID() == DragMouseMode.MODE_ID) {
                menuBar.getNewRoute().setSelected(true);
                mainFrame.getChartPanel().setMouseMode(0);
            } else {
                mainFrame.getChartPanel().setMouseMode(1);
                menuBar.getNewRoute().setSelected(false);
            }
            // } else if (e.getSource() == nogoButton) {
            // nogoHandler.toggleLayer();
        } else if (e.getSource() == newRouteBtn) {
            newRoute();
        } else if (e.getSource() == aisToggleName) {
            aisLayer.toggleAllLabels();
        } else if (e.getSource() == toggleSafeHaven) {
            routeLayer.toggleSafeHaven();
        } else if (e.getSource() == dragMouseMode) {
            System.out.println("Drag mouse mode!");
            mainFrame.getChartPanel().setMouseMode(2);
        } else if (e.getSource() == navigationMouseMode) {
            mainFrame.getChartPanel().setMouseMode(1);
            System.out.println("Nav mouse mode!");
        }
        // else if (e.getSource() == tglbtnMsiFilter) {
        // EeINS.getSettings().getEnavSettings()
        // .setMsiFilter(tglbtnMsiFilter.isSelected());
        // msiHandler.notifyUpdate();
        // }
        // else if (e.getSource() == lockFrames) {
        // mainFrame.getDockableComponents().toggleFrameLock();
        // }

    }

    
    
    public ToggleButtonLabel getNavigationMouseMode() {
        return navigationMouseMode;
    }

    public ToggleButtonLabel getDragMouseMode() {
        return dragMouseMode;
    }

    public ButtonLabel getMsiButton() {
        return msiButton;
    }

    // public ToggleButtonLabel getNogoButton() {
    // return nogoButton;
    // }

    public ToggleButtonLabel getAisBtn() {
        return aisBtn;
    }

    //
    // public ToggleButtonLabel getRiskBtn() {
    // return riskBtn;
    // }

    public ToggleButtonLabel getEncBtn() {
        return encBtn;
    }

    // public ToggleButtonLabel getLockFrames() {
    // return lockFrames;
    // }

    // public ToggleButtonLabel getTglbtnMsiFilter() {
    // return tglbtnMsiFilter;
    // }

    public ToggleButtonLabel getAutoFollowBtn() {
        return autoFollowBtn;
    }

    public void zoomIn() {
        mainFrame.getChartPanel().doZoom(0.5f);
    }

    /**
     * Function for resizing the icons for the toolbar
     * 
     * @param imgpath
     *            path of the image
     * @return newimage the newly created and resized image
     */
    public ImageIcon toolbarIcon(String imgpath) {
        URL url = EPDShip.class.getClassLoader().getResource(imgpath);
        ImageIcon icon = new ImageIcon(url);

        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(iconWidth, iconHeight, java.awt.Image.SCALE_DEFAULT);
        ImageIcon newImage = new ImageIcon(newimg);
        return newImage;
    }

}

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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.beans.beancontext.BeanContextMembershipEvent;
import java.beans.beancontext.BeanContextMembershipListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.bbn.openmap.Environment;
import com.bbn.openmap.I18n;
import com.bbn.openmap.LightMapHandlerChild;
import com.bbn.openmap.PropertyConsumer;
import com.bbn.openmap.gui.WindowSupport;

import dk.dma.ais.virtualnet.transponder.gui.TransponderFrame;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.settings.layers.AisLayerCommonSettings.IObserver;
import dk.dma.epd.common.prototype.settings.layers.AisLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.LayerSettings;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.layers.nogo.NogoLayer;
import dk.dma.epd.ship.nogo.NogoHandler;

public class MenuBar extends JMenuBar implements PropertyConsumer, BeanContextChild, BeanContextMembershipListener,
        LightMapHandlerChild, IObserver {

    private static final long serialVersionUID = 1L;

    protected I18n i18n = Environment.getI18n();

    protected int orientation = SwingConstants.HORIZONTAL;

    protected boolean isolated;

    protected BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport(this);

    MainFrame mainFrame;
    TopPanel topPanel;
    NogoHandler nogoHandler;
    MsiHandler msiHandler;
    TransponderFrame transponderFrame;

    JCheckBoxMenuItem lock;
    private JCheckBoxMenuItem autoFollow;
    private JCheckBoxMenuItem aisLayer;
    private JCheckBoxMenuItem encLayer;
    private JCheckBoxMenuItem msPntLayer;
    private final JCheckBoxMenuItem nogoLayer = new JCheckBoxMenuItem("NoGo Layer");;
    private JCheckBoxMenuItem intendedRouteLayer;
    private JCheckBoxMenuItem newRoute;
    private JMenu dockableMenu;

    private JMenu layouts;

    // private boolean fullscreenState;

    public MenuBar() {
        super();
        // Observe AIS layer settings
        EPDShip.getInstance().getSettings().getPrimaryAisLayerSettings().addObserver(this);
    }

    private void initMenuBar() {

        /*****************************************/
        /** File menu                           **/
        /*****************************************/
        
        JMenu file = new JMenu("File");
        this.add(file);

        // Create a menu item
        JMenuItem fullscreen = new JMenuItem("Fullscreen");
        file.add(fullscreen);
        fullscreen.setIcon(toolbarIcon("images/toolbar/application-resize.png"));

        fullscreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.toggleFullScreen();
            }
        });

        // Create a menu item
        JMenuItem setup = new JMenuItem("Setup");
        file.add(setup);

        setup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.openSetupDialog();
            }
        });

        setup.setIcon(toolbarIcon("images/toolbar/wrench.png"));

        JMenuItem transponder = new JMenuItem("Transponder");
        file.add(transponder);
        // transponder.setIcon(toolbarIcon("images/toolbar/transponder.png"));
        transponder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (transponderFrame != null) {
                    transponderFrame.setVisible(true);
                }
            }
        });

        JMenuItem exit = new JMenuItem("Exit");
        file.add(exit);
        exit.setIcon(toolbarIcon("images/toolbar/cross-circle.png"));

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.onWindowClosing();
            }
        });

        /*****************************************/
        /** Interact menu                       **/
        /*****************************************/
        
        JMenu interact = new JMenu("Interact");
        this.add(interact);

        JMenuItem zoomIn = new JMenuItem("Zoom in : Shortcut Numpad +");
        interact.add(zoomIn);
        zoomIn.setIcon(toolbarIcon("images/toolbar/magnifier-zoom-in.png"));

        zoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.getChartPanel().doZoom(0.5f);
            }
        });

        JMenuItem zoomOut = new JMenuItem("Zoom out : Shortcut Numpad -");
        interact.add(zoomOut);
        zoomOut.setIcon(toolbarIcon("images/toolbar/magnifier-zoom-out.png"));

        zoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.getChartPanel().doZoom(2f);
            }
        });

        JCheckBoxMenuItem centerOnShip = new JCheckBoxMenuItem("Centre on ship : Shortcut C");
        interact.add(centerOnShip);
        centerOnShip.setIcon(toolbarIcon("images/toolbar/arrow-in.png"));

        centerOnShip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.getChartPanel().centreOnShip();
            }
        });

        autoFollow = new JCheckBoxMenuItem("Auto follow own ship");
        interact.add(autoFollow);
        autoFollow.setSelected(EPDShip.getInstance().getSettings().getMapSettings().isAutoFollow());
        autoFollow.setIcon(toolbarIcon("images/toolbar/arrow-curve-000-double.png"));

        autoFollow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                topPanel.getAutoFollowBtn().setSelected(!topPanel.getAutoFollowBtn().isSelected());

                EPDShip.getInstance().getSettings().getMapSettings().setAutoFollow(topPanel.getAutoFollowBtn().isSelected());

                if (topPanel.getAutoFollowBtn().isSelected()) {
                    mainFrame.getChartPanel().autoFollow();
                }
            }
        });

        /*****************************************/
        /** Layers menu                         **/
        /*****************************************/
        
        JMenu layers = new JMenu("Layers");
        this.add(layers);

        aisLayer = new JCheckBoxMenuItem("AIS Layer");
        layers.add(aisLayer);
        aisLayer.setSelected(EPDShip.getInstance().getSettings().getPrimaryAisLayerSettings().isVisible());

        aisLayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Toggle AIS layer visibility.
                // Listeners will be notified.
                mainFrame.getChartPanel().aisVisible(aisLayer.isSelected());
            }
        });

        encLayer = new JCheckBoxMenuItem("ENC Layer");
        layers.add(encLayer);
        encLayer.setEnabled(EPDShip.getInstance().getSettings().getENCLayerSettings().isEncInUse());
        // TODO shouldn't below check be isEncInUse() && isVisible()?
        encLayer.setSelected(EPDShip.getInstance().getSettings().getENCLayerSettings().isVisible());

        encLayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                topPanel.getEncBtn().setSelected(!topPanel.getEncBtn().isSelected());
                // persisting setting is done as part of encVisible - no need to do it here.
//                EPDShip.getInstance().getSettings().getMapSettings().setEncVisible(topPanel.getEncBtn().isSelected());
                mainFrame.getChartPanel().encVisible(topPanel.getEncBtn().isSelected());
            }
        });

        layers.add(nogoLayer);

        nogoLayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nogoHandler.toggleLayer();
            }
        });

        // TODO remove? selected is determined earlier based on isVisible.
//        aisLayer.setSelected(EPDShip.getInstance().getSettings().getAisHandlerSettings().isStrict());
        
        // Intended route layer
        intendedRouteLayer = new JCheckBoxMenuItem("Intended route Layer");
        layers.add(intendedRouteLayer);
        intendedRouteLayer.setSelected(EPDShip.getInstance().getSettings().getPrimaryIntendedRouteLayerSettings().isVisible());
        intendedRouteLayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = !topPanel.getIntendedRouteButton().isSelected();
                topPanel.getIntendedRouteButton().setSelected(selected);
                EPDShip.getInstance().getSettings().getPrimaryIntendedRouteLayerSettings().setVisible(selected);
            }
        });

        // Multi-source PNT (a.k.a "Resilient PNT") layer.
        // Please note, this later is actually a virtual layer;
        // the RPNT graphics is handler by the OwnShipLayer.
        msPntLayer = new JCheckBoxMenuItem("Resilient PNT Layer");
        layers.add(msPntLayer);
        msPntLayer.setSelected(EPDShip.getInstance().getSettings().getOwnShipLayerSettings().isMultiSourcePntVisible());
        msPntLayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EPDShip.getInstance().getSettings().getOwnShipLayerSettings().setMultiSourcePntVisible(msPntLayer.isSelected());
            }
        });

        /*****************************************/
        /** Tools menu                          **/
        /*****************************************/
        
        JMenu tools = new JMenu("Tools");
        this.add(tools);

        newRoute = new JCheckBoxMenuItem("New Route | Ctrl n");
        tools.add(newRoute);
        newRoute.setIcon(toolbarIcon("images/toolbar/marker--plus.png"));

        newRoute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                topPanel.getNewRouteBtn().setSelected(true);
                topPanel.newRoute();
            }
        });

        JCheckBoxMenuItem msiFilter = new JCheckBoxMenuItem("MSI Filtering");
        tools.add(msiFilter);

        msiFilter.setSelected(EPDShip.getInstance().getSettings().getMsiHandlerSettings().isMsiFilter());

        msiFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EPDShip.getInstance().getSettings().getMsiHandlerSettings().setMsiFilter(!EPDShip.getInstance().getSettings().getMsiHandlerSettings().isMsiFilter());
                msiHandler.notifyUpdate();
            }
        });
        
        JCheckBoxMenuItem newSAR = new JCheckBoxMenuItem("SAR Operation");
        tools.add(newSAR);

        
        // Add notifications and a "Send message" menu item
        tools.add(new JSeparator());        
        
        JMenuItem notCenter = new JMenuItem("Notification Center");
        tools.add(notCenter);
        notCenter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EPD.getInstance().getNotificationCenter().toggleVisibility();
            }
        });
        
        JMenuItem chatMenuItem = new JMenuItem("Send message...");
        chatMenuItem.addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                EPD.getInstance().getMainFrame().getChatServiceDialog().init();
            }
        });
        tools.add(chatMenuItem);
        

        /*****************************************/
        /** Layouts menu                        **/
        /*****************************************/
        
        layouts = new JMenu("Layouts");
        this.add(layouts);

        lock = new JCheckBoxMenuItem("Unlock");
        layouts.add(lock);
        lock.setSelected(true);
        lock.setIcon(toolbarIcon("images/toolbar/lock-unlock.png"));

        lock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem m = (JCheckBoxMenuItem) e.getSource();
                mainFrame.getDockableComponents().toggleFrameLock();
                if (m.isSelected()) {
                    m.setText("Unlock");
                    m.setIcon(toolbarIcon("images/toolbar/lock-unlock.png"));
                } else {
                    m.setText("Lock");
                    m.setIcon(toolbarIcon("images/toolbar/lock.png"));
                }
            }
        });

        layouts.add(new JSeparator());

        generateStaticLayouts();

        layouts.add(new JSeparator());

        JMenuItem load = new JMenuItem("Load Custom");
        layouts.add(load);
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String[] list = findNoneStaticLayouts();
                String layout = null;

                final String path = EPDShip.getInstance().getHomePath().toString() + "/layout/";

                if (list.length == 0) {
                    JOptionPane.showMessageDialog(mainFrame, "No custom layouts saved.", "No Layouts", JOptionPane.ERROR_MESSAGE);
                } else {
                    layout = (String) JOptionPane.showInputDialog(mainFrame, "Choose one", "Load layout",
                            JOptionPane.INFORMATION_MESSAGE, null, list, null);
                }
                if (layout != null) {
                    mainFrame.getDockableComponents().loadLayout(path + layout + ".xml");
                    lock.setSelected(true);
                    lock.setText("Unlock");
                    lock.setIcon(toolbarIcon("images/toolbar/lock-unlock.png"));

                }
                refreshDockableMenu();
            }
        });

        // List already saved
        // When saving add it to the list

        JMenuItem saveAs = new JMenuItem("Save Custom");
        layouts.add(saveAs);
        saveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!mainFrame.getDockableComponents().isLocked()) {
                    mainFrame.getDockableComponents().toggleFrameLock();
                }

                String name = null;
                name = JOptionPane.showInputDialog(mainFrame, "Please input name of layout");
                System.out.println(name);

                if (name != null) {

                    String[] list = findNoneStaticLayouts();

                    if (list.length == 0) {
                        mainFrame.getDockableComponents().saveLayout(name);
                    }

                    for (String element : list) {
                        if (element.equals(name)) {
                            int n = JOptionPane.showConfirmDialog(mainFrame,
                                    "A layout with that name already exists. \n Do you wish to overwrite it?",
                                    "Layout already exists", JOptionPane.YES_NO_OPTION);
                            if (n == 1) {
                                mainFrame.getDockableComponents().saveLayout(name);
                            }
                        } else {
                            mainFrame.getDockableComponents().saveLayout(name);
                        }
                    }

                }
            }
        });

        dockableMenu = mainFrame.getDockableComponents().createDockableMenu();
        this.add(dockableMenu);

        /*****************************************/
        /** Help menu                           **/
        /*****************************************/
        
        JMenu help = new JMenu("Help");
        this.add(help);

        JMenuItem aboutEpdShip = new JMenuItem(mainFrame.getAboutAction());
        help.add(aboutEpdShip);
    }

    public void generateStaticLayouts() {
        Path home = EPDShip.getInstance().getHomePath();

        String files;
        File folder = home.resolve("layout/static").toFile();
        File[] listOfFiles = folder.listFiles();

        for (File listOfFile : listOfFiles) {

            if (listOfFile.isFile()) {
                files = listOfFile.getName();
                if (files.endsWith(".xml") || files.endsWith(".XML")) {

                    JMenuItem layout = new JMenuItem(files.substring(0, files.length() - 4));
                    layouts.add(layout);
                    final String p = listOfFile.getAbsolutePath();
                    layout.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            mainFrame.getDockableComponents().loadLayout(p);

                            lock.setSelected(true);
                            lock.setText("Unlock");
                            lock.setIcon(toolbarIcon("images/toolbar/lock-unlock.png"));

                            refreshDockableMenu();
                        }
                    });

                    // System.out.println(path + files);
                }
            }
        }
    }

    public void refreshDockableMenu() {
        this.remove(dockableMenu);
        dockableMenu = null;
        dockableMenu = mainFrame.getDockableComponents().createDockableMenu();

        this.add(dockableMenu, this.getComponentCount() - 1);
    }

    public String[] findNoneStaticLayouts() {
        final String path = EPDShip.getInstance().getHomePath().toString() + "/layout/";

        List<String> list = new ArrayList<>();

        String files;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File listOfFile : listOfFiles) {

            if (listOfFile.isFile()) {
                files = listOfFile.getName();
                if (files.endsWith(".xml") || files.endsWith(".XML")) {
                    list.add(files.substring(0, files.length() - 4));
                }
            }
        }

        String[] array = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        Arrays.sort(array);

        return array;
    }

    public JCheckBoxMenuItem getLock() {
        return lock;
    }

    public void setLock(JCheckBoxMenuItem lock) {
        this.lock = lock;
    }

    protected WindowSupport windowSupport;

    public void setWindowSupport(WindowSupport ws) {
        windowSupport = ws;
    }

    public WindowSupport getWindowSupport() {
        return windowSupport;
    }

    protected String propertyPrefix;

    @Override
    public void setProperties(java.util.Properties props) {
        setProperties(getPropertyPrefix(), props);
    }

    @Override
    public void setProperties(String prefix, java.util.Properties props) {
        setPropertyPrefix(prefix);

        // String realPrefix =
        // PropUtils.getScopedPropertyPrefix(prefix);
    }

    @Override
    public Properties getProperties(Properties props) {
        if (props == null) {
            props = new Properties();
        }
        return props;
    }

    @Override
    public Properties getPropertyInfo(Properties list) {
        if (list == null) {
            list = new Properties();
        }
        return list;
    }

    @Override
    public void setPropertyPrefix(String prefix) {
        propertyPrefix = prefix;
    }

    @Override
    public String getPropertyPrefix() {
        return propertyPrefix;
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof TopPanel) {
            topPanel = (TopPanel) obj;
        }
        if (obj instanceof NogoHandler) {
            nogoHandler = (NogoHandler) obj;

        }
        if (obj instanceof MainFrame) {
            mainFrame = (MainFrame) obj;
            initMenuBar();
        }
        if (obj instanceof NogoLayer) {
            nogoLayer.setSelected(((NogoLayer) obj).isVisible());
        }
        if (obj instanceof MsiHandler) {
            msiHandler = (MsiHandler) obj;
        }
        if (obj instanceof TransponderFrame) {
            transponderFrame = (TransponderFrame) obj;
        }
    }

    @Override
    public void findAndUndo(Object obj) {
    }

    public void findAndInit(Iterator<?> it) {
        while (it.hasNext()) {
            findAndInit(it.next());
        }
    }

    @Override
    public void childrenAdded(BeanContextMembershipEvent bcme) {
        if (!isolated || bcme.getBeanContext().equals(getBeanContext())) {
            findAndInit(bcme.iterator());
        }
    }

    @Override
    public void childrenRemoved(BeanContextMembershipEvent bcme) {
        Iterator<?> it = bcme.iterator();
        while (it.hasNext()) {
            findAndUndo(it.next());
        }
    }

    @Override
    public BeanContext getBeanContext() {
        return beanContextChildSupport.getBeanContext();
    }

    @Override
    public void setBeanContext(BeanContext in_bc) throws PropertyVetoException {

        if (in_bc != null) {
            if (!isolated || beanContextChildSupport.getBeanContext() == null) {
                in_bc.addBeanContextMembershipListener(this);
                beanContextChildSupport.setBeanContext(in_bc);
                findAndInit(in_bc.iterator());
            }
        }
    }

    @Override
    public void addVetoableChangeListener(String propertyName, VetoableChangeListener in_vcl) {
        beanContextChildSupport.addVetoableChangeListener(propertyName, in_vcl);
    }

    @Override
    public void removeVetoableChangeListener(String propertyName, VetoableChangeListener in_vcl) {
        beanContextChildSupport.removeVetoableChangeListener(propertyName, in_vcl);
    }

    @Override
    public void fireVetoableChange(String name, Object oldValue, Object newValue) throws PropertyVetoException {
        beanContextChildSupport.fireVetoableChange(name, oldValue, newValue);
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isIsolated() {
        return isolated;
    }

    public void setIsolated(boolean isolated) {
        this.isolated = isolated;
    }

    public JCheckBoxMenuItem getAutoFollow() {
        return autoFollow;
    }

    public JCheckBoxMenuItem getEncLayer() {
        return encLayer;
    }

    public JCheckBoxMenuItem getNogoLayer() {
        return nogoLayer;
    }
    
    public JCheckBoxMenuItem getIntendedRouteLayer() {
        return intendedRouteLayer;
    }

    public JCheckBoxMenuItem getNewRoute() {
        return newRoute;
    }

    public ImageIcon toolbarIcon(String imgpath) {
        ImageIcon icon = EPDShip.res().getCachedImageIcon(imgpath);
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(16, 16, java.awt.Image.SCALE_DEFAULT);
        ImageIcon newImage = new ImageIcon(newimg);
        return newImage;
    }

    @Override
    public void showVesselNameLabelsChanged(boolean show) {
        // Not relevant for MenuBar.
    }

    @Override
    public void movementVectorLengthMinChanged(int newMinLengthMinutes) {
        // Not relevant for MenuBar.
    }

    @Override
    public void movementVectorLengthMaxChanged(int newMaxLengthMinutes) {
        // Not relevant for MenuBar.
    }

    @Override
    public void movementVectorLengthStepSizeChanged(float newStepSize) {
        // Not relevant for MenuBar.
    }

    @Override
    public void movementVectorHideBelowChanged(float newMinSpeed) {
        // Not relevant for MenuBar.
    }

    @Override
    public void isVisibleChanged(LayerSettings<?> source, boolean newValue) {
        if(source instanceof AisLayerCommonSettings<?> && aisLayer != null) {
            /*
             * AIS layer was toggled on/off.
             * Update menu bar accordingly.
             */
            aisLayer.setSelected(newValue);
        }
    }

    @Override
    public void showAllPastTracksChanged(boolean newValue) {
        // Not relevant for MenuBar.
    }

    @Override
    public void layerRedrawIntervalChanged(int newValue) {
        // Not relevant for MenuBar.
    }

}

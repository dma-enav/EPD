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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.event.ToolbarMoveMouseListener;
import dk.dma.epd.shore.gui.settingtabs.AisSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.ConnectionStatus;
import dk.dma.epd.shore.gui.settingtabs.ENavSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.GuiStyler;
import dk.dma.epd.shore.gui.settingtabs.MapSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.MapWindowSinglePanel;
import dk.dma.epd.shore.gui.settingtabs.MapWindowsPanel;
import dk.dma.epd.shore.gui.utils.ComponentFrame;
import dk.dma.epd.shore.layers.wms.WMSService;
import dk.dma.epd.shore.services.shore.ShoreServices;
import dk.dma.epd.shore.settings.ESDSettings;

public class JSettingsWindow extends ComponentFrame implements MouseListener {

    private static final long serialVersionUID = 1L;

    private JPanel backgroundPane;

    Border paddingLeft = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(65, 65, 65));
    Border paddingBottom = BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(83, 83, 83));
    Border notificationPadding = BorderFactory.createCompoundBorder(paddingBottom, paddingLeft);
    Border notificationsIndicatorImportant = BorderFactory.createMatteBorder(0, 0, 0, 10, new Color(206, 120, 120));
    Border paddingLeftPressed = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(45, 45, 45));
    Border notificationPaddingPressed = BorderFactory.createCompoundBorder(paddingBottom, paddingLeftPressed);

    Font defaultFont = new Font("Arial", Font.PLAIN, 11);
    Color textColor = new Color(237, 237, 237);

    private JLabel breadCrumps;

    private JLabel mapSettings;
    private JLabel connections;
    private JLabel aisSettings;
    private JLabel eNavServices;
    private JLabel mapWindows;
    private JLabel routeSettings;

    private boolean mapSettingsChanged = true;
    private boolean aisSettingsChanged;
    private boolean eNavServicesChanged;
    private boolean mapWindowsChanged;
    // private boolean routeSettingsChanged = false;

    private List<JLabel> mapWindowsList;
    private List<MapWindowSinglePanel> mapWindowsListPanels;

    private MapSettingsPanel mapSettingsPanel;
    private MapWindowsPanel mapWindowsPanel;
    private ConnectionStatus connectionsPanel;
    private AisSettingsPanel aisSettingsPanel;
    private ENavSettingsPanel eNavSettingsPanel;
    private JPanel routeSettingsPanel;

    private JPanel contentPane;

    private JPanel labelContainer;

    private JLabel ok;
    private JLabel cancel;

    MouseMotionListener[] actions;
    private JLabel moveHandler;
    private JPanel masterPanel;
    private JPanel mapPanel;
    private static int moveHandlerHeight = 18;
    public int width;
    public int height;
    JInternalFrame settingsWindow;
    private MainFrame mainFrame;
    private ESDSettings settings;
    private List<IStatusComponent> statusComponents = new ArrayList<IStatusComponent>();
    private boolean reset;

    /**
     * Create the frame.
     */
    public JSettingsWindow() {
        super("Settings Window", false, true, false, false);
        setSize(800, 600);
        setLocation(10, 10);

        settings = EPDShore.getSettings();

        setResizable(false);
        setTitle("Preferences");
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 661, 481);
        backgroundPane = new JPanel();
        // backgroundPane.setBorder(new LineBorder(new Color(0, 0, 0)));
        // backgroundPane.setBackground(new Color(83, 83, 83));

        backgroundPane.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("653px:grow"), }, new RowSpec[] {
                FormFactory.NARROW_LINE_GAP_ROWSPEC, RowSpec.decode("23px:grow"), RowSpec.decode("428px:grow"), }));

        backgroundPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel topPanel = new JPanel();
        topPanel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(70, 70, 70)));
        backgroundPane.add(topPanel, "1, 2, fill, fill");
        topPanel.setLayout(null);
        topPanel.setBackground(GuiStyler.backgroundColor);

        breadCrumps = new JLabel("Preferences > Map Settings");
        GuiStyler.styleText(breadCrumps);

        breadCrumps.setBounds(10, 4, 603, 14);
        breadCrumps.setHorizontalAlignment(SwingConstants.LEFT);
        topPanel.add(breadCrumps);

    }

    public void addMouseListeners() {
        mapSettings.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mapSettings.setBackground(new Color(45, 45, 45));
                hideAllPanels();
                mapSettingsPanel.setVisible(true);
                hideMapTabs();

                resetTabs();
                mapSettings.setBackground(new Color(55, 55, 55));
                breadCrumps.setText("Preferences > Map Settings");
                mapSettingsChanged = true;
            }

            public void mouseReleased(MouseEvent e) {

            }

        });

        mapWindows.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mapWindows.setBackground(new Color(45, 45, 45));
                hideAllPanels();
                mapWindowsPanel.loadSettings();
                mapWindowsPanel.setVisible(true);
                updateLabels();

                resetTabs();
                mapWindows.setBackground(new Color(55, 55, 55));
                breadCrumps.setText("Preferences > Map Windows");

                for (int i = 0; i < mapWindowsList.size(); i++) {
                    mapWindowsList.get(i).setVisible(true);
                }

                mapWindowsChanged = true;
            }

            public void mouseReleased(MouseEvent e) {

            }

        });

        connections.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                connections.setBackground(new Color(45, 45, 45));
                hideAllPanels();
                connectionsPanel.setVisible(true);
                connectionsPanel.showStatus(statusComponents);
                hideMapTabs();

                resetTabs();
                connections.setBackground(new Color(55, 55, 55));
                breadCrumps.setText("Preferences > Connections");

            }

            public void mouseReleased(MouseEvent e) {
            }

        });

        aisSettings.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                aisSettings.setBackground(new Color(45, 45, 45));
                hideAllPanels();
                aisSettingsPanel.loadSettings(settings.getAisSettings(), settings.getSensorSettings());
                aisSettingsPanel.setVisible(true);
                hideMapTabs();

                resetTabs();
                aisSettings.setBackground(new Color(55, 55, 55));
                breadCrumps.setText("Preferences > AIS Settings");

                aisSettingsChanged = true;

            }

            public void mouseReleased(MouseEvent e) {
            }

        });

        eNavServices.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                eNavServices.setBackground(new Color(45, 45, 45));
                hideAllPanels();
                // eNavSettingsPanel.
                // TO DO
                eNavSettingsPanel.loadSettings(settings.getEnavSettings());
                eNavSettingsPanel.setVisible(true);
                hideMapTabs();

                resetTabs();
                eNavServices.setBackground(new Color(55, 55, 55));
                breadCrumps.setText("Preferences > e-Nav Services");

                eNavServicesChanged = true;

            }

            public void mouseReleased(MouseEvent e) {
            }

        });

        routeSettings.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                routeSettings.setBackground(new Color(45, 45, 45));
                hideAllPanels();
                routeSettingsPanel.setVisible(true);
                hideMapTabs();

                resetTabs();
                routeSettings.setBackground(new Color(55, 55, 55));
                breadCrumps.setText("Preferences > Routes Settings");
                // routeSettingsChanged = true;

            }

            public void mouseReleased(MouseEvent e) {
            }

        });

        ok.addMouseListener(this);
        cancel.addMouseListener(this);

        // ok.addMouseListener(new MouseAdapter() {
        // public void mousePressed(MouseEvent e) {
        // ok.setBackground(new Color(45, 45, 45));
        //
        // System.out.println("OK PRESSED");
        // }
        //
        // public void mouseReleased(MouseEvent e) {
        // ok.setBackground(new Color(65, 65, 65));
        // }
        //
        // });
        //
        // cancel.addMouseListener(new MouseAdapter() {
        // public void mousePressed(MouseEvent e) {
        // cancel.setBackground(new Color(45, 45, 45));
        // System.out.println("Cancel pressed");
        // }
        //
        // public void mouseReleased(MouseEvent e) {
        // cancel.setBackground(new Color(65, 65, 65));
        // }
        //
        // });

    }

    public void resetTabs() {
        mapSettings.setBackground(new Color(65, 65, 65));
        connections.setBackground(new Color(65, 65, 65));
        aisSettings.setBackground(new Color(65, 65, 65));
        eNavServices.setBackground(new Color(65, 65, 65));
        mapWindows.setBackground(new Color(65, 65, 65));
        routeSettings.setBackground(new Color(65, 65, 65));

        for (int i = 0; i < mapWindowsList.size(); i++) {
            mapWindowsList.get(i).setBackground(new Color(75, 75, 75));
        }
    }

    public JPanel createConnectionsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(GuiStyler.backgroundColor);
        panel.setBounds(10, 11, 493, 406);
        panel.setLayout(null);

        return panel;
    }

    private void hideAllPanels() {
        mapSettingsPanel.setVisible(false);
        mapWindowsPanel.setVisible(false);
        connectionsPanel.setVisible(false);
        aisSettingsPanel.setVisible(false);
        eNavSettingsPanel.setVisible(false);
        routeSettingsPanel.setVisible(false);

        for (int i = 0; i < mapWindowsListPanels.size(); i++) {
            mapWindowsListPanels.get(i).setVisible(false);
        }

    }

    /**
     * Function for setting up custom GUI for the map frame
     */
    public void initGUI() {

        JPanel bottomPanel = new JPanel();
        backgroundPane.add(bottomPanel, "1, 3, fill, fill");
        bottomPanel.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(0, 0, 140, 428);
        scrollPane.setBorder(null);
        bottomPanel.add(scrollPane);

        // Panels
        JPanel menuPanel = new JPanel();
        scrollPane.setViewportView(menuPanel);
        menuPanel.setBackground(GuiStyler.backgroundColor);
        menuPanel.setLayout(null);

        labelContainer = new JPanel();
        labelContainer.setLocation(0, 0);
        labelContainer.setBackground(GuiStyler.backgroundColor);
        labelContainer.setSize(new Dimension(140, 500));

        menuPanel.add(labelContainer);

        contentPane = new JPanel();
        contentPane.setBorder(new MatteBorder(0, 1, 0, 0, new Color(70, 70, 70)));
        contentPane.setBounds(140, 0, 513, 428);
        bottomPanel.add(contentPane);
        contentPane.setBackground(GuiStyler.backgroundColor);
        contentPane.setLayout(null);

        ok = new JLabel("OK", new ImageIcon(EPDShore.class.getClassLoader().getResource("images/buttons/ok.png")), SwingConstants.CENTER);
        ok.setBounds(335, 390, 75, 20);
        GuiStyler.styleButton(ok);
        contentPane.add(ok);

        cancel = new JLabel("CANCEL", new ImageIcon(EPDShore.class.getClassLoader().getResource("images/buttons/cancel.png")), SwingConstants.CENTER);
        GuiStyler.styleButton(cancel);
        cancel.setBounds(417, 390, 75, 20);
        contentPane.add(cancel);

        // Content panels
        mapSettingsPanel = new MapSettingsPanel(settings);

        mapWindowsPanel = new MapWindowsPanel(mainFrame, settings);
        mapWindowsPanel.setVisible(false);

        connectionsPanel = new ConnectionStatus(mainFrame);
        connectionsPanel.setVisible(false);

        aisSettingsPanel = new AisSettingsPanel();
        aisSettingsPanel.setVisible(false);

        eNavSettingsPanel = new ENavSettingsPanel();
        eNavSettingsPanel.setVisible(false);

        routeSettingsPanel = createConnectionsPanel();
        routeSettingsPanel.setVisible(false);

        contentPane.add(mapSettingsPanel);

        contentPane.add(mapWindowsPanel);

        contentPane.add(connectionsPanel);

        contentPane.add(aisSettingsPanel);

        contentPane.add(eNavSettingsPanel);

        // contentPane.add(routeSettingsPanel);

        generateTabs();

        settingsWindow = this;

        // Strip off
        setRootPaneCheckingEnabled(false);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
        this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Map tools
        mapPanel = new JPanel(new GridLayout(1, 3));
        mapPanel.setPreferredSize(new Dimension(500, moveHandlerHeight));
        mapPanel.setOpaque(true);
        mapPanel.setBackground(Color.DARK_GRAY);
        mapPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 30, 30)));

        ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this, mainFrame);
        mapPanel.addMouseListener(mml);
        mapPanel.addMouseMotionListener(mml);

        // Placeholder - for now
        mapPanel.add(new JLabel());

        // Movehandler/Title dragable)
        moveHandler = new JLabel("Preferences", SwingConstants.CENTER);
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setForeground(new Color(200, 200, 200));
        moveHandler.addMouseListener(this);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);
        actions = moveHandler.getListeners(MouseMotionListener.class);
        mapPanel.add(moveHandler);

        // The tools (minimize, maximize and close)
        JPanel mapToolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        mapToolsPanel.setOpaque(false);
        mapToolsPanel.setPreferredSize(new Dimension(60, 50));

        JLabel close = new JLabel(new ImageIcon(EPDShore.class.getClassLoader().getResource("images/window/close.png")));
        close.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                settingsWindow.setVisible(false);
            }

        });
        close.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 2));
        mapToolsPanel.add(close);
        mapPanel.add(mapToolsPanel);

        // Create the masterpanel for aligning
        masterPanel = new JPanel(new BorderLayout());
        masterPanel.add(mapPanel, BorderLayout.NORTH);
        masterPanel.add(backgroundPane, BorderLayout.SOUTH);
        masterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(
                45, 45, 45)));

        this.setContentPane(masterPanel);

        reset = true;

    }

    @Override
    public void mouseClicked(MouseEvent arg0) {

    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        // Remove the generated map panels so that we can make new ones
        if (!visible && mapWindowsListPanels != null) {
            for (int i = 0; i < mapWindowsListPanels.size(); i++) {
                contentPane.remove(mapWindowsListPanels.get(i));
            }
        }

        // Reset view
        if (reset) {
            mapSettings.setBackground(new Color(45, 45, 45));
            hideAllPanels();
            mapSettingsPanel.setVisible(true);
            hideMapTabs();

            resetTabs();
            mapSettings.setBackground(new Color(55, 55, 55));
            breadCrumps.setText("Preferences > Map Settings");
        }

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
    public void mouseReleased(MouseEvent arg0) {

        if (arg0.getSource() == ok) {
            boolean restart = false;

            System.out.println("ok pressed");



            // Map settings check if changed
            if (mapSettingsChanged) {
                mapSettingsPanel.saveSettings();

                // Set the new WMS Query
                for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
                    mainFrame.getMapWindows().get(i).getChartPanel().getWmsLayer().getWmsService()
                            .setWMSString(settings.getGuiSettings().getWmsQuery());
                }
            }

            if (aisSettingsChanged) {
                aisSettingsPanel.saveSettings();
                restart = true;

            }

            if (eNavServicesChanged) {
                eNavSettingsPanel.saveSettings();
            }

            if (mapWindowsChanged) {
                for (int i = 0; i < mapWindowsListPanels.size(); i++) {
                    mapWindowsListPanels.get(i).saveSettings();
                }
            }

            settings.saveToFile();

            if (restart && this.isVisible()) {
                restart = false;
                System.out.println("ais changed?");
                int choice = JOptionPane.showOptionDialog(EPDShore.getMainFrame(),
                        "The settings will take effect next time the application is started.\nStop now?",
                        "Restart required", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null,
                        JOptionPane.YES_OPTION);
                System.out.println(choice);
                if (choice == JOptionPane.YES_OPTION) {
                    EPDShore.closeApp();
                }
            }

            this.setVisible(false);

        }
        if (arg0.getSource() == cancel) {

            this.setVisible(false);
        }


    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MainFrame) {
            mainFrame = (MainFrame) obj;
            initGUI();
        }

        if (obj instanceof AisHandler) {
            // aisHandler = (AisHandler) obj;
            statusComponents.add((AisHandler) obj);
        }
        if (obj instanceof ShoreServices) {
            // shoreServices = (ShoreServices) obj;
            statusComponents.add((ShoreServices) obj);
        }
        if (obj instanceof WMSService) {
            // System.out.println("wmsService");
            // shoreServices = (ShoreServices) obj;
            statusComponents.add((WMSService) obj);
        }
    }

    public void createMapLabels() {
        mapWindowsList = new ArrayList<JLabel>();
        mapWindowsListPanels = new ArrayList<MapWindowSinglePanel>();

        List<JMapFrame> mainWindows = mainFrame.getMapWindows();

        for (int i = 0; i < mainWindows.size(); i++) {
            JLabel mapLabel = new JLabel("      " + mainWindows.get(i).getTitle());
            MapWindowSinglePanel panel = new MapWindowSinglePanel(mainFrame, i);
            panel.loadSettings();
            panel.setVisible(false);
            contentPane.add(panel);
            mapWindowsListPanels.add(panel);

            mapLabel.setName(Integer.toString(i));
            GuiStyler.styleSubTab(mapLabel);
            mapWindowsList.add(mapLabel);

            mapLabel.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    updateLabels();
                    int id = Integer.parseInt(((JLabel) e.getSource()).getName());
                    ((JLabel) e.getSource()).setBackground(new Color(45, 45, 45));
                    hideAllPanels();
                    mapWindowsListPanels.get(id).setVisible(true);
                }

                public void mouseReleased(MouseEvent e) {
                    resetTabs();

                    int id = Integer.parseInt(((JLabel) e.getSource()).getName());
                    ((JLabel) e.getSource()).setBackground(new Color(55, 55, 55));
                    breadCrumps.setText("Preferences > Map Windows > " + mainFrame.getMapWindows().get(id).getTitle());
                }

            });

        }
    }

    public void updateLabels() {
        for (int i = 0; i < mapWindowsList.size(); i++) {
            mapWindowsList.get(i).setText(mapWindowsListPanels.get(i).getMapTitle());
        }
    }

    /**
     * Change the visiblity
     */
    public void toggleVisibility() {
        setVisible(!this.isVisible());

        // Regenerate Tabs
        if (this.isVisible()) {
            // System.out.println("ello visible toggle?");
            generateTabs();
        } else {
            // System.out.println("removing panels");
            for (int i = 0; i < mapWindowsListPanels.size(); i++) {
                contentPane.remove(mapWindowsListPanels.get(i));
            }
        }

    }

    public void generateTabs() {

        labelContainer.removeAll();

        mapSettings = new JLabel("Map Settings", new ImageIcon(EPDShore.class.getClassLoader().getResource("images/settings/map.png")), SwingConstants.LEFT);
        GuiStyler.styleActiveTabButton(mapSettings);

        mapWindows = new JLabel("Map Windows", new ImageIcon(EPDShore.class.getClassLoader().getResource("images/settings/window.png")), SwingConstants.LEFT);
        GuiStyler.styleTabButton(mapWindows);

        connections = new JLabel("Connections", new ImageIcon(EPDShore.class.getClassLoader().getResource("images/settings/connections.png")), SwingConstants.LEFT);
        GuiStyler.styleTabButton(connections);

        aisSettings = new JLabel("AIS Settings", new ImageIcon(EPDShore.class.getClassLoader().getResource("images/settings/binocular.png")), SwingConstants.LEFT);
        GuiStyler.styleTabButton(aisSettings);

        eNavServices = new JLabel("e-Nav Services", new ImageIcon(EPDShore.class.getClassLoader().getResource("images/settings/servers-network.png")), SwingConstants.LEFT);
        GuiStyler.styleTabButton(eNavServices);

        routeSettings = new JLabel("Route Settings", new ImageIcon(EPDShore.class.getClassLoader().getResource("images/settings/routes.png")), SwingConstants.LEFT);
        GuiStyler.styleTabButton(routeSettings);

        labelContainer.add(mapSettings);
        labelContainer.add(mapWindows);

        // Create labels for map windows
        createMapLabels();
        for (int i = 0; i < mapWindowsList.size(); i++) {
            // System.out.println("ello this is dog");
            labelContainer.add(mapWindowsList.get(i));
            mapWindowsList.get(i).setVisible(false);
        }

        labelContainer.add(connections);
        labelContainer.add(aisSettings);
        labelContainer.add(eNavServices);
        // labelContainer.add(routeSettings);

        addMouseListeners();
    }

    public void hideMapTabs() {
        for (int i = 0; i < mapWindowsList.size(); i++) {
            mapWindowsList.get(i).setVisible(false);
        }
    }

}

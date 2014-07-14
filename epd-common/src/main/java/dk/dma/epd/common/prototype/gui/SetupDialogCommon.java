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
package dk.dma.epd.common.prototype.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.common.prototype.gui.settings.CommonENavSettingsPanel;
import dk.dma.epd.common.prototype.gui.settings.CommonMapSettingsPanel;
import dk.dma.epd.common.prototype.settings.Settings;
import dk.dma.epd.common.prototype.settings.gui.MapCommonSettings;
import dk.dma.epd.common.prototype.settings.handlers.MSIHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.handlers.MetocHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.ENCLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.MSILayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.WMSLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.network.NetworkSettings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the common setup GUI for ship and shore.
 * 
 * @author adamduehansen
 * 
 */
public class SetupDialogCommon extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    
    private Timer timer = new Timer(500, this);
    private JButton btnAccept;
    private JButton btnCancel;
    private JTabbedPane tabbedPane;
    private List<BaseSettingsPanel> settingsPanels;

    // Common settings panels
    private CommonENavSettingsPanel enavSettings;
    private CommonMapSettingsPanel mapSettings;
    private Settings settings;
    private String tabPrefix;
    private int fontSize;

    /**
     * 
     * @param parent
     *            The parent frame.
     * @param title
     *            The title
     */
    public SetupDialogCommon(JFrame parent, String title, int tabAlignment) {
        super(parent, title, true);

        // Frame settings.
        setSize(462, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                timer.stop();
            }});
        setResizable(false);
        setLocationRelativeTo(parent);
        getContentPane().setLayout(new BorderLayout(0, 0));

        tabbedPane = new JTabbedPane(tabAlignment);

        // If the tabs is displayed on the left side, give them
        // more width and height and a larger font.
        if (tabAlignment == JTabbedPane.LEFT) {
            tabPrefix = "<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5 align=left>";
            fontSize = 13;
        } else {
            tabPrefix = "";
            fontSize = 10;
        }

        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        getContentPane().add(btnPanel, BorderLayout.SOUTH);

        btnAccept = new JButton("Accept", EPD.res().getCachedImageIcon("images/settings/btnok.png"));
        btnAccept.setEnabled(false);
        btnPanel.add(btnAccept);

        btnCancel = new JButton("Cancel", EPD.res().getCachedImageIcon("images/settings/btncancel.png"));
        btnPanel.add(btnCancel);

        // Get references to settings data.
        NetworkSettings<?> enavServicesHttpSettings = EPD.getInstance().getSettings().getEnavServicesHttpSettings();
        MetocHandlerCommonSettings<?> metocHandlerSettings = EPD.getInstance().getSettings().getMetocHandlerSettings();
        MSIHandlerCommonSettings<?> msiHandlerSettings = EPD.getInstance().getSettings().getMsiHandlerSettings();
        MSILayerCommonSettings<?> msiLayerSettings = EPD.getInstance().getSettings().getPrimaryMsiLayerSettings();
        MapCommonSettings<?> mapCommonSettings = EPD.getInstance().getSettings().getMapSettings();
        ENCLayerCommonSettings<?> encLayerSettings = EPD.getInstance().getSettings().getENCLayerSettings();
        WMSLayerCommonSettings<?> wmsLayerSettings = EPD.getInstance().getSettings().getPrimaryWMSLayerSettings();
        
        // Create the panels.
        settingsPanels = new ArrayList<BaseSettingsPanel>();
        enavSettings   = new CommonENavSettingsPanel(enavServicesHttpSettings, metocHandlerSettings, msiHandlerSettings, msiLayerSettings);
        mapSettings    = new CommonMapSettingsPanel(mapCommonSettings, encLayerSettings, wmsLayerSettings);

        // Register the panels to the tab menu.
        registerSettingsPanels(
                enavSettings, 
                mapSettings);

        btnAccept.addActionListener(this);
        btnCancel.addActionListener(this);
        
    }

    /**
     * Adds a setting panel to the tabbed pane section, and adds an icon infront of the title.
     * 
     * @param tabbedPane
     *            The tabbed pane.
     */
    protected void addTabs() {
        for (int i = 0; i < settingsPanels.size(); i++) {

            // Get the settingsPanel.
            BaseSettingsPanel newPanel = settingsPanels.get(i);

            // Add the panel.
            tabbedPane.add(newPanel);

            // Create icon and title for tab.
            JLabel panelTitle = new JLabel(tabPrefix + newPanel.getName());
            panelTitle.setFont(new Font(panelTitle.getFont().getName(), Font.PLAIN, fontSize));
            panelTitle.setIcon(newPanel.getIcon());
            panelTitle.setIconTextGap(5);
            panelTitle.setHorizontalTextPosition(SwingConstants.RIGHT);
            tabbedPane.setTabComponentAt(i, panelTitle);
        }
    }

    /**
     * Register the given settings panels.
     * 
     * @param settings
     *            The settings panel to register.
     */
    public void registerSettingsPanels(BaseSettingsPanel... settingsPanels) {

        for (BaseSettingsPanel baseSettingsPanel : settingsPanels) {
            this.settingsPanels.add(baseSettingsPanel);
            baseSettingsPanel.addListener(EPD.getInstance());
        }
    }
    
    /**
     * Resizes the settings panels in the setup dialog
     * to fit into the container.
     * @param container The container in which the panels are placed.
     */
    public void resizePanelsToFitContainer(Container container) {
        
        // The value to resize with.
        int resizeWidthWith = 20;
        
        // If the tab placement is placed on the left side, we need to add
        // some more width.
        if (tabbedPane.getTabPlacement() == JTabbedPane.LEFT) {
            resizeWidthWith = 188;
        }
        
        // For each setting panel get the components.
        for (BaseSettingsPanel baseSettingsPanel : settingsPanels) {
            Component[] components = baseSettingsPanel.getComponents();
            
            // Go through each of the settings panels components.
            for (Component component : components) {
                
                // If the component is an instance of JPanel, resize it.
                if (component instanceof JPanel) {
                    
                    component.setBounds(
                            component.getX(), 
                            component.getY(), 
                            container.getWidth()-resizeWidthWith, 
                            component.getHeight());
                }
            }
        }
    }

    /**
     * Initializes the settin panels with the settings.
     * 
     * @param settings
     *            The settings.
     */
    public void loadSettings(Settings settings) {
        this.settings = settings;
        for (BaseSettingsPanel baseSettingsPanel : settingsPanels) {
            // Load settings.
            baseSettingsPanel.setSettings(settings);
            baseSettingsPanel.loadSettings();
        }
        
        timer.start();
    }

    /**
     * Updates the global settings with the tab settings.
     * 
     * @return If there was made any changes.
     */
    public boolean saveSettings() {
        boolean changesWereMade = false;
        boolean restartRequired = false;
        for (BaseSettingsPanel baseSettingsPanel : settingsPanels) {
            restartRequired |= baseSettingsPanel.needsRestart();
            changesWereMade |= baseSettingsPanel.saveSettings();
        }

        if (changesWereMade) {
            settings.saveToFile();
        }
        
        if (restartRequired) {
            int result = JOptionPane.showConfirmDialog(
                    this, 
                    "The changes requires a restart to take effect.\nRestart now?", 
                    "Restart Required",
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                EPD.getInstance().closeApp(true);
            }
        }

        return changesWereMade;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Dialog buttons.
        if (e.getSource() == btnAccept) {
            
            // Save changes if changes were made.
            saveSettings();
            timer.stop();
            dispose();
            
        } else if (e.getSource() == btnCancel) {
            
            // Close the window.
            timer.stop();
            dispose();
        
        } else if (e.getSource() == timer) {
            checkSettingsChanged();
        }
    }
    
    /**
     * Checks if the settings have changed and update the OK button enabled state
     * 
     * @return
     */
    public boolean checkSettingsChanged() {
        if (!isVisible()) {
            return false;
        }
        boolean changed = false;
        for (BaseSettingsPanel settingsPanel : settingsPanels) {
            changed |= settingsPanel.settingsChanged();
        }
        btnAccept.setEnabled(changed);
        return changed;
    }

    /**
     * Sets the given tab as the active tab
     * @param tabNumber the tab index
     */
    public void setActivePanel(int tabNumber) {
        tabbedPane.setSelectedIndex(tabNumber);
    }

}

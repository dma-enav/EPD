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
package dk.dma.epd.common.prototype.gui;

import javax.swing.ImageIcon;
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
import dk.dma.epd.common.prototype.event.SetupDialogHandler;
import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.common.prototype.gui.settings.CommonENavSettingsPanel;
import dk.dma.epd.common.prototype.gui.settings.CommonMapSettingsPanel;
import dk.dma.epd.common.prototype.settings.Settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the common setup GUI for ship and shore.
 * 
 * @author adamduehansen
 * 
 */
public class SetupDialogCommon extends JDialog {

    /**
     * private fields.
     */
    private static final long serialVersionUID = 1L;
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
    private JButton btnWarningCancel;
    private JButton btnWarningOkay;
    private SetupDialogHandler dialogListener;

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
        this.setSize(462, 720);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(parent);
        this.getContentPane().setLayout(new BorderLayout(0, 0));

        this.tabbedPane = new JTabbedPane(tabAlignment);

        // If the tabs is displayed on the left side, give them
        // more width and height and a larger font.
        if (tabAlignment == JTabbedPane.LEFT) {
            tabPrefix = "<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5 align=left>";
            fontSize = 13;
        } else {
            tabPrefix = "";
            fontSize = 10;
        }

        this.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        this.getContentPane().add(btnPanel, BorderLayout.SOUTH);

        this.btnAccept = new JButton("Accept", new ImageIcon(SetupDialogCommon.class.getResource("/images/settings/btnok.png")));
        this.btnAccept.setEnabled(false);
        btnPanel.add(this.btnAccept);

        this.btnCancel = new JButton("Cancel", new ImageIcon(SetupDialogCommon.class.getResource("/images/settings/btncancel.png")));
        btnPanel.add(this.btnCancel);

        // Create the panels.
        this.settingsPanels = new ArrayList<BaseSettingsPanel>();
        this.enavSettings   = new CommonENavSettingsPanel();
        this.mapSettings    = new CommonMapSettingsPanel();

        // Register the panels to the tab menu.
        this.registerSettingsPanels(
                enavSettings, 
                mapSettings);

        this.dialogListener = new SetupDialogHandler(this);
        this.addWindowListener(dialogListener);
        this.btnAccept.addActionListener(dialogListener);
        this.btnCancel.addActionListener(dialogListener);
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
            BaseSettingsPanel newPanel = this.settingsPanels.get(i);

            // Add the panel.
            tabbedPane.add(newPanel);

            // Create icon and title for tab.
            JLabel panelTitle = new JLabel(tabPrefix + newPanel.getName());
            panelTitle.setFont(new Font(panelTitle.getFont().getName(), Font.PLAIN, this.fontSize));
            panelTitle.setIcon(newPanel.getIcon());
            panelTitle.setIconTextGap(5);
            panelTitle.setHorizontalTextPosition(SwingConstants.RIGHT);
            this.tabbedPane.setTabComponentAt(i, panelTitle);
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
        if (this.tabbedPane.getTabPlacement() == JTabbedPane.LEFT) {
            resizeWidthWith = 188;
        }
        
        // For each setting panel get the components.
        for (BaseSettingsPanel baseSettingsPanel : this.settingsPanels) {
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
    }

    /**
     * Updates the global settings with the tab settings.
     * 
     * @return If there was made any changes.
     */
    public boolean saveSettings() {
        boolean changesWereMade = false;
        for (BaseSettingsPanel baseSettingsPanel : settingsPanels) {
            changesWereMade |= baseSettingsPanel.saveSettings();
        }

        if (changesWereMade) {
            settings.saveToFile();
        }

        return changesWereMade;
    }

    public JButton getAcceptButton() {
        return this.btnAccept;
    }

    public JButton getCancelButton() {
        return this.btnCancel;
    }

    public JButton getWarningAcceptButton() {
        return this.btnWarningOkay;
    }

    public JButton getWarningCancelButton() {
        return this.btnWarningCancel;
    }

    /**
     * Checks if the settings have changed and update the OK button enabled state
     * 
     * @return
     */
    public boolean checkSettingsChanged() {
        if (!this.isVisible()) {
            return false;
        }
        boolean changed = false;
        for (BaseSettingsPanel settingsPanel : settingsPanels) {
            changed |= settingsPanel.settingsChanged();
        }
        btnAccept.setEnabled(changed);
        return changed;
    }

    public void setActivePanel(int tabNumber) {
        tabbedPane.setSelectedIndex(tabNumber);
    }

    public int askIfShouldSaveChanges() {

        btnWarningOkay = new JButton("Yes", new ImageIcon(SetupDialogCommon.class.getResource("/images/settings/btnok.png")));

        // Add a value to the button, when it is pressed.
        btnWarningOkay.addActionListener(dialogListener);

        btnWarningCancel = new JButton("No", new ImageIcon(SetupDialogCommon.class.getResource("/images/settings/btncancel.png")));

        // Add a value to the button, when pressed.
        btnWarningCancel.addActionListener(dialogListener);

        return JOptionPane.showOptionDialog(this, "Some changes were made to the settings.\nShould these changes be saved?",
                "Changes were made", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] { btnWarningOkay,
                        btnWarningCancel }, btnWarningOkay);
    }

    public Timer getHandlerTimer() {
        return this.dialogListener.getTimer();
    }
}

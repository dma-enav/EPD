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

import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import dk.dma.epd.common.prototype.event.SetupDialogActionListener;
import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.common.prototype.gui.settings.ENavSettingsPanelCommon;
import dk.dma.epd.common.prototype.settings.Settings;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the common setup GUI for ship and shore.
 * 
 * @author adamduehansen
 *
 */
public class SetupDialogCommon extends JDialog {

    private static final long serialVersionUID = 1L;
    private JButton btnOk;
    private JButton btnCancel;
    private List<BaseSettingsPanel> settingsPanels;
    
    // Common settings panels
    private ENavSettingsPanelCommon enavSettings;
    
    private Settings settings;
    private JTabbedPane tabbedPane;
    
    /**
     * 
     * @param parent The parent frame.
     */
    public SetupDialogCommon(JFrame parent, String title, boolean modal) {
        super(parent, title, modal);
        
        // Frame settings.
        this.setSize(462, 720);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(parent);
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        
        this.btnOk = new JButton("OK");
        panel.add(this.btnOk);
        
        this.btnCancel = new JButton("Cancel");
        panel.add(this.btnCancel);
        
        // Create the panels.
        this.settingsPanels = new ArrayList<BaseSettingsPanel>();
        this.enavSettings   = new ENavSettingsPanelCommon();
        
        // Register the panels to the tab menu.
        registerSettingsPanels(enavSettings);
                
        SetupDialogActionListener dialogListener = new SetupDialogActionListener(this);
        this.btnOk.addActionListener(dialogListener);
        this.btnCancel.addActionListener(dialogListener);
    }

    /**
     * Adds a setting panel to the tabbed pane section, and adds an
     * icon infront of the title.
     * @param tabbedPane The tabbed pane.
     */
    private void addTabs(JTabbedPane tabbedPane) {
        for (int i = 0; i < settingsPanels.size(); i++) {
            // Add the panel.
            tabbedPane.add(this.settingsPanels.get(i));

            // Get the settingsPanel.
            BaseSettingsPanel newPanel = this.settingsPanels.get(i);
            
            // Create icon and title for tab.
            JLabel panelTitle = new JLabel(newPanel.getName());
            panelTitle.setIcon(newPanel.getIcon());
            panelTitle.setIconTextGap(5);
            panelTitle.setHorizontalTextPosition(SwingConstants.RIGHT);
            tabbedPane.setTabComponentAt(i, panelTitle);
        }
    }
    
    /**
     * Register the given settings panels.
     * @param settings The settings panel to register.
     */
    public void registerSettingsPanels(BaseSettingsPanel... settingsPanels) {
        for (BaseSettingsPanel baseSettingsPanel : settingsPanels) {
            this.settingsPanels.add(baseSettingsPanel);
        }
        
        addTabs(tabbedPane);
    }
    
    /**
     * Initializes the settin panels with the settings.
     * @param settings The settings.
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
    
    public JButton getOkButton() {
        return this.btnOk;
    }
    
    public JButton getCancelButton() {
        return this.btnCancel;
    }
}

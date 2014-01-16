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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.common.prototype.gui.settings.CloudSettingsPanel;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.setuptabs.AisTab;
import dk.dma.epd.ship.gui.setuptabs.ENavTab;
import dk.dma.epd.ship.gui.setuptabs.MapTab;
import dk.dma.epd.ship.gui.setuptabs.NavigationTab;
import dk.dma.epd.ship.gui.setuptabs.SensorTab;
import dk.dma.epd.ship.settings.EPDSettings;

/**
 * The setup dialog
 */
public class SetupDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;

    private EPDSettings settings;
    
    private JButton btnOk;
    private JButton btnCancel;
    
    // Settings tabs
    private AisTab aisTab               = new AisTab();
    private ENavTab enavTab             = new ENavTab();
    private NavigationTab navTab        = new NavigationTab();
    private SensorTab sensorTab         = new SensorTab();
    private MapTab mapTab               = new MapTab();
    private CloudSettingsPanel cloudTab = new CloudSettingsPanel();
    private BaseSettingsPanel[] settingsPanels = { 
            aisTab, enavTab, navTab, sensorTab, mapTab, cloudTab };
    
    /**
     * Constructor
     * 
     * @param parent the parent frame
     */
    public SetupDialog(JFrame parent) {
        super(parent, "Setup", true);
        
        setSize(462, 720);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        for (BaseSettingsPanel tab : settingsPanels) {
            tabbedPane.addTab(tab.getName(), null, tab, null);            
        }
        
        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        
        btnOk = new JButton("OK");
        btnOk.addActionListener(this);
        panel.add(btnOk);
        
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        panel.add(btnCancel);
    }
    
    /**
     * Initializes the tabs with the settings
     * @param settings the settings
     */
    public void loadSettings(EPDSettings settings) {
        this.settings = settings;
        for (BaseSettingsPanel tab : settingsPanels) {
            tab.loadSettings();            
        }
    }
    
    /**
     * Updates the global settings with the
     * tab settings
     */
    public void saveSettings() {
        for (BaseSettingsPanel tab : settingsPanels) {
            tab.saveSettings();            
        }
        settings.saveToFile();
    }

    /**
     * Called when OK or Cancel is clicked
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnOk){
            saveSettings();
            this.setVisible(false);
            int choice = JOptionPane.showOptionDialog(EPDShip.getInstance().getMainFrame(), "The settings will take effect next time the application is started.\nStop now?", "Restart required", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, JOptionPane.YES_OPTION);
            if(choice == JOptionPane.YES_OPTION) {
                EPDShip.closeApp();
            }
        }
        if(e.getSource() == btnCancel){
            this.setVisible(false);
        }
    }
}

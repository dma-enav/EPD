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

import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.setuptabs.AisTab;
import dk.dma.epd.ship.gui.setuptabs.CloudTab;
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
    private JButton btnOk;
    private JButton btnCancel;
    private AisTab aisTab;
    private ENavTab enavTab;
    private NavigationTab navTab;
    private SensorTab sensorTab;
    private MapTab mapTab;
    private EPDSettings settings;
    
    private CloudTab cloudTab;
    
    public SetupDialog(JFrame parent) {
        super(parent, "Setup", true);
        
        setSize(462, 720);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
//        Component comp = EeINS.getMainFrame().getChartPanel().getS52Layer().getGUI();
//        getContentPane().add(comp);
        
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        aisTab = new AisTab();
        tabbedPane.addTab("AIS", null, aisTab, null);
        
        enavTab = new ENavTab();
        tabbedPane.addTab("E-Nav Services", null, enavTab, null);
        
        navTab = new NavigationTab();
        tabbedPane.addTab("Navigation", null, navTab, null);
        
        sensorTab = new SensorTab();
        tabbedPane.addTab("Sensor", null, sensorTab, null);
        
        mapTab = new MapTab();
        tabbedPane.addTab("Map", null, mapTab, null);
        
        this.cloudTab = new CloudTab();
        tabbedPane.addTab("Cloud", null, cloudTab, null);
        
        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        
        btnOk = new JButton("OK");
        btnOk.addActionListener(this);
        panel.add(btnOk);
        
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        panel.add(btnCancel);
//        comp.setVisible(true);
    }
    
    public void loadSettings(EPDSettings settings) {
        this.settings = settings;
        aisTab.loadSettings(settings.getAisSettings(), settings.getNavSettings());
        enavTab.loadSettings(settings.getEnavSettings());
        navTab.loadSettings(settings.getNavSettings());
        sensorTab.loadSettings(settings.getSensorSettings());
        mapTab.loadSettings(settings.getMapSettings());
        this.cloudTab.loadSettings(settings.getEnavSettings());
    }
    
    public void saveSettings() {
        aisTab.saveSettings();
        enavTab.saveSettings();
        navTab.saveSettings();
        sensorTab.saveSettings();
        mapTab.saveSettings();
        this.cloudTab.saveSettings();
        settings.saveToFile();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnOk){
            saveSettings();
            this.setVisible(false);
            int choice = JOptionPane.showOptionDialog(EPDShip.getMainFrame(), "The settings will take effect next time the application is started.\nStop now?", "Restart required", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, JOptionPane.YES_OPTION);
            if(choice == JOptionPane.YES_OPTION) {
                EPDShip.closeApp();
            }
        }
        if(e.getSource() == btnCancel){
            this.setVisible(false);
        }
    }
}

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
package dk.dma.epd.ship.gui.setuptabs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.common.prototype.model.route.PartialRouteFilter.FilterType;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.settings.EPDCloudSettings;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JComboBox;

public class ShipServicesSettingsPanel extends BaseSettingsPanel implements ActionListener {
    
    private static final long serialVersionUID = 1L;
    private EPDCloudSettings cloudSettings;
    private JCheckBox chckbxBroadcastIntendedRoute;
    private JComboBox<String> comboBoxSelectMethod;
    private JPanel generalPanel;
    private JPanel useMeterPanel;
    private JSpinner spinnerForwardMeters;
    private JSpinner spinnerBackwardsMeters;
    private JSpinner spinnerForwardMinutes;
    private JSpinner spinnerBackwardsMinutes;
    private JPanel useMinutesPanel;
    private JPanel useNumberOfWayPoints;
    private JSpinner spinnerForwardNumbers;
    private JSpinner spinnerBackwardsNumber;
    
    public ShipServicesSettingsPanel() {
        super("Services", new ImageIcon(
                ShipServicesSettingsPanel.class.getResource("/images/settingspanels/services.png")));
        setLayout(null);
        
        
        /************** General settings ***************/
        
        generalPanel = new JPanel();
        generalPanel.setBorder(new TitledBorder(null, "Generel Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        generalPanel.setBounds(6, 6, 438, 245);
        generalPanel.setLayout(null);
        
        chckbxBroadcastIntendedRoute = new JCheckBox("Broadcast intended route");
        chckbxBroadcastIntendedRoute.setBounds(16, 20, 189, 20);
        generalPanel.add(chckbxBroadcastIntendedRoute);
        
        JLabel lblTimeBetweenBroadcast = new JLabel("Time between broadcast (min)");
        lblTimeBetweenBroadcast.setBounds(103, 47, 189, 16);
        generalPanel.add(lblTimeBetweenBroadcast);
        
        JSpinner spinnerTimeBetweenBroadcast = new JSpinner();
        spinnerTimeBetweenBroadcast.setBounds(16, 45, 75, 20);
        generalPanel.add(spinnerTimeBetweenBroadcast);
        
        JSpinner spinnerAdaptionTime = new JSpinner();
        spinnerAdaptionTime.setBounds(16, 70, 75, 20);
        generalPanel.add(spinnerAdaptionTime);
        
        JLabel lblAdaptionTime = new JLabel("Adaption time");
        lblAdaptionTime.setBounds(103, 72, 89, 16);
        generalPanel.add(lblAdaptionTime);
        
        DefaultComboBoxModel<String> defaultComboBox = new DefaultComboBoxModel<String>();
        defaultComboBox.addElement("Distance");
        defaultComboBox.addElement("Time");
        defaultComboBox.addElement("Number of way points");
        comboBoxSelectMethod = new JComboBox<String>(defaultComboBox);
        comboBoxSelectMethod.addActionListener(this);
        comboBoxSelectMethod.setBounds(16, 100, 176, 20);
        generalPanel.add(comboBoxSelectMethod);
        
        // Add the panel
        this.add(generalPanel);
        
        
        /*********** Combobox changing panels *************/
        
        /************** Use Distance panel. ***************/
        
        useMeterPanel = new JPanel();
        useMeterPanel.setBorder(new TitledBorder(
                null, "Use meters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        useMeterPanel.setLayout(null);
        useMeterPanel.setBounds(16, 132, 404, 90);
        
        JLabel lblForwardMetes = new JLabel("Forward:");
        lblForwardMetes.setBounds(16, 20, 54, 16);
        useMeterPanel.add(lblForwardMetes);
        
        spinnerForwardMeters = new JSpinner();
        spinnerForwardMeters.setBounds(98, 18, 75, 20);
        useMeterPanel.add(spinnerForwardMeters);
        
        JLabel lblForwardKnMeters = new JLabel("(kn)");
        lblForwardKnMeters.setBounds(185, 18, 24, 16);
        useMeterPanel.add(lblForwardKnMeters);
        
        JLabel lblBackwardsMeters = new JLabel("Backwards:");
        lblBackwardsMeters.setBounds(16, 48, 70, 16);
        useMeterPanel.add(lblBackwardsMeters);
        
        spinnerBackwardsMeters = new JSpinner();
        spinnerBackwardsMeters.setBounds(98, 46, 75, 20);
        useMeterPanel.add(spinnerBackwardsMeters);
        
        JLabel lblBackwardsKnMeters = new JLabel("(kn)");
        lblBackwardsKnMeters.setBounds(185, 48, 24, 16);
        useMeterPanel.add(lblBackwardsKnMeters);
        this.useMeterPanel.setVisible(false);
        
        
        /************** Use Minutes panel. ***************/
        
        useMinutesPanel = new JPanel();
        useMinutesPanel.setBorder(new TitledBorder(
                null, "Use minutes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        useMinutesPanel.setLayout(null);
        useMinutesPanel.setBounds(16, 132, 404, 90);
        
        JLabel lblForwardMinutes = new JLabel("Forward:");
        lblForwardMinutes.setBounds(16, 20, 54, 16);
        useMinutesPanel.add(lblForwardMinutes);
        
        spinnerForwardMinutes = new JSpinner();
        spinnerForwardMinutes.setBounds(98, 18, 75, 20);
        useMinutesPanel.add(spinnerForwardMinutes);
        
        JLabel lblForwardMin = new JLabel("(Min)");
        lblForwardMin.setBounds(185, 18, 24, 16);
        useMinutesPanel.add(lblForwardMin);
        
        JLabel lblBackwardsMinutes = new JLabel("Backwards:");
        lblBackwardsMinutes.setBounds(16, 48, 70, 16);
        useMinutesPanel.add(lblBackwardsMinutes);
        
        spinnerBackwardsMinutes = new JSpinner();
        spinnerBackwardsMinutes.setBounds(98, 46, 75, 20);
        useMinutesPanel.add(spinnerBackwardsMinutes);
        
        JLabel lblBackwardsMin = new JLabel("(Min)");
        lblBackwardsMin.setBounds(185, 48, 24, 16);
        useMinutesPanel.add(lblBackwardsMin);
        this.useMinutesPanel.setVisible(false);
        
        
        /************** Use Number of way points. ***************/
        
        useNumberOfWayPoints = new JPanel();
        useNumberOfWayPoints.setBorder(new TitledBorder(
                null, "Use number of way points", TitledBorder.LEADING, TitledBorder.TOP, null, null));
       
        useNumberOfWayPoints.setLayout(null);
        useNumberOfWayPoints.setBounds(16, 132, 404, 90);
        
        JLabel lblForwardNumber = new JLabel("Forward:");
        lblForwardNumber.setBounds(16, 20, 54, 16);
        useNumberOfWayPoints.add(lblForwardNumber);
        
        spinnerForwardNumbers = new JSpinner();
        spinnerForwardNumbers.setBounds(98, 18, 75, 20);
        useNumberOfWayPoints.add(spinnerForwardNumbers);
        
        JLabel lblForwardNumbers = new JLabel("(Way points)");
        lblForwardNumbers.setBounds(185, 18, 100, 16);
        useNumberOfWayPoints.add(lblForwardNumbers);
        
        JLabel lblBackwardsNumber = new JLabel("Backwards:");
        lblBackwardsNumber.setBounds(16, 48, 70, 16);
        useNumberOfWayPoints.add(lblBackwardsNumber);
        
        spinnerBackwardsNumber = new JSpinner();
        spinnerBackwardsNumber.setBounds(98, 46, 75, 20);
        useNumberOfWayPoints.add(spinnerBackwardsNumber);
        
        JLabel lblBackwardsNumbers = new JLabel("(Way points)");
        lblBackwardsNumbers.setBounds(185, 48, 100, 16);
        useNumberOfWayPoints.add(lblBackwardsNumbers);
        
        this.useNumberOfWayPoints.setVisible(false);
                
        this.generalPanel.add(this.useMeterPanel);
        this.generalPanel.add(this.useMinutesPanel);
        this.generalPanel.add(this.useNumberOfWayPoints);
        
        this.updateGui();
    }
    
    private void updateGui() {
        
        if (this.comboBoxSelectMethod.getSelectedItem().equals("Distance")) {

            this.useNumberOfWayPoints.setVisible(false);
            this.useMinutesPanel.setVisible(false);
            this.useMeterPanel.setVisible(true);
                        
        } else if (this.comboBoxSelectMethod.getSelectedItem().equals("Time")) {
            
            this.useNumberOfWayPoints.setVisible(false);
            this.useMeterPanel.setVisible(false);
            this.useMinutesPanel.setVisible(true);
                        
        } else if (this.comboBoxSelectMethod.getSelectedItem().equals("Number of way points")) {
            
            this.useMeterPanel.setVisible(false);
            this.useMinutesPanel.setVisible(false);
            this.useNumberOfWayPoints.setVisible(true);
            
        }
        
        this.generalPanel.repaint();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        updateGui();
    }

    @Override
    protected boolean checkSettingsChanged() {
        
        return 
                changed(this.cloudSettings.isBroadcastIntendedRoute(), this.chckbxBroadcastIntendedRoute.isSelected());
    }

    @Override
    protected void doLoadSettings() {
        
        this.cloudSettings = EPDShip.getInstance().getSettings().getCloudSettings();
        
        // Load cloud Intended Route Settings.
        this.chckbxBroadcastIntendedRoute.setSelected(this.cloudSettings.isBroadcastIntendedRoute());
        
        // Get filter type. 
        FilterType type = this.cloudSettings.getIntendedRouteFilter().getType();
        
        // Set the combo boks to the filter type.
        if (type.equals(FilterType.METERS)) {
            this.comboBoxSelectMethod.setSelectedItem("Distance");
        } else if (type.equals(FilterType.MINUTES)) {
            this.comboBoxSelectMethod.setSelectedItem("Time");
        } else if (type.equals(FilterType.COUNT)) {
            this.comboBoxSelectMethod.setSelectedItem("Number of way points");
        }
        
        // Load settings for combobox based on what filter type was loaded.
        if (this.comboBoxSelectMethod.getSelectedItem().equals("Distance")) {
            
            this.spinnerForwardMeters.setValue(this.cloudSettings.getIntendedRouteFilter().getForward());
            this.spinnerBackwardsMeters.setValue(this.cloudSettings.getIntendedRouteFilter().getBackward());
            
        } else if (this.comboBoxSelectMethod.getSelectedItem().equals("Time")) {
            
            this.spinnerForwardMinutes.setValue(this.cloudSettings.getIntendedRouteFilter().getForward());
            this.spinnerBackwardsMinutes.setValue(this.cloudSettings.getIntendedRouteFilter().getBackward());
            
        } else if (this.comboBoxSelectMethod.getSelectedItem().equals("Number of way points")) {
            
            this.spinnerForwardNumbers.setValue(this.cloudSettings.getIntendedRouteFilter().getForward());
            this.spinnerBackwardsNumber.setValue(this.cloudSettings.getIntendedRouteFilter().getBackward());
            
        }
            
    }

    @Override
    protected void doSaveSettings() {
        
        // Save cloud Intended Route Settings.
        this.cloudSettings.setBroadcastIntendedRoute(this.chckbxBroadcastIntendedRoute.isSelected());
        
        String type = (String) this.comboBoxSelectMethod.getSelectedItem();
        
        System.out.println("TYPE: "+type);
        
        // Save Partial Route Filter settings.
        if (type.equals("Distance")) {
            this.cloudSettings.getIntendedRouteFilter().setType(FilterType.METERS);
        } else if (type.equals("Time")) {
            this.cloudSettings.getIntendedRouteFilter().setType(FilterType.MINUTES);
        } else if (type.equals("Number of way points")) {
            this.cloudSettings.getIntendedRouteFilter().setType(FilterType.COUNT);
        }
        
        if (this.comboBoxSelectMethod.getSelectedItem().equals("Distance")) {
            
            this.cloudSettings.getIntendedRouteFilter().setForward(
                    (Integer) this.spinnerForwardMeters.getValue());
            
            this.cloudSettings.getIntendedRouteFilter().setBackward(
                    (Integer) this.spinnerBackwardsMeters.getValue());
            
        } else if (this.comboBoxSelectMethod.getSelectedItem().equals("Time")) {

            this.cloudSettings.getIntendedRouteFilter().setForward(
                    (Integer) this.spinnerForwardNumbers.getValue());
            
            this.cloudSettings.getIntendedRouteFilter().setBackward(
                    (Integer) this.spinnerBackwardsMinutes.getValue());
            
        } else if (this.comboBoxSelectMethod.getSelectedItem().equals("Number of way points")) {
            
            this.cloudSettings.getIntendedRouteFilter().setForward(
                    (Integer) this.spinnerForwardNumbers.getValue());
            
            this.cloudSettings.getIntendedRouteFilter().setBackward(
                    (Integer) this.spinnerBackwardsNumber.getValue());
            
        }
        
    }

    @Override
    protected void fireSettingsChanged() {
        fireSettingsChanged(Type.CLOUD);

    }
}

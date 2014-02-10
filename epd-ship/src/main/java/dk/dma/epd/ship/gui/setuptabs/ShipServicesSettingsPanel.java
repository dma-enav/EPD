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
import javax.swing.SpinnerNumberModel;

public class ShipServicesSettingsPanel extends BaseSettingsPanel implements ActionListener {
    
    private static final long serialVersionUID = 1L;
    private EPDCloudSettings cloudSettings;
    private JCheckBox chckbxBroadcastIntendedRoute;
    private JComboBox<String> comboBoxSelectMethod;
    private JPanel generalPanel;
    private JSpinner spinnerTimeBetweenBroadcast;
    private JSpinner spinnerAdaptionTime;
    private JSpinner spinnerForward;
    private JSpinner spinnerBackward;
    private JLabel lblTimeMeasurementForward;
    private JLabel lblTimeMesaurementBackward;
    private FilterType filterType;
    private FilterType selectedType;
    
    public ShipServicesSettingsPanel() {
        super("Services", new ImageIcon(
                ShipServicesSettingsPanel.class.getResource("/images/settingspanels/services.png")));
        this.setLayout(null);
        
        
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
        
        spinnerTimeBetweenBroadcast = new JSpinner();
        spinnerTimeBetweenBroadcast.setBounds(16, 45, 75, 20);
        generalPanel.add(spinnerTimeBetweenBroadcast);
        
        spinnerAdaptionTime = new JSpinner();
        spinnerAdaptionTime.setBounds(16, 70, 75, 20);
        generalPanel.add(spinnerAdaptionTime);
        
        JLabel lblAdaptionTime = new JLabel("Adaption time");
        lblAdaptionTime.setBounds(103, 72, 89, 16);
        generalPanel.add(lblAdaptionTime);
        
        DefaultComboBoxModel<String> defaultComboBox = new DefaultComboBoxModel<String>();
        defaultComboBox.addElement("Distance");
        defaultComboBox.addElement("Time");
        defaultComboBox.addElement("Number of way points");
        
        // Add the panel
        this.add(generalPanel);
        
        
        /*********** Intended route filter *************/
        
        JPanel intendedRouteFilterPanel = new JPanel();
        intendedRouteFilterPanel.setBounds(16, 102, 405, 120);
        intendedRouteFilterPanel.setBorder(new TitledBorder(
                null, "Intended Route Filter"));
        generalPanel.add(intendedRouteFilterPanel);
        intendedRouteFilterPanel.setLayout(null);
        
        JLabel lblForward = new JLabel("Forward:");
        lblForward.setBounds(16, 54, 54, 16);
        intendedRouteFilterPanel.add(lblForward);
        
        JLabel lblBackward = new JLabel("Backward:");
        lblBackward.setBounds(16, 79, 63, 16);
        intendedRouteFilterPanel.add(lblBackward);
        
        spinnerForward = new JSpinner(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
        spinnerForward.setBounds(91, 52, 75, 20);
        intendedRouteFilterPanel.add(spinnerForward);
        
        spinnerBackward = new JSpinner(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
        spinnerBackward.setBounds(91, 77, 75, 20);
        intendedRouteFilterPanel.add(spinnerBackward);
        
        lblTimeMeasurementForward = new JLabel("null");
        lblTimeMeasurementForward.setBounds(178, 54, 210, 16);
        intendedRouteFilterPanel.add(lblTimeMeasurementForward);
        
        lblTimeMesaurementBackward = new JLabel("null");
        lblTimeMesaurementBackward.setBounds(178, 79, 210, 16);
        intendedRouteFilterPanel.add(lblTimeMesaurementBackward);
        comboBoxSelectMethod = new JComboBox<String>(defaultComboBox);
        comboBoxSelectMethod.setBounds(16, 20, 176, 20);
        intendedRouteFilterPanel.add(comboBoxSelectMethod);
        comboBoxSelectMethod.addActionListener(this);
        
        this.updateGui();
    }
    
    /**
     * Updates the gui from what the user has selected.
     */
    private void updateGui() {

        String measurement = "";
        selectedType = null;
        
        if (this.comboBoxSelectMethod.getSelectedIndex() == 0) {
            
            measurement = "(kn)";
            selectedType = FilterType.METERS;
            
        } else if (this.comboBoxSelectMethod.getSelectedIndex() == 1) {
            
            measurement = "(hour)";
            selectedType = FilterType.MINUTES;
            
        } else if (this.comboBoxSelectMethod.getSelectedIndex() == 2) {
            
            measurement = "(Number of way point)";
            selectedType = FilterType.COUNT;
        }
        
        this.setMeasurementLabels(measurement);
        this.setSpinnerValues(selectedType);
    }

    /**
     * Updates the value of the forward and backward spinner by the
     * selected FilterType Enum.
     * @param type The selected FilterType Enum.
     */
    private void setSpinnerValues(FilterType type) {
        
        if (this.filterType != null) {
            
            if (this.filterType.equals(type)) {
                this.spinnerForward.setValue(this.cloudSettings.getIntendedRouteFilter().getForward());
                this.spinnerBackward.setValue(this.cloudSettings.getIntendedRouteFilter().getBackward());
            } else {
                this.spinnerForward.setValue(0);
                this.spinnerBackward.setValue(0);
            }
        }
    }
    
    /**
     * Updates the text in the measurement labels.
     * @param measurement The text to be written in the measurement labels.
     */
    private void setMeasurementLabels(String measurement) {
        this.lblTimeMeasurementForward.setText(measurement);
        this.lblTimeMesaurementBackward.setText(measurement);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        updateGui();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        
        boolean changesWereMade = 
                changed(this.cloudSettings.isBroadcastIntendedRoute(), this.chckbxBroadcastIntendedRoute.isSelected()) ||
                changed(this.cloudSettings.getTimeBetweenBroadCast(), this.spinnerTimeBetweenBroadcast.getValue()) ||
                changed(this.cloudSettings.getAdaptionTime(), this.spinnerAdaptionTime.getValue()) ||
                changed(this.cloudSettings.getIntendedRouteFilter().getType(), this.selectedType) ||
                changed(this.cloudSettings.getIntendedRouteFilter().getForward(), this.spinnerForward.getValue()) ||
                changed(this.cloudSettings.getIntendedRouteFilter().getBackward(), this.spinnerBackward.getValue());

        return changesWereMade;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {

        // Get the settings.
        this.cloudSettings = EPDShip.getInstance().getSettings().getCloudSettings();
        
        // Load cloud Intended Route Settings.
        this.chckbxBroadcastIntendedRoute.setSelected(this.cloudSettings.isBroadcastIntendedRoute());
        this.spinnerTimeBetweenBroadcast.setValue(this.cloudSettings.getTimeBetweenBroadCast());
        this.spinnerAdaptionTime.setValue(this.cloudSettings.getAdaptionTime());
        
        filterType = this.cloudSettings.getIntendedRouteFilter().getType();
        
        // Set combobox selection.
        if (filterType.equals(FilterType.METERS)) {
            this.comboBoxSelectMethod.setSelectedIndex(0);
        } else if (filterType.equals(FilterType.MINUTES)) {
            this.comboBoxSelectMethod.setSelectedIndex(1);
        } else if (filterType.equals(FilterType.COUNT)) {
            this.comboBoxSelectMethod.setSelectedIndex(2);
        }
        
        // Set value of backward and forward.
        this.spinnerForward.setValue(this.cloudSettings.getIntendedRouteFilter().getForward());
        this.spinnerBackward.setValue(this.cloudSettings.getIntendedRouteFilter().getBackward());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
        
        System.out.println(this.chckbxBroadcastIntendedRoute.isSelected());
        
        // Save cloud Intended Route Settings.
        this.cloudSettings.setBroadcastIntendedRoute(this.chckbxBroadcastIntendedRoute.isSelected());
        this.cloudSettings.setTimeBetweenBroadCast((Integer) this.spinnerTimeBetweenBroadcast.getValue());
        this.cloudSettings.setAdaptionTime((Integer) this.spinnerAdaptionTime.getValue());
        
        // Save Intended route filter settings.
        this.cloudSettings.getIntendedRouteFilter().setType(this.selectedType);
        this.cloudSettings.getIntendedRouteFilter().setForward((Integer) this.spinnerForward.getValue());
        this.cloudSettings.getIntendedRouteFilter().setBackward((Integer) this.spinnerBackward.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
        fireSettingsChanged(Type.CLOUD);

    }
}

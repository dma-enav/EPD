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
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.common.prototype.gui.settings.IntendedRouteFilterSettingsPanel;
import dk.dma.epd.common.prototype.model.route.PartialRouteFilter.FilterType;
import dk.dma.epd.common.prototype.settings.EnavSettings;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.settings.EPDCloudSettings;

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
    private IntendedRouteFilterSettingsPanel intendedRouteFilterSettingsPanel;
    private EnavSettings enavSettings;
    
    public ShipServicesSettingsPanel() {
        super("Services", new ImageIcon(
                ShipServicesSettingsPanel.class.getResource("/images/settingspanels/services.png")));
        this.setLayout(null);
        
        
        /************** General settings ***************/
        
        generalPanel = new JPanel();
        generalPanel.setBorder(new TitledBorder(null, "Generel Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        generalPanel.setBounds(6, 6, 438, 110);
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
        
        
        /*********** Intended route *************/
                
        JPanel intendedRoutePanel = new JPanel();
        intendedRoutePanel.setBounds(6, 128, 438, 120);
        intendedRoutePanel.setBorder(new TitledBorder(
                null, "Intended Route"));
        intendedRoutePanel.setLayout(null);
        
        JLabel lblForward = new JLabel("Forward:");
        lblForward.setBounds(16, 54, 54, 16);
        intendedRoutePanel.add(lblForward);
        
        JLabel lblBackward = new JLabel("Backward:");
        lblBackward.setBounds(16, 79, 63, 16);
        intendedRoutePanel.add(lblBackward);
        
        spinnerForward = new JSpinner(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
        spinnerForward.setBounds(91, 52, 75, 20);
        intendedRoutePanel.add(spinnerForward);
        
        spinnerBackward = new JSpinner(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
        spinnerBackward.setBounds(91, 77, 75, 20);
        intendedRoutePanel.add(spinnerBackward);
        
        lblTimeMeasurementForward = new JLabel("null");
        lblTimeMeasurementForward.setBounds(178, 54, 210, 16);
        intendedRoutePanel.add(lblTimeMeasurementForward);
        
        lblTimeMesaurementBackward = new JLabel("null");
        lblTimeMesaurementBackward.setBounds(178, 79, 210, 16);
        intendedRoutePanel.add(lblTimeMesaurementBackward);
        comboBoxSelectMethod = new JComboBox<String>(defaultComboBox);
        comboBoxSelectMethod.setBounds(16, 20, 176, 20);
        this.comboBoxSelectMethod.addActionListener(this);
        intendedRoutePanel.add(comboBoxSelectMethod);
        
        this.add(intendedRoutePanel);

        
        /*********** Intended route filter *************/

        this.intendedRouteFilterSettingsPanel = new IntendedRouteFilterSettingsPanel();
        this.add(this.intendedRouteFilterSettingsPanel);
        // Update gui.
        this.updateGui();
    }
    
    /**
     * Updates the gui from what the user has selected.
     */
    private void updateGui() {

        String measurement = ""; // The unit which the filter type is useing.
        selectedType = null;     // The selected filter type.
        
        // Set measurement unit and selected filter type based on what
        // index is chosen in the combobox.
        if (this.comboBoxSelectMethod.getSelectedIndex() == 0) {
            
            measurement = "(nm)";
            selectedType = FilterType.METERS;
            
        } else if (this.comboBoxSelectMethod.getSelectedIndex() == 1) {
            
            measurement = "(hour)";
            selectedType = FilterType.MINUTES;
            
        } else if (this.comboBoxSelectMethod.getSelectedIndex() == 2) {
            
            measurement = "(Number of way point)";
            selectedType = FilterType.COUNT;
        }
        
        // Update measurement.
        this.setMeasurementLabels(measurement);
        
        // Update spinner values based on what filter type is selected in the combobox.
        this.setSpinnerValues(selectedType);
    }

    /**
     * Updates the value of the forward and backward spinner by the
     * selected FilterType Enum.
     * @param type The selected FilterType Enum.
     */
    private void setSpinnerValues(FilterType type) {
        // If the filter type is set to distance, set the forward and backward spinner models to
        // a double model, since nm is measured in double values.
        if (type.equals(FilterType.METERS)) {
            this.spinnerForward.setModel(new SpinnerNumberModel(new Double(1), new Double(0.001), null, new Double(0.001)));
            this.spinnerBackward.setModel(new SpinnerNumberModel(new Double(1), new Double(0.001), null, new Double(0.001)));
        
        // If the filter type is set to any other than distance, set the forward and backward
        // spinner models to a reguler integer model.
        } else {

            this.spinnerForward.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
            this.spinnerBackward.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
        }
        
        // If the selected filter type is the same as the one stored in the settings,
        // load the values into the forward and backward spinners.
        if (type.equals(this.filterType)) {
            if (this.comboBoxSelectMethod.getSelectedIndex() == 0) {
                this.spinnerForward.setValue(Converter.metersToNm(this.cloudSettings.getIntendedRouteFilter().getForward()));
                this.spinnerBackward.setValue(Converter.metersToNm(this.cloudSettings.getIntendedRouteFilter().getBackward()));                
            } else {
                this.spinnerForward.setValue(this.cloudSettings.getIntendedRouteFilter().getForward());
                this.spinnerBackward.setValue(this.cloudSettings.getIntendedRouteFilter().getBackward());
            }

        // If the selected filter type is not the same as the one stored in the settings,
        // just load 0 into the spinners, to show that the user should init new values.
        } else {
            if (this.comboBoxSelectMethod.getSelectedIndex() == 0) {
                this.spinnerForward.setValue(0.001);
                this.spinnerBackward.setValue(0.001);
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
        
        // Update gui everytime a filter type is selected.
        updateGui();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        
        // Check for changes in generel settings.
        boolean changesWereMade = 
                changed(this.cloudSettings.isBroadcastIntendedRoute(), this.chckbxBroadcastIntendedRoute.isSelected()) ||
                changed(this.cloudSettings.getTimeBetweenBroadCast(), this.spinnerTimeBetweenBroadcast.getValue()) ||
                changed(this.cloudSettings.getAdaptionTime(), this.spinnerAdaptionTime.getValue()) ||
                changed(this.cloudSettings.getIntendedRouteFilter().getType(), this.selectedType) ||
                
                // Changes in enav settings.
                changed(this.enavSettings.getRouteTimeToLive(), 
                        TimeUnit.MINUTES.toMillis(this.intendedRouteFilterSettingsPanel.getTimeToLive())) ||
                changed(this.enavSettings.getNotificationDistance(), this.intendedRouteFilterSettingsPanel.getNotificationDistance()) ||
                changed(this.enavSettings.getAlertDistance(), this.intendedRouteFilterSettingsPanel.getAlertDistance()) ||
                changed(this.enavSettings.getFilterDistance(), this.intendedRouteFilterSettingsPanel.getFilterDistance());
        
        // If no changes were made to the other settings, check if changes were
        // made to the intended router filter settings.
        if (!changesWereMade) {
            
            // If the filter type is set to distance, convert the forward and backward 
            // values to meter and compare them to what is stored in the settings.
            if (this.filterType.equals(FilterType.METERS)) {
                changesWereMade = 
                        changed(this.cloudSettings.getIntendedRouteFilter().getForward(), 
                                Math.round(Converter.nmToMeters((double) this.spinnerForward.getValue()))) ||
                        changed(this.cloudSettings.getIntendedRouteFilter().getBackward(), 
                                Math.round(Converter.nmToMeters((double) this.spinnerBackward.getValue())));
            
            // If the filter type is set to any other type, compare the forward and backward
            // values to what is stored in the settings.
            } else {
                changesWereMade =
                        changed(this.cloudSettings.getIntendedRouteFilter().getForward(), this.spinnerForward.getValue()) ||
                        changed(this.cloudSettings.getIntendedRouteFilter().getBackward(), this.spinnerBackward.getValue());
            }
        }

        return changesWereMade;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {

        // Get cloud settings.
        this.cloudSettings = EPDShip.getInstance().getSettings().getCloudSettings();
        this.enavSettings = EPDShip.getInstance().getSettings().getEnavSettings();
        
        // Load cloud Intended Route Settings.
        this.chckbxBroadcastIntendedRoute.setSelected(this.cloudSettings.isBroadcastIntendedRoute());
        this.spinnerTimeBetweenBroadcast.setValue(this.cloudSettings.getTimeBetweenBroadCast());
        this.spinnerAdaptionTime.setValue(this.cloudSettings.getAdaptionTime());
        
        this.filterType = this.cloudSettings.getIntendedRouteFilter().getType();
        
        // Set combobox selection.
        if (filterType.equals(FilterType.METERS)) {
            this.comboBoxSelectMethod.setSelectedIndex(0);
        } else if (filterType.equals(FilterType.MINUTES)) {
            this.comboBoxSelectMethod.setSelectedIndex(1);
        } else if (filterType.equals(FilterType.COUNT)) {
            this.comboBoxSelectMethod.setSelectedIndex(2);
        }
        
        // Set value of backward and forward.
        if (filterType.equals(FilterType.METERS)) {
            this.spinnerForward.setValue(Converter.metersToNm(this.cloudSettings.getIntendedRouteFilter().getForward()));
            this.spinnerBackward.setValue(Converter.metersToNm(this.cloudSettings.getIntendedRouteFilter().getBackward())); 
        } else {
            this.spinnerForward.setValue(this.cloudSettings.getIntendedRouteFilter().getForward());
            this.spinnerBackward.setValue(this.cloudSettings.getIntendedRouteFilter().getBackward()); 
        }
        
        // Load intended route filter settings.
        this.intendedRouteFilterSettingsPanel.setFilterDistance(this.enavSettings.getFilterDistance());
        this.intendedRouteFilterSettingsPanel.setTimeToLive(this.enavSettings.getRouteTimeToLive());
        this.intendedRouteFilterSettingsPanel.setAlertDistance(this.enavSettings.getAlertDistance());
        this.intendedRouteFilterSettingsPanel.setNotificationDistance(this.enavSettings.getNotificationDistance());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
                
        // Save cloud Intended Route Settings.
        this.cloudSettings.setBroadcastIntendedRoute(this.chckbxBroadcastIntendedRoute.isSelected());
        this.cloudSettings.setTimeBetweenBroadCast((Integer) this.spinnerTimeBetweenBroadcast.getValue());
        this.cloudSettings.setAdaptionTime((Integer) this.spinnerAdaptionTime.getValue());
        
        // Save Intended route filter settings.
        this.cloudSettings.getIntendedRouteFilter().setType(this.selectedType);
        
        // If the filter type is set to distance, the forward and backward values are
        // converted to meters and stored in the settings.
        if (this.comboBoxSelectMethod.getSelectedIndex() == 0) {
            this.cloudSettings.getIntendedRouteFilter().setForward((int) Math.round(Converter.nmToMeters((double) this.spinnerForward.getValue())));
            this.cloudSettings.getIntendedRouteFilter().setBackward((int) Math.round(Converter.nmToMeters((double) this.spinnerBackward.getValue())));
        // If the filter tupe is set to any other than distance, just save the
        // forward and backward values to the settings.
        } else {
            this.cloudSettings.getIntendedRouteFilter().setForward((int) this.spinnerForward.getValue());
            this.cloudSettings.getIntendedRouteFilter().setBackward((int) this.spinnerBackward.getValue());
        }
        
        // Save enav settings.
        this.enavSettings.setFilterDistance(this.intendedRouteFilterSettingsPanel.getFilterDistance());
        this.enavSettings.setAlertDistance(this.intendedRouteFilterSettingsPanel.getAlertDistance());
        this.enavSettings.setNotificationDistance(this.intendedRouteFilterSettingsPanel.getNotificationDistance());
        this.enavSettings.setRouteTimeToLive(TimeUnit.MINUTES.toMillis(this.intendedRouteFilterSettingsPanel.getTimeToLive()));
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
        fireSettingsChanged(Type.CLOUD);
    }
}

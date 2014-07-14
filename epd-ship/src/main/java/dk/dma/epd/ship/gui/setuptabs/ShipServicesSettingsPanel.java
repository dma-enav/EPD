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
import java.util.Objects;

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
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.ship.settings.handlers.IntendedRouteHandlerSettings;

public class ShipServicesSettingsPanel extends BaseSettingsPanel implements ActionListener {
    
    private static final long serialVersionUID = 1L;
    private JCheckBox chckbxBroadcastIntendedRoute;
    private JComboBox<String> comboBoxSelectMethod;
    private JPanel generalPanel;
    private JSpinner spinnerTimeBetweenBroadcast;
    private JSpinner spinnerAdaptionTime;
    private JSpinner spinnerForward;
    private JSpinner spinnerBackward;
    private JLabel lblTimeMeasurementForward;
    private JLabel lblTimeMesaurementBackward;
    private FilterType selectedType;
    private IntendedRouteFilterSettingsPanel intendedRouteFilterSettingsPanel;
    private IntendedRouteHandlerSettings handlerSettings;
    
    public ShipServicesSettingsPanel(IntendedRouteHandlerSettings handlerSettings) {
        super("Services", new ImageIcon(
                ShipServicesSettingsPanel.class.getResource("/images/settingspanels/services.png")));
        this.handlerSettings = Objects.requireNonNull(handlerSettings);
        this.setLayout(null);
        
        
        /************** General settings ***************/
        
        generalPanel = new JPanel();
        generalPanel.setBorder(new TitledBorder(null, "Generel Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        generalPanel.setBounds(6, 6, 438, 110);
        generalPanel.setLayout(null);
        
        chckbxBroadcastIntendedRoute = new JCheckBox("Broadcast intended route");
        chckbxBroadcastIntendedRoute.setBounds(16, 20, 189, 20);
        generalPanel.add(chckbxBroadcastIntendedRoute);
        
        JLabel lblTimeBetweenBroadcast = new JLabel("Time between broadcast (seconds)");
        lblTimeBetweenBroadcast.setBounds(103, 47, 189, 16);
        generalPanel.add(lblTimeBetweenBroadcast);
        
        spinnerTimeBetweenBroadcast = new JSpinner(new SpinnerNumberModel(new Long(1), new Long(1), null, new Long(1)));
        spinnerTimeBetweenBroadcast.setBounds(16, 45, 75, 20);
        generalPanel.add(spinnerTimeBetweenBroadcast);
        
        spinnerAdaptionTime = new JSpinner();
        spinnerAdaptionTime.setBounds(16, 70, 75, 20);
        generalPanel.add(spinnerAdaptionTime);
        
        JLabel lblAdaptionTime = new JLabel("Adaption time (seconds)");
        lblAdaptionTime.setBounds(103, 72, 120, 16);
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

        String measurement = ""; // The unit which the filter type is using.
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
        if (type.equals(handlerSettings.getIntendedRouteFilter().getType())) {
            if (this.comboBoxSelectMethod.getSelectedIndex() == 0) {
                this.spinnerForward.setValue(Converter.metersToNm(this.handlerSettings.getIntendedRouteFilter().getForward()));
                this.spinnerBackward.setValue(Converter.metersToNm(this.handlerSettings.getIntendedRouteFilter().getBackward()));                
            } else {
                this.spinnerForward.setValue(this.handlerSettings.getIntendedRouteFilter().getForward());
                this.spinnerBackward.setValue(this.handlerSettings.getIntendedRouteFilter().getBackward());
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
                changed(this.handlerSettings.isBroadcastIntendedRoute(), this.chckbxBroadcastIntendedRoute.isSelected()) ||
                changed(this.handlerSettings.getTimeBetweenBroadCast(), this.spinnerTimeBetweenBroadcast.getValue()) ||
                changed(this.handlerSettings.getAdaptionTime(), this.spinnerAdaptionTime.getValue()) ||
                changed(this.handlerSettings.getIntendedRouteFilter().getType(), this.selectedType) ||
                
                // Changes in enav settings.
                changed(this.handlerSettings.getRouteTimeToLive(), 
                        TimeUnit.MINUTES.toMillis(this.intendedRouteFilterSettingsPanel.getTimeToLive())) ||
                changed(this.handlerSettings.getNotificationDistance(), this.intendedRouteFilterSettingsPanel.getNotificationDistance()) ||
                changed(this.handlerSettings.getAlertDistance(), this.intendedRouteFilterSettingsPanel.getAlertDistance()) ||
                changed(this.handlerSettings.getFilterDistance(), this.intendedRouteFilterSettingsPanel.getFilterDistance());
        
        // If no changes were made to the other settings, check if changes were
        // made to the intended router filter settings.
        if (!changesWereMade) {
            
            // If the filter type is set to distance, convert the forward and backward 
            // values to meter and compare them to what is stored in the settings.
            if (handlerSettings.getIntendedRouteFilter().getType().equals(FilterType.METERS)) {
                changesWereMade = 
                        changed(this.handlerSettings.getIntendedRouteFilter().getForward(), 
                                Math.round(Converter.nmToMeters((double) this.spinnerForward.getValue()))) ||
                        changed(this.handlerSettings.getIntendedRouteFilter().getBackward(), 
                                Math.round(Converter.nmToMeters((double) this.spinnerBackward.getValue())));
            
            // If the filter type is set to any other type, compare the forward and backward
            // values to what is stored in the settings.
            } else {
                changesWereMade =
                        changed(this.handlerSettings.getIntendedRouteFilter().getForward(), this.spinnerForward.getValue()) ||
                        changed(this.handlerSettings.getIntendedRouteFilter().getBackward(), this.spinnerBackward.getValue());
            }
        }

        return changesWereMade;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {
        
        // Load cloud Intended Route Settings.
        this.chckbxBroadcastIntendedRoute.setSelected(this.handlerSettings.isBroadcastIntendedRoute());
        this.spinnerTimeBetweenBroadcast.setValue(this.handlerSettings.getTimeBetweenBroadCast());
        this.spinnerAdaptionTime.setValue(this.handlerSettings.getAdaptionTime());
        
        // Set combobox selection.
        if (handlerSettings.getIntendedRouteFilter().getType().equals(FilterType.METERS)) {
            this.comboBoxSelectMethod.setSelectedIndex(0);
        } else if (handlerSettings.getIntendedRouteFilter().getType().equals(FilterType.MINUTES)) {
            this.comboBoxSelectMethod.setSelectedIndex(1);
        } else if (handlerSettings.getIntendedRouteFilter().getType().equals(FilterType.COUNT)) {
            this.comboBoxSelectMethod.setSelectedIndex(2);
        }
        
        // Set value of backward and forward.
        if (handlerSettings.getIntendedRouteFilter().getType().equals(FilterType.METERS)) {
            this.spinnerForward.setValue(Converter.metersToNm(this.handlerSettings.getIntendedRouteFilter().getForward()));
            this.spinnerBackward.setValue(Converter.metersToNm(this.handlerSettings.getIntendedRouteFilter().getBackward())); 
        } else {
            this.spinnerForward.setValue(this.handlerSettings.getIntendedRouteFilter().getForward());
            this.spinnerBackward.setValue(this.handlerSettings.getIntendedRouteFilter().getBackward()); 
        }
        
        // Load intended route filter settings.
        this.intendedRouteFilterSettingsPanel.setFilterDistance(this.handlerSettings.getFilterDistance());
        this.intendedRouteFilterSettingsPanel.setTimeToLive(this.handlerSettings.getRouteTimeToLive());
        this.intendedRouteFilterSettingsPanel.setAlertDistance(this.handlerSettings.getAlertDistance());
        this.intendedRouteFilterSettingsPanel.setNotificationDistance(this.handlerSettings.getNotificationDistance());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
                
        // Save cloud Intended Route Settings.
        this.handlerSettings.setBroadcastIntendedRoute(this.chckbxBroadcastIntendedRoute.isSelected());
        this.handlerSettings.setTimeBetweenBroadCast((Long) this.spinnerTimeBetweenBroadcast.getValue());
        this.handlerSettings.setAdaptionTime((Integer) this.spinnerAdaptionTime.getValue());
        
        // Save Intended route filter settings.
        this.handlerSettings.getIntendedRouteFilter().setType(this.selectedType);
        
        // If the filter type is set to distance, the forward and backward values are
        // converted to meters and stored in the settings.
        if (this.comboBoxSelectMethod.getSelectedIndex() == 0) {
            this.handlerSettings.getIntendedRouteFilter().setForward((int) Math.round(Converter.nmToMeters((double) this.spinnerForward.getValue())));
            this.handlerSettings.getIntendedRouteFilter().setBackward((int) Math.round(Converter.nmToMeters((double) this.spinnerBackward.getValue())));
        // If the filter tupe is set to any other than distance, just save the
        // forward and backward values to the settings.
        } else {
            this.handlerSettings.getIntendedRouteFilter().setForward((int) this.spinnerForward.getValue());
            this.handlerSettings.getIntendedRouteFilter().setBackward((int) this.spinnerBackward.getValue());
        }
        
        // Save enav settings.
        this.handlerSettings.setFilterDistance(this.intendedRouteFilterSettingsPanel.getFilterDistance());
        this.handlerSettings.setAlertDistance(this.intendedRouteFilterSettingsPanel.getAlertDistance());
        this.handlerSettings.setNotificationDistance(this.intendedRouteFilterSettingsPanel.getNotificationDistance());
        this.handlerSettings.setRouteTimeToLive(TimeUnit.MINUTES.toMillis(this.intendedRouteFilterSettingsPanel.getTimeToLive()));
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
        fireSettingsChanged(Type.CLOUD);
    }
}

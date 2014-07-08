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
package dk.dma.epd.shore.gui.settingtabs;

import dk.dma.epd.common.prototype.gui.settings.CommonAisSettingsPanel;
import dk.dma.epd.common.prototype.settings.SensorSettings.SensorConnectionType;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.settings.EPDSensorSettings;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;

public class ShoreAisSettingsPanel extends CommonAisSettingsPanel {

    private static final long serialVersionUID = 1L;
    private JTextField textFieldAisHostOrSerialPort;
    private EPDSensorSettings settings;
    @SuppressWarnings("rawtypes")
    private JComboBox comboBoxAisConnectionType;
    private JSpinner spinnerTcpOrUpdPort;

    public ShoreAisSettingsPanel() {
        getTransponderPanel().setLocation(6, 136);

        
        /************** AIS Connection settings ***************/

        JPanel aisConnectionPanel = new JPanel();
        aisConnectionPanel.setBounds(6, 6, 438, 118);
        aisConnectionPanel.setLayout(null);
        aisConnectionPanel.setBorder(new TitledBorder(
                null, "AIS Connection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        JLabel lblConnectionType = new JLabel("Connection type");
        lblConnectionType.setBounds(16, 20, 103, 16);
        aisConnectionPanel.add(lblConnectionType);
        
        this.comboBoxAisConnectionType = new JComboBox<>(
                new DefaultComboBoxModel<>(EPDSensorSettings.SensorConnectionType.values()));
        this.comboBoxAisConnectionType.setBounds(144, 19, 134, 20);
        aisConnectionPanel.add(this.comboBoxAisConnectionType);
        
        JLabel lblHostOrSerial = new JLabel("Host or serial port");
        lblHostOrSerial.setBounds(16, 48, 115, 16);
        aisConnectionPanel.add(lblHostOrSerial);
        
        this.textFieldAisHostOrSerialPort = new JTextField();
        this.textFieldAisHostOrSerialPort.setBounds(144, 46, 134, 20);
        aisConnectionPanel.add(this.textFieldAisHostOrSerialPort);
        this.textFieldAisHostOrSerialPort.setColumns(10);
        
        JLabel lblTcpPort = new JLabel("TCP port");
        lblTcpPort.setBounds(16, 76, 61, 16);
        aisConnectionPanel.add(lblTcpPort);
        
        this.spinnerTcpOrUpdPort = new JSpinner();
        this.spinnerTcpOrUpdPort.setEditor(new NumberEditor(this.spinnerTcpOrUpdPort, "#"));
        this.spinnerTcpOrUpdPort.setBounds(144, 78, 134, 20);
        aisConnectionPanel.add(spinnerTcpOrUpdPort);
        
        this.add(aisConnectionPanel);
    }
    
    public void doLoadSettings() {
        
        // Load settings for common components.
        super.doLoadSettings();
        
        settings = EPDShore.getInstance().getSettings().getSensorSettings();
        
        // Load settings for AIS connection.
        this.comboBoxAisConnectionType.setSelectedItem(this.settings.getAisConnectionType());
        this.textFieldAisHostOrSerialPort.setText(this.settings.getAisHostOrSerialPort());
        this.spinnerTcpOrUpdPort.setValue(this.settings.getAisTcpOrUdpPort());
    }
    
    public void doSaveSettings() {
       
        // Save changes in AIS connection settings.
        this.settings.setAisConnectionType((SensorConnectionType) this.comboBoxAisConnectionType.getSelectedItem());
        this.settings.setAisHostOrSerialPort(this.textFieldAisHostOrSerialPort.getText());
        this.settings.setAisTcpOrUdpPort((Integer) this.spinnerTcpOrUpdPort.getValue());
    }
    
    public boolean checkSettingsChanged() {
        
        // First check if changes were made in common components.
        boolean changesWereMade = super.checkSettingsChanged();
        
        // Only check if changes were made in ship components if super.checkSettingsChanged
        // return false:
        // Consider a change were made to the common components but not the ship components. It
        // would result in "changesWereMade" to be false, and the changes in common components
        // would not be saved!
        if (!changesWereMade) {
            changesWereMade =
                    // Check for changes in AIS connection settings.
                    changed(this.settings.getAisConnectionType(), this.comboBoxAisConnectionType.getSelectedItem()) ||
                    changed(this.settings.getAisHostOrSerialPort(), this.textFieldAisHostOrSerialPort.getText()) ||
                    changed(this.settings.getAisTcpOrUdpPort(), this.spinnerTcpOrUpdPort.getValue());
        }
        
        return changesWereMade;
    }
}

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
package dk.dma.epd.shore.gui.settingtabs;

import java.util.Objects;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.gui.settings.CommonAisSettingsPanel;
import dk.dma.epd.common.prototype.settings.handlers.AisHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.sensor.ExternalSensorsCommonSettings.SensorConnectionType;
import dk.dma.epd.shore.settings.sensor.ExternalSensorsSettings;

public class ShoreAisSettingsPanel extends CommonAisSettingsPanel {

    private static final long serialVersionUID = 1L;
    protected ExternalSensorsSettings externalSensorsSettings;
    private JTextField textFieldAisHostOrSerialPort;
    @SuppressWarnings("rawtypes")
    private JComboBox comboBoxAisConnectionType;
    private JSpinner spinnerTcpOrUpdPort;

    public ShoreAisSettingsPanel(AisHandlerCommonSettings<AisHandlerCommonSettings.IObserver> aisHandlerSettings, ExternalSensorsSettings externalSensorsSettings) {
        super(aisHandlerSettings);
        this.externalSensorsSettings = Objects.requireNonNull(externalSensorsSettings);
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
                new DefaultComboBoxModel<>(ExternalSensorsSettings.SensorConnectionType.values()));
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
        
        // Load settings for AIS connection.
        this.comboBoxAisConnectionType.setSelectedItem(this.externalSensorsSettings.getAisConnectionType());
        this.textFieldAisHostOrSerialPort.setText(this.externalSensorsSettings.getAisHostOrSerialPort());
        this.spinnerTcpOrUpdPort.setValue(this.externalSensorsSettings.getAisTcpOrUdpPort());
    }
    
    public void doSaveSettings() {
       
        // Save changes in AIS connection settings.
        this.externalSensorsSettings.setAisConnectionType((SensorConnectionType) this.comboBoxAisConnectionType.getSelectedItem());
        this.externalSensorsSettings.setAisHostOrSerialPort(this.textFieldAisHostOrSerialPort.getText());
        this.externalSensorsSettings.setAisTcpOrUdpPort((Integer) this.spinnerTcpOrUpdPort.getValue());
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
                    changed(this.externalSensorsSettings.getAisConnectionType(), this.comboBoxAisConnectionType.getSelectedItem()) ||
                    changed(this.externalSensorsSettings.getAisHostOrSerialPort(), this.textFieldAisHostOrSerialPort.getText()) ||
                    changed(this.externalSensorsSettings.getAisTcpOrUdpPort(), this.spinnerTcpOrUpdPort.getValue());
        }
        
        return changesWereMade;
    }
}

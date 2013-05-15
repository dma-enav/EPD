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

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.settings.SensorSettings.SensorConnectionType;
import dk.dma.epd.ship.settings.EPDSensorSettings;


/**
 * Sensor tab panel in setup panel
 */
public class SensorTab extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JTextField textFieldAisHostOrSerialPort;
    private JSpinner spinnerAisTcpPort;
    private JTextField textFieldGpsHostOrSerialPort;
    private JTextField textFieldGpsFilename;
    private JSpinner spinnerGpsTcpPort;
    private JTextField textFieldAisFilename;
    private JComboBox<SensorConnectionType> comboBoxAisConnectionType;
    private JComboBox<SensorConnectionType> comboBoxGpsConnectionType;
    private JCheckBox startTransponder;
    private JSpinner spinnerAisSensorRange;
    private EPDSensorSettings sensorSettings;
    
    public SensorTab() {
        
        JPanel AisConnectionPanel = new JPanel();
        AisConnectionPanel.setBorder(new TitledBorder(null, "AIS Connection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        JLabel label_2 = new JLabel("Connection type");
        
        JLabel label_3 = new JLabel("TPC-port");
        
        JLabel label_4 = new JLabel("AIS-file name");
        
        JLabel label_5 = new JLabel("Host or serial port");
        
        textFieldAisHostOrSerialPort = new JTextField();
        textFieldAisHostOrSerialPort.setColumns(10);
        
        textFieldAisFilename = new JTextField();
        textFieldAisFilename.setColumns(10);
        
        spinnerAisTcpPort = new JSpinner();
        spinnerAisTcpPort.setEditor(new NumberEditor(spinnerAisTcpPort, "#"));
        
        comboBoxAisConnectionType = new JComboBox<>();
        comboBoxAisConnectionType.addActionListener(this);
        comboBoxAisConnectionType.setModel(new DefaultComboBoxModel<>(SensorConnectionType.values()));
        GroupLayout gl_AisConnectionPanel = new GroupLayout(AisConnectionPanel);
        gl_AisConnectionPanel.setHorizontalGroup(
            gl_AisConnectionPanel.createParallelGroup(Alignment.LEADING)
                .addGap(0, 429, Short.MAX_VALUE)
                .addGroup(gl_AisConnectionPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_AisConnectionPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(label_2)
                        .addComponent(label_3)
                        .addComponent(label_4)
                        .addComponent(label_5))
                    .addGap(14)
                    .addGroup(gl_AisConnectionPanel.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(textFieldAisHostOrSerialPort, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldAisFilename, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
                        .addComponent(spinnerAisTcpPort, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboBoxAisConnectionType, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE))
                    .addGap(161))
        );
        gl_AisConnectionPanel.setVerticalGroup(
            gl_AisConnectionPanel.createParallelGroup(Alignment.LEADING)
                .addGap(0, 135, Short.MAX_VALUE)
                .addGroup(gl_AisConnectionPanel.createSequentialGroup()
                    .addGroup(gl_AisConnectionPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_2)
                        .addComponent(comboBoxAisConnectionType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_AisConnectionPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_5)
                        .addComponent(textFieldAisHostOrSerialPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_AisConnectionPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_4)
                        .addComponent(textFieldAisFilename, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_AisConnectionPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_3)
                        .addComponent(spinnerAisTcpPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        AisConnectionPanel.setLayout(gl_AisConnectionPanel);
        
        JPanel GpsConnectionPanel = new JPanel();
        GpsConnectionPanel.setBorder(new TitledBorder(null, "GPS Connection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        JLabel label_6 = new JLabel("Connection type");
        
        JLabel label_7 = new JLabel("TPC-port");
        
        JLabel label_8 = new JLabel("GPS-file name");
        
        JLabel label_9 = new JLabel("Host or serial port");
        
        comboBoxGpsConnectionType = new JComboBox<>();
        comboBoxGpsConnectionType.addActionListener(this);
        comboBoxGpsConnectionType.setModel(new DefaultComboBoxModel<>(SensorConnectionType.values()));
        
        textFieldGpsHostOrSerialPort = new JTextField();
        textFieldGpsHostOrSerialPort.setColumns(10);
        
        textFieldGpsFilename = new JTextField();
        textFieldGpsFilename.setColumns(10);
        
        spinnerGpsTcpPort = new JSpinner();
        spinnerGpsTcpPort.setEditor(new NumberEditor(spinnerGpsTcpPort, "#"));
        
        GroupLayout gl_GpsConnectionPanel = new GroupLayout(GpsConnectionPanel);
        gl_GpsConnectionPanel.setHorizontalGroup(
            gl_GpsConnectionPanel.createParallelGroup(Alignment.LEADING)
                .addGap(0, 429, Short.MAX_VALUE)
                .addGroup(gl_GpsConnectionPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_GpsConnectionPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(label_6)
                        .addComponent(label_7)
                        .addComponent(label_8)
                        .addComponent(label_9))
                    .addGap(14)
                    .addGroup(gl_GpsConnectionPanel.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(comboBoxGpsConnectionType, 0, 137, Short.MAX_VALUE)
                        .addComponent(textFieldGpsHostOrSerialPort)
                        .addComponent(textFieldGpsFilename)
                        .addComponent(spinnerGpsTcpPort))
                    .addContainerGap(170, Short.MAX_VALUE))
        );
        gl_GpsConnectionPanel.setVerticalGroup(
            gl_GpsConnectionPanel.createParallelGroup(Alignment.LEADING)
                .addGap(0, 137, Short.MAX_VALUE)
                .addGroup(gl_GpsConnectionPanel.createSequentialGroup()
                    .addGroup(gl_GpsConnectionPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_6)
                        .addComponent(comboBoxGpsConnectionType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_GpsConnectionPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_9)
                        .addComponent(textFieldGpsHostOrSerialPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_GpsConnectionPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_8)
                        .addComponent(textFieldGpsFilename, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_GpsConnectionPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_7)
                        .addComponent(spinnerGpsTcpPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        GpsConnectionPanel.setLayout(gl_GpsConnectionPanel);
        
        JPanel transponderPanel = new JPanel();
        transponderPanel.setBorder(new TitledBorder(null, "Transponder", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        startTransponder = new JCheckBox("Start virtual transponder on startup");
        
        spinnerAisSensorRange = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        
        JLabel label = new JLabel("AIS sensor range");
        GroupLayout gl_SimulationPanel = new GroupLayout(transponderPanel);
        gl_SimulationPanel.setHorizontalGroup(
            gl_SimulationPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_SimulationPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_SimulationPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(startTransponder)
                        .addGroup(gl_SimulationPanel.createSequentialGroup()
                            .addComponent(spinnerAisSensorRange, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(label)))
                    .addContainerGap(239, Short.MAX_VALUE))
        );
        gl_SimulationPanel.setVerticalGroup(
            gl_SimulationPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_SimulationPanel.createSequentialGroup()
                    .addComponent(startTransponder)
                    .addGap(40)
                    .addGroup(gl_SimulationPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerAisSensorRange, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        transponderPanel.setLayout(gl_SimulationPanel);
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(transponderPanel, GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                        .addComponent(GpsConnectionPanel, GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                        .addComponent(AisConnectionPanel, GroupLayout.PREFERRED_SIZE, 313, Short.MAX_VALUE))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(AisConnectionPanel, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(GpsConnectionPanel, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(transponderPanel, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
                    .addGap(21))
        );
        setLayout(groupLayout);
    }
    
    public void loadSettings(EPDSensorSettings sensorSettings) {
        this.sensorSettings = sensorSettings;
        comboBoxAisConnectionType.getModel().setSelectedItem(sensorSettings.getAisConnectionType());
        textFieldAisHostOrSerialPort.setText(sensorSettings.getAisHostOrSerialPort());
        textFieldAisFilename.setText(sensorSettings.getAisFilename());
        spinnerAisTcpPort.setValue(sensorSettings.getAisTcpPort());
        
        comboBoxGpsConnectionType.getModel().setSelectedItem(sensorSettings.getGpsConnectionType());
        textFieldGpsHostOrSerialPort.setText(sensorSettings.getGpsHostOrSerialPort());
        textFieldGpsFilename.setText(sensorSettings.getGpsFilename());
        spinnerGpsTcpPort.setValue(sensorSettings.getGpsTcpPort());
        
        startTransponder.setSelected(sensorSettings.isStartTransponder());
        spinnerAisSensorRange.setValue(sensorSettings.getAisSensorRange());
    }
    
    public void saveSettings() {
        sensorSettings.setAisConnectionType((SensorConnectionType) comboBoxAisConnectionType.getModel().getSelectedItem());
        sensorSettings.setAisHostOrSerialPort(textFieldAisHostOrSerialPort.getText());
        sensorSettings.setAisFilename(textFieldAisFilename.getText());
        sensorSettings.setAisTcpPort((Integer) spinnerAisTcpPort.getValue());
        
        sensorSettings.setGpsConnectionType((SensorConnectionType) comboBoxGpsConnectionType.getModel().getSelectedItem());
        sensorSettings.setGpsHostOrSerialPort(textFieldGpsHostOrSerialPort.getText());
        sensorSettings.setGpsFilename(textFieldGpsFilename.getText());
        sensorSettings.setGpsTcpPort((Integer) spinnerGpsTcpPort.getValue());
        
        sensorSettings.setStartTransponder(startTransponder.isSelected());
        
        sensorSettings.setAisSensorRange((Double) spinnerAisSensorRange.getValue());
    }

    @Override
    public void actionPerformed(ActionEvent e) {        
        if(e.getSource() == comboBoxAisConnectionType) {
            if(comboBoxAisConnectionType.getModel().getSelectedItem() == SensorConnectionType.FILE) {
                textFieldAisFilename.setEnabled(true);
            } else {
                textFieldAisFilename.setEnabled(false);
            }
        }
        if(e.getSource() == comboBoxGpsConnectionType) {
            if(comboBoxGpsConnectionType.getModel().getSelectedItem() == SensorConnectionType.FILE) {
                textFieldGpsFilename.setEnabled(true);
            } else {
                textFieldGpsFilename.setEnabled(false);
            }
        }
    }
    
}

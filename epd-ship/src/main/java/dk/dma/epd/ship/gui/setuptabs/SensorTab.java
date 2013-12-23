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

import dk.dma.epd.common.prototype.settings.SensorSettings.PntSource;
import dk.dma.epd.common.prototype.settings.SensorSettings.SensorConnectionType;
import dk.dma.epd.ship.settings.EPDSensorSettings;


/**
 * Sensor tab panel in setup panel
 */
public class SensorTab extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    private JComboBox<PntSource> comboBoxPntSource;
    
    private JPanel AisConnectionPanel = new JPanel();
    private JTextField textFieldAisHostOrSerialPort;
    private JSpinner spinnerAisTcpPort;
    private JTextField textFieldAisFilename;
    private JComboBox<SensorConnectionType> comboBoxAisConnectionType;
    
    private JPanel GpsConnectionPanel = new JPanel();
    private JTextField textFieldGpsHostOrSerialPort;
    private JTextField textFieldGpsFilename;
    private JSpinner spinnerGpsTcpPort;
    private JComboBox<SensorConnectionType> comboBoxGpsConnectionType;
    
    private JPanel MsPntConnectionPanel = new JPanel();
    private JTextField textFieldMsPntHostOrSerialPort;
    private JTextField textFieldMsPntFilename;
    private JSpinner spinnerMsPntTcpPort;
    private JComboBox<SensorConnectionType> comboBoxMsPntConnectionType;
    
    private JCheckBox startTransponder;
    private JSpinner spinnerAisSensorRange;
    private EPDSensorSettings sensorSettings;
    
    /**
     * Constructor
     * Creates the GUI of the Sensor settings tab
     */
    public SensorTab() {
        
        /************** PNT Source ***************/
        
        JPanel PntSourcePanel = new JPanel();
        // NB: TitledBorder seems superfluous for the PNT source panel
        
        JLabel label_1 = new JLabel("PNT Source");

        comboBoxPntSource = new JComboBox<>();
        comboBoxPntSource.addActionListener(this);
        comboBoxPntSource.setModel(new DefaultComboBoxModel<>(PntSource.values()));
        GroupLayout gl_PntSourcePanel = new GroupLayout(PntSourcePanel);
        gl_PntSourcePanel.setHorizontalGroup(
            gl_PntSourcePanel.createParallelGroup(Alignment.LEADING)
                .addGap(0, 429, Short.MAX_VALUE)
                .addGroup(gl_PntSourcePanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_PntSourcePanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(label_1))
                    .addGap(14)
                    .addGroup(gl_PntSourcePanel.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(comboBoxPntSource, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE))
                    .addGap(161))
        );
        gl_PntSourcePanel.setVerticalGroup(
            gl_PntSourcePanel.createParallelGroup(Alignment.LEADING)
                .addGap(0, 135, Short.MAX_VALUE)
                .addGroup(gl_PntSourcePanel.createSequentialGroup()
                    .addGroup(gl_PntSourcePanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_1)
                        .addComponent(comboBoxPntSource, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PntSourcePanel.setLayout(gl_PntSourcePanel);
        
        
        /************** AIS Connection ***************/
        
        AisConnectionPanel.setBorder(new TitledBorder(null, "AIS Connection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        JLabel label_2 = new JLabel("Connection type");
        JLabel label_3 = new JLabel("TCP-port");
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

        
        /************** GPS Connection ***************/
        
        GpsConnectionPanel.setBorder(new TitledBorder(null, "GPS Connection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        JLabel label_6 = new JLabel("Connection type");
        JLabel label_7 = new JLabel("TCP-port");
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
        
        
        /************** Multi-source PNT Connection ***************/
        
        MsPntConnectionPanel.setBorder(new TitledBorder(null, "Multi-source PNT Connection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        JLabel label_10 = new JLabel("Connection type");
        JLabel label_11 = new JLabel("TCP-port");
        JLabel label_12 = new JLabel("MS PNT-file name");
        JLabel label_13 = new JLabel("Host or serial port");
        
        comboBoxMsPntConnectionType = new JComboBox<>();
        comboBoxMsPntConnectionType.addActionListener(this);
        comboBoxMsPntConnectionType.setModel(new DefaultComboBoxModel<>(SensorConnectionType.values()));
        
        textFieldMsPntHostOrSerialPort = new JTextField();
        textFieldMsPntHostOrSerialPort.setColumns(10);
        
        textFieldMsPntFilename = new JTextField();
        textFieldMsPntFilename.setColumns(10);
        
        spinnerMsPntTcpPort = new JSpinner();
        spinnerMsPntTcpPort.setEditor(new NumberEditor(spinnerMsPntTcpPort, "#"));
        
        GroupLayout gl_MsPntConnectionPanel = new GroupLayout(MsPntConnectionPanel);
        gl_MsPntConnectionPanel.setHorizontalGroup(
            gl_MsPntConnectionPanel.createParallelGroup(Alignment.LEADING)
                .addGap(0, 429, Short.MAX_VALUE)
                .addGroup(gl_MsPntConnectionPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_MsPntConnectionPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(label_10)
                        .addComponent(label_11)
                        .addComponent(label_12)
                        .addComponent(label_13))
                    .addGap(14)
                    .addGroup(gl_MsPntConnectionPanel.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(comboBoxMsPntConnectionType, 0, 137, Short.MAX_VALUE)
                        .addComponent(textFieldMsPntHostOrSerialPort)
                        .addComponent(textFieldMsPntFilename)
                        .addComponent(spinnerMsPntTcpPort))
                    .addContainerGap(170, Short.MAX_VALUE))
        );
        gl_MsPntConnectionPanel.setVerticalGroup(
            gl_MsPntConnectionPanel.createParallelGroup(Alignment.LEADING)
                .addGap(0, 137, Short.MAX_VALUE)
                .addGroup(gl_MsPntConnectionPanel.createSequentialGroup()
                    .addGroup(gl_MsPntConnectionPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_10)
                        .addComponent(comboBoxMsPntConnectionType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_MsPntConnectionPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_13)
                        .addComponent(textFieldMsPntHostOrSerialPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_MsPntConnectionPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_12)
                        .addComponent(textFieldMsPntFilename, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_MsPntConnectionPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_11)
                        .addComponent(spinnerMsPntTcpPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MsPntConnectionPanel.setLayout(gl_MsPntConnectionPanel);
        
        
        /************** Transponder Panel ***************/
        
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
                    .addGap(10)
                    .addGroup(gl_SimulationPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerAisSensorRange, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        transponderPanel.setLayout(gl_SimulationPanel);
        
        
        /************** Sensor Tab ***************/
        
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(transponderPanel, GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                        .addComponent(MsPntConnectionPanel, GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                        .addComponent(GpsConnectionPanel, GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                        .addComponent(AisConnectionPanel, GroupLayout.PREFERRED_SIZE, 313, Short.MAX_VALUE)
                        .addComponent(PntSourcePanel, GroupLayout.PREFERRED_SIZE, 313, Short.MAX_VALUE))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(PntSourcePanel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(AisConnectionPanel, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(GpsConnectionPanel, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(MsPntConnectionPanel, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(transponderPanel, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                    .addGap(21))
        );
        setLayout(groupLayout);
        
        // Update the UI state based on the current selection
        updateUIState();
    }
    
    /**
     * Loads the current settings
     */
    public void loadSettings(EPDSensorSettings sensorSettings) {
        this.sensorSettings = sensorSettings;
        
        // Loads the AIS Connection settings
        comboBoxAisConnectionType.setSelectedItem(sensorSettings.getAisConnectionType());
        textFieldAisHostOrSerialPort.setText(sensorSettings.getAisHostOrSerialPort());
        textFieldAisFilename.setText(sensorSettings.getAisFilename());
        spinnerAisTcpPort.setValue(sensorSettings.getAisTcpPort());
        
        // Loads the GPS Connection settings
        comboBoxGpsConnectionType.setSelectedItem(sensorSettings.getGpsConnectionType());
        textFieldGpsHostOrSerialPort.setText(sensorSettings.getGpsHostOrSerialPort());
        textFieldGpsFilename.setText(sensorSettings.getGpsFilename());
        spinnerGpsTcpPort.setValue(sensorSettings.getGpsTcpPort());
        
        // Loads the Multi-source PNT Connection settings
        comboBoxMsPntConnectionType.setSelectedItem(sensorSettings.getMsPntConnectionType());
        textFieldMsPntHostOrSerialPort.setText(sensorSettings.getMsPntHostOrSerialPort());
        textFieldMsPntFilename.setText(sensorSettings.getMsPntFilename());
        spinnerMsPntTcpPort.setValue(sensorSettings.getMsPntTcpPort());
                
        // Loads the PNT source settings
        comboBoxPntSource.setSelectedItem(sensorSettings.getPntSource());
        
        // Loads the Transponder settings
        startTransponder.setSelected(sensorSettings.isStartTransponder());
        spinnerAisSensorRange.setValue(sensorSettings.getAisSensorRange());
        
        // Update the UI state based on the current selection
        updateUIState();
    }
    
    /**
     * Saves the current settings
     */
    public void saveSettings() {
        
        // Saves the AIS Connection settings
        sensorSettings.setAisConnectionType((SensorConnectionType) comboBoxAisConnectionType.getSelectedItem());
        sensorSettings.setAisHostOrSerialPort(textFieldAisHostOrSerialPort.getText());
        sensorSettings.setAisFilename(textFieldAisFilename.getText());
        sensorSettings.setAisTcpPort((Integer) spinnerAisTcpPort.getValue());
        
        // Saves the GPS Connection settings
        sensorSettings.setGpsConnectionType((SensorConnectionType) comboBoxGpsConnectionType.getSelectedItem());
        sensorSettings.setGpsHostOrSerialPort(textFieldGpsHostOrSerialPort.getText());
        sensorSettings.setGpsFilename(textFieldGpsFilename.getText());
        sensorSettings.setGpsTcpPort((Integer) spinnerGpsTcpPort.getValue());
        
        // Saves the Multi-source PNT Connection settings
        sensorSettings.setMsPntConnectionType((SensorConnectionType) comboBoxMsPntConnectionType.getSelectedItem());
        sensorSettings.setMsPntHostOrSerialPort(textFieldMsPntHostOrSerialPort.getText());
        sensorSettings.setMsPntFilename(textFieldMsPntFilename.getText());
        sensorSettings.setMsPntTcpPort((Integer) spinnerMsPntTcpPort.getValue());
        
        // Saves the PNT source settings
        sensorSettings.setPntSource((PntSource) comboBoxPntSource.getSelectedItem());
        
        // Saves the Transponder settings
        sensorSettings.setStartTransponder(startTransponder.isSelected());        
        sensorSettings.setAisSensorRange((Double) spinnerAisSensorRange.getValue());
    }

    /**
     * Called when the selection of various combo-boxes changes
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Update the UI state based on the current selection
        updateUIState();
    }
    
    /**
     * Updates the enabled state of UI components based on the current selection
     */
    private void updateUIState() {
        textFieldAisFilename.setEnabled(comboBoxAisConnectionType.getSelectedItem() == SensorConnectionType.FILE);
        textFieldGpsFilename.setEnabled(comboBoxGpsConnectionType.getSelectedItem() == SensorConnectionType.FILE);
        textFieldMsPntFilename.setEnabled(comboBoxMsPntConnectionType.getSelectedItem() == SensorConnectionType.FILE);

        // Enabling/disabling the connection panels will not actually disabled the child components.
        // But it will change the appearance of the panel title to indicate the current PNT source selection.
        PntSource pntSrc = (PntSource)comboBoxPntSource.getSelectedItem();
        AisConnectionPanel.setEnabled(pntSrc == PntSource.AUTO || pntSrc == PntSource.AIS);
        GpsConnectionPanel.setEnabled(pntSrc == PntSource.AUTO || pntSrc == PntSource.GPS);
        MsPntConnectionPanel.setEnabled(pntSrc == PntSource.AUTO || pntSrc == PntSource.MSPNT);
    }    
}

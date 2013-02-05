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

import java.awt.Color;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.settings.SensorSettings.SensorConnectionType;
import dk.dma.epd.shore.settings.ESDAisSettings;
import dk.dma.epd.shore.settings.ESDSensorSettings;

public class AisSettingsPanel extends JPanel{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JTextField textFieldAisHostOrSerialPort;
    private ESDAisSettings aisSettings;
    @SuppressWarnings("rawtypes")
    private JComboBox comboBoxAisConnectionType;
    private JSpinner spinnerAisTcpPort;
    private JCheckBox chckbxAllowSending;
    private JCheckBox chckbxStrictTimeout;
    private JTextField ownMMSITxt;
    private ESDSensorSettings sensorSettings;


    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AisSettingsPanel(){
        super();

        setBackground(GuiStyler.backgroundColor);
        setBounds(10, 11, 493, 600);
        setLayout(null);

        JPanel aisConnection = new JPanel();
        aisConnection.setBackground(GuiStyler.backgroundColor);
        aisConnection.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "AIS Connection", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));

        aisConnection.setBounds(10, 11, 473, 117);
        add(aisConnection);
        aisConnection.setLayout(null);

        JLabel lblNewLabel = new JLabel("Connection type:");
        GuiStyler.styleText(lblNewLabel);
        lblNewLabel.setBounds(10, 22, 114, 14);
        aisConnection.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Host or serial port:");
        GuiStyler.styleText(lblNewLabel_1);
        lblNewLabel_1.setBounds(10, 46, 114, 14);
        aisConnection.add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("TCP Port:");
        GuiStyler.styleText(lblNewLabel_2);
        lblNewLabel_2.setBounds(10, 68, 46, 14);
        aisConnection.add(lblNewLabel_2);

        comboBoxAisConnectionType = new JComboBox();
        GuiStyler.styleDropDown(comboBoxAisConnectionType);
        comboBoxAisConnectionType.setModel(new DefaultComboBoxModel(ESDSensorSettings.SensorConnectionType.values()));
        comboBoxAisConnectionType.setBounds(134, 19, 142, 20);
        aisConnection.add(comboBoxAisConnectionType);

        textFieldAisHostOrSerialPort = new JTextField();
        GuiStyler.styleTextFields(textFieldAisHostOrSerialPort);
        textFieldAisHostOrSerialPort.setBounds(134, 43, 142, 20);
        aisConnection.add(textFieldAisHostOrSerialPort);
        textFieldAisHostOrSerialPort.setColumns(10);

        spinnerAisTcpPort = new JSpinner();
        spinnerAisTcpPort.setEditor(new NumberEditor(spinnerAisTcpPort, "#"));
        GuiStyler.styleSpinner(spinnerAisTcpPort);
        spinnerAisTcpPort.setBounds(134, 65, 142, 20);
        aisConnection.add(spinnerAisTcpPort);

        JPanel transponderSettings = new JPanel();

        transponderSettings.setBackground(GuiStyler.backgroundColor);
        transponderSettings.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "Transponder Settings", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));
        transponderSettings.setBounds(10, 150, 472, 100);
        add(transponderSettings);
        transponderSettings.setLayout(null);

        chckbxAllowSending = new JCheckBox("Allow Sending");
        GuiStyler.styleCheckbox(chckbxAllowSending);
        chckbxAllowSending.setBounds(6, 27, 125, 23);
        transponderSettings.add(chckbxAllowSending);


        chckbxStrictTimeout = new JCheckBox("Strict timeout");
        GuiStyler.styleCheckbox(chckbxStrictTimeout);
        chckbxStrictTimeout.setBounds(6, 53, 97, 23);
        transponderSettings.add(chckbxStrictTimeout);



        JPanel ownMMSI = new JPanel();

        ownMMSI.setBackground(GuiStyler.backgroundColor);
        ownMMSI.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "MMSI Settings", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));

        ownMMSI.setLayout(null);
        ownMMSI.setBounds(11, 272, 472, 100);
        add(ownMMSI);

        JLabel lblNewLabel_3 = new JLabel("Own MMSI:");
        GuiStyler.styleText(lblNewLabel_3);
        lblNewLabel_3.setBounds(10, 22, 114, 14);
        ownMMSI.add(lblNewLabel_3);

        ownMMSITxt = new JTextField();
        ownMMSITxt.setBounds(134, 19, 142, 20);
        GuiStyler.styleTextFields(ownMMSITxt);
        ownMMSI.add(ownMMSITxt);
        ownMMSITxt.setColumns(10);

    }


    public void loadSettings(ESDAisSettings aisSettings, ESDSensorSettings sensorSettings) {
        this.aisSettings = aisSettings;
        this.sensorSettings = sensorSettings;
        comboBoxAisConnectionType.getModel().setSelectedItem(sensorSettings.getAisConnectionType());
        textFieldAisHostOrSerialPort.setText(sensorSettings.getAisHostOrSerialPort());
        spinnerAisTcpPort.setValue(sensorSettings.getAisTcpPort());

        chckbxAllowSending.setSelected(aisSettings.isAllowSending());
        chckbxStrictTimeout.setSelected(aisSettings.isStrict());

        ownMMSITxt.setText(Long.toString(aisSettings.getOwnMMSI()));

    }

    public void saveSettings() {
        sensorSettings.setAisConnectionType((SensorConnectionType) comboBoxAisConnectionType.getModel().getSelectedItem());
        sensorSettings.setAisHostOrSerialPort(textFieldAisHostOrSerialPort.getText());
        sensorSettings.setAisTcpPort((Integer) spinnerAisTcpPort.getValue());

        aisSettings.setAllowSending(chckbxAllowSending.isSelected());
        aisSettings.setStrict(chckbxStrictTimeout.isSelected());

        aisSettings.setOwnMMSI(Long.parseLong(ownMMSITxt.getText()));

    }

}

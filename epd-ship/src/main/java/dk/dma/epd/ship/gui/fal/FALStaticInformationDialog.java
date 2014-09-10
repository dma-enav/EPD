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
package dk.dma.epd.ship.gui.fal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import dk.dma.ais.message.ShipTypeCargo;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.fal.FALManager;
import dk.dma.epd.ship.fal.StaticFalShipData;

public class FALStaticInformationDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JTextField nameAndTypeField;
    private JTextField imoField;
    private JTextField callsignField;
    private JTextField flagStateField;
    private JTextField nameOfMasterField;
    private JTextField certificateOfRegistryField;
    private JTextField grossTonageField;
    private JTextField netTonnageField;
    private JTextArea nameAndContactAgentField;

    private JButton importBtn;
    private JButton resetButton;
    private JButton saveButton;
    private JButton btnClose;

    private Insets noPadding = new Insets(0, 0, 0, 0);

    private FALManager falManager;

    /**
     * Create the dialog.
     */
    public FALStaticInformationDialog(FALManager falManager) {
        this.falManager = falManager;
        setTitle("Ship Information");

        initGUI();

        initFromSavedData(falManager.getStaticShipData());
    }

    private void initFromSavedData(StaticFalShipData data) {
        StaticFalShipData staticData = data;

        nameAndTypeField.setText(staticData.getNameAndTypeOfShip());
        imoField.setText(staticData.getImoNumber());
        callsignField.setText(staticData.getCallSign());
        flagStateField.setText(staticData.getFlagStateOfShip());
        nameOfMasterField.setText(staticData.getNameOfMaster());
        certificateOfRegistryField.setText(staticData.getCertificateOfRegistry());
        grossTonageField.setText(staticData.getGrossTonnage());
        netTonnageField.setText(staticData.getNetTonnage());
        nameAndContactAgentField.setText(staticData.getNameAndContactDetalsOfShipsAgent());

    }

    private void initGUI() {
        setBounds(100, 100, 633, 323);
        setMinimumSize(new Dimension(633, 323));
        setVisible(true);

        setResizable(true);

        getContentPane().setLayout(new BorderLayout());

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                importBtn = new JButton("Import from AIS");
                buttonPane.add(importBtn);
            }
            {
                resetButton = new JButton("Reset All");
                buttonPane.add(resetButton);
            }
            {
                saveButton = new JButton("Save and Close");
                buttonPane.add(saveButton);
                getRootPane().setDefaultButton(saveButton);
            }
            {
                btnClose = new JButton("Close");
                btnClose.setEnabled(false);
                buttonPane.add(btnClose);
            }

            importBtn.addActionListener(this);
            resetButton.addActionListener(this);
            saveButton.addActionListener(this);
            btnClose.addActionListener(this);
        }

        JPanel FalForm1Panel = new JPanel();

        getContentPane().add(FalForm1Panel, BorderLayout.CENTER);
        FalForm1Panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gbl_FalForm1Panel = new GridBagLayout();

        gbl_FalForm1Panel.columnWidths = new int[] { 0, 0, 0 };
        gbl_FalForm1Panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gbl_FalForm1Panel.columnWeights = new double[] { 1.0, 2.0, Double.MIN_VALUE };
        gbl_FalForm1Panel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
        FalForm1Panel.setLayout(gbl_FalForm1Panel);

        // Name and type of ship
        {
            JPanel nameAndTypeOfShipPanel = new JPanel();
            nameAndTypeOfShipPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Name and type of ship",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GridBagConstraints gbc_nameAndTypeOfShipPanel = new GridBagConstraints();
            gbc_nameAndTypeOfShipPanel.insets = new Insets(0, 0, 5, 5);
            gbc_nameAndTypeOfShipPanel.fill = GridBagConstraints.BOTH;
            gbc_nameAndTypeOfShipPanel.gridx = 0;
            gbc_nameAndTypeOfShipPanel.gridy = 0;
            FalForm1Panel.add(nameAndTypeOfShipPanel, gbc_nameAndTypeOfShipPanel);
            nameAndTypeOfShipPanel.setLayout(new BorderLayout(0, 0));
            {
                nameAndTypeField = new JTextField();
                nameAndTypeField.setText("FRV-DaMSA");
                nameAndTypeOfShipPanel.add(nameAndTypeField);
                nameAndTypeField.setColumns(10);
            }
        }
        {
            JPanel imoNumberPanel = new JPanel();
            imoNumberPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "IMO number",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GridBagConstraints gbc_imoNumberPanel = new GridBagConstraints();
            gbc_imoNumberPanel.insets = new Insets(0, 0, 5, 0);
            gbc_imoNumberPanel.fill = GridBagConstraints.BOTH;
            gbc_imoNumberPanel.gridx = 1;
            gbc_imoNumberPanel.gridy = 0;
            // gbc_imoNumberPanel.anchor = GridBagConstraints.CENTER;
            FalForm1Panel.add(imoNumberPanel, gbc_imoNumberPanel);
            imoNumberPanel.setLayout(new BorderLayout(0, 0));
            {
                imoField = new JTextField();
                imoField.setText("01010101");
                imoNumberPanel.add(imoField, BorderLayout.CENTER);
                imoField.setColumns(10);
            }
        }
        {
            JPanel callsignPanel = new JPanel();
            callsignPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Call sign", TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));
            GridBagConstraints gbc_callsignPanel = new GridBagConstraints();
            gbc_callsignPanel.gridwidth = 2;
            gbc_callsignPanel.insets = new Insets(0, 0, 5, 0);
            gbc_callsignPanel.fill = GridBagConstraints.BOTH;
            gbc_callsignPanel.gridx = 0;
            gbc_callsignPanel.gridy = 1;
            FalForm1Panel.add(callsignPanel, gbc_callsignPanel);
            callsignPanel.setLayout(new BorderLayout(0, 0));
            {
                callsignField = new JTextField();
                callsignPanel.add(callsignField, BorderLayout.CENTER);
                callsignField.setColumns(10);
            }
        }
        {
            JPanel flagSateOfShipPanel = new JPanel();
            GridBagConstraints gbc_flagSateOfShipPanel = new GridBagConstraints();
            gbc_flagSateOfShipPanel.fill = GridBagConstraints.BOTH;
            gbc_flagSateOfShipPanel.insets = new Insets(0, 0, 5, 5);
            gbc_flagSateOfShipPanel.gridx = 0;
            gbc_flagSateOfShipPanel.gridy = 2;
            FalForm1Panel.add(flagSateOfShipPanel, gbc_flagSateOfShipPanel);
            flagSateOfShipPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Flag State of ship",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            flagSateOfShipPanel.setLayout(new BorderLayout(0, 0));
            {
                flagStateField = new JTextField();
                flagSateOfShipPanel.add(flagStateField, BorderLayout.CENTER);
                flagStateField.setColumns(10);
            }
        }
        {
            JPanel nameOfMasterPanel = new JPanel();
            GridBagConstraints gbc_nameOfMasterPanel = new GridBagConstraints();
            gbc_nameOfMasterPanel.fill = GridBagConstraints.BOTH;
            gbc_nameOfMasterPanel.insets = new Insets(0, 0, 5, 0);
            gbc_nameOfMasterPanel.gridx = 1;
            gbc_nameOfMasterPanel.gridy = 2;
            FalForm1Panel.add(nameOfMasterPanel, gbc_nameOfMasterPanel);
            nameOfMasterPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Name of master",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            nameOfMasterPanel.setLayout(new BorderLayout(0, 0));
            {
                nameOfMasterField = new JTextField();
                nameOfMasterPanel.add(nameOfMasterField, BorderLayout.CENTER);
                nameOfMasterField.setColumns(10);
            }
        }
        {
            JPanel certificateTonnagePositionParentPanel = new JPanel();
            GridBagConstraints gbc_certificateTonnagePositionParentPanel = new GridBagConstraints();
            gbc_certificateTonnagePositionParentPanel.insets = new Insets(0, 0, 0, 5);
            gbc_certificateTonnagePositionParentPanel.fill = GridBagConstraints.BOTH;
            gbc_certificateTonnagePositionParentPanel.gridx = 0;
            gbc_certificateTonnagePositionParentPanel.gridy = 3;
            FalForm1Panel.add(certificateTonnagePositionParentPanel, gbc_certificateTonnagePositionParentPanel);
            GridBagLayout gbl_certificateTonnagePositionParentPanel = new GridBagLayout();
            gbl_certificateTonnagePositionParentPanel.columnWidths = new int[] { 0, 0 };
            gbl_certificateTonnagePositionParentPanel.rowHeights = new int[] { 0, 0, 0 };
            gbl_certificateTonnagePositionParentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
            gbl_certificateTonnagePositionParentPanel.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
            certificateTonnagePositionParentPanel.setLayout(gbl_certificateTonnagePositionParentPanel);
            {
                JPanel certificateOfRegistryPanel = new JPanel();
                certificateOfRegistryPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                        "Certificate of registry (Port; date; number)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                GridBagConstraints gbc_certificateOfRegistryPanel = new GridBagConstraints();
                gbc_certificateOfRegistryPanel.insets = new Insets(0, 0, 5, 0);
                gbc_certificateOfRegistryPanel.fill = GridBagConstraints.BOTH;
                gbc_certificateOfRegistryPanel.gridx = 0;
                gbc_certificateOfRegistryPanel.gridy = 0;
                certificateTonnagePositionParentPanel.add(certificateOfRegistryPanel, gbc_certificateOfRegistryPanel);
                certificateOfRegistryPanel.setLayout(new BorderLayout(0, 0));
                {
                    certificateOfRegistryField = new JTextField();
                    certificateOfRegistryPanel.add(certificateOfRegistryField, BorderLayout.CENTER);
                    certificateOfRegistryField.setColumns(10);
                }
            }
            {
                JPanel tonnageParentPanel = new JPanel();
                GridBagConstraints gbc_tonnageParentPanel = new GridBagConstraints();
                gbc_tonnageParentPanel.insets = noPadding;
                gbc_tonnageParentPanel.fill = GridBagConstraints.BOTH;
                gbc_tonnageParentPanel.gridx = 0;
                gbc_tonnageParentPanel.gridy = 1;
                certificateTonnagePositionParentPanel.add(tonnageParentPanel, gbc_tonnageParentPanel);
                GridBagLayout gbl_tonnageParentPanel = new GridBagLayout();
                gbl_tonnageParentPanel.columnWidths = new int[] { 0, 0, 0 };
                gbl_tonnageParentPanel.rowHeights = new int[] { 0, 0 };
                gbl_tonnageParentPanel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
                gbl_tonnageParentPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
                tonnageParentPanel.setLayout(gbl_tonnageParentPanel);
                {
                    JPanel grossTonnagePanel = new JPanel();
                    grossTonnagePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Gross tonnage",
                            TitledBorder.LEADING, TitledBorder.TOP, null, null));
                    GridBagConstraints gbc_grossTonnagePanel = new GridBagConstraints();
                    gbc_grossTonnagePanel.insets = noPadding;
                    gbc_grossTonnagePanel.fill = GridBagConstraints.BOTH;
                    gbc_grossTonnagePanel.gridx = 0;
                    gbc_grossTonnagePanel.gridy = 0;
                    tonnageParentPanel.add(grossTonnagePanel, gbc_grossTonnagePanel);
                    grossTonnagePanel.setLayout(new BorderLayout(0, 0));
                    {
                        grossTonageField = new JTextField();
                        grossTonnagePanel.add(grossTonageField, BorderLayout.CENTER);
                        grossTonageField.setColumns(10);
                    }
                }
                {
                    JPanel netTonnagePanel = new JPanel();
                    netTonnagePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Net tonnage",
                            TitledBorder.LEADING, TitledBorder.TOP, null, null));
                    GridBagConstraints gbc_netTonnagePanel = new GridBagConstraints();
                    gbc_netTonnagePanel.fill = GridBagConstraints.BOTH;
                    gbc_netTonnagePanel.gridx = 1;
                    gbc_netTonnagePanel.gridy = 0;
                    tonnageParentPanel.add(netTonnagePanel, gbc_netTonnagePanel);
                    netTonnagePanel.setLayout(new BorderLayout(0, 0));
                    {
                        netTonnageField = new JTextField();
                        netTonnagePanel.add(netTonnageField, BorderLayout.CENTER);
                        netTonnageField.setColumns(10);
                    }
                }
            }
        }
        {
            JPanel nameandContactDetailsShipAgentPanel = new JPanel();
            nameandContactDetailsShipAgentPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                    "Name and contact details of ship\u2019s agent", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GridBagConstraints gbc_nameandContactDetailsShipAgentPanel = new GridBagConstraints();
            gbc_nameandContactDetailsShipAgentPanel.fill = GridBagConstraints.BOTH;
            gbc_nameandContactDetailsShipAgentPanel.gridx = 1;
            gbc_nameandContactDetailsShipAgentPanel.gridy = 3;
            FalForm1Panel.add(nameandContactDetailsShipAgentPanel, gbc_nameandContactDetailsShipAgentPanel);
            nameandContactDetailsShipAgentPanel.setLayout(new BorderLayout(0, 0));
            {
                nameAndContactAgentField = new JTextArea();
                nameAndContactAgentField.setRows(5);
                JScrollPane sp = new JScrollPane(nameAndContactAgentField);

                nameandContactDetailsShipAgentPanel.add(sp, BorderLayout.CENTER);
            }
        }

        this.pack();
    };

    private void importFromAIS() {

        VesselStaticData shipData = EPDShip.getInstance().getOwnShipHandler().getStaticData();

        if (shipData != null) {

            String shipName = shipData.getTrimmedName();

            ShipTypeCargo shipType = shipData.getShipType();

            String callsign = shipData.getCallsign();

            nameAndTypeField.setText(shipName + " " + shipType.prettyCargo());
            callsignField.setText(callsign);

            String imoNumber = shipData.getImo() + "";

            imoField.setText(imoNumber);
        }

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == importBtn) {

            importFromAIS();

        } else if (arg0.getSource() == resetButton) {
            resetAllFields();
        } else if (arg0.getSource() == saveButton) {
            saveAllFields();
        } else if (arg0.getSource() == btnClose) {

            this.dispose();
        }

    }

    private void resetAllFields() {
        nameAndTypeField.setText("");
        imoField.setText("");
        callsignField.setText("");
        flagStateField.setText("");
        nameOfMasterField.setText("");
        certificateOfRegistryField.setText("");
        grossTonageField.setText("");
        netTonnageField.setText("");
        nameAndContactAgentField.setText("");
    }

    private void saveAllFields() {

        StaticFalShipData staticData = falManager.getStaticShipData();

        staticData.setNameAndTypeOfShip(nameAndTypeField.getText());
        staticData.setImoNumber(imoField.getText());
        staticData.setCallSign(callsignField.getText());
        staticData.setFlagStateOfShip(flagStateField.getText());
        staticData.setNameOfMaster(nameOfMasterField.getText());
        staticData.setCertificateOfRegistry(certificateOfRegistryField.getText());
        staticData.setGrossTonnage(grossTonageField.getText());
        staticData.setNetTonnage(netTonnageField.getText());
        staticData.setNameAndContactDetalsOfShipsAgent(nameAndContactAgentField.getText());

        falManager.setStaticShipData(staticData);

        falManager.saveStaticData();

        this.dispose();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        try {
            setOpacity((float) 0.95);
        } catch (Exception E) {
            System.out.println("Failed to set opacity, ignore");
        }

    }
}

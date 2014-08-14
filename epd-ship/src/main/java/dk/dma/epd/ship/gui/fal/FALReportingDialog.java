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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class FALReportingDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JPanel FalForm1Panel = new JPanel();
    private JTextField txtFrvdamsa;
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;
    private JTextField textField_3;
    private JTextField textField_4;
    private JTextField textField_5;
    private JTextField textField_6;
    private JTextField textField_7;
    private JTextField textField_9;
    private JTextField textField_10;
    private JTextField textField_11;
    private JTextField textField_12;
    private JTextField textField_8;
    private JTextField textField_13;
    private JTextField textField_14;

    private Insets noPadding = new Insets(0, 0, 0, 0);


    /**
     * Create the dialog.
     */
    public FALReportingDialog() {
        setBounds(100, 100, 1366, 768);
        setVisible(true);
//        this.setResizable(false);
        getContentPane().setLayout(new BorderLayout());

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton saveButton = new JButton("Save");
                buttonPane.add(saveButton);
                getRootPane().setDefaultButton(saveButton);
            }
            {
                JButton importBtn = new JButton("Import");
                buttonPane.add(importBtn);
            }
            {
                JButton resetButton = new JButton("Reset All");
                buttonPane.add(resetButton);
            }
            {
                JButton printBtn = new JButton("Export/Print");
                buttonPane.add(printBtn);
            }
        }
        {
            JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
            getContentPane().add(tabbedPane, BorderLayout.NORTH);
            tabbedPane.addTab("IMO GENERAL DECLARATION", null, FalForm1Panel, null);
            FalForm1Panel.setBorder(new EmptyBorder(5, 5, 5, 5));
            GridBagLayout gbl_FalForm1Panel = new GridBagLayout();

            gbl_FalForm1Panel.columnWidths = new int[] { 0, 0, 0 };
            gbl_FalForm1Panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            gbl_FalForm1Panel.columnWeights = new double[] { 1.0, 2.0, Double.MIN_VALUE };
            gbl_FalForm1Panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
            FalForm1Panel.setLayout(gbl_FalForm1Panel);

            {
                JPanel depatureArrivalParentPanel = new JPanel();
                GridBagConstraints gbc_depatureArrivalParentPanel = new GridBagConstraints();
                gbc_depatureArrivalParentPanel.insets = noPadding;
                gbc_depatureArrivalParentPanel.fill = GridBagConstraints.BOTH;
                gbc_depatureArrivalParentPanel.gridx = 1;
                gbc_depatureArrivalParentPanel.gridy = 0;
                gbc_depatureArrivalParentPanel.anchor = GridBagConstraints.CENTER;
                FalForm1Panel.add(depatureArrivalParentPanel, gbc_depatureArrivalParentPanel);
                GridBagLayout gbl_depatureArrivalParentPanel = new GridBagLayout();
                gbl_depatureArrivalParentPanel.columnWidths = new int[] { 0, 0, 0 };
                gbl_depatureArrivalParentPanel.rowHeights = new int[] { 0, 0 };
                gbl_depatureArrivalParentPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
                gbl_depatureArrivalParentPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
                depatureArrivalParentPanel.setLayout(gbl_depatureArrivalParentPanel);

                {
                    JCheckBox chckbxArrival = new JCheckBox("Arrival");
                    GridBagConstraints gbc_chckbxArrival = new GridBagConstraints();
                    gbc_chckbxArrival.insets = noPadding;
                    gbc_chckbxArrival.anchor = GridBagConstraints.WEST;
                    gbc_chckbxArrival.gridx = 0;
                    gbc_chckbxArrival.gridy = 0;
                    depatureArrivalParentPanel.add(chckbxArrival, gbc_chckbxArrival);
                }
                {
                    JCheckBox chckbxDepature = new JCheckBox("Depature");
                    GridBagConstraints gbc_chckbxDepature = new GridBagConstraints();
                    gbc_chckbxDepature.anchor = GridBagConstraints.WEST;
                    gbc_chckbxDepature.gridx = 1;
                    gbc_chckbxDepature.gridy = 0;
                    depatureArrivalParentPanel.add(chckbxDepature, gbc_chckbxDepature);
                }
            }

            // Name and type of ship
            {
                JPanel nameAndTypeOfShipPanel = new JPanel();
                nameAndTypeOfShipPanel.setBorder(new TitledBorder(null, "1.1 Name and type of ship", TitledBorder.LEADING,
                        TitledBorder.TOP, null, null));
                GridBagConstraints gbc_nameAndTypeOfShipPanel = new GridBagConstraints();
                gbc_nameAndTypeOfShipPanel.insets = noPadding;
                gbc_nameAndTypeOfShipPanel.fill = GridBagConstraints.BOTH;
                gbc_nameAndTypeOfShipPanel.gridx = 0;
                gbc_nameAndTypeOfShipPanel.gridy = 1;
                FalForm1Panel.add(nameAndTypeOfShipPanel, gbc_nameAndTypeOfShipPanel);
                nameAndTypeOfShipPanel.setLayout(new BorderLayout(0, 0));
                {
                    txtFrvdamsa = new JTextField();
                    txtFrvdamsa.setText("FRV-DaMSA");
                    nameAndTypeOfShipPanel.add(txtFrvdamsa);
                    txtFrvdamsa.setColumns(10);
                }
            }
            {
                JPanel imoNumberPanel = new JPanel();
                imoNumberPanel.setBorder(new TitledBorder(null, "1.2 IMO number", TitledBorder.LEADING, TitledBorder.TOP, null,
                        null));
                GridBagConstraints gbc_imoNumberPanel = new GridBagConstraints();
                gbc_imoNumberPanel.insets = noPadding;
                gbc_imoNumberPanel.fill = GridBagConstraints.BOTH;
                gbc_imoNumberPanel.gridx = 1;
                gbc_imoNumberPanel.gridy = 1;
                // gbc_imoNumberPanel.anchor = GridBagConstraints.CENTER;
                FalForm1Panel.add(imoNumberPanel, gbc_imoNumberPanel);
                imoNumberPanel.setLayout(new BorderLayout(0, 0));
                {
                    textField = new JTextField();
                    textField.setText("01010101");
                    imoNumberPanel.add(textField, BorderLayout.CENTER);
                    textField.setColumns(10);
                }
            }
            {
                JPanel callsignPanel = new JPanel();
                callsignPanel
                        .setBorder(new TitledBorder(null, "1.3 Call sign", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                GridBagConstraints gbc_callsignPanel = new GridBagConstraints();
                gbc_callsignPanel.insets = noPadding;
                gbc_callsignPanel.fill = GridBagConstraints.BOTH;
                gbc_callsignPanel.gridx = 0;
                gbc_callsignPanel.gridy = 2;
                FalForm1Panel.add(callsignPanel, gbc_callsignPanel);
                callsignPanel.setLayout(new BorderLayout(0, 0));
                {
                    textField_1 = new JTextField();
                    callsignPanel.add(textField_1, BorderLayout.CENTER);
                    textField_1.setColumns(10);
                }
            }
            {
                JPanel voyageNumberPanel = new JPanel();
                voyageNumberPanel.setBorder(new TitledBorder(null, "1.4 Voyage number", TitledBorder.LEADING, TitledBorder.TOP,
                        null, null));
                GridBagConstraints gbc_voyageNumberPanel = new GridBagConstraints();
                gbc_voyageNumberPanel.insets = noPadding;
                gbc_voyageNumberPanel.fill = GridBagConstraints.BOTH;
                gbc_voyageNumberPanel.gridx = 1;
                gbc_voyageNumberPanel.gridy = 2;
                FalForm1Panel.add(voyageNumberPanel, gbc_voyageNumberPanel);
                voyageNumberPanel.setLayout(new BorderLayout(0, 0));
                {
                    textField_2 = new JTextField();
                    voyageNumberPanel.add(textField_2, BorderLayout.CENTER);
                    textField_2.setColumns(10);
                }
            }
            {
                JPanel portofArrivalDeparturePanel = new JPanel();
                portofArrivalDeparturePanel.setBorder(new TitledBorder(null, "2. Port of arrival/depature", TitledBorder.LEADING,
                        TitledBorder.TOP, null, null));
                GridBagConstraints gbc_portofArrivalDeparturePanel = new GridBagConstraints();
                gbc_portofArrivalDeparturePanel.insets = noPadding;
                gbc_portofArrivalDeparturePanel.fill = GridBagConstraints.BOTH;
                gbc_portofArrivalDeparturePanel.gridx = 0;
                gbc_portofArrivalDeparturePanel.gridy = 3;
                FalForm1Panel.add(portofArrivalDeparturePanel, gbc_portofArrivalDeparturePanel);
                portofArrivalDeparturePanel.setLayout(new BorderLayout(0, 0));
                {
                    textField_3 = new JTextField();
                    portofArrivalDeparturePanel.add(textField_3, BorderLayout.CENTER);
                    textField_3.setColumns(10);
                }
            }
            {
                JPanel dateandtimeofarrivalDepaturePanel = new JPanel();
                dateandtimeofarrivalDepaturePanel.setBorder(new TitledBorder(null, "3.  Date and time of arrival/departure",
                        TitledBorder.LEADING, TitledBorder.TOP, null, null));
                GridBagConstraints gbc_dateandtimeofarrivalDepaturePanel = new GridBagConstraints();
                gbc_dateandtimeofarrivalDepaturePanel.insets = noPadding;
                gbc_dateandtimeofarrivalDepaturePanel.fill = GridBagConstraints.BOTH;
                gbc_dateandtimeofarrivalDepaturePanel.gridx = 1;
                gbc_dateandtimeofarrivalDepaturePanel.gridy = 3;
                FalForm1Panel.add(dateandtimeofarrivalDepaturePanel, gbc_dateandtimeofarrivalDepaturePanel);
                dateandtimeofarrivalDepaturePanel.setLayout(new BorderLayout(0, 0));
                {
                    textField_4 = new JTextField();
                    dateandtimeofarrivalDepaturePanel.add(textField_4, BorderLayout.CENTER);
                    textField_4.setColumns(10);
                }
            }
            {
                JPanel panel45Parent = new JPanel();
                GridBagConstraints gbc_panel45Parent = new GridBagConstraints();
                gbc_panel45Parent.insets = noPadding;
                gbc_panel45Parent.fill = GridBagConstraints.BOTH;
                gbc_panel45Parent.gridx = 0;
                gbc_panel45Parent.gridy = 4;
                FalForm1Panel.add(panel45Parent, gbc_panel45Parent);
                GridBagLayout gbl_panel45Parent = new GridBagLayout();
                gbl_panel45Parent.columnWidths = new int[] { 0, 0, 0 };
                gbl_panel45Parent.rowHeights = new int[] { 0, 0 };
                gbl_panel45Parent.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
                gbl_panel45Parent.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
                panel45Parent.setLayout(gbl_panel45Parent);
                {
                    JPanel flagSateOfShipPanel = new JPanel();
                    flagSateOfShipPanel.setBorder(new TitledBorder(null, "4.  Flag State of ship", TitledBorder.LEADING,
                            TitledBorder.TOP, null, null));
                    GridBagConstraints gbc_flagSateOfShipPanel = new GridBagConstraints();
                    gbc_flagSateOfShipPanel.insets = noPadding;
                    gbc_flagSateOfShipPanel.fill = GridBagConstraints.BOTH;
                    gbc_flagSateOfShipPanel.gridx = 0;
                    gbc_flagSateOfShipPanel.gridy = 0;
                    panel45Parent.add(flagSateOfShipPanel, gbc_flagSateOfShipPanel);
                    flagSateOfShipPanel.setLayout(new BorderLayout(0, 0));
                    {
                        textField_5 = new JTextField();
                        flagSateOfShipPanel.add(textField_5, BorderLayout.CENTER);
                        textField_5.setColumns(10);
                    }
                }
                {
                    JPanel nameOfMasterPanel = new JPanel();
                    nameOfMasterPanel.setBorder(new TitledBorder(null, "5. Name of master", TitledBorder.LEADING, TitledBorder.TOP,
                            null, null));
                    GridBagConstraints gbc_nameOfMasterPanel = new GridBagConstraints();
                    gbc_nameOfMasterPanel.fill = GridBagConstraints.BOTH;
                    gbc_nameOfMasterPanel.gridx = 1;
                    gbc_nameOfMasterPanel.gridy = 0;
                    panel45Parent.add(nameOfMasterPanel, gbc_nameOfMasterPanel);
                    nameOfMasterPanel.setLayout(new BorderLayout(0, 0));
                    {
                        textField_6 = new JTextField();
                        nameOfMasterPanel.add(textField_6, BorderLayout.CENTER);
                        textField_6.setColumns(10);
                    }
                }
            }
            {
                JPanel lastPortOfCallPanel = new JPanel();
                lastPortOfCallPanel.setBorder(new TitledBorder(null, "6.  Last port of call/Next port of call",
                        TitledBorder.LEADING, TitledBorder.TOP, null, null));
                GridBagConstraints gbc_lastPortOfCallPanel = new GridBagConstraints();
                gbc_lastPortOfCallPanel.insets = noPadding;
                gbc_lastPortOfCallPanel.fill = GridBagConstraints.BOTH;
                gbc_lastPortOfCallPanel.gridx = 1;
                gbc_lastPortOfCallPanel.gridy = 4;
                FalForm1Panel.add(lastPortOfCallPanel, gbc_lastPortOfCallPanel);
                lastPortOfCallPanel.setLayout(new BorderLayout(0, 0));
                {
                    textField_7 = new JTextField();
                    lastPortOfCallPanel.add(textField_7, BorderLayout.CENTER);
                    textField_7.setColumns(10);
                }
            }
            {
                JPanel certificateTonnagePositionParentPanel = new JPanel();
                GridBagConstraints gbc_certificateTonnagePositionParentPanel = new GridBagConstraints();
                gbc_certificateTonnagePositionParentPanel.insets = noPadding;
                gbc_certificateTonnagePositionParentPanel.fill = GridBagConstraints.BOTH;
                gbc_certificateTonnagePositionParentPanel.gridx = 0;
                gbc_certificateTonnagePositionParentPanel.gridy = 5;
                FalForm1Panel.add(certificateTonnagePositionParentPanel, gbc_certificateTonnagePositionParentPanel);
                GridBagLayout gbl_certificateTonnagePositionParentPanel = new GridBagLayout();
                gbl_certificateTonnagePositionParentPanel.columnWidths = new int[] { 0, 0 };
                gbl_certificateTonnagePositionParentPanel.rowHeights = new int[] { 0, 0, 0, 0 };
                gbl_certificateTonnagePositionParentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
                gbl_certificateTonnagePositionParentPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
                certificateTonnagePositionParentPanel.setLayout(gbl_certificateTonnagePositionParentPanel);
                {
                    JPanel certificateOfRegistryPanel = new JPanel();
                    certificateOfRegistryPanel.setBorder(new TitledBorder(null, "7.  Certificate of registry (Port; date; number)",
                            TitledBorder.LEADING, TitledBorder.TOP, null, null));
                    GridBagConstraints gbc_certificateOfRegistryPanel = new GridBagConstraints();
                    gbc_certificateOfRegistryPanel.insets = noPadding;
                    gbc_certificateOfRegistryPanel.fill = GridBagConstraints.BOTH;
                    gbc_certificateOfRegistryPanel.gridx = 0;
                    gbc_certificateOfRegistryPanel.gridy = 0;
                    certificateTonnagePositionParentPanel.add(certificateOfRegistryPanel, gbc_certificateOfRegistryPanel);
                    certificateOfRegistryPanel.setLayout(new BorderLayout(0, 0));
                    {
                        textField_9 = new JTextField();
                        certificateOfRegistryPanel.add(textField_9, BorderLayout.CENTER);
                        textField_9.setColumns(10);
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
                        grossTonnagePanel.setBorder(new TitledBorder(null, "9.  Gross tonnage", TitledBorder.LEADING,
                                TitledBorder.TOP, null, null));
                        GridBagConstraints gbc_grossTonnagePanel = new GridBagConstraints();
                        gbc_grossTonnagePanel.insets = noPadding;
                        gbc_grossTonnagePanel.fill = GridBagConstraints.BOTH;
                        gbc_grossTonnagePanel.gridx = 0;
                        gbc_grossTonnagePanel.gridy = 0;
                        tonnageParentPanel.add(grossTonnagePanel, gbc_grossTonnagePanel);
                        grossTonnagePanel.setLayout(new BorderLayout(0, 0));
                        {
                            textField_10 = new JTextField();
                            grossTonnagePanel.add(textField_10, BorderLayout.CENTER);
                            textField_10.setColumns(10);
                        }
                    }
                    {
                        JPanel netTonnagePanel = new JPanel();
                        netTonnagePanel.setBorder(new TitledBorder(null, "10.  Net tonnage", TitledBorder.LEADING,
                                TitledBorder.TOP, null, null));
                        GridBagConstraints gbc_netTonnagePanel = new GridBagConstraints();
                        gbc_netTonnagePanel.fill = GridBagConstraints.BOTH;
                        gbc_netTonnagePanel.gridx = 1;
                        gbc_netTonnagePanel.gridy = 0;
                        tonnageParentPanel.add(netTonnagePanel, gbc_netTonnagePanel);
                        netTonnagePanel.setLayout(new BorderLayout(0, 0));
                        {
                            textField_11 = new JTextField();
                            netTonnagePanel.add(textField_11, BorderLayout.CENTER);
                            textField_11.setColumns(10);
                        }
                    }
                }
                {
                    JPanel positionOfTheShip = new JPanel();
                    positionOfTheShip.setBorder(new TitledBorder(null, "11.  Position of the ship in the port (berth or station)",
                            TitledBorder.LEADING, TitledBorder.TOP, null, null));
                    GridBagConstraints gbc_positionOfTheShip = new GridBagConstraints();
                    gbc_positionOfTheShip.fill = GridBagConstraints.BOTH;
                    gbc_positionOfTheShip.gridx = 0;
                    gbc_positionOfTheShip.gridy = 2;
                    certificateTonnagePositionParentPanel.add(positionOfTheShip, gbc_positionOfTheShip);
                    positionOfTheShip.setLayout(new BorderLayout(0, 0));
                    {
                        textField_12 = new JTextField();
                        positionOfTheShip.add(textField_12, BorderLayout.CENTER);
                        textField_12.setColumns(10);
                    }
                }
            }
            {
                JPanel nameandContactDetailsShipAgentPanel = new JPanel();
                nameandContactDetailsShipAgentPanel.setBorder(new TitledBorder(null,
                        "8.  Name and contact details of ship\u2019s agent", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                GridBagConstraints gbc_nameandContactDetailsShipAgentPanel = new GridBagConstraints();
                gbc_nameandContactDetailsShipAgentPanel.insets = noPadding;
                gbc_nameandContactDetailsShipAgentPanel.fill = GridBagConstraints.BOTH;
                gbc_nameandContactDetailsShipAgentPanel.gridx = 1;
                gbc_nameandContactDetailsShipAgentPanel.gridy = 5;
                FalForm1Panel.add(nameandContactDetailsShipAgentPanel, gbc_nameandContactDetailsShipAgentPanel);
                nameandContactDetailsShipAgentPanel.setLayout(new BorderLayout(0, 0));
                {
                    JTextArea textArea = new JTextArea();
                    JScrollPane sp = new JScrollPane(textArea);

                    nameandContactDetailsShipAgentPanel.add(sp, BorderLayout.CENTER);
                }
            }
            {
                JPanel voyageParticularsPanel = new JPanel();
                voyageParticularsPanel
                        .setBorder(new TitledBorder(
                                null,
                                "12.  Brief particulars of voyage (previous and subsequent ports of call; underline where remaining cargo will be discharged)",
                                TitledBorder.LEADING, TitledBorder.TOP, null, null));
                GridBagConstraints gbc_voyageParticularsPanel = new GridBagConstraints();
                gbc_voyageParticularsPanel.gridwidth = 2;
                gbc_voyageParticularsPanel.insets = noPadding;
                gbc_voyageParticularsPanel.fill = GridBagConstraints.BOTH;
                gbc_voyageParticularsPanel.gridx = 0;
                gbc_voyageParticularsPanel.gridy = 6;
                FalForm1Panel.add(voyageParticularsPanel, gbc_voyageParticularsPanel);
                voyageParticularsPanel.setLayout(new BorderLayout(0, 0));
                {
                    JTextArea textArea = new JTextArea();
                    textArea.setRows(3);
                    JScrollPane sp = new JScrollPane(textArea);

                    voyageParticularsPanel.add(sp, BorderLayout.CENTER);
                }
            }
            {
                JPanel briefDescriptionOfTheCargo = new JPanel();
                briefDescriptionOfTheCargo.setBorder(new TitledBorder(null, "13.  Brief description of the cargo",
                        TitledBorder.LEADING, TitledBorder.TOP, null, null));
                GridBagConstraints gbc_briefDescriptionOfTheCargo = new GridBagConstraints();
                gbc_briefDescriptionOfTheCargo.insets = noPadding;
                gbc_briefDescriptionOfTheCargo.gridwidth = 2;
                gbc_briefDescriptionOfTheCargo.fill = GridBagConstraints.BOTH;
                gbc_briefDescriptionOfTheCargo.gridx = 0;
                gbc_briefDescriptionOfTheCargo.gridy = 7;
                FalForm1Panel.add(briefDescriptionOfTheCargo, gbc_briefDescriptionOfTheCargo);
                briefDescriptionOfTheCargo.setLayout(new BorderLayout(0, 0));
                {
                    JTextArea textArea = new JTextArea();
                    JScrollPane sp = new JScrollPane(textArea);

                    briefDescriptionOfTheCargo.add(sp, BorderLayout.CENTER);
                }
            }
            {
                JPanel crewPassengersandDeclarationParentPanel = new JPanel();
                GridBagConstraints gbc_crewPassengersandDeclarationParentPanel = new GridBagConstraints();
                gbc_crewPassengersandDeclarationParentPanel.insets = noPadding;
                gbc_crewPassengersandDeclarationParentPanel.fill = GridBagConstraints.BOTH;
                gbc_crewPassengersandDeclarationParentPanel.gridx = 0;
                gbc_crewPassengersandDeclarationParentPanel.gridy = 8;
                FalForm1Panel.add(crewPassengersandDeclarationParentPanel, gbc_crewPassengersandDeclarationParentPanel);
                GridBagLayout gbl_crewPassengersandDeclarationParentPanel = new GridBagLayout();
                gbl_crewPassengersandDeclarationParentPanel.columnWidths = new int[] { 0, 0, 0 };
                gbl_crewPassengersandDeclarationParentPanel.rowHeights = new int[] { 0, 0, 0, 0 };
                gbl_crewPassengersandDeclarationParentPanel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
                gbl_crewPassengersandDeclarationParentPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
                crewPassengersandDeclarationParentPanel.setLayout(gbl_crewPassengersandDeclarationParentPanel);
                {
                    JPanel numberOfCrewPanel = new JPanel();
                    numberOfCrewPanel.setBorder(new TitledBorder(null, "14.  Number of crew ", TitledBorder.LEADING,
                            TitledBorder.TOP, null, null));
                    GridBagConstraints gbc_numberOfCrewPanel = new GridBagConstraints();
                    gbc_numberOfCrewPanel.insets = noPadding;
                    gbc_numberOfCrewPanel.fill = GridBagConstraints.BOTH;
                    gbc_numberOfCrewPanel.gridx = 0;
                    gbc_numberOfCrewPanel.gridy = 0;
                    crewPassengersandDeclarationParentPanel.add(numberOfCrewPanel, gbc_numberOfCrewPanel);
                    numberOfCrewPanel.setLayout(new BorderLayout(0, 0));
                    {
                        textField_13 = new JTextField();
                        numberOfCrewPanel.add(textField_13, BorderLayout.CENTER);
                        textField_13.setColumns(10);
                    }
                }
                {
                    JPanel numberOfPassengersPanel = new JPanel();
                    numberOfPassengersPanel.setBorder(new TitledBorder(null, "15.  Number of passengers", TitledBorder.LEADING,
                            TitledBorder.TOP, null, null));
                    GridBagConstraints gbc_numberOfPassengersPanel = new GridBagConstraints();
                    gbc_numberOfPassengersPanel.insets = noPadding;
                    gbc_numberOfPassengersPanel.fill = GridBagConstraints.BOTH;
                    gbc_numberOfPassengersPanel.gridx = 1;
                    gbc_numberOfPassengersPanel.gridy = 0;
                    crewPassengersandDeclarationParentPanel.add(numberOfPassengersPanel, gbc_numberOfPassengersPanel);
                    numberOfPassengersPanel.setLayout(new BorderLayout(0, 0));
                    {
                        textField_14 = new JTextField();
                        numberOfPassengersPanel.add(textField_14, BorderLayout.CENTER);
                        textField_14.setColumns(10);
                    }
                }
                {
                    JPanel panel = new JPanel();
                    panel.setBorder(null);
                    GridBagConstraints gbc_panel = new GridBagConstraints();
                    gbc_panel.gridwidth = 2;
                    gbc_panel.insets = noPadding;
                    gbc_panel.fill = GridBagConstraints.VERTICAL;
                    gbc_panel.gridx = 0;
                    gbc_panel.gridy = 1;
                    crewPassengersandDeclarationParentPanel.add(panel, gbc_panel);
                    panel.setLayout(new BorderLayout(0, 0));
                    {
                        JLabel lblAttachedDocumentsindicate = new JLabel("Attached Documents (indicate number of copies)");
                        panel.add(lblAttachedDocumentsindicate, BorderLayout.CENTER);
                    }
                }
                {
                    JPanel cargoDeclarationPanel = new JPanel();
                    cargoDeclarationPanel.setBorder(new TitledBorder(null, "17.  Cargo Declaration", TitledBorder.LEADING,
                            TitledBorder.TOP, null, null));
                    GridBagConstraints gbc_cargoDeclarationPanel = new GridBagConstraints();
                    gbc_cargoDeclarationPanel.insets = noPadding;
                    gbc_cargoDeclarationPanel.fill = GridBagConstraints.BOTH;
                    gbc_cargoDeclarationPanel.gridx = 0;
                    gbc_cargoDeclarationPanel.gridy = 2;
                    crewPassengersandDeclarationParentPanel.add(cargoDeclarationPanel, gbc_cargoDeclarationPanel);
                    cargoDeclarationPanel.setLayout(new BorderLayout(0, 0));
                    {
                        JButton btnAdd = new JButton("Add");
                        btnAdd.setEnabled(false);
                        cargoDeclarationPanel.add(btnAdd, BorderLayout.EAST);
                    }
                    {
                        JLabel label = new JLabel("0");
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        cargoDeclarationPanel.add(label, BorderLayout.CENTER);
                    }
                }
                {
                    JPanel shipStoresDeclarationPanel = new JPanel();
                    shipStoresDeclarationPanel.setBorder(new TitledBorder(null, "18.  Ship\u2019s Stores Declaration",
                            TitledBorder.LEADING, TitledBorder.TOP, null, null));
                    GridBagConstraints gbc_shipStoresDeclarationPanel = new GridBagConstraints();
                    gbc_shipStoresDeclarationPanel.fill = GridBagConstraints.BOTH;
                    gbc_shipStoresDeclarationPanel.gridx = 1;
                    gbc_shipStoresDeclarationPanel.gridy = 2;
                    crewPassengersandDeclarationParentPanel.add(shipStoresDeclarationPanel, gbc_shipStoresDeclarationPanel);
                    shipStoresDeclarationPanel.setLayout(new BorderLayout(0, 0));
                    {
                        JButton btnAdd_1 = new JButton("Add");
                        btnAdd_1.setEnabled(false);
                        shipStoresDeclarationPanel.add(btnAdd_1, BorderLayout.EAST);
                    }
                    {
                        JLabel label = new JLabel("0");
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        shipStoresDeclarationPanel.add(label, BorderLayout.CENTER);
                    }
                }
            }
            {
                JPanel remarksPanel = new JPanel();
                remarksPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "16.  Remarks",
                        TitledBorder.LEADING, TitledBorder.TOP, null, null));
                GridBagConstraints gbc_remarksPanel = new GridBagConstraints();
                gbc_remarksPanel.insets = noPadding;
                gbc_remarksPanel.fill = GridBagConstraints.BOTH;
                gbc_remarksPanel.gridx = 1;
                gbc_remarksPanel.gridy = 8;
                FalForm1Panel.add(remarksPanel, gbc_remarksPanel);
                remarksPanel.setLayout(new BorderLayout(0, 0));
                {
                    JTextArea textArea = new JTextArea();
                    JScrollPane sp = new JScrollPane(textArea);

                    remarksPanel.add(sp, BorderLayout.CENTER);
                }
            }
            {
                JPanel crewListPassengerListOthersParentPanel = new JPanel();
                GridBagConstraints gbc_crewListPassengerListOthersParentPanel = new GridBagConstraints();
                gbc_crewListPassengerListOthersParentPanel.insets = noPadding;
                gbc_crewListPassengerListOthersParentPanel.fill = GridBagConstraints.BOTH;
                gbc_crewListPassengerListOthersParentPanel.gridx = 0;
                gbc_crewListPassengerListOthersParentPanel.gridy = 9;
                FalForm1Panel.add(crewListPassengerListOthersParentPanel, gbc_crewListPassengerListOthersParentPanel);
                GridBagLayout gbl_crewListPassengerListOthersParentPanel = new GridBagLayout();
                gbl_crewListPassengerListOthersParentPanel.columnWidths = new int[] { 0, 0, 0 };
                gbl_crewListPassengerListOthersParentPanel.rowHeights = new int[] { 0, 0, 0 };
                gbl_crewListPassengerListOthersParentPanel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
                gbl_crewListPassengerListOthersParentPanel.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
                crewListPassengerListOthersParentPanel.setLayout(gbl_crewListPassengerListOthersParentPanel);
                {
                    JPanel crewListPanel = new JPanel();
                    crewListPanel.setBorder(new TitledBorder(null, "19.  Crew List", TitledBorder.LEADING, TitledBorder.TOP, null,
                            null));
                    GridBagConstraints gbc_crewListPanel = new GridBagConstraints();
                    gbc_crewListPanel.insets = noPadding;
                    gbc_crewListPanel.fill = GridBagConstraints.BOTH;
                    gbc_crewListPanel.gridx = 0;
                    gbc_crewListPanel.gridy = 0;
                    crewListPassengerListOthersParentPanel.add(crewListPanel, gbc_crewListPanel);
                    crewListPanel.setLayout(new BorderLayout(0, 0));
                    {
                        JButton btnAdd_2 = new JButton("Add");
                        btnAdd_2.setEnabled(false);
                        crewListPanel.add(btnAdd_2, BorderLayout.EAST);
                    }
                    {
                        JLabel label = new JLabel("0");
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        crewListPanel.add(label, BorderLayout.CENTER);
                    }
                }
                {
                    JPanel passengerListPanel = new JPanel();
                    passengerListPanel.setBorder(new TitledBorder(null, "20.  Passenger List", TitledBorder.LEADING,
                            TitledBorder.TOP, null, null));
                    GridBagConstraints gbc_passengerListPanel = new GridBagConstraints();
                    gbc_passengerListPanel.insets = noPadding;
                    gbc_passengerListPanel.fill = GridBagConstraints.BOTH;
                    gbc_passengerListPanel.gridx = 1;
                    gbc_passengerListPanel.gridy = 0;
                    crewListPassengerListOthersParentPanel.add(passengerListPanel, gbc_passengerListPanel);
                    passengerListPanel.setLayout(new BorderLayout(0, 0));
                    {
                        JButton btnAdd_3 = new JButton("Add");
                        btnAdd_3.setEnabled(false);
                        passengerListPanel.add(btnAdd_3, BorderLayout.EAST);
                    }
                    {
                        JLabel label = new JLabel("0");
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        passengerListPanel.add(label, BorderLayout.CENTER);
                    }
                }
                {
                    JPanel crewEffectsDeclarationPanel = new JPanel();
                    crewEffectsDeclarationPanel.setBorder(new TitledBorder(null,
                            "22.  Crew\u2019s Effects Declaration (only on arrival)", TitledBorder.LEADING, TitledBorder.TOP, null,
                            null));
                    GridBagConstraints gbc_crewEffectsDeclarationPanel = new GridBagConstraints();
                    gbc_crewEffectsDeclarationPanel.insets = noPadding;
                    gbc_crewEffectsDeclarationPanel.fill = GridBagConstraints.BOTH;
                    gbc_crewEffectsDeclarationPanel.gridx = 0;
                    gbc_crewEffectsDeclarationPanel.gridy = 1;
                    crewListPassengerListOthersParentPanel.add(crewEffectsDeclarationPanel, gbc_crewEffectsDeclarationPanel);
                    crewEffectsDeclarationPanel.setLayout(new BorderLayout(0, 0));
                    {
                        JButton btnAdd_4 = new JButton("Add");
                        btnAdd_4.setEnabled(false);
                        crewEffectsDeclarationPanel.add(btnAdd_4, BorderLayout.EAST);
                    }
                    {
                        JLabel label = new JLabel("0");
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        crewEffectsDeclarationPanel.add(label, BorderLayout.CENTER);
                    }
                }
                {
                    JPanel panel = new JPanel();
                    panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                            "22.  Crew\u2019s Effects Declaration (only on arrival)", TitledBorder.LEFT, TitledBorder.TOP, null,
                            null));
                    GridBagConstraints gbc_panel = new GridBagConstraints();
                    gbc_panel.fill = GridBagConstraints.BOTH;
                    gbc_panel.gridx = 1;
                    gbc_panel.gridy = 1;
                    crewListPassengerListOthersParentPanel.add(panel, gbc_panel);
                    panel.setLayout(new BorderLayout(0, 0));
                    {
                        JButton btnAdd_5 = new JButton("Add");
                        btnAdd_5.setEnabled(false);
                        panel.add(btnAdd_5, BorderLayout.EAST);
                    }
                    {
                        JLabel label = new JLabel("0");
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        panel.add(label, BorderLayout.CENTER);
                    }
                }
            }
            {
                JPanel shipWasteRequirementsPanel = new JPanel();
                shipWasteRequirementsPanel.setBorder(new TitledBorder(null,
                        "21.\tThe ship\u2019s requirements in terms of waste and residue reception facilities",
                        TitledBorder.LEADING, TitledBorder.TOP, null, null));
                GridBagConstraints gbc_shipWasteRequirementsPanel = new GridBagConstraints();
                gbc_shipWasteRequirementsPanel.insets = noPadding;
                gbc_shipWasteRequirementsPanel.fill = GridBagConstraints.BOTH;
                gbc_shipWasteRequirementsPanel.gridx = 1;
                gbc_shipWasteRequirementsPanel.gridy = 9;
                FalForm1Panel.add(shipWasteRequirementsPanel, gbc_shipWasteRequirementsPanel);
                shipWasteRequirementsPanel.setLayout(new BorderLayout(0, 0));
                {
                    JTextArea textArea = new JTextArea();
                    JScrollPane sp = new JScrollPane(textArea);
                    shipWasteRequirementsPanel.add(sp, BorderLayout.CENTER);
                }
            }
            {
                JPanel dateAndSignature = new JPanel();
                dateAndSignature.setBorder(new TitledBorder(null, "24.  Date and signature by master, authorized agent or officer",
                        TitledBorder.LEADING, TitledBorder.TOP, null, null));
                GridBagConstraints gbc_dateAndSignature = new GridBagConstraints();
                gbc_dateAndSignature.gridwidth = 2;
                gbc_dateAndSignature.fill = GridBagConstraints.BOTH;
                gbc_dateAndSignature.gridx = 0;
                gbc_dateAndSignature.gridy = 10;
                FalForm1Panel.add(dateAndSignature, gbc_dateAndSignature);
                dateAndSignature.setLayout(new BorderLayout(0, 0));
                {
                    textField_8 = new JTextField();
                    dateAndSignature.add(textField_8, BorderLayout.CENTER);
                    textField_8.setColumns(10);
                }
            }
            {
                JPanel FalForm2Panel = new JPanel();
                tabbedPane.addTab("Cargo Declaration", null, FalForm2Panel, null);
            }
            {
                JPanel FalForm3Panel = new JPanel();
                tabbedPane.addTab("Ship's Stores Declaration", null, FalForm3Panel, null);
            }
            {
                JPanel FalForm4Panel = new JPanel();
                tabbedPane.addTab("Crew's Effects Declaration", null, FalForm4Panel, null);
            }
            {
                JPanel FalForm5Panel = new JPanel();
                tabbedPane.addTab("Crew List", null, FalForm5Panel, null);
            }
            {
                JPanel FalForm6Panel = new JPanel();
                tabbedPane.addTab("Passenger List", null, FalForm6Panel, null);
            }
            {
                JPanel FalForm7Panel = new JPanel();
                tabbedPane.addTab("Dangerous Goods", null, FalForm7Panel, null);
            }
        }

        this.pack();
    }

}

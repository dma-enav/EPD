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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.fal.FALForm1;
import dk.dma.epd.ship.fal.FALManager;
import dk.dma.epd.ship.fal.FALReport;
import dk.dma.epd.ship.fal.StaticFalShipData;

public class FALReportingDialog extends JDialog implements ActionListener, ChangeListener {

    private static final long serialVersionUID = 1L;
    private final JPanel FalForm1Panel = new JPanel();

    private JButton importBtn;
    private JButton resetButton;
    private JButton printBtn;
    private JButton saveButton;

    private JCheckBox chckbxArrival;
    private JCheckBox chckbxDepature;
    private JTextField nameAndTypeField;
    private JTextField imoField;
    private JTextField callsignField;
    private JTextField voyageNumberField;
    private JTextField arrivalDepatureField;
    private JTextField dateAndTimeField;
    private JTextField flagStateField;
    private JTextField nameOfMasterField;
    private JTextField lastNextPortField;
    private JTextField certificateOfRegistryField;
    private JTextField grossTonnageField;
    private JTextField netTonnageField;
    private JTextField positionOfShipInPortField;
    private JTextField signatureField;
    private JTextField numberOfCrewField;
    private JTextField numberOfPassengersField;

    private JTextArea shipsAgentsField;
    private JTextArea voyageParticularsField;
    private JTextArea cargoDescriptionField;
    private JTextArea remarksField;
    private JTextArea shipWasteRequirementsField;

    private Insets noPadding = new Insets(0, 0, 0, 0);

    private FALManager falManager;
    private JTextField falReportNameField;

    private long id = -1;

    /**
     * Create the dialog.
     */
    public FALReportingDialog(FALManager falManager, long id) {
        this.falManager = falManager;
        this.id = id;

        initGUI();
        initFromStatic();

        if (id != -1) {
            loadFalData();
        }
    }

    private void initGUI() {
        setBounds(100, 100, 898, 882);
        setMinimumSize(new Dimension(898, 882));
        setVisible(true);
        this.setResizable(true);

        getContentPane().setLayout(new BorderLayout());

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                importBtn = new JButton("Import");
                buttonPane.add(importBtn);
            }
            {
                resetButton = new JButton("Reset All");
                buttonPane.add(resetButton);
            }
            {
                printBtn = new JButton("Export/Print");
                buttonPane.add(printBtn);
            }
            {
                saveButton = new JButton("Save and Close");
                buttonPane.add(saveButton);
                getRootPane().setDefaultButton(saveButton);
            }

            importBtn.addActionListener(this);
            resetButton.addActionListener(this);
            printBtn.addActionListener(this);
            saveButton.addActionListener(this);
        }
        getContentPane().add(FalForm1Panel, BorderLayout.CENTER);
        FalForm1Panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gbl_FalForm1Panel = new GridBagLayout();

        gbl_FalForm1Panel.columnWidths = new int[] { 0, 0, 0 };
        gbl_FalForm1Panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_FalForm1Panel.columnWeights = new double[] { 1.0, 2.0, Double.MIN_VALUE };
        gbl_FalForm1Panel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
        FalForm1Panel.setLayout(gbl_FalForm1Panel);
        {
            JPanel namePanel = new JPanel();
            namePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Report Name (optional)",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GridBagConstraints gbc_namePanel = new GridBagConstraints();
            gbc_namePanel.insets = new Insets(0, 0, 5, 5);
            gbc_namePanel.fill = GridBagConstraints.BOTH;
            gbc_namePanel.gridx = 0;
            gbc_namePanel.gridy = 0;
            FalForm1Panel.add(namePanel, gbc_namePanel);
            namePanel.setLayout(new BorderLayout(0, 0));
            {
                falReportNameField = new JTextField();
                namePanel.add(falReportNameField);
                falReportNameField.setColumns(10);
            }
        }

        {
            JPanel depatureArrivalParentPanel = new JPanel();
            GridBagConstraints gbc_depatureArrivalParentPanel = new GridBagConstraints();
            gbc_depatureArrivalParentPanel.insets = new Insets(0, 0, 5, 0);
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
                chckbxArrival = new JCheckBox("Arrival");
                GridBagConstraints gbc_chckbxArrival = new GridBagConstraints();
                gbc_chckbxArrival.insets = noPadding;
                gbc_chckbxArrival.anchor = GridBagConstraints.WEST;
                gbc_chckbxArrival.gridx = 0;
                gbc_chckbxArrival.gridy = 0;
                depatureArrivalParentPanel.add(chckbxArrival, gbc_chckbxArrival);
            }
            {
                chckbxDepature = new JCheckBox("Depature");
                GridBagConstraints gbc_chckbxDepature = new GridBagConstraints();
                gbc_chckbxDepature.anchor = GridBagConstraints.WEST;
                gbc_chckbxDepature.gridx = 1;
                gbc_chckbxDepature.gridy = 0;
                depatureArrivalParentPanel.add(chckbxDepature, gbc_chckbxDepature);
            }
            chckbxArrival.addChangeListener(this);
            chckbxDepature.addChangeListener(this);
        }

        // Name and type of ship
        {
            JPanel nameAndTypeOfShipPanel = new JPanel();
            nameAndTypeOfShipPanel.setBorder(new TitledBorder(null, "1.1 Name and type of ship", TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));
            GridBagConstraints gbc_nameAndTypeOfShipPanel = new GridBagConstraints();
            gbc_nameAndTypeOfShipPanel.insets = new Insets(0, 0, 5, 5);
            gbc_nameAndTypeOfShipPanel.fill = GridBagConstraints.BOTH;
            gbc_nameAndTypeOfShipPanel.gridx = 0;
            gbc_nameAndTypeOfShipPanel.gridy = 1;
            FalForm1Panel.add(nameAndTypeOfShipPanel, gbc_nameAndTypeOfShipPanel);
            nameAndTypeOfShipPanel.setLayout(new BorderLayout(0, 0));
            {
                nameAndTypeField = new JTextField();

                nameAndTypeOfShipPanel.add(nameAndTypeField);
                nameAndTypeField.setColumns(10);
            }
        }
        {
            JPanel imoNumberPanel = new JPanel();
            imoNumberPanel.setBorder(new TitledBorder(null, "1.2 IMO number", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GridBagConstraints gbc_imoNumberPanel = new GridBagConstraints();
            gbc_imoNumberPanel.insets = new Insets(0, 0, 5, 0);
            gbc_imoNumberPanel.fill = GridBagConstraints.BOTH;
            gbc_imoNumberPanel.gridx = 1;
            gbc_imoNumberPanel.gridy = 1;
            // gbc_imoNumberPanel.anchor = GridBagConstraints.CENTER;
            FalForm1Panel.add(imoNumberPanel, gbc_imoNumberPanel);
            imoNumberPanel.setLayout(new BorderLayout(0, 0));
            {
                imoField = new JTextField();

                imoNumberPanel.add(imoField, BorderLayout.CENTER);
                imoField.setColumns(10);
            }
        }
        {
            JPanel callsignPanel = new JPanel();
            callsignPanel.setBorder(new TitledBorder(null, "1.3 Call sign", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GridBagConstraints gbc_callsignPanel = new GridBagConstraints();
            gbc_callsignPanel.insets = new Insets(0, 0, 5, 5);
            gbc_callsignPanel.fill = GridBagConstraints.BOTH;
            gbc_callsignPanel.gridx = 0;
            gbc_callsignPanel.gridy = 2;
            FalForm1Panel.add(callsignPanel, gbc_callsignPanel);
            callsignPanel.setLayout(new BorderLayout(0, 0));
            {
                callsignField = new JTextField();
                callsignPanel.add(callsignField, BorderLayout.CENTER);
                callsignField.setColumns(10);
            }
        }
        {
            JPanel voyageNumberPanel = new JPanel();
            voyageNumberPanel.setBorder(new TitledBorder(null, "1.4 Voyage number", TitledBorder.LEADING, TitledBorder.TOP, null,
                    null));
            GridBagConstraints gbc_voyageNumberPanel = new GridBagConstraints();
            gbc_voyageNumberPanel.insets = new Insets(0, 0, 5, 0);
            gbc_voyageNumberPanel.fill = GridBagConstraints.BOTH;
            gbc_voyageNumberPanel.gridx = 1;
            gbc_voyageNumberPanel.gridy = 2;
            FalForm1Panel.add(voyageNumberPanel, gbc_voyageNumberPanel);
            voyageNumberPanel.setLayout(new BorderLayout(0, 0));
            {
                voyageNumberField = new JTextField();
                voyageNumberPanel.add(voyageNumberField, BorderLayout.CENTER);
                voyageNumberField.setColumns(10);
            }
        }
        {
            JPanel portofArrivalDeparturePanel = new JPanel();
            portofArrivalDeparturePanel.setBorder(new TitledBorder(null, "2. Port of arrival/depature", TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));
            GridBagConstraints gbc_portofArrivalDeparturePanel = new GridBagConstraints();
            gbc_portofArrivalDeparturePanel.insets = new Insets(0, 0, 5, 5);
            gbc_portofArrivalDeparturePanel.fill = GridBagConstraints.BOTH;
            gbc_portofArrivalDeparturePanel.gridx = 0;
            gbc_portofArrivalDeparturePanel.gridy = 3;
            FalForm1Panel.add(portofArrivalDeparturePanel, gbc_portofArrivalDeparturePanel);
            portofArrivalDeparturePanel.setLayout(new BorderLayout(0, 0));
            {
                arrivalDepatureField = new JTextField();
                portofArrivalDeparturePanel.add(arrivalDepatureField, BorderLayout.CENTER);
                arrivalDepatureField.setColumns(10);
            }
        }
        {
            JPanel dateandtimeofarrivalDepaturePanel = new JPanel();
            dateandtimeofarrivalDepaturePanel.setBorder(new TitledBorder(null, "3.  Date and time of arrival/departure",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GridBagConstraints gbc_dateandtimeofarrivalDepaturePanel = new GridBagConstraints();
            gbc_dateandtimeofarrivalDepaturePanel.insets = new Insets(0, 0, 5, 0);
            gbc_dateandtimeofarrivalDepaturePanel.fill = GridBagConstraints.BOTH;
            gbc_dateandtimeofarrivalDepaturePanel.gridx = 1;
            gbc_dateandtimeofarrivalDepaturePanel.gridy = 3;
            FalForm1Panel.add(dateandtimeofarrivalDepaturePanel, gbc_dateandtimeofarrivalDepaturePanel);
            dateandtimeofarrivalDepaturePanel.setLayout(new BorderLayout(0, 0));
            {
                dateAndTimeField = new JTextField();
                dateandtimeofarrivalDepaturePanel.add(dateAndTimeField, BorderLayout.CENTER);
                dateAndTimeField.setColumns(10);
            }
        }
        {
            JPanel panel45Parent = new JPanel();
            GridBagConstraints gbc_panel45Parent = new GridBagConstraints();
            gbc_panel45Parent.insets = new Insets(0, 0, 5, 5);
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
                    flagStateField = new JTextField();
                    flagSateOfShipPanel.add(flagStateField, BorderLayout.CENTER);
                    flagStateField.setColumns(10);
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
                    nameOfMasterField = new JTextField();
                    nameOfMasterPanel.add(nameOfMasterField, BorderLayout.CENTER);
                    nameOfMasterField.setColumns(10);
                }
            }
        }
        {
            JPanel lastPortOfCallPanel = new JPanel();
            lastPortOfCallPanel.setBorder(new TitledBorder(null, "6.  Last port of call/Next port of call", TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));
            GridBagConstraints gbc_lastPortOfCallPanel = new GridBagConstraints();
            gbc_lastPortOfCallPanel.insets = new Insets(0, 0, 5, 0);
            gbc_lastPortOfCallPanel.fill = GridBagConstraints.BOTH;
            gbc_lastPortOfCallPanel.gridx = 1;
            gbc_lastPortOfCallPanel.gridy = 4;
            FalForm1Panel.add(lastPortOfCallPanel, gbc_lastPortOfCallPanel);
            lastPortOfCallPanel.setLayout(new BorderLayout(0, 0));
            {
                lastNextPortField = new JTextField();
                lastPortOfCallPanel.add(lastNextPortField, BorderLayout.CENTER);
                lastNextPortField.setColumns(10);
            }
        }
        {
            JPanel certificateTonnagePositionParentPanel = new JPanel();
            GridBagConstraints gbc_certificateTonnagePositionParentPanel = new GridBagConstraints();
            gbc_certificateTonnagePositionParentPanel.insets = new Insets(0, 0, 5, 5);
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
                    grossTonnagePanel.setBorder(new TitledBorder(null, "9.  Gross tonnage", TitledBorder.LEADING, TitledBorder.TOP,
                            null, null));
                    GridBagConstraints gbc_grossTonnagePanel = new GridBagConstraints();
                    gbc_grossTonnagePanel.insets = noPadding;
                    gbc_grossTonnagePanel.fill = GridBagConstraints.BOTH;
                    gbc_grossTonnagePanel.gridx = 0;
                    gbc_grossTonnagePanel.gridy = 0;
                    tonnageParentPanel.add(grossTonnagePanel, gbc_grossTonnagePanel);
                    grossTonnagePanel.setLayout(new BorderLayout(0, 0));
                    {
                        grossTonnageField = new JTextField();
                        grossTonnagePanel.add(grossTonnageField, BorderLayout.CENTER);
                        grossTonnageField.setColumns(10);
                    }
                }
                {
                    JPanel netTonnagePanel = new JPanel();
                    netTonnagePanel.setBorder(new TitledBorder(null, "10.  Net tonnage", TitledBorder.LEADING, TitledBorder.TOP,
                            null, null));
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
                    positionOfShipInPortField = new JTextField();
                    positionOfTheShip.add(positionOfShipInPortField, BorderLayout.CENTER);
                    positionOfShipInPortField.setColumns(10);
                }
            }
        }
        {
            JPanel nameandContactDetailsShipAgentPanel = new JPanel();
            nameandContactDetailsShipAgentPanel.setBorder(new TitledBorder(null,
                    "8.  Name and contact details of ship\u2019s agent", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GridBagConstraints gbc_nameandContactDetailsShipAgentPanel = new GridBagConstraints();
            gbc_nameandContactDetailsShipAgentPanel.insets = new Insets(0, 0, 5, 0);
            gbc_nameandContactDetailsShipAgentPanel.fill = GridBagConstraints.BOTH;
            gbc_nameandContactDetailsShipAgentPanel.gridx = 1;
            gbc_nameandContactDetailsShipAgentPanel.gridy = 5;
            FalForm1Panel.add(nameandContactDetailsShipAgentPanel, gbc_nameandContactDetailsShipAgentPanel);
            nameandContactDetailsShipAgentPanel.setLayout(new BorderLayout(0, 0));
            {
                shipsAgentsField = new JTextArea();
                shipsAgentsField.setRows(3);
                JScrollPane sp = new JScrollPane(shipsAgentsField);

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
            gbc_voyageParticularsPanel.insets = new Insets(0, 0, 5, 0);
            gbc_voyageParticularsPanel.fill = GridBagConstraints.BOTH;
            gbc_voyageParticularsPanel.gridx = 0;
            gbc_voyageParticularsPanel.gridy = 6;
            FalForm1Panel.add(voyageParticularsPanel, gbc_voyageParticularsPanel);
            voyageParticularsPanel.setLayout(new BorderLayout(0, 0));
            {
                voyageParticularsField = new JTextArea();
                voyageParticularsField.setRows(3);
                JScrollPane sp = new JScrollPane(voyageParticularsField);

                voyageParticularsPanel.add(sp, BorderLayout.CENTER);
            }
        }
        {
            JPanel briefDescriptionOfTheCargo = new JPanel();
            briefDescriptionOfTheCargo.setBorder(new TitledBorder(null, "13.  Brief description of the cargo",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GridBagConstraints gbc_briefDescriptionOfTheCargo = new GridBagConstraints();
            gbc_briefDescriptionOfTheCargo.insets = new Insets(0, 0, 5, 0);
            gbc_briefDescriptionOfTheCargo.gridwidth = 2;
            gbc_briefDescriptionOfTheCargo.fill = GridBagConstraints.BOTH;
            gbc_briefDescriptionOfTheCargo.gridx = 0;
            gbc_briefDescriptionOfTheCargo.gridy = 7;
            FalForm1Panel.add(briefDescriptionOfTheCargo, gbc_briefDescriptionOfTheCargo);
            briefDescriptionOfTheCargo.setLayout(new BorderLayout(0, 0));
            {
                cargoDescriptionField = new JTextArea();
                cargoDescriptionField.setRows(3);
                JScrollPane sp = new JScrollPane(cargoDescriptionField);

                briefDescriptionOfTheCargo.add(sp, BorderLayout.CENTER);
            }
        }
        {
            JPanel crewPassengersandDeclarationParentPanel = new JPanel();
            GridBagConstraints gbc_crewPassengersandDeclarationParentPanel = new GridBagConstraints();
            gbc_crewPassengersandDeclarationParentPanel.insets = new Insets(0, 0, 5, 5);
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
                numberOfCrewPanel.setBorder(new TitledBorder(null, "14.  Number of crew ", TitledBorder.LEADING, TitledBorder.TOP,
                        null, null));
                GridBagConstraints gbc_numberOfCrewPanel = new GridBagConstraints();
                gbc_numberOfCrewPanel.insets = noPadding;
                gbc_numberOfCrewPanel.fill = GridBagConstraints.BOTH;
                gbc_numberOfCrewPanel.gridx = 0;
                gbc_numberOfCrewPanel.gridy = 0;
                crewPassengersandDeclarationParentPanel.add(numberOfCrewPanel, gbc_numberOfCrewPanel);
                numberOfCrewPanel.setLayout(new BorderLayout(0, 0));
                {
                    numberOfCrewField = new JTextField();
                    numberOfCrewPanel.add(numberOfCrewField, BorderLayout.CENTER);
                    numberOfCrewField.setColumns(10);
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
                    numberOfPassengersField = new JTextField();
                    numberOfPassengersPanel.add(numberOfPassengersField, BorderLayout.CENTER);
                    numberOfPassengersField.setColumns(10);
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
            gbc_remarksPanel.insets = new Insets(0, 0, 5, 0);
            gbc_remarksPanel.fill = GridBagConstraints.BOTH;
            gbc_remarksPanel.gridx = 1;
            gbc_remarksPanel.gridy = 8;
            FalForm1Panel.add(remarksPanel, gbc_remarksPanel);
            remarksPanel.setLayout(new BorderLayout(0, 0));
            {
                remarksField = new JTextArea();
                remarksField.setRows(3);
                JScrollPane sp = new JScrollPane(remarksField);

                remarksPanel.add(sp, BorderLayout.CENTER);
            }
        }
        {
            JPanel crewListPassengerListOthersParentPanel = new JPanel();
            GridBagConstraints gbc_crewListPassengerListOthersParentPanel = new GridBagConstraints();
            gbc_crewListPassengerListOthersParentPanel.insets = new Insets(0, 0, 5, 5);
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
                crewListPanel
                        .setBorder(new TitledBorder(null, "19.  Crew List", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
                passengerListPanel.setBorder(new TitledBorder(null, "20.  Passenger List", TitledBorder.LEADING, TitledBorder.TOP,
                        null, null));
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
                        "22.  Crew\u2019s Effects Declaration (only on arrival)", TitledBorder.LEFT, TitledBorder.TOP, null, null));
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
                    "21.\tThe ship\u2019s requirements in terms of waste and residue reception facilities", TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));
            GridBagConstraints gbc_shipWasteRequirementsPanel = new GridBagConstraints();
            gbc_shipWasteRequirementsPanel.insets = new Insets(0, 0, 5, 0);
            gbc_shipWasteRequirementsPanel.fill = GridBagConstraints.BOTH;
            gbc_shipWasteRequirementsPanel.gridx = 1;
            gbc_shipWasteRequirementsPanel.gridy = 9;
            FalForm1Panel.add(shipWasteRequirementsPanel, gbc_shipWasteRequirementsPanel);
            shipWasteRequirementsPanel.setLayout(new BorderLayout(0, 0));
            {
                shipWasteRequirementsField = new JTextArea();
                JScrollPane sp = new JScrollPane(shipWasteRequirementsField);
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
                signatureField = new JTextField();
                dateAndSignature.add(signatureField, BorderLayout.CENTER);
                signatureField.setColumns(10);
            }
        }

        this.pack();
    }

    private void initFromStatic() {
        StaticFalShipData staticData = falManager.getStaticShipData();

        certificateOfRegistryField.setText(staticData.getCertificateOfRegistry());
        callsignField.setText(staticData.getCallSign());

        flagStateField.setText(staticData.getFlagStateOfShip());
        grossTonnageField.setText(staticData.getGrossTonnage());
        imoField.setText(staticData.getImoNumber());
        shipsAgentsField.setText(staticData.getNameAndContactDetalsOfShipsAgent());
        nameAndTypeField.setText(staticData.getNameAndTypeOfShip());
        nameOfMasterField.setText(staticData.getNameOfMaster());

        netTonnageField.setText(staticData.getNetTonnage());

    };

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        setOpacity((float) 0.95);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == importBtn) {
            importFALReport();

        } else if (arg0.getSource() == resetButton) {
            resetAllFields();
        } else if (arg0.getSource() == printBtn) {
            print();
        } else if (arg0.getSource() == saveButton) {
            saveFALReport();
        }

    }

    private void loadFalData() {
        FALReport falReport = EPDShip.getInstance().getFalManager().getFalReportWithID(id);

        falReportNameField.setText(falReport.getFalReportName());

        FALForm1 falForm1 = falReport.getFalform1();

        if (falForm1.isArrival()) {
            chckbxArrival.setSelected(true);
            chckbxDepature.setSelected(false);
        } else {
            chckbxArrival.setSelected(false);
            chckbxDepature.setSelected(true);
        }

        nameAndTypeField.setText(falForm1.getNameAndTypeOfShip());

        imoField.setText(falForm1.getImoNumber());

        callsignField.setText(falForm1.getCallSign());

        voyageNumberField.setText(falForm1.getVoyageNumber());

        arrivalDepatureField.setText(falForm1.getPortOfArrivalDeapture());

        dateAndTimeField.setText(falForm1.getDateAndTimeOfArrivalDepature());

        flagStateField.setText(falForm1.getFlagStateOfShip());

        nameOfMasterField.setText(falForm1.getNameOfMaster());

        lastNextPortField.setText(falForm1.getLastPortOfCall());

        certificateOfRegistryField.setText(falForm1.getCertificateOfRegistry());

        grossTonnageField.setText(falForm1.getGrossTonnage());

        netTonnageField.setText(falForm1.getNetTonnage());

        positionOfShipInPortField.setText(falForm1.getPositionOfTheShip());

        signatureField.setText(falForm1.getSignature());

        numberOfCrewField.setText(falForm1.getNumberOfCrew());

        numberOfPassengersField.setText(falForm1.getNumberOfPassengers());

        shipsAgentsField.setText(falForm1.getNameAndContactDetalsOfShipsAgent());

        voyageParticularsField.setText(falForm1.getBriefParticulars());

        cargoDescriptionField.setText(falForm1.getBriefDescriptionOfCargo());

        remarksField.setText(falForm1.getRemarks());

        shipWasteRequirementsField.setText(falForm1.getShipWasteRequirements());

    }

    private void saveFALReport() {

        // Do we create new or overwrite?

        FALReport falReport = null;

        if (id == -1) {

            falReport = new FALReport();
        } else {

            falReport = EPDShip.getInstance().getFalManager().getFalReportWithID(id);
        }

        falReport.setFalReportName(falReportNameField.getText());

        FALForm1 falForm1 = new FALForm1();

        falForm1.setArrival(chckbxArrival.isSelected());

        falForm1.setNameAndTypeOfShip(nameAndTypeField.getText());

        falForm1.setImoNumber(imoField.getText());
        falForm1.setCallSign(callsignField.getText());
        falForm1.setVoyageNumber(voyageNumberField.getText());
        falForm1.setPortOfArrivalDeapture(arrivalDepatureField.getText());
        falForm1.setDateAndTimeOfArrivalDepature(dateAndTimeField.getText());
        falForm1.setFlagStateOfShip(flagStateField.getText());
        falForm1.setNameOfMaster(nameOfMasterField.getText());
        falForm1.setLastPortOfCall(lastNextPortField.getText());
        falForm1.setCertificateOfRegistry(certificateOfRegistryField.getText());
        falForm1.setGrossTonnage(grossTonnageField.getText());
        falForm1.setNetTonnage(netTonnageField.getText());
        falForm1.setPositionOfTheShip(positionOfShipInPortField.getText());
        falForm1.setSignature(signatureField.getText());
        falForm1.setNumberOfCrew(numberOfCrewField.getText());
        falForm1.setNumberOfPassengers(numberOfPassengersField.getText());
        falForm1.setNameAndContactDetalsOfShipsAgent(shipsAgentsField.getText());
        falForm1.setBriefParticulars(voyageParticularsField.getText());
        falForm1.setBriefDescriptionOfCargo(cargoDescriptionField.getText());
        falForm1.setRemarks(remarksField.getText());
        falForm1.setShipWasteRequirements(shipWasteRequirementsField.getText());

        falReport.setFalform1(falForm1);

        if (id == -1) {
            EPDShip.getInstance().getFalManager().getFalReports().add(falReport);
        } else {
            EPDShip.getInstance().getFalManager().replaceFalReport(falReport);
        }

        this.dispose();

    }

    private void resetAllFields() {
        falReportNameField.setText("");

        chckbxArrival.setSelected(false);
        chckbxDepature.setSelected(false);

        nameAndTypeField.setText("");

        imoField.setText("");

        callsignField.setText("");

        voyageNumberField.setText("");

        arrivalDepatureField.setText("");

        dateAndTimeField.setText("");

        flagStateField.setText("");

        nameOfMasterField.setText("");

        lastNextPortField.setText("");

        certificateOfRegistryField.setText("");

        grossTonnageField.setText("");

        netTonnageField.setText("");

        positionOfShipInPortField.setText("");

        signatureField.setText("");

        numberOfCrewField.setText("");

        numberOfPassengersField.setText("");

        shipsAgentsField.setText("");

        voyageParticularsField.setText("");

        cargoDescriptionField.setText("");

        remarksField.setText("");

        shipWasteRequirementsField.setText("");
    }

    private void print() {

    }

    private void importFALReport() {

    }

    @Override
    public void stateChanged(ChangeEvent arg0) {

        // If neither is selected don't change the value
        if (!chckbxArrival.isSelected() && !chckbxDepature.isSelected()) {
            return;
        }

        if (arg0.getSource() == chckbxArrival) {

            if (chckbxArrival.isSelected()) {
                chckbxDepature.setSelected(false);
            } else {
                chckbxDepature.setSelected(true);
            }

        } else if (arg0.getSource() == chckbxDepature) {
            if (chckbxDepature.isSelected()) {
                chckbxArrival.setSelected(false);
            } else {
                chckbxArrival.setSelected(true);
            }
        }

    }

}

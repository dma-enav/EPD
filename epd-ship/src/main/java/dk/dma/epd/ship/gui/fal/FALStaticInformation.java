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

public class FALStaticInformation extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JPanel FalForm1Panel = new JPanel();
    private JTextField nameAndTypeField;
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_5;
    private JTextField textField_6;
    private JTextField textField_9;
    private JTextField textField_10;
    private JTextField textField_11;

    private Insets noPadding = new Insets(0, 0, 0, 0);

    /**
     * Create the dialog.
     */
    public FALStaticInformation() {
        setTitle("Ship Information");
        setBounds(100, 100, 620, 476);
        setMinimumSize(new Dimension(620,  476));
        setVisible(true);
        
        setResizable(true);

        
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
                JButton importBtn = new JButton("Import from AIS");
                buttonPane.add(importBtn);
            }
            {
                JButton resetButton = new JButton("Reset All");
                buttonPane.add(resetButton);
            }
            {
                JButton btnClose = new JButton("Close");
                buttonPane.add(btnClose);
            }
        }
        getContentPane().add(FalForm1Panel, BorderLayout.CENTER);
        FalForm1Panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gbl_FalForm1Panel = new GridBagLayout();

        gbl_FalForm1Panel.columnWidths = new int[] { 0, 0, 0 };
        gbl_FalForm1Panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
        gbl_FalForm1Panel.columnWeights = new double[] { 1.0, 2.0, Double.MIN_VALUE };
        gbl_FalForm1Panel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
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
                textField = new JTextField();
                textField.setText("01010101");
                imoNumberPanel.add(textField, BorderLayout.CENTER);
                textField.setColumns(10);
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
                textField_1 = new JTextField();
                callsignPanel.add(textField_1, BorderLayout.CENTER);
                textField_1.setColumns(10);
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
                textField_5 = new JTextField();
                flagSateOfShipPanel.add(textField_5, BorderLayout.CENTER);
                textField_5.setColumns(10);
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
                textField_6 = new JTextField();
                nameOfMasterPanel.add(textField_6, BorderLayout.CENTER);
                textField_6.setColumns(10);
            }
        }
        {
            JPanel certificateTonnagePositionParentPanel = new JPanel();
            GridBagConstraints gbc_certificateTonnagePositionParentPanel = new GridBagConstraints();
            gbc_certificateTonnagePositionParentPanel.insets = new Insets(0, 0, 5, 5);
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
                        textField_10 = new JTextField();
                        grossTonnagePanel.add(textField_10, BorderLayout.CENTER);
                        textField_10.setColumns(10);
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
                        textField_11 = new JTextField();
                        netTonnagePanel.add(textField_11, BorderLayout.CENTER);
                        textField_11.setColumns(10);
                    }
                }
            }
        }
        {
            JPanel nameandContactDetailsShipAgentPanel = new JPanel();
            nameandContactDetailsShipAgentPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                    "Name and contact details of ship\u2019s agent", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GridBagConstraints gbc_nameandContactDetailsShipAgentPanel = new GridBagConstraints();
            gbc_nameandContactDetailsShipAgentPanel.insets = new Insets(0, 0, 5, 0);
            gbc_nameandContactDetailsShipAgentPanel.fill = GridBagConstraints.BOTH;
            gbc_nameandContactDetailsShipAgentPanel.gridx = 1;
            gbc_nameandContactDetailsShipAgentPanel.gridy = 3;
            FalForm1Panel.add(nameandContactDetailsShipAgentPanel, gbc_nameandContactDetailsShipAgentPanel);
            nameandContactDetailsShipAgentPanel.setLayout(new BorderLayout(0, 0));
            {
                JTextArea textArea = new JTextArea();
                textArea.setRows(5);
                JScrollPane sp = new JScrollPane(textArea);

                nameandContactDetailsShipAgentPanel.add(sp, BorderLayout.CENTER);
            }
        }
        {
            JPanel shipWasteRequirementsPanel = new JPanel();
            shipWasteRequirementsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                    "The ship\u2019s requirements in terms of waste and residue reception facilities", TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));
            GridBagConstraints gbc_shipWasteRequirementsPanel = new GridBagConstraints();
            gbc_shipWasteRequirementsPanel.gridwidth = 2;
            gbc_shipWasteRequirementsPanel.fill = GridBagConstraints.BOTH;
            gbc_shipWasteRequirementsPanel.gridx = 0;
            gbc_shipWasteRequirementsPanel.gridy = 4;
            FalForm1Panel.add(shipWasteRequirementsPanel, gbc_shipWasteRequirementsPanel);
            shipWasteRequirementsPanel.setLayout(new BorderLayout(0, 0));
            {
                JTextArea textArea = new JTextArea();
                textArea.setRows(5);
                JScrollPane sp = new JScrollPane(textArea);
                shipWasteRequirementsPanel.add(sp, BorderLayout.CENTER);
            }
        }

        this.pack();
    }

}

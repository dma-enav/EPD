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

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.VERTICAL;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import dk.dma.enav.model.fal.FALReport;
import dk.dma.epd.common.util.FALPDFGenerator;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.fal.FALManager;

/**
 * Main panel of the fal manager dialog
 */
public class FALManagerPanel extends JPanel implements ActionListener, ListSelectionListener, TableModelListener {

    private static final long serialVersionUID = 1L;

    private JButton staticDataBtn = new JButton("Static Ship Data");
    private JButton newBtn = new JButton("New Report");
    private JButton propertiesBtn = new JButton("View");
    private JButton copyBtn = new JButton("Copy");
    private JButton deleteBtn = new JButton("Delete");
    private JButton exportBtn = new JButton("Export");
    private JButton closeBtn = new JButton("Close");
    private JButton sendFAL = new JButton("Send FAL Report");

    private JButton[] buttons = { staticDataBtn, newBtn, propertiesBtn, copyBtn, deleteBtn, exportBtn, sendFAL, closeBtn };

    private JTable falTable = new JTable();
    private JScrollPane falScrollPane = new JScrollPane(falTable);
    private FALTableModel falTableModel;

    private FALManager falManager;
    private FALManagerDialog falManagerDialog;

    /**
     * Constructor
     * 
     * @param FALManagerDialog
     */
    public FALManagerPanel(FALManagerDialog falManagerDialog) {

        falManager = EPDShip.getInstance().getFalManager();

        this.falManagerDialog = falManagerDialog;

        falTableModel = new FALTableModel();
        falTableModel.addTableModelListener(this);
        falTable.setModel(falTableModel);
        falTable.getColumnModel().getColumn(0).setPreferredWidth(175);
        falTable.getColumnModel().getColumn(1).setPreferredWidth(175);
        falTable.getColumnModel().getColumn(2).setPreferredWidth(50);

        falTable.setShowHorizontalLines(false);
        falTable.setFillsViewportHeight(true);
        falTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        falTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    properties();
                }
            }
        });

        falScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        falScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        falTable.getSelectionModel().addListSelectionListener(this);

        for (JButton btn : buttons) {
            btn.addActionListener(this);
        }

        initGUI();

        updateTable();
        updateButtons();
    }

    /**
     * Builds the GUI
     */
    private void initGUI() {

        setLayout(new GridBagLayout());

        JPanel btnPanel = new JPanel(new GridBagLayout());

        add(falScrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, new Insets(5, 5, 5, 5), 0, 0));
        add(btnPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, NORTH, VERTICAL, new Insets(5, 5, 5, 5), 0, 0));

        // All all buttons bar the close button
        for (int x = 0; x < buttons.length - 1; x++) {
            btnPanel.add(buttons[x], new GridBagConstraints(0, x, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));
        }

        // Add a filler
        btnPanel.add(new JLabel(" "), new GridBagConstraints(0, buttons.length - 1, 1, 1, 0.0, 1.0, NORTH, VERTICAL, new Insets(0,
                0, 0, 0), 0, 0));

        // Add the close button
        closeBtn.setMinimumSize(new Dimension(100, 20));
        btnPanel.add(closeBtn, new GridBagConstraints(0, buttons.length, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, new Insets(0, 0, 0, 0),
                0, 0));

    }

    /**
     * returns a reference to the close button
     * 
     * @return the close button
     */
    public JButton getCloseButton() {
        return closeBtn;
    }

    /**
     * Updates the buttons states depending on the current selection
     */
    private void updateButtons() {
        boolean falReportSelected = falTable.getSelectedRow() >= 0;
        boolean singleSelected = falTable.getSelectedRows().length == 1;
        boolean activeSelected = false;

        propertiesBtn.setEnabled(singleSelected);

        copyBtn.setEnabled(singleSelected);
        deleteBtn.setEnabled(falReportSelected && !activeSelected);

        exportBtn.setEnabled(singleSelected);
        sendFAL.setEnabled(singleSelected);
    }

    /**
     * Called when the underlying set of fal reports has been changed
     */
    public void updateTable() {

        // Update falTable
        falTableModel.fireTableDataChanged();
    }

    private void close() {
        falManagerDialog.dispose();
    }

    private void copy() {
        if (falTable.getSelectedRow() >= 0) {
            FALReport newFal = new FALReport(falManager.getFalReports().get(falTable.getSelectedRow()));

            falManager.addFALReport(newFal);

            updateTable();
        }
    }

    private void properties() {
        int i = falTable.getSelectedRow();
        if (i >= 0) {
            FALReportingDialog dialog = new FALReportingDialog(falManager, falManager.getFalReports().get(i).getId(), true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }
    }

    private void delete() {
        int row = falTable.getSelectedRow();

        row = Math.min(row, falTable.getRowCount() - 1);
        if (row == -1) {
            falTable.clearSelection();
        } else {
            falManager.removeFALReport(row);
            falTable.getSelectionModel().setSelectionInterval(row, row);
            updateTable();
            updateButtons();
        }
    }

    private void exportToFile() {

        // Generate PDF based on report
        exportToFile(falTable.getSelectedRow());
    }

    private void exportToFile(int falId) {
        if (falId < 0) {
            return;
        }

        // Select the Path
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(this);

        // User selected a spot
        if (returnVal == 0) {
            File file = fc.getSelectedFile();
            FALPDFGenerator falPDFGenerator = new FALPDFGenerator();
            falPDFGenerator.generateFal1Form(EPDShip.getInstance().getFalManager().getFalReports().get(falId).getFalform1(),
                    file.getAbsolutePath() + ".pdf");
        } else {
            // Do nothing
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeBtn) {
            close();
        } else if (e.getSource() == propertiesBtn) {
            properties();
        } else if (e.getSource() == copyBtn) {
            copy();
        } else if (e.getSource() == deleteBtn) {
            delete();
        } else if (e.getSource() == exportBtn) {
            exportToFile();
        } else if (e.getSource() == newBtn) {
            FALReportingDialog dialog = new FALReportingDialog(falManager, -1, true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        } else if (e.getSource() == staticDataBtn) {
            FALStaticInformationDialog dialog = new FALStaticInformationDialog(falManager);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        } else if (e.getSource() == sendFAL) {
            TransmitFALReportDialog dialog = new TransmitFALReportDialog(falManagerDialog);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setSelectedFALReport(EPDShip.getInstance().getFalManager().getFalReports().get(falTable.getSelectedRow()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        updateButtons();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tableChanged(TableModelEvent e) {

    }
}

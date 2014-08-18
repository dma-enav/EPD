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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.ship.EPDShip;

/**
 * Main panel of the route manager dialog
 */
public class FALManagerPanel extends JPanel implements ActionListener, ListSelectionListener, TableModelListener {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(FALManagerPanel.class);

    private JButton staticDataBtn = new JButton("Static Ship Data");
    private JButton newBtn = new JButton("New Report");
    private JButton propertiesBtn = new JButton("View");
    private JButton copyBtn = new JButton("Copy");
    private JButton deleteBtn = new JButton("Delete");
    private JButton exportBtn = new JButton("Export");
    private JButton closeBtn = new JButton("Close");

    private JButton[] buttons = { staticDataBtn, newBtn, propertiesBtn, copyBtn, deleteBtn, exportBtn, closeBtn };

    private JTable routeTable = new JTable();
    private JScrollPane routeScrollPane = new JScrollPane(routeTable);
    private FALTableModel routesTableModel;

    // private RouteManager routeManager;
    private FALManagerDialog falManagerDialog;

    private volatile File lastPath;

    /**
     * Constructor
     * 
     * @param routeManagerDialog
     */
    public FALManagerPanel(FALManagerDialog falManagerDialog) {

        this.falManagerDialog = falManagerDialog;

        routesTableModel = new FALTableModel();
        routesTableModel.addTableModelListener(this);
        routeTable.setModel(routesTableModel);
        routeTable.getColumnModel().getColumn(0).setPreferredWidth(175);
        routeTable.getColumnModel().getColumn(1).setPreferredWidth(175);
        routeTable.getColumnModel().getColumn(2).setPreferredWidth(50);

        routeTable.setShowHorizontalLines(false);
        routeTable.setFillsViewportHeight(true);
        routeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        routeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    properties();
                }
            }
        });

        routeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        routeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        routeTable.getSelectionModel().addListSelectionListener(this);

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

        add(routeScrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, new Insets(5, 5, 5, 5), 0, 0));
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

        // int selectRow = routeManager.getActiveRouteIndex();
        // if (selectRow < 0 && routeManager.getRouteCount() > 0) {
        // selectRow = 0;
        // }
        // if (selectRow >= 0) {
        // routeTable.getSelectionModel().setSelectionInterval(selectRow, selectRow);
        // }
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
        boolean routeSelected = routeTable.getSelectedRow() >= 0;
        boolean singleSelected = routeTable.getSelectedRows().length == 1;
        boolean activeSelected = false;
        // for (int row : routeTable.getSelectedRows()) {
        // if (routeManager.isActiveRoute(row)) {
        // activeSelected = true;
        // }
        // }

        propertiesBtn.setEnabled(singleSelected);

        copyBtn.setEnabled(singleSelected);
        deleteBtn.setEnabled(routeSelected && !activeSelected);

        exportBtn.setEnabled(singleSelected);
    }

    /**
     * Called when the underlying set of routes has been changed
     */
    public void updateTable() {
        // Record the old selection
        // Set<Route> selection = new HashSet<>(getSelectedRoutes());

        // Update routeTable
        routesTableModel.fireTableDataChanged();

        // Restore old selection
        // for (int row = 0; row < routeTable.getRowCount(); row++) {
        // Route route = routeManager.getRoute(row);
        // if (selection.contains(route)) {
        // routeTable.addRowSelectionInterval(row, row);
        // }
        // }
    }

    private void close() {
        falManagerDialog.dispose();
    }

    private void copy() {
        if (routeTable.getSelectedRow() >= 0) {
            // routeManager.routeCopy(routeTable.getSelectedRow());
            updateTable();
        }
    }

    private void properties() {
        int i = routeTable.getSelectedRow();
        if (i >= 0) {
            // RoutePropertiesDialogCommon routePropertiesDialog = new RoutePropertiesDialogCommon(falManagerDialog, EPDShip
            // .getInstance().getMainFrame().getChartPanel(), i);
            // routePropertiesDialog.setVisible(true);
        }
    }

    private void delete() {
        int row = routeTable.getSelectedRow();
        // for (Route route : getSelectedRoutes()) {
        // routeManager.removeRoute(route);
        // }
        row = Math.min(row, routeTable.getRowCount() - 1);
        if (row == -1) {
            routeTable.clearSelection();
        } else {
            routeTable.getSelectionModel().setSelectionInterval(row, row);
        }
    }

    private void exportToFile() {
        exportToFile(routeTable.getSelectedRow());
    }

    private void exportToFile(int routeId) {
        if (routeId < 0) {
            return;
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
            FALReportingDialog dialog = new FALReportingDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        } else if (e.getSource() == staticDataBtn) {
            FALStaticInformation dialog = new FALStaticInformation();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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

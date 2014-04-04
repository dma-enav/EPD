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
package dk.dma.epd.ship.gui.route;

import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
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

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.route.RouteMetocDialog;
import dk.dma.epd.common.prototype.gui.route.RoutePropertiesDialogCommon;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLoadException;
import dk.dma.epd.common.prototype.model.route.RouteLoader;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.route.RouteManager;

/**
 * Main panel of the route manager dialog
 */
public class RouteManagerPanel extends JPanel implements ActionListener,
    ListSelectionListener, TableModelListener {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
            .getLogger(RouteManagerPanel.class);

    private JButton propertiesBtn = new JButton("Properties");
    private JButton activateBtn = new JButton("Activate");
    private JButton zoomToBtn = new JButton("Zoom to");
    private JButton copyBtn = new JButton("Copy");
    private JButton reverseCopyBtn = new JButton("Reverse copy");
    private JButton deleteBtn = new JButton("Delete");
    private JButton exportBtn = new JButton("Export");
    private JButton exportAllBtn = new JButton("Export All");
    private JButton importBtn = new JButton("Import");
    private JButton metocBtn = new JButton("METOC");
    private JButton closeBtn = new JButton("Close");
    
    private JButton[] buttons = {
            propertiesBtn, activateBtn, zoomToBtn, copyBtn, reverseCopyBtn,
            deleteBtn, exportBtn, exportAllBtn, importBtn, metocBtn, closeBtn };

    private JTable routeTable = new JTable();
    private JScrollPane routeScrollPane = new JScrollPane(routeTable);
    private RoutesTableModel routesTableModel;
    
    private RouteManager routeManager;
    private RouteManagerDialog routeManagerDialog;
    
    private volatile File lastPath;

    /**
     * Constructor
     * 
     * @param routeManagerDialog
     */
    public RouteManagerPanel(RouteManagerDialog routeManagerDialog){
        this.routeManager = EPDShip.getInstance().getRouteManager();
        this.routeManagerDialog = routeManagerDialog;
        
        routesTableModel = new RoutesTableModel(routeManager);
        routesTableModel.addTableModelListener(this);
        routeTable.setModel(routesTableModel);
        routeTable.getColumnModel().getColumn(0).setPreferredWidth(175);
        routeTable.getColumnModel().getColumn(1).setPreferredWidth(175);
        routeTable.getColumnModel().getColumn(2).setPreferredWidth(50);

        routeTable.setShowHorizontalLines(false);
        routeTable.setFillsViewportHeight(true);
        routeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        routeTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    properties();
                }
            }});
        
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
        btnPanel.add(new JLabel(" "), new GridBagConstraints(0, buttons.length - 1, 1, 1, 0.0, 1.0, NORTH, VERTICAL, new Insets(0, 0, 0, 0), 0, 0));                    
        
        // Add the close button
        closeBtn.setMinimumSize(new Dimension(100, 20));
        btnPanel.add(closeBtn, new GridBagConstraints(0, buttons.length, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));                    
        
        int selectRow = routeManager.getActiveRouteIndex();
        if (selectRow < 0 && routeManager.getRouteCount() > 0) {
            selectRow = 0;
        }
        if (selectRow >= 0) {
            routeTable.getSelectionModel().setSelectionInterval(selectRow, selectRow);
        }
    }
    
    /**
     * returns a reference to the close button
     * @return the close button
     */
    public JButton getCloseButton() {
        return closeBtn;
    }
    
    /**
     * Returns the list of selected routes
     * @return the list of selected routes
     */
    public List<Route> getSelectedRoutes() {
        List<Route> result = new ArrayList<>();
        for (int row : routeTable.getSelectedRows()) {
            if (row >= 0 && row < routeManager.getRouteCount()) {
                result.add(routeManager.getRoute(row));
            }
        }
        return result;
    }
    
    /**
     * Updates the buttons states depending on the current selection
     */
    private void updateButtons() {
        boolean routeSelected = routeTable.getSelectedRow() >= 0;
        boolean singleSelected = routeTable.getSelectedRows().length == 1;
        boolean activeSelected = false;
        for (int row : routeTable.getSelectedRows()) {
            if (routeManager.isActiveRoute(row)) {
                activeSelected = true;
            }
        }

        activateBtn.setText(activeSelected ? "Deactivate" : "Activate");
        if (routeManager.isRouteActive()) {
            activateBtn.setEnabled(activeSelected && singleSelected);
        } else {
            activateBtn.setEnabled(singleSelected);
        }

        propertiesBtn.setEnabled(singleSelected);
        zoomToBtn.setEnabled(singleSelected);
        reverseCopyBtn.setEnabled(singleSelected);
        copyBtn.setEnabled(singleSelected);
        deleteBtn.setEnabled(routeSelected && !activeSelected);
        metocBtn.setEnabled(singleSelected);
        exportBtn.setEnabled(singleSelected);
    }

    /**
     * Called when the underlying set of routes has been changed
     */
    public void updateTable() {
        // Record the old selection
        Set<Route> selection = new HashSet<>(getSelectedRoutes());
        
        // Update routeTable
        routesTableModel.fireTableDataChanged();

        // Restore old selection
        for (int row = 0; row < routeTable.getRowCount(); row++) {
            Route route = routeManager.getRoute(row);
            if (selection.contains(route)) {
                routeTable.addRowSelectionInterval(row, row);
            }
        }
    }

    private void close() {
        routeManagerDialog.dispose();
    }

    private void activateRoute() {
        LOG.debug("Activate route");
        if (routeTable.getSelectedRow() >= 0) {
            if (routeManager.isRouteActive()) {
                routeManager.deactivateRoute();
            } else {
                routeManager.activateRoute(routeTable.getSelectedRow());
            }

            updateTable();
        }
    }

    private void zoomTo() {
        if (getSelectedRoutes().size() == 1 && EPD.getInstance().getMainFrame().getActiveChartPanel() != null) {
            EPD.getInstance().getMainFrame().getActiveChartPanel()
                .zoomToWaypoints(getSelectedRoutes().get(0).getWaypoints());
        }
    }

    private void copy() {
        if (routeTable.getSelectedRow() >= 0) {
            routeManager.routeCopy(routeTable.getSelectedRow());
            updateTable();
        }
    }

    private void reverseCopy() {
        if (routeTable.getSelectedRow() >= 0) {
            routeManager.routeReverse(routeTable.getSelectedRow());
            updateTable();
        }
    }

    private void properties() {
        int i = routeTable.getSelectedRow();
        if (i >= 0) {
            RoutePropertiesDialogCommon routePropertiesDialog = new RoutePropertiesDialogCommon(
                    routeManagerDialog, 
                    EPDShip.getInstance().getMainFrame().getChartPanel(),
                    i);
            routePropertiesDialog.setVisible(true);
        }
    }

    private void metocProperties() {
        int i = routeTable.getSelectedRow();
        if (i >= 0) {
            RouteMetocDialog routeMetocDialog = new RouteMetocDialog(routeManagerDialog,
                    routeManager, i);
            routeMetocDialog.setVisible(true);
            routeManager
                    .notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
        }
    }

    private void delete() {
        int row = routeTable.getSelectedRow();
        for (Route route : getSelectedRoutes()) {
            routeManager.removeRoute(route);
        }
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

        Route route = routeManager.getRoute(routeId);
        
        String path = lastPath != null ? lastPath.getAbsolutePath() : EPDShip.getInstance().getHomePath().resolve("routes")
                .toString();        
        JFileChooser fc = new JFileChooser(path);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);

        fc.addChoosableFileFilter(new FileNameExtensionFilter(
                "Simple route text format", "txt", "TXT"));
        fc.setAcceptAllFileFilterUsed(true);
        File f = new File(route.getName() + ".txt");
        fc.setSelectedFile(f);

        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fc.getSelectedFile();

        if (!fc.getSelectedFile().toString().contains(".txt")) {
            file = new File(fc.getSelectedFile().getPath() + ".txt");
        }

        if (file.exists()) {
            if (JOptionPane.showConfirmDialog(this, "File exists. Overwrite?",
                    "Overwrite?", JOptionPane.YES_NO_OPTION) != 0) {
                exportToFile(routeId);
                return;
            }
        }

        if (!RouteLoader.saveSimple(route, file)) {
            JOptionPane.showMessageDialog(EPDShip.getInstance().getMainFrame(),
                    "Route save error", "Route not saved",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void importFromFile() {
        // Get filename from dialog
        String path = lastPath != null ? lastPath.getAbsolutePath() : EPDShip.getInstance().getHomePath().resolve("routes")
                .toString();        
        JFileChooser fc = new JFileChooser(path);

        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(true);
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
                "Simple route text format", "txt", "TXT"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
                "ECDIS900 V3 route", "rou", "ROU"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
                "Navisailor 3000 route", "rt3", "RT3"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
                "Google KML", "kml", "KML"));
        fc.setAcceptAllFileFilterUsed(true);

        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        for (File file : fc.getSelectedFiles()) {
            try {
                routeManager.loadFromFile(file);
            } catch (RouteLoadException e) {
                JOptionPane.showMessageDialog(this, e.getMessage() + ": "
                        + file.getName(), "Route load error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            lastPath = file;
        }

        updateTable();
        routeTable.getSelectionModel().setSelectionInterval(routeTable.getRowCount() - 1,
                routeTable.getRowCount() - 1);
    }

    private void exportAllToFile() {
        for (int i = 0; i < routeTable.getRowCount(); i++) {
            exportToFile(i);
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
        } else if (e.getSource() == activateBtn) {
            activateRoute();
        } else if (e.getSource() == zoomToBtn) {
            zoomTo();
        } else if (e.getSource() == copyBtn) {
            copy();
        } else if (e.getSource() == reverseCopyBtn) {
            reverseCopy();
        } else if (e.getSource() == deleteBtn) {
            delete();
        } else if (e.getSource() == metocBtn) {
            metocProperties();
        } else if (e.getSource() == exportBtn) {
            exportToFile();
        } else if (e.getSource() == exportAllBtn) {
            exportAllToFile();
        } else if (e.getSource() == importBtn) {
            importFromFile();
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
        if (e.getColumn() == 2) {
            // Visibility has changed
            routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
        }
    }
}

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.gui.route.RouteMetocDialog;
import dk.dma.epd.common.prototype.gui.route.RoutePropertiesDialogCommon;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLoadException;
import dk.dma.epd.common.prototype.model.route.RouteLoader;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.route.RouteManager;

public class RouteManagerPanel extends JPanel implements ActionListener,
ListSelectionListener, TableModelListener, MouseListener {

    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
            .getLogger(RouteManagerPanel.class);

    private JButton propertiesBtn;
    private JButton zoomToBtn;
    private JButton reverseCopyBtn;
    private JButton deleteBtn;
    private JButton exportBtn;
    private JButton importBtn;
    private JButton closeBtn;
    private JButton activateBtn;

    private JScrollPane routeScrollPane;
    private JTable routeTable;
    private RoutesTableModel routesTableModel;
    private ListSelectionModel routeSelectionModel;

    private JButton exportAllBtn;

    private JButton metocBtn;

    private JButton copyBtn;
    
    private RouteManager routeManager;
    private RouteManagerDialog routeManagerDialog;
    
    public RouteManagerPanel(RouteManager routeManager, RouteManagerDialog routeManagerDialog){
        this.routeManager = routeManager;
        this.routeManagerDialog = routeManagerDialog;
        
        propertiesBtn = new JButton("Properties");
        propertiesBtn.addActionListener(this);
        activateBtn = new JButton("Activate");
        activateBtn.addActionListener(this);
        zoomToBtn = new JButton("Zoom to");
        zoomToBtn.addActionListener(this);
        reverseCopyBtn = new JButton("Reverse copy");
        reverseCopyBtn.addActionListener(this);
        deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(this);
        exportBtn = new JButton("Export");
        exportBtn.addActionListener(this);
        exportAllBtn = new JButton("Export All");
        exportAllBtn.addActionListener(this);
        importBtn = new JButton("Import");
        importBtn.addActionListener(this);
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        metocBtn = new JButton("METOC");
        metocBtn.addActionListener(this);
        copyBtn = new JButton("Copy");
        copyBtn.addActionListener(this);

        routeTable = new JTable();
        routesTableModel = new RoutesTableModel(routeManager);
        routesTableModel.addTableModelListener(this);
        routeTable.setShowHorizontalLines(false);
        routeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        routeScrollPane = new JScrollPane(routeTable);
        routeScrollPane
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        routeScrollPane
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        routeTable.setFillsViewportHeight(true);
        // TODO: Comment this line when using WindowBuilder
        routeTable.setModel(routesTableModel);
        for (int i = 0; i < 3; i++) {
            if (i == 2) {
                routeTable.getColumnModel().getColumn(i).setPreferredWidth(50);
            } else {
                routeTable.getColumnModel().getColumn(i).setPreferredWidth(175);
            }
        }
        routeSelectionModel = routeTable.getSelectionModel();
        routeSelectionModel.addListSelectionListener(this);
        routeTable.setSelectionModel(routeSelectionModel);
        routeTable.addMouseListener(this);
        
        
        initGUI();
        

        updateTable();
        updateButtons();
        
        
        
    }
    
    
    
    private void initGUI(){
        GroupLayout gl_routePanel = new GroupLayout(this);
        gl_routePanel
                .setHorizontalGroup(gl_routePanel
                        .createParallelGroup(Alignment.TRAILING)
                        .addGroup(
                                gl_routePanel
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(routeScrollPane,
                                                GroupLayout.DEFAULT_SIZE, 427,
                                                Short.MAX_VALUE)
                                        .addPreferredGap(
                                                ComponentPlacement.RELATED)
                                        .addGroup(
                                                gl_routePanel
                                                        .createParallelGroup(
                                                                Alignment.LEADING)
                                                        .addGroup(
                                                                gl_routePanel
                                                                        .createParallelGroup(
                                                                                Alignment.LEADING,
                                                                                false)
                                                                        .addComponent(
                                                                                closeBtn,
                                                                                Alignment.TRAILING,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                zoomToBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                activateBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                propertiesBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                131,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                copyBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE))
                                                        .addGroup(
                                                                gl_routePanel
                                                                        .createParallelGroup(
                                                                                Alignment.LEADING,
                                                                                false)
                                                                        .addComponent(
                                                                                exportBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                deleteBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                reverseCopyBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                metocBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                131,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                exportAllBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                131,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                importBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                131,
                                                                                Short.MAX_VALUE)))
                                        .addContainerGap()));
        gl_routePanel
                .setVerticalGroup(gl_routePanel
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                gl_routePanel
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                gl_routePanel
                                                        .createParallelGroup(
                                                                Alignment.LEADING)
                                                        .addGroup(
                                                                gl_routePanel
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                propertiesBtn)
                                                                        .addPreferredGap(
                                                                                ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                activateBtn)
                                                                        .addPreferredGap(
                                                                                ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                zoomToBtn)
                                                                        .addPreferredGap(
                                                                                ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                copyBtn)
                                                                        .addPreferredGap(
                                                                                ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                reverseCopyBtn)
                                                                        .addPreferredGap(
                                                                                ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                deleteBtn)
                                                                        .addPreferredGap(
                                                                                ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                exportBtn)
                                                                        .addGap(7)
                                                                        .addComponent(
                                                                                metocBtn)
                                                                        .addPreferredGap(
                                                                                ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                exportAllBtn)
                                                                        .addPreferredGap(
                                                                                ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                importBtn))
                                                        .addComponent(
                                                                routeScrollPane,
                                                                Alignment.TRAILING,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                289,
                                                                Short.MAX_VALUE))
                                        .addGap(28).addComponent(closeBtn)
                                        .addContainerGap()));

        this.setLayout(gl_routePanel);

        
        
        
        
        
        int selectRow = routeManager.getActiveRouteIndex();
        if (selectRow < 0 && routeManager.getRouteCount() > 0) {
            selectRow = 0;
        }
        if (selectRow >= 0) {
            routeSelectionModel.setSelectionInterval(selectRow, selectRow);
        }
    }
    
    
    
    
    
    
    
    
    
    

    private void updateButtons() {
        boolean routeSelected = routeTable.getSelectedRow() >= 0;
        boolean activeSelected = routeManager.isActiveRoute(routeTable
                .getSelectedRow());

        // LOG.info("---------------------------------------");
        // LOG.info("routeSelected: " + routeSelected);
        // LOG.info("routeTable.getSelectedRow(): " +
        // routeTable.getSelectedRow());
        // LOG.info("activeSelected: " + activeSelected);
        // LOG.info("routeManager.isRouteActive(): " +
        // routeManager.isRouteActive());
        // LOG.info("activeRoute: " + routeManager.getActiveRouteIndex());
        // LOG.info("\n\n");

        activateBtn.setEnabled(routeSelected);
        activateBtn.setText(activeSelected ? "Deactivate" : "Activate");

        if (routeSelected) {
            if (routeManager.isRouteActive()) {
                activateBtn.setEnabled(activeSelected);
            } else {
                activateBtn.setEnabled(true);
            }
        }

        propertiesBtn.setEnabled(routeSelected);
        zoomToBtn.setEnabled(routeSelected);
        reverseCopyBtn.setEnabled(routeSelected);
        copyBtn.setEnabled(routeSelected);
        deleteBtn.setEnabled(routeSelected && !activeSelected);
        metocBtn.setEnabled(routeSelected);
        exportBtn.setEnabled(routeSelected);
    }

    public void updateTable() {
        int selectedRow = routeTable.getSelectedRow();
        // Update routeTable
        routesTableModel.fireTableDataChanged();
        // routeTable.doLayout();
        updateButtons();
        if (selectedRow >= 0 && selectedRow < routeTable.getRowCount()) {
            routeSelectionModel.setSelectionInterval(selectedRow, selectedRow);
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

        // TODO ChartPanel should implement a method that given a route does the
        // following
        // TODO disable auto follow
        // TODO find minx, miny and maxx, maxy
        // TODO center and scale map to include whole route
        //
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
                    routeManagerDialog, routeManager, i);
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
        if (routeTable.getSelectedRow() >= 0) {
            routeManager.removeRoute(routeTable.getSelectedRow());
            updateTable();
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

        JFileChooser fc = new JFileChooser(System.getProperty("user.dir")
                + "/routes/");
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
        JFileChooser fc = new JFileChooser(EPDShip.getInstance().getHomePath()
                .resolve("routes").toString());

        // private static final String aisViewFile =
        // EeINS.getHomePath().resolve(".aisview").toString();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(true);
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
                "Simple route text format", "txt", "TXT"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
                "ECDIS900 V3 route", "rou", "ROU"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
                "Navisailor 3000 route", "rt3", "RT3"));
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
        }

        updateTable();
        routeSelectionModel.setSelectionInterval(routeTable.getRowCount() - 1,
                routeTable.getRowCount() - 1);
    }

    private void exportAllToFile() {
        for (int i = 0; i < routeTable.getRowCount(); i++) {
            exportToFile(i);
        }
    }

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

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            properties();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // ListSelectionModel lsm = (ListSelectionModel) e.getSource();

        // int firstIndex = e.getFirstIndex();
        // int lastIndex = e.getLastIndex();
        // boolean isAdjusting = e.getValueIsAdjusting();
        // LOG.info("Event for indexes " + firstIndex + " - " + lastIndex +
        // "; isAdjusting is " + isAdjusting + "; selected indexes:");

        updateButtons();

    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getColumn() == 2) {
            // Visibility has changed
            routeManager
                    .notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }
}

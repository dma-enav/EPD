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

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
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

import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.service.SuggestedRoute;
import dk.dma.epd.ship.service.SuggestedRoute.SuggestedRouteStatus;

public class RouteExchangeManagerPanel extends JPanel implements
        ActionListener, ListSelectionListener, TableModelListener,
        MouseListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

//    private static final Logger LOG = LoggerFactory
//            .getLogger(RouteExchangeManagerPanel.class);

    private JButton detailsBtn;
    private JButton zoomToBtn;
    private JButton rejectBtn;
    private JButton notedBtn;

    private JButton deleteBtn;
    private JButton closeBtn;
    private JButton acceptBtn;

    private JScrollPane routeScrollPane;
    private JTable routeTable;

    private RoutesExchangeTableModel routesTableModel;
    private ListSelectionModel routeSelectionModel;

    private RouteManager routeManager;
    private RouteManagerDialog routeManagerDialog;

    public RouteExchangeManagerPanel(RouteManager routeManager,
            RouteManagerDialog routeManagerDialog) {
        this.routeManager = routeManager;
        this.routeManagerDialog = routeManagerDialog;

        detailsBtn = new JButton("Details");
        detailsBtn.addActionListener(this);

        zoomToBtn = new JButton("Zoom to");
        zoomToBtn.addActionListener(this);

        rejectBtn = new JButton("Reject");
        rejectBtn.addActionListener(this);

        notedBtn = new JButton("Noted");
        notedBtn.addActionListener(this);

        deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(this);

        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);

        acceptBtn = new JButton("Accept");
        acceptBtn.addActionListener(this);

        routeTable = new JTable();
        routesTableModel = new RoutesExchangeTableModel(routeManager);
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

    private void initGUI() {
        GroupLayout gl_routePanel = new GroupLayout(this);
        gl_routePanel
                .setHorizontalGroup(gl_routePanel
                        .createParallelGroup(Alignment.TRAILING)
                        .addGroup(
                                gl_routePanel
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(routeScrollPane,
                                                GroupLayout.DEFAULT_SIZE, 293,
                                                Short.MAX_VALUE)
                                        .addPreferredGap(
                                                ComponentPlacement.RELATED)
                                        .addGroup(
                                                gl_routePanel
                                                        .createParallelGroup(
                                                                Alignment.LEADING,
                                                                false)
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
                                                                                detailsBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                131,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                acceptBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE))
                                                        .addGroup(
                                                                gl_routePanel
                                                                        .createParallelGroup(
                                                                                Alignment.LEADING,
                                                                                false)
                                                                        .addComponent(
                                                                                notedBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                rejectBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                deleteBtn,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                131,
                                                                                Short.MAX_VALUE))
                                                        .addComponent(
                                                                zoomToBtn,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                131,
                                                                GroupLayout.PREFERRED_SIZE))
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
                                                                                detailsBtn)
                                                                        .addPreferredGap(
                                                                                ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                zoomToBtn)
                                                                        .addGap(35)
                                                                        .addComponent(
                                                                                acceptBtn)
                                                                        .addPreferredGap(
                                                                                ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                rejectBtn)
                                                                        .addPreferredGap(
                                                                                ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                notedBtn)
                                                                        .addGap(94)
                                                                        .addComponent(
                                                                                deleteBtn))
                                                        .addComponent(
                                                                routeScrollPane,
                                                                Alignment.TRAILING,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                285,
                                                                Short.MAX_VALUE))
                                        .addGap(28).addComponent(closeBtn)
                                        .addContainerGap()));

        this.setLayout(gl_routePanel);

        int selectRow = -1;
        if (selectRow < 0 && routeManager.getSuggestedRoutes().size() > 0) {
            selectRow = 0;
        }
        if (selectRow >= 0) {
            routeSelectionModel.setSelectionInterval(selectRow, selectRow);
        }

    }

    private void updateButtons() {
        
        if(routeTable.getSelectedRow() >= 0){
            SuggestedRoute route = routeManager.getSuggestedRoutes().get(
                    routeTable.getSelectedRow());
            SuggestedRouteStatus status = route.getStatus();

            switch (status) {
            case PENDING:
                detailsBtn.setEnabled(true);
                zoomToBtn.setEnabled(true);
                acceptBtn.setEnabled(true);
                rejectBtn.setEnabled(true);
                notedBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
                break;
            case NOTED:
                detailsBtn.setEnabled(true);
                zoomToBtn.setEnabled(true);
                acceptBtn.setEnabled(false);
                rejectBtn.setEnabled(false);
                notedBtn.setEnabled(false);
                deleteBtn.setEnabled(true);
                break;
            case IGNORED:
                detailsBtn.setEnabled(true);
                zoomToBtn.setEnabled(true);
                acceptBtn.setEnabled(true);
                rejectBtn.setEnabled(true);
                notedBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
                break;
                
            case REJECTED:
                detailsBtn.setEnabled(true);
                zoomToBtn.setEnabled(true);
                acceptBtn.setEnabled(false);
                rejectBtn.setEnabled(false);
                notedBtn.setEnabled(false);
                deleteBtn.setEnabled(true);
                break;
                
            default:
                break;
            }
        }else{
            detailsBtn.setEnabled(false);
            zoomToBtn.setEnabled(false);
            acceptBtn.setEnabled(false);
            rejectBtn.setEnabled(false);
            notedBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }
        
   

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

    private void zoomTo() {

        // TODO ChartPanel should implement a method that given a route does the
        // following
        // TODO disable auto follow
        // TODO find minx, miny and maxx, maxy
        // TODO center and scale map to include whole route
        //
    }

    private void details() {
        if (routeTable.getSelectedRow() >= 0) {
            routeManager.showSuggestionDialog(routeTable.getSelectedRow());
        }
    }

    private void handleReply(SuggestedRouteStatus status) {
        if (routeTable.getSelectedRow() >= 0) {
            routeManager.routeSuggestionReply(routeManager.getSuggestedRoutes()
                    .get(routeTable.getSelectedRow()), status, "No message");
//            updateTable();
//            routeManager.notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
            routeManager.getRouteSuggestionDialog().setVisible(false);
        }
    }

    private void delete() {
        if (routeTable.getSelectedRow() >= 0) {
            routeManager.getSuggestedRoutes().remove(
                    routeTable.getSelectedRow());
            updateTable();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeBtn) {
            close();
        } else if (e.getSource() == detailsBtn) {
            details();
        } else if (e.getSource() == zoomToBtn) {
            zoomTo();
        } else if (e.getSource() == acceptBtn) {
            handleReply(SuggestedRouteStatus.ACCEPTED);
        } else if (e.getSource() == rejectBtn) {
            handleReply(SuggestedRouteStatus.REJECTED);
        } else if (e.getSource() == notedBtn) {
            handleReply(SuggestedRouteStatus.NOTED);
        } else if (e.getSource() == deleteBtn) {
            delete();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
         updateButtons();
        }

        if (e.getClickCount() == 2) {
            details();
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

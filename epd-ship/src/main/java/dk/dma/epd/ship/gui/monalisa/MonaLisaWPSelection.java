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
package dk.dma.epd.ship.gui.monalisa;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import dk.dma.epd.ship.route.RouteManager;

public class MonaLisaWPSelection extends JDialog implements ActionListener,
        ListSelectionListener, TableModelListener, MouseListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTable routeTable;
    private MonaLisaSelectionTableModel routesTableModel;
    private ListSelectionModel routeSelectionModel;
    List<Boolean> selectedWp;
    int routeid;
    RouteManager routeManager;
    JButton okButton;
    JButton cancelButton;
    MonaLisaOptionsDialog parent;

    public MonaLisaWPSelection(MonaLisaOptionsDialog parent, RouteManager routeManager, List<Boolean> selectedWp, int routeid) {
        super(parent, "Waypoint Selection", true);
        
        this.parent = parent;
        this.routeManager = routeManager;
        this.selectedWp = selectedWp;
        this.routeid = routeid;
        
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
//        setResizable(false);
        
//        setBounds(100, 100, 450, 300);
        setSize(450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setLayout(new FlowLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        routeTable = new JTable();
        routesTableModel = new MonaLisaSelectionTableModel(routeManager, selectedWp, routeid);
        routesTableModel.addTableModelListener(this);
        routeTable.setShowHorizontalLines(false);
        routeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        routeSelectionModel = routeTable.getSelectionModel();
        routeSelectionModel.addListSelectionListener(this);
        routeTable.setSelectionModel(routeSelectionModel);
        routeTable.addMouseListener(this);

        getContentPane().add(contentPanel, BorderLayout.CENTER);
        {
            JScrollPane scrollPane = new JScrollPane(routeTable);
            scrollPane
                    .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane
                    .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            contentPanel.add(scrollPane);
            
            scrollPane.setPreferredSize(new Dimension(400, 200));
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                okButton = new JButton("OK");
                okButton.addActionListener(this);
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(this);
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        
        routeTable.setModel(routesTableModel);
        for (int i = 0; i < 1; i++) {
            if (i == 1) {
                routeTable.getColumnModel().getColumn(i).setPreferredWidth(10);
            } else {
                routeTable.getColumnModel().getColumn(i).setPreferredWidth(290);
            }
        }
        
        updateTable();
    }

    
    public void updateTable() {
        int selectedRow = routeTable.getSelectedRow();
        // Update routeTable
        routesTableModel.fireTableDataChanged();
        // routeTable.doLayout();
        if (selectedRow >= 0 && selectedRow < routeTable.getRowCount()) {
            routeSelectionModel.setSelectionInterval(selectedRow, selectedRow);
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tableChanged(TableModelEvent arg0) {
        if (arg0.getColumn() == 1) {
            System.out.println("Changed inclusion");
//            selectedWp.
            // Visibility has changed
//            routeManager
//                    .notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
        }
        
    }

    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource() == cancelButton) {
            parent.resetSelected();
            this.dispose();
        }
        
        if (arg0.getSource() == okButton) {
            parent.updateSelected();
            this.dispose();
            
//            System.out.println("Time to update?");
//            for (int i = 0; i < selectedWp.size(); i++) {
//                System.out.println(i + " : " + selectedWp.get(i));
//            }
//            parent.printSelected();
        }
        
    }

}

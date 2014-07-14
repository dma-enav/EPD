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
package dk.dma.epd.shore.gui.voct.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import dk.dma.epd.common.prototype.gui.voct.SearchPatternsPanelCommon;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.voct.SearchPatternDialog;
import dk.dma.epd.shore.gui.voct.panels.SRUSearchRouteTableModel.SRUSearchPAtternButtonHandler;
import dk.dma.epd.shore.voct.SRUUpdateEvent;
import dk.dma.epd.shore.voct.SRUUpdateListener;

public class SearchPatternsPanel extends SearchPatternsPanelCommon implements ActionListener, ListSelectionListener,
        TableModelListener, MouseListener, SRUSearchPAtternButtonHandler, SRUUpdateListener {

    private static final long serialVersionUID = 1L;

    private JScrollPane sruScrollPane;
    private JTable sruTable;
    private SRUSearchRouteTableModel sruTableModel;
    private ListSelectionModel sruSelectionModel;

    private SearchPatternDialog searchPatternDialog = new SearchPatternDialog();

    public SearchPatternsPanel() {

        EPDShore.getInstance().getSruManager().addListener(this);

        this.setBorder(new TitledBorder(null, "Search Patterns", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        DefaultTableModel model = new DefaultTableModel(30, 3);

        sruTable = new JTable(model) {
            private static final long serialVersionUID = 1L;

            public Component prepareRenderer(TableCellRenderer renderer, int Index_row, int Index_col) {
                Component comp = super.prepareRenderer(renderer, Index_row, Index_col);
                if (Index_row % 2 == 0) {
                    comp.setBackground(new Color(49, 49, 49));
                } else {
                    comp.setBackground(new Color(65, 65, 65));
                }

                if (isCellSelected(Index_row, Index_col)) {
                    comp.setForeground(Color.white);
                    comp.setBackground(new Color(85, 85, 85));
                }

                return comp;
            }
        };

        TableButtonRenderer renderer = new TableButtonRenderer();
        sruTable.setDefaultEditor(JButton.class, renderer);
        sruTable.setDefaultRenderer(JButton.class, renderer);

        // routeTable.setTableHeader(null);

        sruTable.setBorder(new EmptyBorder(0, 0, 0, 0));
        // routeTable.setIntercellSpacing(new Dimension(0, 0));
        sruTable.setBackground(new Color(49, 49, 49));
        sruTable.setShowVerticalLines(false);
        sruTable.setShowHorizontalLines(false);
        sruTable.setShowGrid(false);
        sruTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        sruTable.setForeground(Color.white);
        sruTable.setSelectionForeground(Color.gray);
        // routeTable.setRowHeight(20);
        sruTable.setFocusable(false);
        // routeTable.setAutoResizeMode(0);

        sruTableModel = new SRUSearchRouteTableModel(this, EPDShore.getInstance().getSruManager(), EPDShore
                .getInstance().getVoctManager());
        sruTableModel.addTableModelListener(this);

        sruTable.setShowHorizontalLines(false);
        sruTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sruScrollPane = new JScrollPane(sruTable);
        sruScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        sruScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sruTable.setFillsViewportHeight(true);

        sruScrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(30, 30, 30)));

        // TODO: Comment this line when using WindowBuilder
        sruTable.setModel(sruTableModel);
        // for (int i = 0; i < 2; i++) {
        //
        // if (i == 0){
        // sruTable.getColumnModel().getColumn(i).setPreferredWidth(25);
        // }
        // if (i == 1){
        // sruTable.getColumnModel().getColumn(i).setPreferredWidth(25);
        // }
        // if (i == 2){
        // sruTable.getColumnModel().getColumn(i).setPreferredWidth(25);
        // }
        //
        //
        // }
        sruSelectionModel = sruTable.getSelectionModel();
        sruSelectionModel.addListSelectionListener(this);
        sruTable.setSelectionModel(sruSelectionModel);
        sruTable.addMouseListener(this);

        GridBagLayout gbl_searchPatternsPanel = new GridBagLayout();
        gbl_searchPatternsPanel.columnWidths = new int[] { 153, 0 };
        gbl_searchPatternsPanel.rowHeights = new int[] { 75, 0 };
        gbl_searchPatternsPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_searchPatternsPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        setLayout(gbl_searchPatternsPanel);

        GridBagConstraints gbc_sruScrollPane = new GridBagConstraints();
        gbc_sruScrollPane.fill = GridBagConstraints.BOTH;
        gbc_sruScrollPane.gridx = 0;
        gbc_sruScrollPane.gridy = 0;
        add(sruScrollPane, gbc_sruScrollPane);

        searchPatternDialog.setVoctManager(EPDShore.getInstance().getVoctManager());
    }

    @Override
    public void resetValues() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {

            EPDShore.getInstance().getVoctManager().getSruManagerDialog().properties();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // updateButtons();
    }

    @Override
    public void tableChanged(TableModelEvent e) {

        if (e.getColumn() == 2) {
            // Visibility has changed
            // routeManager
            // .notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
        }
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

    // SRU Button clicked

    @Override
    public void buttonClicked(long sruID) {

        searchPatternDialog.resetValues(sruID);
        searchPatternDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        searchPatternDialog.setVisible(true);

        // searchPatternDialog
        // TODO Auto-generated method stub
        // System.out.println("Clicked SRU with name " + e.getName());
        // sruID
    }

    @Override
    public void sruUpdated(SRUUpdateEvent e, long mmsi) {
        sruTable.removeAll();
        sruTableModel.fireTableDataChanged();
        // sruTableModel.setValueAt(null, 0, 1);

//        sruTable.updateUI();
//        sruTable.repaint();
//        sruTable.validate();
//
//        this.repaint();
//        this.updateUI();
//        this.validate();
    }
}

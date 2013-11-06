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
package dk.dma.epd.shore.gui.voct;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import dk.dma.epd.common.prototype.gui.voct.ButtonsPanelCommon;
import dk.dma.epd.common.prototype.voct.VOCTUpdateEvent;
import dk.dma.epd.common.prototype.voct.VOCTUpdateListener;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.voct.VOCTManager;

public class SARPanelTracking extends JPanel implements VOCTUpdateListener,
        ActionListener, ListSelectionListener, TableModelListener,
        MouseListener {

    private static final long serialVersionUID = 1L;

    private JButton btnSendSar;

//    protected EffortAllocationWindow effortAllocationWindow = new EffortAllocationWindow();

    private VOCTManager voctManager;

    protected JButton btnStartSar;

    private JPanel statusPanel;
    private JPanel buttonPanel;

    protected ButtonsPanelCommon buttonsPanel;
    private JLabel lblSAR;

    private JScrollPane sruScrollPane;
    private JTable sruTable;
    private SARTrackingTableModel sruTableModel;
    
    private ListSelectionModel sruSelectionModel;
    private JButton btnManageSarTracking;
    
    private VOCTCommunicationWindow voctCommsWindow = new VOCTCommunicationWindow();

    public SARPanelTracking() {
        super();

        setVoctManager(EPDShore.getVoctManager());

        voctManager.addListener(this);

        initGUI();
    }

    /**
     * @param voctManager
     *            the voctManager to set
     */

    public void setVoctManager(VOCTManager voctManager) {
        this.voctManager = voctManager;

         voctCommsWindow.setVoctManager(this.voctManager);
        // searchPatternDialog.setVoctManager(voctManager);
    }

    private void initGUI() {

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 100, 0 };
        gridBagLayout.rowHeights = new int[] { 20, 16, 16, 16, 16, 16, 16, 0,
                0, 10 };
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 1.0, 1.0, 1.0, 1.0,
                1.0, 1.0, 1.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);
        lblSAR = new JLabel("Search And Rescue - Tracking");
        lblSAR.setHorizontalAlignment(SwingConstants.CENTER);
        lblSAR.setFont(new Font("Segoe UI", Font.BOLD, 14));
        GridBagConstraints gbc_lblSAR = new GridBagConstraints();
        gbc_lblSAR.anchor = GridBagConstraints.NORTH;
        gbc_lblSAR.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblSAR.insets = new Insets(0, 0, 5, 0);
        gbc_lblSAR.gridx = 0;
        gbc_lblSAR.gridy = 0;
        add(lblSAR, gbc_lblSAR);

        statusPanel = new JPanel();
        statusPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Vessels being tracked", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_statusPanel = new GridBagConstraints();
        gbc_statusPanel.insets = new Insets(0, 0, 5, 0);
        gbc_statusPanel.fill = GridBagConstraints.BOTH;
        gbc_statusPanel.gridx = 0;
        gbc_statusPanel.gridy = 1;
        add(statusPanel, gbc_statusPanel);
        GridBagLayout gbl_statusPanel = new GridBagLayout();
        gbl_statusPanel.columnWidths = new int[] { 32, 28, 0 };
        gbl_statusPanel.rowHeights = new int[] { 14, 0 };
        gbl_statusPanel.columnWeights = new double[] { };
        gbl_statusPanel.rowWeights = new double[] { };
        statusPanel.setLayout(gbl_statusPanel);

        buttonPanel = new JPanel();
        buttonPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
        gbc_buttonPanel.insets = new Insets(0, 0, 5, 0);
        gbc_buttonPanel.fill = GridBagConstraints.BOTH;
        gbc_buttonPanel.gridx = 0;
        gbc_buttonPanel.gridy = 2;
        add(buttonPanel, gbc_buttonPanel);
                buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
                btnSendSar = new JButton("Send SAR");
                buttonPanel.add(btnSendSar);
                
                btnManageSarTracking = new JButton("Manage SAR Tracking");
                buttonPanel.add(btnManageSarTracking);
                
                btnSendSar.addActionListener(this);
                btnManageSarTracking.addActionListener(this);

        

        
        DefaultTableModel model = new DefaultTableModel(30, 3);

        sruTable = new JTable(model) {
            private static final long serialVersionUID = 1L;

            public Component prepareRenderer(TableCellRenderer renderer,
                    int Index_row, int Index_col) {
                Component comp = super.prepareRenderer(renderer, Index_row,
                        Index_col);
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

        sruTableModel = new SARTrackingTableModel(EPDShore.getVoctManager()
                .getSruManager(), EPDShore.getVoctManager());
        sruTableModel.addTableModelListener(this);

        sruTable.setShowHorizontalLines(false);
        sruTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sruScrollPane = new JScrollPane(sruTable);
        sruScrollPane
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        sruScrollPane
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sruTable.setFillsViewportHeight(true);

        sruScrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
                new Color(30, 30, 30)));

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
        gbl_searchPatternsPanel.columnWeights = new double[] { 1.0,
                Double.MIN_VALUE };
        gbl_searchPatternsPanel.rowWeights = new double[] { 1.0,
                Double.MIN_VALUE };
        statusPanel.setLayout(gbl_searchPatternsPanel);

        GridBagConstraints gbc_sruScrollPane = new GridBagConstraints();
        gbc_sruScrollPane.fill = GridBagConstraints.BOTH;
        gbc_sruScrollPane.gridx = 0;
        gbc_sruScrollPane.gridy = 0;
        statusPanel.add(sruScrollPane, gbc_sruScrollPane);
    }

    @Override
    public void voctUpdated(VOCTUpdateEvent e) {

        if (e == VOCTUpdateEvent.SAR_CANCEL) {
            // sarCancel();
        }

        if (e == VOCTUpdateEvent.SAR_DISPLAY) {
            System.out.println("SAR PANEL DISPLAY ?");
            // sarComplete(voctManager.getSarData());
        }
        if (e == VOCTUpdateEvent.EFFORT_ALLOCATION_DISPLAY) {
            // effortAllocationComplete(voctManager.getSarData());
        }
        if (e == VOCTUpdateEvent.SEARCH_PATTERN_GENERATED) {
            // searchPatternGenerated(voctManager.getSarData());
        }

        // this.repaint();

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == btnSendSar) {
            voctManager.updateEffectiveAreaLocation();

            try {
                EPDShore.getEnavServiceHandler().sendVOCTMessage(0,
                        voctManager.getSarData(), "OSC", "Please Join", 0);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (TimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        
        if (arg0.getSource() == btnManageSarTracking) {

            // We have a SAR in progress
            if (voctManager != null && voctManager.isHasSar()) {

                // Determine what type of SAR then retrieve the input data
                if (voctCommsWindow != null) {
                    voctCommsWindow.setValues();
                    voctCommsWindow
                            .setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
                    voctCommsWindow.setVisible(true);
                }

            }
            return;
        }
        

        // if (arg0.getSource() == btnStartSar
        // || arg0.getSource() == btnReopenCalculations) {
        //
        //
        // if (voctManager != null) {
        //
        // voctManager.showSarInput();
        //
        // }
        // return;
        // }
        //
        //
        // if (arg0.getSource() == btnSruDialog) {
        //
        //
        // if (voctManager != null) {
        //
        // voctManager.showSRUManagerDialog();
        //
        // }
        // return;
        // }
        //
        //
        //
        // if (arg0.getSource() == btnEffortAllocation) {
        //
        // // We have a SAR in progress
        // if (voctManager != null && voctManager.isHasSar()) {
        //
        // // Determine what type of SAR then retrieve the input data
        // if (effortAllocationWindow != null) {
        // effortAllocationWindow.setValues();
        // effortAllocationWindow
        // .setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        // effortAllocationWindow.setVisible(true);
        // }
        //
        // }
        // return;
        // }

        // if (arg0.getSource() == btnGenerateSearchPattern) {
        //
        // if (searchPatternDialog != null) {
        //
        // // Semi hack for optimziation
        // voctManager.updateEffectiveAreaLocation();
        //
        // searchPatternDialog.setValues();
        // searchPatternDialog
        // .setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        // searchPatternDialog.setVisible(true);
        // }
        //
        // return;
        // }

        // if (arg0.getSource() == chckbxShowDynamicPattern) {
        //
        // if (chckbxShowDynamicPattern.isSelected()) {
        // sarData.getSearchPatternRoute().switchToDynamic();
        // } else {
        // sarData.getSearchPatternRoute().switchToStatic();
        // }
        //
        // EPDShip.getRouteManager().notifyListeners(
        // RoutesUpdateEvent.ROUTE_CHANGED);
        //
        // return;
        // }

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
        // TODO Auto-generated method stub
        
    }

    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        // TODO Auto-generated method stub
        
    }

}

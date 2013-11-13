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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.voct.SRU;
import dk.dma.epd.shore.voct.SRUManager;
import dk.dma.epd.shore.voct.SRUUpdateEvent;
import dk.dma.epd.shore.voct.SRUUpdateListener;
import dk.dma.epd.shore.voct.VOCTManager;

public class VOCTCommunicationWindow extends JDialog implements
        ListSelectionListener, MouseListener, TableModelListener,
        ActionListener, SRUUpdateListener {
    private static final long serialVersionUID = 1L;

    private final JPanel initPanel = new JPanel();
    private JButton sendSAR;
    private VOCTManager voctManager;
    private SRUManager sruManager;

    private JLabel noSRUs;

    // DefaultListModel<String> listModel = new DefaultListModel<String>();
    // JList<String> sruScrollPane;

    private JScrollPane sruScrollPane;
    private JTable sruTable;
    private VOCTCommunicationTableModel sruTableModel;
    private ListSelectionModel sruSelectionModel;

    /**
     * Create the dialog.
     */
    public VOCTCommunicationWindow() {
        setTitle("SAR Tracking");
        this.setModal(true);
        this.setResizable(false);

        // setBounds(100, 100, 559, 733);
        setBounds(100, 100, 621, 408);
        getContentPane().setLayout(new BorderLayout());

        buttomBar();

        initPanel();

        this.setVisible(false);
    }

    public void setVisible(boolean visible) {

        if (visible) {

            if (sruManager.getSRUCount() == 0) {
                noSRUs.setVisible(true);
                sendSAR.setEnabled(false);

                sruScrollPane.setVisible(false);
            } else {

                // fillSruList();
                sruTableModel.updateCalculateTable();

                sruScrollPane.setVisible(true);

                noSRUs.setVisible(false);
                sendSAR.setEnabled(true);

            }

        }

        super.setVisible(visible);

    }

    // private void fillSruList() {
    // // sruJList.removeAll();
    // listModel.removeAllElements();
    // List<SRU> sruList = sruManager.getSRUs();
    //
    // for (int i = 0; i < sruList.size(); i++) {
    // SRU currentSRU = sruList.get(i);
    // String sruTarget = currentSRU.getName() + " - "
    // + currentSRU.getSearchSpeed() + " kn - "
    // + currentSRU.getType();
    //
    // listModel.addElement(sruTarget);
    //
    // }
    //
    // }

    public void setVoctManager(VOCTManager voctManager) {
        this.voctManager = voctManager;
        sruManager = voctManager.getSruManager();
        sruManager.addListener(this);
    }

    private void initPanel() {
        initPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(initPanel, BorderLayout.CENTER);

        initPanel.setLayout(null);

        {
            JPanel panel = new JPanel();
            panel.setBorder(new TitledBorder(UIManager
                    .getBorder("TitledBorder.border"), "SRU Tracking",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel.setBounds(10, 11, 595, 325);
            initPanel.add(panel);
            panel.setLayout(null);

            JPanel panel_2 = new JPanel();
            panel_2.setBorder(new TitledBorder(UIManager
                    .getBorder("TitledBorder.border"), "All SRUs",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_2.setBounds(10, 32, 575, 282);
            panel.add(panel_2);
            panel_2.setLayout(null);

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

            sruTableModel = new VOCTCommunicationTableModel(EPDShore
                    .getVoctManager().getSruManager(),
                    EPDShore.getVoctManager());
            sruTableModel.addTableModelListener(this);

            sruTable.setShowHorizontalLines(false);
            sruTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // sruScrollPane = new JList<String>(listModel);
            sruScrollPane = new JScrollPane(sruTable);
            sruScrollPane.setEnabled(false);

            sruScrollPane.setBounds(10, 23, 555, 248);

            sruScrollPane
                    .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            sruScrollPane
                    .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            sruTable.setFillsViewportHeight(true);

            sruScrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
                    new Color(30, 30, 30)));

            // TODO: Comment this line when using WindowBuilder
            sruTable.setModel(sruTableModel);
            for (int i = 0; i < 6; i++) {

                if (i == 0) {
                    sruTable.getColumnModel().getColumn(i).setPreferredWidth(5);
                }
                if (i == 1) {
                    sruTable.getColumnModel().getColumn(i)
                            .setPreferredWidth(75);
                }
                if (i == 2) {
                    sruTable.getColumnModel().getColumn(i)
                            .setPreferredWidth(50);
                }

                if (i == 3) {
                    sruTable.getColumnModel().getColumn(i)
                            .setPreferredWidth(15);
                }
                if (i == 4) {
                    sruTable.getColumnModel().getColumn(i).setPreferredWidth(5);
                }
                if (i == 5) {
                    sruTable.getColumnModel().getColumn(i)
                            .setPreferredWidth(25);
                }
            }
            sruSelectionModel = sruTable.getSelectionModel();
            sruSelectionModel.addListSelectionListener(this);
            sruTable.setSelectionModel(sruSelectionModel);
            sruTable.addMouseListener(this);

            panel_2.add(sruScrollPane);

            noSRUs = new JLabel(
                    "There are no SRUs added. Please add a SRU before doing Effort Allocation");
            noSRUs.setBounds(10, 23, 446, 14);
            noSRUs.setVisible(false);
            panel_2.add(noSRUs);
            // targetTypeDropdown.setModel(new DefaultComboBoxModel<String>(
            // new String[] { "Person in Water, raft or boat < 30 ft",
            // "Other targets" }));

        }

    }

    public void setValues() {
        // VesselTarget ownship = EPDShip.getAisHandler().getOwnShip();
        //
        // if (ownship != null) {
        // if (ownship.getStaticData() != null) {
        // shipName.setText(ownship.getStaticData().getName());
        //
        // double length = ownship.getStaticData().getDimBow()
        // + ownship.getStaticData().getDimStern();
        // // String width = Integer.toString(ownship.getStaticData()
        // // .getDimPort()
        // // + ownship.getStaticData().getDimStarboard()) + " M";
        //
        // // Is the lenght indicated by the AIS longer than 89 feet then
        // // it falls under Ship category
        // if (Converter.metersToFeet(length) > 89) {
        // sruType.setSelectedIndex(1);
        // }
        //
        // }
        // }
    }

    private void buttomBar() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        {
            sendSAR = new JButton("Send SAR");
            buttonPane.add(sendSAR);
            getRootPane().setDefaultButton(sendSAR);
            sendSAR.addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        // Send SAR
        if (arg0.getSource() == sendSAR) {

            List<SRU> sruList = sruManager.getSRUs();

            // Which are we sending
            for (int i = 0; i < sruList.size(); i++) {

                // Send
                if ((boolean) sruTable.getValueAt(i, 0)) {

                    try {
                        voctManager.updateEffectiveAreaLocation();
                        EPDShore.getEnavServiceHandler().sendVOCTMessage(
                                sruList.get(i).getMmsi(),
                                voctManager.getSarData(), "OSC", "Please Join",
                                0, (boolean) sruTable.getValueAt(i, 4),
                                (boolean) sruTable.getValueAt(i, 5));

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

            }
            
            this.setVisible(false);

        }

    }

    private void displayMissingField(String fieldname) {
        // Missing or incorrect value in
        JOptionPane.showMessageDialog(this, "Missing or incorrect value in "
                + fieldname, "Input Error", JOptionPane.ERROR_MESSAGE);
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
    public void valueChanged(ListSelectionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void tableChanged(TableModelEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sruUpdated(SRUUpdateEvent e, int id) {
        // TODO Auto-generated method stub
//        sruTableModel.updateCalculateTable();
    }
}

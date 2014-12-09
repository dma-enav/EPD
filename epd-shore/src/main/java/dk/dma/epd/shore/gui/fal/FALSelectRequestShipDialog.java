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
package dk.dma.epd.shore.gui.fal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dk.dma.epd.common.prototype.service.ChatServiceData;

public class FALSelectRequestShipDialog extends JDialog implements ActionListener, ListSelectionListener, TableModelListener,
        MouseListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTable vesselNameTable;
    private FALSelectRequestShipTableModel routesTableModel;
    private ListSelectionModel routeSelectionModel;

    JButton okButton;
    JButton cancelButton;
    FALManagerDialog parent;

    public FALSelectRequestShipDialog(FALManagerDialog falManagerDialog) {
        super(falManagerDialog, "Request FAL Report", true);

        this.parent = falManagerDialog;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(falManagerDialog);
        // setResizable(false);

        // setBounds(100, 100, 450, 300);
        setSize(450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setLayout(new FlowLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        vesselNameTable = new JTable();
        routesTableModel = new FALSelectRequestShipTableModel();
        routesTableModel.addTableModelListener(this);
        vesselNameTable.setShowHorizontalLines(false);
        vesselNameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        routeSelectionModel = vesselNameTable.getSelectionModel();
        routeSelectionModel.addListSelectionListener(this);
        vesselNameTable.setSelectionModel(routeSelectionModel);
        vesselNameTable.addMouseListener(this);

        getContentPane().add(contentPanel, BorderLayout.CENTER);
        {
            JScrollPane scrollPane = new JScrollPane(vesselNameTable);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            contentPanel.add(scrollPane);

            scrollPane.setPreferredSize(new Dimension(400, 200));
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                okButton = new JButton("Send FAL Request");
                okButton.addActionListener(this);
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(this);
                buttonPane.add(cancelButton);
            }
        }

        vesselNameTable.setModel(routesTableModel);
        for (int i = 0; i < 1; i++) {
            if (i == 1) {
                vesselNameTable.getColumnModel().getColumn(i).setPreferredWidth(10);
            } else {
                vesselNameTable.getColumnModel().getColumn(i).setPreferredWidth(290);
            }
        }

        updateTable();

        setVisible(true);
    }

    public void updateTable() {
        int selectedRow = vesselNameTable.getSelectedRow();
        // Update routeTable
        routesTableModel.fireTableDataChanged();
        // routeTable.doLayout();
        if (selectedRow >= 0 && selectedRow < vesselNameTable.getRowCount()) {
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
//            System.out.println("Changed inclusion");
            // selectedWp.
            // Visibility has changed
            // routeManager
            // .notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource() == cancelButton) {

            this.dispose();
        }

        if (arg0.getSource() == okButton) {

            int selectedIndex = vesselNameTable.getSelectedRow();

            if (selectedIndex >= 0) {
                sendRequest();
            }

            this.dispose();

        }

    }

    private void sendRequest() {

        int selectedIndex = vesselNameTable.getSelectedRow();
        if (selectedIndex >= 0) {

            MaritimeId id = EPD.getInstance().getChatServiceHandler().getChatServiceList().get(selectedIndex).getId();

            int mmsi = Integer.parseInt(id.toString().split("mmsi://")[1]);

            ChatServiceData chatData = EPD.getInstance().getChatServiceHandler().getChatServiceData(new MmsiId(mmsi));

            // Sanity check
            if (chatData == null) {
                return;
            }

            String msg = "Please Transmit a FAL report";

            NotificationSeverity severity = NotificationSeverity.MESSAGE;

            EPD.getInstance().getChatServiceHandler().sendChatMessage(chatData.getId(), msg, severity);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        try {
            setOpacity((float) 0.95);
        } catch (Exception E) {
            System.out.println("Failed to set opacity, ignore");
        }

    }
}

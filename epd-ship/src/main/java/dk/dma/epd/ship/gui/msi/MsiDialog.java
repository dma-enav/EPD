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
package dk.dma.epd.ship.gui.msi;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;

import dk.dma.epd.common.prototype.msi.IMsiUpdateListener;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.ComponentFrame;
import dk.dma.epd.ship.layers.msi.EpdMsiLayer;
import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.common.xml.msi.MsiPoint;

/**
 * MSI dialog
 */
public class MsiDialog extends ComponentFrame implements ListSelectionListener, ActionListener, IMsiUpdateListener {
    private static final long serialVersionUID = 1L;

    private EpdMsiLayer msiLayer;
    private MsiHandler msiHandler;
    private JButton ackButton;
    private JButton deleteBtn;
    private JButton closeBtn;
    private JTable msiTable;
    private JScrollPane msiScrollPane;
    private JTextPane msiDetails;
    private JScrollPane detailsScrollPane; 
    private MsiTableModel msiTableModel;
    private ListSelectionModel msiSelectionModel;
    

    private JButton gotoBtn;

    private JPanel detailsPanel;
    
    public MsiDialog(Window parent) {
        super();
        setTitle("Maritime Safety Information");
        
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLocationRelativeTo(parent);
    }
    
    public void showMessage(int msgId) {
        setVisible(true);
        List<MsiMessageExtended> messages = msiTableModel.getMessages();
        for (int i = 0; i < messages.size(); i++) {
            MsiMessageExtended message = messages.get(i);
            if (message.msiMessage.getMessageId() == msgId) {
                if (i < msiTable.getRowCount()) {
                    msiSelectionModel.setSelectionInterval(i, i);
                    msiTable.scrollRectToVisible(msiTable.getCellRect(i, -1, true));
                }
                return;
            }            
        }
    }
    
    private void initGui() {
        ackButton = new JButton("Acknowledge");
        ackButton.addActionListener(this);
        deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(this);
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        gotoBtn = new JButton("Goto");
        gotoBtn.addActionListener(this);
        detailsPanel = new JPanel();
        detailsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        msiDetails = new JTextPane();
        msiDetails.setContentType("text/html");
        msiDetails.setEditable(false);
        detailsScrollPane = new JScrollPane(msiDetails);
        detailsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        detailsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        msiTable = new JTable();
        msiTable.setBorder(new LineBorder(new Color(0, 0, 0)));
        msiTable.setShowHorizontalLines(false);
        msiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        msiScrollPane = new JScrollPane(msiTable);
        msiScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        msiScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);        
        msiTable.setFillsViewportHeight(true);
        
        msiTableModel = new MsiTableModel(msiHandler);        
        //msiTableModel.addTableModelListener(this);
        msiTable.setModel(msiTableModel);
        msiTable.getColumnModel().getColumn(0).setPreferredWidth(25);
        msiTable.getColumnModel().getColumn(1).setPreferredWidth(25);
        msiTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        msiTable.getColumnModel().getColumn(0).setCellRenderer(new MsiTableCellRenderer(msiHandler));
        msiSelectionModel = msiTable.getSelectionModel();
        msiSelectionModel.addListSelectionListener(this);
        msiTable.setSelectionModel(msiSelectionModel);
        
        int preSelected = msiHandler.getFirstNonAcknowledged();
        setSelected(preSelected);

        updateButtons();
        
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(msiScrollPane, GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(ackButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(gotoBtn, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(deleteBtn, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(closeBtn, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE))
                        .addComponent(detailsPanel, GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(ackButton)
                        .addComponent(deleteBtn)
                        .addComponent(closeBtn)
                        .addComponent(gotoBtn))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(msiScrollPane, GroupLayout.PREFERRED_SIZE, 166, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(detailsPanel, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                    .addGap(10))
        );
        
        GroupLayout gl_detailsPanel = new GroupLayout(detailsPanel);
        gl_detailsPanel.setHorizontalGroup(
            gl_detailsPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(detailsScrollPane, GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
        );
        gl_detailsPanel.setVerticalGroup(
            gl_detailsPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(detailsScrollPane, GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
        );
        detailsPanel.setLayout(gl_detailsPanel);
        getContentPane().setLayout(groupLayout);
                
    }
    
    private void updateButtons() {
        int selected = msiTable.getSelectedRow();
        boolean ackAble = false;
        if (selected >= 0 && msiTableModel.getMessages().size() > 0) {
            MsiMessageExtended message = msiTableModel.getMessages().get(selected);
            if (!message.acknowledged) {
                ackAble = true;
            }
            setDetails(message.msiMessage);
        } else {
            setDetails(null);
        }
        ackButton.setEnabled(ackAble);
        gotoBtn.setEnabled(selected >= 0);
        deleteBtn.setEnabled(selected >= 0);
    }
    
    private void setSelected(int selectedRow) {
        msiSelectionModel.setSelectionInterval(selectedRow, selectedRow);
        msiTable.scrollRectToVisible(msiTable.getCellRect(selectedRow, -1, true));
    }
    
    private void updateTable() {
        int selectedRow = msiTable.getSelectedRow();
        msiTableModel.updateMessages();
        // Update table
        msiTableModel.fireTableDataChanged();
        if (selectedRow >= 0 && selectedRow < msiTable.getRowCount()) {
            setSelected(selectedRow);
        } else {
            if (selectedRow >= 0) {
                selectedRow = msiTable.getRowCount() - 1;
                setSelected(selectedRow);
            }
        }
        
        updateButtons();
        
    }
    
    private void setDetails(MsiMessage msiMessage) {
        if (msiMessage == null) {
            msiDetails.setText("");
            return;
        }
        StringBuilder buf = new StringBuilder();
        MsiLocation msiLocation = msiMessage.getLocation();
        
        buf.append("<table>");
        buf.append("<font color =\"FFFFFF\"><tr><td><b>Unique ID</b></td><td>" + msiMessage.getId() + "</td></tr>");
        buf.append("<font color =\"FFFFFF\"><tr><td><b>Msg ID</b></td><td>" + msiMessage.getMessageId() + "</td></tr>");
        buf.append("<font color =\"FFFFFF\"><tr><td><b>Version</b></td><td>" + msiMessage.getVersion() + "</td></tr>");
        buf.append("<font color =\"FFFFFF\"><tr><td><b>Message</b></td><td>" + msiMessage.getMessage() + "</td></tr>");
        buf.append("<font color =\"FFFFFF\"><tr><td><b>ENC text</b></td><td>" + Formatter.formatString(msiMessage.getEncText(), "") + "</td></tr>");
        if (msiLocation != null) {
            buf.append("<font color =\"FFFFFF\"><tr><td><b>Area</b></td><td>" + Formatter.formatString(msiLocation.getArea(), "") + "</td></tr>");
            if (msiLocation.getSubArea() != null && msiLocation.getSubArea().length() > 0) {
                buf.append("<font color =\"FFFFFF\"><tr><td><b>Sub area</b></td><td>" + Formatter.formatString(msiLocation.getSubArea(), "") + "</td></tr>");
            }
        }
        buf.append("<font color =\"FFFFFF\"><tr><td><b>Updated</b></td><td>" + Formatter.formatShortDateTime(msiMessage.getUpdated()) + "</td></tr>");
        buf.append("<font color =\"FFFFFF\"><tr><td><b>Created</b></td><td>" + Formatter.formatShortDateTime(msiMessage.getCreated()) + "</td></tr>");        
        buf.append("<font color =\"FFFFFF\"><tr><td><b>Reference</b></td><td>" + Formatter.formatString(msiMessage.getReference(), "") + "</td></tr>");
        if (msiMessage.getNavtexNo() != null && msiMessage.getNavtexNo().length() > 0) {
            buf.append("<font color =\"FFFFFF\"><tr><td><b>Navtex no</b></td><td>" + Formatter.formatString(msiMessage.getNavtexNo(), "") + "</td></tr>");
        }
        buf.append("<font color =\"FFFFFF\"><tr><td><b>Priority</b></td><td>" + Formatter.formatString(msiMessage.getPriority(), "") + "</td></tr>");
        buf.append("<font color =\"FFFFFF\"><tr><td><b>Valid from</b></td><td>" + Formatter.formatShortDateTime(msiMessage.getValidFrom()) + "</td></tr>");
        buf.append("<font color =\"FFFFFF\"><tr><td><b>Valid to</b></td><td>" + Formatter.formatShortDateTime(msiMessage.getValidTo()) + "</td></tr>");
        if (msiMessage.getLocationPrecision() != null) {
            buf.append("<font color =\"FFFFFF\"><tr><td><b>Location precision</b></td><td>" + Formatter.formatDouble(msiMessage.getLocationPrecision(), 2) + "</td></tr>");
        }
        if (msiMessage.getValidForDraugth() != null) {
            buf.append("<font color =\"FFFFFF\"><tr><td><b>Valid for draught</b></td><td>" + Formatter.formatDouble(msiMessage.getValidForDraugth(), 2) + " m</td></tr>");
        }
        if (msiMessage.getValidForShipType() != null) {
            buf.append("<font color =\"FFFFFF\"><tr><td><b>Valid for ship type</b></td><td>" + Formatter.formatString(msiMessage.getValidForShipType(), "") + "</td></tr>");
        }
        buf.append("<font color =\"FFFFFF\"><tr><td><b>Organisation</b></td><td>" + Formatter.formatString(msiMessage.getOrganisation(), "") + "</td></tr>");
        buf.append("<font color =\"FFFFFF\"><tr><td><b>Username</b></td><td>" + Formatter.formatString(msiMessage.getUsername(), "") + "</td></tr>");
        
        if (msiLocation != null && msiLocation.getPoints() != null) {
            buf.append("<font color =\"FFFFFF\"><tr><td><b>Location</b></td><td>");
            buf.append(msiLocation.getLocationType().name() + ": ");
            List<String> points = new ArrayList<>();
            for (MsiPoint msiPoint : msiLocation.getPoints()) {
                points.add(String.format("(%.4f,%.4f)", msiPoint.getLatitude(), msiPoint.getLongitude()));
            }
            buf.append(StringUtils.join(points.iterator(), ", "));
            buf.append("</td></tr>");
        }
        
        buf.append("</table>");
        
        msiDetails.setText(buf.toString());
        
        msiDetails.setSelectionStart(0);
        msiDetails.setSelectionEnd(0);
        
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        updateButtons();
    }
    
    public MsiMessageExtended getMessage(int i) {
        List<MsiMessageExtended> messages = msiTableModel.getMessages();
        return messages.get(i);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ackButton) {
            int selected;
            msiHandler.setAcknowledged(getMessage(msiTable.getSelectedRow()).msiMessage);
            if (EPDShip.getSettings().getEnavSettings().isMsiFilter()){
                selected = msiHandler.getFirstNonAcknowledgedFiltered();
            }else{
                selected = msiHandler.getFirstNonAcknowledged();
            }

            setSelected(selected);
            
            //updateTable();
            msiHandler.notifyUpdate();
        } else if (e.getSource() == gotoBtn) {
            msiLayer.zoomTo(getMessage(msiTable.getSelectedRow()).msiMessage);
        } else if (e.getSource() == closeBtn) {
            setVisible(false);
        } else if (e.getSource() == deleteBtn) {
            int res = JOptionPane.showConfirmDialog(this, "Delete MSI permanently?", "Delete MSI", JOptionPane.YES_NO_OPTION);
            if (res == 0) {
                msiHandler.deleteMessage(getMessage(msiTable.getSelectedRow()).msiMessage);
                //updateTable();
            }
        }        
    }

    @Override
    public void msiUpdate() {
        updateTable();        
    }
    
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MsiHandler) {
            msiHandler = (MsiHandler)obj;
            msiHandler.addListener(this);
            initGui();
        }
        if (obj instanceof EpdMsiLayer) {
            msiLayer = (EpdMsiLayer)obj;
        }
    }
}

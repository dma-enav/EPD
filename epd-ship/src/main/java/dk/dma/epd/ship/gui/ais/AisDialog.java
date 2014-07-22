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
package dk.dma.epd.ship.gui.ais;


import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.maritimecloud.core.id.MmsiId;
import dk.dma.ais.message.AisMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.ais.VesselTarget.AisClass;
import dk.dma.epd.common.prototype.gui.ComponentDialog;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.layers.ais.AisLayer;

/**
 * AIS targets dialog
 */
public class AisDialog extends ComponentDialog implements ListSelectionListener, ActionListener, WindowListener {
    private static final long serialVersionUID = 1L;

    private AisLayer aisLayer;
    private AisHandler aisHandler;
    
    private JButton closeBtn = new JButton("Close", EPD.res().getCachedImageIcon("images/notifications/cross.png"));
    private JButton gotoBtn = new JButton("Goto", EPD.res().getCachedImageIcon("images/notifications/map-pin.png"));
    private JButton chatBtn = new JButton("Chat", EPD.res().getCachedImageIcon("images/notifications/balloon.png"));
    
    private JTable aisTable = new JTable();
    private JTable aisTableDetails;
    private JScrollPane aisScrollPane;
    
    private JScrollPane detailsScrollPane; 
    
    private AisTableModel aisTableModel ;
    private ListSelectionModel aisSelectionModel; 
    
    public AisDialog(Window parent) {
        super(parent, "AIS Vessel Target", Dialog.ModalityType.MODELESS);
        setSize(580, 437);  
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLocationRelativeTo(parent);       
        // NB: Initializing the UI is deferred until findAndInit
    }

    private void initGui() {
        
        this.addWindowListener(this);
    
        
        closeBtn.addActionListener(this);
        gotoBtn.addActionListener(this);
        chatBtn.addActionListener(this);
        gotoBtn.setEnabled(false);
        chatBtn.setEnabled(false);
             
        String[] columnNames = {"Type", "Details"};
        
        Object[][] data = {
                {" MMSI", ""},
                {" AIS", ""},
                {" Name", ""},
                {" Call Sign", ""},
                {" Length", ""},
                {" Width", ""},
                {" Draught", ""},
                {" Nav status", ""},
                {" Type", ""},
                {" Cargo", ""},
                {" Lat", ""},
                {" Long", ""},
                {" Last Received", ""},
                {" Destination", ""},
                {" ETA", ""},
                {" Heading", ""},
                {" COG", ""},
                {" SOG", ""},
                {" ROT", ""},
                {" Risk1", ""},
                {" Risk2", ""},
                {" Risk3", ""}
            };
        
        
        aisTableDetails = new JTable(data, columnNames);
        
        aisTableDetails.setBorder(new LineBorder(new Color(0, 0, 0)));
        aisTableDetails.setShowHorizontalLines(false);
        aisTableDetails.setEnabled(false);
        
        detailsScrollPane = new JScrollPane(aisTableDetails);
        detailsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        detailsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        aisTable = new JTable();
        
        aisTable.setBorder(new LineBorder(new Color(0, 0, 0)));
        aisTable.setShowHorizontalLines(false);
        aisTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        aisScrollPane = new JScrollPane(aisTable);
        aisScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        aisScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);        
        aisTable.setFillsViewportHeight(true);
        
    
        aisTableModel = new AisTableModel(aisHandler);        

        aisTable.setModel(aisTableModel);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(aisTable.getModel());
        
        sorter.setComparator(aisTableModel.getColumnIndex(AisTableModel.COL_NAME), new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        
        sorter.setComparator(aisTableModel.getColumnIndex(AisTableModel.COL_MMSI), new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return o1.compareTo(o2);
            }
        });
        
        sorter.setComparator(aisTableModel.getColumnIndex(AisTableModel.COL_HDG), new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return o1.compareTo(o2);
            }
        });
        
        sorter.setComparator(aisTableModel.getColumnIndex(AisTableModel.COL_DST), new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                try {
                    Float l1 = Float.parseFloat(o1.split(" ")[0].replace(".","").replace(",","."));
                    Float l2 = Float.parseFloat(o2.split(" ")[0].replace(".","").replace(",","."));
                    
                    if (l1 < l2) {
                        return -1;
                    } else if (l1 == l2) {
                        return 0;
                    } else if (l1 > l2) {
                        return 1;
                    }
                    
                } catch(java.lang.NumberFormatException e) {
                    //fallback to string comparison
                    return o1.compareTo(o2);
                }
                

                return -1;
            }
            
        });
        
        sorter.toggleSortOrder(1);
        sorter.setSortsOnUpdates(true);
        aisTable.setRowSorter(sorter);
        
        aisSelectionModel = aisTable.getSelectionModel();
        aisSelectionModel.addListSelectionListener(this);
        aisTable.setSelectionModel(aisSelectionModel);        
        aisTable.setSelectionMode(0);
        
        JPanel btnPanel = new JPanel();
        btnPanel.add(gotoBtn);
        btnPanel.add(chatBtn);

        // Assemble the GUI
        JPanel mainPanel = new JPanel(new GridBagLayout());
        getContentPane().add(mainPanel);
        Insets insets1  = new Insets(5, 5, 5, 5);
        Insets insets2  = new Insets(5, 5, 0, 5);
        
        mainPanel.add(new JLabel("Targets"), 
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        mainPanel.add(new JLabel("Details"), 
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
               
        mainPanel.add(aisScrollPane, 
                new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, WEST, BOTH, insets2, 0, 0));
        mainPanel.add(detailsScrollPane, 
                new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, WEST, BOTH, insets2, 0, 0));

        mainPanel.add(btnPanel, 
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));
        mainPanel.add(closeBtn, 
                new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, EAST, NONE, insets2, 0, 0));
               
    }
    
    private void setSelected(int selectedRow, boolean opening) {
        aisSelectionModel.setSelectionInterval(selectedRow, selectedRow);
        if (opening){
            aisTable.scrollRectToVisible(aisTable.getCellRect(selectedRow, -1, true));
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        /*
         * TODO cannot perform this cleanup as MainFrame explicitly
         * calls dispose as part of its initialization :-(.
         */
//        // Cleanup.
//        if(aisHandler != null && aisTableModel != null) {
//            aisHandler.removeListener(aisTableModel);
//        }
    }
       
    private void updateDetails() {
        int selected = aisTable.getSelectedRow();
        if (selected >= 0 && selected < aisTable.getRowCount()){
            int modelRowIndex = aisTable.convertRowIndexToModel(selected);
            int modelColIndex = aisTableModel.getColumnIndex(AisTableModel.COL_MMSI);
            Long mmsi = (Long) aisTableModel.getValueAt(modelRowIndex, modelColIndex);
            
            if (aisHandler.getVesselTarget(mmsi) != null) {
                setDetails(aisHandler.getVesselTarget(mmsi));
            }
            gotoBtn.setEnabled(aisHandler.getVesselTarget(mmsi) != null && 
                    aisHandler.getVesselTarget(mmsi).getPositionData() != null);
            chatBtn.setEnabled(EPD.getInstance().getChatServiceHandler()
                    .availableForChat(mmsi.intValue()));
        } else {
            gotoBtn.setEnabled(false);
            chatBtn.setEnabled(false);
        }
    }
        

    private void setDetails(VesselTarget vesselTarget) {

        Position aisLocation = vesselTarget.getPositionData().getPos();
        
        String name = "N/A";    
        String callsign = "N/A";
//        String imo = "unknown";
        String type = "unknown";
        String destination = "unknown";
        String draught = "N/A";
        String trueHeading = "N/A";
        String length = "N/A";
        String width = "N/A";
        String navStatus = "N/A";
        String lastReceived = "N/A";
        String eta = "N/A";
        String cargo = "unknown";
        Date currentDate = PntTime.getDate();

        if (vesselTarget.getStaticData() != null ){
            name = vesselTarget.getStaticData().getTrimmedName();
            callsign = vesselTarget.getStaticData().getTrimmedCallsign();
//            imo = Long.toString(vesselTarget.getStaticData().getImo());
            type = vesselTarget.getStaticData().getShipType().prettyType();
            cargo = vesselTarget.getStaticData().getShipType().prettyCargo();
            destination = AisMessage.trimText(vesselTarget.getStaticData().getDestination());
            if (destination == null){
                destination = "unknown";
            }
            draught = Float.toString(vesselTarget.getStaticData().getDraught()/10);
            trueHeading = Float.toString(vesselTarget.getPositionData().getTrueHeading());
            length = Integer.toString(vesselTarget.getStaticData().getDimBow() + vesselTarget.getStaticData().getDimStern()) + " M";
            width = Integer.toString(vesselTarget.getStaticData().getDimPort() + vesselTarget.getStaticData().getDimStarboard()) + " M";
            navStatus = vesselTarget.getPositionData().getEnumNavStatus().toString();
            lastReceived = Long.toString((currentDate.getTime() - vesselTarget.getLastReceived().getTime()) / 1000) + " seconds ago";

            eta = Long.toString(vesselTarget.getStaticData().getEta());
        }
        
        updateTable(vesselTarget.getMmsi(), vesselTarget.getAisClass(), name, callsign, length, 
                width, draught, navStatus, type, cargo, Formatter.latToPrintable(aisLocation.getLatitude()), 
                        Formatter.lonToPrintable(aisLocation.getLongitude()), lastReceived, destination, 
                                eta, trueHeading, vesselTarget.getPositionData().getCog(), 
                                vesselTarget.getPositionData().getSog(), vesselTarget.getPositionData().getRot());
    }


    private void updateTable(long mmsi, AisClass aisClass, String name, String callSign,
            String length, String width, String draught, String navStatus, String type,
            String cargo, String lat, String longi, String lastReceived, String destination,
            String eta, String trueHeading, float cog, float sog, float rot){
        
        if (!compare(aisTableDetails.getValueAt(0, 1), mmsi)){
        aisTableDetails.setValueAt(mmsi, 0, 1);
        }
        if (!compare(aisTableDetails.getValueAt(1, 1), "Class " + aisClass)){        
        aisTableDetails.setValueAt("Class " + aisClass, 1, 1);
        }
        if (!compare(aisTableDetails.getValueAt(2, 1), name)){
        aisTableDetails.setValueAt(name, 2, 1);
        }
        if (!compare(aisTableDetails.getValueAt(3, 1), callSign)){
        aisTableDetails.setValueAt(callSign, 3, 1);
        }
        if (!compare(aisTableDetails.getValueAt(4, 1), length)){
        aisTableDetails.setValueAt(length, 4, 1);
        }
        if (!compare(aisTableDetails.getValueAt(5, 1), width)){
        aisTableDetails.setValueAt(width, 5, 1);
        }
        if (!compare(aisTableDetails.getValueAt(6, 1), draught)){
        aisTableDetails.setValueAt(draught, 6, 1);
        }
        if (!compare(aisTableDetails.getValueAt(7, 1), navStatus)){
        aisTableDetails.setValueAt(navStatus, 7, 1);
        }
        if (!compare(aisTableDetails.getValueAt(8, 1), type)){
        aisTableDetails.setValueAt(type, 8, 1);
        }
        if (!compare(aisTableDetails.getValueAt(9, 1), cargo)){
        aisTableDetails.setValueAt(cargo, 9, 1);
        }
        if (!compare(aisTableDetails.getValueAt(10, 1), lat)){
        aisTableDetails.setValueAt(lat, 10, 1);
        }
        if (!compare(aisTableDetails.getValueAt(11, 1), longi)){
        aisTableDetails.setValueAt(longi, 11, 1);
        }
        if (!compare(aisTableDetails.getValueAt(12, 1), lastReceived)){
        aisTableDetails.setValueAt(lastReceived, 12, 1);
        }
        if (!compare(aisTableDetails.getValueAt(13, 1), destination)){
        aisTableDetails.setValueAt(destination, 13, 1);
        }
        if (!compare(aisTableDetails.getValueAt(14, 1), eta)){
        aisTableDetails.setValueAt(eta, 14, 1);
        }
        if (!compare(aisTableDetails.getValueAt(15, 1), trueHeading)){
        aisTableDetails.setValueAt(trueHeading, 15, 1);
        }
        if (!compare(aisTableDetails.getValueAt(16, 1), cog)){
        aisTableDetails.setValueAt(cog, 16, 1);
        }
        if (!compare(aisTableDetails.getValueAt(17, 1), sog)){
        aisTableDetails.setValueAt(sog, 17, 1);
        }
        if (!compare(aisTableDetails.getValueAt(18, 1), rot)){
        aisTableDetails.setValueAt(rot, 18, 1);
        }
    }
    
    private boolean compare(Object value1, Object value2){
        if (value1 != null && value2 != null) {
            if (value1.toString().equals(value2.toString())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == gotoBtn) {
            int selectedRow = aisTable.getSelectedRow();
            if(selectedRow == -1) {
                // no row selected.
                return;
            }
            // convert view row to model row.
            int modelRow = aisTable.convertRowIndexToModel(selectedRow);
            // lookup MMSI in model data.
            long selectedMMSI = (Long) aisTableModel.getValueAt(modelRow, aisTableModel.getColumnIndex(AisTableModel.COL_MMSI));
            aisLayer.zoomTo(aisHandler.getVesselTarget(selectedMMSI).getPositionData().getPos());
        
        } else if (e.getSource() == closeBtn) {
            setVisible(false);
            
        } else if (e.getSource() == chatBtn) {
            int selectedRow = aisTable.getSelectedRow();
            long selectedMMSI = (Long) aisTable.getValueAt(selectedRow, 1);
            EPD.getInstance().getNotificationCenter()
                .openNotification(NotificationType.MESSAGES, new MmsiId((int)selectedMMSI), false);
        }        
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof AisLayer) {
            aisLayer = (AisLayer)obj;
        }
        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler)obj;
            initGui();
        }        
    }

    public void setSelection(long mmsi, boolean opening) {
        setSelected(getMMSISelection(mmsi), opening);
    }
    
    public int getMMSISelection(long mmsi){
        for (int i = 0; i < aisTable.getRowCount(); i++){
            Long currentValue = (Long) aisTable.getValueAt(i, 1);
            if (currentValue == mmsi){
                System.out.println("Value found");
                return i;
            }
        }
        return 0;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        updateDetails();
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    
}

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
package dk.dma.epd.ship.gui.ais;


import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import dk.dma.ais.message.AisMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon.AisMessageExtended;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.ais.VesselTarget.AisClass;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.ComponentFrame;
import dk.dma.epd.ship.layers.ais.AisLayer;

/**
 * AIS targets dialog
 */
public class AisDialog extends ComponentFrame implements ListSelectionListener, ActionListener, IAisTargetListener, WindowListener {
    private static final long serialVersionUID = 1L;

    private AisLayer aisLayer;
    private AisHandler aisHandler;
    
    private JButton closeBtn;
    private JButton gotoBtn;
    
    private JTable aisTable = new JTable();
    private JTable aisTableDetails;
    private JScrollPane aisScrollPane;
    
    private JScrollPane detailsScrollPane; 
    
    private AisTableModel aisTableModel ;
    private ListSelectionModel aisSelectionModel;
    
    private JPanel detailsPanel;
    
    
    
    public AisDialog(Window parent) {
        super();
        setTitle("AIS Vessel Target");
        setSize(580, 437);
        
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLocationRelativeTo(parent);
    }

    private void initGui() {
        
        this.addWindowListener(this);
    
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        gotoBtn = new JButton("Goto");
        gotoBtn.addActionListener(this);
        
        detailsPanel = new JPanel();
        //detailsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
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
                {" Last Recieved", ""},
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
        
        
        
        //detailsScrollPane = new JScrollPane(aisDetails);
        detailsScrollPane = new JScrollPane(aisTableDetails);
        detailsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        detailsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        //detailsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        aisTable = new JTable();
        
        aisTable.setBorder(new LineBorder(new Color(0, 0, 0)));
        aisTable.setShowHorizontalLines(false);
        aisTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        aisScrollPane = new JScrollPane(aisTable);
        aisScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        aisScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);        
        aisTable.setFillsViewportHeight(true);
        
        JLabel tagetsLabel = new JLabel("Targets");
        JLabel detailsLabel = new JLabel("Details");
        
        
        
    
        aisTableModel = new AisTableModel(aisHandler);        

        aisTable.setModel(aisTableModel);
        
        aisSelectionModel = aisTable.getSelectionModel();
        aisTable.setSelectionModel(aisSelectionModel);

        //        aisTable.setAutoCreateRowSorter(true);
        TableRowSorter<TableModel> sorter 
        = new TableRowSorter<>(aisTable.getModel());
        
        //
        sorter.setComparator(3, new Comparator<String>() {

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
                    //Logger LOG = LoggerFactory.getLogger(Util.class);
                    //LOG.error("Invalid format: "+o1+" vs "+o2);

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
        
        
        GroupLayout gl_detailsPanel =  new GroupLayout(getContentPane());

        
        gl_detailsPanel.setHorizontalGroup(
                gl_detailsPanel.createParallelGroup(Alignment.TRAILING)
                    .addGroup(gl_detailsPanel.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_detailsPanel.createParallelGroup(Alignment.TRAILING)
                            .addGroup(gl_detailsPanel.createSequentialGroup()
                                .addComponent(gotoBtn)
                                .addPreferredGap(ComponentPlacement.RELATED, 420, Short.MAX_VALUE)
                                .addComponent(closeBtn))
                            .addGroup(gl_detailsPanel.createSequentialGroup()
                                .addGroup(gl_detailsPanel.createParallelGroup(Alignment.LEADING)
                                    .addComponent(aisScrollPane, GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                                    .addComponent(tagetsLabel))
                                .addGap(14)
                                .addGroup(gl_detailsPanel.createParallelGroup(Alignment.LEADING)
                                    .addComponent(detailsLabel)
                                    .addComponent(detailsScrollPane, GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))))
                        .addContainerGap())
            );
        
        gl_detailsPanel.setVerticalGroup(
                gl_detailsPanel.createParallelGroup(Alignment.TRAILING)
                    .addGroup(gl_detailsPanel.createSequentialGroup()
                        .addGroup(gl_detailsPanel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(tagetsLabel)
                            .addComponent(detailsLabel))
                        .addGap(9)
                        .addGroup(gl_detailsPanel.createParallelGroup(Alignment.TRAILING)
                            .addComponent(detailsScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                            .addComponent(aisScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addGroup(gl_detailsPanel.createParallelGroup(Alignment.LEADING)
                            .addComponent(gotoBtn)
                            .addComponent(closeBtn))
                        .addContainerGap())
            );

        detailsPanel.setLayout(gl_detailsPanel);
        getContentPane().setLayout(gl_detailsPanel);
                
    }
    
    private void setSelected(int selectedRow, boolean opening) {
        aisSelectionModel.setSelectionInterval(selectedRow, selectedRow);
        if (opening){
            aisTable.scrollRectToVisible(aisTable.getCellRect(selectedRow, -1, true));
        }
    }
    
    private void updateTable() {
        if (aisTable != null) {
            int selectedRow = aisTable.getSelectedRow();
    
            long selectedMMSI = 0L;
            if (selectedRow >=0 && selectedRow < aisTable.getRowCount()) {
                selectedMMSI = (Long) aisTable.getValueAt(selectedRow, 1);
            }            
                
            if (aisTableModel != null) {
                aisTableModel.updateShips(); //nonblocking background task

                if (selectedRow >= 0 && selectedRow < aisTable.getRowCount()) {
                    setSelected(selectedRow, false);
                } else {
                    if (selectedRow >= 0) {
                        selectedRow = aisTable.getRowCount() - 1;
                        setSelected(selectedRow, false);
                    }
                }
                updateDetails();
                setSelection(selectedMMSI, false);
            }
        }
    }
    
    private void updateTable(final AisTarget aisTarget) {
        if (aisTable != null) {
            if (aisTableModel != null) {
                if (aisTarget instanceof VesselTarget) {
                    aisTableModel.queueShip((VesselTarget)aisTarget);
                    
                }
            }
        }
    }    
    
    
    private void updateDetails() {
        int selected = aisTable.getSelectedRow();
        if (selected >= 0 && selected < aisTable.getRowCount() && aisHandler.getVesselTargets() != null){
            Object mmsi = aisTable.getValueAt(selected, 1);
            if (aisHandler.getVesselTargets().get(mmsi) != null) {
            setDetails(aisHandler.getVesselTargets().get(mmsi));
            //setRiskDetails(EeINS.getRiskHandler().getRiskList((Long)mmsi));
            }
        }
    }
        
//    private void setRiskDetails(RiskList risk) {
//        if(risk==null){
//            aisTableDetails.setValueAt("", 19, 1);
//            aisTableDetails.setValueAt("", 20, 1);
//            aisTableDetails.setValueAt("", 21, 1);
//            return;
//        }
////        if (!compare(aisTableDetails.getValueAt(19, 1), risk.getCpaDist())){
////            aisTableDetails.setValueAt(risk.getCpaDist(), 19, 1);
////            }
////        if (!compare(aisTableDetails.getValueAt(20, 1), risk.getCpaTime())){
////            aisTableDetails.setValueAt(risk.getCpaTime(), 20, 1);
////            }
////        if (!compare(aisTableDetails.getValueAt(21, 1), risk.getCpaTargetMmsi())){
////            aisTableDetails.setValueAt(risk.getCpaTargetMmsi(), 21, 1);
////            }
//    }

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
        String lastRecieved = "N/A";
        String eta = "N/A";
        String cargo = "unknown";
        Date currentDate = new Date();

        if (vesselTarget.getStaticData() != null ){
            name = AisMessage.trimText(vesselTarget.getStaticData().getName());
            callsign = AisMessage.trimText(vesselTarget.getStaticData().getCallsign());
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
            lastRecieved = Long.toString((currentDate.getTime() - vesselTarget.getLastReceived().getTime()) / 1000) + " seconds ago";

            eta = Long.toString(vesselTarget.getStaticData().getEta());
        }
        
        updateTable(vesselTarget.getMmsi(), vesselTarget.getAisClass(), name, callsign, length, 
                width, draught, navStatus, type, cargo, Formatter.latToPrintable(aisLocation.getLatitude()), 
                        Formatter.lonToPrintable(aisLocation.getLongitude()), lastRecieved, destination, 
                                eta, trueHeading, vesselTarget.getPositionData().getCog(), 
                                vesselTarget.getPositionData().getSog(), vesselTarget.getPositionData().getRot());
    }


    private void updateTable(long mmsi, AisClass aisClass, String name, String callSign,
            String length, String width, String draught, String navStatus, String type,
            String cargo, String lat, String longi, String lastRecieved, String destination,
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
        if (!compare(aisTableDetails.getValueAt(12, 1), lastRecieved)){
        aisTableDetails.setValueAt(lastRecieved, 12, 1);
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
        if (value1 != null && value2 != null)
        {
            if (value1.toString().equals(value2.toString())){
                return true;
            }
        }
    return false;
}

    public AisMessageExtended getMessage(int i) {
        List<AisMessageExtended> messages = aisTableModel.getShips();
        return messages.get(i);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    if (e.getSource() == gotoBtn) {
        int selectedRow = aisTable.getSelectedRow();
        long selectedMMSI = (Long) aisTable.getValueAt(selectedRow, 1);
        
        aisLayer.zoomTo(aisHandler.getVesselTargets().get(selectedMMSI).getPositionData().getPos());
        } else if (e.getSource() == closeBtn) {
            setVisible(false);
        }        
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof AisLayer) {
            aisLayer = (AisLayer)obj;
        }
        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler)obj;
            aisHandler.addListener(this);
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
                return i;
            }
        }
        return 0;
    }

    @Override
    public void targetUpdated(final AisTarget aisTarget) {
        // Only update table if dialog is visible
        if (isVisible()) {
            if (aisTarget instanceof VesselTarget) {

                SwingUtilities.invokeLater(new Runnable() {
                        
                    @Override
                    public void run() {
                        updateTable(aisTarget);
                        
                    }
                });
            }
        }
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
        updateDetails();
    }

    /**
     * Update table when window is activated
     */
    @Override
    public void windowActivated(WindowEvent e) {
        updateTable();
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

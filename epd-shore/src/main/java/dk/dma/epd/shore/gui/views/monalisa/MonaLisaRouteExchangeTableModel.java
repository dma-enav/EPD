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
package dk.dma.epd.shore.gui.views.monalisa;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.service.MonaLisaHandler;
import dk.dma.epd.shore.service.MonaLisaRouteNegotiationData;

/**
 * Table model for Route Exchange Notifications
 */
public class MonaLisaRouteExchangeTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

//    private static final String[] AREA_COLUMN_NAMES = { "ID", "MMSI",
//            "Ship Name", "Call Sign", "Type", "Destination", "Length", "Width",
//            "Draught", "COG", "SOG",
//            "Sent Date", "Message", "Status", "Reply Sent", "Message" };
    private static final String[] COLUMN_NAMES = { "Name", "Callsign",
            "Called", "Status" };

//    private EnavServiceHandler enavServiceHandler;
    private MonaLisaHandler monaLisaHandler;
    private AisHandler aisHandler;

    private List<MonaLisaRouteNegotiationData> messages = new ArrayList<MonaLisaRouteNegotiationData>();

    /**
     * Constructor for creating the msi table model
     * 
     * @param msiHandler
     */
    public MonaLisaRouteExchangeTableModel() {
        super();
        updateMessages();
    }

    public void setAisHandler(AisHandler aisHandler) {
        this.aisHandler = aisHandler;
    }
    
    

//    public void setEnavServiceHandler(EnavServiceHandler enavServiceHandler) {
//        this.enavServiceHandler = enavServiceHandler;
//    }

    
    
    
    public void setMonaLisaHandler(MonaLisaHandler monaLisaHandler) {
        this.monaLisaHandler = monaLisaHandler;
    }

    /**
     * Get column class at specific index
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Object value = getValueAt(0, columnIndex);
        if (value == null) {
            return String.class;
        }
        return value.getClass();
    }

    /**
     * Get the column count
     */
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

//    public int areaGetColumnCount() {
//        return AREA_COLUMN_NAMES.length;
//    }

    /**
     * Return the column names
     */
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

//    public String areaGetColumnName(int column) {
//        return AREA_COLUMN_NAMES[column];
//    }

    /**
     * Return messages
     * 
     * @return
     */
    public List<MonaLisaRouteNegotiationData> getMessages() {
        return messages;

    }

    /**
     * Get the row count
     */
    @Override
    public int getRowCount() {
        return messages.size();
    }

    /**
     * Get the value at a specific row and colum index
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (rowIndex == -1) {
            return "";
        }
        MonaLisaRouteNegotiationData message = messages.get(rowIndex);

        switch (columnIndex) {
        case 0:
            if (aisHandler != null) {
                if (aisHandler.getVesselTargets().get(message.getMmsi())
                        .getStaticData() != null) {
                    return aisHandler.getVesselTargets().get(message.getMmsi())
                            .getStaticData().getName().trim();
                } else {
                    return message.getMmsi();
                }
            } else {
                return message.getMmsi();
            }
        case 1:
            if (aisHandler != null) {
                if (aisHandler.getVesselTargets().get(message.getMmsi())
                        .getStaticData() != null) {
                    return aisHandler.getVesselTargets().get(message.getMmsi())
                            .getStaticData().getCallsign();
                } else {
                    return "N/A";
                }
            } else {
                return "N/A";
            }
        case 2:
            return Formatter.formatShortDateTime(message.getRouteMessage()
                    .get(0).getSent());
//        case 3:
//            return message.getRouteMessage().get(0).getRoute().getName();
        case 3:
            return message.getStatus();
        default:
            return "";

        }
    }
    
//    public Object areaGetValueAt(int rowIndex, int columnIndex) {
//        if (rowIndex == -1 || this.getRowCount() < 1) {
//            return "";
//        }
//        MonaLisaRouteNegotiationData message = messages.get(rowIndex);
//
//        switch (columnIndex) {
//        case 0:
//            return message.getId();
//        case 1:
//            return message.getMmsi();
//        case 2:
//            if (aisHandler.getVesselTargets().get(message.getMmsi())
//                    .getStaticData() != null) {
//                return aisHandler.getVesselTargets().get(message.getMmsi())
//                        .getStaticData().getName();
//            } else {
//                return message.getMmsi();
//            }
//        case 3:
//            if (aisHandler.getVesselTargets().get(message.getMmsi())
//                    .getStaticData() != null) {
//                return aisHandler.getVesselTargets().get(message.getMmsi())
//                        .getStaticData().getCallsign();
//            } else {
//                return "N/A";
//            }
//        case 4:
//            if (aisHandler.getVesselTargets().get(message.getMmsi())
//                    .getStaticData() != null) {
//                return aisHandler.getVesselTargets().get(message.getMmsi())
//                        .getStaticData().getShipType();
//            } else {
//                return "N/A";
//            }
//        case 5:
//            if (aisHandler.getVesselTargets().get(message.getMmsi())
//                    .getStaticData() != null) {
//                return aisHandler.getVesselTargets().get(message.getMmsi())
//                        .getStaticData().getDestination();
//            } else {
//                return "N/A";
//            }
//        case 6:
//            if (aisHandler.getVesselTargets().get(message.getMmsi())
//                    .getStaticData() != null) {
//                return aisHandler.getVesselTargets().get(message.getMmsi())
//                        .getStaticData().getDimBow() + aisHandler.getVesselTargets().get(message.getMmsi())
//                        .getStaticData().getDimStern();
//            } else {
//                return "N/A";
//            }
//        case 7:
//            if (aisHandler.getVesselTargets().get(message.getMmsi())
//                    .getStaticData() != null) {
//                return aisHandler.getVesselTargets().get(message.getMmsi())
//                        .getStaticData().getDimPort() + aisHandler.getVesselTargets().get(message.getMmsi())
//                        .getStaticData().getDimStarboard();
//            } else {
//                return "N/A";
//            }
//        case 8:
//            if (aisHandler.getVesselTargets().get(message.getMmsi())
//                    .getStaticData() != null) {
//                return aisHandler.getVesselTargets().get(message.getMmsi())
//                        .getStaticData().getDraught()/10;
//            } else {
//                return "N/A";
//            }
//        case 9:
//           return aisHandler.getVesselTargets().get(message.getMmsi()).getPositionData().getCog();
//        case 10:
//            return aisHandler.getVesselTargets().get(message.getMmsi()).getPositionData().getSog();
//        case 11:
//            return Formatter.formatShortDateTime(message.getRouteMessage()
//                    .get(0).getSent());
//        case 12:
//            return message.getRouteMessage().get(0).getMessage();
//        case 13:
//            return message.getStatus();
//        case 14:
//            if (message.getRouteReply().size() > 0){
//                return Formatter.formatShortDateTime(new Date(message.getRouteReply().get(0).getSendDate()));
//            }
//        case 15:
//            if (message.getRouteReply().size() > 0){
//                return message.getRouteReply().get(0).getMessage();
//            }
//        default:
//            return "";
//        }
//    }

    /**
     * Update messages
     */
    public void updateMessages() {
        if (monaLisaHandler != null){
            
        
        messages.clear();

        for (Iterator<MonaLisaRouteNegotiationData> it = monaLisaHandler
                .getMonaLisaNegotiationData().values().iterator(); it.hasNext();) {
            messages.add(it.next());
        }
        }
    }

    public boolean isAwk(int rowIndex) {
        if (rowIndex == -1 || this.getRowCount() < 1) {
            return false;
        }
         return messages.get(rowIndex).isHandled();
    }

}

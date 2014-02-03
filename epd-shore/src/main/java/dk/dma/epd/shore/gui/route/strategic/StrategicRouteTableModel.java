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
package dk.dma.epd.shore.gui.route.strategic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.service.StrategicRouteHandler;
import dk.dma.epd.shore.service.StrategicRouteNegotiationData;

/**
 * Table model for Route Exchange Notifications
 */
public class StrategicRouteTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private static final String[] COLUMN_NAMES = { "Name", "Callsign",
            "Called", "Status" };

    private StrategicRouteHandler strategicRouteHandler;
    private AisHandler aisHandler;

    private List<StrategicRouteNegotiationData> messages = new ArrayList<StrategicRouteNegotiationData>();

    /**
     * Constructor for creating the msi table model
     * 
     * @param msiHandler
     */
    public StrategicRouteTableModel() {
        super();
        updateMessages();
    }

    public void setAisHandler(AisHandler aisHandler) {
        this.aisHandler = aisHandler;
    }
    
    public void setStrategicRouteHandler(StrategicRouteHandler strategicRouteHandler) {
        this.strategicRouteHandler = strategicRouteHandler;
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

    /**
     * Return the column names
     */
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    /**
     * Return messages
     * 
     * @return
     */
    public List<StrategicRouteNegotiationData> getMessages() {
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
        StrategicRouteNegotiationData message = messages.get(rowIndex);

        switch (columnIndex) {
        case 0:
            if (aisHandler != null) {
                VesselStaticData staticData = aisHandler.getVesselTarget(message.getMmsi()).getStaticData();
                if (staticData != null) {
                    return staticData.getName().trim();
                } else {
                    return message.getMmsi();
                }
            } else {
                return message.getMmsi();
            }
        case 1:
            if (aisHandler != null) {
                VesselStaticData staticData = aisHandler.getVesselTarget(message.getMmsi()).getStaticData();
                if (staticData != null) {
                    return staticData.getCallsign();
                } else {
                    return "N/A";
                }
            } else {
                return "N/A";
            }
        case 2:
            return Formatter.formatShortDateTime(message.getRouteMessage()
                    .get(0).getSent());
        case 3:
            return message.getStatus();
        default:
            return "";

        }
    }

    /**
     * Update messages
     */
    public void updateMessages() {
        if (strategicRouteHandler != null){
            
        
        messages.clear();

        for (Iterator<StrategicRouteNegotiationData> it = strategicRouteHandler
                .getStrategicNegotiationData().values().iterator(); it.hasNext();) {
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

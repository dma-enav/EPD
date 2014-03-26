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
package dk.dma.epd.shore.gui.route;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.service.RouteSuggestionHandler;

/**
 * Table model for Route Exchange Notifications
 */
public class RouteExchangeTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private static final String[] AREA_COLUMN_NAMES = { "ID", "MMSI",
            "Route Name", "Sent Date", "Sender", "Message", "Status",
            "Reply Sent", "Message" };
    private static final String[] COLUMN_NAMES = { "ID", "MMSI", "Route Name",
            "Status" };

    private RouteSuggestionHandler routeSuggestionHandler;

    private List<RouteSuggestionData> messages = new ArrayList<RouteSuggestionData>();

    /**
     * Constructor for creating the msi table model
     * 
     * @param msiHandler
     */
    public RouteExchangeTableModel(RouteSuggestionHandler routeSuggestionHandler) {
        super();
        this.routeSuggestionHandler = routeSuggestionHandler;
        updateMessages();
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

    public int areaGetColumnCount() {
        return AREA_COLUMN_NAMES.length;
    }

    /**
     * Return the column names
     */
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    public String areaGetColumnName(int column) {
        return AREA_COLUMN_NAMES[column];
    }

    /**
     * Return messages
     * 
     * @return
     */
    public List<RouteSuggestionData> getMessages() {
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
        RouteSuggestionData message = messages.get(rowIndex);

        switch (columnIndex) {
        case 0:
            return message.getId();
        case 1:
            return "" + message.getMmsi();
        case 2:
            return message.getMessage().getRoute().getName();
        case 3:
            return message.getStatus().toString();
        default:
            return "";

        }
    }

    public Object areaGetValueAt(int rowIndex, int columnIndex) {
        if (rowIndex == -1 || this.getRowCount() < 1) {
            return "";
        }
        RouteSuggestionData message = messages.get(rowIndex);

        switch (columnIndex) {
        case 0:
            return message.getId();
        case 1:
            return message.getMmsi();
        case 2:
            return message.getMessage().getRoute().getName();
        case 3:
            return Formatter.formatShortDateTime(message.getMessage()
                    .getSentDate());
        case 4:
            return message.getMessage().getSender();
        case 5:
            return message.getMessage().getMessage();
        case 6:
            return message.getStatus().toString();
        case 7:
            if (message.getReply() != null) {
                return Formatter.formatShortDateTime(message.getReply().getSentDate());
            } else {
                return "No reply received yet";
            }
        case 8:
            if (message.getReply() != null){
                return message.getReply().getMessage();
            }else{
                return "No reply received yet";
            }
        default:
            return "";
        }
    }

    /**
     * Update messages
     */
    public void updateMessages() {
        messages.clear();
        for (RouteSuggestionData data : routeSuggestionHandler.getSortedRouteSuggestions()) {
            messages.add(data);
        }
    }

    public boolean isAwk(int rowIndex) {
        if (rowIndex == -1 || this.getRowCount() < 1) {
            return false;
        }
        return messages.get(rowIndex).isAcknowleged();
        // return false;
    }

}

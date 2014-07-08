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
package dk.dma.epd.shore.gui.msi;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.dma.epd.common.text.Formatter;
import dk.frv.enav.common.xml.msi.MsiLocation;

/**
 * Table model for MSI dialog
 */
public class MsiTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private static final String[] AREA_COLUMN_NAMES = { "ID", "Ver", "Priority", "Updated", "Main Area", "Message", "Valid from", "Valid until" };
    private static final String[] COLUMN_NAMES = { "ID", "Priority", "Updated", "Main Area" };

    private MsiHandler msiHandler;
    private List<MsiMessageExtended> messages;
    private boolean filtered;

    /**
     * Constructor for creating the msi table model
     * @param msiHandler
     */
    public MsiTableModel(MsiHandler msiHandler) {
        super();
        this.msiHandler = msiHandler;
        updateMessages();
    }

    public Position getMessageLatLon(int rowIndex){
        return messages.get(rowIndex).msiMessage.getLocation().getCenter();
    }

    public boolean isAwk(int rowIndex){
        if(rowIndex == -1){
            return false;
        }
        return messages.get(rowIndex).acknowledged;
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
    public List<MsiMessageExtended> getMessages() {
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
        if(rowIndex == -1) {
            return "";
        }
        MsiMessageExtended message = messages.get(rowIndex);

        switch (columnIndex) {
        case 0:
            return message.msiMessage.getId();
        case 1:
            return message.msiMessage.getPriority();
        case 2:
            Date updated = message.msiMessage.getUpdated();
            if (updated == null) {
                updated = message.msiMessage.getCreated();
            }
            return Formatter.formatShortDateTime(updated);
        case 3:
            MsiLocation location = message.msiMessage.getLocation();
            if (location != null) {
                return location.getArea();
            }
            return "";
        default:
            return "";

        }
    }

    public Object areaGetValueAt(int rowIndex, int columnIndex) {
        if(rowIndex == -1) {
            return "";
        }
        MsiMessageExtended message = messages.get(rowIndex);

        switch (columnIndex) {
        case 0:
            return message.msiMessage.getId();
        case 1:
            return message.msiMessage.getVersion();
        case 2:
            return message.msiMessage.getPriority();
        case 3:
            Date updated = message.msiMessage.getUpdated();
            if (updated == null) {
                updated = message.msiMessage.getCreated();
            }
            return Formatter.formatShortDateTime(updated);
        case 4:
            MsiLocation location = message.msiMessage.getLocation();
            if (location != null) {
                return location.getArea();
            }
            return "";
        case 5:
            String msgShort = message.msiMessage.getMessage();
            if (msgShort == null) {
                msgShort = "";
            }
            // if (msgShort.length() > 32) {
            // msgShort = msgShort.substring(0, 28) + " ...";
            // }
            return msgShort;
        case 6:
            return Formatter.formatShortDateTime(message.msiMessage.getValidFrom());
        case 7:
            return Formatter.formatShortDateTime(message.msiMessage.getValidTo());
        default:
            return "";

        }
    }

    /**
     * Update messages
     */
    public void updateMessages() {
        // Is filtered or not?
        if (filtered) {
            messages = msiHandler.getFilteredMessageList();
        } else {
            messages = msiHandler.getMessageList();
        }
    }

}

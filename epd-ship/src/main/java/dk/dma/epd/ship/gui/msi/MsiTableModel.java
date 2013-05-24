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

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.EPDShip;
import dk.frv.enav.common.xml.msi.MsiLocation;

/**
 * Table model for MSI dialog
 */
public class MsiTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    
    private static final String[] COLUMN_NAMES = {"ID", "Ver", "Priority", "Updated", "Main Area", "Message", "Valid from", "Valid until"};
    
    private MsiHandler msiHandler;
    private List<MsiMessageExtended> messages;
    
    public MsiTableModel(MsiHandler msiHandler) {
        super();
        this.msiHandler = msiHandler;
        updateMessages();
    }
    
    public void updateMessages() {
        if(EPDShip.getSettings().getEnavSettings().isMsiFilter()) {
            messages = msiHandler.getFilteredMessageList();
        } else {
            messages = msiHandler.getMessageList();
        }
    }
    
    public List<MsiMessageExtended> getMessages() {
        return messages;
    }
    
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public int getRowCount() {
        return messages.size();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Object value = getValueAt(0, columnIndex);
        if (value == null) {
            return String.class;
        }
        return value.getClass();
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
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
            if (msgShort.length() > 32) {
                msgShort = msgShort.substring(0, 28) + " ...";
            }
            return msgShort;
        case 6:
            return Formatter.formatShortDateTime(message.msiMessage.getValidFrom());
        case 7:
            return Formatter.formatShortDateTime(message.msiMessage.getValidTo());
        default:
            return "";
                
        }
    }
    
}

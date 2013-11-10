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

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import dk.dma.epd.common.prototype.model.voct.sardata.EffortAllocationData;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.voct.SRU;
import dk.dma.epd.shore.voct.SRUCommunicationObject;
import dk.dma.epd.shore.voct.SRUManager;
import dk.dma.epd.shore.voct.VOCTManager;
import dk.dma.epd.shore.voct.SRU.sru_status;



/**
 * Table model for SRUManagerDialog
 */
public class SARTrackingTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(SARTrackingTableModel.class);

    private static final String[] COLUMN_NAMES = {"Name", "Status", "Last Msg", "Visible"};

    private SRUManager sruManager;
    private VOCTManager voctManager;

    public SARTrackingTableModel(SRUManager sruManager, VOCTManager voctManager) {
        super();
        this.sruManager = sruManager;
        this.voctManager = voctManager;
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
        return sruManager.getAvailableSRUS();
//        return 0;
//        return voctManager.getSarData().getEffortAllocationData().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
//        SRU sru = sruManager.getsRUCommunication().get(rowIndex);
        SRUCommunicationObject sruobject = sruManager.getSRUCommunicationList().get(rowIndex);
//        EffortAllocationData effortAllocationData = voctManager.getSarData().getEffortAllocationData().get(rowIndex);
        switch (columnIndex) {
        case 0: return Formatter.formatString(sruobject.getSru().getName());
        case 1: return sruobject.getSru().getStatus();
        case 2: return "N/A";
        case 3: return false;
        default:
            LOG.error("Unknown column " + columnIndex);
            return new String("");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        System.out.println("Set value at, aValue: " + aValue + " rowIndex: "+ rowIndex + " columIndex: " + columnIndex);
        SRU sru = sruManager.getSRUs().get(rowIndex);
        switch (columnIndex) {
        case 3:
            
//            sru.setVisible((Boolean)aValue);
//            sruManager.toggleSRUVisiblity(rowIndex, (Boolean)aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
            break;
        default:
            break;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        //return (columnIndex == 2 && rowIndex != routeManager.getActiveRouteIndex());
        return columnIndex == 2;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

}

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

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.voct.SRU;
import dk.dma.epd.shore.voct.SRUManager;
import dk.dma.epd.shore.voct.VOCTManager;

/**
 * Table model for SRUManagerDialog
 */
public class EffortAllocationWindowTabelModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(EffortAllocationWindowTabelModel.class);

    private static final String[] COLUMN_NAMES = { "Name", "Type", "Calculate" };

    private SRUManager sruManager;
    private VOCTManager voctManager;

    private List<Boolean> calculate = new ArrayList<Boolean>();

    public EffortAllocationWindowTabelModel(SRUManager sruManager,
            VOCTManager voctManager) {
        super();
        this.sruManager = sruManager;
        this.voctManager = voctManager;

        updateCalculateTable();
    }

    public void updateCalculateTable() {
        calculate.clear();
        for (int i = 0; i < sruManager.getSRUs().size(); i++) {
            if (voctManager.getSarData().getEffortAllocationData().size() > i) {
                calculate.add(false);
            } else {
                calculate.add(true);
            }
        }
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
        // return sruManager.getSRUCount();
        // return 0;
        return sruManager.getSRUs().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SRU sru = sruManager.getSRUs().get(rowIndex);

        switch (columnIndex) {
        case 0:
            return Formatter.formatString(sru.getName());
        case 1:
            return sru.getType();
        case 2:
            return calculate.get(rowIndex);
        default:
            LOG.error("Unknown column " + columnIndex);
            return new String("");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        System.out.println("Set value at, aValue: " + aValue + " rowIndex: "
                + rowIndex + " columIndex: " + columnIndex);
        // SRU sru = sruManager.getSRUs().get(rowIndex);
        switch (columnIndex) {
        case 2:
            
            if (voctManager.getSarData().getEffortAllocationData().size() > rowIndex){
                calculate.set(rowIndex, (Boolean) aValue);    
            }
            
            
            // super.setValueAt((Boolean) aValue, rowIndex, columnIndex);

            // sru.setVisible((Boolean)aValue);
            // sruManager.toggleSRUVisiblity(rowIndex, (Boolean)aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
            break;
        default:
            break;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // return (columnIndex == 2 && rowIndex !=
        // routeManager.getActiveRouteIndex());
        return columnIndex == 2;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

}

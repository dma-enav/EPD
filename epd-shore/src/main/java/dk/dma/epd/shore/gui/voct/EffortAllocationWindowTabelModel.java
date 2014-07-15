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
    private static final Logger LOG = LoggerFactory.getLogger(EffortAllocationWindowTabelModel.class);

    private static final String[] COLUMN_NAMES = { "Name", "Type", "Calculate" };

    private SRUManager sruManager;
    private VOCTManager voctManager;

    private List<Boolean> calculate = new ArrayList<Boolean>();

    public EffortAllocationWindowTabelModel(SRUManager sruManager, VOCTManager voctManager) {
        super();
        this.sruManager = sruManager;
        this.voctManager = voctManager;

        updateCalculateTable();
    }

    public void updateCalculateTable() {

        calculate.clear();
        for (int i = 0; i < sruManager.getSRUsAsList().length; i++) {
            if (voctManager.getSarData().getEffortAllocationData().get(sruManager.getSRUsAsList()[i]) != null) {
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
        return sruManager.getSRUsAsList().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SRU sru = sruManager.getSRUsAsList()[rowIndex];

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
        System.out.println("Set value at, aValue: " + aValue + " rowIndex: " + rowIndex + " columIndex: " + columnIndex);
        // SRU sru = sruManager.getSRUs().get(rowIndex);
        switch (columnIndex) {
        case 2:

            // if (voctManager.getSarData().getEffortAllocationData().get(sruManager.getSRUsAsList()[rowIndex]) != null) {
            calculate.set(rowIndex, (Boolean) aValue);
            // }

            // if (voctManager.getSarData().getEffortAllocationData().size() > rowIndex) {
            // calculate.set(rowIndex, (Boolean) aValue);
            // }

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

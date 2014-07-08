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
package dk.dma.epd.common.prototype.monalisa;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.text.Formatter;

/**
 * Table model for RouteManagerDialog
 */
public class MonaLisaSSPASelectionTableModel extends AbstractTableModel {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MonaLisaSSPASelectionTableModel.class);
    
    private static final String[] COLUMN_NAMES = {"Name", "Included"};
    
    private Route route;
    private List<Boolean> selectedWp;
    
    public MonaLisaSSPASelectionTableModel(Route route, List<Boolean> selectedWp) {
        super();
        this.route = route;
        this.selectedWp = selectedWp;
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
        return route.getWaypoints().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
        case 0: return Formatter.formatString(route.getWaypoints().get(rowIndex).getName());
        case 1: return selectedWp.get(rowIndex);
        default:
            LOG.error("Unknown column " + columnIndex);
            return new String("");
        }
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
//        Route route = routeManager.getRoute(routeid);     
        switch (columnIndex) {
        case 1:
            selectedWp.set(rowIndex, !selectedWp.get(rowIndex));
            fireTableCellUpdated(rowIndex, columnIndex);
            break;
        default:
            break;
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        //return (columnIndex == 2 && rowIndex != routeManager.getActiveRouteIndex());
        return columnIndex == 1;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }


    
}

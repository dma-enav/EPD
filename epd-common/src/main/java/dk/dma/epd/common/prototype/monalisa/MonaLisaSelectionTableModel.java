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
public class MonaLisaSelectionTableModel extends AbstractTableModel {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MonaLisaSelectionTableModel.class);
    
    private static final String[] COLUMN_NAMES = {"Name", "Included"};
    
    private Route route;
    private List<Boolean> selectedWp;
    
    public MonaLisaSelectionTableModel(Route route, List<Boolean> selectedWp) {
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

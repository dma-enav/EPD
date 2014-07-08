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
package dk.dma.epd.shore.gui.route;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.route.RouteManager;


/**
 * Table model for RouteManagerDialog
 */
public class RoutesTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(RoutesTableModel.class);

    private static final String[] COLUMN_NAMES = {"Name", "Destination", "Visible"};

    private RouteManager routeManager;

    public RoutesTableModel(RouteManager routeManager) {
        super();
        this.routeManager = routeManager;
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
        return routeManager.getRouteCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Route route = routeManager.getRoutes().get(rowIndex);
        switch (columnIndex) {
        case 0: return Formatter.formatString(route.getName());
        case 1: return Formatter.formatString(route.getDestination());
        case 2: return route.isVisible();
        default:
            LOG.error("Unknown column " + columnIndex);
            return new String("");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Route route = routeManager.getRoutes().get(rowIndex);
        switch (columnIndex) {
        case 2:
            route.setVisible((Boolean)aValue);
            if (rowIndex == routeManager.getActiveRouteIndex()) {
                routeManager.getActiveRoute().setVisible((Boolean)aValue);
            }
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

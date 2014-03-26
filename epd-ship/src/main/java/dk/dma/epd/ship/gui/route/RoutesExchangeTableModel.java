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
package dk.dma.epd.ship.gui.route;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionStatus;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.service.SuggestedRoute;

/**
 * Table model for RouteManagerDialog
 */
public class RoutesExchangeTableModel extends AbstractTableModel {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(RoutesExchangeTableModel.class);
    
    private static final String[] COLUMN_NAMES = {"Name", "Status", "Visible"};
    
    private RouteManager routeManager;
    
    public RoutesExchangeTableModel(RouteManager routeManager) {
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
        return routeManager.getSuggestedRoutes().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SuggestedRoute route = routeManager.getSuggestedRoutes().get(rowIndex);
        
        switch (columnIndex) {
        case 0: return Formatter.formatString(route.getRoute().getName());
        case 1: return formatRouteSuggestionStatus(route.getStatus());
        case 2: return !route.isHidden();
        default:
            LOG.error("Unknown column " + columnIndex);
            return new String("");
        }
    }
    
    private String formatRouteSuggestionStatus(RouteSuggestionStatus status) {
        switch (status) {
        case PENDING:
            return "Pending";
        case ACCEPTED:
            return "Accepted";
        case REJECTED:
            return "Rejected";
        case NOTED:
            return "Noted";
        case IGNORED:
            return "Ignored";
        default:
            return "Unknown";
        }
    }
        
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        SuggestedRoute route = routeManager.getSuggestedRoutes().get(rowIndex);        
        switch (columnIndex) {
        case 2:
            route.setHidden(!(Boolean)aValue);
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

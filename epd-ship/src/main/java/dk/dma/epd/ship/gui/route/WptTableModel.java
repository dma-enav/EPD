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
package dk.dma.epd.ship.gui.route;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.ParseUtils;
import dk.dma.epd.ship.route.RouteManager;

/**
 * Table model for the list waypoints in route RoutePropertiesDialog
 */
public class WptTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(WptTableModel.class);

    private static final String[] COLUMN_NAMES = { "Name", "Lat", "Lon", "Rng", "Brg", "Heading", "Rad", "ROT", "XTD P", "XTD S",
            "SOG", "WP TTG", "WP ETA" };

    private Route route;
    private JDialog dialog;
    private RouteManager routeManager;

    public WptTableModel(JDialog dialog, RouteManager routeManager) {
        super();
        this.dialog = dialog;
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
        return route.getWaypoints().size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (rowIndex == getRowCount() - 1) {
            return columnIndex == 0 || columnIndex == 1 || columnIndex == 2;
        } else {
            return !(columnIndex == 3 || columnIndex == 4 || columnIndex == 7 || columnIndex == 11 || columnIndex == 12);
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        RouteWaypoint wpt = route.getWaypoints().get(rowIndex);
        RouteLeg outLeg = wpt.getOutLeg();

        try {
        switch (columnIndex) {
        case 0:
            wpt.setName((String) aValue);
            break;
        case 1:
            wpt.setPos(wpt.getPos().withLatitude(parseLat((String)aValue)));
            break;
        case 2:
            wpt.setPos(wpt.getPos().withLongitude(parseLon((String)aValue)));
            break;
        case 5:
            String head = (String)aValue;
            if (head != null && head.equalsIgnoreCase("GC")) {
                outLeg.setHeading(Heading.GC);
            } else {
                outLeg.setHeading(Heading.RL);
            }
            break;
        case 6:
            wpt.setTurnRad(parseDouble((String)aValue));
            break;
        case 8:
            outLeg.setXtdPort(parseDouble((String)aValue) / 1852.0);
            break;
        case 9:
            outLeg.setXtdStarboard(parseDouble((String)aValue) / 1852.0);
            break;
        case 10:
            outLeg.setSpeed(parseDouble((String)aValue));
            break;
        }
        
        } catch (FormatException e) {
            JOptionPane.showMessageDialog(this.dialog, "Error in entered value", "Input error", JOptionPane.ERROR_MESSAGE);
            return;
        }
                
        if (route instanceof ActiveRoute) {
            ActiveRoute activeRoute = (ActiveRoute)route;
            activeRoute.calcValues(true);
            routeManager.changeActiveWp(activeRoute.getActiveWaypointIndex());
        } else {
            route.calcValues(true);
            routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_CHANGED);
        }
        fireTableDataChanged();                
    }
    
    private static double parseDouble(String str) throws FormatException {
        str = str.replaceAll(",", ".");
        String[] parts = StringUtils.split(str, " ");
        return ParseUtils.parseDouble(parts[0]);
    }
    
    private static double parseLat(String latStr) throws FormatException {
        return ParseUtils.parseLatitude(latStr);
    }
    
    private static double parseLon(String lonStr) throws FormatException {
        return ParseUtils.parseLongitude(lonStr);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // Get wpt
        RouteWaypoint wpt = route.getWaypoints().get(rowIndex);
        if (wpt == null) {
            LOG.error("Unknown WPT with id " + rowIndex);
            return new String("");
        }
        RouteLeg outLeg = wpt.getOutLeg();

        switch (columnIndex) {
        case 0:
            return Formatter.formatString(wpt.getName());
        case 1:
            return Formatter.latToPrintable(wpt.getPos().getLatitude());
        case 2:
            return Formatter.lonToPrintable(wpt.getPos().getLongitude());
        case 3:
            return Formatter.formatDistNM(route.getWpRng(rowIndex));
        case 4:
            return Formatter.formatDegrees(route.getWpBrg(wpt), 2);
        case 5:
            return Formatter.formatHeading(wpt.getHeading());
        case 6:
            return Formatter.formatDistNM(wpt.getTurnRad());
            // case 7: return Formatter.formatRot(wpt.getRot());
        case 7:
            return Formatter.formatRot(null);
        case 8:
            return Formatter.formatMeters(outLeg != null ? outLeg.getXtdPortMeters() : null);
        case 9:
            return Formatter.formatMeters(outLeg != null ? outLeg.getXtdStarboardMeters() : null);
        case 10:
            return Formatter.formatSpeed(outLeg != null ? outLeg.getSpeed() : null);
        case 11:
            return Formatter.formatTime(route.getWpTtg(rowIndex));
        case 12:
            return Formatter.formatShortDateTime(route.getWpEta(rowIndex));
        default:
            break;
        }

        return new String("");
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Object value = getValueAt(0, columnIndex);
        if (value == null) {
            return String.class;
        }
        return value.getClass();
    }

    public void setRoute(Route route) {
        this.route = route;
    }

}

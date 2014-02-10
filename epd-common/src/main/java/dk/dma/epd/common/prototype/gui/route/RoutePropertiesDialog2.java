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
package dk.dma.epd.common.prototype.gui.route;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.BOTH;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.ParseUtils;


public class RoutePropertiesDialog2 {



    public static final void main(String... args) throws Exception {

        //=====================
        // Create test data
        //=====================
        final Route route = new Route();
        final LinkedList<RouteWaypoint> waypoints = new LinkedList<>();
        route.setWaypoints(waypoints);
        route.setStarttime(new Date());

        int len = 10;
        final boolean[] locked = new boolean[len];
        for (int x = 0; x < len; x++) {
            locked[x] = false;
            RouteWaypoint wp = new RouteWaypoint();
            waypoints.add(wp);

            // Set leg values
            if (x > 0) {
                RouteLeg leg = new RouteLeg();
                leg.setSpeed(12.00 + x);
                leg.setHeading(Heading.RL);
                leg.setXtdPort(185.0);
                leg.setXtdStarboard(185.0);

                wp.setInLeg(leg);
                waypoints.get(x-1).setOutLeg(leg);
                leg.setStartWp(waypoints.get(x-1));
                leg.setEndWp(wp);
            }

            wp.setName("WP_00" + x);
            wp.setPos(Position.create(56.02505 + Math.random() * 2.0, 12.37 + Math.random() * 2.0));    
        }
        for (int x = 1; x < len; x++) {
            waypoints.get(x).setTurnRad(0.5 + x * 0.2);
        }
        route.calcValues(true);

        //=====================
        // Create UI
        //=====================
        JFrame f = new JFrame("Route table test");
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        f.setBounds(100, 100, 1000, 500);

        TableModel model = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            String[] cols = {
                    " ", "Name", "Latutide", "Longtitude", "Rad", "Rot", "TTG", "ETA", "RNG",
                    "BRG", "Heading", "SOG", "XTDS", "XTD P", "SF Width", "SFLen" };

            @Override
            public int getRowCount() {
                return waypoints.size();
            }

            @Override
            public int getColumnCount() {
                return 16;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return cols[columnIndex];
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                RouteWaypoint wp = waypoints.get(rowIndex);
                switch (columnIndex) {
                case  0: return locked[rowIndex];
                case  1: return wp.getName();
                case  2: return wp.getPos().getLatitudeAsString();
                case  3: return wp.getPos().getLongitudeAsString();
                case  4: return wp.getTurnRad() == null ? "N/A" : Formatter.formatDistNM(wp.getTurnRad());
                case  5: return "N/A";
                case  6: return Formatter.formatTime(route.getWpTtg(rowIndex));
                case  7: return Formatter.formatShortDateTimeNoTz(route.getWpEta(rowIndex));
                case  8: return Formatter.formatDistNM(route.getWpRng(rowIndex));
                case  9: return Formatter.formatDegrees(route.getWpBrg(wp), 2);
                case 10: return wp.getHeading();
                case 11: return Formatter.formatSpeed(wp.getOutLeg().getSpeed());
                case 12: return Formatter.formatMeters(wp.getOutLeg().getXtdStarboardMeters());
                case 13: return Formatter.formatMeters(wp.getOutLeg().getXtdPortMeters());
                case 14: return Formatter.formatMeters(wp.getOutLeg().getSFWidth());
                case 15: return Formatter.formatMeters(wp.getOutLeg().getSFLen());
                default: return null;
                }
            }

            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {
                try {
                    RouteWaypoint wp = waypoints.get(rowIndex);
                    switch (columnIndex) {
                    case  0: 
                        locked[rowIndex] = ((Boolean)value).booleanValue(); 
                        fireTableRowsUpdated(rowIndex, rowIndex); 
                        break;
                    case  1: wp.setName(value.toString()); break;
                    case  2: wp.setPos(Position.create(ParseUtils.parseLatitude(value.toString()), wp.getPos().getLongitude())); break;
                    case  3: wp.setPos(Position.create(wp.getPos().getLatitude(), ParseUtils.parseLatitude(value.toString()))); break;
                    case  10: wp.getOutLeg().setHeading((Heading)value); break;
                    default:
                    }
                } catch (Exception ex) {
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return (columnIndex == 0 || !locked[rowIndex]) &&
                        (columnIndex < 5 || columnIndex > 9);
            }   
        };

        final DeltaTable routeTable = new DeltaTable(model, 8);
        routeTable.setTableFont(routeTable.getTableFont().deriveFont(10.0f));
        routeTable.setNonEditableBgColor(UIManager.getColor("Table.background").darker());
        
        routeTable.addListSelectionListener(new ListSelectionListener() {
            @Override public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    System.out.println("Selection " + routeTable.getSelectedRow());
                }
            }});

        // Configure lock column
        routeTable.fixColumnWidth(0, 25);
        routeTable.getColumn(0).setCellRenderer(new LockTableCell.CustomBooleanCellRenderer());
        routeTable.getColumn(0).setCellEditor(new LockTableCell.CustomBooleanCellEditor());

        // Configure latitude column 
        //MaskFormatter mask = new MaskFormatter("## ##.###U"); // 56 37.828N
        //routeTable.getColumn(1).setCellEditor(new DefaultCellEditor(new JFormattedTextField(mask)));

        // Configure heading column
        JComboBox<Heading> headingCombo = new JComboBox<>(Heading.values());
        headingCombo.setFont(headingCombo.getFont().deriveFont(10.0f));
        routeTable.getColumn(10).setCellEditor(new DefaultCellEditor(headingCombo));


        JPanel content = new JPanel(new GridBagLayout());
        Insets insets10  = new Insets(10, 10, 10, 10);
        content.add(new JLabel("Name"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets10, 0, 0));
        content.add(routeTable, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, NORTHWEST, BOTH, insets10, 0, 0));

        f.getContentPane().add(content);


        f.setVisible(true);
    }   

}

/**
 * Helper class used to implement the lock icon
 * Somebody should punish the people designing the Swing table API...
 */
class LockTableCell extends JCheckBox {
    private static final long serialVersionUID = 1L;

    public static ImageIcon unlockedIcon = EPD.res().getCachedImageIcon("images/toolbar/lock-unlock.png");
    public static ImageIcon lockedIcon = EPD.res().getCachedImageIcon("images/toolbar/lock.png");

    public LockTableCell() {
        super();
        setOpaque(false);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            setIcon(lockedIcon);
        } else {
            setIcon(unlockedIcon);
        }
    }

    public static class CustomBooleanCellRenderer extends LockTableCell implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Boolean) {
                boolean selected = (boolean) value;
                setSelected(selected);
            }
            setBackground(UIManager.getColor(isSelected ? "Table.selectionBackground" : "Table.background"));
            return this;
        }
    }

    public static class CustomBooleanCellEditor extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = 1L;

        private LockTableCell editor = new LockTableCell();

        public CustomBooleanCellEditor() {
            super();
            editor.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }});
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof Boolean) {
                boolean selected = (boolean) value;
                editor.setSelected(selected);
                editor.setBackground(UIManager.getColor(isSelected ? "Table.selectionBackground" : "Table.background"));
            }
            return editor;
        }

        @Override
        public Object getCellEditorValue() {
            return editor.isSelected();
        }
    }
}  

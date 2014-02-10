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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import dk.dma.epd.common.graphics.GraphicsUtil;

/**
 * The {@code DeltaTable} class is initialized with a 
 * {@linkplain TableModel} and an index, {@code deltaStartColIndex},
 * which denotes the start of the delta columns.
 * <p>
 * Internally, {@code DeltaTable} manages two {@linkplain JTable} tables,
 * A and B, where A displays the first {@code deltaStartColIndex} columns
 * and B displays the remaining columns.
 * <p>
 * The rows of table B are offset vertically by half a row height, and it
 * has one row less than table A.
 */
public class DeltaTable extends JPanel {

    private static final long serialVersionUID = 1L;

    List<ListSelectionListener> selectionListeners = new CopyOnWriteArrayList<>();

    JTable table1 = new JTable();
    JTable table2 = new JTable();
    JTable[] tables = { table1, table2 };
    JTextField defaultCellEditor = new JTextField();

    TableModel model;
    int deltaStartColIndex;
    Color nonEditableBgColor;

    JScrollPane scrollPane1 = new JScrollPane(table1, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    JScrollPane scrollPane2 = new JScrollPane(table2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    JScrollPane[] scrollPanes = { scrollPane1, scrollPane2 };

    /**
     * Constructor
     * 
     * @param rowClass
     * @param deltaRowClass
     */
    public DeltaTable(TableModel model, int deltaStartColIndex) {
        //super(new GridBagLayout());

        this.model = model;
        this.deltaStartColIndex = deltaStartColIndex;


        table1.setModel(new DeltaTableModel(false));
        table2.setModel(new DeltaTableModel(true));

        setBorder(BorderFactory.createLineBorder(Color.darkGray));

        for (JTable table : tables) {
            final JTable t = table;
            //table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
            table.setDefaultEditor(Object.class, new DefaultCellEditor(defaultCellEditor));
            table.setDefaultRenderer(Object.class, new DeltaCellRenderer(table));
            table.setIntercellSpacing(new Dimension(0, 1));
            table.setShowHorizontalLines(false);
            table.setShowVerticalLines(false);
            table.setFillsViewportHeight(true);

            // Handle shifting cell focus between the tables
            table.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "focusNextCell");
            table.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "focusNextCell");
            table.getActionMap().put("focusNextCell", new ShiftFocusAction(true));  
            table.getInputMap().put(KeyStroke.getKeyStroke("shift TAB"), "focusPrevCell");
            table.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "focusPrevCell");
            table.getActionMap().put("focusPrevCell", new ShiftFocusAction(false));
            table.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "editCell");
            table.getActionMap().put("editCell", new AbstractAction() { 
                private static final long serialVersionUID = 1L;
                @Override public void actionPerformed(ActionEvent e) {
                    t.editCellAt(t.getSelectedRow(), t.getSelectedColumn());
                }});
        }
        // Add table header that offsets rows with half a rows height
        table2.setTableHeader(new DeltaTableHeader(table2));
        table1.getTableHeader().setReorderingAllowed(false);
        table2.getTableHeader().setReorderingAllowed(false);

        // Synchronize the row selection between the two tables
        new SyncSelectionModel(table1, table2, true);
        new SyncSelectionModel(table2, table1, false);

        //Insets insets  = new Insets(0, 0, 0, 0);
        for (int x = 0; x < scrollPanes.length; x++) {
            scrollPanes[x].setBorder(BorderFactory.createEmptyBorder());
            //add(scrollPanes[x], new GridBagConstraints(x, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, insets, 0, 0));
        }
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(scrollPanes[0]);
        add(scrollPanes[1]);

        // Let scrollPane2 scroll both tables
        scrollPane2.getVerticalScrollBar().setModel(scrollPane1.getVerticalScrollBar().getModel());

        // Add extra height to scrollPane2 for missing row
        Dimension d = table2.getPreferredSize();
        table2.setPreferredSize(new Dimension(d.width, d.height + table2.getRowHeight() / 2));

        // Add resizing between the tables
        ResizeTablesMouseAdapter tableResizer = new ResizeTablesMouseAdapter();
        table1.getTableHeader().addMouseListener(tableResizer);
        table1.getTableHeader().addMouseMotionListener(tableResizer);
    }


    /**
     * Returns the table model
     * @return the table model
     */
    public TableModel getModel() {
        return model;
    }

    /**
     * Returns the table column for the given column index
     * @param columnIndex the column index
     * @return the table column
     */
    public TableColumn getColumn(int columnIndex) {
        if (columnIndex < deltaStartColIndex) {
            return table1.getColumnModel().getColumn(columnIndex);
        }
        return table2.getColumnModel().getColumn(columnIndex - deltaStartColIndex);
    }

    /**
     * Fixes the width of the given column
     * @param columnIndex the column
     * @param width the width
     */
    public void fixColumnWidth(int columnIndex, int width) {
        TableColumn col = getColumn(columnIndex);
        col.setWidth(width);
        col.setMaxWidth(width);
        col.setMinWidth(width);
    }

    /**
     * Adds a list selection listener
     * @param listener the listener to add
     */
    public void addListSelectionListener(ListSelectionListener listener) {
        selectionListeners.add(listener);
    }

    /**
     * Removes a list selection listener
     * @param listener the listener to remove
     */
    public void removeListSelectionListener(ListSelectionListener listener) {
        selectionListeners.remove(listener);
    }

    /**
     * Returns the number of rows
     * @return the number of rows
     */
    public int getRowCount() {
        return table1.getRowCount();
    }

    /**
     * Returns the index of the selected row, -1 if no row is selected.
     * @return the index of the selected row
     */
    public int getSelectedRow() {
        return table1.getSelectedRow();
    }

    /**
     * Returns the font of the table
     * @return the font of the table
     */
    public Font getTableFont() {
        return table1.getFont();
    }

    /**
     * Sets the font of the table
     * @param font the font to set
     */
    public void setTableFont(Font font) {
        table1.setFont(font);
        table2.setFont(font);
        defaultCellEditor.setFont(font);
    }

    /**
     * Returns the background color to use for cells that are not editable
     * @return the background color to use for cells that are not editable
     */
    public Color getNonEditableBgColor() {
        return nonEditableBgColor;
    }

    /** 
     * Sets the background color to use for cells that are not editable
     * @param nonEditableBgColor the background color to use for cells that are not editable
     */
    public void setNonEditableBgColor(Color nonEditableBgColor) {
        this.nonEditableBgColor = nonEditableBgColor;
    }

    
    /****************************************************/
    /** Helper classes                                 **/
    /****************************************************/

    /**
     * Wrapper table model that handles either the normal rows
     * or the delta rows
     */
    class DeltaTableModel implements TableModel {

        boolean deltaTable;

        public DeltaTableModel(boolean deltaTable) {
            this.deltaTable = deltaTable;
        }

        public int col(int col) {
            return deltaTable ? deltaStartColIndex + col : col;
        }

        @Override
        public int getRowCount() {
            return deltaTable ? model.getRowCount() - 1 : model.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return deltaTable ? model.getColumnCount() - deltaStartColIndex : deltaStartColIndex;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return model.getColumnName(col(columnIndex));
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return model.getColumnClass(col(columnIndex));
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return model.isCellEditable(rowIndex, col(columnIndex));
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return model.getValueAt(rowIndex, col(columnIndex));
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            model.setValueAt(aValue, rowIndex, col(columnIndex));
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            model.addTableModelListener(l); 
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            model.removeTableModelListener(l);
        }
    }

    /**
     * Paints a disabled background.
     * <p>
     * This takes effect if the {@code nonEditableBgColor} has been set
     */
    class DeltaCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        JTable table;

        public DeltaCellRenderer(JTable table) {
            super();
            this.table = table;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value,isSelected, hasFocus, row, column);
            Color bgColor = UIManager.getColor(isSelected ? "Table.selectionBackground" : "Table.background");
            if (nonEditableBgColor != null && !table.getModel().isCellEditable(row, column)) {
                c.setBackground(GraphicsUtil.blendColors(nonEditableBgColor, bgColor));
            } else {
                c.setBackground(bgColor);    
            }
            return c;
        }
    }

    /**
     * Handles changing focus from one table to the next.
     * <p>
     * Depending on the {@code rightBound} constructor parameter,
     * it either handles right-bound or left-bound focus shifts.
     */
    class ShiftFocusAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        boolean rightBound;

        public ShiftFocusAction(boolean rightBound) {
            this.rightBound = rightBound;
        }

        @Override public void actionPerformed(ActionEvent evt) {
            JTable tbl = (table1.isFocusOwner()) ? table1 : table2;
            int row = tbl.getSelectedRow();
            int col = tbl.getSelectedColumn();
            int nextCol = col + (rightBound ? 1 : -1); 

            if (nextCol >= 0 && nextCol < tbl.getColumnCount()) {
                tbl.changeSelection(row, nextCol, false, false);

            } else if (tbl == table2) {
                table1.requestFocus();
                if (nextCol < 0) {
                    table1.changeSelection(row, table1.getColumnCount() - 1, false, false);
                } else if (row < table1.getRowCount() - 1) {
                    table1.changeSelection(row + 1, 0, false, false);
                }

            } else {
                if (nextCol < 0 && row > 0) {
                    table2.requestFocus();
                    table2.changeSelection(row - 1, table2.getColumnCount() - 1, false, false);
                } else if (nextCol >= tbl.getColumnCount() && row >= table2.getRowCount()) {
                    table1.changeSelection(0, 0, false, false);
                } else if (nextCol >= tbl.getColumnCount()) {
                    table2.requestFocus();
                    table2.changeSelection(row, 0, false, false);
                }
            }
        }

    }

    /**
     * A list selection listener that keeps the selection in 
     * two tables in sync.
     */
    class SyncSelectionModel implements ListSelectionListener {
        JTable tbl1, tbl2;
        boolean propagateSelection;

        SyncSelectionModel(JTable table1, JTable table2, boolean propagateSelection) {
            this.tbl1 = table1;
            this.tbl2 = table2;
            this.propagateSelection = propagateSelection;
            table1.getSelectionModel().addListSelectionListener(this);
        }

        @Override public void valueChanged(ListSelectionEvent e) {
            int row1 = tbl1.getSelectedRow();
            int row2 = tbl2.getSelectedRow();
            if (row1 > tbl2.getRowCount() - 1) {
                tbl2.getSelectionModel().clearSelection();
            } else if (row1 != -1 && row1 != row2) {
                tbl2.setRowSelectionInterval(row1, row1);
            }
            if (propagateSelection) {
                for (ListSelectionListener listener : selectionListeners) {
                    listener.valueChanged(e);
                }
            }
        }
    }

    /**
     * Table header class that offsets the table rows vertically
     * with half a row height.
     */
    class DeltaTableHeader extends JTableHeader {
        private static final long serialVersionUID = 1L;
        int deltaHeight;

        public DeltaTableHeader(JTable table) {
            super(table.getColumnModel());
            deltaHeight = table.getRowHeight() / 2;
            final TableCellRenderer tcr = table2.getTableHeader().getDefaultRenderer();
            setDefaultRenderer(new TableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable table, 
                        Object value, boolean isSelected, boolean hasFocus, 
                        int row, int column) {
                    JLabel lbl = (JLabel) tcr.getTableCellRendererComponent(table, 
                            value, isSelected, hasFocus, row, column);
                    lbl.setVerticalAlignment(SwingConstants.TOP);
                    return lbl;
                }});    
        }

        @Override 
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = d.height + deltaHeight;
            return d;
        }
    }

    /**
     * Adds resizing behavior between the tables.
     * <p>
     * If you click and drag the upper right corner
     * of the table1 header, table1 and table2 are
     * resized to make it appear as if the last column
     * of table1 is resized.
     */
    class ResizeTablesMouseAdapter extends MouseAdapter {
        Point point;

        @Override
        public void mousePressed(MouseEvent e) {
            // Only kick in if the user clicked upper right border
            if (e.getY() < 20 && e.getX() > table1.getWidth() - 5) {
                point = new Point(e.getX(), e.getY());
            } else {
                point = null;
            }
        }

        @Override
        public void mouseDragged(MouseEvent e){
            if (point != null) {
                int dx = e.getX() - point.x;
                Dimension d1 = scrollPane1.getSize();
                Dimension d2 = scrollPane2.getSize();
                scrollPane1.setPreferredSize(new Dimension(d1.width + dx, d1.height));
                scrollPane2.setPreferredSize(new Dimension(d2.width - dx, d2.height));
                revalidate();
                point = new Point(e.getX(), e.getY());
            }
        }
    }
}

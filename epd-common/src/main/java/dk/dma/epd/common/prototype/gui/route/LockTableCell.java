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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import dk.dma.epd.common.prototype.EPD;

/**
 * Helper class used to implement the lock icon.
 * <p>
 * The class defines a {@code CustomBooleanCellRenderer} renderer and 
 * a {@code CustomBooleanCellEditor} editor that may be applied to
 * the relevant lock table column.
 */
class LockTableCell extends JCheckBox {
    private static final long serialVersionUID = 1L;

    public static ImageIcon unlockedIcon = EPD.res().getCachedImageIcon("images/toolbar/lock-unlock.png");
    public static ImageIcon lockedIcon = EPD.res().getCachedImageIcon("images/toolbar/lock.png");

    /**
     * Constructor
     */
    public LockTableCell() {
        super();
        setOpaque(false);
    }

    /**
     * Sets the icon to be displayed for the selected state
     * @param selected the selected state
     */
    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            setIcon(lockedIcon);
        } else {
            setIcon(unlockedIcon);
        }
    }

    /**
     * A {@linkplain TableCellRenderer} for the {@code LockTableCell} widget
     */
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

    /**
     * A {@linkplain TableCellEditor} for the {@code LockTableCell} widget
     */
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

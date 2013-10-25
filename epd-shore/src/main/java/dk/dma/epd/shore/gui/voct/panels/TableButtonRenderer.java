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
package dk.dma.epd.shore.gui.voct.panels;

import java.awt.Component;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class TableButtonRenderer extends AbstractCellEditor
  implements TableCellRenderer, TableCellEditor
{
  private Map<String, JButton> renderButtons = new WeakHashMap<String, JButton>();

  public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column)
  {
    JButton button = (JButton)value;
    JButton renderButton = renderButtons.get(button.getText());

    if (renderButton == null)
    {
      renderButton = new JButton(button.getText());
      renderButtons.put(button.getText(), renderButton);
    }
    
    return renderButton;
  }

  public Object getCellEditorValue()
  {
    return null;
  }

  public Component getTableCellEditorComponent(JTable table, Object value,
    boolean isSelected, int row, int column)
  {
    return (JButton)value;
  }
}

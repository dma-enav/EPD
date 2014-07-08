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
package dk.dma.epd.shore.gui.voct.panels;

import java.awt.Component;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class TableButtonRenderer extends AbstractCellEditor implements
        TableCellRenderer, TableCellEditor {

    private static final long serialVersionUID = 1L;
    private Map<String, JButton> renderButtons = new WeakHashMap<String, JButton>();

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JButton button = (JButton) value;
        JButton renderButton = renderButtons.get(button.getText());

        if (renderButton == null) {
            renderButton = new JButton(button.getText());
            renderButtons.put(button.getText(), renderButton);
        }

        return renderButton;
    }

    public Object getCellEditorValue() {
        return null;
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        return (JButton) value;
    }
}

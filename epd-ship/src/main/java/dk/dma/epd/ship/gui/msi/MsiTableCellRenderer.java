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
package dk.dma.epd.ship.gui.msi;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.dma.epd.ship.EPDShip;

/**
 * Cell coloring for MSI messages
 */
public class MsiTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;
    
    private MsiHandler msiHandler;
    
    public MsiTableCellRenderer(MsiHandler msiHandler) {
        this.msiHandler = msiHandler;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (column == 0) {
            boolean acked = false;
            if (row >= 0) {
                MsiMessageExtended msg;
                if(EPDShip.getInstance().getSettings().getEnavSettings().isMsiFilter()) {
                    msg = msiHandler.getFilteredMessageList().get(row);
                } else {
                    msg = msiHandler.getMessageList().get(row);
                }
                if (msg != null) {
                    acked = msg.acknowledged;
                }
            }
            
            if (!acked) {
                if (isSelected) {
                    cell.setBackground(new Color(128, 0, 0));
                } else {
                    cell.setBackground(Color.RED);
                }
                cell.setForeground(Color.WHITE);
            } else {
                if (isSelected) {
                    cell.setBackground(new Color(0, 128, 0));
                } else {
                    cell.setBackground(Color.GREEN);
                }
                cell.setForeground(Color.WHITE);
            }
        }
        
        return this;
    }

}

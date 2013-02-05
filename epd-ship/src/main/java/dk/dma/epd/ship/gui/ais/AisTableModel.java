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
package dk.dma.epd.ship.gui.ais;

import java.text.NumberFormat;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.ais.AisHandler.AisMessageExtended;

/**
 * Table model for MSI dialog
 */
public class AisTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    
    private static final String[] COLUMN_NAMES = {"Name", "MMSI", "HDG", "DST"};
    
    private AisHandler aisHandler;
    private List<AisHandler.AisMessageExtended> ships;
    
    
    public AisTableModel(AisHandler aisHandler) {
        super();
        this.aisHandler = aisHandler;
    }
    
    public void updateShips() {
        //Get new list from store/handler
        ships = aisHandler.getShipList();
    }
    
    public List<AisMessageExtended> getShips() {
        if (ships != null) {
        return ships;
        }
        else{
            //updateShips();
            return ships;
        }
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
        if (ships == null) {
            updateShips();
        }
        return ships.size();
        //return 0;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Object value = getValueAt(0, columnIndex);
        if (value == null) {
            return String.class;
        }
        return value.getClass();
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= ships.size()) {
            return null;
        }            
        
        AisMessageExtended ship = ships.get(rowIndex);

        switch (columnIndex) {
        case 0:
            //return "Name"; 
            return ship.name;
        case 1:
            //return "MMSI";
            return ship.MMSI;
        case 2:
            //return "???";
            return ship.hdg;
        case 3:
              NumberFormat nf = NumberFormat.getInstance();  
              nf.setMaximumFractionDigits(2);// set as you need  
              //String dst = nf.format(ship.dst);  
            return ship.dst;
            //return "DST";
        default:
            return "";
                
        }
    }
    
}

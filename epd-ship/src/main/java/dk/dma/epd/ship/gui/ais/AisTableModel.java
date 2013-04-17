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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.ais.AisHandler.AisMessageExtended;

/**
 * Table model for MSI dialog
 */
public class AisTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    
    private static final String[] COLUMN_NAMES = {"Name", "MMSI", "HDG", "DST"};
    
    private AisHandler aisHandler;
    private List<AisMessageExtended> ships;
    private ConcurrentLinkedQueue<VesselTarget> queue;
    
    class UpdateShipQueueWorker extends SwingWorker<Void,Void> {
        private AisTableModel model;
        private ConcurrentLinkedQueue<VesselTarget>queue;
        
        UpdateShipQueueWorker(AisTableModel model, ConcurrentLinkedQueue<VesselTarget>queue) {
            this.model = model;
            this.queue = queue;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            while (true) {
                VesselTarget vt = this.queue.poll();
                
                if (vt == null) {
                    //System.out.println("DONE WITH QUEUE");
                    Thread.sleep(2000);
                    
                } else {
                    //System.out.println(this.queue.size());
                    assert (this.queue.size() < 2000);
                    this.model.updateShip(vt);
                }
            }
        }
        
    }
    private UpdateShipQueueWorker worker;
    
    public AisTableModel(AisHandler aisHandler) {
        super();
        this.aisHandler = aisHandler;
        
        ships = new ArrayList<AisHandler.AisMessageExtended>();
        queue = new ConcurrentLinkedQueue<VesselTarget>();
        
        worker = new UpdateShipQueueWorker(this, this.queue);
        worker.execute();
        
    }
    
    public void queueShip(VesselTarget vesselTarget) {
        queue.add(vesselTarget);
    }
    
    public synchronized void updateShips() {
        List<AisMessageExtended>shipsList = aisHandler.getShipList();
        
        ships.clear();
        ships.addAll(shipsList);
        fireTableDataChanged();

        
    }
    public synchronized void updateShip(VesselTarget aisTarget) {
        //still takes O(n), but only updates a single target

        final Long mmsi = aisTarget.getMmsi();
        int count = 0;
        List<AisMessageExtended> ships = getShips();
        for (AisMessageExtended ship: ships) {
            if (mmsi == ship.MMSI) {
                AisMessageExtended s = aisHandler.getShip(aisTarget);
                ships.set(count, s);
                fireTableRowsUpdated(count, count);

            }
            count++;
        }            
    
        //if ship was not found in the list, add it
        this.addRow(aisTarget);
        
    }
    
    private void addRow(VesselTarget aisTarget) {
        AisMessageExtended s = aisHandler.getShip(aisTarget);        
        this.ships.add(s);
        fireTableRowsInserted(ships.size()-1, ships.size()-1);

    }
    
    public List<AisMessageExtended> getShips() {
        return ships;
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
    public synchronized Object getValueAt(int rowIndex, int columnIndex) {

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



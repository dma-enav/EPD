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
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import dk.dma.epd.common.prototype.ais.AisHandlerCommon.AisMessageExtended;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ais.AisHandler;

/**
 * Table model for MSI dialog
 */
public class AisTableModel extends AbstractTableModel implements IAisTargetListener {
    
    private static final long serialVersionUID = 1L;

    private static final String[] COLUMN_NAMES = { "Name", "MMSI", "HDG", "DST" };

    private AisHandler aisHandler;
    
    /**
     * The model data.<br/>
     * <b>IMPORTANT:</b> Always use {@link #addVessel(VesselTarget)} and {@link #deleteVessel(Long)} when modifying this list (to keep {@link #mappedItems} in sync).
     */
    private List<AisMessageExtended> items = new ArrayList<>();
    
    /**
     * Provides quick lookup of model items by MMSI.
     * The value-set of this map is the same set of objects as contained in {@link #items}.<br/>
     * <b>IMPORTANT:</b> Always use {@link #addVessel(VesselTarget)} and {@link #deleteVessel(Long)} when modifying this map (to keep {@link #items} in sync).
     */
    private ConcurrentHashMap<Long, AisMessageExtended> mappedItems = new ConcurrentHashMap<>();
    
    public AisTableModel(AisHandler aisHandler) {
        super();
        this.aisHandler = aisHandler;
        // TODO where to remove listener?
        this.aisHandler.addListener(this);
        // TODO where to stop thread?
//        publisher.start();

//        ships = new ArrayList<AisHandler.AisMessageExtended>();
//        queue = new ConcurrentLinkedQueue<VesselTarget>();

//        worker = new UpdateShipQueueWorker(this, this.queue);
//        worker.execute();

    }

//    public void queueShip(VesselTarget vesselTarget) {
//        queue.add(vesselTarget);
//    }

//    public synchronized void updateShips() {
//        List<AisMessageExtended> shipsList = aisHandler.getShipList();
//
//        ships.clear();
//        ships.addAll(shipsList);
//        fireTableDataChanged();
//
//    }

//    public synchronized void updateShip(VesselTarget aisTarget) {
//        // still takes O(n), but only updates a single target
//
//        
//        
//        final Long mmsi = aisTarget.getMmsi();
//        int count = 0;
//        List<AisMessageExtended> ships = getShips();
//        for (AisMessageExtended ship : ships) {
//            if (mmsi == ship.MMSI) {
//                AisMessageExtended s = aisHandler.getShip(aisTarget, EPDShip.getInstance().getPntHandler().getCurrentData());
//                ships.set(count, s);
//                fireTableRowsUpdated(count, count);
//                return;
//            }
//            count++;
//        }
//
//        // if ship was not found in the list, add it
//        this.addRow(aisTarget);
//
//    }

//    private void addRow(VesselTarget aisTarget) {
//        AisMessageExtended s = aisHandler.getShip(aisTarget, EPDShip.getInstance().getPntHandler().getCurrentData());
//        this.ships.add(s);
//        fireTableRowsInserted(ships.size() - 1, ships.size() - 1);
//
//    }

//    public List<AisMessageExtended> getShips() {
//        return ships;
//    }

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
        if(!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("getRowCount should only be called on EDT");
        }
//        return ships.size();
        // return 0;
//        try {
//            itemsLock.readLock().lock();
            return items.size();
//        } finally {
//            itemsLock.readLock().unlock();
//        }
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

//        itemsLock.readLock().lock();
        if(rowIndex >= items.size()) {
//            itemsLock.readLock().unlock();
            return null;
        }

        AisMessageExtended ship = items.get(rowIndex);
//        itemsLock.readLock().unlock();
        
        switch (columnIndex) {
        case 0:
            // return "Name";
            return ship.name;
        case 1:
            // return "MMSI";
            return ship.MMSI;
        case 2:
            // return "???";
            return ship.hdg;
        case 3:
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);// set as you need
            // String dst = nf.format(ship.dst);
            return ship.dst;
            // return "DST";
        default:
            return "";

        }

    }

//    class UpdateShipQueueWorker extends SwingWorker<Void, Void> {
//        private AisTableModel model;
//        private ConcurrentLinkedQueue<VesselTarget> queue;
//
//        UpdateShipQueueWorker(AisTableModel model,
//                ConcurrentLinkedQueue<VesselTarget> queue) {
//            this.model = model;
//            this.queue = queue;
//        }
//
//        @Override
//        protected Void doInBackground() throws Exception {
//            while (true) {
//                VesselTarget vt = this.queue.poll();
//
//                if (vt == null) {
//                    // System.out.println("DONE WITH QUEUE");
//                    Thread.sleep(2000);
//
//                } else {
//                    // System.out.println(this.queue.size());
//                    assert this.queue.size() < 2000;
//                    this.model.updateShip(vt);
//                }
//            }
//        }
//        
//    }

//    private class UpdatePublisherThread extends Thread {
//        
//        @Override
//        public void run() {
//            while(AisTableModel.this.keepAlive) {
//                if(AisTableModel.this.updatesPending) {
//                    System.out.println("[## PUBLISHING TABLE UPDATES ##]");
////                    AisTableModel.this.fireTableDataChanged();
//                    AisTableModel.this.updatesPending = false;
//                }
//                // Check for updates every 2 seconds.
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
//        
//    }
    
    @Override
    public void targetUpdated(final AisTarget aisTarget) {
        // Model must be updated on the EDT.
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                
                if(!(aisTarget instanceof VesselTarget)) {
                    // We are only interested in vessels.
                    return;
                }
                
                VesselTarget vessel = (VesselTarget) aisTarget;
                if(vessel.isGone() && !mappedItems.containsKey(vessel.getMmsi())) {
                    // Vessel is gone and not managed in model, do nothing.
                    return;
                }
                
                if(vessel.isGone()) {
                    // If target is gone but managed in model, we remove it from the model.
                    AisTableModel.this.deleteVessel(vessel.getMmsi());
                    return;
                }
                
                // Update or add new?
                if(mappedItems.containsKey(vessel.getMmsi())) {
                    AisTableModel.this.updateVessel(vessel);
                } else {
                    AisTableModel.this.addVessel(vessel);
                }
            }
        });
        
       /* 
        
        if(!(aisTarget instanceof VesselTarget)) {
            // We are only interested in vessels.
            return;
        }
        VesselTarget vessel = (VesselTarget) aisTarget;
        if(vessel.isGone() && !mappedItems.containsKey(vessel.getMmsi())) {
            // Vessel is gone and not managed in model, do nothing.
            return;
        }
        
        // We now know that we need to either add, remove or update a model item - updates are pending.
        updatesPending = true;
        if(vessel.isGone()) {
            // If target is gone, we remove it from the model.
//            int idx = this.deleteVessel(vessel.getMmsi());
//            fireTableRowsDeleted(idx, idx);
            this.deleteVessel(vessel.getMmsi());
            return;
        }
        
        // Attempt to retrieve existing model object.
        AisMessageExtended item = mappedItems.get(vessel.getMmsi());
        if(item != null) {
            // Found existing model object.
            // Use AisHandler factory method. TODO hacky PNT access.
            AisMessageExtended newValues = aisHandler.getShip(vessel, EPDShip.getInstance().getPntHandler().getCurrentData());
            // Copy new values into existing object.
            item.updateFrom(newValues);
            // TODO 
        } else {
            // No existing model object, add new.
            this.addVessel(vessel);
//            itemsLock.readLock().lock();
//            item = mappedItems.get(vessel.getMmsi());
//            int index = items.indexOf(item);
                
//            fireTableRowsInserted(index, index);

//            itemsLock.readLock().unlock();
        }
        */
    }
    
    /**
     * Adds a vessel to the model.
     * @param vessel The vessel to add to the model.
     */
    private void addVessel(final VesselTarget vessel) {
//        SwingUtilities.invokeLater(new Runnable() {
            // Table model must be modified on the on EDT.
//            @Override
//            public void run() {
                // TODO hacky PNT access.
                AisMessageExtended item = AisTableModel.this.aisHandler.getShip(vessel, EPDShip.getInstance().getPntHandler().getCurrentData());
                // Add to map providing model lookup based on MMSI.
                AisTableModel.this.mappedItems.put(item.MMSI, item);
                // Add to model.
//                AisTableModel.this.itemsLock.writeLock().lock();
                AisTableModel.this.items.add(item);
                int idx = AisTableModel.this.items.size() - 1;
                AisTableModel.this.fireTableRowsInserted(idx, idx);
//                AisTableModel.this.itemsLock.writeLock().unlock();
//            }
//        });
    }
    
    private void updateVessel(final VesselTarget vessel) {
//        SwingUtilities.invokeLater(new Runnable() {
            // Table model must be modified on the on EDT.
//            @Override
//            public void run() {
                AisMessageExtended current = AisTableModel.this.mappedItems.get(vessel.getMmsi());
                AisMessageExtended newValues = AisTableModel.this.aisHandler.getShip(vessel, EPDShip.getInstance().getPntHandler().getCurrentData());
                // Update current with new values
                current.updateFrom(newValues);
                // notify view
                int idx = AisTableModel.this.items.indexOf(current);
                fireTableRowsUpdated(idx, idx);
//            }
//        });
    }
    
    /**
     * Removes a vessel from the model.
     * @param mmsi The MMSI of the vessel to remove.
     */
    private void deleteVessel(final Long mmsi) {
//        SwingUtilities.invokeLater(new Runnable() {
            // Table model must be modified on the on EDT.
//            @Override
//            public void run() {
                // Present in map?
                AisMessageExtended removed = AisTableModel.this.mappedItems.remove(mmsi);
                if(removed != null) {
                    // Vessel was present in map, also remove from model.
//                    AisTableModel.this.itemsLock.writeLock().lock();
                    int idx = AisTableModel.this.items.indexOf(removed);
                    AisTableModel.this.items.remove(idx);
//                    AisTableModel.this.itemsLock.writeLock().unlock();
                    AisTableModel.this.fireTableRowsDeleted(idx, idx);
                }
//            }
//        });
    }
}

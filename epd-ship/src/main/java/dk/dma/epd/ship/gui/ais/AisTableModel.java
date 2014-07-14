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
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import dk.dma.epd.common.prototype.ais.AisHandlerCommon.AisMessageExtended;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ais.AisHandler;

/**
 * Table model for "nearby AIS targets" dialog.
 */
public class AisTableModel extends AbstractTableModel implements IAisTargetListener {
    
    private static final long serialVersionUID = 1L;

    private static final String[] COLUMN_NAMES = { "Name", "MMSI", "HDG", "DST" };

    public static final String COL_NAME = "Name";
    
    public static final String COL_MMSI = "MMSI";
    
    public static final String COL_HDG = "HDG";
    
    public static final String COL_DST = "DST";
    
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
    private HashMap<Long, AisMessageExtended> mappedItems = new HashMap<>();
    
    public AisTableModel(AisHandler aisHandler) {
        super();
        this.aisHandler = aisHandler;
        // TODO where to remove listener?
        this.aisHandler.addListener(this);
    }

    /**
     * Gets column index by column name.
     * Column names are given as static fields, see e.g. {@link AisTableModel#COL_MMSI}.
     * @param colName Name of column.
     * @return The index of column with name equal to {@code colName} or -1 if no such column exists.
     */
    public int getColumnIndex(String colName) {
        int index = 0;
        for(String s : COLUMN_NAMES) {
            if(s.equals(colName)) {
                return index;
            }
            index++;
        }
        return -1;
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
        if(!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("getRowCount should only be called on the EDT");
        }
        return items.size();
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
        if(!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("Model can only be accessed on the EDT.");
        }
        if(rowIndex >= items.size()) {
            return null;
        }
        AisMessageExtended ship = items.get(rowIndex);
        switch (columnIndex) {
        case 0:
            return ship.name;
        case 1:
            return ship.MMSI;
        case 2:
            return ship.hdg;
        case 3:
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);// set as you need
            // String dst = nf.format(ship.dst);
            return ship.dst;
        default:
            return "";
        }

    }
    
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
    }
    
    /**
     * Adds a new vessel to the model. <b>Should always be called on the EDT.</b>
     * @param vessel The vessel to add to the model.
     */
    private void addVessel(final VesselTarget vessel) {
        // Table model must be modified on the on EDT.
        if(!SwingUtilities.isEventDispatchThread()) {
            // TODO update with more specific exception.
            throw new RuntimeException(getClass().getSimpleName() + ": attempt to add to model outside the EDT");
        }
        // TODO hacky PNT access.
        AisMessageExtended item = AisTableModel.this.aisHandler.getShip(vessel, EPDShip.getInstance().getPntHandler().getCurrentData());
        // Add to map providing model lookup based on MMSI.
        AisTableModel.this.mappedItems.put(item.MMSI, item);
        // Add to model.
        AisTableModel.this.items.add(item);
        int idx = AisTableModel.this.items.size() - 1;
        AisTableModel.this.fireTableRowsInserted(idx, idx);
    }
    
    /**
     * Updates a model item. <b>Should always be called on the EDT.</b>
     * @param vessel Contains new data for the model item.
     */
    private void updateVessel(final VesselTarget vessel) {
        // Table model must be modified on the on EDT.
        if(!SwingUtilities.isEventDispatchThread()) {
            // TODO update with more specific exception.
            throw new RuntimeException(getClass().getSimpleName() + ": attempt to update model item outside the EDT");
        }
        AisMessageExtended current = AisTableModel.this.mappedItems.get(vessel.getMmsi());
        AisMessageExtended newValues = AisTableModel.this.aisHandler.getShip(vessel, EPDShip.getInstance().getPntHandler().getCurrentData());
        // Update current with new values
        current.updateFrom(newValues);
        // notify view
        int idx = AisTableModel.this.items.indexOf(current);
        fireTableRowsUpdated(idx, idx);
    }
    
    /**
     * Removes a vessel from the model.
     * <b>Should always be called on the EDT.</b>
     * @param mmsi The MMSI of the vessel to remove.
     */
    private void deleteVessel(final Long mmsi) {
        // Table model must be modified on the on EDT.
        if(!SwingUtilities.isEventDispatchThread()) {
            // TODO update with more specific exception.
            throw new RuntimeException(getClass().getSimpleName() + ": attempt to delete from model outside the EDT");
        }
        // Present in map?
        AisMessageExtended removed = AisTableModel.this.mappedItems.remove(mmsi);
        if(removed != null) {
            // Vessel was present in map, also remove from model.
            int idx = AisTableModel.this.items.indexOf(removed);
            AisTableModel.this.items.remove(idx);
            AisTableModel.this.fireTableRowsDeleted(idx, idx);
        }
    }
}

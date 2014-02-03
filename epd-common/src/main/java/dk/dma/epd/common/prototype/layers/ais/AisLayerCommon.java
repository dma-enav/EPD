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
package dk.dma.epd.common.prototype.layers.ais;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dk.dma.epd.common.graphics.ISelectableGraphic;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.layers.LazyLayerCommon;

/**
 * @author Janus Varmarken
 */
@SuppressWarnings("serial")
public abstract class AisLayerCommon<AISHANDLER extends AisHandlerCommon>
        extends LazyLayerCommon implements IAisTargetListener {

    /**
     * The AIS handler that provides AIS data for this layer.
     */
    protected volatile AISHANDLER aisHandler;

    /**
     * Maps an MMSI to the object handling its graphical representation.
     */
    private Map<Long, TargetGraphic> targets = new ConcurrentHashMap<>();

    /**
     * The graphic that is currently selected by the user.
     */
    private ISelectableGraphic selectedGraphic;

    public AisLayerCommon(int repaintIntervalMillis) {
        super(repaintIntervalMillis);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        if (obj instanceof AisHandlerCommon) {
            this.aisHandler = (AISHANDLER) obj;
            this.aisHandler.addListener(this);
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (obj == this.aisHandler) {
            this.aisHandler.removeListener(this);
        }
        super.findAndUndo(obj);
    }

    /**
     * Mark a graphic as selected. Repaint this layer if requested.
     * 
     * @param newSelection
     *            The graphic that is now the selected graphic.
     * @param repaint
     *            If this layer should repaint itself to reflect the change in
     *            selection.
     */
    public void setSelectedGraphic(ISelectableGraphic newSelection,
            boolean repaint) {
        if (this.selectedGraphic != null) {
            // remove current selection
            this.selectedGraphic.setSelection(false);
        }
        if (newSelection != null) {
            // mark new selection
            newSelection.setSelection(true);
        }
        // keep reference to new selection
        this.selectedGraphic = newSelection;
        if (repaint) {
            this.doPrepare();
        }
    }

    /**
     * Add a graphical representation of an AIS target to this layers set of
     * graphics.
     * 
     * @param mmsi
     *            The MMSI of the AIS target that {@code tg} represents.
     * @param tg
     *            Object that manages the graphical representation of the given
     *            {@code mmsi}.
     */
    protected void addTargetGraphic(Long mmsi, TargetGraphic tg) {
        if (tg == null || mmsi == null) {
            return;
        }
        // Add the new graphic to the map of ais targets
        TargetGraphic oldVal = this.targets.put(mmsi, tg);
        synchronized (this.graphics) {
            if (oldVal != null) {
                // remove the old graphical representation of this MMSI
                this.graphics.remove(oldVal);
            }
            // add the new graphical representation of this MMSI to the list of
            // graphics to be displayed.
            this.graphics.add(tg);
        }
    }

    /**
     * Remove a graphical representation of an AIS target from this layers set
     * of graphics.
     * 
     * @param mmsi
     *            The MMSI of the AIS target that should have its graphical
     *            representation removed.
     */
    protected void removeTargetGraphic(Long mmsi) {
        TargetGraphic deleted = this.targets.remove(mmsi);
        if (deleted != null) {
            // Successfully removed a graphic object from map
            // This graphic object should also be present in the list of
            // graphics to be displayed, hence we need to remove it from the
            // graphics list.
            synchronized (this.graphics) {
                this.graphics.remove(deleted);
            }
        }
    }

    /**
     * Get the graphical representation of an AIS target with the given MMSI.
     * 
     * @param mmsi
     *            The MMSI to find the graphical representation for.
     * @return The graphical representation of the MMSI or null if there is
     *         currently no graphical representation for this MMSI.
     */
    protected TargetGraphic getTargetGraphic(Long mmsi) {
        return mmsi == null ? null : this.targets.get(mmsi);
    }
    
    /**
     * Force this AIS layer to update itself.
     */
    public abstract void forceLayerUpdate();

    /**
     * Set if this AIS layer should show name labels for the AIS targets it
     * displays.
     * 
     * @param showLabels
     *            Use true to show name labels, and use false to hide name
     *            labels.
     */
    public abstract void setShowNameLabels(boolean showLabels);
}

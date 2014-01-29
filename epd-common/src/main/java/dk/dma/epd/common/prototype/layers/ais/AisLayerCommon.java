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

import dk.dma.epd.common.graphics.ISelectableGraphic;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
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
}

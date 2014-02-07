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
package dk.dma.epd.common.prototype.layers;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.layers.ais.AisTargetSelectionGraphic;

/**
 * Class that provides selection highlight capability by adding a circle
 * surrounding an existing graphic.
 * 
 * @author Janus Varmarken
 */
public class CircleSelectionGraphic {

    /**
     * The graphic that is to be marked as selected (have a circle added to its
     * list)
     */
    private OMGraphicList selectedGraphic;

    /**
     * The circle that is used to visualize the selection.
     */
    private AisTargetSelectionGraphic selectionMark;

    /**
     * Create a {@code CircleSelectionGraphic} that will allow for visualization
     * of selection/deselection of the argument {@code OMGraphicList} using a
     * circle.
     * 
     * @param selectedGraphic
     *            The graphic that is to be marked as selected (have a circle
     *            added to its list)
     */
    public CircleSelectionGraphic(OMGraphicList selectedGraphic) {
        this.selectedGraphic = selectedGraphic;
        this.selectionMark = new AisTargetSelectionGraphic();
    }

    /**
     * Update the selection specifying if the graphic should currently be
     * highlighted as selected or if the selection highlight should be removed.
     * 
     * @param selected
     *            True if the target should be marked as selected using a
     *            circle, false otherwise.
     * @param center
     *            The center of the circle used for selection visualization. If
     *            null the selection visualization will be removed.
     */
    public void updateSelection(boolean selected, Position center) {
        // update position of selection marker
        if (selected && center != null) {
            this.selectionMark.moveSymbol(center);
            if (!this.selectedGraphic.contains(this.selectionMark)) {
                // Make sure graphic is only added once
                // (in case user reselects already selected target)
                this.selectedGraphic.add(this.selectionMark);
            }
        } else {
            // Selection was removed or there was no position data available.
            // Cannot paint the selection without awareness of its location on
            // map.
            this.selectedGraphic.remove(this.selectionMark);
        }
    }
}

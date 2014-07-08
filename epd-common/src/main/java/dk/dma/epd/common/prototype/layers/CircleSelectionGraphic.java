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
    
    /**
     * Updates the location of this {@code CircleSelectionGraphic}.
     * @param center The new center of the circle marking the selection.
     */
    public void updatePosition(Position center) {
        if(center != null) {
            this.selectionMark.moveSymbol(center);
        }
    }
}

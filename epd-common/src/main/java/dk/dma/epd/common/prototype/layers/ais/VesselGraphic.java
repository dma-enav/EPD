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

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.graphics.ISelectableGraphic;
import dk.dma.epd.common.prototype.ais.VesselTarget;

/**
 * <p>
 * Base class for graphics that visualize a vessel and its current selection
 * status. In other words: it is <b>NOT</b> intended that sub classes of this class add
 * graphics that are visual representations of items related to a vessel (e.g.
 * past track display, COG vector). If you want to create a graphic item that
 * wraps all graphics related to an AIS target or similar, make use of
 * {@link TargetGraphic}. Using composition, you can then wrap a sub class of
 * {@code VesselGraphic} inside your {@link TargetGraphic} which can be in
 * charge of visualizing the vessel part.
 * </p>
 * 
 * @author Janus Varmarken
 */
@SuppressWarnings("serial")
public abstract class VesselGraphic extends OMGraphicList implements
        ISelectableGraphic {

    /**
     * The {@link VesselTarget} received in the most recent call to
     * {@link #updateGraphic(VesselTarget, float)} (assuming sub classes make
     * sure to call the super implementation of this method)
     */
    private VesselTarget mostRecentUpdate;

    /**
     * Default constructor. Sets the vague property of {@link OMGraphicList} to
     * true such that this graphic behaves as a single unit.
     */
    public VesselGraphic() {
//        this.setVague(true);
    }

    /**
     * Gets the most recent {@link VesselTarget}, i.e. the {@code VesselTarget}
     * passed as argument to the most recent call to
     * {@link #updateGraphic(VesselTarget, float)}.
     * 
     * @return The {@link VesselTarget} passed as argument to the most recent
     *         call to {@link #updateGraphic(VesselTarget, float)} (assuming sub
     *         classes make sure to call the super implementation of this
     *         method).
     */
    public VesselTarget getMostRecentVesselTarget() {
        synchronized (this.mostRecentUpdate) {
            return this.mostRecentUpdate;
        }
    }

    /**
     * Update this {@code VesselGraphic} with position data, static data and map
     * scale.
     * <p>
     * <b>Sub classes should always all the super implementation of this
     * method.</b> The base implementation simply stores the vessel target
     * received in the update such that it can be accessed later via
     * {@link #getMostRecentVesselTarget()}.
     * </p>
     * 
     * @param vesselTarget
     *            {@code VesselTarget} Contains position data and static data
     *            used by this {@code VesselGraphic} to draw itself.
     * @param mapScale
     *            The current map scale for the layer displaying this
     *            {@code VesselGraphic}.
     */
    public void updateGraphic(VesselTarget vesselTarget, float mapScale) {
        synchronized (this.mostRecentUpdate) {
            this.mostRecentUpdate = vesselTarget;
        }
    }
}
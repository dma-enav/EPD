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
package dk.dma.epd.common.prototype.layers.ais;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.graphics.ISelectableGraphic;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.layers.predictor.VesselPortrayalData;

/**
 * <p>
 * Base class for graphics that visualize a vessel and its current selection
 * status. In other words: it is <b>NOT</b> intended that sub classes of this
 * class add graphics that are visual representations of items or meta data
 * related to a vessel (e.g. past track display, COG vector). If you want to
 * create a graphic item that wraps all graphics related to an AIS target or
 * similar, make use of {@link TargetGraphic} (or {@link VesselGraphicComponent}
 * for vessels). Using composition, you can then wrap a sub class of
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
     * Lock used to access selectionStatus.
     */
    private Object selectionStatusLock = new Object();

    /**
     * Selection status of this graphic. True indicates that the graphic is
     * selected.
     */
    private boolean selectionStatus;

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
        this.setVague(true);
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
        // TODO consider if locking is needed - add a dummy Object instance as
        // mutex if it is
        return this.mostRecentUpdate;
    }

    /**
     * Update this {@code VesselGraphic} with position data, static data and map
     * scale.
     * <p>
     * <b>Sub classes should always call the super implementation of this
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
        // TODO consider if locking is needed - add a dummy Object instance as
        // mutex if it is
        this.mostRecentUpdate = vesselTarget;
    }

    /**
     * Updates the display of this {@link VesselGraphic} with new data. Only use
     * this method directly in client code if you do not intend to query this
     * graphic for its associated {@link VesselTarget} using
     * {@link #getMostRecentVesselTarget()}. This method is a minor hack that
     * was introduced to allow clients, that do not rely on AIS data, the
     * ability to display a vessel using any of the concrete subclasses of
     * {@link VesselGraphic}. If your client code is AIS based, make use of
     * {@link #updateGraphic(VesselTarget, float)} instead.
     * 
     * @param data
     *            The updated data.
     */
    public abstract void updateGraphic(VesselPortrayalData data);

    /**
     * {@inheritDoc}<br/>
     * <b>Sub classes overriding this should always call the super
     * implementation</b> which sets the selection flag used by
     * {@link #getSelectionStatus()}.
     */
    @Override
    public void setSelectionStatus(boolean selected) {
        synchronized (this.selectionStatusLock) {
            this.selectionStatus = selected;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getSelectionStatus() {
        synchronized (this.selectionStatusLock) {
            return this.selectionStatus;
        }
    }
}

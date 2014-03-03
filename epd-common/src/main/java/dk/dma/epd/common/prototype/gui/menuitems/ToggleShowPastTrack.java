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
package dk.dma.epd.common.prototype.gui.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.MobileTarget;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.layers.ais.AisLayerCommon;

/**
 * Toggle the visibility of the past track for a specific vessel
 */
public class ToggleShowPastTrack extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;

    /**
     * The {@link MobileTarget} which should have its past track visibility
     * toggled using this {@code ToggleShowPastTrack}.
     */
    private MobileTarget mobileTarget;

    /**
     * The {@link AisLayerCommon} to refresh when this
     * {@code ToggleShowPastTrack} has its {@link #doAction()} invoked. The
     * {@link AisLayerCommon} is refreshed using
     * {@link AisLayerCommon#forceLayerUpdate()}.
     */
    private AisLayerCommon<? extends AisHandlerCommon> aisLayer;

    /**
     * Constructor
     */
    public ToggleShowPastTrack() {
        super();
    }

    /**
     * Called when the menu item is enacted
     */
    @Override
    public void doAction() {
        // Toggle past-track visibility
        mobileTarget.getSettings().setShowPastTrack(
                !mobileTarget.getSettings().isShowPastTrack());
        if (this.aisLayer != null) {
            // TODO this is a hack:
            // Do a fake target update that propagates to PastTrackGraphic that
            // then enables or disables its visibility itself.
            this.aisLayer.targetUpdated(mobileTarget);
            // Force a layer update to visually reflect the change.
            this.aisLayer.forceLayerUpdate();
        }
    }

    /**
     * Sets the {@link MobileTarget} which should have its past track visibility
     * toggled using this {@code ToggleShowPastTrack}.
     * 
     * @param mobileTarget
     *            The {@link MobileTarget} which should have its past track
     *            visibility toggled using this {@code ToggleShowPastTrack}.
     */
    public void setMobileTarget(MobileTarget mobileTarget) {
        this.mobileTarget = mobileTarget;
    }

    /**
     * Set which {@link AisLayerCommon} to refresh when this
     * {@code ToggleShowPastTrack} has its {@link #doAction()} invoked. The
     * {@link AisLayerCommon} is refreshed using
     * {@link AisLayerCommon#forceLayerUpdate()}.
     * 
     * @param aisLayer
     *            the {@link AisLayerCommon} to be refreshed when the
     *            {@link #doAction()} method of this {@code ToggleShowPastTrack}
     *            is invoked.
     */
    public void setAisLayerToRefresh(
            AisLayerCommon<? extends AisHandlerCommon> aisLayer) {
        this.aisLayer = aisLayer;
    }
}

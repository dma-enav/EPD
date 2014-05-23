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

import com.bbn.openmap.proj.Projection;

import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;
import dk.dma.epd.common.prototype.settings.layers.VesselLayerSettings;
import dk.dma.epd.common.prototype.zoom.ZoomLevel;

/**
 * A concrete {@link VesselGraphicComponent} that is used to chose among a set
 * of sub {@link VesselGraphicComponent}s. This is used to change the
 * visualization of a {@link VesselTarget} on the map can according to the
 * current scale of the map.
 */
@SuppressWarnings("serial")
public class VesselGraphicComponentSelector extends VesselGraphicComponent {

    /**
     * The {@link VesselTarget} received in the latest target update.
     */
    private VesselTarget vesselTarget;
    
    /**
     * The current display mode for this graphic (i.e. Outline, Dot or
     * Triangle).
     */
    private VesselGraphicComponent currentDisplay;

    /**
     * Component graphic displaying the vessel as a triangle. Also manages
     * display of other vessel meta data.
     */
    private VesselTriangleGraphicComponent vesselTriangleGraphic;

    /**
     * Component graphic displaying the vessel outline. Also manages display of
     * other vessel meta data.
     */
    private VesselOutlineGraphicComponent vesselOutlineGraphic;

    /**
     * Component graphic displaying the vessel as a dot. Also manages display of
     * other vessel meta data.
     */
    private VesselDotGraphicComponent vesselDotGraphic;

    /**
     * Manages display of the vessel's past track. Shared between all the
     * component graphics used for the different display modes.
     */
    private PastTrackGraphic pastTrackGraphic = new PastTrackGraphic();

    /**
     * Creates a new {@code VesselGraphicComponentSelector}.
     * 
     * @param layerSettings
     *            Settings for the layer that displays this graphic.
     * @param showName
     *            If this graphic's sub graphics should display AIS name labels.
     */
    public VesselGraphicComponentSelector(VesselLayerSettings<?> layerSettings) {
        super();
        this.vesselTriangleGraphic = new VesselTriangleGraphicComponent(layerSettings);
        this.vesselOutlineGraphic = new VesselOutlineGraphicComponent(layerSettings,
                ColorConstants.VESSEL_COLOR, 2.0f);
        this.vesselDotGraphic = new VesselDotGraphicComponent();
    }

    /**
     * Performs initialization.
     */
    private void createGraphics() {
        this.add(this.pastTrackGraphic);
    }

    @Override
    public void update(AisTarget aisTarget, float mapScale) {

        if (aisTarget instanceof VesselTarget) {

            vesselTarget = (VesselTarget) aisTarget;
            // Initialize if this is the first update we receive
            if (size() == 0) {
                createGraphics();
            }
            // update the sub graphics that manages the different vessel and
            // vessel metadata displays
            this.vesselOutlineGraphic.update(aisTarget, mapScale);
            this.vesselTriangleGraphic.update(aisTarget, mapScale);
            this.vesselDotGraphic.update(aisTarget, mapScale);

            // Update the past-track graphic
            pastTrackGraphic.update(vesselTarget);
            // redraw according to scale and data availability
            this.drawAccordingToScale(mapScale);
        }
    }

    /**
     * Set the current display mode.
     * 
     * @param newDisplay
     *            The {@link VesselGraphicComponent} to be displayed by this
     *            graphic.
     */
    private void updateCurrentDisplay(VesselGraphicComponent newDisplay) {
        // Remove previous display
        this.remove(this.currentDisplay);
        // Log new display
        this.currentDisplay = newDisplay;
        // Update display
        this.add(this.currentDisplay);
    }

    /**
     * Changes display mode to use an instance of
     * {@link VesselOutlineGraphicComponent}.
     */
    private void drawOutline() {
        this.updateCurrentDisplay(this.vesselOutlineGraphic);
    }

    /**
     * Changes display mode to use an instance of {@link VesselTriangleGraphicComponent}.
     */
    private void drawTriangle() {
        this.updateCurrentDisplay(this.vesselTriangleGraphic);
    }

    /**
     * Changes display mode to use an instance of {@link VesselDotGraphicComponent}.
     */
    private void drawDot() {
        this.updateCurrentDisplay(this.vesselDotGraphic);
    }

    /**
     * Get the {@link VesselTarget} received in the most recent call to
     * {@link #update(AisTarget, AisSettings, NavSettings, float)}.
     * 
     * @return The most recent {@link VesselTarget}.
     */
    public VesselTarget getVesselTarget() {
        return vesselTarget;
    }

    /**
     * Sets if sub {@link VesselGraphicComponent}s should show or hide AIS name
     * labels.
     * 
     * @param showNameLabel
     *            Specify true to show AIS name labels, false to hide AIS name
     *            labels.
     */
    public void setShowNameLabel(boolean showNameLabel) {
        if (this.vesselTriangleGraphic != null) {
            this.vesselTriangleGraphic.setShowNameLabel(showNameLabel);
        }

        if (this.vesselOutlineGraphic != null) {
            this.vesselOutlineGraphic.setShowNameLabel(showNameLabel);
        }
    }

    /**
     * Get if sub {@link VesselGraphicComponent}s are set to show AIS name
     * labels.
     * 
     * @return True if sub {@link VesselGraphicComponent}s are set to show AIS
     *         name labels, false otherwise.
     */
    public boolean getShowNameLabel() {
        if (this.vesselTriangleGraphic != null) {
            return this.vesselTriangleGraphic.getShowNameLabel();
        }
        return true;
    }

    /**
     * Get the {@link PastTrackGraphic} associated with this
     * {@code VesselGraphicComponentSelector}.
     * 
     * @return the {@link PastTrackGraphic} associated with this
     *         {@code VesselGraphicComponentSelector}.
     */
    public PastTrackGraphic getPastTrackGraphic() {
        return pastTrackGraphic;
    }

    /**
     * Chooses what display mode to use based on map scale and availability of
     * {@link VesselStaticData}.
     * 
     * @param mapScale
     *            The current map scale of the layer in which this
     *            {@code VesselGraphicComponentSelector} resides.
     */
    private void drawAccordingToScale(float mapScale) {
        if (this.vesselTarget == null
                || this.vesselTarget.getPositionData() == null) {
            // cannot draw when we have no vessel data
            return;
        }
        ZoomLevel zl = ZoomLevel.getFromScale(mapScale);
        switch (zl) {
        case VESSEL_OUTLINE:
            VesselStaticData vsd = this.vesselTarget.getStaticData();
            if (vsd != null && (vsd.getDimBow() + vsd.getDimStern()) > 0
                    && (vsd.getDimPort() + vsd.getDimStarboard()) > 0) {
                // can only draw outline if static data is available
                this.drawOutline();
            } else {
                // draw standard triangle if we do not have static data
                this.drawTriangle();
            }
            break;
        case VESSEL_TRIANGLE:
            this.drawTriangle();
            break;
        case VESSEL_DOT:
            this.drawDot();
            break;
        }
    }

    @Override
    public boolean generate(Projection p, boolean forceProjectAll) {
        // Avoid NPE's during start-up
        if (p == null) {
            return true;
        }

        // Generate is called every time the layer's projection changes.
        // A projection change might impose a need for changing the current draw
        // mode for this graphic.
        // Hence recompute how this graphic should visualize itself with the new
        // projection.
        this.drawAccordingToScale(p.getScale());
        return super.generate(p, forceProjectAll);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    VesselGraphic getVesselGraphic() {
        return this.currentDisplay != null ? this.currentDisplay
                .getVesselGraphic() : null;
    }
}

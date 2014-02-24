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
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.NavSettings;
import dk.dma.epd.common.prototype.zoom.ZoomLevel;

/**
 * A concrete {@link VesselGraphicComponent} that contains sub {@link VesselGraphicComponent}s.
 */
@SuppressWarnings("serial")
public class VesselGraphicComponentSelector extends VesselGraphicComponent  {
    
    private VesselTarget vesselTarget;

    /**
     * The current display mode for this graphic (i.e. Outline, Dot or Triangle).
     */
    private VesselGraphicComponent currentDisplay;
    
    private VesselTriangleGraphic vesselTriangleGraphic;
    private VesselOutlineGraphicComponent vesselOutlineGraphic;
    private VesselDotGraphic vesselDotGraphic;

    private PastTrackGraphic pastTrackGraphic = new PastTrackGraphic();

    public VesselGraphicComponentSelector(boolean showName) {
        super();
        this.vesselTriangleGraphic = new VesselTriangleGraphic();
        this.vesselTriangleGraphic.setShowNameLabel(showName);
        this.vesselOutlineGraphic = new VesselOutlineGraphicComponent(ColorConstants.VESSEL_COLOR, 2.0f);
        this.vesselOutlineGraphic.setShowNameLabel(showName);
        this.vesselDotGraphic = new VesselDotGraphic();
    }

    private void createGraphics() {
        this.add(this.pastTrackGraphic);
    }

    @Override
    public void update(AisTarget aisTarget, AisSettings aisSettings, NavSettings navSettings, float mapScale) {

        if (aisTarget instanceof VesselTarget) {

            vesselTarget = (VesselTarget) aisTarget;
            // Initialize if this is the first update we receive
            if (size() == 0) {
                createGraphics();
            }
            // update the sub graphics that manages the different vessel and vessel metadata displays
            this.vesselOutlineGraphic.update(aisTarget, aisSettings, navSettings, mapScale);
            this.vesselTriangleGraphic.update(aisTarget, aisSettings, navSettings, mapScale);
            this.vesselDotGraphic.update(aisTarget, aisSettings, navSettings, mapScale);

            // Update the past-track graphic
            pastTrackGraphic.update(vesselTarget);
            // redraw according to scale and data availability
            this.drawAccordingToScale(mapScale);
        }
    }
    
    /**
     * Set the current display mode.
     * @param newDisplay The {@link VesselGraphicComponent} to be displayed by this graphic.
     */
    private void updateCurrentDisplay(VesselGraphicComponent newDisplay) {
        // Remove previous display
        this.remove(this.currentDisplay);
        // Log new display
        this.currentDisplay = newDisplay;
        // Update display
        this.add(this.currentDisplay);
    }

    private void drawOutline() {
        this.updateCurrentDisplay(this.vesselOutlineGraphic);
    }

    private void drawTriangle() {
        this.updateCurrentDisplay(this.vesselTriangleGraphic);
    }

    private void drawDot() {
        this.updateCurrentDisplay(this.vesselDotGraphic);
    }

    public VesselTarget getVesselTarget() {
        return vesselTarget;
    }

    public void setShowNameLabel(boolean showNameLabel) {
        if (this.vesselTriangleGraphic != null) {
            this.vesselTriangleGraphic.setShowNameLabel(showNameLabel);
        }
        
        if (this.vesselOutlineGraphic != null) {
            this.vesselOutlineGraphic.setShowNameLabel(showNameLabel);
        }
    }

    public boolean getShowNameLabel() {
        if (this.vesselTriangleGraphic != null) {
            return this.vesselTriangleGraphic.getShowNameLabel();
        }
        return true;
    }

    public PastTrackGraphic getPastTrackGraphic() {
        return pastTrackGraphic;
    }

    private void drawAccordingToScale(float mapScale) {
        if (this.vesselTarget == null || this.vesselTarget.getPositionData() == null) {
            // cannot draw when we have no vessel data
            return;
        }
        ZoomLevel zl = ZoomLevel.getFromScale(mapScale);
        switch (zl) {
        case VESSEL_OUTLINE:
            VesselStaticData vsd = this.vesselTarget.getStaticData();
            if (vsd != null && (vsd.getDimBow() + vsd.getDimStern()) > 0 && (vsd.getDimPort() + vsd.getDimStarboard()) > 0) {
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
        // A projection change might impose a need for changing the current draw mode for this graphic.
        // Hence recompute how this graphic should visualize itself with the new projection.
        this.drawAccordingToScale(p.getScale());
        return super.generate(p, forceProjectAll);
    }

    @Override
    VesselGraphic getVesselGraphic() {
        return this.currentDisplay != null ? this.currentDisplay.getVesselGraphic() : null;
    }
}

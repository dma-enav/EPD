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

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.proj.Projection;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.ISelectableGraphic;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.NavSettings;
import dk.dma.epd.common.prototype.zoom.ZoomLevel;

/**
 * Graphic for vessel target
 */
public class VesselTargetGraphic extends TargetGraphic implements ISelectableGraphic {

    private static final long serialVersionUID = 1L;

    public static final float STROKE_WIDTH = 1.5f;

    private VesselTarget vesselTarget;

    // VesselTriangleGraphic
    private VesselTriangleGraphic vesselTriangleGraphic;
    // VesselOutlineGraphic
    private VesselOutlineGraphic vesselOutlineGraphic;
    // VesselDotGraphic
    private VesselDotGraphic vesselDotGraphic;

    private PastTrackGraphic pastTrackGraphic = new PastTrackGraphic();

    public VesselTargetGraphic(boolean showName, OMGraphicHandlerLayer parentLayer) {
        super();
        this.vesselTriangleGraphic = new VesselTriangleGraphic(this, parentLayer);
        this.vesselTriangleGraphic.setShowNameLabel(showName);
        this.vesselOutlineGraphic = new VesselOutlineGraphic(ColorConstants.VESSEL_COLOR, 2.0f, parentLayer, this);
        this.vesselOutlineGraphic.setShowNameLabel(showName);
        this.vesselDotGraphic = new VesselDotGraphic();
    }

    private void createGraphics() {
        this.add(this.pastTrackGraphic);
        this.add(this.vesselTriangleGraphic);
        this.add(this.vesselOutlineGraphic);
        this.add(this.vesselDotGraphic);
    }

    @Override
    public void update(AisTarget aisTarget, AisSettings aisSettings, NavSettings navSettings, float mapScale) {

        if (aisTarget instanceof VesselTarget) {

            vesselTarget = (VesselTarget) aisTarget;
            VesselPositionData posData = vesselTarget.getPositionData();

            Position pos = posData.getPos();

            if (size() == 0) {
                createGraphics();
            }
            // update sub graphic
            this.vesselTriangleGraphic.update(aisTarget, aisSettings, navSettings, mapScale);
            
            if (pos != null) {
                this.vesselDotGraphic.updateLocation(posData);
            }

            // Past-track graphics
            pastTrackGraphic.update(vesselTarget);

            ZoomLevel zl = ZoomLevel.getFromScale(mapScale);
            this.drawAccordingToScale(zl);
        }
    }
    
    /**
     * Removes all graphics that represent a vessel from this {@code OMGraphicList}.
     */
    private void removeVesselGraphics() {
        this.remove(this.vesselTriangleGraphic);
        this.remove(this.vesselDotGraphic);
        this.remove(this.vesselOutlineGraphic);
    }

    private void drawOutline() {
        // clear vessel displays
        this.removeVesselGraphics();
        // update data
        this.vesselOutlineGraphic.setLocation(vesselTarget.getPositionData(), vesselTarget.getStaticData());
        // add outline graphic for display
        this.add(this.vesselOutlineGraphic);

    }

    private void drawTriangle() {
        // clear vessel displays
        this.removeVesselGraphics();
        // add triangle graphic for display
        this.add(this.vesselTriangleGraphic);
    }

    private void drawDot() {
        // clear vessel displays
        this.removeVesselGraphics();
        // add dot graphic for display 
        this.add(this.vesselDotGraphic);
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

    private void drawAccordingToScale(ZoomLevel zl) {
        if (this.vesselTarget == null || this.vesselTarget.getPositionData() == null) {
            // cannot draw when we have no vessel data
            return;
        }
        switch (zl) {
        case VESSEL_OUTLINE:
            VesselStaticData vsd = this.vesselTarget.getStaticData();
            if (vsd != null && (vsd.getDimBow() + vsd.getDimStern()) > 0 && (vsd.getDimPort() + vsd.getDimStarboard()) > 0) {
                // can only draw outline if static data is available
                this.drawOutline();
            } else {
                // draw standard triangle if we do not have static data
                // System.out.println(this.vesselTarget.getMmsi() + " has static data = null");
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
        this.drawAccordingToScale(ZoomLevel.getFromScale(p.getScale()));
        return super.generate(p, forceProjectAll);
    }
    
    // Get the visibility of VesselTriangleGraphic object of this class.
    public boolean getVesselTriangleVisibility() {
        return this.vesselTriangleGraphic.isVisible();
    }

    @Override
    public void setSelection(boolean selected) {
        // Simply delegate call to sub graphics
        this.vesselOutlineGraphic.setSelection(selected);
        this.vesselTriangleGraphic.setSelection(selected);
        this.vesselDotGraphic.setSelection(selected);
    }
}

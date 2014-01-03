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

import dk.dma.ais.message.AisMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.ais.VesselTargetSettings;
import dk.dma.epd.common.prototype.enavcloud.CloudIntendedRoute;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.NavSettings;
import dk.dma.epd.common.prototype.zoom.ZoomLevel;

/**
 * Graphic for vessel target
 */
public class VesselTargetGraphic extends TargetGraphic {

    private static final long serialVersionUID = 1L;

    public static final float STROKE_WIDTH = 1.5f;

    private VesselTarget vesselTarget;

    // VesselTriangleGraphic
    private VesselTriangleGraphic vesselTriangleGraphic;
    // VesselOutlineGraphic
    private VesselOutlineGraphic vesselOutlineGraphic;
    // VesselDotGraphic
    private VesselDotGraphic vesselDotGraphic;
    
    private IntendedRouteGraphic routeGraphic = new IntendedRouteGraphic();

    private PastTrackGraphic pastTrackGraphic = new PastTrackGraphic();
    
    public VesselTargetGraphic(boolean showName) {
        super();
        this.vesselTriangleGraphic = new VesselTriangleGraphic(this);
        this.vesselTriangleGraphic.setShowNameLabel(showName);
        this.vesselOutlineGraphic = new VesselOutlineGraphic(ColorConstants.EPD_SHIP_VESSEL_COLOR, 2.0f);
        this.vesselDotGraphic = new VesselDotGraphic();
    }

    private void createGraphics() {
        this.add(this.pastTrackGraphic);
        this.add(this.routeGraphic);
        this.add(this.vesselTriangleGraphic);
        this.add(this.vesselOutlineGraphic);
        this.add(this.vesselDotGraphic);
    }

    @Override
    public void update(AisTarget aisTarget, AisSettings aisSettings, NavSettings navSettings, float mapScale) {

        if (aisTarget instanceof VesselTarget) {
            
            vesselTarget = (VesselTarget) aisTarget;
            VesselPositionData posData = vesselTarget.getPositionData();
            VesselStaticData staticData = vesselTarget.getStaticData();
            VesselTargetSettings targetSettings = vesselTarget.getSettings();
            CloudIntendedRoute cloudIntendedRoute = vesselTarget.getIntendedRoute();

            Position pos = posData.getPos();
            
            if (size() == 0) {
                createGraphics();
            }
            // update sub graphic
            this.vesselTriangleGraphic.update(aisTarget, aisSettings, navSettings, mapScale);
            if(pos != null) {
                this.vesselDotGraphic.updateLocation(pos);
            }
            // Determine name
            String name;
            if (staticData != null) {
                name = AisMessage.trimText(staticData.getName());
            } else {
                Long mmsi = vesselTarget.getMmsi();
                name = "ID:" + mmsi.toString();
            }
            
            // Intended route graphic
            routeGraphic.update(vesselTarget, name, cloudIntendedRoute, pos);
            if (!targetSettings.isShowRoute()) {
                routeGraphic.setVisible(false);
            }
            
            // Past-track graphics
            pastTrackGraphic.update(vesselTarget);
            
            ZoomLevel zl = ZoomLevel.getFromScale(mapScale);
            this.drawAccordingToScale(zl);
        }
    }

    private void drawOutline() {
        // hide other display modes
        this.vesselTriangleGraphic.setVisible(false);
        this.vesselDotGraphic.setVisible(false);
        // update data
        this.vesselOutlineGraphic.setLocation(vesselTarget.getPositionData(), vesselTarget.getStaticData());
        // (re-)enable visibility for outline mode
        this.vesselOutlineGraphic.setVisible(true);
        
    }
    
    private void drawTriangle() {
        // hide other display modes
        this.vesselOutlineGraphic.setVisible(false);
        this.vesselDotGraphic.setVisible(false);
        // (re-)enable visibility for triangle mode
        this.vesselTriangleGraphic.setVisible(true);
    }
    
    private void drawDot() {
        // hide other display modes
        this.vesselOutlineGraphic.setVisible(false);
        this.vesselTriangleGraphic.setVisible(false);
        // (re-)enable visibility for dot mode
        this.vesselDotGraphic.setVisible(true);
    }
    
    @Override
    public void setMarksVisible(Projection projection, AisSettings aisSettings, NavSettings navSettings) {
        if(this.vesselTriangleGraphic != null) {
            this.vesselTriangleGraphic.setMarksVisible(projection, aisSettings, navSettings);
        }
    }

    public VesselTarget getVesselTarget() {
        return vesselTarget;
    }

    public void setShowNameLabel(boolean showNameLabel) {
        if(this.vesselTriangleGraphic != null) {
            this.vesselTriangleGraphic.setShowNameLabel(showNameLabel);
        }
    }

    public boolean getShowNameLabel() {
        if(this.vesselTriangleGraphic != null) {
            return this.vesselTriangleGraphic.getShowNameLabel();
        }
        return true;
    }

    public IntendedRouteGraphic getRouteGraphic() {
        return routeGraphic;
    }
    
    public PastTrackGraphic getPastTrackGraphic() {
        return pastTrackGraphic;
    }
    
    public void drawAccordingToScale(ZoomLevel zl) {
        if(this.vesselTarget == null || this.vesselTarget.getPositionData() == null) {
            // cannot draw when we have no vessel data
            return;
        }
        switch(zl) {
        case VESSEL_OUTLINE:
            if(this.vesselTarget.getStaticData() != null) {
                // can only draw outline if static data is available
                this.drawOutline();
            }
            else {
                // draw standard triangle if we do not have static data
//                System.out.println(this.vesselTarget.getMmsi() + " has static data = null");
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
}

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
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.NavSettings;

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
    // VesselDotGraphic
    
    private IntendedRouteGraphic routeGraphic = new IntendedRouteGraphic();

    public VesselTargetGraphic(boolean showName) {
        super();
        this.vesselTriangleGraphic = new VesselTriangleGraphic(this);
        this.vesselTriangleGraphic.setShowNameLabel(showName);
    }

    private void createGraphics() {
        this.add(this.routeGraphic);
        this.add(this.vesselTriangleGraphic);
    }

    @Override
    public void update(AisTarget aisTarget, AisSettings aisSettings, NavSettings navSettings) {

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
            this.vesselTriangleGraphic.update(aisTarget, aisSettings, navSettings);

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
        }
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

}

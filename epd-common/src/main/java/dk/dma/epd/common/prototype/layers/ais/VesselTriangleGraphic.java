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

import java.awt.Font;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMText;

import dk.dma.ais.message.AisMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.ISelectableGraphic;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;
import dk.dma.epd.common.prototype.layers.CircleSelectionGraphic;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.NavSettings;

/**
 * @author Janus Varmarken
 */
public class VesselTriangleGraphic extends TargetGraphic implements ISelectableGraphic {

    private static final long serialVersionUID = 1L;

    private VesselTarget vesselTarget;

    private VesselTargetGraphic parentGraphic;

    private VesselTargetTriangle vessel;
    private RotationalPoly heading;

    private Font font;
    private OMText label;

    private SpeedVectorGraphic speedVector;
    
    /**
     * Manages visualization of selection of this graphic.
     */
    private CircleSelectionGraphic circleSelectionGraphic;
    
    /**
     * The layer that displays this graphic object.
     * If this graphic is a subgraphic of another graphic,
     * use the top level graphic's parent layer.
     */
    private OMGraphicHandlerLayer parentLayer;
    
    public VesselTriangleGraphic(VesselTargetGraphic parentGraphic, OMGraphicHandlerLayer parentLayer) {
        this.parentGraphic = parentGraphic;
        this.parentLayer = parentLayer;
    }

    private void createGraphics(AisSettings aisSettings) {
        vessel = new VesselTargetTriangle(this.parentGraphic);

        int[] headingX = { 0, 0 };
        int[] headingY = { 0, -100 };
        heading = new RotationalPoly(headingX, headingY, null, ColorConstants.VESSEL_HEADING_COLOR);

        font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
        label = new OMText(0, 0, 0, 0, "", font, OMText.JUSTIFY_CENTER);

        this.speedVector = new SpeedVectorGraphic(ColorConstants.VESSEL_HEADING_COLOR);
        
        add(label);
        this.label.setVisible(aisSettings.isShowNameLabels());
        add(0, vessel);
        this.add(this.speedVector);
        add(heading);
        // create the selection graphic
        this.circleSelectionGraphic = new CircleSelectionGraphic(this);
    }

    @Override
    public void update(AisTarget aisTarget, AisSettings aisSettings, NavSettings navSettings, float mapScale) {
        if (aisTarget instanceof VesselTarget) {

            vesselTarget = (VesselTarget) aisTarget;
            VesselPositionData posData = vesselTarget.getPositionData();
            VesselStaticData staticData = vesselTarget.getStaticData();

            Position pos = posData.getPos();
            double trueHeading = posData.getTrueHeading();
            boolean noHeading = false;
            if (trueHeading == 511) {
                trueHeading = vesselTarget.getPositionData().getCog();
                noHeading = true;
            }

            double lat = pos.getLatitude();
            double lon = pos.getLongitude();

            if (size() == 0) {
                createGraphics(aisSettings);
            }

            double hdgR = Math.toRadians(trueHeading);

            vessel.update(lat, lon, OMGraphicConstants.DECIMAL_DEGREES, hdgR);
            heading.setLocation(lat, lon, OMGraphicConstants.DECIMAL_DEGREES, hdgR);
            if (noHeading) {
                heading.setVisible(false);
            }
            
            // update the speed vector with the new data
            if (this.parentLayer != null && this.parentLayer.getProjection() != null) {
                this.speedVector.update(posData, this.parentLayer.getProjection().getScale());
            }
            // update position of selection marker
            this.circleSelectionGraphic.updatePosition(pos);
            
            // Set label
            label.setLat(lat);
            label.setLon(lon);
            if (trueHeading > 90 && trueHeading < 270) {
                label.setY(-10);
            } else {
                label.setY(20);
            }

            // Determine name
            String name;
            if (staticData != null) {
                name = AisMessage.trimText(staticData.getName());
            } else {
                Long mmsi = vesselTarget.getMmsi();
                name = "ID:" + mmsi.toString();
            }
            label.setData(name);
        }
    }

    public void setShowNameLabel(boolean showNameLabel) {
        if(this.label != null) {
            this.label.setVisible(showNameLabel);    
        }
    }

    public boolean getShowNameLabel() {
        return this.label.isVisible();
    }

    @Override
    public void setSelection(boolean selected) {
        // Get the latest position data
        Position centerPos = this.vesselTarget != null ? this.vesselTarget.getPositionData() != null ? this.vesselTarget.getPositionData().getPos() : null : null;
        this.circleSelectionGraphic.updateSelection(selected, centerPos);
    }
}

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

import java.awt.Font;
import java.util.Objects;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMText;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;
import dk.dma.epd.common.prototype.settings.layers.VesselLayerSettings;

/**
 * A concrete implementation of {@link VesselGraphicComponent} that displays a
 * {@link VesselTarget} as a triangle (using a {@link VesselTriangle}) and
 * vessel meta data such as a COG/Speed vector and heading.
 * 
 * @author Janus Varmarken et al.
 */
@SuppressWarnings("serial")
public class VesselTriangleGraphicComponent extends VesselGraphicComponent {

    /**
     * Displays the vessel's position on map.
     */
    private VesselTriangle vessel;

    /**
     * Displays the vessel's true heading (i.e. the direction of the bow).
     */
    private RotationalPoly heading;

    /**
     * Font used for the AIS name label.
     */
    private Font font;

    /**
     * Displays the AIS name label.
     */
    private OMText label;

    /**
     * Displays a COG/speed vector.
     */
    private SpeedVectorGraphic speedVector;

    /**
     * Settings for the layer that displays this graphic.
     */
    private final VesselLayerSettings<?> layerSettings;
    
    /**
     * Creates a new {@link VesselTriangleGraphicComponent}.
     * 
     * @param layerSettings
     *             Settings for the layer that displays this graphic.
     */
    public VesselTriangleGraphicComponent(VesselLayerSettings<?> layerSettings) {
        this.layerSettings = Objects.requireNonNull(layerSettings);
    }
    
    /**
     * Initializes sub graphics.
     * 
     * @param aisSettings
     *            The {@link AisSettings} containing information on whether to
     *            hide or show the AIS name label.
     */
    private void createGraphics() {
        this.vessel = new VesselTriangle();

        int[] headingX = { 0, 0 };
        int[] headingY = { 0, -100 };
        this.heading = new RotationalPoly(headingX, headingY, null,
                ColorConstants.VESSEL_HEADING_COLOR);

        this.font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
        this.label = new OMText(0, 0, 0, 0, "", font, OMText.JUSTIFY_CENTER);

        this.speedVector = new SpeedVectorGraphic(this.layerSettings,
                ColorConstants.VESSEL_HEADING_COLOR);

        add(label);
        this.label.setVisible(this.layerSettings.isShowVesselNameLabels());
        add(0, vessel);
        this.add(this.speedVector);
        add(heading);
    }

    /**
     * Update this {@link VesselTriangleGraphicComponent} with new AIS data.
     */
    @Override
    public void update(AisTarget aisTarget, float mapScale) {
        if (aisTarget instanceof VesselTarget) {

            VesselTarget vesselTarget = (VesselTarget) aisTarget;
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
                createGraphics();
            }

            double hdgR = Math.toRadians(trueHeading);

            this.vessel.updateGraphic(vesselTarget, mapScale);
            this.heading.setLocation(lat, lon,
                    OMGraphicConstants.DECIMAL_DEGREES, hdgR);
            if (noHeading) {
                this.heading.setVisible(false);
            }

            // update the speed vector with the new data
            this.speedVector.update(posData, mapScale);

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
                name = staticData.getTrimmedName();
            } else {
                Long mmsi = vesselTarget.getMmsi();
                name = "ID:" + mmsi.toString();
            }
            label.setData(name);
        }
    }

    /**
     * Toggles display of AIS name label on/off.
     * 
     * @param showNameLabel
     *            True to display name label, false to hide name label.
     */
    public void setShowNameLabel(boolean showNameLabel) {
        if (this.label != null) {
            this.label.setVisible(showNameLabel);
        }
    }

    /**
     * Get if this {@link VesselTriangleGraphicComponent} is currently set to
     * display its name label.
     * 
     * @return True if this {@link VesselTriangleGraphicComponent} is currently
     *         set to display its name label, false otherwise.
     */
    public boolean getShowNameLabel() {
        return this.label.isVisible();
    }

    /**
     * Get the {@link VesselTriangle} that this
     * {@code VesselTriangleGraphicComponent} uses to display the vessel.
     * 
     * @return The {@link VesselTriangle} that this
     *         {@code VesselTriangleGraphicComponent} uses to display the
     *         vessel.
     */
    @Override
    VesselTriangle getVesselGraphic() {
        return this.vessel;
    }
}

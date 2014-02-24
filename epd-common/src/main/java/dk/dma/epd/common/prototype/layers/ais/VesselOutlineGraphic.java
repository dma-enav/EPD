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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMText;

import dk.dma.ais.message.AisMessage;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.NavSettings;

/**
 * A concrete implementation of {@link TargetGraphic} that displays a vessel
 * target by painting the outline of the vessel relative to the map scale. This
 * class uses a {@link VesselOutline} to paint the actual outline, but it also
 * adds more graphics that visualizes other target related information such as a
 * PNT device marker and a COG vector.
 * 
 * @author Janus Varmarken
 */
@SuppressWarnings("serial")
public class VesselOutlineGraphic extends VesselGraphicComponent {

    /**
     * Handles display of the vessel outline.
     */
    private VesselOutline vesselOutline;

    /**
     * Displays the position of the PNT device.
     */
    private OMCircle pntDevice;

    /**
     * Handles display of the COG/Speed vector.
     */
    private SpeedVectorGraphic speedVector;

    /**
     * Color of sub graphics of this {@code VesselOutlineGraphic}.
     */
    private Color lineColor;

    /**
     * Thickness of the line used for the vessel outline.
     */
    private final float lineThickness;

    /**
     * Used to paint the name label for this vessel graphic.
     */
    private OMText aisName;

    /**
     * Create a new {@code VesselOutlineGraphic} with a given line color and a
     * given line thickness.
     * 
     * @param lineColor
     *            Line color to use when painting the vessel outline.
     * @param lineThickness
     *            Line thickness to use when painting the vessel outline.
     */
    public VesselOutlineGraphic(Color lineColor, float lineThickness) {
        this.lineColor = lineColor;
        this.lineThickness = lineThickness;

        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
        this.aisName = new OMText(0, 0, 0, 0, "", font, OMText.JUSTIFY_CENTER);
        this.add(aisName);
    }

    /**
     * Turn on anti-aliasing
     */
    @Override
    public void render(Graphics g) {
        Graphics2D image = (Graphics2D) g;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }

    public void setShowNameLabel(boolean showNameLabel) {
        if (this.aisName != null) {
            this.aisName.setVisible(showNameLabel);
        }
    }

    /**
     * Updates this graphic with new AIS target information.
     * 
     * @param aisTarget
     *            The AIS target containing the updated information.
     * @param aisSettings
     *            Not relevant for this sub class of {@link TargetGraphic}, use
     *            null.
     * @param navSettings
     *            Not relevant for this sub class of {@link TargetGraphic}, use
     *            null.
     * @param mapScale
     *            The current map scale of the layer in which this
     *            {@code VesselOutlineGraphic} resides.
     */
    @Override
    public void update(AisTarget aisTarget, AisSettings aisSettings,
            NavSettings navSettings, float mapScale) {
        if (aisTarget instanceof VesselTarget) {
            VesselTarget vesselTarget = (VesselTarget) aisTarget;
            // Need to create the graphic if this is the first update we receive
            if (this.vesselOutline == null) {
                this.vesselOutline = new VesselOutline(this.lineColor,
                        this.lineThickness);
                this.add(this.vesselOutline);
            }
            // Update the sub graphic in charge of drawing the outline
            this.vesselOutline.updateGraphic(vesselTarget, mapScale);
            // Create speed vector if this is the first update we receive
            if (this.speedVector == null) {
                this.speedVector = new SpeedVectorGraphic(this.lineColor);
                this.add(this.speedVector);
            }
            // TODO can pos data be null?
            VesselPositionData positionData = vesselTarget.getPositionData();
            VesselStaticData staticData = vesselTarget.getStaticData();
            // don't show COG vector if vessel is docked
            this.speedVector.setVisible(positionData.getSog() > 0.1);
            this.speedVector.update(positionData, mapScale);
            double lat = positionData.getPos().getLatitude();
            double lon = positionData.getPos().getLongitude();
            // clear old PntDevice display
            this.remove(this.pntDevice);
            this.pntDevice = new OMCircle(lat, lon, 3, 3);
            this.pntDevice.setFillPaint(this.lineColor);
            this.pntDevice.setLinePaint(this.lineColor);
            this.add(pntDevice);
            // Update label
            this.aisName.setLon(positionData.getPos().getLongitude());
            this.aisName.setLat(positionData.getPos().getLatitude());
            this.aisName.setY(-20);
            if (staticData != null) {
                this.aisName.setData(AisMessage.trimText(staticData.getName()));
            } else {
                this.aisName.setData("ID: " + vesselTarget.getMmsi());
            }
            this.aisName.setLinePaint(Color.BLACK);
        }
    }

    /**
     * Get the {@link VesselOutline} that this {@code VesselOutlineGraphic} uses
     * to display the vessel.
     * 
     * @return The {@link VesselOutline} that this {@code VesselOutlineGraphic}
     *         uses to display the vessel.
     */
    @Override
    VesselOutline getVesselGraphic() {
        return this.vesselOutline;
    }
}

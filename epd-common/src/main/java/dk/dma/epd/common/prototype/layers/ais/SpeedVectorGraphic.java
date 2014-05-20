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

import java.awt.BasicStroke;
import java.awt.Paint;
import java.util.Objects;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.gui.constants.ColorConstants;
import dk.dma.epd.common.prototype.settings.layers.VesselLayerSettings;
import dk.dma.epd.common.prototype.zoom.ScaleDependentValues;

/**
 * @author Janus Varmarken
 */
public class SpeedVectorGraphic extends OMGraphicList {

    /**
     * Default.
     */
    private static final long serialVersionUID = 1L;

    private OMLine speedVector;

    private static final float STROKE_WIDTH = 1.5f;

    private OMGraphicList marks;

    /**
     * Start and end lat-lon points for the speed vector. Formatted lat1, lon1, lat2, lon2. Used internally by OMLine to draw a line
     * between two geographical points.
     */
    private double[] speedLL = new double[4];

    private LatLonPoint startPos;
    protected LatLonPoint endPos;

    private int[] markX = { -5, 5 };
    private int[] markY = { 0, 0 };

    protected Paint paintUsed;

    private VesselPositionData lastUpdate;

    private final VesselLayerSettings<?> layerSettings;
    
    public SpeedVectorGraphic(VesselLayerSettings<?> layerSettings, Paint lineColour) {
        this.paintUsed = lineColour;
        this.layerSettings = Objects.requireNonNull(layerSettings);
        this.init();
    }

    /**
     * Call to update this graphic when new PNT data is received.
     * 
     * @param vessel
     *            Vessel containing the position data used when drawing this graphic.
     * @param currentMapScale
     *            the current scale of the map in which this graphic is displayed.
     */
    public void update(VesselPositionData posData, float currentMapScale) {
        this.lastUpdate = posData;
        if (this.size() == 0) {
            this.init();
        }
        Position newPos = posData.getPos();
        double cogR = Math.toRadians(posData.getCog());
        // the new starting point of the speed vector equals the PNT device position.
        this.speedLL[0] = (float) newPos.getLatitude();
        this.speedLL[1] = (float) newPos.getLongitude();
        this.startPos = new LatLonPoint.Double(newPos.getLatitude(), newPos.getLongitude());
        // Calculate the length of the speed vector
        double sog = posData.getSog();
        float cogVectorLength = ScaleDependentValues.getCogVectorLength(currentMapScale, this.layerSettings);
        float length = (float) Length.NM.toRadians(cogVectorLength * (sog / 60.0));
        this.endPos = this.startPos.getPoint(length, cogR);
        this.speedLL[2] = endPos.getLatitude();
        this.speedLL[3] = endPos.getLongitude();
        this.speedVector.setLL(speedLL);
        // Add minute marks
        this.marks.clear();
        if (this.isVisible()) {
            // only add marks if this speed vector is currently displayed.
            for (int i = 1; i < cogVectorLength; i++) {
                float newMarker = (float) Length.NM.toRadians(i * (sog / 60.0));
                LatLonPoint marker = startPos.getPoint(newMarker, (float) cogR);
                RotationalPoly vtm = new RotationalPoly(markX, markY, new BasicStroke(STROKE_WIDTH), this.paintUsed);
                vtm.setLocation(marker.getLatitude(), marker.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, cogR);
                this.marks.add(vtm);
            }
        }
        if (this.layerSettings.getMovementVectorHideBelow() < posData.getSog()) {
            this.setVisible(true);
        }
        else {
            this.setVisible(false);
        }
    }

    protected void init() {
        this.speedVector = new OMLine(0, 0, 0, 0, OMGraphicConstants.LINETYPE_STRAIGHT);
        this.speedVector.setStroke(new BasicStroke(STROKE_WIDTH, // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                10.0f, // Miter limit
                new float[] { 10.0f, 8.0f }, // Dash pattern
                0.0f) // Dash phase
                );
        if (this.paintUsed == null) {
            // Use default color if none specified
            this.paintUsed = ColorConstants.VESSEL_HEADING_COLOR;
        }
        this.speedVector.setLinePaint(this.paintUsed);
        this.add(this.speedVector);
        this.marks = new OMGraphicList();
        this.add(this.marks);
    }

    @Override
    public boolean generate(Projection p) {
        return this.generate(p, true);
    }

    @Override
    public boolean generate(Projection p, boolean forceProjectAll) {
        if (this.lastUpdate != null) {
            // force an update to apply possible change to speed vector (according to new scale)
            this.update(this.lastUpdate, p.getScale());
        }
        return super.generate(p, forceProjectAll);
    }
}

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
package dk.dma.epd.ship.layers.ownship;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.util.Objects;

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMEllipse;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.GraphicsUtil;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.sensor.nmea.PntSource;
import dk.dma.epd.common.prototype.sensor.rpnt.ResilientPntData;
import dk.dma.epd.common.prototype.sensor.rpnt.ResilientPntData.JammingFlag;
import dk.dma.epd.ship.settings.layers.OwnShipLayerSettings;

/**
 * Draws the resilient PNT indicators around the own-ship graphics:
 * <ul>
 *   <li>HPL (horizontal protection level) circle<li>
 *   <li>GPS error ellipse</li>
 * </ul>
 */
public class RpntErrorGraphic  extends OMGraphicList {

    private static final long serialVersionUID  = 298296212706297238L;
    private static final float RADIUS_BOOST     = 1f;
    private static final float STROKE_WIDTH     = 1.5f;
    
    private static final Color COLOR_ERROR_ELLIPSE  = new Color(100, 200, 100);
    private static final Color COLOR_PNT_SRC_NONE   = new Color(200, 100, 100);
    private static final Color COLOR_PNT_SRC_GPS    = new Color(100, 100, 200);
    private static final Color COLOR_PNT_SRC_ELORAN = new Color(200, 200, 200);
    private static final Color COLOR_PNT_SRC_RADAR  = new Color(200, 200, 200);
    private static final Paint PAINT_JAMMING        
        = GraphicsUtil.generateTexturePaint(
                        "Jamming", 
                        new Font("Segoe UI", Font.PLAIN, 11), 
                        new Color(255, 255, 255, 100), 
                        new Color(0, 0, 0, 20), 
                        80, 
                        50);

    private OMCircle hplCicle = new OMCircle();
    private OMEllipse errorEllipse = new OMEllipse(new LatLonPoint.Double(0d, 0d), 0d, 0d, Length.METER, 0d);
    
    private OwnShipLayerSettings layerSettings;
    
    /**
     * Constructor
     */
    public RpntErrorGraphic(OwnShipLayerSettings layerSettings) {
        super();
        this.layerSettings = Objects.requireNonNull(layerSettings);
        hplCicle.setRenderType(RENDERTYPE_LATLON);
        hplCicle.setLatLon(0, 0); // Avoid NPE's
        hplCicle.setLinePaint(COLOR_PNT_SRC_GPS);
        hplCicle.setStroke(new BasicStroke(
                STROKE_WIDTH,                   // Width
                BasicStroke.CAP_SQUARE,         // End cap
                BasicStroke.JOIN_MITER,         // Join style
                10.0f,                          // Miter limit
                new float[] { 10.0f, 8.0f },    // Dash pattern
                0.0f));                         // Dash phase
        add(hplCicle);

        errorEllipse.setLinePaint(COLOR_ERROR_ELLIPSE);
        errorEllipse.setStroke(new BasicStroke(
                STROKE_WIDTH,                   // Width
                BasicStroke.CAP_SQUARE,         // End cap
                BasicStroke.JOIN_MITER,         // Join style
                10.0f,                          // Miter limit
                new float[] { 10.0f, 8.0f },    // Dash pattern
                0.0f));                         // Dash phase
        add(errorEllipse);
    }
    
    /**
     * Sets the visible state of the RPNT graphics,
     * taking into account the "Resilient PNT Layer" setting
     * 
     * @param visible whether the graphics should be visible or not
     */
    public void setVisible(boolean visible) {
        // Check if the "Resilient PNT Layer" layer is visible
        visible = visible & layerSettings.isMultiSourcePntVisible();
        super.setVisible(visible);
    }
    
    /**
     * Returns the line color to use for the HPL circle
     * which depends on the PNT source type
     * 
     * @param rpntData the resilient PNT data
     * @return the color to use for the HPL circle
     */
    private Color getHplCircleColor(ResilientPntData rpntData) {
        if (rpntData != null && rpntData.getPntSource() == PntSource.ELORAN) {
            return COLOR_PNT_SRC_ELORAN;
        } else if (rpntData != null && rpntData.getPntSource() == PntSource.RADAR) {
                return COLOR_PNT_SRC_RADAR;
        } else if (rpntData != null && rpntData.getPntSource() == PntSource.NONE) {
            return COLOR_PNT_SRC_NONE;
        }
        // Default
        return COLOR_PNT_SRC_GPS;
    }
    
    /**
     * Updates this {@code RpntErrorGraphic} with the current position and 
     * PNT error information.
     * 
     * @param vesselPositionData the vessel position data
     * @param rpntData the resilient PNT data
     */
    public void update(VesselPositionData vesselPositionData, ResilientPntData rpntData) {
        // Check if the "Resilient PNT layer is visible
        if (!layerSettings.isMultiSourcePntVisible()) {
            return;
        }
        
        // Check that we have a well-defined position
        Position pos = vesselPositionData.getPos();
        if(pos == null || rpntData == null) {
            return;
        }
        
        // Update the UI
        hplCicle.setLinePaint(getHplCircleColor(rpntData));
        hplCicle.setFillPaint(rpntData.getJammingFlag() != JammingFlag.OK ? PAINT_JAMMING : null);
        hplCicle.setRadius(
                rpntData.getHpl() / 2.0 * RADIUS_BOOST, 
                Length.METER);
        hplCicle.setLatLon(pos.getLatitude(), pos.getLongitude());
        
        errorEllipse.setAxis(
                rpntData.getErrorEllipse().getMajorAxis() * RADIUS_BOOST, 
                rpntData.getErrorEllipse().getMinorAxis() * RADIUS_BOOST, 
                Length.METER);
        errorEllipse.setRotationAngle(rpntData.getErrorEllipse().getOMBearing());
        errorEllipse.setLatLon(pos.getLatitude(), pos.getLongitude());
    }
    
    /**
     * Turn on anti-aliasing
     */
    @Override
    public void render(Graphics g) {
        Graphics2D image = (Graphics2D) g;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }
}

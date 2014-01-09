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
package dk.dma.epd.ship.layers.ownship;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

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
    
    /**
     * Constructor
     */
    public RpntErrorGraphic() {
        super();

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
        Position pos = vesselPositionData.getPos();
        if(pos == null || rpntData == null) {
            return;
        }
        
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

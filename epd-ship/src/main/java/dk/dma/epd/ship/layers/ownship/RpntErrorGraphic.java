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
    private static final float RADIUS_BOOST     = 5f;
    private static final float STROKE_WIDTH     = 2.0f;
    private static final Paint COLOR_NO_JAMMING = new Color(200, 100, 100, 0); // transparent
    private static final Paint COLOR_JAMMING    = GraphicsUtil.generateTexturePaint(
            "Jamming", new Font("Segoe UI", Font.BOLD, 11), new Color(200, 200, 200, 200), new Color(200, 100, 100, 120), 80, 50);

    private OMCircle hplCicle = new OMCircle();
    private OMEllipse errorEllipse = new OMEllipse(new LatLonPoint.Double(0d, 0d), 0d, 0d, Length.METER, 0d);
    
    /**
     * Constructor
     */
    public RpntErrorGraphic() {
        super();

        hplCicle.setRenderType(RENDERTYPE_LATLON);
        hplCicle.setLinePaint(new Color(200, 100, 100, 255));
        hplCicle.setStroke(new BasicStroke(
                STROKE_WIDTH,                   // Width
                BasicStroke.CAP_SQUARE,         // End cap
                BasicStroke.JOIN_MITER,         // Join style
                10.0f,                          // Miter limit
                new float[] { 10.0f, 8.0f },    // Dash pattern
                0.0f));                         // Dash phase
        add(hplCicle);

        errorEllipse.setLinePaint(new Color(100, 200, 100, 255));
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
        
        hplCicle.setFillPaint(rpntData.getJammingFlag() != JammingFlag.OK ? COLOR_JAMMING : COLOR_NO_JAMMING);
        hplCicle.setRadius(
                rpntData.getHpl() * RADIUS_BOOST, 
                Length.METER);
        hplCicle.setLatLon(pos.getLatitude(), pos.getLongitude());
        
        errorEllipse.setAxis(
                rpntData.getErrorEllipse().getMajorAxis() * RADIUS_BOOST, 
                rpntData.getErrorEllipse().getMinorAxis() * RADIUS_BOOST, 
                Length.METER);
        double radians = Math.toRadians(rpntData.getErrorEllipse().getBearing() - 90);
        errorEllipse.setRotationAngle(radians);
        errorEllipse.setLatLon(pos.getLatitude(), pos.getLongitude());
        System.out.println("***** error ellipse " + rpntData.getErrorEllipse() + " -> " + radians);
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

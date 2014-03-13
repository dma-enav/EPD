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
package dk.dma.epd.common.prototype.layers;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.proj.Length;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Calculator;

public class CommonRulerGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;
    float[] dash = { 0.1f };

    private Position centerPos;
    private OMCircle center;

    private int centerPixelWidth = 5;
    private int centerPixelHeight = 5;

    public CommonRulerGraphic(Position position) {
        this.centerPos = position;
        drawCenter();
    }

    private void drawCenter() {
        // Center circle is located at a specific lat/lon, hence it is
        // subject to projection changes.
        // However, its height+width ignores projection changes.
        center = new OMCircle(centerPos.getLatitude(),
                centerPos.getLongitude(), 0, 0, this.centerPixelWidth,
                this.centerPixelHeight);
        this.add(center);
    }

    @Override
    public void render(Graphics gr) {
        Graphics2D image = (Graphics2D) gr;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }

    public void updateOutside(Position gl) {
        // Clear old data
        this.clear();
        // Still display center
        this.add(this.center);
        // Create line from center to periphery (distance line)
        OMLine omLine = new OMLine(centerPos.getLatitude(),
                centerPos.getLongitude(), gl.getLatitude(), gl.getLongitude(),
                LINETYPE_GREATCIRCLE);
        this.add(omLine);

        double radius = centerPos.distanceTo(gl, CoordinateSystem.CARTESIAN);
        // Create the outer circle (the periphery)
        OMCircle outerCircle = new OMCircle(centerPos.getLatitude(),
                centerPos.getLongitude(), radius, Length.METER);
        this.add(outerCircle);
        // Text offset from center
        float offsetY = (this.centerPixelHeight + 1.0f) * 3.0f;
        // Calculate distance in nautical miles
        // TODO use Heading.GC in these calculations?
        double distNM = Calculator.range(this.centerPos, gl, Heading.RL);
        //double bearing = Calculator.bearing(this.centerPos, gl, Heading.RL);
        // Calculate angle
        double angle = this.centerPos.rhumbLineBearingTo(gl);
        
        // Pretty print distance and azimuth
        String ppDistAzimuth = "Dist.: " + Formatter.formatDistNM(distNM) + "\n" + "Angle: " + Formatter.formatDegrees(angle, 1);
        // Create OMText
        OMText text = new OMText(centerPos.getLatitude(),
                centerPos.getLongitude(), 0, offsetY, ppDistAzimuth,
                OMText.JUSTIFY_CENTER);

        this.add(text);

    }
}

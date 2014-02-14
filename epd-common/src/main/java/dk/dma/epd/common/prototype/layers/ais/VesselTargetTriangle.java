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
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.graphics.RotationalPoly;

/**
 * Graphic for vessel target shown as triangle
 */
public class VesselTargetTriangle extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    private RotationalPoly vessel;
    private Paint paint = new Color(74, 97, 205, 255);
    private Stroke stroke = new BasicStroke(2.0f);

    public VesselTargetTriangle() {
        int[] vesselX = { 0, 5, -5, 0 };
        int[] vesselY = { -10, 5, 5, -10 };
        vessel = new RotationalPoly(vesselX, vesselY, stroke, paint);
        add(vessel);
        this.setVague(true);
    }

    public void update(double lat, double lon, int units, double heading) {
        vessel.setLocation(lat, lon, units, heading);
    }

    @Override
    public void setLinePaint(Paint paint) {
        vessel.setLinePaint(paint);
    }
}

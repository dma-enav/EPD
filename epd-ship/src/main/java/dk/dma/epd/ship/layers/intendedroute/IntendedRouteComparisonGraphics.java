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
package dk.dma.epd.ship.layers.intendedroute;

import java.awt.BasicStroke;
import java.awt.Color;

import com.bbn.openmap.corba.CSpecialist.GraphicPackage.LineType;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMPoint;

import dk.dma.enav.model.geometry.Position;

/**
 * Graphic for intended route active route and intended route line
 */
public class IntendedRouteComparisonGraphics extends OMGraphicList {

    private static final long serialVersionUID = 1L;
    private static final float SCALE = 0.7f; // "Size" of graphics

    Position pos1;
    Position pos2;

    public IntendedRouteComparisonGraphics(Position pos1, Position pos2) {

        this.pos1 = pos1;
        this.pos2 = pos2;

        this.setVague(true);

        initGraphics();
    }

    private void initGraphics() {

        OMPoint endPoint1 = new OMPoint(pos1.getLatitude(), pos1.getLongitude(), (int) (5 * SCALE));

        endPoint1.setLinePaint(Color.GRAY);
        endPoint1.setOval(true);
        endPoint1.setFillPaint(Color.BLUE);

        OMPoint endPoint2 = new OMPoint(pos2.getLatitude(), pos2.getLongitude(), (int) (5 * SCALE));

        endPoint2.setLinePaint(Color.GRAY);
        endPoint2.setOval(true);
        endPoint2.setFillPaint(Color.GREEN);

        add(endPoint1);
        add(endPoint2);

        OMLine broadLine = new OMLine(pos1.getLatitude(), pos1.getLongitude(), pos2.getLatitude(), pos2.getLongitude(),
                LineType._LT_Straight);

        // Green bg line
        float alpha = 0.5f;
        Color color = new Color(0.12f, 0.70f, 0.5f, alpha); // Green

        broadLine.setLinePaint(color);

        broadLine.setStroke(new BasicStroke(10.0f * SCALE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

        // Thin red line
        OMLine thinLine = new OMLine(pos1.getLatitude(), pos1.getLongitude(), pos2.getLatitude(), pos2.getLongitude(),
                LineType._LT_Straight);

        thinLine.setStroke(new BasicStroke(2.0f * SCALE, // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                10.0f * SCALE, // Miter limit
                new float[] { 5.0f * SCALE, 4.0f * SCALE }, // Dash pattern
                0.0f)); // Dash phase)

        alpha = 1.0f;
        color = new Color(0.7f, 0.22f, 0.12f, alpha); // Red
        thinLine.setLinePaint(color);

        add(thinLine);
        add(broadLine);

    }

}

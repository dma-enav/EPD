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
package dk.dma.epd.common.prototype.layers.intendedroute;

import java.awt.BasicStroke;
import java.awt.Color;

import com.bbn.openmap.corba.CSpecialist.GraphicPackage.LineType;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.model.intendedroute.IntendedRouteFilterMessage;

/**
 * Graphic for intended route TCPA
 */
public class IntendedRouteTCPAGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;
    private IntendedRouteFilterMessage message;
    private static final float SCALE = 1.0f; // "Size" of graphics

    public IntendedRouteTCPAGraphic(IntendedRouteFilterMessage message, float scale) {

        this.message = message;

        this.setVague(true);
        
        initGraphics();

        // Graphic class

        //
        // super(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude(), LineType._LT_Straight);
        //
        // setStroke(new BasicStroke(2.0f * scale, // Width
        // BasicStroke.CAP_SQUARE, // End cap
        // BasicStroke.JOIN_MITER, // Join style
        // 10.0f * scale, // Miter limit
        // new float[] { 10.0f * scale, 8.0f * scale }, // Dash pattern
        // 0.0f)); // Dash phase)
        //
        //
        // setLinePaint(Color.YELLOW);

        // OMLine broadLine = new OMLine(startLat, startLon, endLat, endLon, routeLeg.getHeading().getOMLineType());
        // broadLine.setLinePaint(color);
        // broadLine.setStroke(new BasicStroke(12.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, broadLineDash, 0.0f));
        //
        // add(broadLine);
    }

    private void initGraphics() {
        OMPoint endPoint1 = new OMPoint(message.getPosition1().getLatitude(), message.getPosition1().getLongitude(),
                (int) (5 * SCALE));
        
        endPoint1.setLinePaint(Color.GRAY);
        endPoint1.setOval(true);
        endPoint1.setFillPaint(Color.BLUE);
        
        OMPoint endPoint2 = new OMPoint(message.getPosition2().getLatitude(), message.getPosition2().getLongitude(),
                (int) (5 * SCALE));
        
        endPoint2.setLinePaint(Color.GRAY);
        endPoint2.setOval(true);
        endPoint2.setFillPaint(Color.BLUE);
        
        
        
        add(endPoint1);
        add(endPoint2);
        
        
        OMLine broadLine = new OMLine(message.getPosition1().getLatitude(), message.getPosition1().getLongitude(), message
                .getPosition2().getLatitude(), message.getPosition2().getLongitude(), LineType._LT_Straight);
        broadLine.setLinePaint(Color.YELLOW);
        broadLine.setStroke(new BasicStroke(7.0f * SCALE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

        OMLine thinLine = new OMLine(message.getPosition1().getLatitude(), message.getPosition1().getLongitude(), message
                .getPosition2().getLatitude(), message.getPosition2().getLongitude(), LineType._LT_Straight);

        thinLine.setStroke(new BasicStroke(2.0f * SCALE, // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                10.0f * SCALE, // Miter limit
                new float[] { 5.0f * SCALE, 4.0f * SCALE }, // Dash pattern
                0.0f)); // Dash phase)

        thinLine.setLinePaint(Color.RED);

        add(thinLine);
        add(broadLine);


    }
    

    public IntendedRouteFilterMessage getMessage() {
        return message;
    }

}

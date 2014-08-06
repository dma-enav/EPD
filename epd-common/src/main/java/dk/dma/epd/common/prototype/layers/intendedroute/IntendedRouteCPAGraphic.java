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
package dk.dma.epd.common.prototype.layers.intendedroute;

import java.awt.BasicStroke;
import java.awt.Color;

import com.bbn.openmap.corba.CSpecialist.GraphicPackage.LineType;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMPoint;

import dk.dma.epd.common.prototype.model.intendedroute.IntendedRouteFilterMessage;

/**
 * Graphic for intended route TCPA
 */
public class IntendedRouteCPAGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;
    private static final boolean ALWAYS_DRAW_ENDPOINTS = true;
    private static final float SCALE = 0.7f; // "Size" of graphics
    
    private IntendedRouteFilterMessage message;
    private boolean isMinDist;

    public IntendedRouteCPAGraphic(IntendedRouteFilterMessage message, boolean isMinDist) {

        this.message = message;
        this.isMinDist = isMinDist;

        this.setVague(true);
        
        initGraphics();
    }

    private void initGraphics() {
        if (isMinDist || ALWAYS_DRAW_ENDPOINTS) {
            OMPoint endPoint1 = new OMPoint(message.getPosition1().getLatitude(), message.getPosition1().getLongitude(),
                    (int) (5 * SCALE));
            
            endPoint1.setLinePaint(Color.GRAY);
            endPoint1.setOval(true);
            endPoint1.setFillPaint(Color.BLUE);
            
            OMPoint endPoint2 = new OMPoint(message.getPosition2().getLatitude(), message.getPosition2().getLongitude(),
                    (int) (5 * SCALE));
            
            endPoint2.setLinePaint(Color.GRAY);
            endPoint2.setOval(true);
            endPoint2.setFillPaint(Color.GREEN);
            
            
            
            add(endPoint1);
            add(endPoint2);
        }
        
        OMLine broadLine = new OMLine(message.getPosition1().getLatitude(), message.getPosition1().getLongitude(), message
                .getPosition2().getLatitude(), message.getPosition2().getLongitude(), LineType._LT_Straight);
        
//        broadLine.setLinePaint(Color.YELLOW);
        float alpha = 0.5f;
        Color color = new Color(1, 1, 0, alpha); //Yellow 
        broadLine.setLinePaint(color);
        
        broadLine.setStroke(new BasicStroke(12.0f * SCALE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

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

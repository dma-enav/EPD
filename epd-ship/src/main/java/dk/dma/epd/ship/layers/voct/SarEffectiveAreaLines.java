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
package dk.dma.epd.ship.layers.voct;

import java.awt.BasicStroke;
import java.awt.Color;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.ship.layers.voct.EffectiveSRUAreaGraphics.LineType;

public class SarEffectiveAreaLines extends OMGraphicList{

    private static final long serialVersionUID = 1L;
    float[] dash = { 0.1f };
    LineType type;
    Position A;
    Position B;
    
    EffectiveSRUAreaGraphics effectiveSRUAreaGraphics;
    
    public SarEffectiveAreaLines(Position pointA, Position pointB, LineType type, EffectiveSRUAreaGraphics effectiveSRUAreaGraphics){
        this.type = type;
        this.setVague(true);
        this.effectiveSRUAreaGraphics = effectiveSRUAreaGraphics;
        
        this.A = pointA;
        this.B = pointB;
        
        
        
        lineType = LINETYPE_RHUMB;
        drawLine(pointA, pointB);
    }
 
    
    
    private void drawLine(Position A, Position B){
        this.A = A;
        this.B = B;
        
        OMLine line = new OMLine(A.getLatitude(), A.getLongitude(), B.getLatitude(), B.getLongitude(), lineType);
        
        line.setLinePaint(Color.black);
        line.setStroke(new BasicStroke(2.0f, BasicStroke.JOIN_MITER,
                BasicStroke.JOIN_MITER, 1.0f, dash, 0.0f));

        
        add(line);

        
    }
    
    public void updateLine(Position A, Position B){
        this.clear();
        drawLine(A, B);
    }
    
    public void updateArea(Position pos){
        //The position is the new one
        
        //change lat
        if (this.type == LineType.BOTTOM || this.type == LineType.TOP){
            Position newPos = Position.create(pos.getLatitude(), A.getLongitude());
            
            effectiveSRUAreaGraphics.updateLength(type, newPos);
            
            
            //Update point A;
            //Make the height smaller
            
            
        }
        
        
    }
}

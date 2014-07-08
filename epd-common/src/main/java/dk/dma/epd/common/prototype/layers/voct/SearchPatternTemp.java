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
package dk.dma.epd.common.prototype.layers.voct;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;

import dk.dma.enav.model.geometry.Position;

public class SearchPatternTemp extends OMGraphicList{

    private static final long serialVersionUID = 1L;
    float[] dash = { 0.1f };
    
    Position A;
    Position B;
    
    
    
    public SearchPatternTemp(Position pointA, Position pointB){
        
        this.setVague(true);
        
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

    
    @Override
    public void render(Graphics gr) {
        Graphics2D image = (Graphics2D) gr;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }
}

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
package dk.dma.epd.common.graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMPoly;

/**
 * A OM polygon that can be rotated
 */
public class RotationalPoly extends OMPoly {    
    private static final long serialVersionUID = 1L;
    
    private double heading;
    private int[] origXPoints;
    private int[] origYPoints;
    private int[] xPoints;
    private int[] yPoints;
    
    /**
     * Creates a OMPoly which can be rotated
     * @param origXPoints Array containing x-coordinates for the shape 
     * @param origYPoints Array containing y-coordinates for the shape
     * @param stroke Stroke which the shape is drawn with
     * @param linePaint Color which the shape is drawn with
     */
    public RotationalPoly(int[] origXPoints, int[] origYPoints, Stroke stroke, Paint linePaint) {
        super();
        this.origXPoints = origXPoints;
        this.origYPoints = origYPoints;
        this.xPoints = new int[origXPoints.length];
        this.yPoints = new int[origYPoints.length];
        System.arraycopy(origXPoints, 0, xPoints, 0, origXPoints.length);
        System.arraycopy(origYPoints, 0, yPoints, 0, origYPoints.length);
        this.heading = 0;
        this.setRenderType(OMGraphicConstants.RENDERTYPE_OFFSET);
        this.setStroke(stroke);
        this.setLinePaint(linePaint);
    }
    
    /**
     * 
     * @param latPoint Offset latitude
     * @param lonPoint Offset longitude
     * @param units radians or decimal degrees. Use OMGraphic.RADIANS or OMGraphic.DECIMAL_DEGREES
     * @param heading Heading in <i>radians</i>
     */
    public void setLocation(double latPoint, double lonPoint, int units, double heading) {
        if(this.heading != heading){
            for (int i = 0; i < origXPoints.length; i++) {
                xPoints[i] = (int) (origXPoints[i] * Math.cos(heading) - origYPoints[i] * Math.sin(heading));
                yPoints[i] = (int) (origXPoints[i] * Math.sin(heading) + origYPoints[i] * Math.cos(heading));
            }
            this.heading = heading;
        }
        super.setLocation(latPoint, lonPoint, units, xPoints, yPoints);
    }
    
    @Override
    public void render(Graphics g) {
        Graphics2D image = (Graphics2D) g;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }
}

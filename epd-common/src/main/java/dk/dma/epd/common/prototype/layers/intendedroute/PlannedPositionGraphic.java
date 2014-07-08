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
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.prototype.layers.ais.VesselGraphicComponentSelector;

public class PlannedPositionGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    // CenterRaster selectionGraphics;

    ImageIcon targetImage;
    int imageWidth;
    int imageHeight;

    private Rectangle hatchFillRectangle;
    private BufferedImage hatchFill;

    
    
    
    
    private RotationalPoly vessel;
    private Paint paint = Color.GRAY;
    private Stroke stroke = new BasicStroke(2.0f);
    private VesselGraphicComponentSelector vesselTarget;

    public PlannedPositionGraphic() {

        
        
        hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D big = hatchFill.createGraphics();
        Composite originalComposite = big.getComposite();
        // big.setColor(Color.green);
        big.setColor(new Color(0f, 0f, 0f, 0.7f));
        // big.setComposite(makeComposite(0.7f));
        big.drawLine(0, 0, 10, 10);

        hatchFillRectangle = new Rectangle(0, 0, 10, 10);
        big.setComposite(originalComposite);
        
        
        
        int[] vesselX = { 0, 5, -5, 0 };
        int[] vesselY = { -10, 5, 5, -10 };
        vessel = new RotationalPoly(vesselX, vesselY, stroke, paint);
        
        
        vessel.setFillPaint(new Color(0, 0, 0, 1));
        vessel.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));
        
        add(vessel);
    }

    public void update(double lat, double lon, int units, double heading) {
        
        vessel.setLocation(lat, lon, units, heading);
    }

    public VesselGraphicComponentSelector getVesselGraphicComponentSelector() {
        return vesselTarget;
    }

    @Override
    public void setLinePaint(Paint paint) {
        vessel.setLinePaint(paint);
    }
    
 

    public void moveSymbol(Position pos, double bearing, double width, double height) {

        if (pos != null) {
              double hdgR = Math.toRadians(bearing);
            
            vessel.setLocation(pos.getLatitude(), pos.getLongitude(), OMGraphicConstants.DECIMAL_DEGREES, hdgR);
            
            this.setVisible(true);
        }else{
            this.setVisible(false);
        }

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

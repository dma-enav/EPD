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
package dk.dma.epd.ship.event;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.proj.Projection;

import dk.dma.epd.common.prototype.event.mouse.CommonNavigationMouseMode;
import dk.dma.epd.ship.gui.ChartPanel;

public class NoGoMouseMode extends CommonNavigationMouseMode {

    private static final long serialVersionUID = 1L;

    /**
     * Mouse Mode identifier, which is "NoGo".
     */
    public static final transient String MODE_ID = "NoGo";

    private ChartPanel chartPanel;

    /**
     * Constructs a NoGoMouseListener: sets the ID of the mode, the consume mode to
     * true, and the cursor to the crosshair.
     */
    public NoGoMouseMode(ChartPanel chartPanel) {
        super(chartPanel, 0, MODE_ID);
        this.chartPanel = chartPanel;
        this.setModeCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        
        super.mouseDragged(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        
        MapBean map = (MapBean) e.getSource();
        Projection projection = map.getProjection();
        
        synchronized (this) {
            
            super.point2 = e.getPoint();
            
            int rectangleWidth = Math.abs(super.point2.x - super.point1.x);
            int rectangleHeight = Math.abs(super.point2.y - super.point1.y);
            
            // Don't bother redrawing if the rectangle is too small
            if (rectangleWidth < 10 || rectangleHeight < 10) {
                
                paintRectangle(map.getGraphics(), super.point1, super.point2);
                
                super.point1 = null;
                super.point2 = null;
                
                return;
            }
            
            Point2D[] points = new Point2D[2];
            
            points[0] = projection.inverse(super.point1);
            points[1] = projection.inverse(super.point2);
            
            this.chartPanel.getNogoDialog().setSelectedArea(points);
            this.chartPanel.getNogoDialog().setVisible(true);
            
            paintRectangle(map.getGraphics(), super.point1, super.point2);
            super.point2 = null;
        }
    }
}

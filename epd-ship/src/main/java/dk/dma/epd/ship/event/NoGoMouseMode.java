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
    private String previousMouseModeID;

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
     * If the the mouse is pressed down, the first point will be saved,
     * the second point will be reset, and the doZoom boolean will be
     * set to true, so that if the mouse is releasted, after being 
     * dragged, a zoom to that selected area will be executed.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
    }
    
    /**
     * If the mouse is dragged, a test will be done if it is a layer related
     * element. If it isn't, a rectangle will be drawn from the first point,
     * to the point of the mouse. If control is down when mouse is dragged
     * the rectangle will follow the mouse. Else the mouse will draw a rectangle
     * fitted in ratio to the map frame.
     */
    @Override
    public void mouseDragged(MouseEvent e) { 
        super.mouseDragged(e);
    }

    /**
     * This method handles a mouse released event. It will store the
     * second point and create a final rectangle from the first point 
     * to the second. If the rectangle is too small, it will not draw
     * the ractangle, but let the user select a new.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        
        // Get the map from the source.
        MapBean map = (MapBean) e.getSource();
        Projection projection = map.getProjection();
        
        // Get the second point and the length of the width and height.
        super.point2 = e.getPoint();
        int rectangleWidth = Math.abs(super.point2.x - super.point1.x);
        int rectangleHeight = Math.abs(super.point2.y - super.point1.y);
        
        synchronized (this) {
            
            // Reset points if the rectangle is too small.
            if (rectangleWidth < 10 || rectangleHeight < 10) {
                
                super.paintRectangle(map.getGraphics(), super.point1, super.point2);
                super.point1 = null;
                super.point2 = null;
                
            // Draw the rectangle if it is large enough.
            } else {
                
                Point2D[] points = new Point2D[2];
                points[0] = projection.inverse(super.point1);
                points[1] = projection.inverse(super.point2);
                
                this.chartPanel.getNogoDialog().setSelectedArea(points);
                this.chartPanel.getNogoDialog().setVisible(true);
                
                super.paintRectangle(map.getGraphics(), super.point1, super.point2);
                super.point2 = null;
            }
        }
    }

    /**
     * Returns the previous used mouse mode which was active.
     * @return The previous used mouse mode.
     */
    public String getPreviousMouseModeID() {
        return previousMouseModeID;
    }

    /**
     * Sets the previous used mouse mode which was active.
     * @param previousMouseModeID 
     *          The previous used mouse mode which was active.
     */
    public void setPreviousMouseModeID(String previousMouseModeID) {
        this.previousMouseModeID = previousMouseModeID;
    }
}

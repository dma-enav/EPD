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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.proj.Projection;

import dk.dma.epd.ship.gui.ChartPanel;

public class NoGoMouseMode extends AbstractCoordMouseMode {

    private static final long serialVersionUID = 1L;

    /**
     * Mouse Mode identifier, which is "NoGo".
     */
    public static final transient String MODE_ID = "NoGo";
    
    /**
     * Private fields
     */
    // Points between pressed and released.
    private Point pressedPoint;
    private Point draggedPoint;
    private boolean mouseDragged;
    private boolean layerMouseDrag;

    private ChartPanel chartPanel;

    /**
     * Constructs a NoGoMouseListener: sets the ID of the mode, the consume mode to
     * true, and the cursor to the crosshair.
     */
    public NoGoMouseMode(ChartPanel chartPanel) {
        
        this(true);
        this.chartPanel = chartPanel;
        
        // Reset points.
        this.pressedPoint = null;
        this.draggedPoint = null;
        
        // Set the cursor icon.
        this.setModeCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));  
    }
    
    /**
     * Constructs a NoGoMouseListener: Lets you set the consume mode. If the events
     * are consumed, then a MouseEvent is sent only to the first
     * MapMouseListener that successfully processes the event. If they are not
     * consumed, then all of the listeners get a chance to act on the event.
     * 
     * @param shouldConsumeEvents
     *            the mode setting.
     */
    public NoGoMouseMode(boolean shouldConsumeEvents) {
        super(MODE_ID, shouldConsumeEvents);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent e) {
        
        if (!mouseSupport.fireMapMousePressed(e) && 
                e.getSource() instanceof MapBean) {
            
            // Set the first point.
            this.pressedPoint = e.getPoint();
            // Ensure that the second point has not been set.
            this.draggedPoint = null;            
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        
        if (e.getSource() instanceof MapBean) {
            
            if (!this.mouseDragged) {
                this.layerMouseDrag = mouseSupport.fireMapMouseDragged(e);
            }
            
            if (!this.layerMouseDrag && SwingUtilities.isLeftMouseButton(e)) {
                
                this.mouseDragged = true;
                
                // Clean up the old rectangle.
                paintRectangle((MapBean) e.getSource(), pressedPoint, draggedPoint);
                
                // Set the next point.
                this.draggedPoint = e.getPoint();
                
                // paint the new rectangle.
                paintRectangle((MapBean) e.getSource(), pressedPoint, draggedPoint);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        
        this.mouseDragged   = false;
        this.layerMouseDrag = false;
        
        MapBean map = (MapBean) e.getSource();
        Projection projection = map.getProjection();
        
        synchronized (this) {
            
            this.draggedPoint = e.getPoint();
            
            int rectangleWidth = Math.abs(this.draggedPoint.x - this.pressedPoint.x);
            int rectangleHeight = Math.abs(this.draggedPoint.y - this.pressedPoint.y);
            
            // Don't bother redrawing if the rectangle is too small
            if (rectangleWidth < 10 || rectangleHeight < 10) {
                
                paintRectangle(map, this.pressedPoint, this.draggedPoint);
                
                this.pressedPoint = null;
                this.draggedPoint = null;
                
                return;
            }
            
            Point2D[] points = new Point2D[2];
            
            points[0] = projection.inverse(this.pressedPoint);
            points[1] = projection.inverse(this.draggedPoint);
            
            this.chartPanel.getNogoDialog().setSelectedArea(points);
            this.chartPanel.getNogoDialog().setVisible(true);
            
            paintRectangle(map, this.pressedPoint, this.draggedPoint);
            this.draggedPoint = null;

            // Set the mouse mode back to navigation.
            this.chartPanel.setMouseMode(NavigationMouseMode.MODE_ID);
        }
    }
    
    @Override
    public void listenerPaint(Graphics g) {
        paintRectangle(g, pressedPoint, draggedPoint);
    }
   
    /**
     * Draws or erases boxes between two screen pixel points. The graphics from
     * the map is set to XOR mode, and this method uses two colors to make the
     * box disappear if on has been drawn at these coordinates, and the box to
     * appear if it hasn't.
     * 
     * @param pt1
     *            one corner of the box to drawn, in window pixel coordinates.
     * @param pt2
     *            the opposite corner of the box.
     */
    private void paintRectangle(MapBean map, Point pressedPoint, Point draggedPoint) {
                
        if (map != null) {
            paintRectangle(map.getGraphics(), pressedPoint, draggedPoint);
        }
    }

    /**
     * Draws or erases boxes between two screen pixel points. The graphics from
     * the map is set to XOR mode, and this method uses two colors to make the
     * box disappear if on has been drawn at these coordinates, and the box to
     * appear if it hasn't.
     * 
     * @param pt1
     *            one corner of the box to drawn, in window pixel coordinates.
     * @param pt2
     *            the opposite corner of the box.
     */
    private void paintRectangle(Graphics g, Point pressedPoint, Point draggedPoint) {
        
        g.setXORMode(Color.LIGHT_GRAY);
        g.setColor(Color.DARK_GRAY);
        
        if (pressedPoint != null && draggedPoint != null) {
                        
            int rectangleWidth = Math.abs(draggedPoint.x - pressedPoint.x);
            int rectangleHeight = Math.abs(draggedPoint.y - pressedPoint.y);
            
            if (rectangleWidth == 0) {
                rectangleWidth++;
            }
            
            if (rectangleHeight == 0) {
                rectangleHeight++;
            }
            
            g.drawRect( pressedPoint.x < draggedPoint.x ? pressedPoint.x : draggedPoint.x, 
                        pressedPoint.y < draggedPoint.y ? pressedPoint.y : draggedPoint.y, 
                        rectangleWidth,
                        rectangleHeight);
            
            g.drawRect( pressedPoint.x < draggedPoint.x 
                            ? pressedPoint.x + (draggedPoint.x - pressedPoint.x) / 2 - 1
                            : draggedPoint.x + (pressedPoint.x - draggedPoint.x) / 2 - 1, 
                        pressedPoint.y < draggedPoint.y
                            ? pressedPoint.y + (draggedPoint.y - pressedPoint.y) / 2 - 1
                            : draggedPoint.y + (pressedPoint.y - draggedPoint.y) / 2 - 1, 
                        2, 
                        2);
        }
    }
}

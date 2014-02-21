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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.proj.Proj;
import com.bbn.openmap.proj.ProjMath;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.ChartPanel;

/**
 * The mouse mode used in navigation mode
 */
public class NavigationMouseMode extends AbstractCoordMouseMode {
    private static final long serialVersionUID = 1L;

    /**
     * Mouse Mode identifier, which is "Navigation".
     */
    public static final transient String MODE_ID = "Navigation";

    private ClickTimer clickTimer;
    protected Point point1, point2;
    protected boolean autoZoom;

    private boolean mouseDragged;
    boolean layerMouseDrag;
    private int maxScale;
    private ChartPanel chartPanel;

    /**
     * Construct a NavMouseMode. Sets the ID of the mode to the modeID, the
     * consume mode to true, and the cursor to the crosshair.
     */
    public NavigationMouseMode(ChartPanel chartPanel) {
        this(true);
        this.chartPanel = chartPanel;
        clickTimer = ClickTimer.getClickTimer();
        maxScale = EPDShip.getInstance().getSettings().getMapSettings().getMaxScale();
    }

    /**
     * Construct a NavMouseMode. Lets you set the consume mode. If the events
     * are consumed, then a MouseEvent is sent only to the first
     * MapMouseListener that successfully processes the event. If they are not
     * consumed, then all of the listeners get a chance to act on the event.
     * 
     * @param shouldConsumeEvents
     *            the mode setting.
     */
    public NavigationMouseMode(boolean shouldConsumeEvents) {
        super(MODE_ID, shouldConsumeEvents);
        // override the default cursor
        setModeCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * Handle a mousePressed MouseListener event. Erases the old navigation
     * rectangle if there is one, and then keeps the press point for reference
     * later.
     * 
     * @param e
     *            MouseEvent to be handled
     */
    @Override
    public void mousePressed(MouseEvent e) {
        e.getComponent().requestFocus();
        clickTimer.setInterval(500);
        clickTimer.startTime();
        if (!mouseSupport.fireMapMousePressed(e)
                && e.getSource() instanceof MapBean) {
            // set the new first point
            point1 = e.getPoint();
            // ensure the second point isn't set.
            point2 = null;
            autoZoom = true;
        }
    }

    private void setNewScale(Proj p, float factor) {
        float newScale = p.getScale() * factor;
        if (newScale < maxScale) {
            newScale = maxScale;
        }
        p.setScale(newScale);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        Object obj = e.getSource();

        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() != 2) {
            return;
        }

        mouseSupport.fireMapMouseClicked(e);

        if (e.getClickCount() == 2 && !e.isConsumed()) {
            e.consume();
            // Only center on left click
            if (!(e.getButton() == MouseEvent.BUTTON1)) {
                return;
            }

            if (!(obj instanceof MapBean) || point1 == null) {
                return;
            }

            MapBean map = (MapBean) obj;
            Projection projection = map.getProjection();
            Proj p = (Proj) projection;

            LatLonPoint llp = projection.inverse(e.getPoint());

            boolean shift = e.isShiftDown();
            boolean control = e.isControlDown();

            if (control) {
                if (shift) {
                    setNewScale(p, 2.0f);
                } else {
                    setNewScale(p, 0.5f);
                }
            }

            // reset the points here so the point doesn't get
            // rendered on the repaint.
            point1 = null;
            point2 = null;
            
            // Save the centering of ship to history.
            // ----------------------------
            
            EPDShip.getInstance().getMainFrame().getChartPanel().getProjectChangeListener().setShouldSave(true);
            EPDShip.getInstance().getMainFrame().getChartPanel().getProjectChangeListener().saveToHistoryBeforeMoving();

            p.setCenter(llp);
            map.setProjection(p);
            chartPanel.manualProjChange();
        }
    }

    /**
     * Handle a mouseReleased MouseListener event. If there was no drag events,
     * or if there was only a small amount of dragging between the occurence of
     * the mousePressed and this event, then recenter the map. Otherwise we get
     * the second corner of the navigation rectangle and try to figure out the
     * best scale and location to zoom in to based on that rectangle.
     * 
     * @param e
     *            MouseEvent to be handled
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        
        if (SwingUtilities.isLeftMouseButton(e)) {
            Object obj = e.getSource();
            
            if (layerMouseDrag && obj instanceof MapBean) {
                mouseSupport.fireMapMouseReleased(e);
            }
            
            if (!(obj instanceof MapBean) || !autoZoom || point1 == null
                    || point2 == null) {
                if (clickTimer.isIntervalExceeded() && !layerMouseDrag) {
                    mouseSupport.fireMapMouseReleased(e);
                }
                return;
            }
            
            mouseDragged = false;
            layerMouseDrag = false;
            
            MapBean map = (MapBean) obj;
            Projection projection = map.getProjection();
            Proj p = (Proj) projection;
            
            synchronized (this) {
                
                // If control was held down when the mouse was release, resize to fit the points.
                if ( e.isControlDown() ) {
                    
                    Point fakeRatioPoint = null;
                    Point offsetPoint = null;
                    double boxWidth = 0;
                    double boxHeight = 0;
                    double ratio = 0;
                    
                    // If the selected area is more wider than higher.
                    if ( Math.abs(e.getPoint().x - this.point1.x) > Math.abs(e.getPoint().y - this.point1.y) ) {
                                            
                        // Calculate the ratio with the help of the frame and the selected area.
                        double frameWidth = this.chartPanel.getMap().getWidth();
                        boxWidth          = e.getPoint().x - this.point1.x;
                        ratio             = frameWidth / boxWidth;
                        
                        // Calculate the hight of the box, if it had been a ratio point.
                        boxHeight = this.chartPanel.getMap().getHeight() / ratio;
                        
                        // Create a fake ratio point.
                        fakeRatioPoint = this.getRatioPoint(
                                (MapBean) e.getSource(), this.point1, new Point(e.getPoint().x, (int) (this.point1.y+boxHeight)));
                                                
                        offsetPoint = new Point((int) (this.point1.x - boxWidth/this.point2.x), (int) (e.getPoint().y - boxHeight/2));
                    
                    // If the selected area is more higher than wider.
                    } else if ( Math.abs(e.getPoint().x - this.point1.x) < Math.abs(e.getPoint().y - this.point1.y) ) {
                                            
                        // Calculate the ratio with the help of the frame and the selected area.
                        double frameHeight = this.chartPanel.getMap().getHeight();
                        boxHeight          = e.getPoint().y - this.point1.y;
                        ratio              = frameHeight / boxHeight;
                        
                        // Calculate the width of the box, if had it been a ratio point.
                        boxWidth = this.chartPanel.getMap().getWidth() / ratio;
                        
                        fakeRatioPoint = this.getRatioPoint(
                                (MapBean) e.getSource(), this.point1, new Point((int) (this.point1.x+boxWidth), e.getPoint().y));

                        offsetPoint = new Point((int) (this.point1.x - boxWidth / 2), (int) (e.getPoint().y - boxHeight));
                    }
                    
                    int centerX = (int) (offsetPoint.x + boxWidth / 2);
                    int centerY = (int) (offsetPoint.y + boxHeight / 2);
                    
                    LatLonPoint center = projection.inverse(centerX, centerY);
                    
                    float newScale;
                    if (this.point1.x < e.getPoint().x) {
                        newScale = ProjMath.getScale(offsetPoint, fakeRatioPoint, projection);                    
                    } else {
                        newScale = ProjMath.getScale(fakeRatioPoint, offsetPoint, projection);
                    }
                    
                    p.setScale(newScale);
                    p.setCenter(center);
                    map.setProjection(p);
                    
                    
                    // Reset points.
                    this.point1 = null;
                    this.point2 = null;
                    
                    
                    return;
                }
                
                point2 = getRatioPoint((MapBean) e.getSource(), point1,
                        e.getPoint());
                
                int dx = Math.abs(point2.x - point1.x);
                int dy = Math.abs(point2.y - point1.y);
                
                // Don't bother redrawing if the rectangle is too small
                if (dx < 10 || dy < 10) {
                    // clean up the rectangle, since point2 has the old
                    // value.
                    paintRectangle(map, point1, point2);
                    
                    point1 = null;
                    point2 = null;
                    
                    return;
                }
                
                // Figure out the new scale
                float newScale;
                if (point1.x < point2.x) {
                    newScale = com.bbn.openmap.proj.ProjMath.getScale(point1,
                            point2, projection);
                } else {
                    newScale = com.bbn.openmap.proj.ProjMath.getScale(point2,
                            point1, projection);
                }
                
                // Figure out the center of the rectangle
                int centerx = Math.min(point1.x, point2.x) + dx / 2;
                int centery = Math.min(point1.y, point2.y) + dy / 2;
                LatLonPoint center = projection.inverse(centerx, centery);

                if (newScale < maxScale) {
                    newScale = maxScale;
                }
                
                // on the repaint.
                point1 = null;
                point2 = null;
                
                
                // Save the scaling to history.
                // ----------------------------
                
                EPDShip.getInstance().getMainFrame().getChartPanel().getHistoryListener().setShouldSave(true);
                EPDShip.getInstance().getMainFrame().getChartPanel().getHistoryListener().saveToHistoryBeforeMoving();
                
                // Move to the new view.
                p.setScale(newScale);
                p.setCenter(center);
                map.setProjection(p);
                chartPanel.manualProjChange();
                
                // Toggle buttons for navigation.
            
            }
        }
    }

    /**
     * Handle a mouseEntered MouseListener event. The boolean autoZoom is set to
     * true, which will make the delegate ask the map to zoom in to a box that
     * is drawn.
     * 
     * @param e
     *            MouseEvent to be handled
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        autoZoom = true;
    }

    /**
     * Handle a mouseExited MouseListener event. The boolean autoZoom is set to
     * false, which will cause the delegate to NOT ask the map to zoom in on a
     * box. If a box is being drawn, it will be erased. The point1 is kept in
     * case the mouse comes back on the screen with the button still down. Then,
     * a new box will be drawn with the original mouse press position.
     * 
     * @param e
     *            MouseEvent to be handled
     */
    @Override
    public void mouseExited(MouseEvent e) {

        super.mouseExited(e);

        if (e.getSource() instanceof MapBean) {
            // don't zoom in, because the mouse is off the window.
            autoZoom = false;
            // clean up the last box drawn
            paintRectangle((MapBean) e.getSource(), point1, point2);
            // set the second point to null so that a new box will be
            // drawn if the mouse comes back, and the box will use the
            // old
            // starting point, if the mouse button is still down.
            point2 = null;

        }
    }

    // Mouse Motion Listener events
    // /////////////////////////////

    /**
     * Handle a mouseDragged MouseMotionListener event. A rectangle is drawn
     * from the mousePressed point, since I'm assuming that I'm drawing a box to
     * zoom the map to. If a previous box was drawn, it is erased.
     * 
     * @param e
     *            MouseEvent to be handled
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getSource() instanceof MapBean) {
            super.mouseDragged(e);
            if (!mouseDragged) {
                layerMouseDrag = mouseSupport.fireMapMouseDragged(e);
            }
            if (!layerMouseDrag) {
                if (!javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }
                mouseDragged = true;

                if (!autoZoom) {
                    return;
                }

                // clean up the old rectangle, since point2 has the old
                // value.
                paintRectangle((MapBean) e.getSource(), point1, point2);
                // paint new rectangle
                // point2 = e.getPoint();
                if (!e.isControlDown()) {
                    point2 = getRatioPoint((MapBean) e.getSource(), point1, e.getPoint());
                } else {
                    this.point2 = e.getPoint();
                }
                
                paintRectangle((MapBean) e.getSource(), point1, point2);
            }
        }
    }

    /**
     * Given a MapBean, which provides the projection, and the starting point of
     * a box (pt1), look at pt2 to see if it represents the ratio of the
     * projection map size. If it doesn't, provide a point that does.
     */
    protected Point getRatioPoint(MapBean map, Point pt1, Point pt2) {

        if (map != null && pt1 != null && pt2 != null) {

            Projection proj = map.getProjection();
            float mapRatio = (float) proj.getHeight() / (float) proj.getWidth();

            float boxHeight = pt1.y - pt2.y;
            float boxWidth = pt1.x - pt2.x;
            float boxRatio = Math.abs(boxHeight / boxWidth);
            int isNegative = -1;
            if (boxRatio > mapRatio) {
                // box is too tall, adjust boxHeight
                if (boxHeight < 0) {
                    isNegative = 1;
                }
                boxHeight = Math.abs(mapRatio * boxWidth);
                pt2.y = pt1.y + isNegative * (int) boxHeight;

            } else if (boxRatio < mapRatio) {
                // box is too wide, adjust boxWidth
                if (boxWidth < 0) {
                    isNegative = 1;
                }
                boxWidth = Math.abs(boxHeight / mapRatio);
                pt2.x = pt1.x + isNegative * (int) boxWidth;
            }
        }
        return pt2;
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
    protected void paintRectangle(MapBean map, Point pt1, Point pt2) {
        if (map != null) {
            paintRectangle(map.getGraphics(), pt1, pt2);
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
    protected void paintRectangle(Graphics g, Point pt1, Point pt2) {
        g.setXORMode(java.awt.Color.lightGray);
        g.setColor(java.awt.Color.darkGray);

        if (pt1 != null && pt2 != null) {
            int width = Math.abs(pt2.x - pt1.x);
            int height = Math.abs(pt2.y - pt1.y);

            if (width == 0) {
                width++;
            }
            if (height == 0) {
                height++;
            }

            g.drawRect(pt1.x < pt2.x ? pt1.x : pt2.x, pt1.y < pt2.y ? pt1.y
                    : pt2.y, width, height);
            g.drawRect(pt1.x < pt2.x ? pt1.x + (pt2.x - pt1.x) / 2 - 1 : pt2.x
                    + (pt1.x - pt2.x) / 2 - 1,
                    pt1.y < pt2.y ? pt1.y + (pt2.y - pt1.y) / 2 - 1 : pt2.y
                            + (pt1.y - pt2.y) / 2 - 1, 2, 2);
        }
    }

    /**
     * Called by the MapBean when it repaints, to let the MouseMode know when to
     * update itself on the map. PaintListener interface.
     */
    @Override
    public void listenerPaint(java.awt.Graphics g) {
        // will be properly rejected of point1, point2 == null
        paintRectangle(g, point1, point2);
    }

}

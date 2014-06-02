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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.proj.Proj;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

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
     * Constructs a NoGoMouseListener: sets the ID of the mode, the consume mode to true, and the cursor to the crosshair.
     */
    public NoGoMouseMode(ChartPanel chartPanel) {
        super(chartPanel, 0, MODE_ID);
        this.chartPanel = chartPanel;
        this.setModeCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * This method handles a mouse released event. It will store the second point and create a final rectangle from the first point
     * to the second. If the rectangle is too small, it will not draw the ractangle, but let the user select a new.
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        if (super.mouseDragged) {

            if (e != null) {

                // Get the map from the source.
                MapBean map = (MapBean) e.getSource();
                Projection projection = map.getProjection();

                // Get the second point and the length of the width and height.
                super.point2 = e.getPoint();

                if (point2 != null) {

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
            }
        }
    }

    /**
     * If the mouse is pressed twice right after each other, this mouse event handler method will update the location on the map by
     * the position of the mouse. If the control button is pushed down when this method is called, a new scale value will be
     * calculated so that a zoom to the new position will be done too. If the control and shift button are both down at when called
     * a zoom out from the point will be done.
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        super.mouseClicked(e);
        if (e.getSource() instanceof MapBean && SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed()) {

            // Fire the mouse support.
            super.mouseSupport.fireMapMouseClicked(e);

            // Get the map and a new location from the clicked position.
            MapBean map = (MapBean) e.getSource();
            Projection projection = map.getProjection();
            Proj proj = (Proj) projection;
            LatLonPoint llp = projection.inverse(e.getPoint());

            // Update the scale factor.
            proj.setScale(this.getNewScale(proj.getScale(), 2.0f));

            // Reset the points.
            this.point1 = null;
            this.point2 = null;

            // Update location on map.
            proj.setCenter(llp);
            map.setProjection(proj);
        }
    }

    /**
     * If the mouse is dragged, a test will be done if it is a layer related element. If it isn't, a rectangle will be drawn from
     * the first point, to the point of the mouse. If control is down when mouse is dragged the rectangle will follow the mouse.
     * Else the mouse will draw a rectangle fitted in ratio to the map frame.
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if (e.getSource() instanceof MapBean && SwingUtilities.isLeftMouseButton(e) && this.doZoom) {

            super.mouseDragged(e);

            if (!this.mouseDragged) {
                this.layerMouseDrag = super.mouseSupport.fireMapMouseDragged(e);
            }

            if (this.layerMouseDrag && this.mouseExited) {

                this.mouseReleased(e);
                this.mouseExited = false;

            } else if (!this.layerMouseDrag) {

                this.mouseDragged = true;

                // Clear up the old point.
                this.paintRectangle(((MapBean) e.getSource()).getGraphics(), this.point1, this.point2);

                this.point2 = e.getPoint();

                // Clear up the old point.
                this.paintRectangle(((MapBean) e.getSource()).getGraphics(), this.point1, this.point2);

                // Repaint new rectangle.
                ((MapBean) e.getSource()).repaint();
            }
        }
    }

    /**
     * Returns the previous used mouse mode which was active.
     * 
     * @return The previous used mouse mode.
     */
    public String getPreviousMouseModeID() {
        return previousMouseModeID;
    }

    /**
     * Sets the previous used mouse mode which was active.
     * 
     * @param previousMouseModeID
     *            The previous used mouse mode which was active.
     */
    public void setPreviousMouseModeID(String previousMouseModeID) {
        this.previousMouseModeID = previousMouseModeID;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
    }
}

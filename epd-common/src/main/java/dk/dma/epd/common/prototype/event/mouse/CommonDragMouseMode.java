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
package dk.dma.epd.common.prototype.event.mouse;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import com.bbn.openmap.MapBean;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.util.DraggableLayerMapBean;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;

public class CommonDragMouseMode extends AbstractCoordMouseMode {

    /**
     * Public fields.
     */
    public final Cursor DRAG_CURSOR; // Default cursor when mouse is over map in drag mode.
    public final Cursor DRAG_DOWN_CURSOR; // Default cursor for when mouse is down.

    /**
     * Private fields
     */
    private static final long serialVersionUID = 1L;
    private boolean isPanning;
    private boolean mouseDragged;
    private boolean mouseExited;
    private boolean layerMouseDrag;

    /**
     * Protected fields
     */
    protected ChartPanelCommon chartPanel;

    /**
     * Creates a new CommonDragMouseMode. <br>
     * The object is common behaviour for dragging in ship side and shore side.
     * 
     * @param chartPanel
     *            The ChartPanel of the map which should be dragged on.
     * 
     * @param modeid
     *            The modeid of the route edit mouse mode.
     */
    public CommonDragMouseMode(ChartPanelCommon chartPanel, String modeid) {
        super(modeid, true);
        this.chartPanel = chartPanel;

        // Create the drag cursor.
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image cursorIcon = EPD.res().getCachedImageIcon("images/toolbar/drag_mouse.png").getImage();
        this.DRAG_CURSOR = tk.createCustomCursor(cursorIcon, new Point(7, 7), "drag");

        // Create the drag-when-button-is-down cursor.
        cursorIcon = EPD.res().getCachedImageIcon("images/toolbar/drag_on_mouse.png").getImage();
        this.DRAG_DOWN_CURSOR = tk.createCustomCursor(cursorIcon, new Point(7, 7), "dragDown");

        // Set the drag cursor.
        this.setModeCursor(this.DRAG_CURSOR);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if (e.getSource() instanceof MapBean && SwingUtilities.isRightMouseButton(e)) {
            mouseExited = false;
            layerMouseDrag = false;
            mouseDragged = false;
            
            DraggableLayerMapBean map = this.chartPanel.getMap();
            map.stopDragging(e);
        }
    }

    /**
     * This method is called when the mouse is pressed and dragged across the map. The method will take a screen shot of the map,
     * and let the user drag the map around, and update the cursor to a dragging hand.
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        super.mouseDragged(e);
        if (e.getSource() instanceof MapBean && SwingUtilities.isLeftMouseButton(e)) {

            // Ensure that other layer elements can be dragged (fx waypoints)
            if (!this.mouseDragged) {
                this.layerMouseDrag = super.mouseSupport.fireMapMouseDragged(e);
            }

            /*
             * This if statement is used to ensure that the navigation mouse mode can be used after a NoGo area has been selected.
             * This is to make sure that the variables layerMouseDrag and mouseDragged are initialized back to their default values.
             */
            if (this.layerMouseDrag && this.mouseExited) {
                this.mouseReleased(e);
                this.mouseExited = false;

                // If other layer elements was not pressed, do the dragging of map.
            } else if (!this.layerMouseDrag) {
                this.drag(e);
            }
        }
    }

    /**
     * Completes the dragging method. This is in a seperate method, because the same method is used when dragging in route edit
     * mouse mode.
     * 
     * @param e
     *            The mouse event.
     */
    protected void drag(MouseEvent e) {

        this.mouseDragged = true;

        DraggableLayerMapBean map = this.chartPanel.getMap();

        if (!this.isPanning) {
            this.isPanning = true;
            map.startDragging(e);

        } else {
            map.drag(e);
        }

        // Change cursor to a dragging hand.
        this.chartPanel.setCursor(this.DRAG_DOWN_CURSOR);
    }

    /**
     * This method is called when the mouse is released. It will get the coordinates for the current view (which is dragged to) and
     * set center of the map to that location.
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        super.mouseReleased(e);

        if (this.isPanning && e.getSource() instanceof MapBean) {

            DraggableLayerMapBean map = (DraggableLayerMapBean) e.getSource();

            this.isPanning = false;
            this.mouseDragged = false;
            map.stopDragging(e);
        }

    }

    /**
     * Handles a mouse exited event. The boolean is mouseExited is set to true, so that dragging can be activated after NoGo area
     * has been selected.
     */
    @Override
    public void mouseExited(MouseEvent e) {

        if (e.getSource() instanceof MapBean) {

            super.mouseExited(e);
            // this.mouseExited = true;
            mouseDragged = false;
            mouseExited = false;
            layerMouseDrag = false;
        }
    }

    @Override
    public void setActive(boolean active) {
        mouseDragged = false;
        mouseExited = false;
        layerMouseDrag = false;
        super.setActive(active);

    }

}

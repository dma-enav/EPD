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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.proj.Projection;

import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;

public class CommonDragMouseMode extends AbstractCoordMouseMode {

    /**
     * Public fields.
     */
    public final Cursor DRAG_CURSOR;
    public final Cursor DRAG_DOWN_CURSOR;

    /**
     * Private fields
     */
    private static final long serialVersionUID = 1L;
    private boolean isPanning;
    private boolean mouseDragged;
    private boolean layerMouseDrag;
    private BufferedImage bufferedMapImage;
    private BufferedImage onScreenMap;
    private int oX, oY;

    /**
     * Protected fields
     */
    protected boolean calledFromShore;
    protected ChartPanelCommon chartPanel;

    /**
     * Creates a new CommonDragMouseMode. <br>
     * The object is common behaviour for dragging in ship side and
     * shore side.
     * 
     * @param chartPanel
     *          The ChartPanel of the map which should be dragged
     *          on.
     * 
     * @param modeid
     *          The modeid of the route edit mouse mode.
     */
    public CommonDragMouseMode(ChartPanelCommon chartPanel, String modeid) {
        super(modeid, true);
        this.chartPanel = chartPanel;
        
        // Create the drag cursor.
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image cursorIcon = tk.getImage(this.getClass().getResource("/images/toolbar/drag_mouse.png"));
        this.DRAG_CURSOR = tk.createCustomCursor(cursorIcon, new Point(0, 0), "drag");
        
        // Create the drag-when-button-is-down cursor.
        cursorIcon = tk.getImage(this.getClass().getResource("/images/toolbar/drag_on_mouse.png"));
        this.DRAG_DOWN_CURSOR = tk.createCustomCursor(cursorIcon, new Point(0, 0), "dragDown");
        
        // Set the drag cursor.
        this.setModeCursor(this.DRAG_CURSOR);
    }
    
    /**
     * This method is called each time the mouse mode changes
     */
    @Override
    public void setActive(boolean active) {
        
        if (!active) {
            
            if (this.bufferedMapImage != null) {
                this.bufferedMapImage.flush();
            }
        }
    }
    
    /**
     * This method is called when the mouse is pressed and dragged across
     * the map. The method will take a screen shot of the map, and let the
     * user drag the map around, and update the cursor to a dragging hand.
     * Since Ship and Shore have separate behaviours the boolean
     * "calledFromShore" will decide if Shore side of this method will run,
     * or the ship side will run.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
                
        super.mouseDragged(e);
        if (e.getSource() instanceof MapBean &&
                e.getButton() == MouseEvent.BUTTON1) {
                        
            // Ensure that other layer elements can be dragged (fx waypoints)
            if (!this.mouseDragged) {
                this.layerMouseDrag = super.mouseSupport.fireMapMouseDragged(e);
            }
            
            // If other layer elements was not pressed, do the dragging of map.
            if (!this.layerMouseDrag) {
                this.drag(e);
            }
        }
    }

    /**
     * Completes the dragging method. This is in a seperate method,
     * because the same method is used when dragging in route edit
     * mouse mode.
     * @param e
     *          The mouse event.
     */
    protected void drag(MouseEvent e) {
        
        this.mouseDragged = true;
        
        MapBean map = this.chartPanel.getMap();
        Point2D pnt = map.getNonRotatedLocation(e);
        int x = (int) pnt.getX();
        int y = (int) pnt.getY();
        
        // Ship side of the method.
        if (!this.calledFromShore) {
            if (!this.isPanning) {
                
                this.isPanning = true;
                this.chartPanel.getDragMapRenderer().updateFinalBuffer();
                
                // Create the screen shot.
                this.onScreenMap = new BufferedImage(
                        map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_RGB);
                map.paint(this.onScreenMap.getGraphics());
                
                this.oX = x;
                this.oY = y;
                
                this.chartPanel.getMap().setVisible(false);
                
            } else {
                
                int startX = map.getWidth();
                int startY = map.getHeight();
                int posX   = startX - (x - this.oX);
                int posY   = startY - (y - this.oY);
                
                    BufferedImage offScreenMap = 
                            this.chartPanel.getDragMapRenderer().getFinalBuffer().getSubimage(
                                    posX, posY, this.chartPanel.getWidth(), this.chartPanel.getHeight());
                    
                    BufferedImage renderImage = offScreenMap;
                    renderImage.getGraphics().drawImage(this.onScreenMap, x-this.oX, y-this.oY, null);
                    this.chartPanel.getGraphics().drawImage(renderImage, 0, 0, null);
            } 
            
        // Shore side of the method.
        } else if (this.calledFromShore) {
                                
            if (!this.isPanning) {
                this.isPanning = true;
                
                this.chartPanel.getDragMapRenderer().updateFinalBuffer();
                
                this.onScreenMap = new BufferedImage(
                        this.chartPanel.getWidth(), this.chartPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
                
                map.paint(this.onScreenMap.getGraphics());
                this.oX = x;
                this.oY = y;
                
            } else {
                                        
                int startX = this.chartPanel.getWidth();
                int startY = this.chartPanel.getHeight();
                
                int posX = startX - (x - this.oX);
                int posY = startY - (y - this.oY);
                
                final BufferedImage offScreenMap = this.chartPanel.getDragMapRenderer().getFinalBuffer().getSubimage(posX,
                        posY, this.chartPanel.getWidth(), this.chartPanel.getHeight());
                
                final BufferedImage renderImage = offScreenMap;
                //renderImage.getGraphics().drawImage(offScreenMap,0,0,null);                        
                renderImage.getGraphics().drawImage(this.onScreenMap,x-oX,y-oY,null);
                
                this.chartPanel.getMap().getGraphics().drawImage(renderImage, 0,0, null);
            }
        }
        
        // Change cursor to a dragging hand.
        this.chartPanel.setCursor(this.DRAG_DOWN_CURSOR);
    }
    
    /**
     * This method is called when the mouse is released. It will get
     * the coordinates for the current view (which is dragged to) and
     * set center of the map to that location.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        
        super.mouseReleased(e);
        
        if (this.isPanning && 
                e.getSource() instanceof MapBean) {
            
            MapBean map = (MapBean) e.getSource();
            Projection projection = map.getProjection();
            Point2D center = projection.forward(projection.getCenter());
            
            Point2D pnt = map.getNonRotatedLocation(e);
            int x = (int) pnt.getX();
            int y = (int) pnt.getY();
            
            center.setLocation(center.getX() - x + this.oX, center.getY() - y + this.oY);
            map.setCenter(projection.inverse(center));
            
            this.isPanning = false;
            this.mouseDragged = false;
            this.chartPanel.getMap().setVisible(true);
        }
        
        this.layerMouseDrag = false;
    }
}

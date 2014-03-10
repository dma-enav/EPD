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
package dk.dma.epd.shore.event;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import dk.dma.epd.common.prototype.event.mouse.CommonRouteEditMouseMode;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;

/**
 * Mouse mode for route edit
 */
public class RouteEditMouseMode extends CommonRouteEditMouseMode {

    private static final long serialVersionUID = 1L;

    public static final transient String MODEID = "RouteEdit";

    private JPanel glassFrame;

    public RouteEditMouseMode(ChartPanel chartPanel) {
        super(chartPanel, MODEID);
        
        this.chartPanel.getMap().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    
    @Override
    public void setActive(boolean active) {
        super.setActive(active);
    }
    
    /**
    * Called when a CoordMouseMode is added to a BeanContext, or when another
    * object is added to the BeanContext after that. The CoordMouseMode looks
    * for an InformationDelegator to use to fire the coordinate updates. If
    * another InforationDelegator is added when one is already set, the later
    * one will replace the current one.
    * 
    * @param someObj an object being added to the BeanContext.
    */
    @Override
    public void findAndInit(Object someObj) {
        if (someObj instanceof JMapFrame) {
            this.glassFrame = ((JMapFrame) someObj).getGlassPanel();
            this.glassFrame.setVisible(true);
        }

        super.findAndInit(someObj);
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
        
        this.glassFrame.setCursor(super.DRAG_DOWN_CURSOR);
        super.drag(e);
    }
    
    /**
     * This method is called when the mouse is released. It will get
     * the coordinates for the current view (which is dragged to) and
     * set center of the map to that location.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        this.glassFrame.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
    
    /**
     * This method changes the cursor to a crosshair cursor when the 
     * mouse is hovering a map.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        this.glassFrame.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
}

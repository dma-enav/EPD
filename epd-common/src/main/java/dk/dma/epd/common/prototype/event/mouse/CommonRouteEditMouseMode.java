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

import java.awt.event.MouseEvent;

import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;

public class CommonRouteEditMouseMode extends CommonDragMouseMode {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new CommonRouteEditMouseMode object.<br>
     * The object is common to route edit object and extends the
     * CommonDragMouseMode, so that methods for dragging and
     * releasing can be reused.
     * 
     * @param chartPanel
     *          The ChartPanel of the map which should be dragged
     *          on.
     * 
     * @param modeid
     *          The modeid of the route edit mouse mode.
     */
    public CommonRouteEditMouseMode(ChartPanelCommon chartPanel, String modeid) {
        super(chartPanel, modeid);
    }
    
    /**
     * Sets this mouse mode to the active mouse mode. 
     */
    @Override
    public void setActive(boolean active) {
        super.setActive(active);
    }
    
    /**
     * Runs the super method for when the mouse is released. This move
     * the user to the dragged location of the map.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        
        super.drag(e);
    }
}

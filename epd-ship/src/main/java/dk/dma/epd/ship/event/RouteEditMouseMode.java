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

import dk.dma.epd.common.prototype.event.mouse.CommonRouteEditMouseMode;
import dk.dma.epd.ship.gui.ChartPanel;

public class RouteEditMouseMode extends CommonRouteEditMouseMode {

    private static final long serialVersionUID = 1L;

    /**
     * Mouse Mode identifier, which is "DragMouse".
     */
    public static final transient String MODE_ID = "RouteEdit";

    /**
     * Creates a new RouteEditMouseMode object.<br> 
     * This object handles mouse dragging in route navigation. 
     * 
     * @param chartPanel
     *          The ChartPanel of the map which should be dragged
     *          on.
     */
    public RouteEditMouseMode(ChartPanel chartPanel) {
        super(chartPanel, MODE_ID);        

        // Changes the cursor to crosshair cursor.
        this.setModeCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
}

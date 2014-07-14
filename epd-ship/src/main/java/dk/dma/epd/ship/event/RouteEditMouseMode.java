/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

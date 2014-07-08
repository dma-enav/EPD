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

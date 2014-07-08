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

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import dk.dma.epd.common.prototype.EPD;

public class CommonDistanceCircleMouseMode extends AbstractCoordMouseMode {

    private static final long serialVersionUID = 1L;
    private String previousActiveMouseModeID;
    
    public static final transient String MODE_ID = "DistanceCircle";

    /**
     * 
     * @param chartPanel
     */
    public CommonDistanceCircleMouseMode() {
        super(MODE_ID, false);        
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
        if (SwingUtilities.isLeftMouseButton(e) ||  
                SwingUtilities.isRightMouseButton(e)) {
            mouseSupport.fireMapMouseClicked(e);
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        EPD.getInstance().getMainFrame().getActiveChartPanel().getMap().setCursor(
                Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
    
    /**
     * Set the MODE_ID of the mouse mode that was active prior to setting this
     * mouse mode as the active mouse mode.
     * 
     * @param modeID
     */
    public void setPreviousMouseModeModeID(String modeID) {
        this.previousActiveMouseModeID = modeID;
    }

    /**
     * Get the MODE_ID of the mouse mode that was the active mouse mode prior to
     * setting this mouse mode as the active mouse mode.
     * 
     * @return The MODE_ID of the previous active mouse mode, or null if no
     *         previous active mouse mode was registered (via setter call).
     */
    public String getPreviousMouseMode() {
        return this.previousActiveMouseModeID;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
    }
}

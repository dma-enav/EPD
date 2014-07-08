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
import java.awt.event.MouseEvent;

import dk.dma.epd.common.prototype.event.mouse.AbstractCoordMouseMode;
import dk.dma.epd.ship.EPDShip;

public class DistanceCircleMouseMode extends AbstractCoordMouseMode {
    
    private static final long serialVersionUID = 1L;

    public static final transient String MODE_ID = "DistanceCircle";

    private String previousActiveMouseModeID;

    /**
     * Create a distance circle mouse mode.
     * 
     * @param shouldConsumeEvents
     *            If events should be consumed.
     */
    public DistanceCircleMouseMode(boolean shouldConsumeEvents) {
        super(MODE_ID, shouldConsumeEvents);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // At this stage, this mouse mode only delegates BUTTON1 and BUTTON3
        // events to its listeners.
        if (e.getButton() == MouseEvent.BUTTON1
                || e.getButton() == MouseEvent.BUTTON3) {
            mouseSupport.fireMapMouseClicked(e);
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        EPDShip.getInstance().getMainFrame().getChartPanel().getMap().setCursor(
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

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
        EPDShip.getInstance().getMainFrame().getChartPanel().getMap().setCursor(
                Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
}

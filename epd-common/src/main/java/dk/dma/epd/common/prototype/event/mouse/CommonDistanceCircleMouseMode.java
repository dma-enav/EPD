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
import java.awt.event.MouseEvent;

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
        
        if (e.getButton() == MouseEvent.BUTTON1 || 
                e.getButton() == MouseEvent.BUTTON3) {
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

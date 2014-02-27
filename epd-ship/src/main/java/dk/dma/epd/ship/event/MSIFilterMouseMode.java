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
import dk.dma.epd.common.prototype.event.mouse.AbstractCoordMouseMode;

/**
 * Mouse mode for MSI filter 
 */
public class MSIFilterMouseMode extends AbstractCoordMouseMode {
    private static final long serialVersionUID = 1L;
    public static final transient String MODE_ID = "MSIFilter";
    
    /**
     * Construct a NavMouseMode. Sets the ID of the mode to the modeID, the
     * consume mode to true, and the cursor to the crosshair.
     */
    public MSIFilterMouseMode() {
        this(true);
    }
    
    public MSIFilterMouseMode(boolean shouldConsumeEvents) {
        super(MODE_ID, shouldConsumeEvents);
        // override the default cursor
        setModeCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
}

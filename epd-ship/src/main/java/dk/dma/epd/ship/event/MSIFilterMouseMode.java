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

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
package dk.dma.epd.common.prototype.sensor.pnt;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.util.Util;

/**
 * Component to handle multi-source PNT messages
 */
public class MultiSourcePntHandler extends MapHandlerChild implements IMultiSourcePntListener, Runnable {
    
    @SuppressWarnings("unused")
    private final PntHandler pntHandler;
    
    public MultiSourcePntHandler(PntHandler pntHandler) {
        this.pntHandler = pntHandler;
        EPD.startThread(this, "MultiSourcePntHandler");        
    }
    
    public void receiveSomeMessage(String message) {
        // TODO implement message classes and receive methods
        // TODO send pnt to pntHandler when available
        // TODO invalidate data in pntHandler if indicated from messages
        
    }
    
    
    @Override
    public void run() {
        while (true) {
            // TODO: Validate if data is still fresh
            Util.sleep(10000);
        }

        
    }

}

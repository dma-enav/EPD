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
package dk.dma.epd.common.prototype.ais;

import java.util.Date;

import net.jcip.annotations.ThreadSafe;
import dk.dma.ais.message.AisMessage4;

/**
 * Class representing a base station target
 */
@ThreadSafe
public class BaseStationTarget extends AisTarget {
    
    private static final long serialVersionUID = 1L;
    
    // TODO
    
    public BaseStationTarget() {
        super();    
    }
    
    public BaseStationTarget(BaseStationTarget bsTarget) {
        // TODO
    }
        
    public synchronized void update(AisMessage4 msg4) {
        // TODO
    }
    
    @Override
    public synchronized boolean hasGone(Date now, boolean strict) {
        long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;        
        // Base gone "loosely" on ITU-R Rec M1371-4 4.2.1  (10 seconds)
        long tol = 120; // 2 minutes        
        return elapsed > tol;
    }

}

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

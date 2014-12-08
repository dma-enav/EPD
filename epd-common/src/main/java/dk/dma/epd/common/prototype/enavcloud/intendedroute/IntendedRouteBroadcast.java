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
package dk.dma.epd.common.prototype.enavcloud.intendedroute;

import net.maritimecloud.net.broadcast.BroadcastMessage;

/**
 * Used for intended route broadcasts
 */
public class IntendedRouteBroadcast extends BroadcastMessage {

    private IntendedRouteMessage route;
    
    public IntendedRouteBroadcast() {
        
    }
    
    public IntendedRouteMessage getRoute() {
        return route;
    }
    
    public void setRoute(IntendedRouteMessage route) {
        this.route = route;
    }
    
}
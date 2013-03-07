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
package dk.dma.epd.common.prototype.enavcloud;

import dk.dma.enav.communication.broadcast.BroadcastMessage;
import dk.dma.enav.model.voyage.Route;

//import dk.dma.epd.common.prototype.model.route.Route;


public class EnavRouteBroadcast extends BroadcastMessage {

    private Route intendedRoute;

    public Route getIntendedRoute() {
        return intendedRoute;
    }

    public void setIntendedRoute(Route intendedRoute) {
        this.intendedRoute = intendedRoute;
    }


    
//    public static void main(String[] args) throws Exception {
//        ObjectMapper m = new ObjectMapper();
//        
//   String msg=m.writeValueAsString(new Route());
//
//   System.out.println(    m.readValue(msg, Route.class));
//    
//    }
    
}

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
package dk.dma.epd.ship.service;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.service.communication.enavcloud.EnavCloudHandler;
import dk.dma.epd.ship.service.intendedroute.ActiveRouteProvider;
import dk.dma.epd.ship.service.intendedroute.IntendedRouteService;

/**
 * Component offering e-Navigation services
 */
public class EnavServiceHandler extends MapHandlerChild {

    private EnavCloudHandler enavCloudHandler;
    private IntendedRouteService intendedRouteService;

    public EnavServiceHandler() {

    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof RouteManager) {
            intendedRouteService = new IntendedRouteService(this, (ActiveRouteProvider) obj);
            ((RouteManager) obj).addListener(intendedRouteService);
            
            ((RouteManager) obj).setIntendedRouteService(intendedRouteService);
            // intendedRouteService.start();
        } else if (obj instanceof EnavCloudHandler) {
            enavCloudHandler = (EnavCloudHandler) obj;
            enavCloudHandler.start();
        }
    }

    public EnavCloudHandler getEnavCloudHandler() {
        return enavCloudHandler;
    }

}

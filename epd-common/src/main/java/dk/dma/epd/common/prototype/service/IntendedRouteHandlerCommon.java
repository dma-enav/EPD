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
package dk.dma.epd.common.prototype.service;

import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.broadcast.BroadcastListener;
import net.maritimecloud.net.broadcast.BroadcastMessageHeader;
import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.enavcloud.IntendedRouteBroadcast;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;

/**
 * Intended route service implementation.
 * <p>
 * Listens for intended route broadcasts, and updates the
 * vessel target when one is received.
 */
public class IntendedRouteHandlerCommon 
    extends EnavServiceHandlerCommon {

    private AisHandlerCommon aisHandler;

    /**
     * Constructor
     */
    public IntendedRouteHandlerCommon() {
        super();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        
        // Hook up as a broadcast listener
        connection.broadcastListen(IntendedRouteBroadcast.class,
                new BroadcastListener<IntendedRouteBroadcast>() {
                    public void onMessage(BroadcastMessageHeader l, IntendedRouteBroadcast r) {

                        getStatus().markCloudReception();
                        int id = MaritimeCloudUtils.toMmsi(l.getId());
                        updateIntendedRoute(id, r.getIntendedRoute());
                    }
                });
    }

    /**
     * Update intended route of vessel target
     * 
     * @param mmsi
     * @param routeData
     */
    private synchronized void updateIntendedRoute(long mmsi, Route routeData) {
        // Sanity check
        if (aisHandler == null) {
            return;
        }
        
        // Try to find exiting target
        VesselTarget vesselTarget = aisHandler.getVesselTarget(mmsi);
        // If not exists, wait for it to be created by position report
        if (vesselTarget == null) {
            return;
        }

        IntendedRoute intendedRoute = new IntendedRoute(routeData);

        // Update intended route
        vesselTarget.setCloudRouteData(intendedRoute);
        aisHandler.publishUpdate(vesselTarget);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof AisHandlerCommon) {
            aisHandler = (AisHandlerCommon) obj;
        }
    }
}

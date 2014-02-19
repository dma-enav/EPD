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
package dk.dma.epd.ship.service.voct;

import net.maritimecloud.net.broadcast.BroadcastOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.enavcloud.VOCTSARBroadCast;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.ship.route.RouteManager;

///**
// * Intended route service implementation
// */
//public class VOCTBroadcastService extends EnavService implements IRoutesUpdateListener, IPntDataListener {
//
//    private static final Logger LOG = LoggerFactory.getLogger(VOCTBroadcastService.class);
//
//    /**
//     * The current active route provider
//     */
//    private final ActiveRouteProvider provider;
//    private final VOCTManager voctManager;
//    // private final PntHandler pntHandler;
//
//    PntData pntData;
//
//    public VOCTBroadcastService(EnavServiceHandler enavServiceHandler, RouteManager provider, PntHandler pntHandler,
//            VOCTManager voctManager) {
//        super(enavServiceHandler);
//
//        provider.addListener(this);
//        pntHandler.addListener(this);
//
//        this.provider = provider;
//
//        this.voctManager = voctManager;
//
//        // this.pntHandler = pntHandler;
//
//    }
//
//    /**
//     * Broadcast intended route
//     */
//    // @ScheduleWithFixedDelay(10000)
//    public void broadcastVOCTMessage() {
//        System.out.println("BROADCAST SAR INFORMATION");
//
//        VOCTSARBroadCast voctBroadCast = new VOCTSARBroadCast();
//
//        // Is there an active route?
//        if (provider.getActiveRoute() != null) {
//
//            // Do we have effort allocation data?
//            if (voctManager.getSarData().getEffortAllocationData().size() > 0) {
//
//                // Do we have a route to send?
//                if (voctManager.getSarData().getFirstEffortAllocationData().getSearchPatternRoute() != null) {
//
//                    // Is the search pattern the same as the search
//                    // pattern route?
//                    if (voctManager.getSarData().getFirstEffortAllocationData().getSearchPatternRoute()
//                            .isActiveRoute(provider.getActiveRoute())) {
//
//                        voctBroadCast.setIntendedSearchPattern(provider.getActiveRoute().getFullRouteData());
//
//                    } else {
//                        LOG.info("Not attaching search route");
//                    }
//                }
//
//            }
//        }
//
//        if (pntData != null) {
//
//            double heading = -1.0;
//            if (pntData.getCog() != null) {
//                heading = pntData.getCog();
//            }
//
//            // double headingRadian = Math.toRadians(heading);
//
//            // Set location of ship
//            Position currentPos = pntData.getPosition();
//
//            voctBroadCast.setHeading(heading);
//            voctBroadCast.setLat(currentPos.getLatitude());
//            voctBroadCast.setLon(currentPos.getLongitude());
//
//        } else {
//            voctBroadCast.setHeading(-1);
//            voctBroadCast.setLat(-9999);
//            voctBroadCast.setLon(-9999);
//        }
//
//        // send message
//        LOG.info("Sending VOCT Broadcast");
//
////        try {
//////            enavServiceHandler.sendMessage(voctBroadCast);
////            submitIfConnected(new Runnable() {
////                @Override
////                public void run() {
////                    BroadcastOptions options = new BroadcastOptions();
////                    options.setBroadcastRadius(BROADCAST_RADIUS);
////                    getMaritimeCloudConnection().broadcast(message, options);
////                    getStatus().markSuccesfullSend();
////                }
////            });
////            
////            
////            
////        } catch (Exception e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////        }
//
//    }
//
//    /**
//     * Handle event of active route change
//     */
//    @Override
//    public void routesChanged(RoutesUpdateEvent e) {
//        if (e != null) {
//            if (e.is(RoutesUpdateEvent.ROUTE_ACTIVATED, RoutesUpdateEvent.ROUTE_DEACTIVATED)) {
//
//                // Check if the active route is the same as the sar search
//                // pattern
//
//                broadcastVOCTMessage();
//
//                //
//            }
//        }
//    }
//
//    @Override
//    public void pntDataUpdate(PntData pntData) {
//        if (pntData == null || pntData.getPosition() == null) {
//            return;
//        }
//
//        if (pntData == this.pntData) {
//            return;
//        }
//
//        this.pntData = pntData;
//
//        broadcastVOCTMessage();
//
//    }
//}

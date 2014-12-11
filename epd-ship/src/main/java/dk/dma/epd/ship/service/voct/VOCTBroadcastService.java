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
package dk.dma.epd.ship.service.voct;

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
////                    getMmsClient().broadcast(message, options);
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

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

import java.util.concurrent.TimeUnit;

import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.service.invocation.InvocationCallback;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceDatumPoint;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceDatumPoint.VOCTCommunicationMessageDatumPoint;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceRapidResponse;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceRapidResponse.CLOUD_STATUS;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceRapidResponse.VOCTCommunicationMessageRapidResponse;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.service.VoctHandlerCommon;
import dk.dma.epd.common.util.Util;

/**
 * Ship specific intended route service implementation.
 * <p>
 * Listens for changes to the active route and broadcasts it. Also broadcasts the route periodically.
 * <p>
 * Improvements:
 * <ul>
 * <li>Use a worker pool rather than spawning a new thread for each broadcast.</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class VoctHandler extends VoctHandlerCommon implements Runnable {

    /**
     * Protocols needed for VOCT Communication - may be further split or combined in future
     */
    private InvocationCallback.Context<VOCTCommunicationServiceRapidResponse.VOCTCommunicationReplyRapidResponse> voctContextRapidResponse;
    private InvocationCallback.Context<VOCTCommunicationServiceDatumPoint.VOCTCommunicationReplyDatumPoint> voctContextDatumPoint;

    //
    //
    //
    // private static final Logger LOG = LoggerFactory.getLogger(VoctHandler.class);
    // private static final long BROADCAST_TIME = 60; // Broadcast intended route every minute for now
    // private static final long ADAPTIVE_TIME = 60 * 10; // Set to 10 minutes?
    // private static final int BROADCAST_RADIUS = Integer.MAX_VALUE;
    //
    // private DateTime lastTransmitActiveWp;
    // private DateTime lastSend = new DateTime(1);
    // private RouteManager routeManager;
    private boolean running;

    // private IntendedRouteLayerCommon intendedRouteLayerCommon;

    /**
     * Constructor
     */
    public VoctHandler() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        super.cloudConnected(connection);

        // Register for RapidResponse
        try {
            getMaritimeCloudConnection()
                    .serviceRegister(
                            VOCTCommunicationServiceRapidResponse.INIT,
                            new InvocationCallback<VOCTCommunicationServiceRapidResponse.VOCTCommunicationMessageRapidResponse, VOCTCommunicationServiceRapidResponse.VOCTCommunicationReplyRapidResponse>() {
                                public void process(
                                        VOCTCommunicationMessageRapidResponse message,
                                        InvocationCallback.Context<VOCTCommunicationServiceRapidResponse.VOCTCommunicationReplyRapidResponse> context) {
                                    //
                                    // System.out.println("Received SAR Payload!");
                                    // cloudStatus.markCloudReception();
                                    //
                                    // voctContextRapidResponse = context;
                                    //
                                    // voctManager.handleSARDataPackage(message);

                                }
                            }).awaitRegistered(4, TimeUnit.SECONDS);

            // Register for DatumPoint
            getMaritimeCloudConnection()
                    .serviceRegister(
                            VOCTCommunicationServiceDatumPoint.INIT,
                            new InvocationCallback<VOCTCommunicationServiceDatumPoint.VOCTCommunicationMessageDatumPoint, VOCTCommunicationServiceDatumPoint.VOCTCommunicationReplyDatumPoint>() {
                                public void process(
                                        VOCTCommunicationMessageDatumPoint message,
                                        InvocationCallback.Context<VOCTCommunicationServiceDatumPoint.VOCTCommunicationReplyDatumPoint> context) {

//                                    System.out.println("Received SAR Payload!");
//                                    cloudStatus.markCloudReception();
//
//                                    voctContextDatumPoint = context;
//
//                                    voctManager.handleSARDataPackage(message);

                                }
                            }).awaitRegistered(4, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Start broadcasting our own active route
        running = true;
        new Thread(this).start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudDisconnected() {
        running = false;
    }

    /**
     * Main thread run method. Broadcasts the intended route
     */
    public void run() {

        // Initialize first send
        // lastSend = new DateTime();
        // broadcastIntendedRoute();

        while (running) {
            Util.sleep(1000L);
        }

    }

    public void sendVOCTReply(CLOUD_STATUS recievedAccepted, long id, String message, SAR_TYPE type) {

//        if (type == SAR_TYPE.RAPID_RESPONSE) {
//            try {
//                voctContextRapidResponse.complete(new VOCTCommunicationServiceRapidResponse.VOCTCommunicationReplyRapidResponse(
//                        message, id, ownShipHandler.getMmsi(), System.currentTimeMillis(), recievedAccepted));
//                cloudStatus.markSuccesfullSend();
//            } catch (Exception e) {
//                cloudStatus.markFailedSend();
//                System.out.println("Failed to reply");
//            }
//        }
//
//        if (type == SAR_TYPE.DATUM_POINT) {
//            try {
//                voctContextDatumPoint.complete(new VOCTCommunicationServiceDatumPoint.VOCTCommunicationReplyDatumPoint(message, id,
//                        ownShipHandler.getMmsi(), System.currentTimeMillis(), recievedAccepted));
//                cloudStatus.markSuccesfullSend();
//            } catch (Exception e) {
//                cloudStatus.markFailedSend();
//                System.out.println("Failed to reply");
//            }
//        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndUndo(Object obj) {
//        if (obj instanceof RouteManager) {
//            routeManager.removeListener(this);
//            routeManager = null;
//        }
        super.findAndUndo(obj);
    }

}

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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.service.invocation.InvocationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceDatumPoint;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionReply;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceDatumPoint.VOCTCommunicationMessageDatumPoint;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceDatumPoint.VOCTCommunicationReplyDatumPoint;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceRapidResponse;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceRapidResponse.VOCTCommunicationMessageRapidResponse;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceRapidResponse.VOCTCommunicationReplyRapidResponse;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.VoctHandlerCommon;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon.ICloudMessageListener;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon.VOCT_MSG_STATUS;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.service.voct.VOCTManager;

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
    private boolean running;
    private VOCTManager voctManager;

    // private IntendedRouteLayerCommon intendedRouteLayerCommon;

    private static final Logger LOG = LoggerFactory.getLogger(VoctHandlerCommon.class);

    // ID, MMSI
    protected Map<Long, Long> voctInvitations = new ConcurrentHashMap<>();

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

                                    // LOG.info("Shore received a VOCT reply");
                                    System.out.println("Received SAR Payload!");

                                    MaritimeId caller = context.getCaller();
                                    long mmsi = MaritimeCloudUtils.toMmsi(context.getCaller());
                                    //
                                    voctInvitations.put(message.getId(), mmsi);
                                    // cloudStatus.markCloudReception();
                                    //
                                    // voctContextRapidResponse = context;
                                    //
                                    voctManager.handleSARDataPackage(message);

                                    context.complete(new VOCTCommunicationReplyRapidResponse(message.getId(), EPDShip.getInstance()
                                            .getOwnShipHandler().getMmsi(), System.currentTimeMillis()));
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

                                    System.out.println("Received SAR Payload!");
                                    MaritimeId caller = context.getCaller();
                                    long mmsi = MaritimeCloudUtils.toMmsi(context.getCaller());
                                    //
                                    voctInvitations.put(message.getId(), mmsi);
                                    // cloudStatus.markCloudReception();
                                    //
                                    // voctContextDatumPoint = context;
                                    //
                                    voctManager.handleSARDataPackage(message);

                                    // message.get
                                    // context.complete(new RouteSuggestionReply(message.getId()));
                                    context.complete(new VOCTCommunicationReplyDatumPoint(message.getId(), EPDShip.getInstance()
                                            .getOwnShipHandler().getMmsi(), System.currentTimeMillis()));
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

    public void sendVOCTReply(VOCT_MSG_STATUS recievedAccepted, long id, String message, SAR_TYPE type) {

        if (type == SAR_TYPE.RAPID_RESPONSE) {
            // try {

            VOCTCommunicationMessageRapidResponse voctMessage = new VOCTCommunicationMessageRapidResponse(id, message,
                    recievedAccepted);

            // RouteSuggestionMessage routeMessage = new RouteSuggestionMessage(null, null, null);
            System.out.println("Replying to : " + (long) voctInvitations.get(id));
            boolean toSend = sendMaritimeCloudMessage(new MmsiId((int) (long) voctInvitations.get(id)), voctMessage, this);

            System.out.println("To Send is " + toSend);

            // sendMaritimeCloudMessage(new MmsiId((int)100), routeMessage, EPDShip.getInstance()
            // .getRouteSuggestionHandler());

            // } catch (Exception e) {
            // System.out.println("Failed to reply " + e);
            // }
        }
        //
        // if (type == SAR_TYPE.DATUM_POINT) {
        // try {
        // voctContextDatumPoint.complete(new VOCTCommunicationServiceDatumPoint.VOCTCommunicationReplyDatumPoint(message, id,
        // ownShipHandler.getMmsi(), System.currentTimeMillis(), recievedAccepted));
        // cloudStatus.markSuccesfullSend();
        // } catch (Exception e) {
        // cloudStatus.markFailedSend();
        // System.out.println("Failed to reply");
        // }
        // }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (obj instanceof VOCTManager) {
            voctManager = (VOCTManager) obj;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndUndo(Object obj) {
        // if (obj instanceof RouteManager) {
        // routeManager.removeListener(this);
        // routeManager = null;
        // }
        super.findAndUndo(obj);
    }

    @Override
    public void messageReceivedByCloud(VOCTCommunicationMessageRapidResponse message) {
        // TODO Auto-generated method stub

        System.out.println("Message recieved by Cloud");
    }

    @Override
    public void messageHandled(VOCTCommunicationMessageRapidResponse message, VOCTCommunicationReplyRapidResponse reply) {
        // TODO Auto-generated method stub
        System.out.println("Message Handled / whats this?");
    }

}

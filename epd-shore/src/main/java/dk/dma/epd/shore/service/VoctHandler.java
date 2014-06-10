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
package dk.dma.epd.shore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.net.ConnectionFuture;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.broadcast.BroadcastListener;
import net.maritimecloud.net.broadcast.BroadcastMessageHeader;
import net.maritimecloud.net.service.ServiceEndpoint;
import net.maritimecloud.net.service.invocation.InvocationCallback;
import net.maritimecloud.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.xjc.generator.bean.field.Messages;

import dk.dma.enav.model.voct.DatumPointDTO;
import dk.dma.enav.model.voct.EffortAllocationDTO;
import dk.dma.enav.model.voct.RapidResponseDTO;
import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceDatumPoint;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceDatumPoint.VOCTCommunicationMessageDatumPoint;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceDatumPoint.VOCTCommunicationReplyDatumPoint;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceRapidResponse;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceRapidResponse.VOCTCommunicationMessageRapidResponse;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceRapidResponse.VOCTCommunicationReplyRapidResponse;
import dk.dma.epd.common.prototype.enavcloud.VOCTSARBroadCast;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.VoctHandlerCommon;
import dk.dma.epd.common.prototype.status.CloudStatus;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.shore.voct.SRUManager;
import dk.dma.epd.shore.voct.VOCTManager;

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

    private boolean listenToSAR;
    /**
     * Network list for various SAR data objects
     */

    private List<ServiceEndpoint<VOCTCommunicationMessageRapidResponse, VOCTCommunicationReplyRapidResponse>> voctMessageListRapidResponse = new ArrayList<>();
    private List<ServiceEndpoint<VOCTCommunicationMessageDatumPoint, VOCTCommunicationReplyDatumPoint>> voctMessageListDatumPoint = new ArrayList<>();

    private boolean running;
    private static final Logger LOG = LoggerFactory.getLogger(VoctHandlerCommon.class);
    // private IntendedRouteLayerCommon intendedRouteLayerCommon;

    public SRUManager sruManager;
    public VOCTManager voctManager;

    /**
     * Constructor
     */
    public VoctHandler() {
        super();

        // // Schedule a refresh of the available SRUs
        scheduleWithFixedDelayWhenConnected(new Runnable() {
            @Override
            public void run() {
                fetchVOCTMessageList();
            }
        }, 5, 62, TimeUnit.SECONDS);
    }

    private void listenToVOCTBroadcasts() throws InterruptedException {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        super.cloudConnected(connection);

        try {
            getMaritimeCloudConnection()
                    .serviceRegister(
                            VOCTCommunicationServiceRapidResponse.INIT,
                            new InvocationCallback<VOCTCommunicationServiceRapidResponse.VOCTCommunicationMessageRapidResponse, VOCTCommunicationServiceRapidResponse.VOCTCommunicationReplyRapidResponse>() {
                                public void process(
                                        VOCTCommunicationMessageRapidResponse message,
                                        InvocationCallback.Context<VOCTCommunicationServiceRapidResponse.VOCTCommunicationReplyRapidResponse> context) {

                                    // LOG.info("Shore received a VOCT reply");
                                    System.out.println("Received SAR Reply from Ship!");

                                    MaritimeId caller = context.getCaller();
                                    long mmsi = MaritimeCloudUtils.toMmsi(context.getCaller());

                                    // sruManager.sruSRUStatus(mmsi, message.getStatus());

                                    sruManager.handleSRUReply(mmsi, message.getStatus());
                                    //
                                    // voctInvitations.put(message.getId(), mmsi);
                                    // cloudStatus.markCloudReception();
                                    //
                                    // voctContextRapidResponse = context;
                                    //
                                    // voctManager.handleSARDataPackage(message);

                                    // context.complete(new VOCTCommunicationReplyRapidResponse(message.getId(),
                                    // EPDShore.getInstance()
                                    // .getOwnShipHandler().getMmsi(), System.currentTimeMillis()));
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
                                    // voctInvitations.put(message.getId(), mmsi);
                                    // cloudStatus.markCloudReception();
                                    //
                                    // voctContextDatumPoint = context;
                                    //
                                    // voctManager.handleSARDataPackage(message);

                                    // message.get
                                    // context.complete(new RouteSuggestionReply(message.getId()));
                                    // context.complete(new VOCTCommunicationReplyDatumPoint(message.getId(), EPDShip.getInstance()
                                    // .getOwnShipHandler().getMmsi(), System.currentTimeMillis()));
                                }
                            }).awaitRegistered(4, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        getMaritimeCloudConnection().broadcastListen(VOCTSARBroadCast.class, new BroadcastListener<VOCTSARBroadCast>() {
            public void onMessage(BroadcastMessageHeader l, VOCTSARBroadCast r) {

                System.out.println("Broadcast recieved!");

                long id = Long.parseLong(l.getId().toString().split("mmsi://")[1]);

                // sruManager.handleSRUBroadcast(id, r);

            }
        });

        // Start broadcasting our own active route
        running = true;
        new Thread(this).start();
    }

    public void fetchVOCTMessageList() {
        System.out.println("Checking for VOCT message list");
        try {

            voctMessageListRapidResponse = getMaritimeCloudConnection().serviceLocate(VOCTCommunicationServiceRapidResponse.INIT)
                    .nearest(Integer.MAX_VALUE).get();
            voctMessageListDatumPoint = getMaritimeCloudConnection().serviceLocate(VOCTCommunicationServiceDatumPoint.INIT)
                    .nearest(Integer.MAX_VALUE).get();

            // for (int i = 0; i < voctMessageListRapidResponse.size(); i++) {
            // System.out.println("VOCT Listener with ID: " + voctMessageListRapidResponse.get(i).getId());
            // }

            sruManager.updateSRUsStatus();
        } catch (Exception e) {
            LOG.error(e.getMessage());

        }
    }

    public List<ServiceEndpoint<VOCTCommunicationMessageRapidResponse, VOCTCommunicationReplyRapidResponse>> getVoctMessageList() {
        return voctMessageListRapidResponse;
    }

    public void sendVOCTMessage(long mmsi, SARData sarData, String sender, String message, int id, boolean isAO,
            boolean isSearchPattern) throws InterruptedException, ExecutionException, TimeoutException {

        // System.out.println("Send to : " + mmsi);
        String mmsiStr = "mmsi://" + mmsi;

        if (sarData instanceof RapidResponseData) {
            ServiceEndpoint<VOCTCommunicationServiceRapidResponse.VOCTCommunicationMessageRapidResponse, VOCTCommunicationServiceRapidResponse.VOCTCommunicationReplyRapidResponse> end = null;

            for (int i = 0; i < voctMessageListRapidResponse.size(); i++) {
                if (voctMessageListRapidResponse.get(i).getId().toString().equals(mmsiStr)) {
                    end = voctMessageListRapidResponse.get(i);

                    break;
                }
            }
            // end = voctMessageList.get(voctMessageList.size() - 1);

            VOCTCommunicationMessageRapidResponse voctMessage = null;

            if (sarData instanceof RapidResponseData) {
                RapidResponseDTO rapidResponseModelData = ((RapidResponseData) sarData).getModelData();

                EffortAllocationDTO effortAllocationData = null;
                Route searchPattern = null;

                if (isAO) {
                    if (sarData.getEffortAllocationData().size() > id) {
                        effortAllocationData = sarData.getEffortAllocationData().get(id).getModelData();

                        if (isSearchPattern) {

                            if (sarData.getEffortAllocationData().get(id).getSearchPatternRoute() != null) {

                                if (sarData.getEffortAllocationData().get(id).getSearchPatternRoute().isDynamic()) {
                                    sarData.getEffortAllocationData().get(id).getSearchPatternRoute().switchToStatic();
                                    searchPattern = sarData.getEffortAllocationData().get(id).getSearchPatternRoute()
                                            .getFullRouteData();
                                    sarData.getEffortAllocationData().get(id).getSearchPatternRoute().switchToDynamic();
                                } else {
                                    searchPattern = sarData.getEffortAllocationData().get(id).getSearchPatternRoute()
                                            .getFullRouteData();
                                }

                            }
                        }
                    }

                }
                voctMessage = new VOCTCommunicationServiceRapidResponse.VOCTCommunicationMessageRapidResponse(
                        rapidResponseModelData, effortAllocationData, searchPattern, sender, message, System.currentTimeMillis(),
                        mmsi);
            }

            System.out.println("Sending VOCT SAR to mmsi: " + mmsi);

            // Internal tracking stuff
            // RouteSuggestionData suggestionData = new RouteSuggestionData(
            // routeMessage, null, routeMessage.getId(), mmsi, false,
            // AIS_STATUS.RECIEVED_APP_ACK);
            //
            // RouteSuggestionKey routeSuggestionKey = new
            // RouteSuggestionKey(mmsi,
            // routeMessage.getId());
            // routeSuggestions.put(routeSuggestionKey, suggestionData);

            sendMaritimeCloudMessage(voctMessageListRapidResponse, new MmsiId((int) mmsi), voctMessage, this);

            // if (end != null) {
            // ConnectionFuture<VOCTCommunicationServiceRapidResponse.VOCTCommunicationReplyRapidResponse> f = end
            // .invoke(voctMessage);
            //
            // f.handle(new BiConsumer<VOCTCommunicationServiceRapidResponse.VOCTCommunicationReplyRapidResponse, Throwable>() {
            //
            // @Override
            // public void accept(VOCTCommunicationReplyRapidResponse l, Throwable r) {
            // // TODO Auto-generated method stub
            //
            // // System.out.println("Reply recieved SAR with status: " + l.getStatus());
            // System.out.println("Recieved reply");
            //
            // sruManager.sruSRUStatus(l.getMmsi(), SRU_NETWORK_STATUS.RECIEVED_APP_ACK);
            // // sruManager.handleSRUReply(l.getMmsi(), l.getStatus());
            // }
            // });
            //
            // } else {
            // // notifyRouteExchangeListeners();
            // System.out.println("Failed to send");
            // // replyRecieved(f.get());
            // }

        }

        if (sarData instanceof DatumPointData) {
            ServiceEndpoint<VOCTCommunicationServiceDatumPoint.VOCTCommunicationMessageDatumPoint, VOCTCommunicationServiceDatumPoint.VOCTCommunicationReplyDatumPoint> end = null;

            for (int i = 0; i < voctMessageListDatumPoint.size(); i++) {
                if (voctMessageListDatumPoint.get(i).getId().toString().equals(mmsiStr)) {
                    end = voctMessageListDatumPoint.get(i);

                    break;
                }
            }
            // end = voctMessageList.get(voctMessageList.size() - 1);

            VOCTCommunicationMessageDatumPoint voctMessage = null;

            if (sarData instanceof DatumPointData) {
                DatumPointDTO datumPointModelData = ((DatumPointData) sarData).getModelData();

                EffortAllocationDTO effortAllocationData = null;
                Route searchPattern = null;

                if (isAO) {
                    if (sarData.getEffortAllocationData().size() > id) {
                        effortAllocationData = sarData.getEffortAllocationData().get(id).getModelData();

                        if (isSearchPattern) {

                            if (sarData.getEffortAllocationData().get(id).getSearchPatternRoute() != null) {

                                if (sarData.getEffortAllocationData().get(id).getSearchPatternRoute().isDynamic()) {
                                    sarData.getEffortAllocationData().get(id).getSearchPatternRoute().switchToStatic();
                                    searchPattern = sarData.getEffortAllocationData().get(id).getSearchPatternRoute()
                                            .getFullRouteData();
                                    sarData.getEffortAllocationData().get(id).getSearchPatternRoute().switchToDynamic();
                                } else {
                                    searchPattern = sarData.getEffortAllocationData().get(id).getSearchPatternRoute()
                                            .getFullRouteData();
                                }

                            }
                        }
                    }

                }
                voctMessage = new VOCTCommunicationServiceDatumPoint.VOCTCommunicationMessageDatumPoint(datumPointModelData,
                        effortAllocationData, searchPattern, sender, message, System.currentTimeMillis());
            }

            System.out.println("Sending VOCT SAR to mmsi: " + mmsi);

            // Internal tracking stuff
            // RouteSuggestionData suggestionData = new RouteSuggestionData(
            // routeMessage, null, routeMessage.getId(), mmsi, false,
            // AIS_STATUS.RECIEVED_APP_ACK);
            //
            // RouteSuggestionKey routeSuggestionKey = new
            // RouteSuggestionKey(mmsi,
            // routeMessage.getId());
            // routeSuggestions.put(routeSuggestionKey, suggestionData);
            //

            if (end != null) {
                ConnectionFuture<VOCTCommunicationServiceDatumPoint.VOCTCommunicationReplyDatumPoint> f = end.invoke(voctMessage);

                f.handle(new BiConsumer<VOCTCommunicationServiceDatumPoint.VOCTCommunicationReplyDatumPoint, Throwable>() {

                    @Override
                    public void accept(VOCTCommunicationReplyDatumPoint l, Throwable r) {
                        // TODO Auto-generated method stub
                        System.out.println("Reply recieved SAR");
                        // sruManager.handleSRUReply(l.getMmsi(), l.getStatus());
                    }
                });

            } else {
                // notifyRouteExchangeListeners();
                System.out.println("Failed to send");
                // replyRecieved(f.get());
            }

        }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {

        if (obj instanceof SRUManager) {
            sruManager = (SRUManager) obj;
        }

        if (obj instanceof VOCTManager) {
            voctManager = (VOCTManager) obj;
        }

        super.findAndInit(obj);

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

        sruManager.sruSRUStatus(message.getReceiversMMSI(), CloudMessageStatus.RECEIVED_BY_CLOUD);
    }

    @Override
    public void messageHandled(VOCTCommunicationMessageRapidResponse message, VOCTCommunicationReplyRapidResponse reply) {
        // TODO Auto-generated method stub
        sruManager.sruSRUStatus(message.getReceiversMMSI(), CloudMessageStatus.RECEIVED_BY_CLIENT);
    }

}

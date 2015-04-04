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
package dk.dma.epd.shore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.maritimecloud.net.EndpointInvocationFuture;
import net.maritimecloud.net.MessageHeader;
import net.maritimecloud.net.mms.MmsClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.MaritimeId;
import dk.dma.epd.common.prototype.enavcloud.TODO;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.VOCTCommunicationMessage;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.VoctHandlerCommon;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon.VoctMsgStatus;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.shore.voct.SRUManager;
import dk.dma.epd.shore.voct.VOCTManager;
import dma.voct.AbstractVOCTReplyEndpoint;
import dma.voct.DatumPoint;
import dma.voct.EffortAllocation;
import dma.voct.RapidResponse;
import dma.voct.SAR_TYPE;
import dma.voct.VOCTEndpoint;
import dma.voct.VOCTMessage;
import dma.voct.VOCTReply;

@SuppressWarnings("unused")
public class VoctHandler extends VoctHandlerCommon implements Runnable {

    private boolean listenToSAR;
    /**
     * Network list for various SAR data objects
     */

    private List<VOCTEndpoint> voctMessageList = new ArrayList<>();
    private boolean running;
    private static final Logger LOG = LoggerFactory
            .getLogger(VoctHandlerCommon.class);
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
        }, 5, 30, TimeUnit.SECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MmsClient connection) {

        // Refresh the service list
        fetchVOCTMessageList();

        // Register a cloud chat service
        try {
            getMmsClient().endpointRegister(new AbstractVOCTReplyEndpoint() {

                @Override
                protected void sendVOCTReply(MessageHeader header,
                        VOCTReply reply) {

                    // A reply was sent from a vessel, update
                    System.out.println("Reply recieved from "
                            + header.getSender());

                    LOG.info("Shore received a VOCT reply");
                    // // System.out.println("Received SAR Reply from Ship!");
                    //
                    net.maritimecloud.core.id.MaritimeId caller = header
                            .getSender();

                    long mmsi = MaritimeCloudUtils.toMmsi(caller);
                    sruManager.sruSRUStatus(mmsi,
                            CloudMessageStatus.HANDLED_BY_CLIENT);
                    sruManager.handleSRUReply(mmsi, reply.getStatus());

                }

            }).awaitRegistered(4, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            LOG.error("Error hooking up services", e);
        }

        // TODO: Maritime Cloud 0.2 re-factoring
        // try {
        // getMaritimeCloudConnection()
        // .serviceRegister(
        // VOCTCommunicationService.INIT,
        // new
        // InvocationCallback<VOCTCommunicationService.VOCTCommunicationMessage,
        // VOCTCommunicationService.VOCTCommunicationReply>() {
        // public void process(VOCTCommunicationMessage message,
        // InvocationCallback.Context<VOCTCommunicationService.VOCTCommunicationReply>
        // context) {
        //
        // // LOG.info("Shore received a VOCT reply");
        // // System.out.println("Received SAR Reply from Ship!");
        //
        // MaritimeId caller = context.getCaller();
        // long mmsi = MaritimeCloudUtils.toMmsi(context.getCaller());
        //
        // // sruManager.sruSRUStatus(mmsi, message.getStatus());
        //
        // sruManager.handleSRUReply(mmsi, message.getStatus());
        // //
        // // voctInvitations.put(message.getId(), mmsi);
        // // cloudStatus.markCloudReception();
        // //
        // // voctContextRapidResponse = context;
        // //
        // // voctManager.handleSARDataPackage(message);
        //
        // // context.complete(new
        // VOCTCommunicationReplyRapidResponse(message.getId(),
        // // EPDShore.getInstance()
        // // .getOwnShipHandler().getMmsi(), System.currentTimeMillis()));
        // }
        // }).awaitRegistered(4, TimeUnit.SECONDS);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // // System.out.println("Failed to register services?");
        // }
        //
        // // Start broadcasting our own active route
        // running = true;
        // new Thread(this).start();
        // // }
    }

    private void fetchVOCTMessageList() {
        // voctMessageList

        try {
            voctMessageList = getMmsClient().endpointLocate(VOCTEndpoint.class)
                    .findAll().timeout(CLOUD_TIMEOUT, TimeUnit.SECONDS).get();
            System.out.println("Fetching VOCT MEssage Lists "
                    + voctMessageList.size());
            for (int i = 0; i < voctMessageList.size(); i++) {
                System.out.println(voctMessageList.get(i).getRemoteId());
            }
        } catch (Exception e) {
            LOG.error("Failed looking up route suggestion services: "
                    + e.getMessage());
        }

        // TODO: Maritime Cloud 0.2 re-factoring
        // // System.out.println("Checking for VOCT message list");
        // try {
        //
        // voctMessageList =
        // getMmsClient().serviceLocate(VOCTCommunicationService.INIT).nearest(Integer.MAX_VALUE)
        // .get();
        //
        // sruManager.updateSRUsStatus();
        // } catch (Exception e) {
        // LOG.error(e.getMessage());
        //
        // }
    }

    // public List<TODO.ServiceEndpoint<VOCTCommunicationMessage,
    // VOCTCommunicationReply>> getVoctMessageList() {
    // return voctMessageList;
    // }

    private VOCTMessage convertToVOCTMessage(SARData sarData) {
        VOCTMessage message = new VOCTMessage();

        if (sarData instanceof RapidResponseData) {
            RapidResponse rapidResponseData;

            rapidResponseData = ((RapidResponseData) sarData).getModelData();

            message.setRapidResponse(rapidResponseData);
            message.setSarType(SAR_TYPE.RAPID_RESPONSE);

        }

        if (sarData instanceof DatumPointData) {
            DatumPoint sarModelData = ((DatumPointData) sarData).getModelData();

            message.setDatumPoint(sarModelData);
            message.setSarType(SAR_TYPE.DATUM_POINT);

        }
        //
        // if (sarData instanceof DatumPointDataSARIS) {
        // DatumPointSARISDTO sarModelData = ((DatumPointDataSARIS) sarData)
        // .getModelData();
        // voctMessage = new VOCTCommunicationService.VOCTCommunicationMessage(
        // sarModelData, effortAllocationData, searchPattern, sender,
        // message, voctManager.getVoctID(), mmsi);
        //
        // }

        return message;
    }

    /**
     * Used to send a VOCT Data package
     * 
     * @param mmsi
     * @param sarData
     * @param sender
     * @param message
     * @param isAO
     * @param isSearchPattern
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public void sendVOCTMessage(long mmsi, SARData sarData, String sender,
            String message, boolean isAO, boolean isSearchPattern)
            throws InterruptedException, ExecutionException, TimeoutException {

        VOCTMessage voctMessage;
        voctMessage = convertToVOCTMessage(sarData);

        EffortAllocation effortAllocationData = null;
        dma.route.Route searchPattern = null;

        if (isAO) {
            if (sarData.getEffortAllocationData().containsKey(mmsi)) {
                effortAllocationData = sarData.getEffortAllocationData()
                        .get(mmsi).getModelData();

                voctMessage.setEffortAllocation(effortAllocationData);

                if (isSearchPattern) {

                    if (sarData.getEffortAllocationData().get(mmsi)
                            .getSearchPatternRoute() != null) {

                        if (sarData.getEffortAllocationData().get(mmsi)
                                .getSearchPatternRoute().isDynamic()) {

                            sarData.getEffortAllocationData().get(mmsi)
                                    .getSearchPatternRoute().switchToStatic();

                            searchPattern = sarData.getEffortAllocationData()
                                    .get(mmsi).getSearchPatternRoute()
                                    .toMaritimeCloudRoute();

                            sarData.getEffortAllocationData().get(mmsi)
                                    .getSearchPatternRoute().switchToDynamic();
                        } else {
                            searchPattern = sarData.getEffortAllocationData()
                                    .get(mmsi).getSearchPatternRoute()
                                    .toMaritimeCloudRoute();

                        }

                        voctMessage.setSearchPattern(searchPattern);

                    }
                }

            }
        }

        voctMessage.setId(System.currentTimeMillis());

        VOCTEndpoint voctEndpoint = MaritimeCloudUtils.findServiceWithMmsi(
                voctMessageList, mmsi);

        if (voctEndpoint != null) {
            EndpointInvocationFuture<Void> returnVal = voctEndpoint
                    .SendVOCTData(voctMessage);

            returnVal.relayed().handle(new Consumer<Throwable>() {

                @Override
                public void accept(Throwable t) {
                    // RouteSuggestionData routeData =
                    // routeSuggestions.get(routeSegmentSuggestion.getId());
                    // routeData.setCloudMessageStatus(CloudMessageStatus.RECEIVED_BY_CLOUD);
                    // notifyRouteSuggestionListeners();
                    sruManager.sruSRUStatus(mmsi,
                            CloudMessageStatus.RECEIVED_BY_CLOUD);

                }
            });

            returnVal.handle(new BiConsumer<Void, Throwable>() {
                @Override
                public void accept(Void t, Throwable u) {
                    sruManager.sruSRUStatus(mmsi,
                            CloudMessageStatus.RECEIVED_BY_CLOUD);

                }
            });
        } else {
            LOG.error("Could not find VOCT endpoint for mmsi: " + mmsi);

            return;
        }

        // for (int i = 0; i < voctMessageList.size(); i++) {
        // if (voctMessageList.get(i).getId().toString().equals(mmsiStr)) {
        // // end = voctMessageList.get(i);
        //
        // break;
        // }
        // }
        // end = voctMessageList.get(voctMessageList.size() - 1);

        // VOCTCommunicationMessage voctMessage = null;
        //
        //
        // if (sarData instanceof RapidResponseData) {
        // RapidResponseDTO sarModelData = ((RapidResponseData) sarData)
        // .getModelData();
        // voctMessage = new VOCTCommunicationService.VOCTCommunicationMessage(
        // sarModelData, effortAllocationData, searchPattern, sender,
        // message, voctManager.getVoctID(), mmsi);
        //
        // }
        //
        // if (sarData instanceof DatumPointData) {
        // DatumPointDTO sarModelData = ((DatumPointData) sarData)
        // .getModelData();
        // voctMessage = new VOCTCommunicationService.VOCTCommunicationMessage(
        // sarModelData, effortAllocationData, searchPattern, sender,
        // message, voctManager.getVoctID(), mmsi);
        //
        // }
        //
        // if (sarData instanceof DatumPointDataSARIS) {
        // DatumPointSARISDTO sarModelData = ((DatumPointDataSARIS) sarData)
        // .getModelData();
        // voctMessage = new VOCTCommunicationService.VOCTCommunicationMessage(
        // sarModelData, effortAllocationData, searchPattern, sender,
        // message, voctManager.getVoctID(), mmsi);
        //
        // }

        // TODO: Maritime Cloud 0.2 re-factoring
        // sendMaritimeCloudMessage(voctMessageList, new MmsiId((int) mmsi),
        // voctMessage, this);

        // System.out.println("Sending VOCT SAR to mmsi: " + mmsi);

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

    public void sendCancelMessage(List<Long> srusToCancel) {
        // System.out.println("Send SAR cancel message " + srusToCancel.size());
        for (int i = 0; i < srusToCancel.size(); i++) {

            VOCTMessage voctMessage = new VOCTMessage();
            voctMessage.setId(System.currentTimeMillis());

            // VOCTCommunicationMessage voctMessage = new
            // VOCTCommunicationMessage(
            // voctManager.getVoctID(), VoctMsgStatus.WITHDRAWN);

            // sendVoctMessage(r);
            // TODO: Maritime Cloud 0.2 re-factoring
            // boolean toSend = sendMaritimeCloudMessage(new MmsiId((int) (long)
            // srusToCancel.get(i)), voctMessage, this);
        }

    }

    /**
     * @return the voctMessageList
     */
    public List<VOCTEndpoint> getVoctMessageList() {
        return voctMessageList;
    }

}

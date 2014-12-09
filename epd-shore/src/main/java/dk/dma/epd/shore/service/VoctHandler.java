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

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.service.ServiceEndpoint;
import net.maritimecloud.net.service.invocation.InvocationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.voct.DatumPointDTO;
import dk.dma.enav.model.voct.DatumPointSARISDTO;
import dk.dma.enav.model.voct.EffortAllocationDTO;
import dk.dma.enav.model.voct.RapidResponseDTO;
import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.VOCTCommunicationMessage;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.VOCTCommunicationReply;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointDataSARIS;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.VoctHandlerCommon;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon.VoctMsgStatus;
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

    private List<ServiceEndpoint<VOCTCommunicationMessage, VOCTCommunicationReply>> voctMessageList = new ArrayList<>();
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
        }, 5, 30, TimeUnit.SECONDS);
    }

    private void listenToVOCTBroadcasts() throws InterruptedException {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        super.cloudConnected(connection);

        // try {
        try {
            getMaritimeCloudConnection()
                    .serviceRegister(
                            VOCTCommunicationService.INIT,
                            new InvocationCallback<VOCTCommunicationService.VOCTCommunicationMessage, VOCTCommunicationService.VOCTCommunicationReply>() {
                                public void process(VOCTCommunicationMessage message,
                                        InvocationCallback.Context<VOCTCommunicationService.VOCTCommunicationReply> context) {

                                    // LOG.info("Shore received a VOCT reply");
//                                    System.out.println("Received SAR Reply from Ship!");

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
        } catch (InterruptedException e) {
            e.printStackTrace();
//            System.out.println("Failed to register services?");
        }

        // Start broadcasting our own active route
        running = true;
        new Thread(this).start();
        // }
    }

    private void fetchVOCTMessageList() {
        // System.out.println("Checking for VOCT message list");
        try {

            voctMessageList = getMaritimeCloudConnection().serviceLocate(VOCTCommunicationService.INIT).nearest(Integer.MAX_VALUE)
                    .get();

            sruManager.updateSRUsStatus();
        } catch (Exception e) {
            LOG.error(e.getMessage());

        }
    }

    public List<ServiceEndpoint<VOCTCommunicationMessage, VOCTCommunicationReply>> getVoctMessageList() {
        return voctMessageList;
    }

    public void sendVOCTMessage(long mmsi, SARData sarData, String sender, String message, boolean isAO, boolean isSearchPattern)
            throws InterruptedException, ExecutionException, TimeoutException {

        // System.out.println("Send to : " + mmsi);
        String mmsiStr = "mmsi://" + mmsi;

        // if (sarData instanceof RapidResponseData) {
        ServiceEndpoint<VOCTCommunicationService.VOCTCommunicationMessage, VOCTCommunicationService.VOCTCommunicationReply> end = null;

        for (int i = 0; i < voctMessageList.size(); i++) {
            if (voctMessageList.get(i).getId().toString().equals(mmsiStr)) {
                end = voctMessageList.get(i);

                break;
            }
        }
        // end = voctMessageList.get(voctMessageList.size() - 1);

        VOCTCommunicationMessage voctMessage = null;

        EffortAllocationDTO effortAllocationData = null;
        Route searchPattern = null;

        if (isAO) {
            if (sarData.getEffortAllocationData().containsKey(mmsi)) {
                effortAllocationData = sarData.getEffortAllocationData().get(mmsi).getModelData();

                if (isSearchPattern) {

                    if (sarData.getEffortAllocationData().get(mmsi).getSearchPatternRoute() != null) {

                        if (sarData.getEffortAllocationData().get(mmsi).getSearchPatternRoute().isDynamic()) {
                            sarData.getEffortAllocationData().get(mmsi).getSearchPatternRoute().switchToStatic();
                            searchPattern = sarData.getEffortAllocationData().get(mmsi).getSearchPatternRoute().getFullRouteData();
                            sarData.getEffortAllocationData().get(mmsi).getSearchPatternRoute().switchToDynamic();
                        } else {
                            searchPattern = sarData.getEffortAllocationData().get(mmsi).getSearchPatternRoute().getFullRouteData();
                        }

                    }
                }
                // }

            }
        }

        if (sarData instanceof RapidResponseData) {
            RapidResponseDTO sarModelData = ((RapidResponseData) sarData).getModelData();
            voctMessage = new VOCTCommunicationService.VOCTCommunicationMessage(sarModelData, effortAllocationData, searchPattern,
                    sender, message, voctManager.getVoctID(), mmsi);

        }

        if (sarData instanceof DatumPointData) {
            DatumPointDTO sarModelData = ((DatumPointData) sarData).getModelData();
            voctMessage = new VOCTCommunicationService.VOCTCommunicationMessage(sarModelData, effortAllocationData, searchPattern,
                    sender, message, voctManager.getVoctID(), mmsi);

        }

        if (sarData instanceof DatumPointDataSARIS) {
            DatumPointSARISDTO sarModelData = ((DatumPointDataSARIS) sarData).getModelData();
            voctMessage = new VOCTCommunicationService.VOCTCommunicationMessage(sarModelData, effortAllocationData, searchPattern,
                    sender, message, voctManager.getVoctID(), mmsi);

        }

        sendMaritimeCloudMessage(voctMessageList, new MmsiId((int) mmsi), voctMessage, this);

//        System.out.println("Sending VOCT SAR to mmsi: " + mmsi);

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
    public void messageReceivedByCloud(VOCTCommunicationMessage message) {
        // TODO Auto-generated method stub

        sruManager.sruSRUStatus(message.getReceiversMMSI(), CloudMessageStatus.RECEIVED_BY_CLOUD);
    }

    @Override
    public void messageHandled(VOCTCommunicationMessage message, VOCTCommunicationReply reply) {
        // TODO Auto-generated method stub
        sruManager.sruSRUStatus(message.getReceiversMMSI(), CloudMessageStatus.RECEIVED_BY_CLIENT);
    }

    public void sendCancelMessage(List<Long> srusToCancel) {
//        System.out.println("Send SAR cancel message " + srusToCancel.size());
        for (int i = 0; i < srusToCancel.size(); i++) {
            VOCTCommunicationMessage voctMessage = new VOCTCommunicationMessage(voctManager.getVoctID(), VoctMsgStatus.WITHDRAWN);

            boolean toSend = sendMaritimeCloudMessage(new MmsiId((int) (long) srusToCancel.get(i)), voctMessage, this);
        }

    }

}

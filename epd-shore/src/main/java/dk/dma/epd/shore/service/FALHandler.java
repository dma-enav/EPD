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
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.enavcloud.FALReportingService;
import dk.dma.epd.common.prototype.enavcloud.FALReportingService.FALReportMessage;
import dk.dma.epd.common.prototype.enavcloud.FALReportingService.FALReportReply;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.VOCTCommunicationMessage;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.VOCTCommunicationReply;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointDataSARIS;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.prototype.notification.GeneralNotification;
import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dk.dma.epd.common.prototype.notification.NotificationAlert.AlertType;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.service.FALHandlerCommon;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.VoctHandlerCommon;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon.VoctMsgStatus;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.shore.fal.FALManager;
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
public class FALHandler extends FALHandlerCommon implements Runnable {

    private boolean listenToSAR;
    /**
     * Network list for various SAR data objects
     */

    private List<ServiceEndpoint<FALReportMessage, FALReportReply>> voctMessageList = new ArrayList<>();
    private boolean running;
    private static final Logger LOG = LoggerFactory.getLogger(VoctHandlerCommon.class);

    public FALManager falManager;

    /**
     * Constructor
     */
    public FALHandler() {
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

        try {
            getMaritimeCloudConnection().serviceRegister(FALReportingService.INIT,
                    new InvocationCallback<FALReportingService.FALReportMessage, FALReportingService.FALReportReply>() {
                        public void process(FALReportMessage message,
                                InvocationCallback.Context<FALReportingService.FALReportReply> context) {

                            // LOG.info("Shore received a VOCT reply");
//                            System.out.println("Received a FAL report from Ship! " + message.getFalReport());

                            MaritimeId caller = context.getCaller();
                            long mmsi = MaritimeCloudUtils.toMmsi(context.getCaller());

                            falManager.addFALReport(message.getFalReport());

                            String type = "departure";

                            if (message.getFalReport().getFalform1().isArrival()) {
                                type = "arrival";
                            }

                            String desc = "New FAL Report from vessel " + message.getFalReport().getReportOwner() + " recieved at "
                                    + message.getSentDate() + " regarding an " + type;
                            sendNotification(NotificationSeverity.MESSAGE, "New FAL Report Recieved from "
                                    + message.getFalReport().getReportOwner(), desc);

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

    /**
     * Sends a new notification to the notification center with the given parameters
     * 
     * @param severity
     *            the notification severity
     * @param title
     *            the title
     * @param desc
     *            the description
     */
    private void sendNotification(NotificationSeverity severity, String title, String desc) {
        GeneralNotification notification = new GeneralNotification();
        notification.setSeverity(severity);
        notification.setTitle(title);
        notification.setDescription(desc);
        notification.setDate(PntTime.getDate());
        notification.addAlerts(new NotificationAlert(AlertType.POPUP, AlertType.SYSTEM_TRAY, AlertType.BEEP));
        EPD.getInstance().getNotificationCenter().addNotification(notification);
    }

    private void fetchVOCTMessageList() {
        // System.out.println("Checking for VOCT message list");
        try {

            voctMessageList = getMaritimeCloudConnection().serviceLocate(FALReportingService.INIT).nearest(Integer.MAX_VALUE).get();

        } catch (Exception e) {
            LOG.error(e.getMessage());

        }
    }

    public void sendVOCTMessage(long mmsi, SARData sarData, String sender, String message, boolean isAO, boolean isSearchPattern)
            throws InterruptedException, ExecutionException, TimeoutException {

        // System.out.println("Send to : " + mmsi);
        String mmsiStr = "mmsi://" + mmsi;

        FALReportMessage falMessage = new FALReportMessage();

        boolean toSend = sendMaritimeCloudMessage(new MmsiId((int) 0), falMessage, this);

//        System.out.println("Sending VOCT SAR to mmsi: " + mmsi);

    }

    // /**
    // * Checks for a ship with the given mmsi in the route suggestion service list
    // *
    // * @param mmsi
    // * the mmsi of the ship to search for
    // * @return if one such ship is available
    // */
    // public boolean shipAvailableForRouteSuggestion(long mmsi) {
    // return MaritimeCloudUtils.findServiceWithMmsi(falReportRecievers, (int) mmsi) != null;
    // }

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

        if (obj instanceof FALManager) {
            falManager = (FALManager) obj;
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
    public void messageReceivedByCloud(FALReportMessage message) {
        // TODO Auto-generated method stub

        // sruManager.sruSRUStatus(message.getReceiversMMSI(), CloudMessageStatus.RECEIVED_BY_CLOUD);
    }

    @Override
    public void messageHandled(FALReportMessage message, FALReportReply reply) {
        // TODO Auto-generated method stub
        // sruManager.sruSRUStatus(message.getReceiversMMSI(), CloudMessageStatus.RECEIVED_BY_CLIENT);
    }

}

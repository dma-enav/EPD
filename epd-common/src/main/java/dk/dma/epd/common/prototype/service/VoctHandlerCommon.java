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
package dk.dma.epd.common.prototype.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.maritimecloud.net.EndpointInvocationFuture;
import net.maritimecloud.net.MessageHeader;
import net.maritimecloud.net.mms.MmsClient;
import net.maritimecloud.util.Timestamp;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.voct.sardata.SARTextLogMessage;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon;
import dma.voct.AbstractSARTextingService;
import dma.voct.SARTextingService;
import dma.voct.SarText;
import dma.voct.VOCTReplyStatus;

/**
 * Intended route service implementation.
 * <p>
 * Listens for intended route broadcasts, and updates the vessel target when one
 * is received.
 */
public abstract class VoctHandlerCommon extends EnavServiceHandlerCommon {

    /**
     * Time an intended route is considered valid without update
     */
    public static final long ROUTE_TTL = 10 * 60 * 1000; // 10 min

    protected List<IIntendedRouteListener> listeners = new CopyOnWriteArrayList<>();

    public static final int CLOUD_TIMEOUT = 10; // Seconds

    protected VOCTManagerCommon voctManager;

    /**
     * Constructor
     */
    public VoctHandlerCommon() {
        super();

        getScheduler().scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    requestSARMessageSync();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MmsClient connection) {

        try {
            getMmsClient().endpointRegister(new AbstractSARTextingService() {

                @Override
                protected void sendMessage(MessageHeader header, SarText msg) {
                    // Receieved a message
                    // System.out.println("Message Receieved " + msg.getMsg());

                    if (voctManager != null) {
                        if (voctManager.getSarData() != null) {
                            // if (voctManager.getSarData().getTransactionId()
                            // == sarTransactionId) {
                            SARTextLogMessage sarMsg = new SARTextLogMessage(
                                    msg.getMsg(), msg.getPriority(), msg
                                            .getOriginalSender(), msg
                                            .getOriginalSender());

                            voctManager.addSARText(sarMsg);
                        }

                    }

                }

                @Override
                protected List<SarText> requestMessageSync(
                        MessageHeader header, Long sarTransactionId,
                        Long lastMessageRecieved) {
                    // Got a request message sync
                    System.out.println("Got a request message sync");
                    List<SarText> msgList = new ArrayList<>();
                    if (voctManager != null) {
                        if (voctManager.getSarData() != null) {

                            // if (voctManager.getSarData().getTransactionId()
                            // == sarTransactionId) {

                            // Prepare list of all messages

                            for (SARTextLogMessage sarMessage : voctManager
                                    .getSarData().getSarMessages()) {

                                if (sarMessage.getOriginalSender()
                                        - EPD.getInstance().getMmsi() == 0
                                        && sarMessage.getOriginalSentDate() > lastMessageRecieved) {

                                    SarText sarText = new SarText();
                                    sarText.setMsg(sarMessage.getMsg());
                                    sarText.setOriginalSendDate(sarMessage
                                            .getOriginalSentDate());
                                    sarText.setOriginalSender(sarMessage
                                            .getOriginalSender());
                                    sarText.setPriority(sarMessage
                                            .getPriority());
                                    msgList.add(sarText);
                                    // System.out.println("Adding");

                                }
                            }

                            System.out.println("We found " + msgList.size()
                                    + " own messages that we havent sent");

                            // if (msgList.size() > 0) {
                            //
                            // System.out.println("And we want to send some messages");
                            //
                            // List<SARTextingService> availableEndpoints;
                            // try {
                            // availableEndpoints = getMmsClient()
                            // .endpointLocate(SARTextingService.class).findAll()
                            // .get();
                            // System.out.println("Running through shit " +
                            // availableEndpoints.size());
                            //
                            // for (int i = 0; i < availableEndpoints
                            // .size(); i++) {
                            //
                            // System.out.println("Should we send to " +
                            // availableEndpoints.get(i)
                            // .getRemoteId().getIdAsInt());
                            // System.out.println("From " +
                            // header.getSender().getIdAsInt());
                            // if (availableEndpoints.get(i)
                            // .getRemoteId().getIdAsInt() == header
                            // .getSender().getIdAsInt()) {
                            // System.out
                            // .println("Sending sync msg "
                            // + msgList.size());
                            // availableEndpoints.get(i)
                            // .syncSARMessages(msgList);
                            //
                            // }
                            //
                            // }
                            //
                            //
                            // } catch (InterruptedException e) {
                            // // TODO Auto-generated catch block
                            // e.printStackTrace();
                            // } catch (ExecutionException e) {
                            // // TODO Auto-generated catch block
                            // e.printStackTrace();
                            // }
                            //
                            //
                            //
                            //
                            // }

                            // }
                        }
                    }
                    return msgList;

                }

            }).awaitRegistered(4, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendVoctMessage(final SarText sarText) {
        SARTextLogMessage sarMsg = new SARTextLogMessage(sarText.getMsg(),
                sarText.getPriority(), sarText.getOriginalSender(),
                sarText.getOriginalSendDate());
        voctManager.addSARText(sarMsg);
        if (voctManager != null && getMmsClient() != null) {

            try {
                List<SARTextingService> availableEndpoints = getMmsClient()
                        .endpointLocate(SARTextingService.class).findAll()
                        .get();

                for (int i = 0; i < availableEndpoints.size(); i++) {
                    availableEndpoints.get(i).sendMessage(sarText);
                }

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.out.println("Voctmanager is null");
        }

    }

    /**
     * @return the voctManager
     */
    public VOCTManagerCommon getVoctManager() {
        return voctManager;
    }

    private void requestSARMessageSync() throws InterruptedException,
            ExecutionException {
        System.out.println("Request msg");
        if (voctManager != null) {
            if (voctManager.getSarData() != null) {
                // if (voctManager.getSarData().getTransactionId() != null) {
                HashMap<Long, SARTextLogMessage> sarMap = new HashMap<>();

                for (SARTextLogMessage sarMessage : voctManager.getSarData()
                        .getSarMessages()) {

                    if (sarMap.containsKey(sarMessage.getOriginalSender())) {
                        // Is the new message newer? overwrite
                        if (sarMap.get(sarMessage.getOriginalSender())
                                .getOriginalSentDate() < sarMessage
                                .getOriginalSentDate()) {
                            sarMap.put(sarMessage.getOriginalSender(),
                                    sarMessage);
                            System.out.println("Message added with last "
                                    + sarMessage.getOriginalSentDate()
                                    + " for " + sarMessage.getOriginalSender());
                        }
                    } else {
                        sarMap.put(sarMessage.getOriginalSender(), sarMessage);
                    }
                }

                System.out.println("Requesting SAR Message Sync");
                List<SARTextingService> availableEndpoints = getMmsClient()
                        .endpointLocate(SARTextingService.class).findAll()
                        .get();

                for (int i = 0; i < availableEndpoints.size(); i++) {

                    Long endPointId = (long) availableEndpoints.get(i)
                            .getRemoteId().getIdAsInt();

                    long lastMessage = 0;

                    if (sarMap.containsKey(endPointId)) {
                        lastMessage = sarMap.get(endPointId)
                                .getOriginalSentDate();
                    }

                    List<SarText> msg = availableEndpoints
                            .get(i)
                            .requestMessageSync(
                                    voctManager.getSarData().getTransactionId(),
                                    lastMessage).get();
                    System.out.println("Sync sent to " + endPointId);

                    for (SarText sarText : msg) {
                        SARTextLogMessage sarMsg = new SARTextLogMessage(
                                sarText.getMsg(), sarText.getPriority(),
                                sarText.getOriginalSender(),
                                sarText.getOriginalSender());

                        voctManager.addSARText(sarMsg);
                    }

                }
                // }
            }
        }

    }

    /**
     * Only used on ship for reconnection
     * 
     * @param accepted
     * @param string
     * @param transactionId
     * @param oscId
     */
    public void sendVOCTReply(VOCTReplyStatus recievedAccepted, String message,
            long messageId, Long oscId) throws InterruptedException,
            ExecutionException {

    }

}

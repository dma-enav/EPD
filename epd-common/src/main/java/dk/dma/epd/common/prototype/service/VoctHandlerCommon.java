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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.maritimecloud.net.MessageHeader;
import net.maritimecloud.net.mms.MmsClient;
import dma.voct.AbstractSARTextingService;
import dma.voct.SARTextingService;
import dma.voct.SarText;
import dma.voct.VOCTReplyEndpoint;

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
    protected List<SarText> additionalInformationMsgs = new ArrayList<SarText>();
    protected List<IVoctInfoListener> voctInfoMsgListener = new CopyOnWriteArrayList<>();
    private static final int BROADCAST_RADIUS = Integer.MAX_VALUE;
    public static final int CLOUD_TIMEOUT = 10; // Seconds

    /**
     * Constructor
     */
    public VoctHandlerCommon() {
        super();

        getScheduler().scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

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
                    System.out.println("Message Receieved " + msg.getMsg());
                    additionalInformationMsgs.add(msg);
                    notifyVoctInfoMsgListeners();
                }

            }).awaitRegistered(4, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendVoctMessage(final SarText sarText) {
        additionalInformationMsgs.add(sarText);
        notifyVoctInfoMsgListeners();

        try {
            List<SARTextingService> availableEndpoints = getMmsClient()
                    .endpointLocate(SARTextingService.class).findAll()
                    .timeout(CLOUD_TIMEOUT, TimeUnit.SECONDS).get();

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

    }

    private void notifyVoctInfoMsgListeners() {
        for (int i = 0; i < voctInfoMsgListener.size(); i++) {
            voctInfoMsgListener.get(i).voctMessageUpdate();
        }
    }

    public void addVoctSarInfoListener(IVoctInfoListener listener) {
        voctInfoMsgListener.add(listener);
    }

    /**
     * @return the additionalInformationMsg
     */
    public List<SarText> getAdditionalInformationMsgs() {
        return additionalInformationMsgs;
    }

    public interface IVoctInfoListener {

        /**
         * Called when a SAR info message has been received or sent
         */
        void voctMessageUpdate();

        /**
         * Called when adding SAR info
         */
        // void voctMessageSent(VOCTSARInfoMessage message);
    }

}

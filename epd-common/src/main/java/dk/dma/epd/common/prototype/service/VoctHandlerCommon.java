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

import dk.dma.epd.common.prototype.enavcloud.VOCTSARInfoMessage;
import net.maritimecloud.net.mms.MmsClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Intended route service implementation.
 * <p>
 * Listens for intended route broadcasts, and updates the vessel target when one is received.
 */
public abstract class VoctHandlerCommon extends EnavServiceHandlerCommon {

    /**
     * Time an intended route is considered valid without update
     */
    public static final long ROUTE_TTL = 10 * 60 * 1000; // 10 min

    protected List<IIntendedRouteListener> listeners = new CopyOnWriteArrayList<>();
    protected List<VOCTSARInfoMessage> additionalInformationMsgs = new ArrayList<VOCTSARInfoMessage>();
    protected List<IVoctInfoListener> voctInfoMsgListener = new CopyOnWriteArrayList<>();
    private static final int BROADCAST_RADIUS = Integer.MAX_VALUE;

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

// TODO: Maritime Cloud 0.2 re-factoring
//        getMmsClient().broadcastListen(VOCTSARInfoMessage.class, new BroadcastListener<VOCTSARInfoMessage>() {
//            public void onMessage(BroadcastMessageHeader l, VOCTSARInfoMessage r) {
//
//                System.out.println("SAR Info message recieved");
//                additionalInformationMsgs.add(r);
//                notifyVoctInfoMsgListeners();
//            }
//        });

    }

    public void sendVoctMessage(final VOCTSARInfoMessage r) {
        additionalInformationMsgs.add(r);
        notifyVoctInfoMsgListeners();

// TODO: Maritime Cloud 0.2 re-factoring
//        Runnable broadcastMessage = new Runnable() {
//            @Override
//            public void run() {
//                BroadcastOptions options = new BroadcastOptions();
//                options.setBroadcastRadius(BROADCAST_RADIUS);
//                getMmsClient().broadcast(r, options);
//            }
//        };
//        submitIfConnected(broadcastMessage);
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
    public List<VOCTSARInfoMessage> getAdditionalInformationMsgs() {
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

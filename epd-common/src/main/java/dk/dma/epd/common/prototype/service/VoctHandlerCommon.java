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
package dk.dma.epd.common.prototype.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.broadcast.BroadcastListener;
import net.maritimecloud.net.broadcast.BroadcastMessageHeader;
import net.maritimecloud.net.broadcast.BroadcastOptions;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceRapidResponse.VOCTCommunicationMessageRapidResponse;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceRapidResponse.VOCTCommunicationReplyRapidResponse;
import dk.dma.epd.common.prototype.enavcloud.VOCTSARInfoMessage;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon.ICloudMessageListener;

/**
 * Intended route service implementation.
 * <p>
 * Listens for intended route broadcasts, and updates the vessel target when one is received.
 */
public abstract class VoctHandlerCommon extends EnavServiceHandlerCommon implements
        ICloudMessageListener<VOCTCommunicationMessageRapidResponse, VOCTCommunicationReplyRapidResponse> {

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
    public void cloudConnected(MaritimeCloudClient connection) {

        getMaritimeCloudConnection().broadcastListen(VOCTSARInfoMessage.class, new BroadcastListener<VOCTSARInfoMessage>() {
            public void onMessage(BroadcastMessageHeader l, VOCTSARInfoMessage r) {

                System.out.println("SAR Info message recieved");
                additionalInformationMsgs.add(r);
                notifyVoctInfoMsgListeners();
            }
        });

    }

    public void sendVoctMessage(final VOCTSARInfoMessage r) {
        additionalInformationMsgs.add(r);
        notifyVoctInfoMsgListeners();

        Runnable broadcastMessage = new Runnable() {
            @Override
            public void run() {
                BroadcastOptions options = new BroadcastOptions();
                options.setBroadcastRadius(BROADCAST_RADIUS);
                getMaritimeCloudConnection().broadcast(r, options);
            }
        };
        submitIfConnected(broadcastMessage);
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
         * 
         * @param targetId
         *            the maritime id of the target
         */
        void voctMessageUpdate();

        /**
         * Called when adding SAR info
         */
        // void voctMessageSent(VOCTSARInfoMessage message);
    }

}

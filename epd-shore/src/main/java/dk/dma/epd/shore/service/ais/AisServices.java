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
package dk.dma.epd.shore.service.ais;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.ais.message.AisMessage6;
import dk.dma.ais.message.binary.AsmAcknowledge;
import dk.dma.ais.message.binary.RouteSuggestion;
import dk.dma.ais.message.binary.RouteSuggestionReply;
import dk.dma.ais.reader.SendRequest;
import dk.dma.epd.common.prototype.ais.AisAdressedRouteSuggestion;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSensor;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.settings.ESDSettings;

/**
 * AIS service component providing an AIS link interface.
 */
public class AisServices extends MapHandlerChild {

    private static final Logger LOG = LoggerFactory.getLogger(AisServices.class);

    private static final long INTENDED_ROUTE_BROADCAST_INTERVAL = 6 * 60 * 1000; // 6 min

    private Integer sequence = 0;
    private NmeaSensor nmeaSensor;
    private ESDSettings settings;
    private AisHandler aisHandler;

    private Date lastIntendedRouteBroadcast = new Date(0);

    public enum AIS_STATUS {
        NOT_SENT, FAILED, SENT_NOT_ACK, RECIEVED_APP_ACK, RECIEVED_ACCEPTED, RECIEVED_REJECTED, RECIEVED_NOTED
    }

    public AisServices() {

    }

    /**
     * Acknowledge the reception of a route suggestion
     * @param routeInformation
     */
    public void acknowledgeRouteSuggestion(AisMessage6 receivedMsg6, RouteSuggestion routeSuggestion) {
        LOG.debug("In acknowledgeRouteSuggestion()");
        if (!allowSend()) {
            return;
        }
        // Create acknowledge message
        AsmAcknowledge acknowledge = new AsmAcknowledge();
        acknowledge.setReceivedFi(routeSuggestion.getFi());
        acknowledge.setReceivedDac(routeSuggestion.getDac());
        acknowledge.setAiAvailable(1);
        acknowledge.setAiResponse(1);
        acknowledge.setTextSequenceNum(routeSuggestion.getMsgLinkId());

        // Create AIS msg 6
        AisMessage6 msg6 = new AisMessage6();
        msg6.setDestination(receivedMsg6.getUserId());
        msg6.setAppMessage(acknowledge);
        msg6.setRetransmit(0);

        // Create a send request
        SendRequest sendRequest = new SendRequest(msg6, nextSeq(), receivedMsg6.getUserId());

        // Create a send thread
        AisSendThread aisSendThread = new AisSendThread(sendRequest, this);

        // Start send thread
        aisSendThread.start();
    }

    /**
     * Reply on a route suggestion
     * @param routeSuggestion
     */
    public void routeSuggestionReply(AisAdressedRouteSuggestion routeSuggestion) {
        if (!allowSend()) {
            return;
        }
        // Create reply message
        RouteSuggestionReply routeSuggestionReply = new RouteSuggestionReply();
        routeSuggestionReply.setRefMsgLinkId(routeSuggestion.getMsgLinkId());
        switch (routeSuggestion.getStatus()) {
        case ACCEPTED:
            routeSuggestionReply.setResponse(0);
            break;
        case NOTED:
            routeSuggestionReply.setResponse(2);
            break;
        default:
            routeSuggestionReply.setResponse(1);
            break;
        }
        // Create AIS msg 6
        AisMessage6 msg6 = new AisMessage6();
        msg6.setDestination(routeSuggestion.getSender());
        msg6.setAppMessage(routeSuggestionReply);
        msg6.setRetransmit(0);

        // Create a send request
        SendRequest sendRequest = new SendRequest(msg6, nextSeq(), (int)routeSuggestion.getSender());

        // Create a send thread
        AisSendThread aisSendThread = new AisSendThread(sendRequest, this);

        // Start send thread
        aisSendThread.start();
    }

    public void intendedRouteBroadcast(ActiveRoute activeRoute) {

    }


    private boolean doBroadcastIntented() {
        return allowSend() && settings.getAisSettings().isBroadcastIntendedRoute();
    }

    private boolean allowSend() {
        return settings.getAisSettings().isAllowSending();
    }

    public void setLastIntendedRouteBroadcast() {
        synchronized (lastIntendedRouteBroadcast) {
            lastIntendedRouteBroadcast = GnssTime.getInstance().getDate();
        }
    }

    public long getLastIntendedRouteBroadcast() {
        synchronized (lastIntendedRouteBroadcast) {
            return lastIntendedRouteBroadcast.getTime();
        }
    }

    public void periodicIntendedRouteBroadcast(ActiveRoute activeRoute) {
        if (!doBroadcastIntented()) {
            return;
        }
        long elapsed = GnssTime.getInstance().getDate().getTime() - getLastIntendedRouteBroadcast();
        if (elapsed >= INTENDED_ROUTE_BROADCAST_INTERVAL) {
            intendedRouteBroadcast(activeRoute);
        }
    }

    public NmeaSensor getNmeaSensor() {
        return nmeaSensor;
    }

    private int nextSeq() {
        synchronized (sequence) {
            int seq = sequence;
            sequence = (sequence + 1) % 4;
            return seq;
        }
    }

    public void sendResult(boolean sendOk) {
        if (aisHandler == null) {
            return;
        }
        if (sendOk) {
            aisHandler.getAisStatus().markSuccesfullSend();
        } else {
            aisHandler.getAisStatus().markFailedSend();
        }
    }

    @Override
    public void findAndInit(Object obj) {
        if (nmeaSensor == null && obj instanceof NmeaSensor) {
            nmeaSensor = (NmeaSensor)obj;
        }
        else if (settings == null && obj instanceof ESDSettings) {
            settings = (ESDSettings)obj;
        }
        else if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler)obj;
        }
    }

}

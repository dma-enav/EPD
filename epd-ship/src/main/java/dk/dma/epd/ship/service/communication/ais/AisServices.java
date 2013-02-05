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
package dk.dma.epd.ship.service.communication.ais;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.ais.message.AisMessage6;
import dk.dma.ais.message.AisMessage8;
import dk.dma.ais.message.AisPosition;
import dk.dma.ais.message.binary.AsmAcknowledge;
import dk.dma.ais.message.binary.BroadcastIntendedRoute;
import dk.dma.ais.message.binary.RouteSuggestion;
import dk.dma.ais.message.binary.RouteSuggestionReply;
import dk.dma.ais.reader.SendRequest;
import dk.dma.epd.common.prototype.ais.AisAdressedRouteSuggestion;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSensor;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.settings.EPDAisSettings;
import dk.dma.epd.ship.settings.EPDSettings;

/**
 * AIS service component providing an AIS link interface.
 */
public class AisServices extends MapHandlerChild {
    
    private static final Logger LOG = LoggerFactory.getLogger(AisServices.class);
    
    private static final long INTENDED_ROUTE_BROADCAST_INTERVAL = 6 * 60 * 1000; // 6 min
    
    private Integer sequence = 0;
    private NmeaSensor nmeaSensor;
    private EPDSettings settings;
    private AisHandler aisHandler;
    
    private Date lastIntendedRouteBroadcast = new Date(0);
    
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
        LOG.debug("In intendedRouteBroadcast()");
        if (!doBroadcastIntented()) {
            return;
        }
        
        // Create intended route ASM
        BroadcastIntendedRoute intendedRoute;
        if (activeRoute == null) {
            intendedRoute = noIntendedRoute();
        } else {
            intendedRoute = intendedRouteFromActiveRoute(activeRoute, settings.getAisSettings());
        }
        
        // Create AIS message 8
        AisMessage8 msg8 = new AisMessage8();
        msg8.setAppMessage(intendedRoute);
        
        // Create a send request
        SendRequest sendRequest = new SendRequest(msg8, nextSeq());
        
        // Create a send thread
        AisIntendedRouteSendThread aisSendThread = new AisIntendedRouteSendThread(sendRequest, this);
        
        // Start send thread
        aisSendThread.start();
    }
    
    private static BroadcastIntendedRoute intendedRouteFromActiveRoute(ActiveRoute activeRoute, EPDAisSettings aisSettings) {
        BroadcastIntendedRoute intendedRoute = new BroadcastIntendedRoute();
        
        // Recalculate all remaining ETA's
        if (!activeRoute.reCalcRemainingWpEta()) {
            // No valid ETA
            return noIntendedRoute();
        }         
        
        int maxWps = aisSettings.getIntendedRouteMaxWps();
        if (maxWps == 0) {
            maxWps = 8;
        }
        long maxTimeLen = aisSettings.getIntendedRouteMaxTime() * 60 * 1000;
        if (maxTimeLen == 0) {
            maxTimeLen = Long.MAX_VALUE;
        }
        
        // Get first and last wp
        int startWp = activeRoute.getActiveWaypointIndex();
        // Find last wp if no time limit
        int lastWp = startWp;
        int maxWp = startWp + maxWps - 1;
        while (lastWp < maxWp && lastWp < activeRoute.getWaypoints().size() - 1) {
            long timeLen = activeRoute.getWpEta(lastWp).getTime() - activeRoute.getWpEta(startWp).getTime(); 
            if (lastWp > startWp + 1 && timeLen >= maxTimeLen) {
                lastWp--;
                break;
            }
            lastWp++;
        }
                
        // Find start and duration
        Date start = activeRoute.getWpEta(startWp);
        Date end = activeRoute.getWpEta(lastWp);
        int duration = (int)(end.getTime() - start.getTime()) / 1000 / 60;
        intendedRoute.setDuration(duration);
        
        // Set start time
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
        intendedRoute.setStartMonth(cal.get(Calendar.MONTH) + 1);
        intendedRoute.setStartDay(cal.get(Calendar.DAY_OF_MONTH));
        intendedRoute.setStartHour(cal.get(Calendar.HOUR_OF_DAY));
        intendedRoute.setStartMin(cal.get(Calendar.MINUTE));
        
        // Add waypoints
        for (int i = startWp; i <= lastWp; i++) {
            intendedRoute.addWaypoint(new AisPosition(activeRoute.getWaypoints().get(i).getPos()));
        }
        
        return intendedRoute;
    }
    
    private static BroadcastIntendedRoute noIntendedRoute() {
        BroadcastIntendedRoute intendedRoute = new BroadcastIntendedRoute();
        
        // Use start as now
        Calendar cal = Calendar.getInstance();
        cal.setTime(GnssTime.getInstance().getDate());
        cal.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
        
        intendedRoute.setStartMonth(cal.get(Calendar.MONTH) + 1);
        intendedRoute.setStartDay(cal.get(Calendar.DAY_OF_MONTH));
        intendedRoute.setStartHour(cal.get(Calendar.HOUR_OF_DAY));
        intendedRoute.setStartMin(cal.get(Calendar.MINUTE));
        
        // Set no duration and 
        intendedRoute.setDuration(0);
        intendedRoute.setWaypointCount(0);        
        
        return intendedRoute;
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
        else if (settings == null && obj instanceof EPDSettings) {
            settings = (EPDSettings)obj;
        }
        else if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler)obj;
        }
    }

}

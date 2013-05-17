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
package dk.dma.epd.common.prototype.ais;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import net.jcip.annotations.ThreadSafe;
import dk.dma.ais.message.binary.RouteInformation;
import dk.dma.ais.message.binary.RouteMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;

/**
 * Abstract base class for AIS route data
 */
@ThreadSafe
public abstract class AisRouteData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected Date received;
    protected long duration;
    protected Date etaFirst;
    protected Date etaLast;
    protected List<Position> waypoints = new ArrayList<>();
    protected List<Double> ranges = new ArrayList<>();
    protected Double routeRange;
    protected long sender;
    protected int msgLinkId;
    protected int routeType;
    protected int senderClassification;
    
    /**
     * Copy constructor
     * @param routeData
     */
    public AisRouteData(AisRouteData routeData) {
        this.waypoints = routeData.waypoints;
        this.received = routeData.received;
        this.duration = routeData.duration;
        this.ranges = routeData.ranges;
        this.routeRange = routeData.routeRange;
        this.etaFirst = routeData.etaFirst;
        this.etaLast = routeData.etaLast;
    }
    
    public AisRouteData(RouteMessage routeMessage) {
        received = GnssTime.getInstance().getDate();
        
        for (int i=0; i < routeMessage.getWaypoints().size(); i++) {
            Position wp = routeMessage.getWaypoints().get(i).getGeoLocation();
            
            if (wp.getLatitude() < 54 || wp.getLatitude() > 60 || wp.getLongitude() < 8 || wp.getLongitude() > 14) {
                System.out.println("ERROR: Wrong wp in AIS broadcast: " + wp);
                System.out.println("i: " + i + " routeMessage.getWaypoints().size(): " + routeMessage.getWaypoints().size());
                System.out.println("routeMessage.getWaypointCount(): " + routeMessage.getWaypointCount());
            } else {
                waypoints.add(wp);
            }
        }
        
        if (routeMessage.getStartMonth() > 0 && routeMessage.getStartDay() > 0) {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0000"));
            cal.set(Calendar.MONTH, routeMessage.getStartMonth() - 1);
            cal.set(Calendar.DAY_OF_MONTH, routeMessage.getStartDay());
            cal.set(Calendar.HOUR_OF_DAY, routeMessage.getStartHour());
            cal.set(Calendar.MINUTE, routeMessage.getStartMin());
            cal.set(Calendar.MILLISECOND, 0);
            etaFirst = cal.getTime();
        }
        
        duration = routeMessage.getDuration() * 60 * 1000;
        if (duration > 0 && etaFirst != null) {
            etaLast = new Date(etaFirst.getTime() + duration);
        }
        
        // Find ranges on each leg
        routeRange = 0.0;
        ranges.add(routeRange);
        for (int i=0; i < waypoints.size() - 1; i++) {
            double dist = waypoints.get(i).rhumbLineDistanceTo(waypoints.get(i + 1)) / 1852.0;
            routeRange += dist;
            ranges.add(routeRange);
        }
    }
    
    /**
     * Constructor given AIS route information
     * @param routeInformation
     */
    public AisRouteData(RouteInformation routeInformation) {
        this((RouteMessage)routeInformation);        
        msgLinkId = routeInformation.getMsgLinkId();
        routeType = routeInformation.getRouteType();
        senderClassification = routeInformation.getSenderClassification();
    }
    
    public synchronized boolean isCancel() {
        return routeType == 31;
    }

    public synchronized Date getReceived() {
        return received;
    }
    
    public synchronized long getDuration() {
        return duration;
    }
    
    public synchronized boolean hasRoute() {
        return waypoints != null && waypoints.size() > 0;
    }

    public synchronized Date getEtaFirst() {
        return etaFirst;
    }
    
    public synchronized Date getEtaLast() {
        return etaLast;
    }
    
    public synchronized List<Position> getWaypoints() {
        return waypoints;
    }
    
    public synchronized long getSender() {
        return sender;
    }
    
    public synchronized void setSender(long sender) {
        this.sender = sender;
    }
    
    public synchronized int getMsgLinkId() {
        return msgLinkId;
    }
    
    public synchronized int getRouteType() {
        return routeType;
    }
    
    public synchronized int getSenderClassification() {
        return senderClassification;
    }

}

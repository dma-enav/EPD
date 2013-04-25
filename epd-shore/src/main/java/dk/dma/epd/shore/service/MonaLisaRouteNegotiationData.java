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
package dk.dma.epd.shore.service;

import java.util.ArrayList;
import java.util.List;

import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteRequestReply;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteStatus;

public class MonaLisaRouteNegotiationData {

    private long id;
    private long mmsi;
    private List<MonaLisaRouteRequestMessage> routeMessages = new ArrayList<MonaLisaRouteRequestMessage>();
    private List<MonaLisaRouteRequestReply> routeReplys = new ArrayList<MonaLisaRouteRequestReply>();
    private MonaLisaRouteStatus status;
    private boolean handled;
    private boolean completed;
    
    
    public MonaLisaRouteNegotiationData(long id, long mmsi) {
        super();
        this.id = id;
        this.mmsi = mmsi;
        this.status = MonaLisaRouteStatus.PENDING;
        handled = false;
    }
    
    
    
    /**
     * @return the completed
     */
    public boolean isCompleted() {
        return completed;
    }



    /**
     * @param completed the completed to set
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }



    public MonaLisaRouteStatus getStatus() {
        return status;
    }



    public void setStatus(MonaLisaRouteStatus status) {
        this.status = status;
    }



    public void addMessage(MonaLisaRouteRequestMessage message){
        routeMessages.add(message);
    }
    
    public void addReply(MonaLisaRouteRequestReply reply){
        routeReplys.add(reply);
    }
    
    public long getId() {
        return id;
    }
    
    public long getMmsi() {
        return mmsi;
    }
    
    public List<MonaLisaRouteRequestMessage> getRouteMessage() {
        return routeMessages;
    }
    
    public List<MonaLisaRouteRequestReply> getRouteReply() {
        return routeReplys;
    }

    public boolean isHandled() {
        return handled;
    }



    public void setHandled(boolean handled) {
        this.handled = handled;
    }
    
    
    
    
}

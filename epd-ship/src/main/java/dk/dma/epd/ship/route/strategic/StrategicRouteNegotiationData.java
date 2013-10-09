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
package dk.dma.epd.ship.route.strategic;

import java.util.ArrayList;
import java.util.List;

import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestReply;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;

public class StrategicRouteNegotiationData {

    private long id;
    private List<StrategicRouteRequestMessage> routeMessages = new ArrayList<StrategicRouteRequestMessage>();
    private List<StrategicRouteRequestReply> routeReplys = new ArrayList<StrategicRouteRequestReply>();
    private StrategicRouteStatus status;
    private boolean handled;
    
    
    public StrategicRouteNegotiationData(long id) {
        super();
        this.id = id;
        this.status = StrategicRouteStatus.PENDING;
        handled = false;
    }
    
    public Route getLatestRoute(){
        if (routeMessages.size() > routeReplys.size()){
            return routeMessages.get(routeMessages.size() - 1).getRoute();
        }else{
            return routeReplys.get(routeReplys.size() - 1).getRoute();
        }
    }
    
    public StrategicRouteStatus getStatus() {
        return status;
    }



    public void setStatus(StrategicRouteStatus status) {
        this.status = status;
    }



    public void addMessage(StrategicRouteRequestMessage message){
        routeMessages.add(message);
    }
    
    public void addReply(StrategicRouteRequestReply reply){
        routeReplys.add(reply);
    }
    
    public long getId() {
        return id;
    }
    
    public List<StrategicRouteRequestMessage> getRouteMessage() {
        return routeMessages;
    }
    
    public List<StrategicRouteRequestReply> getRouteReply() {
        return routeReplys;
    }

    public boolean isHandled() {
        return handled;
    }


    public void setHandled(boolean handled) {
        this.handled = handled;
    }
    
    
}

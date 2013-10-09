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

import java.io.Serializable;
import java.util.Date;

import dk.dma.epd.common.prototype.ais.AisAdressedRouteSuggestion.Status;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.model.route.Route;

public class RecievedRoute implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    Date recieved;
    Date sent;
    String sender;
    Route route;
    String message;
    String replySent;
    long id;
    private boolean hidden;
    private Status status = Status.PENDING;
    

    public RecievedRoute(RouteSuggestionMessage suggestionMessage) {
        this.sender = suggestionMessage.getSender();
        this.sent = suggestionMessage.getSent();
        this.recieved = new Date();
        this.route = new Route(suggestionMessage.getRoute());
        this.message = suggestionMessage.getMessage();
        id = suggestionMessage.getId();
    }

    public Date getRecieved() {
        return recieved;
    }

    public void setRecieved(Date recieved) {
        this.recieved = recieved;
    }

    public Date getSent() {
        return sent;
    }

    public void setSent(Date sent) {
        this.sent = sent;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReplySent() {
        return replySent;
    }

    public void setReplySent(String replySent) {
        this.replySent = replySent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

 
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        switch (status) {
        case ACCEPTED:
        case NOTED:
            setHidden(false);
            break;
        case REJECTED:        
        case IGNORED:
        case CANCELLED:
            setHidden(true);
            break;
        case PENDING:
            break;
        default:
            break;
        }
        this.status = status;
    }
    
    public boolean isReplied() {
        return status == Status.ACCEPTED || status == Status.NOTED || status == Status.REJECTED;
    }
    
    public boolean isHidden() {
        return hidden;
    }
    
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
    
    public boolean isAcceptable() {
        return status == Status.PENDING || status == Status.IGNORED; 
    }
    
    public boolean isRejectable() {
        return status == Status.PENDING || status == Status.IGNORED;
    }
    
    public boolean isNoteable() {
        return status == Status.PENDING || status == Status.IGNORED;
    }
    
    public boolean isIgnorable() {
        return status == Status.PENDING; 
    }
    
    public boolean isPostponable() {
        return status == Status.PENDING; 
    }
    
    public void cancel() {
        setStatus(Status.CANCELLED);
    }
    
    
    

}

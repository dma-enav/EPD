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

import java.util.Date;

import net.jcip.annotations.ThreadSafe;

import dk.dma.ais.message.binary.RouteInformation;
import dk.dma.ais.message.binary.RouteSuggestion;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;

/**
 * Class representing an addressed route suggestion
 */
@ThreadSafe
public class AisAdressedRouteSuggestion extends AisIntendedRoute {
    private static final long serialVersionUID = 1L;
    
    /**
     * Possible status of a suggestion
     */
    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED,
        NOTED,
        IGNORED,
        CANCELLED,
    }
    
    private Status status = Status.PENDING;
    private boolean hidden;

    /**
     * Copy constructor
     * @param routeSuggestion
     */
    public AisAdressedRouteSuggestion(RouteSuggestion routeSuggestion) {
        super(routeSuggestion);
        this.msgLinkId = routeSuggestion.getMsgLinkId();
    }
    
    /**
     * Constructor given AIS route information
     * @param routeInformation
     */
    public AisAdressedRouteSuggestion(RouteInformation routeInformation) {
        super(routeInformation);
        
        // Check if ETA in the past
        Date now = GnssTime.getInstance().getDate();
        if (etaFirst != null && etaFirst.before(now)) {
            etaFirst = null;
            speed = null;
        }
    }
    
    public synchronized Status getStatus() {
        return status;
    }
    
    public synchronized void setStatus(Status status) {
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
    
    public synchronized boolean isReplied() {
        return status == Status.ACCEPTED || status == Status.NOTED || status == Status.REJECTED;
    }
    
    public synchronized boolean isHidden() {
        return hidden;
    }
    
    public synchronized void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
    
    public synchronized boolean isAcceptable() {
        return status == Status.PENDING || status == Status.IGNORED; 
    }
    
    public synchronized boolean isRejectable() {
        return status == Status.PENDING || status == Status.IGNORED;
    }
    
    public synchronized boolean isNoteable() {
        return status == Status.PENDING || status == Status.IGNORED;
    }
    
    public synchronized boolean isIgnorable() {
        return status == Status.PENDING; 
    }
    
    public synchronized boolean isPostponable() {
        return status == Status.PENDING; 
    }
    
    public synchronized void cancel() {
        setStatus(Status.CANCELLED);
    }

}

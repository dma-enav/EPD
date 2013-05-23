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
package dk.dma.epd.ship.status;

import java.util.Date;

import net.jcip.annotations.ThreadSafe;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.text.Formatter;

/**
 * Status for shore services
 */
@ThreadSafe
public class ShoreServiceStatus extends ComponentStatus {

    private Date lastContact;
    private Date lastFailed;
    private ShoreServiceException lastException;

    public ShoreServiceStatus() {
        super("Shore services");
        shortStatusText = "No services performed yet";
    }

    public synchronized void markContactSuccess() {
        lastContact = new Date();
        status = Status.OK;
        shortStatusText = "Last shore contact: " + lastContact;
    }

    public synchronized void markContactError(ShoreServiceException e) {        
        lastFailed = new Date();
        status = Status.ERROR;
        this.lastException = e;
        shortStatusText = "Last failed shore contact: " + Formatter.formatLongDateTime(lastFailed);
    }

    public synchronized Date getLastContact() {
        return lastContact;
    }

    public synchronized Date getLastFailed() {
        return lastFailed;
    }
    
    @Override
    public synchronized String getStatusHtml() {
        StringBuilder buf = new StringBuilder();
        buf.append("Contact: " + status.name() + "<br/>");
        if (status == Status.ERROR) {
            buf.append("Last error: " + Formatter.formatLongDateTime(lastFailed) + "<br/>");
            buf.append("Error message: " + lastException.getMessage());
            if (lastException.getExtraMessage() != null) {
                 buf.append(": " + lastException.getExtraMessage());
            }
        } else {
            buf.append("Last contact: " + Formatter.formatLongDateTime(lastContact));
        }
        
        
        return buf.toString();
    }

}

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
package dk.dma.epd.common.prototype.status;

import java.util.Date;

import dk.dma.epd.common.text.Formatter;


/**
 * Status for shore services
 */
public class WMSStatus extends ComponentStatus {

    private Date lastContact;
    private Date lastFailed;
    private Exception lastException;

    public WMSStatus() {
        super("WMS services");
        shortStatusText = "No services performed yet";
    }

    public synchronized void markContactSuccess() {
        lastContact = new Date();
        status = Status.OK;
        shortStatusText = "Last wms contact: " + lastContact;
    }

    public synchronized void markContactError(Exception e) {
        lastFailed = new Date();
        status = Status.ERROR;
        this.lastException = e;
        shortStatusText = "Last failed wms contact: " + Formatter.formatLongDateTime(lastFailed);
    }

    public Date getLastContact() {
        return lastContact;
    }

    public Date getLastFailed() {
        return lastFailed;
    }

    @Override
    public String getStatusHtml() {
        StringBuilder buf = new StringBuilder();
        buf.append("Contact: " + status.name() + "<br/>");
        if (status == Status.ERROR) {
            buf.append("Last error: " + Formatter.formatLongDateTime(lastFailed) + "<br/>");
            buf.append("Error message: " + lastException.getMessage());
//            if (lastException.getExtraMessage() != null) {
//                 buf.append(": " + lastException.getExtraMessage());
//            }
        } else {
            buf.append("Last contact: " + Formatter.formatLongDateTime(lastContact));
        }


        return buf.toString();
    }

}

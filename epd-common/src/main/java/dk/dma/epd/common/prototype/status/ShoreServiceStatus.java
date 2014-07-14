/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.common.prototype.status;

import java.util.Date;

import net.jcip.annotations.ThreadSafe;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
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

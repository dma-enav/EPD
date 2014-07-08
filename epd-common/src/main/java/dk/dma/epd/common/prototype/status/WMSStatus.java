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

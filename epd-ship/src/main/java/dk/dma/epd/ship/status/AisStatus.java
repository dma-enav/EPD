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

import dk.dma.epd.common.text.Formatter;

/**
 * AIS status
 */
public class AisStatus extends ComponentStatus {
    
    private static final long RECEPTION_INTERVAL = 30000; // 30 secs
    
    private Date lastReceived = new Date(0);
    private Date lastSent;
    private Date lastSendError;
    private Boolean sendOk;
    private Status sendStatus = Status.UNKNOWN;
    private Status receiveStatus = Status.UNKNOWN;
    
    public AisStatus() {
        super("AIS");
    }
    
    public synchronized void markAisReception() {
        lastReceived = new Date();
    }
    
    public synchronized void markSuccesfullSend() {
        lastSent = new Date();
        sendOk = true;
    }
    
    public synchronized void markFailedSend() {
        lastSendError = new Date();
        sendOk = false;
    }
    
    @Override
    public Status getStatus() {
        shortStatusText = "Reception ";
        // Set status based on times
        
        // Base firstly on reception
        long elapsed = System.currentTimeMillis() - lastReceived.getTime();
        status = elapsed > RECEPTION_INTERVAL ? Status.ERROR : Status.OK;
        shortStatusText += status.name() + " - Sending ";
        receiveStatus = status;
        
        if (sendOk != null) {
            sendStatus = sendOk.booleanValue() ? Status.OK : Status.ERROR;
        }
        shortStatusText += sendStatus.name();
        
        // Adjust overall status with sending status
        if (sendStatus == Status.ERROR) {
            if (status == Status.UNKNOWN) {
                status = Status.ERROR;
            }
            if (status == Status.OK) {
                status = Status.PARTIAL;
            }
        }
        
        return status;
    }

    @Override
    public String getStatusHtml() {
        getStatus();
        StringBuilder buf = new StringBuilder();
        buf.append("Reception: " + receiveStatus.name() + "<br/>");
        buf.append("Sending: " + sendStatus.name() + "<br/>");
        buf.append("Last reception: " + Formatter.formatLongDateTime(getLastReceived()) + "<br/>");
        if (sendStatus == Status.ERROR) {
            buf.append("Last send error: " + Formatter.formatLongDateTime(lastSendError));
        } else {
            buf.append("Last send: " + Formatter.formatLongDateTime(lastSent));
        }
        return buf.toString();
    }
    
    public Date getLastReceived() {
        return lastReceived;
    }
    
    public Date getLastSendError() {
        return lastSendError;
    }
    
    public Date getLastSent() {
        return lastSent;
    }

}

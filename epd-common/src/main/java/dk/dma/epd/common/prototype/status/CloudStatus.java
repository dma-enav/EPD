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
import dk.dma.epd.common.text.Formatter;

/**
 * AIS status
 */
@ThreadSafe
public class CloudStatus extends ComponentStatus {
    
    private static final long RECEPTION_INTERVAL = 30000; // 30 secs
    
    private Date lastReceived = new Date(0);
    private Date lastSent= new Date(0);
    private Date lastSendError;
    private Boolean sendOk;
    private Status sendStatus = Status.UNKNOWN;
    private Status receiveStatus = Status.UNKNOWN;
    
    public CloudStatus() {
        super("Maritime Cloud");
    }
    
    public synchronized Status getSendStatus() {
        return sendStatus;
    }

    public synchronized Status getReceiveStatus() {
        return receiveStatus;
    }

    
    public synchronized void markCloudReception() {
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
    
    public synchronized void markFailedReceive() {
        receiveStatus = Status.ERROR;
    }
    
    @Override
    public synchronized Status getStatus() {
        shortStatusText = "Reception ";
        // Set status based on times
        
        // Base firstly on reception
        long elapsed = System.currentTimeMillis() - lastSent.getTime();
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
    public synchronized String getStatusHtml() {
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
    
    public synchronized Date getLastReceived() {
        return lastReceived;
    }
    
    public synchronized Date getLastSendError() {
        return lastSendError;
    }
    
    public synchronized Date getLastSent() {
        return lastSent;
    }

}

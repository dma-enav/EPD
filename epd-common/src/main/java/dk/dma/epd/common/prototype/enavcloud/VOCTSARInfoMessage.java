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
package dk.dma.epd.common.prototype.enavcloud;

import java.util.Date;

import net.maritimecloud.net.broadcast.BroadcastMessage;

public class VOCTSARInfoMessage extends BroadcastMessage {

    private String message;
    private long date;
    private long sender;
    
    public VOCTSARInfoMessage() {
        super();

        date = new Date().getTime();
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the date
     */
    public long getDate() {
        return date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(long date) {
        this.date = date;
    }

    /**
     * @return the sender
     */
    public long getSender() {
        return sender;
    }

    /**
     * @param sender
     *            the sender to set
     */
    public void setSender(long sender) {
        this.sender = sender;
    }
    
    
}

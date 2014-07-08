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
package dk.dma.epd.common.prototype.msi;

import java.util.Date;

import dk.frv.enav.common.xml.msi.MsiMessage;

public class MsiMessageExtended {
    public volatile MsiMessage msiMessage;
    public volatile boolean acknowledged;
    public volatile boolean visible;
    public volatile boolean relevant;

    public MsiMessageExtended(MsiMessage msiMessage, boolean acknowledged,
            boolean visible, boolean relevant) {
        this.msiMessage = msiMessage;
        this.acknowledged = acknowledged;
        this.visible = visible;
        this.relevant = relevant;
    }

    public synchronized boolean isValidAt(Date date) {
        return msiMessage.getValidFrom() == null
                || msiMessage.getValidFrom().before(date);
    }
    
    public MsiMessage getMsiMessage() {
        return msiMessage;
    }
}

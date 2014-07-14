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

import net.jcip.annotations.ThreadSafe;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.text.Formatter;

/**
 * PNT status
 */
@ThreadSafe
public class PntStatus extends ComponentStatus {
    
    private PntData currentData;

    public PntStatus(PntData currentData) {
        super("PNT");
        this.currentData = currentData;
        if (!currentData.isBadPosition()) {
            setStatus(ComponentStatus.Status.OK);
            setShortStatusText("Position OK");
            return;
        }
        long elapsed = System.currentTimeMillis() - currentData.getLastUpdated().getTime();
        if (elapsed > 10000) {
            setStatus(ComponentStatus.Status.ERROR);
            setShortStatusText("No PNT data");
            return;
        }
        setStatus(ComponentStatus.Status.PARTIAL);
        setShortStatusText("Position unknown");
    }
    
    @Override
    public synchronized String getStatusHtml() {
        StringBuilder buf = new StringBuilder();
        buf.append("Position: " + status.name() + "<br/>");
        buf.append("Source: " + currentData.getPntSource() + "<br/>");
        buf.append("Last PNT data: " + Formatter.formatLongDateTime(currentData.getLastUpdated()));
        return buf.toString();
    }
    
    public synchronized PntData getPntData() {
        return this.currentData;
    }
}

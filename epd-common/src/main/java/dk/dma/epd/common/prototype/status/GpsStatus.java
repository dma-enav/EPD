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

import net.jcip.annotations.ThreadSafe;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.text.Formatter;

/**
 * GPS status
 */
@ThreadSafe
public class GpsStatus extends ComponentStatus {
    
    private GpsData currentData;

    public GpsStatus(GpsData currentData) {
        super("GPS");
        this.currentData = currentData;
        if (!currentData.isBadPosition()) {
            setStatus(ComponentStatus.Status.OK);
            setShortStatusText("Position OK");
            return;
        }
        long elapsed = System.currentTimeMillis() - currentData.getLastUpdated().getTime();
        if (elapsed > 10000) {
            setStatus(ComponentStatus.Status.ERROR);
            setShortStatusText("No GPS data");
            return;
        }
        setStatus(ComponentStatus.Status.PARTIAL);
        setShortStatusText("Position unknown");
    }
    
    @Override
    public synchronized String getStatusHtml() {
        StringBuilder buf = new StringBuilder();
        buf.append("Position: " + status.name() + "<br/>");
        buf.append("Last GPS data: " + Formatter.formatLongDateTime(currentData.getLastUpdated()));
        return buf.toString();
    }

}

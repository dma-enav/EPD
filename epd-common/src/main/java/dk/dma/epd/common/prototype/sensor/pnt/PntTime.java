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
package dk.dma.epd.common.prototype.sensor.pnt;

import java.util.Date;

import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.sensor.nmea.PntMessage;

/**
 * Singleton component class that maintains GNSS time as an offset from computer time
 */
@ThreadSafe
public final class PntTime extends MapHandlerChild implements IPntTimeListener {

    private static final Logger LOG = LoggerFactory.getLogger(PntTime.class);

    private volatile long offset;
    private static PntTime instance;

    private PntTime() {

    }

    /**
     * Receive GNSS time update
     */
    @Override
    public void receive(PntMessage pntMessage) {
        if (pntMessage == null || pntMessage.getTime() == null) {
            return;
        }
        offset = System.currentTimeMillis() - pntMessage.getTime();
        LOG.debug("New PNT time offset: " + offset);
    }

    /**
     * Get GNSS date
     * 
     * @return date
     */
    public synchronized Date getDate() {
        return new Date(new Date().getTime() - offset);
    }

    public static void init() {
        synchronized (PntTime.class) {
            if (instance == null) {
                instance = new PntTime();
            }
        }
    }

    public static PntTime getInstance() {
        synchronized (PntTime.class) {
            return instance;
        }
    }

}

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
package dk.dma.epd.common.prototype.sensor.pnt;

import java.util.Date;

import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.sensor.nmea.IPntSensorListener;
import dk.dma.epd.common.prototype.sensor.nmea.PntMessage;

/**
 * Singleton component class that maintains PNT time as an offset from computer time
 */
@ThreadSafe
public final class PntTime extends MapHandlerChild implements IPntSensorListener {

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
    private synchronized Date _getDate() {
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
    
    public static Date getDate() {
        return getInstance()._getDate();
    }

}

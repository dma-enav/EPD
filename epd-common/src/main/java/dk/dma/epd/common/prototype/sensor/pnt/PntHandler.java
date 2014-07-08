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
import java.util.concurrent.CopyOnWriteArrayList;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.sensor.nmea.IPntSensorListener;
import dk.dma.epd.common.prototype.sensor.nmea.PntMessage;
import dk.dma.epd.common.prototype.sensor.nmea.PntMessage.MessageType;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.prototype.status.PntStatus;
import dk.dma.epd.common.util.Util;

/**
 * Component class for handling received PNT messages.
 */
@ThreadSafe
public class PntHandler extends MapHandlerChild implements IPntSensorListener, IStatusComponent, Runnable {

    private static final long PNT_TIMEOUT = 60 * 1000; // 1 min
    private CopyOnWriteArrayList<IPntDataListener> listeners = new CopyOnWriteArrayList<>();
    @GuardedBy("this")
    private PntData currentData = new PntData();

    public PntHandler() {
        EPD.startThread(this, "PntHandler");
    }

    @Override
    public PntStatus getStatus() {
        return new PntStatus(getCurrentData());
    }

    /**
     * Receive PNT message
     */
    @Override
    public synchronized void receive(PntMessage pntMessage) {
        // The PntHandler is not interested in time-only PNT messages
        if (pntMessage.getMessageType() == MessageType.TIME) {
            return;
        }
        
        Date now = new Date();
        long elapsed = now.getTime() - currentData.getLastUpdated().getTime();
        if (elapsed < 900) {
            return;
        }

        currentData.setPntSource(pntMessage.getPntSource());
        currentData.setLastUpdated(now);
        if (pntMessage.getPos() == null || !pntMessage.isValidPosition()) {
            currentData.setBadPosition(true);
        } else {
            currentData.setPosition(pntMessage.getPos());
            currentData.setBadPosition(false);
        }
        if (pntMessage.getCog() != null) {
            currentData.setCog(pntMessage.getCog());
        }
        if (pntMessage.getSog() != null) {
            currentData.setSog(pntMessage.getSog());
        }
        distributeUpdate();
    }

    /**
     * Mark the current data as invalid
     */
    private synchronized void markBadPos() {
        currentData.setBadPosition(true);
    }

    /**
     * Distribute update to all listeners
     */
    private void distributeUpdate() {
        for (IPntDataListener listener : listeners) {
            listener.pntDataUpdate(getCurrentData());
        }
    }

    /**
     * Return if the current data has timed out
     */
    public synchronized boolean pntTimedOut() {
        Date now = new Date();
        return now.getTime() - currentData.getLastUpdated().getTime() > PNT_TIMEOUT;
    }

    /**
     * Routine for monitoring timeout
     */
    @Override
    public void run() {
        while (true) {
            if (pntTimedOut()) {
                markBadPos();
                distributeUpdate();
            }
            Util.sleep(10000);
        }
    }

    public synchronized PntData getCurrentData() {
        return new PntData(currentData);
    }

    public void addListener(IPntDataListener listener) {
        listeners.addIfAbsent(listener);
    }

    public void removeListener(IPntDataListener listener) {
        listeners.remove(listener);
    }

}

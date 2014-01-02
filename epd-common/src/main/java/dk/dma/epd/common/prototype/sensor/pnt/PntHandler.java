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
import java.util.concurrent.CopyOnWriteArrayList;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.sensor.nmea.IPntListener;
import dk.dma.epd.common.prototype.sensor.nmea.PntMessage;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.prototype.status.PntStatus;
import dk.dma.epd.common.util.Util;

/**
 * Component class for handling received PNT messages.
 */
@ThreadSafe
public class PntHandler extends MapHandlerChild implements IPntListener, IStatusComponent, Runnable {

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
        Date now = new Date();
        long elapsed = now.getTime() - currentData.getLastUpdated().getTime();
        if (elapsed < 900) {
            return;
        }

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

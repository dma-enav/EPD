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
package dk.dma.epd.common.prototype.sensor.gps;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.sensor.nmea.GpsMessage;
import dk.dma.epd.common.prototype.sensor.nmea.IGpsListener;
import dk.dma.epd.common.prototype.status.GpsStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.util.Util;

/**
 * Component class for handling received GPS messages.
 */
@ThreadSafe
public class GpsHandler extends MapHandlerChild implements IGpsListener, IStatusComponent, Runnable {

    private static final long GPS_TIMEOUT = 60 * 1000; // 1 min
    private CopyOnWriteArrayList<IGpsDataListener> listeners = new CopyOnWriteArrayList<>();
    @GuardedBy("this")
    private GpsData currentData = new GpsData();

    public GpsHandler() {
        EPD.startThread(this, "GpsHandler");
    }

    @Override
    public GpsStatus getStatus() {
        return new GpsStatus(getCurrentData());
    }

    /**
     * Receive GPS message
     */
    @Override
    public synchronized void receive(GpsMessage gpsMessage) {
        Date now = new Date();
        long elapsed = now.getTime() - currentData.getLastUpdated().getTime();
        if (elapsed < 900) {
            return;
        }

        currentData.setLastUpdated(now);
        if (gpsMessage.getPos() == null || !gpsMessage.isValidPosition()) {
            currentData.setBadPosition(true);
        } else {
            currentData.setPosition(gpsMessage.getPos());
            currentData.setBadPosition(false);
        }
        if (gpsMessage.getCog() != null) {
            currentData.setCog(gpsMessage.getCog());
        }
        if (gpsMessage.getSog() != null) {
            currentData.setSog(gpsMessage.getSog());
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
        for (IGpsDataListener listener : listeners) {
            GpsData currentCopy = getCurrentData();
            listener.gpsDataUpdate(currentCopy);
        }
    }

    /**
     * Return if the current data has timed out
     */
    public synchronized boolean gpsTimedOut() {
        Date now = new Date();
        return now.getTime() - currentData.getLastUpdated().getTime() > GPS_TIMEOUT;
    }

    /**
     * Routine for monitoring timeout
     */
    @Override
    public void run() {
        while (true) {
            if (gpsTimedOut()) {
                markBadPos();
                distributeUpdate();
            }
            Util.sleep(10000);
        }
    }

    public synchronized GpsData getCurrentData() {
        return new GpsData(currentData);
    }

    public void addListener(IGpsDataListener listener) {
        listeners.addIfAbsent(listener);
    }

    public void removeListener(IGpsDataListener listener) {
        listeners.remove(listener);
    }

}

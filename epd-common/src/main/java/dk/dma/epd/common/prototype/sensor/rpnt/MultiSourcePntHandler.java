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
package dk.dma.epd.common.prototype.sensor.rpnt;

import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.sensor.nmea.IPntSensorListener;
import dk.dma.epd.common.prototype.sensor.nmea.IResilientPntSensorListener;
import dk.dma.epd.common.prototype.sensor.nmea.PntMessage;
import dk.dma.epd.common.prototype.sensor.nmea.PntSource;
import dk.dma.epd.common.util.Util;

/**
 * Component to handle multi-source PNT messages
 */
@ThreadSafe
public class MultiSourcePntHandler extends MapHandlerChild implements IResilientPntSensorListener, Runnable {
    
    private static final Logger LOG = LoggerFactory.getLogger(MultiSourcePntHandler.class);
    
    @GuardedBy("this")
    private ResilientPntData rpntData;
    
    @GuardedBy("this")
    private PntMessage pntMessage;

    @GuardedBy("this")
    private final CopyOnWriteArrayList<IPntSensorListener> pntListeners = new CopyOnWriteArrayList<>();

    @GuardedBy("this")
    private final CopyOnWriteArrayList<IResilientPntDataListener> rpntListeners = new CopyOnWriteArrayList<>();

    /**
     * Constructor
     * @param pntHandler
     */
    public MultiSourcePntHandler() {
        EPD.startThread(this, "MultiSourcePntHandler");        
    }
    
    /**
     * Called upon receiving a new {@code ResilientPntData} update
     * @param rpntData the new {@code ResilientPntData} data
     */
    @Override
    public synchronized void receive(ResilientPntData rpntData) {
        // Log significant changes
        if (this.rpntData != null && this.rpntData.getPntSource() != rpntData.getPntSource()) {
            LOG.warn("******** Changed PNT source from " + this.rpntData.getPntSource() + " to " + rpntData.getPntSource());
        }
        if (this.rpntData != null && this.rpntData.getJammingFlag() != rpntData.getJammingFlag()) {
            LOG.warn("******** Changed GPS jamming state from " + this.rpntData.getJammingFlag() + " to " + rpntData.getJammingFlag());
        }
        this.rpntData = rpntData;
        
        // Publish the update to all listeners
        publishResilientPntDataUpdated();
    }
    
    /**
     * Receive PNT message.<p>
     * Before publishing it to listeners, it is
     * verified that it has the proper PNT source as specified by the
     * current RPNT data.
     * @param pntMessage the new {@code PntMessage}
     */
    @Override
    public synchronized void receive(PntMessage pntMessage) {
        if (usePnt() && pntMessage != null && pntMessage.getPntSource() == rpntData.getPntSource()) {
            this.pntMessage = pntMessage;
            checkPublishPntMessage();
        }
    }
    
    /**
     * Returns if PNT should be used or not
     * @return if PNT should be used or not
     */
    public synchronized boolean usePnt() {
        return rpntData != null && rpntData.getPntSource() != PntSource.NONE;
    }

    /**
     * Returns the current resilient PNT data
     * @return the current resilient PNT data
     */
    public synchronized ResilientPntData getRpntData() {
        return rpntData;
    }

    /**
     * Returns the current PNT message
     * and only if the last received PNT message is from the
     * same PNT source as specified by the RPNT data.
     * 
     * @return the current PNT message
     */
    public synchronized PntMessage getPntMessage() {
        return usePnt() && pntMessage != null && pntMessage.getPntSource() == rpntData.getPntSource() 
                ? pntMessage
                : null;
    }

    /**
     * Publishes the current PNT message to all listeners.
     * if the message is valid, i.e. if it matches the 
     * requirements of the current RPNT data.
     */
    private void checkPublishPntMessage() {
        // Check if the current PntMessage is valid (otherwise null)
        PntMessage msg = getPntMessage();
        if (msg != null) {
            for (IPntSensorListener pntListener : pntListeners) {
                pntListener.receive(msg);
            }
        }
    }
    
    /**
     * Publishes the current {@code ResilientPntData}  to all listeners.
     */
    private void publishResilientPntDataUpdated() {
        for (IResilientPntDataListener rpntListener : rpntListeners) {
            rpntListener.rpntDataUpdate(rpntData);
         }
    }
    
    /**
     * Adds a new {@code IPntSensorListener} listener if absent
     * @param pntListener the listener to add
     */
    public synchronized void addPntListener(IPntSensorListener pntListener) {
        pntListeners.addIfAbsent(pntListener);
    }
    
    /**
     * Removes a {@code IPntSensorListener} listener if present
     * @param pntListener the listener to remove
     */
    public synchronized void removePntListener(IPntSensorListener pntListener) {
        pntListeners.remove(pntListener);
    }
    
    /**
     * Adds a new {@code IResilientPntDataListener} listener if absent
     * @param rpntListener the listener to add
     */
    public synchronized void addResilientPntDataListener(IResilientPntDataListener rpntListener) {
        rpntListeners.addIfAbsent(rpntListener);
    }
    
    /**
     * Removes a {@code IResilientPntDataListener} listener if present
     * @param rpntListener the listener to remove
     */
    public synchronized void removeResilientPntListener(IResilientPntDataListener rpntListener) {
        rpntListeners.remove(rpntListener);
    }
    
    @Override
    public void run() {
        while (true) {
            // TODO: Validate if data is still fresh
            Util.sleep(10000);
        }
    }
}

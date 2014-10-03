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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.layers.msi.MsiNmLayerCommon;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.settings.EnavSettings;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Util;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.common.xml.msi.response.MsiResponse;

/**
 * Component for handling MSI messages
 */
@ThreadSafe
public class MsiHandler extends MapHandlerChild implements Runnable,
        IRoutesUpdateListener, IPntDataListener {

    private static final Logger LOG = LoggerFactory.getLogger(MsiHandler.class);

    private ShoreServicesCommon shoreServices;
    private RouteManagerCommon routeManager;
    
    private MsiNmLayerCommon msiLayer;

    private MsiStore msiStore;
    private Date lastUpdate;
    private long pollInterval;
    private final EnavSettings enavSettings;
    private boolean pendingImportantMessages;
    // do not serialize these members
    private transient Position calculationPosition;
    private transient Position currentPosition;

    private CopyOnWriteArrayList<IMsiUpdateListener> listeners = new CopyOnWriteArrayList<>();
    private PntHandler pntHandler;
    private boolean pntUpdate;

    public MsiHandler(EnavSettings enavSettings) {
        this.enavSettings = enavSettings;
        pollInterval = enavSettings.getMsiPollInterval();
        msiStore = MsiStore.loadFromFile(EPD.getInstance().getHomePath(), enavSettings);
        EPD.startThread(this, "MsiHandler");
    }

    public synchronized Collection<MsiMessage> getMessages() {
        return msiStore.getMessages().values();
    }

    /**
     * Get the list of filtered messages
     *
     * @return
     */
    public synchronized List<MsiMessageExtended> getFilteredMessageList() {
        List<MsiMessageExtended> list = new ArrayList<>();
        for (Integer msgId : msiStore.getMessages().keySet()) {
            MsiMessage msiMessage = msiStore.getMessages().get(msgId);
            boolean acknowledged = msiStore.getAcknowledged().contains(msgId);
            boolean visible = msiStore.getVisible().contains(msgId);
            boolean relevant = msiStore.getRelevant().contains(msgId);
            MsiMessageExtended msiMessageExtended = new MsiMessageExtended(
                    msiMessage, acknowledged, visible, relevant);
            if (visible) {
                list.add(msiMessageExtended);
            }
        }
        
        return list;
    }
    
    /**
     * Get the list of MSI messages
     *
     * @return
     */
    public synchronized List<MsiMessageExtended> getMessageList() {
        List<MsiMessageExtended> list = new ArrayList<>();
        for (Integer msgId : msiStore.getMessages().keySet()) {
            MsiMessage msiMessage = msiStore.getMessages().get(msgId);
            boolean acknowledged = msiStore.getAcknowledged().contains(msgId);
            boolean visible = msiStore.getVisible().contains(msgId);
            boolean relevant = msiStore.getRelevant().contains(msgId);
            MsiMessageExtended msiMessageExtended = new MsiMessageExtended(
                    msiMessage, acknowledged, visible, relevant);
            list.add(msiMessageExtended);
        }
        return list;
    }

    /**
     * Set a msi message as acknowledged
     * @param msiMessage
     */
    public void setAcknowledged(MsiMessage msiMessage) {
        synchronized(this) {
            msiStore.getAcknowledged().add(msiMessage.getMessageId());
            saveToFile();
            reCalcMsiStatus();            
        }
        notifyUpdate();
    }

    /**
     * Check if a msi with a given ID is acknowledged
     *
     * @param msgId
     * @return
     */
    public synchronized boolean isAcknowledged(int msgId) {
        return msiStore.getAcknowledged().contains(msgId);
    }    
    /**
     * Delete a message from the msi
     *
     * @param msiMessage
     */
    public void deleteMessage(MsiMessage msiMessage) {
        synchronized(this) {
            msiStore.deleteMessage(msiMessage);
            saveToFile();
            reCalcMsiStatus();            
        }
        notifyUpdate();
    }

    @Override
    public void run() {
        while (true) {
            Util.sleep(30000);
            updateMsi();
        }
    }

    /**
     * Update the msi
     */
    public void updateMsi() {
        boolean msiUpdated = false;

        Date now = new Date();
        if (getLastUpdate() == null
                || now.getTime() - getLastUpdate().getTime() > pollInterval * 1000) {
            // Poll for new messages from shore
            try {
                if (poll()) {
                    msiUpdated = true;
                }
                setLastUpdate(now);
            } catch (ShoreServiceException e) {
                LOG.error("Failed to get MSI from shore: " + e.getMessage());
            }
        }

        // Cleanup msi store
        if (msiStore.cleanup()) {
            msiUpdated = true;
        }

        // Check if new pending messages
        if (reCalcMsiStatus()) {
            LOG.debug("reCalcMsiStatus() changed MSI status");
            msiUpdated = true;
        }

        if (reCalcMsiVisibility()) {
            LOG.debug("reCalcMsiRelevance() changed MSI relevance");
            msiUpdated = true;
        }

        // Notify if update
        if (msiUpdated) {
            notifyUpdate();
        }
    }

    /** 
     * Pushes msi updates to all listeners.
     */
    public void notifyUpdate() {
        // Update layer
        if (msiLayer != null) {
            //doUpdate() will ask msiNmHandler
            //getMessageList() which is currently guarded by a lock
            msiLayer.doUpdate(); 
        }
        // Notify of MSI change, MUST NOT lock.
        for (IMsiUpdateListener listener : listeners) {
            listener.msiUpdate();
        }
    }

    /**
     * Returns true if status has changed
     * 
     * @return
     */
    private synchronized boolean reCalcMsiStatus() {
        // Determine if there are pending relevant MSI
        boolean previous = pendingImportantMessages;

        // pendingImportantMessages = msiStore.hasValidUnacknowledged();
        pendingImportantMessages = msiStore.hasValidVisibleUnacknowledged();
        return previous != pendingImportantMessages;

        // TODO check against current position and active route
        // Is the messages in the vicinity of position or route
        // Only check with unacknowledged messages
    }

    /**
     * Recalculate if a msi is visible
     * @return
     */
    private synchronized boolean reCalcMsiVisibility() {
        
        boolean updated = false;

        if (pntUpdate) {
            pntUpdate = false;
            if (calculationPosition != null) {
                msiStore.setVisibility(calculationPosition);
            }
            updated = true;
        }
        msiStore.setVisibility(routeManager.getRoutes());
        updated = true;
        return updated;
    }

    public boolean poll() throws ShoreServiceException {
        if (shoreServices == null) {
            return false;
        }
        MsiResponse msiResponse = shoreServices.msiPoll(msiStore
                .getLastMessage());
        if (msiResponse == null || msiResponse.getMessages() == null
                || msiResponse.getMessages().size() == 0) {
            return false;
        }
        LOG.info("Received " + msiResponse.getMessages().size()
                + " new MSI messages");
        msiStore.update(msiResponse.getMessages(), calculationPosition,
                routeManager.getRoutes());
        return true;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Set last msi update
     * @param lastUpdate
     */
    private synchronized void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Add a listener to the msihandler
     *
     * @param listener
     */
    @GuardedBy("listeners")
    public void addListener(IMsiUpdateListener listener) {
        listeners.addIfAbsent(listener);
    }

    /**
     * Save the msi to a file
     */
    public synchronized void saveToFile() {
        msiStore.saveToFile();
    }

    @Override
    public void routesChanged(RoutesUpdateEvent e) {
        if (e == RoutesUpdateEvent.ROUTE_ACTIVATED) {
            //these two are commented out in the original non-common implementation
            msiStore.setRelevance(routeManager.getActiveRoute());
            notifyUpdate();
        }
        if (e == RoutesUpdateEvent.ROUTE_DEACTIVATED) {
            msiStore.clearRelevance();
            notifyUpdate();
        }
        if (e == RoutesUpdateEvent.ROUTE_MSI_UPDATE
                || e == RoutesUpdateEvent.ROUTE_ADDED
                || e == RoutesUpdateEvent.ROUTE_REMOVED
                || e == RoutesUpdateEvent.ROUTE_CHANGED) {
            updateMsi();
        }
        if (reCalcMsiStatus()) {
            notifyUpdate();
        }
    }
    

    /**
     * Only set a new calculation position if it is a certain range away from
     * previous point
     */
    @Override
    public void pntDataUpdate(PntData pntData) {
        currentPosition = pntData.getPosition();

        if (calculationPosition == null) {
            calculationPosition = currentPosition;
            pntUpdate = true;
            return;
        }
        Double range = Calculator.range(currentPosition, calculationPosition,
                Heading.GC);
        if (range > enavSettings.getMsiRelevanceGpsUpdateRange()) {
            pntUpdate = true;
            calculationPosition = currentPosition;
        }
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof ShoreServicesCommon) {
            shoreServices = (ShoreServicesCommon) obj;
        }
        if (obj instanceof RouteManagerCommon) {
            routeManager = (RouteManagerCommon) obj;
            routeManager.addListener(this);
        }
        if (obj instanceof MsiNmLayerCommon) {
            msiLayer = (MsiNmLayerCommon) obj;
        }
        if (obj instanceof IMsiUpdateListener) {
            addListener((IMsiUpdateListener) obj);
        }
        if (pntHandler == null && obj instanceof PntHandler) {
            pntHandler = (PntHandler) obj;
            pntHandler.addListener(this);
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (pntHandler == obj) {
            pntHandler.removeListener(this);
            pntHandler = null;
        }
        if (routeManager == obj) {
            routeManager.removeListener(this);
            routeManager  = null;
        }
    }


}

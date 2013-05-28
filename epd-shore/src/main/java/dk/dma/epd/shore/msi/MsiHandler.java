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
package dk.dma.epd.shore.msi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.msi.IMsiUpdateListener;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.dma.epd.common.prototype.msi.MsiStore;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.prototype.sensor.gps.IGpsDataListener;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.layers.msi.MsiLayer;
import dk.dma.epd.shore.route.RouteManager;
import dk.dma.epd.shore.services.shore.ShoreServices;
import dk.dma.epd.shore.settings.ESDEnavSettings;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.common.xml.msi.response.MsiResponse;


/**
 * Component for handling MSI messages
 */
public class MsiHandler extends MapHandlerChild implements Runnable, IRoutesUpdateListener, IGpsDataListener {


    private static final Logger LOG = LoggerFactory.getLogger(MsiHandler.class);

//    private MsiLayer msiLayer;

    private ShoreServices shoreServices;
    private RouteManager routeManager;
    // private MsiLayer msiLayer;

    private MsiStore msiStore;
    private Date lastUpdate;
    private long pollInterval;
    private boolean pendingImportantMessages;
    // do not serialize these members
    private transient Position calculationPosition;
    // transient private GeoLocation currentPosition = null;

    private Set<IMsiUpdateListener> listeners = new HashSet<IMsiUpdateListener>();
    // private GpsHandler gpsHandler;
    private boolean gpsUpdate;
    
    

    /**
     * Constructor
     */
    public MsiHandler(ESDEnavSettings enavSettings) {
        pollInterval = enavSettings.getMsiPollInterval();
        msiStore = MsiStore.loadFromFile(EPDShore.getHomePath(), EPDShore.getSettings().getEnavSettings());
        EPDShore.startThread(this, "MsiHandler");
    }

    /**
     * Add a listener to the msihandler
     *
     * @param listener
     */
    public synchronized void addListener(IMsiUpdateListener listener) {
        listeners.add(listener);
    }

    /**
     * Delete a message from the msi
     *
     * @param msiMessage
     */
    public synchronized void deleteMessage(MsiMessage msiMessage) {
        msiStore.deleteMessage(msiMessage);
        saveToFile();
        reCalcMsiStatus();
        notifyUpdate();
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof ShoreServices) {
            shoreServices = (ShoreServices) obj;
        }
        if (obj instanceof RouteManager) {
            routeManager = (RouteManager) obj;
            routeManager.addListener(this);
        }
//        if (obj instanceof MsiLayer) {
//            msiLayer = (MsiLayer) obj;
//        }
        if (obj instanceof IMsiUpdateListener) {
            addListener((IMsiUpdateListener) obj);
        }
        // if (gpsHandler == null && obj instanceof GpsHandler) {
        // gpsHandler = (GpsHandler) obj;
        // gpsHandler.addListener(this);
        // }
    }

    @Override
    public void findAndUndo(Object obj) {
        // if (gpsHandler == obj) {
        // gpsHandler.removeListener(this);
        // gpsHandler = null;
        // }
    }

    /**
     * Get the list of filtered messages
     *
     * @return
     */
    public synchronized List<MsiMessageExtended> getFilteredMessageList() {
        List<MsiMessageExtended> list = new ArrayList<MsiMessageExtended>();
        for (Integer msgId : msiStore.getMessages().keySet()) {
            MsiMessage msiMessage = msiStore.getMessages().get(msgId);
            boolean acknowledged = msiStore.getAcknowledged().contains(msgId);
            boolean visible = msiStore.getVisible().contains(msgId);
            boolean relevant = msiStore.getRelevant().contains(msgId);
            MsiMessageExtended msiMessageExtended = new MsiMessageExtended(msiMessage, acknowledged, visible, relevant);
            if (visible) {
                list.add(msiMessageExtended);
            }
        }
        return list;
    }

    /**
     * Get the first none acknolwedged msi message
     *
     * @return
     */
    public synchronized int getFirstNonAcknowledged() {
        int index = 0;
        List<MsiMessageExtended> list = getMessageList();
        while (index < list.size()) {
            if (!list.get(index).acknowledged) {
                return index;
            }
            index++;
        }
        return list.size() - 1;
    }

    /**
     * Get the first none acknolwedged msi message from the filtered list
     *
     * @return
     */
    public synchronized int getFirstNonAcknowledgedFiltered() {
        int index = 0;
        List<MsiMessageExtended> list = getFilteredMessageList();
        while (index < list.size()) {
            if (!list.get(index).acknowledged) {
                return index;
            }
            index++;
        }
        return list.size() - 1;
    }

    public synchronized Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Get the list of MSI messages
     *
     * @return
     */
    public synchronized List<MsiMessageExtended> getMessageList() {
        List<MsiMessageExtended> list = new ArrayList<MsiMessageExtended>();
        for (Integer msgId : msiStore.getMessages().keySet()) {
            MsiMessage msiMessage = msiStore.getMessages().get(msgId);
            boolean acknowledged = msiStore.getAcknowledged().contains(msgId);
            boolean visible = msiStore.getVisible().contains(msgId);
            boolean relevant = msiStore.getRelevant().contains(msgId);
            MsiMessageExtended msiMessageExtended = new MsiMessageExtended(msiMessage, acknowledged, visible, relevant);
            list.add(msiMessageExtended);
        }
        return list;
    }

    /**
     * Get all the msi messages
     *
     * @return
     */
    public synchronized Collection<MsiMessage> getMessages() {
        return msiStore.getMessages().values();
    }

    /**
     * Get the amount of unacknowledged msi messages
     *
     * @return
     */
    public int getUnAcknowledgedMSI() {
        List<MsiMessageExtended> messageList = getMessageList();
        int counter = 0;

        for (int i = 0; i < messageList.size(); i++) {
            if (!messageList.get(i).acknowledged) {
                counter++;
            }
        }

        return counter;
    }

    @Override
    public void gpsDataUpdate(GpsData arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * Check if a msi with a given ID is acknowleged
     *
     * @param msgId
     * @return
     */
    public synchronized boolean isAcknowledged(int msgId) {
        return msiStore.getAcknowledged().contains(msgId);
    }

    /**
     * Get the pending important messages
     *
     * @return
     */
    public synchronized boolean isPendingImportantMessages() {
        return pendingImportantMessages;
    }

    /**
     * Notify listeners and layers using this handler of a change
     */
    public void notifyUpdate() {
        // Update layer
//        if (msiLayer != null) {
//            System.out.println("Getting msiLayer and updating");
//            msiLayer.doUpdate();
//        }else{
//            System.out.println("Msilayer is null");
//        }
        
        
        // Notify of MSI change
        for (IMsiUpdateListener listener : listeners) {
            listener.msiUpdate();
        }
    }

    /**
     * Get new msi messages from server and call update
     * @return
     * @throws ShoreServiceException
     */
    public boolean poll() throws ShoreServiceException {
//        System.out.println("Polling");
        if (shoreServices == null) {
            LOG.error("shoreServices not bound");
            return false;
        }
        MsiResponse msiResponse = shoreServices.msiPoll(msiStore.getLastMessage());
        if (msiResponse == null || msiResponse.getMessages() == null || msiResponse.getMessages().size() == 0) {
//            System.out.println("Something went wrong in getting a new mmsi");
            return false;
        }
//        System.out.println("We got one");
        LOG.info("Received " + msiResponse.getMessages().size() + " new MSI messages");
        msiStore.update(msiResponse.getMessages(), calculationPosition, routeManager.getRoutes());
        return true;
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
        if (gpsUpdate) {
            gpsUpdate = false;
            msiStore.setVisibility(calculationPosition);
            updated = true;
        }
        msiStore.setVisibility(routeManager.getRoutes());
        updated = true;
        return updated;
    }

    @Override
    public void routesChanged(RoutesUpdateEvent e) {
        if (e == RoutesUpdateEvent.ROUTE_ACTIVATED) {
            // msiStore.setRelevance(routeManager.getActiveRoute());
            // notifyUpdate();
        }
        if (e == RoutesUpdateEvent.ROUTE_DEACTIVATED) {
            msiStore.clearRelevance();
            notifyUpdate();
        }
        if (e == RoutesUpdateEvent.ROUTE_MSI_UPDATE || e == RoutesUpdateEvent.ROUTE_ADDED
                || e == RoutesUpdateEvent.ROUTE_REMOVED || e == RoutesUpdateEvent.ROUTE_CHANGED) {
            updateMsi();
        }
        if (reCalcMsiStatus()) {
            notifyUpdate();
        }
    }

    @Override
    public void run() {
        while (true) {
            EPDShore.sleep(10000);
//            System.out.println("Checking for new msi");
            updateMsi();
        }
    }

    /**
     * Save the msi to a file
     */
    public synchronized void saveToFile() {
        msiStore.saveToFile();
    }

    // /**
    // * Only set a new calculation position if it is a certain range away from
    // previous point
    // */
    // @Override
    // public void gpsDataUpdate(GpsData gpsData) {
    // currentPosition = gpsData.getPosition();
    // if(calculationPosition == null) {
    // calculationPosition = currentPosition;
    // gpsUpdate = true;
    // return;
    // }
    // Double range = Calculator.range(currentPosition, calculationPosition,
    // Heading.GC);
    // if(range >
    // EeINS.getSettings().getEnavSettings().getMsiRelevanceGpsUpdateRange()) {
    // gpsUpdate = true;
    // calculationPosition = currentPosition;
    // }
    // }

    /**
     * Set a msi message as acknowleged
     * @param msiMessage
     */
    public synchronized void setAcknowledged(MsiMessage msiMessage) {
        msiStore.getAcknowledged().add(msiMessage.getMessageId());
        saveToFile();
        reCalcMsiStatus();
        notifyUpdate();
    }

    /**
     * Set last msi update
     * @param lastUpdate
     */
    private synchronized void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Update the msi
     */
    public void updateMsi() {
        boolean msiUpdated = false;

        Date now = new Date();
        if (getLastUpdate() == null || now.getTime() - getLastUpdate().getTime() > pollInterval * 1000) {
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

}

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
package dk.dma.epd.common.prototype.service;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.notification.MsiNmNotification;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.settings.EnavSettings;
import dk.dma.epd.common.util.Calculator;
import dma.msinm.MCMessage;
import dma.msinm.MCMsiNmService;
import dma.msinm.MCSearchResult;
import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.mms.MmsClient;
import net.maritimecloud.util.Timestamp;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of a Maritime Cloud MSI-NM service
 */
public class MsiNmServiceHandlerCommon extends EnavServiceHandlerCommon implements IRoutesUpdateListener, IPntDataListener {

    private static final Logger LOG = LoggerFactory.getLogger(MsiNmServiceHandlerCommon.class);

    protected List<IMsiNmServiceListener> listeners = new CopyOnWriteArrayList<>();
    private RouteManagerCommon routeManager;
    private PntHandler pntHandler;
    private MsiNmStore msiNmStore;
    private EnavSettings enavSettings;

    private List<MCMsiNmService> msiNmServiceList = new ArrayList<>();
    private List<MsiNmNotification> msiNmMessages = new ArrayList<>();
    private MaritimeId msiNmServiceId;
    private Position currentPosition;

    /**
     * Constructor
     */
    public MsiNmServiceHandlerCommon() {
        super();

        enavSettings = EPD.getInstance().getSettings().getEnavSettings();

        // Compute the currently selected MSI-NM service
        msiNmServiceId = StringUtils.isBlank(enavSettings.getMsiNmServiceId())
                ? null
                : MaritimeCloudUtils.toMaritimeId(enavSettings.getMsiNmServiceId());

        // Instantiate the MSI-NM store, and fetch the saved message list
        msiNmStore = MsiNmStore.loadFromFile(EPD.getInstance().getHomePath());
        addListener(msiNmStore);
        msiNmMessages = msiNmStore.getMsiNmMessages();


        // Schedule a refresh of the chat services approximately every minute
        scheduleWithFixedDelayWhenConnected(new Runnable() {
            @Override public void run() {
                fetchMsiNmServices();
            }
        }, 5, 64, TimeUnit.SECONDS);


        // Schedule a refresh of the active MSI-NM messages approximately every minute
        scheduleWithFixedDelayWhenConnected(new Runnable() {
            @Override public void run() {
                fetchPublishedMsiNmMessages();
            }
        }, 20, enavSettings.getMsiPollInterval(), TimeUnit.SECONDS);

        // Schedule re-computation of message filter
        getScheduler().scheduleWithFixedDelay(new Runnable() {
                    @Override public void run() {
                        recomputeMsiNmMessageFilter();
                    }
                }, 17, 30, TimeUnit.SECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(final MmsClient connection) {
        // Refresh the service list
        fetchMsiNmServices();
    }

    /**
     * Refreshes the list of MSI-NM services
     */
    private void fetchMsiNmServices() {
        try {
            // First record the current services
            Set<MaritimeId> ids = new HashSet<>();
            for (MCMsiNmService service : msiNmServiceList) {
                ids.add(service.getCaller());
            }

            // Fetch the serivice list
            List<MCMsiNmService> services = getMmsClient().endpointFind(MCMsiNmService.class).findAll().get();

            // Look for changes
            boolean identical = msiNmServiceList.size() == services.size();
            for (MCMsiNmService service : services) {
                identical &= ids.remove(service.getCaller());
            }
            identical &= ids.size() == 0;

            if (!identical) {
                msiNmServiceList = services;
                fireMsiNmServicesChanged();

                // If no selected MSI-NM service is defined, select the first in the list
                if (msiNmServiceId == null && msiNmServiceList.size() > 0) {
                    setSelectedMsiNmServiceId(msiNmServiceList.get(0).getCaller());
                }
            }


        } catch (Exception e) {
            LOG.error("Failed looking up MSI-NM services", e.getMessage());
        }
    }

    /**
     * Returns the currently selected MSI-NM service ID or null if undefined
     * @return the currently selected MSI-NM service ID or null if undefined
     */
    public MaritimeId getSelectedMsiNmServiceId() {
        return msiNmServiceId;
    }

    /**
     * Sets the currently selected MSI-NM service ID
     * @param msiNmServiceId currently selected MSI-NM service ID
     */
    public void setSelectedMsiNmServiceId(MaritimeId msiNmServiceId) {
        this.msiNmServiceId = msiNmServiceId;

        // Update the settings
        String id = (msiNmServiceId == null)
            ? ""
            : String.valueOf(MaritimeCloudUtils.toMmsi(msiNmServiceId));
        EPD.getInstance().getSettings().getEnavSettings().setMsiNmServiceId(id);

        fireMsiNmServicesChanged();
    }

    /**
     * Returns the currently selected MSI-NM service or null if un-connected
     * @return the currently selected MSI-NM service or null if un-connected
     */
    public MCMsiNmService getSelectedMsiNmService() {
        for (MCMsiNmService service : msiNmServiceList) {
            if (service.getCaller().equals(msiNmServiceId)) {
                return service;
            }
        }
        return null;
    }


    /**
     * Fetches the list of active MSI-NM messages from the current MSI-NM service
     */
    private void fetchPublishedMsiNmMessages() {
        try {
            MCMsiNmService msiNmService = getSelectedMsiNmService();
            if (msiNmService != null) {

                // Compute the last update time
                Timestamp lastUpdate = null;
                for (MsiNmNotification msg : msiNmMessages) {
                    if (lastUpdate == null || msg.get().getUpdated().getTime() > lastUpdate.getTime()) {
                        lastUpdate = msg.get().getUpdated();
                    }
                }

                // Fetch the list of active messages
                MCSearchResult result = msiNmService.activeMessagesIfUpdates("en", lastUpdate).join();

                if (StringUtils.isNotBlank(result.getError())) {
                    LOG.error("Error fetching active MSI-NM messages: " + result.getError());
                }

                if (!result.getUnchanged()) {
                    updateMsiNmMessages(result.getMessages());
                }

            } else if (msiNmMessages.size() > 0) {
                updateMsiNmMessages(new ArrayList<MCMessage>());
            }

        } catch (Exception e) {
            LOG.error("Failed looking up published MSI-NM messages", e);
        }
    }

    /**
     * Updates the locally held list of MSI-NM messages
     * @param messages the messages
     */
    private synchronized void updateMsiNmMessages(List<MCMessage> messages) {

        Map<Integer, MsiNmNotification> idLookup = new HashMap<>();
        for (MsiNmNotification msg : msiNmMessages) {
            idLookup.put(msg.getId(), msg);
        }

        // Re-use existing messages to preserve acknowledged, read, filtered flags
        List<MsiNmNotification> result = new ArrayList<>();
        for (MCMessage msg : messages) {
            MsiNmNotification existingMsg = idLookup.get(msg.getId());
            if (existingMsg != null && existingMsg.get().getUpdated().equals(msg.getUpdated())) {
                result.add(existingMsg);
                continue;
            }
            result.add(new MsiNmNotification(msg));
        }

        msiNmMessages = result;
        LOG.info("Loaded " + msiNmMessages.size() + " active MSI-NM messages");
        fireMsiNmMessagesChanged();
    }

    /**
     * Save the MSI-NM to a file
     */
    public synchronized void saveToFile() {
        msiNmStore.saveToFile();
    }

    /**
     * Returns the list of MSI-NM services
     * @return the list of MSI-NM services
     */
    public List<MCMsiNmService> getMsiNmServiceList() {
        return msiNmServiceList;
    }

    /**
     * Returns the list of MSI-NM messages
     * @return the list of MSI-NM messages
     */
    public List<MsiNmNotification> getMsiNmMessages() {
        return msiNmMessages;
    }

    /**
     * Re-computes the filtered state of the MSI-NM messages
     */
    private synchronized void recomputeMsiNmMessageFilter() {
        long t0 = System.currentTimeMillis();
        boolean updated = false;

        // Check if the MSI filter is on or not
        if (!enavSettings.isMsiFilter()) {
            // All MSI-NM are included in the filter
            for (MsiNmNotification msg : msiNmMessages) {
                updated |= !msg.isFiltered();
                msg.setFiltered(true);
            }

        } else {
            // MSI-NM filtering is on

            for (MsiNmNotification msg : msiNmMessages) {

                boolean wasFiltered = msg.isFiltered();
                msg.setFiltered(false);

                // Messages without location always included
                if (msg.getLocation() == null) {
                    msg.setFiltered(true);
                }

                // 1) Check proximity to ship
                if (!msg.isFiltered() && currentPosition != null) {
                    Double dist = msg.getDistanceToPosition(currentPosition);
                    if (dist != null && dist < enavSettings.getMsiRelevanceFromOwnShipRange()) {
                        msg.setFiltered(true);
                    }
                }

                // 2) Check proximity to routes
                if (!msg.isFiltered() && msg.nearRoute(routeManager.getVisibleRoutes())) {
                    msg.setFiltered(true);
                }

                updated |= wasFiltered != msg.isFiltered();
            }
        }

        LOG.info("RECOMPUTE MSI-NM IN " + (System.currentTimeMillis() - t0) + " MS");

        // Has the MSI-NM been updated
        if (updated) {
            fireMsiNmMessagesChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pntDataUpdate(PntData pntData) {
        Position position = pntData.getPosition();

        if (currentPosition == null ||
                Calculator.range(position, currentPosition, Heading.GC) > enavSettings.getMsiRelevanceGpsUpdateRange()) {
            currentPosition = position;
            recomputeMsiNmMessageFilter();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void routesChanged(RoutesUpdateEvent e) {
        if (e != null) {
            switch (e) {
                case ROUTE_ACTIVATED:
                case ROUTE_DEACTIVATED:
                case ACTIVE_ROUTE_UPDATE:
                case ROUTE_MSI_UPDATE:
                case ROUTE_ADDED:
                case ROUTE_REMOVED:
                case ROUTE_CHANGED:
                    recomputeMsiNmMessageFilter();
            }
        }
    }

    @Override
    public void findAndInit(Object obj) {
        if (routeManager == null && obj instanceof RouteManagerCommon) {
            routeManager = (RouteManagerCommon) obj;
            routeManager.addListener(this);
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

    //*******************************
    //* Listener functionality
    //*******************************

    /**
     * Called when the service list has been updated
     */
    synchronized void fireMsiNmServicesChanged() {
        for (IMsiNmServiceListener listener : listeners) {
            listener.msiNmServicesChanged(msiNmServiceList);
        }
    }

    /**
     * Called when the service list has been updated
     */
    synchronized void fireMsiNmMessagesChanged() {
        for (IMsiNmServiceListener listener : listeners) {
            listener.msiNmMessagesChanged(msiNmMessages);
        }
    }

    /**
     * Adds a listener
     * @param listener the listener to add
     */
    public void addListener(IMsiNmServiceListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener
     * @param listener the listener to remove
     */
    public void removeListener(IMsiNmServiceListener listener) {
        listeners.remove(listener);
    }


    /**
     * Interface implemented by MSI-NM service listeners
     */
    public interface IMsiNmServiceListener {

        /**
         * Called when the list of MSI-NM services has changed
         * @param msiNmServiceList the new list of MSI-NM services
         */
        void msiNmServicesChanged(List<MCMsiNmService> msiNmServiceList);

        /**
         * Called when the list of MSI-NM messages has changed
         * @param msiNmMessages the new list of MSI-NM messages
         */
        void msiNmMessagesChanged(List<MsiNmNotification> msiNmMessages);

    }

}

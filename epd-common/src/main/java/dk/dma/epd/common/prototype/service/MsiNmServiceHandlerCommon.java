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

import dk.dma.epd.common.prototype.EPD;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of a Maritime Cloud MSI-NM service
 */
public class MsiNmServiceHandlerCommon extends EnavServiceHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(MsiNmServiceHandlerCommon.class);

    private List<MCMsiNmService> msiNmServiceList = new ArrayList<>();
    private List<MCMessage> msiNmMessages = new ArrayList<>();
    private MaritimeId msiNmServiceId;
    protected List<IMsiNmServiceListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Constructor
     */
    public MsiNmServiceHandlerCommon() {
        super();

        // Compute the currently selected MSI-NM service
        String selectedMsiNmServiceId = EPD.getInstance().getSettings().getEnavSettings().getMsiNmServiceId();
        msiNmServiceId = StringUtils.isBlank(selectedMsiNmServiceId)
                ? null
                : MaritimeCloudUtils.toMaritimeId(selectedMsiNmServiceId);


        // Schedule a refresh of the chat services approximately every minute
        scheduleWithFixedDelayWhenConnected(new Runnable() {
            @Override
            public void run() {
                fetchMsiNmServices();
            }
        }, 5, 64, TimeUnit.SECONDS);


        // Schedule a refresh of the active MSI-NM messages approximately every minute
        scheduleWithFixedDelayWhenConnected(new Runnable() {
            @Override
            public void run() {
                fetchPublishedMsiNmMessages();
            }
        }, 20, 95, TimeUnit.SECONDS);
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
                for (MCMessage msg : msiNmMessages) {
                    if (lastUpdate == null || msg.getUpdated().getTime() > lastUpdate.getTime()) {
                        lastUpdate = msg.getUpdated();
                    }
                }

                // Fetch the list of active messages
                MCSearchResult result = msiNmService.activeMessagesIfUpdates("en", lastUpdate).join();

                if (StringUtils.isNotBlank(result.getError())) {
                    LOG.error("Error fetching active MSI-NM messages: " + result.getError());
                }

                if (!result.getUnchanged()) {
                    msiNmMessages = result.getMessages();
                    System.out.println("XXXXXXXXXXXXX " + msiNmMessages.size());
                    fireMsiNmMessagesChanged();
                }

            } else if (msiNmMessages.size() > 0) {
                msiNmMessages = new ArrayList<>();
                fireMsiNmMessagesChanged();
            }

        } catch (Exception e) {
            LOG.error("Failed looking up published MSI-NM messages", e.getMessage());
        }
    }

    public List<MCMsiNmService> getMsiNmServiceList() {
        return msiNmServiceList;
    }

    public List<MCMessage> getMsiNmMessages() {
        return msiNmMessages;
    }

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
        void msiNmMessagesChanged(List<MCMessage> msiNmMessages);

    }

}

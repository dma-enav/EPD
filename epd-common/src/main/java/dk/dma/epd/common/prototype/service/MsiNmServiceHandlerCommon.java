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

import dma.msinm.MCMsiNmService;
import net.maritimecloud.mms.MmsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of a Maritime Cloud MSI-NM service
 */
public class MsiNmServiceHandlerCommon extends EnavServiceHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(MsiNmServiceHandlerCommon.class);

    private List<MCMsiNmService> msiNmServiceList = new ArrayList<>();

    /**
     * Constructor
     */
    public MsiNmServiceHandlerCommon() {
        super();

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
            msiNmServiceList = getMmsClient().endpointFind(MCMsiNmService.class).findAll().get();
            LOG.info("************* MSINM " + msiNmServiceList);

        } catch (Exception e) {
            LOG.error("Failed looking up MSI-NM services", e.getMessage());
        }
    }

    private void fetchPublishedMsiNmMessages() {
        try {
            if (msiNmServiceList.size() > 0) {
                MCMsiNmService msiNmService = msiNmServiceList.get(0);



                LOG.info("***** PUBLISHED MSINM " + msiNmService.activeMessages("en").join());
            }

        } catch (Exception e) {
            LOG.error("Failed looking up published MSI-NM messages", e.getMessage());
        }
    }


    /**
     * Returns the MSI-NM services list
     * 
     * @return the MSI-NM services list
     */
    public List<MCMsiNmService> getMsiNmServiceList() {
        return msiNmServiceList;
    }

}

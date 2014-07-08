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
package dk.dma.epd.common.prototype.model.identity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon;

public class IdentityHandler extends EnavServiceHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(EnavServiceHandlerCommon.class);
    private Map<Long, MaritimeIdentity> maritimeIdentities = new ConcurrentHashMap<>();

    public IdentityHandler() {
        // Load Stored Identitys from file. Currently stored in multiple property files

        LOG.info("Loading Identities");
        loadActors();
    }

    /**
     * Load the actors from the properties
     */
    private void loadActors() {

        File folder = new File(EPD.getInstance().getHomePath().toString() + "/identities");
        File[] listOfFiles = folder.listFiles();

        
        if (listOfFiles != null) {
            LOG.info("A total of " + listOfFiles.length + " Maritime Cloud actors found");
    
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    MaritimeIdentity actor = createActorFromProps(listOfFiles[i]);
                    maritimeIdentities.put(actor.getMaritimeID(), actor);
                }

            }
        }
    }

    private MaritimeIdentity createActorFromProps(File propertyFile) {

        try {
            InputStream in = new FileInputStream(propertyFile);
            Properties identity = new Properties();
            identity.load(in);

            MaritimeIdentity mcIdentity = new MaritimeIdentity(identity);

            return mcIdentity;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void createActorFromAIS() {

        // Do not create if already exists

    }

    /**
     * @return the maritimeIdentitys
     */
    public Map<Long, MaritimeIdentity> getMaritimeIdentities() {
        return maritimeIdentities;
    }

    /**
     * Return if the actor is a registered MC actor
     * 
     * @param mmsi
     * @return
     */
    public boolean actorExists(long mmsi) {
        return maritimeIdentities.containsKey(mmsi);
    }

    /**
     * Return the actor with a given mmsi
     * 
     * @param mmsi
     * @return
     */
    public MaritimeIdentity getActor(long mmsi) {
        if (actorExists(mmsi)) {
            return maritimeIdentities.get(mmsi);
        } else {
            return null;
        }

    }
}

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
    private Map<Long, MaritimeIdentity> maritimeIdentitys = new ConcurrentHashMap<>();

    public IdentityHandler() {
        // Load Stored Identitys from file. Currently stored in multiple property files

        LOG.info("Loading Identitys");
        loadActors();
    }

    /**
     * Load the actors from the properties
     */
    private void loadActors() {

        File folder = new File(EPD.getInstance().getHomePath().toString() + "/identitys");
        File[] listOfFiles = folder.listFiles();

        LOG.info("A total of " + listOfFiles.length + " Maritime Cloud actors found");

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                MaritimeIdentity actor = createActorFromProps(listOfFiles[i]);
                maritimeIdentitys.put(actor.getMaritimeID(), actor);
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
    public Map<Long, MaritimeIdentity> getMaritimeIdentitys() {
        return maritimeIdentitys;
    }

    /**
     * Return if the actor is a registered MC actor
     * 
     * @param mmsi
     * @return
     */
    public boolean actorExists(long mmsi) {
        return maritimeIdentitys.containsKey(mmsi);
    }

    /**
     * Return the actor with a given mmsi
     * 
     * @param mmsi
     * @return
     */
    public MaritimeIdentity getActor(long mmsi) {
        if (actorExists(mmsi)) {
            return maritimeIdentitys.get(mmsi);
        } else {
            return null;
        }

    }
}

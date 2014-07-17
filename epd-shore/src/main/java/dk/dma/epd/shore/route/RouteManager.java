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
package dk.dma.epd.shore.route;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;
import dk.dma.epd.shore.EPDShore;

/**
 * Manager for handling a collection of routes and active route
 */
public class RouteManager extends RouteManagerCommon {

    private static final long serialVersionUID = -8815260482774695988L;
    private static final String ROUTESFILE = EPD.getInstance().getHomePath().resolve(".routes").toString();
    private static final Logger LOG = LoggerFactory.getLogger(RouteManager.class);

    /**
     * Constructor
     */
    public RouteManager() {
        super();
    }

    /**************************************/
    /** Life cycle operations **/
    /**************************************/

    /**
     * Loads and instantiates a {@code RouteManager} from the default routes file.
     * 
     * @return the new route manager
     */
    public static RouteManager loadRouteManager() {
        RouteManager manager = new RouteManager();
        try {
            FileInputStream fileIn = new FileInputStream(ROUTESFILE);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            RouteStore routeStore = (RouteStore) objectIn.readObject();
            objectIn.close();
            fileIn.close();
            manager.setRoutes(routeStore.getRoutes());

        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load routes file: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(ROUTESFILE).delete();
        }

        return manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToFile() {
        synchronized (routes) {
            RouteStore routeStore = new RouteStore(this);
            try {
                FileOutputStream fileOut = new FileOutputStream(ROUTESFILE);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(routeStore);
                objectOut.close();
                fileOut.close();
            } catch (IOException e) {
                LOG.error("Failed to save routes file: " + e.getMessage());
            }
        }
    }

    @Override
    public void notifyListeners(RoutesUpdateEvent e) {
        super.notifyListeners(e);

        EPDShore.getInstance().getVoctManager().saveToFile();
    }
}

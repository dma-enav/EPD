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
import dk.dma.epd.common.prototype.route.RouteManagerCommon;


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
* Loads and instantiates a {@code RouteManager} from the
* default routes file.
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
}

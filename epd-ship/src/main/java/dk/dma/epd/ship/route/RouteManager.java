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
package dk.dma.epd.ship.route;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.ActiveRoute.ActiveWpSelectionResult;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.component_panels.ShowDockableDialog;
import dk.dma.epd.ship.gui.component_panels.ShowDockableDialog.dock_type;

/**
 * Manager for handling a collection of routes and active route
 */
@ThreadSafe
public class RouteManager extends RouteManagerCommon implements IPntDataListener {

    private static final long serialVersionUID = -9019124285849351709L;
    private static final String ROUTES_FILE = EPD.getInstance().getHomePath().resolve(".routes").toString();
    private static final Logger LOG = LoggerFactory.getLogger(RouteManager.class);

    private volatile PntHandler pntHandler;
    
    @GuardedBy("routeSuggestions")
    private List<RouteSuggestionData> routeSuggestions = new LinkedList<>();    
    

    /**
     * Constructor
     */
    public RouteManager() {
        super();
    }

    /**
     * Called when receiving a position update
     * @param pntData the updated position
     */
    @Override
    public void pntDataUpdate(PntData pntData) {
        if (!isRouteActive()) {
            return;
        }
        if (pntData.isBadPosition()) {
            return;
        }

        
        
        ActiveWpSelectionResult endRes;
        ActiveWpSelectionResult res;
        synchronized (this) {
            activeRoute.update(pntData);
            endRes = activeRoute.chooseActiveWp();
            res = endRes;
            // Keep chosing active waypoint until not changed any more
            while (res == ActiveWpSelectionResult.CHANGED) {
                res = activeRoute.chooseActiveWp();
            }
        }

        // If last change ended route, this will be result
        if (res == ActiveWpSelectionResult.ROUTE_FINISHED) {
            endRes = ActiveWpSelectionResult.ROUTE_FINISHED;
        }

        if (endRes == ActiveWpSelectionResult.CHANGED) {
            notifyListeners(RoutesUpdateEvent.ACTIVE_ROUTE_UPDATE);
        } else if (endRes == ActiveWpSelectionResult.ROUTE_FINISHED) {
            synchronized (this) {
                activeRoute = null;
                activeRouteIndex = -1;
            }
            notifyListeners(RoutesUpdateEvent.ACTIVE_ROUTE_FINISHED);
        }
    }

    /**************************************/
    /** Active Route operations          **/
    /**************************************/
    
    /**
     * Activates the route with the given index
     * @param index the index of the route to activate
     */
    public void activateRoute(int index) {
        synchronized (this) {
            if (index < 0 || index >= routes.size()) {
                LOG.error("Could not activate route with index: " + index);
                return;
            }

            if (isRouteActive()) {
                // Deactivate route
                deactivateRoute();
            }

            Route route = routes.get(index);
            route.setVisible(true);
            // Set active route index
            activeRouteIndex = index;

            // Create new
            activeRoute = new ActiveRoute(route, pntHandler.getCurrentData());

            // Set the minimum WP circle radius
            activeRoute.setWpCircleMin(EPDShip.getInstance().getSettings().getNavSettings()
                    .getMinWpRadius());
            // Set relaxed WP change
            activeRoute.setRelaxedWpChange(EPDShip.getInstance().getSettings().getNavSettings()
                    .isRelaxedWpChange());
            // Inject the current position
            activeRoute.update(pntHandler.getCurrentData());
            // Set start time to now
            activeRoute.setStarttime(PntTime.getInstance().getDate());
        }

        // If the dock isn't visible should it show it?
        if (!EPDShip.getInstance().getMainFrame().getDockableComponents()
                .isDockVisible("Active Waypoint")) {

            // Show it display the message?
            if (EPDShip.getInstance().getSettings().getGuiSettings().isShowDockMessage()) {
                new ShowDockableDialog(EPDShip.getInstance().getMainFrame(), dock_type.ROUTE);
            } else {

                if (EPDShip.getInstance().getSettings().getGuiSettings().isAlwaysOpenDock()) {
                    EPDShip.getInstance().getMainFrame().getDockableComponents()
                            .openDock("Active Waypoint");
                    EPDShip.getInstance().getMainFrame().getJMenuBar()
                            .refreshDockableMenu();
                }

                // It shouldn't display message but take a default action

            }

        }

        // Notify listeners
        notifyListeners(RoutesUpdateEvent.ROUTE_ACTIVATED);
    }

    /**
     * Deactivates the currently active route
     */
    public void deactivateRoute() {
        synchronized (this) {
            activeRoute = null;
            activeRouteIndex = -1;
        }

        notifyListeners(RoutesUpdateEvent.ROUTE_DEACTIVATED);
    }


    /**************************************/
    /** Life cycle operations            **/
    /**************************************/
    
    /**
     * Loads and instantiates a {@code RouteManager} from the 
     * default routes file.
     * @return the new route manager
     */
    public static RouteManager loadRouteManager() {
        RouteManager manager = new RouteManager();
        try (
            FileInputStream fileIn = new FileInputStream(ROUTES_FILE);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);) {
            RouteStore routeStore = (RouteStore) objectIn.readObject();
            manager.setRoutes(routeStore.getRoutes());
            manager.activeRoute = routeStore.getActiveRoute();
            manager.activeRouteIndex = routeStore.getActiveRouteIndex();

        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load routes file: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(ROUTES_FILE).delete();
        }

        return manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void saveToFile() {
        RouteStore routeStore = new RouteStore(this);
        try (
            FileOutputStream fileOut = new FileOutputStream(ROUTES_FILE);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);) {
            objectOut.writeObject(routeStore);
        } catch (IOException e) {
            LOG.error("Failed to save routes file: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (pntHandler == null && obj instanceof PntHandler) {
            pntHandler = (PntHandler) obj;
            pntHandler.addListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndUndo(Object obj) {
        if (pntHandler == obj) {
            pntHandler.removeListener(this);
        }
        super.findAndUndo(obj);
    }
 }

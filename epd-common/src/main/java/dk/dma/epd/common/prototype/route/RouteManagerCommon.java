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
package dk.dma.epd.common.prototype.route;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLoadException;
import dk.dma.epd.common.prototype.model.route.RouteLoader;
import dk.dma.epd.common.prototype.model.route.RouteMetocSettings;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.settings.EnavSettings;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.common.util.Util;
import dk.frv.enav.common.xml.metoc.MetocForecast;

/**
 * Base class for route managers, which handles a collection of routes and active route.
 * <p>
 * The default implementation does not support active routes.
 */
@ThreadSafe
public abstract class RouteManagerCommon extends MapHandlerChild implements Runnable, Serializable {

    private static final long serialVersionUID = -3781810760698987644L;
    private static final Logger LOG = LoggerFactory.getLogger(RouteManagerCommon.class);

    private CopyOnWriteArrayList<IRoutesUpdateListener> listeners = new CopyOnWriteArrayList<>();
    protected EnavSettings enavSettings;
    protected ShoreServicesCommon shoreServices;

    @GuardedBy("this")
    protected List<Route> routes = new LinkedList<>();
    @GuardedBy("this")
    protected ActiveRoute activeRoute;
    @GuardedBy("this")
    protected int activeRouteIndex = -1;

    /**
     * Constructor
     */
    public RouteManagerCommon() {
        enavSettings = EPD.getInstance().getSettings().getEnavSettings();
        EPD.startThread(this, "RouteManager");
    }

    /**************************************/
    /** Route operations **/
    /**************************************/

    /**
     * Adds the given route
     * 
     * @param route
     *            the route to add
     */
    public void addRoute(Route route) {
        synchronized (this) {
            routes.add(route);
        }
        notifyListeners(RoutesUpdateEvent.ROUTE_ADDED);
    }

    /**
     * Removes the given route
     * 
     * @param route
     *            the route to remove
     */
    public void removeRoute(Route route) {
        synchronized (this) {
            if (route == activeRoute) {
                LOG.error("Cannot remove active route");
                return;
            }
            for (int x = 0; x < routes.size(); x++) {
                if (routes.get(x) == route) {
                    removeRoute(x);
                    return;
                }
            }
        }
    }

    /**
     * Removes the route with the given index
     * 
     * @param routeIndex
     *            index of the route to remove
     */
    public void removeRoute(int index) {
        synchronized (this) {
            if (index < 0 || index >= routes.size()) {
                LOG.error("Could not deactivate route with index: " + index);
                return;
            }
            if (isActiveRoute(index)) {
                LOG.error("Cannot remove active route");
                return;
            }
            if (index < activeRouteIndex) {
                activeRouteIndex--;
            }
            routes.remove(index);
        }

        notifyListeners(RoutesUpdateEvent.ROUTE_REMOVED);
    }

    /**
     * Copy the route with the given index
     * 
     * @param index
     *            the index of the route to copy
     */
    public void routeCopy(int index) {
        Route selectedRoute = getRoute(index);
        if (selectedRoute == null) {
            return;
        }
        selectedRoute.setVisible(selectedRoute instanceof ActiveRoute);
        Route routeCopy = selectedRoute.copy();
        routeCopy.setName(routeCopy.getName() + " copy");
        routeCopy.setVisible(true);
        addRoute(routeCopy);
    }

    /**
     * Reverts the route with the given index
     * 
     * @param index
     *            the index of the route to revert
     */
    public void routeReverse(int index) {
        Route selectedRoute = getRoute(index);
        if (selectedRoute == null) {
            return;
        }
        selectedRoute.setVisible(selectedRoute instanceof ActiveRoute);
        Route routeReversed = selectedRoute.reverse();
        routeReversed.setName(routeReversed.getName() + " reversed");
        routeReversed.setVisible(true);
        addRoute(routeReversed);
    }

    /**
     * Returns the current list of routes
     * 
     * @return the current list of routes
     */
    public List<Route> getRoutes() {
        synchronized (routes) {
            return routes;
        }
    }

    /**
     * Sets the given routes as the current list of routes
     * 
     * @param routes
     *            the routes to set
     */
    protected void setRoutes(List<Route> routes) {
        if (routes != null) {
            this.routes = routes;
        }
    }

    /**
     * Returns the number of routes
     * 
     * @return the number of routes
     */
    public int getRouteCount() {
        synchronized (routes) {
            return routes.size();
        }
    }

    /**
     * Returns the route with the given index
     * 
     * @param index
     *            the index of the route to return
     * @return the route with the given index
     */
    public synchronized Route getRoute(int index) {
        if (index == activeRouteIndex) {
            return activeRoute;
        }
        return getRoutes().get(index);
    }

    /**
     * Returns the index of the given route in the current list of routes. returns -1 if the route is not found.
     * 
     * @param route
     *            the route to find the index for
     * @return the index or -1 if not found
     */
    public synchronized int getRouteIndex(Route route) {
        for (int i = 0; i < routes.size(); i++) {
            if (route == routes.get(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Hides the route with the given index
     * 
     * @param routeIndex
     *            the route to hide
     */
    public void hideRoute(int routeIndex) {
        Route route = getRoute(routeIndex);
        if (route != null && route.isVisible()) {
            route.setVisible(false);
            notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
        }
    }

    /**
     * Hides all in-active routes
     */
    public void hideInactiveRoutes() {
        boolean visibilityChanged = false;
        int routeIndex = 0;
        synchronized (this) {
            for (Route route : routes) {
                if (routeIndex != activeRouteIndex && route.isVisible()) {
                    route.setVisible(false);
                    visibilityChanged = true;
                }
                routeIndex++;
            }
        }
        if (visibilityChanged) {
            notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
        }
    }

    /**************************************/
    /** Active Route operations **/
    /**************************************/

    /**
     * Returns if there is currently an active route
     * 
     * @return if there is currently an active route
     */
    public synchronized boolean isRouteActive() {
        return activeRouteIndex >= 0;
    }

    /**
     * Returns the currently active route
     * 
     * @return the currently active route
     */
    public synchronized ActiveRoute getActiveRoute() {
        return activeRoute;
    }

    /**
     * Returns whether the route with the given index is the active route
     * 
     * @param index
     *            the index of the route to check
     * @return if route with the given index is the active route
     */
    public synchronized boolean isActiveRoute(int index) {
        return isRouteActive() && index == activeRouteIndex;
    }

    /**
     * Returns the index of the active route
     * 
     * @return the index of the active route
     */
    public synchronized int getActiveRouteIndex() {
        return activeRouteIndex;
    }

    /**
     * Sets the active way point of the active route to be the one with the give index
     * 
     * @param index
     *            the index of the active way point
     */
    public void changeActiveWp(int index) {
        synchronized (this) {
            if (!isRouteActive()) {
                return;
            }
            activeRoute.changeActiveWaypoint(index);
        }

        notifyListeners(RoutesUpdateEvent.ACTIVE_ROUTE_UPDATE);
    }

    /**************************************/
    /** Listener operations **/
    /**************************************/

    /**
     * Notifies the listeners that the route has updated
     * 
     * @param e
     *            the route update event
     */
    public void notifyListeners(RoutesUpdateEvent e) {
        for (IRoutesUpdateListener listener : listeners) {
            listener.routesChanged(e);
        }
        // Persist routes on update
        saveToFile();
    }

    /**
     * Adds a new listener
     * 
     * @param listener
     *            the listener to add
     */
    public void addListener(IRoutesUpdateListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener
     * 
     * @param listener
     *            the listener to remove
     */
    public void removeListener(IRoutesUpdateListener listener) {
        listeners.remove(listener);
    }

    /**************************************/
    /** METOC operations **/
    /**************************************/

    /**
     * Validate if METOC is still valid for route.<br>
     * If not METOC is removed.<br>
     * Not for active route
     * 
     * @param route
     *            the route to validate METOC for
     * @return if the route has valid METOC data
     */
    public boolean validateMetoc(Route route) {
        if (route instanceof ActiveRoute) {
            return false;
        }
        if (!showMetocForRoute(route) || !route.isMetocValid(enavSettings.getMetocTimeDiffTolerance())) {
            if (route.getMetocForecast() != null) {
                route.removeMetoc();
                notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
            }
            return false;
        }
        return true;
    }

    /**
     * Get METOC data for the route
     * 
     * @param route
     *            the route to get METOC data for
     */
    public void requestRouteMetoc(Route route) throws ShoreServiceException {
        // Request METOC from shore
        MetocForecast metocForecast = shoreServices.routeMetoc(route);
        // Add the METOC to route
        route.setMetocForecast(metocForecast);
        // Set show to true
        route.getRouteMetocSettings().setShowRouteMetoc(true);
    }

    /**
     * Determine if route METOC data should be shown for route
     * 
     * @param route
     * @return
     */
    public boolean showMetocForRoute(Route route) {
        if (route.getRouteMetocSettings() == null) {
            route.setRouteMetocSettings(getDefaultRouteMetocSettings());
        }
        if (route.getMetocForecast() == null || !route.isVisible() || !route.getRouteMetocSettings().isShowRouteMetoc()) {
            return false;
        }
        // Determine if METOC info is old
        if (isMetocOld(route)) {
            return false;
        }
        return true;
    }

    /**
     * Determines if the METOC data is too old for the given route
     * 
     * @param route
     *            the route to check
     * @return if the METOC data is too old
     */
    protected boolean isMetocOld(Route route) {
        if (route.getMetocForecast() == null || route.getMetocForecast().getCreated() == null) {
            return true;
        }
        long metocTtl = enavSettings.getMetocTtl() * 60 * 1000;
        Date now = PntTime.getDate();
        Date metocDate = route.getMetocForecast().getCreated();
        if (now.getTime() - metocDate.getTime() > metocTtl) {
            return true;
        }
        return false;
    }

    /**
     * Returns if the given route has associated METOC data
     * 
     * @param route
     *            the route to check
     * @return if the given route has associated METOC data
     */
    public boolean hasMetoc(Route route) {
        if (route.getMetocForecast() != null) {
            // Determine if METOC info is old
            long metocTtl = enavSettings.getMetocTtl() * 60 * 1000;
            Date now = PntTime.getDate();
            Date metocDate = route.getMetocForecast().getCreated();
            if (now.getTime() - metocDate.getTime() > metocTtl) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Checks all routes if the associated METOC data is still valid
     */
    protected void checkValidMetoc() {
        boolean visualUpdate = false;

        synchronized (this) {
            for (Route route : routes) {
                if (route.getMetocForecast() == null) {
                    continue;
                }
                if (isMetocOld(route) || !route.isMetocValid(enavSettings.getMetocTimeDiffTolerance())) {
                    if (route.isVisible() && route.getRouteMetocSettings().isShowRouteMetoc()) {
                        visualUpdate = true;
                    }
                    route.removeMetoc();
                }
            }
        }

        if (visualUpdate) {
            notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
        }
    }

    /**
     * Polls for METOC data at regular intervals. Only applies for the active route (for now)
     */
    protected void pollForMetoc() {
        if (!isRouteActive()) {
            return;
        }
        synchronized (this) {
            if (activeRoute.getRouteMetocSettings() == null || !activeRoute.getRouteMetocSettings().isShowRouteMetoc()) {
                return;
            }
        }
        long activeRouteMetocPollInterval = enavSettings.getActiveRouteMetocPollInterval() * 60 * 1000;
        // Maybe we never want to refresh metoc
        if (activeRouteMetocPollInterval <= 0) {
            return;
        }
        // Find the age of the current METOC
        long metocAge = Long.MAX_VALUE;
        if (getActiveRoute().getMetocForecast() != null) {
            Date now = PntTime.getDate();
            metocAge = now.getTime() - getActiveRoute().getMetocForecast().getCreated().getTime();
        }
        // Check if minimum time since last update has passed
        if (metocAge <= activeRouteMetocPollInterval) {
            return;
        }
        // Check if not old and still valid
        synchronized (this) {
            if (!isMetocOld(activeRoute) && activeRoute.isMetocValid(enavSettings.getMetocTimeDiffTolerance())) {
                return;
            }
        }

        try {
            requestRouteMetoc(getActiveRoute());
            notifyListeners(RoutesUpdateEvent.ROUTE_METOC_CHANGED);
            LOG.info("Auto updated route metoc for active route");
        } catch (ShoreServiceException e) {
            LOG.error("Failed to auto update METOC for active route: " + e.getMessage());
            synchronized (this) {
                activeRoute.removeMetoc();
            }
            notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
        }
    }

    /**
     * Returns the default METOC settings
     * 
     * @return the default METOC settings
     */
    public RouteMetocSettings getDefaultRouteMetocSettings() {
        EnavSettings enavSettings = EPD.getInstance().getSettings().getEnavSettings();
        RouteMetocSettings routeMetocSettings = new RouteMetocSettings();
        routeMetocSettings.setWindWarnLimit(enavSettings.getDefaultWindWarnLimit());
        routeMetocSettings.setCurrentWarnLimit(enavSettings.getDefaultCurrentWarnLimit());
        routeMetocSettings.setWaveWarnLimit(enavSettings.getDefaultWaveWarnLimit());
        return routeMetocSettings;
    }

    /**************************************/
    /** Life cycle operations **/
    /**************************************/

    /**
     * Loads routes from the given file.<br>
     * The supported file formats are, as defined by the file extension:
     * <ul>
     * <li>.txt: Simple text based format</li>
     * <li>.rou: ECDIS900 V3 route format</li>
     * <li>.rt3: Navisailor 3000 route</li>
     * <li>.kml: KML route or waypoint list</li>
     * <li>Otherwise: pertinacious format</li>
     * </ul>
     * 
     * @param file
     * @throws RouteLoadException
     */
    public void loadFromFile(File file) throws RouteLoadException {
        LOG.debug("Load route from file: " + file.getAbsolutePath());
        // Create new route instance
        Route route;

        // Some pertinacious loading
        String ext = "";
        int mid = file.getName().lastIndexOf('.');
        ext = file.getName().substring(mid + 1, file.getName().length()).toUpperCase();
        if (ext.equals("TXT")) {
            // Load simple from file
            route = RouteLoader.loadSimple(file);
        } else if (ext.equals("ROU")) {
            // Load ECDIS900 V3 route
            route = RouteLoader.loadRou(file, EPD.getInstance().getSettings().getNavSettings());
        } else if (ext.equals("RT3")) {
            // Load Navisailor 3000 route
            route = RouteLoader.loadRt3(file, EPD.getInstance().getSettings().getNavSettings());
        } else if (ext.equals("KML")) {
            // Load from KML
            route = RouteLoader.loadKml(file, EPD.getInstance().getSettings().getNavSettings());
        } else {
            route = RouteLoader.pertinaciousLoad(file, EPD.getInstance().getSettings().getNavSettings());
        }

        // Add route to list
        synchronized (this) {
            routes.add(route);
        }
        // Notify of new route
        notifyListeners(RoutesUpdateEvent.ROUTE_ADDED);
    }

    /**
     * Saves the current set of routes to file
     */
    public abstract void saveToFile();

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (shoreServices == null && obj instanceof ShoreServicesCommon) {
            shoreServices = (ShoreServicesCommon) obj;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndUndo(Object obj) {
        if (shoreServices == obj) {
            shoreServices = null;
        }
        super.findAndUndo(obj);
    }

    /**************************************/
    /** Thread operations **/
    /**************************************/

    /**
     * Main thread run method
     */
    @Override
    public void run() {

        // Maintanaince routines
        while (true) {
            Util.sleep(10000);

            // Active route poll for METOC
            pollForMetoc();

            // Check validity of METOC for all routes
            checkValidMetoc();
        }
    }
}

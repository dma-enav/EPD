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
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.ais.AisAdressedRouteSuggestion;
import dk.dma.epd.common.prototype.ais.AisAdressedRouteSuggestion.Status;
import dk.dma.epd.common.prototype.ais.AisBroadcastRouteSuggestion;
import dk.dma.epd.common.prototype.ais.AisRouteData;
import dk.dma.epd.common.prototype.ais.IAisRouteSuggestionListener;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.ActiveRoute.ActiveWpSelectionResult;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLoadException;
import dk.dma.epd.common.prototype.model.route.RouteLoader;
import dk.dma.epd.common.prototype.model.route.RouteMetocSettings;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.route.RouteSuggestionDialog;
import dk.dma.epd.shore.services.shore.ShoreServices;
import dk.dma.epd.shore.settings.ESDEnavSettings;


/**
 * Manager for handling a collection of routes and active route
 */
public class RouteManager extends dk.dma.epd.common.prototype.route.RouteManagerCommon implements Runnable, Serializable, IPntDataListener, IAisRouteSuggestionListener {

    private static final long serialVersionUID = 1L;
    private static final String ROUTESFILE = EPDShore.getHomePath().resolve(".routes").toString();
    private static final Logger LOG = LoggerFactory.getLogger(RouteManager.class);

    private List<Route> routes = new LinkedList<Route>();
    private Set<AisAdressedRouteSuggestion> addressedSuggestedRoutes = new HashSet<AisAdressedRouteSuggestion>();
    private ActiveRoute activeRoute;
    private int activeRouteIndex = -1;

//    private AisServices aisServices = null;
    private ShoreServices shoreServices;
    private AisHandler aisHandler;
    private RouteSuggestionDialog routeSuggestionDialog;

    private Set<IRoutesUpdateListener> listeners = new HashSet<IRoutesUpdateListener>();

    public RouteManager() {
        EPDShore.startThread(this, "RouteManager");
    }

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
        synchronized (routes) {
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
            synchronized (routes) {
                activeRoute = null;
                activeRouteIndex = -1;
            }
            notifyListeners(RoutesUpdateEvent.ACTIVE_ROUTE_FINISHED);
        }
    }

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

    public void deactivateRoute() {
        synchronized (routes) {
            activeRoute = null;
            activeRouteIndex = -1;
        }

        notifyListeners(RoutesUpdateEvent.ROUTE_DEACTIVATED);
    }

    public void changeActiveWp(int index) {
        synchronized (routes) {
            if (!isRouteActive()) {
                return;
            }
            activeRoute.changeActiveWaypoint(index);
        }

        notifyListeners(RoutesUpdateEvent.ACTIVE_ROUTE_UPDATE);
    }

    public void notifyListeners(RoutesUpdateEvent e) {
        // Call AisServices if active route changes
        if (e == RoutesUpdateEvent.ACTIVE_ROUTE_UPDATE || e == RoutesUpdateEvent.ACTIVE_ROUTE_FINISHED
                || e == RoutesUpdateEvent.ROUTE_ACTIVATED || e == RoutesUpdateEvent.ROUTE_DEACTIVATED) {
//            aisServices.intendedRouteBroadcast(activeRoute);
        }

        for (IRoutesUpdateListener listener : listeners) {
            listener.routesChanged(e);
        }
        // Persist routes on update
        saveToFile();
    }

    public void removeRoute(int index) {
        synchronized (routes) {
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

    public void addRoute(Route route) {
        synchronized (routes) {
            routes.add(route);
        }
        notifyListeners(RoutesUpdateEvent.ROUTE_ADDED);
    }

    public boolean isRouteActive() {
        synchronized (routes) {
            return activeRouteIndex >= 0;
        }
    }

    public ActiveRoute getActiveRoute() {
        synchronized (routes) {
            return activeRoute;
        }
    }

    public boolean isActiveRoute(int index) {
        synchronized (routes) {
            return isRouteActive() && index == activeRouteIndex;
        }
    }

    public int getActiveRouteIndex() {
        synchronized (routes) {
            return activeRouteIndex;
        }
    }

    public Route getRoute(int index) {
        synchronized (routes) {
            if (index == activeRouteIndex) {
                return activeRoute;
            }
            return getRoutes().get(index);
        }
    }

    public List<Route> getRoutes() {
        synchronized (routes) {
            return routes;
        }
    }

    public int getRouteCount() {
        synchronized (routes) {
            return routes.size();
        }
    }

    public void receiveRouteSuggestion(AisRouteData routeSuggestion) {
        if (routeSuggestion instanceof AisAdressedRouteSuggestion) {
            handleAddressedRouteSuggestion((AisAdressedRouteSuggestion)routeSuggestion);
        } else if (routeSuggestion instanceof AisBroadcastRouteSuggestion) {
            handleBroadcastRouteSuggestion((AisBroadcastRouteSuggestion) routeSuggestion);
        }

    }

    private void handleAddressedRouteSuggestion(AisAdressedRouteSuggestion routeSuggestion) {
        // Handle cancellation
        if (routeSuggestion.isCancel()) {
            System.out.println("handling route suggestion canecellation");
            synchronized (addressedSuggestedRoutes) {
                for (AisAdressedRouteSuggestion oldRouteSuggestion : addressedSuggestedRoutes) {
                    if (oldRouteSuggestion.getMsgLinkId() == routeSuggestion.getMsgLinkId()) {
                        oldRouteSuggestion.cancel();
                        break;
                    }
                }
            }
            notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
            return;
        }

        // Handle new routes
        System.out.println("handling addressed route sugesstion: " + routeSuggestion);

        // Insert into list of
        synchronized (addressedSuggestedRoutes) {
            addressedSuggestedRoutes.add(routeSuggestion);
        }

        // Update route layer
        notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);

        // Show dialog
        routeSuggestionDialog.showSuggestion(routeSuggestion);


    }

    private void handleBroadcastRouteSuggestion(AisBroadcastRouteSuggestion routeSuggestion) {
        // TODO
    }

    public void loadFromFile(File file) throws RouteLoadException {
        LOG.debug("Load route from file: " + file.getAbsolutePath());
        // Create new route instance
        Route route;


        // Some pertinacious loading
        String ext = "";
        int mid = file.getName().lastIndexOf('.');
        ext = file.getName().substring(mid+1,file.getName().length()).toUpperCase();
        if (ext.equals("TXT")) {
            // Load simple from file
            route = RouteLoader.loadSimple(file);
        } else if (ext.equals("ROU")) {
            // Load ECDIS900 V3 route
            route = RouteLoader.loadRou(file, EPDShore.getSettings().getNavSettings());
        } else if (ext.equals("RT3")) {
            // Load Navisailor 3000 route
            route = RouteLoader.loadRt3(file, EPDShore.getSettings().getNavSettings());
        } else {
            route = RouteLoader.pertinaciousLoad(file, EPDShore.getSettings().getNavSettings());
        }

        // Add route to list
        synchronized (routes) {
            routes.add(route);
        }
        // Notify of new route
        notifyListeners(RoutesUpdateEvent.ROUTE_ADDED);
    }

    public void addListener(IRoutesUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IRoutesUpdateListener listener) {
        listeners.remove(listener);
    }

    /**
     * Get route metoc for route
     *
     * @param route
     * @throws ShoreServiceException
     */
    @Override
    public void requestRouteMetoc(Route route) throws ShoreServiceException {
        // Request METOC from shore
//        MetocForecast metocForecast = shoreServices.routeMetoc(route);
        // Add the METOC to route
//        route.setMetocForecast(metocForecast);
        // Set show to true
//        route.getRouteMetocSettings().setShowRouteMetoc(true);
    }

    /**
     * Determine if route metoc should be shown for route
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

    public  boolean isMetocOld(Route route) {
        if (route.getMetocForecast() == null || route.getMetocForecast().getCreated() == null) {
            return true;
        }
        ESDEnavSettings enavSettings = EPDShore.getSettings().getEnavSettings();
        long metocTtl = enavSettings.getMetocTtl() * 60 * 1000;
        Date now = PntTime.getInstance().getDate();
        Date metocDate = route.getMetocForecast().getCreated();
        if (now.getTime() - metocDate.getTime() > metocTtl) {
            return true;
        }
        return false;
    }

    private void checkValidMetoc() {
        boolean visualUpdate = false;

        synchronized (routes) {
            for (Route route : routes) {
                if (route.getMetocForecast() == null) {
                    continue;
                }
                if (isMetocOld(route) || !route.isMetocValid(EPDShore.getSettings().getEnavSettings().getMetocTimeDiffTolerance())) {
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
     * Validate if metoc is still valid for route
     * If not METOC is removed
     * Not for active route
     */
    public boolean validateMetoc(Route route) {
        if (route instanceof ActiveRoute) {
            return false;
        }
        if (!showMetocForRoute(route) || !route.isMetocValid(EPDShore.getSettings().getEnavSettings().getMetocTimeDiffTolerance())) {
            if (route.getMetocForecast() != null) {
                route.removeMetoc();
                notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
            }
            return false;
        }
        return true;
    }

    public boolean hasMetoc(Route route){
        if(route.getMetocForecast() != null){
            // Determine if METOC info is old
            ESDEnavSettings enavSettings = EPDShore.getSettings().getEnavSettings();
            long metocTtl = enavSettings.getMetocTtl() * 60 * 1000;
            Date now = PntTime.getInstance().getDate();
            Date metocDate = route.getMetocForecast().getCreated();
            if (now.getTime() - metocDate.getTime() > metocTtl) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static RouteManager loadRouteManager() {
        RouteManager manager = new RouteManager();
        try {
            FileInputStream fileIn = new FileInputStream(ROUTESFILE);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            RouteStore routeStore = (RouteStore) objectIn.readObject();
            objectIn.close();
            fileIn.close();
            manager.setRoutes(routeStore.getRoutes());
            manager.activeRoute = routeStore.getActiveRoute();
            manager.activeRouteIndex = routeStore.getActiveRouteIndex();
            manager.setAddressedSuggestedRoutes(routeStore.getAddressedSuggestedRoutes());

        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load routes file: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(ROUTESFILE).delete();
        }

        return manager;
    }

    private void setAddressedSuggestedRoutes(Set<AisAdressedRouteSuggestion> addressedSuggestedRoutes) {
        if (addressedSuggestedRoutes != null) {
            this.addressedSuggestedRoutes = addressedSuggestedRoutes;
        }
    }

    private void setRoutes(List<Route> routes) {
        if (routes != null) {
            this.routes = routes;
        }
    }

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

    public RouteMetocSettings getDefaultRouteMetocSettings() {
        ESDEnavSettings enavSettings = EPDShore.getSettings().getEnavSettings();
        RouteMetocSettings routeMetocSettings = new RouteMetocSettings();
        routeMetocSettings.setWindWarnLimit(enavSettings.getDefaultWindWarnLimit());
        routeMetocSettings.setCurrentWarnLimit(enavSettings.getDefaultCurrentWarnLimit());
        routeMetocSettings.setWaveWarnLimit(enavSettings.getDefaultWaveWarnLimit());
        return routeMetocSettings;
    }


    @Override
    public void findAndInit(Object obj) {
        if (shoreServices == null && obj instanceof ShoreServices) {
            shoreServices = (ShoreServices) obj;
        }
        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler)obj;
//            aisHandler.addRouteSuggestionListener(this);
        }
        if (obj instanceof RouteSuggestionDialog) {
            routeSuggestionDialog = (RouteSuggestionDialog)obj;
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (shoreServices == obj) {
            shoreServices = null;
        }
    }

    private void pollForMetoc() {
        if (!isRouteActive()) {
            return;
        }
        if (activeRoute.getRouteMetocSettings() == null || !activeRoute.getRouteMetocSettings().isShowRouteMetoc()) {
            return;
        }
        long activeRouteMetocPollInterval = EPDShore.getSettings().getEnavSettings().getActiveRouteMetocPollInterval() * 60 * 1000;
        // Maybe we never want to refresh metoc
        if (activeRouteMetocPollInterval <= 0) {
            return;
        }
        // Find the age of the current METOC
        long metocAge = Long.MAX_VALUE;
        if (getActiveRoute().getMetocForecast() != null) {
            Date now = PntTime.getInstance().getDate();
            metocAge = now.getTime() - getActiveRoute().getMetocForecast().getCreated().getTime();
        }
        // Check if minimum time since last update has passed
        if (metocAge <= activeRouteMetocPollInterval) {
            return;
        }
        // Check if not old and still valid
        if (!isMetocOld(activeRoute) && activeRoute.isMetocValid(EPDShore.getSettings().getEnavSettings().getMetocTimeDiffTolerance())) {
            return;
        }

        try {
            requestRouteMetoc(getActiveRoute());
            notifyListeners(RoutesUpdateEvent.ROUTE_METOC_CHANGED);
            LOG.info("Auto updated route metoc for active route");
        } catch (ShoreServiceException e) {
            LOG.error("Failed to auto update METOC for active route: " + e.getMessage());
            activeRoute.removeMetoc();
            notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
        }

    }

    public void aisRouteSuggestionReply(AisAdressedRouteSuggestion routeSuggestion, AisAdressedRouteSuggestion.Status status) {
        switch (status) {
        case ACCEPTED:
            routeSuggestion.setStatus(Status.ACCEPTED);
//            aisServices.routeSuggestionReply(routeSuggestion);
            break;
        case REJECTED:
            routeSuggestion.setStatus(Status.REJECTED);
//            aisServices.routeSuggestionReply(routeSuggestion);
            break;
        case NOTED:
            routeSuggestion.setStatus(Status.NOTED);
//            aisServices.routeSuggestionReply(routeSuggestion);
            break;
        default:
            break;
        }

        notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
    }


    public Set<AisAdressedRouteSuggestion> getAddressedSuggestedRoutes() {
        return addressedSuggestedRoutes;
    }

    @Override
    public void run() {

        // Maintanaince routines
        while (true) {
            EPDShore.sleep(10000);

            // Active route poll for METOC
            pollForMetoc();

            // Broadcast intended route over AIS
            if (isRouteActive()) {
//                aisServices.periodicIntendedRouteBroadcast(activeRoute);
            }

            // Check validity of METOC for all routes
            checkValidMetoc();

        }

    }
//
//    // Hack method for getting AisServices
//    public AisServices getAisServices() {
//        return aisServices;
//    }



}

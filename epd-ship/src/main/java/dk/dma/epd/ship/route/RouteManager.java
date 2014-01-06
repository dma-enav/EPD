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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.ais.AisAdressedRouteSuggestion;
import dk.dma.epd.common.prototype.ais.AisAdressedRouteSuggestion.Status;
import dk.dma.epd.common.prototype.ais.AisBroadcastRouteSuggestion;
import dk.dma.epd.common.prototype.ais.AisRouteData;
import dk.dma.epd.common.prototype.ais.IAisRouteSuggestionListener;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.AIS_STATUS;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.ActiveRoute.ActiveWpSelectionResult;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLoadException;
import dk.dma.epd.common.prototype.model.route.RouteLoader;
import dk.dma.epd.common.prototype.model.route.RouteMetocSettings;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.component_panels.ShowDockableDialog;
import dk.dma.epd.ship.gui.component_panels.ShowDockableDialog.dock_type;
import dk.dma.epd.ship.gui.route.RouteSuggestionDialog;
import dk.dma.epd.ship.route.strategic.RecievedRoute;
import dk.dma.epd.ship.service.EnavServiceHandler;
import dk.dma.epd.ship.service.intendedroute.ActiveRouteProvider;
import dk.dma.epd.ship.service.intendedroute.IntendedRouteService;
import dk.dma.epd.ship.settings.EPDEnavSettings;
import dk.frv.enav.common.xml.metoc.MetocForecast;

/**
 * Manager for handling a collection of routes and active route
 */
@ThreadSafe
public class RouteManager extends RouteManagerCommon implements Runnable,
        Serializable, IPntDataListener, IAisRouteSuggestionListener, ActiveRouteProvider {

    private static final long serialVersionUID = 1L;
//    private static final String routesFile = ".routes";
    private static final String ROUTES_FILE = EPDShip.getHomePath().resolve(".routes").toString();
    private static final Logger LOG = LoggerFactory.getLogger(RouteManager.class);

    private volatile EnavServiceHandler enavServiceHandler;
    private volatile PntHandler pntHandler;
    private volatile ShoreServicesCommon shoreServices;
    private volatile AisHandler aisHandler;
    private volatile IntendedRouteService intendedRouteService;
    
    @GuardedBy("suggestedRoutes")
    private List<RecievedRoute> suggestedRoutes = new LinkedList<>();
    @GuardedBy("this")
    private List<Route> routes = new LinkedList<>();
    @GuardedBy("this")
    private ActiveRoute activeRoute;
    @GuardedBy("this")
    private int activeRouteIndex = -1;
    
    private RouteSuggestionDialog routeSuggestionDialog;
    
    private CopyOnWriteArrayList<IRoutesUpdateListener> listeners = new CopyOnWriteArrayList<>();

    public RouteManager() {
        EPDShip.startThread(this, "RouteManager");
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
            activeRoute.setWpCircleMin(EPDShip.getSettings().getNavSettings()
                    .getMinWpRadius());
            // Set relaxed WP change
            activeRoute.setRelaxedWpChange(EPDShip.getSettings().getNavSettings()
                    .isRelaxedWpChange());
            // Inject the current position
            activeRoute.update(pntHandler.getCurrentData());
            // Set start time to now
            activeRoute.setStarttime(PntTime.getInstance().getDate());
        }

        // If the dock isn't visible should it show it?
        if (!EPDShip.getMainFrame().getDockableComponents()
                .isDockVisible("Active Waypoint")) {

            // Show it display the message?
            if (EPDShip.getSettings().getGuiSettings().isShowDockMessage()) {
                new ShowDockableDialog(EPDShip.getMainFrame(), dock_type.ROUTE);
            } else {

                if (EPDShip.getSettings().getGuiSettings().isAlwaysOpenDock()) {
                    EPDShip.getMainFrame().getDockableComponents()
                            .openDock("Active Waypoint");
                    EPDShip.getMainFrame().getEeINSMenuBar()
                            .refreshDockableMenu();
                }

                // It shouldn't display message but take a default action

            }

        }

        // Notify listeners
        notifyListeners(RoutesUpdateEvent.ROUTE_ACTIVATED);
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
        synchronized (this) {
            activeRoute = null;
            activeRouteIndex = -1;
        }

        notifyListeners(RoutesUpdateEvent.ROUTE_DEACTIVATED);
    }

    @Override
    public void changeActiveWp(int index) {
        synchronized (this) {
            if (!isRouteActive()) {
                return;
            }
            activeRoute.changeActiveWaypoint(index);
        }

        notifyListeners(RoutesUpdateEvent.ACTIVE_ROUTE_UPDATE);
    }

    @Override
    public void notifyListeners(RoutesUpdateEvent e) {
        for (IRoutesUpdateListener listener : listeners) {
            listener.routesChanged(e);
        }
        // Persist routes on update
        saveToFile();
    }
    
    /**
     * Hides the route with the given index
     * @param routeIndex the route to hide
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
        synchronized(this) {
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

    @Override
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
    
    @Override
    public int getRouteIndex(Route route) {
        synchronized (this) {
            for (int i = 0; i < routes.size(); i++) {
                if (route == routes.get(i)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void addRoute(Route route) {
        synchronized (this) {
            routes.add(route);
        }
        notifyListeners(RoutesUpdateEvent.ROUTE_ADDED);
    }

    public boolean isRouteActive() {
        synchronized (this) {
            return activeRouteIndex >= 0;
        }
    }

    @Override
    public ActiveRoute getActiveRoute() {
        synchronized (this) {
            return activeRoute;
        }
    }

    @Override
    public boolean isActiveRoute(int index) {
        synchronized (this) {
            return isRouteActive() && index == activeRouteIndex;
        }
    }

    public int getActiveRouteIndex() {
        synchronized (this) {
            return activeRouteIndex;
        }
    }

    @Override
    public Route getRoute(int index) {
        synchronized (this) {
            if (index == activeRouteIndex) {
                return activeRoute;
            }
            return getRoutes().get(index);
        }
    }

    @Override
    public List<Route> getRoutes() {
        synchronized (this) {
            return new ArrayList<>(routes);
        }
    }

    public int getRouteCount() {
        synchronized (routes) {
            return routes.size();
        }
    }

    @Override
    public void receiveRouteSuggestion(AisRouteData routeSuggestion) {
        if (routeSuggestion instanceof AisAdressedRouteSuggestion) {
            handleAddressedRouteSuggestion((AisAdressedRouteSuggestion) routeSuggestion);
        } else if (routeSuggestion instanceof AisBroadcastRouteSuggestion) {
            handleBroadcastRouteSuggestion((AisBroadcastRouteSuggestion) routeSuggestion);
        }

    }
    

    
    public void recieveRouteSuggestion(RecievedRoute message){
        handleCloudRoute(message);
    }
    
    private void handleCloudRoute(RecievedRoute message){
        
        synchronized(suggestedRoutes){
            suggestedRoutes.add(message);            
        }

        
        // Update route layer
        notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
        
        // Show dialog
        routeSuggestionDialog.showSuggestion(message);
    }
    
    
    public void showSuggestionDialog(int id){
        // Show dialog
        synchronized (suggestedRoutes) {
            routeSuggestionDialog.showSuggestion(suggestedRoutes.get(id));
        }                
    }
    
    public boolean acceptSuggested(RecievedRoute route){
        boolean removed = false;
        
        synchronized (suggestedRoutes) {
            for (int i = 0; i < suggestedRoutes.size(); i++) {
                if (suggestedRoutes.get(i).getId() == route.getId()){
                        suggestedRoutes.remove(i);
                        removed = true;
                        break;
                }
            }
        }
        
        if (removed){
            // Update route layer
            notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
            
            synchronized (this) {
                routes.add(route.getRoute());
            }            
            notifyListeners(RoutesUpdateEvent.ROUTE_ADDED);
            return true;
        }
        return false;
    }
    
    public boolean removeSuggested(RecievedRoute route){
        System.out.println("Removing");
        
        boolean removed = false;
        
        synchronized (suggestedRoutes) {
            for (int i = 0; i < suggestedRoutes.size(); i++) {
                if (suggestedRoutes.get(i).getId() == route.getId()) {
                    suggestedRoutes.remove(i);
                    removed = true;
                    break;
                }
            }
        }
        
        if (removed){
            // Update route layer
            notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);

            return true;
        }
        return false;
    }
    
    
    public void routeSuggestionReply(
            RecievedRoute routeSuggestion,
            Status status, String message) {
        

        switch (status) {
        case ACCEPTED:
            routeSuggestion.setStatus(Status.ACCEPTED);
            acceptSuggested(routeSuggestion);
            enavServiceHandler.sendReply(AIS_STATUS.RECIEVED_ACCEPTED, routeSuggestion.getId(), message);
            break;
        case REJECTED:
            //Remove it
            routeSuggestion.setStatus(Status.REJECTED);
            routeSuggestion.setStatus(Status.REJECTED);
//            removeSuggested(routeSuggestion);
            enavServiceHandler.sendReply(AIS_STATUS.RECIEVED_REJECTED, routeSuggestion.getId(), message);
            break;
        case NOTED:
            //Do nothing
            routeSuggestion.setStatus(Status.NOTED);
            enavServiceHandler.sendReply(AIS_STATUS.RECIEVED_NOTED, routeSuggestion.getId(), message);
            break;
        default:
            break;
        }

        notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
    }
    
    
    
    
    public RouteSuggestionDialog getRouteSuggestionDialog() {
        return routeSuggestionDialog;
    }

    private void handleAddressedRouteSuggestion(
            AisAdressedRouteSuggestion routeSuggestion) {
//        // Handle cancellation
//        if (routeSuggestion.isCancel()) {
//            System.out.println("handling route suggestion canecellation");
//            synchronized (addressedSuggestedRoutes) {
//                for (AisAdressedRouteSuggestion oldRouteSuggestion : addressedSuggestedRoutes) {
//               if (oldRouteSuggestion.getMsgLinkId() == routeSuggestion
//                    .getMsgLinkId()) {
//                oldRouteSuggestion.cancel();
//                break;
//               }
//            }
//            }
//            notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
//            return;
//        }
//
//        // Handle new routes
//        System.out.println("handling addressed route sugesstion: "
//                + routeSuggestion);
//
//        // Insert into list of
//        synchronized (addressedSuggestedRoutes) {
//            addressedSuggestedRoutes.add(routeSuggestion);
//        }
//
//        // Update route layer
//        notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
//
//        // Show dialog
//        routeSuggestionDialog.showSuggestion(routeSuggestion);

    }

    private void handleBroadcastRouteSuggestion(
            AisBroadcastRouteSuggestion routeSuggestion) {
        // TODO
    }

    public void loadFromFile(File file) throws RouteLoadException {
        LOG.debug("Load route from file: " + file.getAbsolutePath());
        // Create new route instance
        Route route;

        // Some pertinacious loading
        String ext = "";
        int mid = file.getName().lastIndexOf('.');
        ext = file.getName().substring(mid + 1, file.getName().length())
                .toUpperCase();
        if (ext.equals("TXT")) {
            // Load simple from file
            route = RouteLoader.loadSimple(file);
        } else if (ext.equals("ROU")) {
            // Load ECDIS900 V3 route
            route = RouteLoader.loadRou(file, EPDShip.getSettings().getNavSettings());
        } else if (ext.equals("RT3")) {
            // Load Navisailor 3000 route
            route = RouteLoader.loadRt3(file, EPDShip.getSettings().getNavSettings());
        } else {
            route = RouteLoader.pertinaciousLoad(file, EPDShip.getSettings().getNavSettings());
        }

        // Add route to list
        synchronized (this) {
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
        MetocForecast metocForecast = shoreServices.routeMetoc(route);
        // Add the METOC to route
        route.setMetocForecast(metocForecast);
        // Set show to true
        route.getRouteMetocSettings().setShowRouteMetoc(true);
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
        if (route.getMetocForecast() == null || !route.isVisible()
                || !route.getRouteMetocSettings().isShowRouteMetoc()) {
            return false;
        }
        // Determine if METOC info is old
        if (isMetocOld(route)) {
            return false;
        }
        return true;
    }

    public boolean isMetocOld(Route route) {
        if (route.getMetocForecast() == null
                || route.getMetocForecast().getCreated() == null) {
            return true;
        }
        EPDEnavSettings enavSettings = EPDShip.getSettings().getEnavSettings();
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

        synchronized (this) {
            for (Route route : routes) {
                if (route.getMetocForecast() == null) {
                    continue;
                }
                if (isMetocOld(route)
                        || !route.isMetocValid(EPDShip.getSettings()
                                .getEnavSettings().getMetocTimeDiffTolerance())) {
                    if (route.isVisible()
                            && route.getRouteMetocSettings().isShowRouteMetoc()) {
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
     * Validate if metoc is still valid for route If not METOC is removed Not
     * for active route
     */
    @Override
    public boolean validateMetoc(Route route) {
        if (route instanceof ActiveRoute) {
            return false;
        }
        if (!showMetocForRoute(route)
                || !route.isMetocValid(EPDShip.getSettings().getEnavSettings()
                        .getMetocTimeDiffTolerance())) {
            if (route.getMetocForecast() != null) {
                route.removeMetoc();
                notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
            }
            return false;
        }
        return true;
    }

    public boolean hasMetoc(Route route) {
        if (route.getMetocForecast() != null) {
            // Determine if METOC info is old
            EPDEnavSettings enavSettings = EPDShip.getSettings().getEnavSettings();
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
        try (
            FileInputStream fileIn = new FileInputStream(ROUTES_FILE);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);) {
            RouteStore routeStore = (RouteStore) objectIn.readObject();
            manager.setRoutes(routeStore.getRoutes());
            manager.activeRoute = routeStore.getActiveRoute();
            manager.activeRouteIndex = routeStore.getActiveRouteIndex();
            manager.setSuggestedRoutes(routeStore
                    .getSuggestedRoutes());

        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load routes file: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(ROUTES_FILE).delete();
        }

        return manager;
    }

    private void setSuggestedRoutes(List<RecievedRoute> suggestedRoutes) {
        synchronized (suggestedRoutes) {
            if (suggestedRoutes != null) {
                this.suggestedRoutes = suggestedRoutes;
            }
        }
    }

    private void setRoutes(List<Route> routes) {
        synchronized (this) {
            if (routes != null) {
                this.routes = routes;
            }
        }
    }

    public void saveToFile() {
        synchronized (this) {
            RouteStore routeStore = new RouteStore(this);
            try (
                FileOutputStream fileOut = new FileOutputStream(ROUTES_FILE);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);) {
                objectOut.writeObject(routeStore);
            } catch (IOException e) {
                LOG.error("Failed to save routes file: " + e.getMessage());
            }
        }
    }

    public RouteMetocSettings getDefaultRouteMetocSettings() {
        EPDEnavSettings enavSettings = EPDShip.getSettings().getEnavSettings();
        RouteMetocSettings routeMetocSettings = new RouteMetocSettings();
        routeMetocSettings.setWindWarnLimit(enavSettings
                .getDefaultWindWarnLimit());
        routeMetocSettings.setCurrentWarnLimit(enavSettings
                .getDefaultCurrentWarnLimit());
        routeMetocSettings.setWaveWarnLimit(enavSettings
                .getDefaultWaveWarnLimit());
        return routeMetocSettings;
    }

    @Override
    public void findAndInit(Object obj) {
        if (shoreServices == null && obj instanceof ShoreServicesCommon) {
            shoreServices = (ShoreServicesCommon) obj;
        }
        if (pntHandler == null && obj instanceof PntHandler) {
            pntHandler = (PntHandler) obj;
            pntHandler.addListener(this);
        }
        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
            aisHandler.addRouteSuggestionListener(this);
        }
        if (obj instanceof RouteSuggestionDialog) {
            routeSuggestionDialog = (RouteSuggestionDialog) obj;
        }
        if (obj instanceof EnavServiceHandler) {
            enavServiceHandler = (EnavServiceHandler) obj;
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (pntHandler == obj) {
            pntHandler.removeListener(this);
        }
        if (shoreServices == obj) {
            shoreServices = null;
        }
    }

    private void pollForMetoc() {
        if (!isRouteActive()) {
            return;
        }
        synchronized (this) {
            if (activeRoute.getRouteMetocSettings() == null
                    || !activeRoute.getRouteMetocSettings().isShowRouteMetoc()) {
                return;
            }
        }
        long activeRouteMetocPollInterval = EPDShip.getSettings()
                .getEnavSettings().getActiveRouteMetocPollInterval() * 60 * 1000;
        // Maybe we never want to refresh metoc
        if (activeRouteMetocPollInterval <= 0) {
            return;
        }
        // Find the age of the current METOC
        long metocAge = Long.MAX_VALUE;
        if (getActiveRoute().getMetocForecast() != null) {
            Date now = PntTime.getInstance().getDate();
            metocAge = now.getTime()
                    - getActiveRoute().getMetocForecast().getCreated()
                            .getTime();
        }
        // Check if minimum time since last update has passed
        if (metocAge <= activeRouteMetocPollInterval) {
            return;
        }        
        // Check if not old and still valid
        synchronized (this) {
            if (!isMetocOld(activeRoute)
                    && activeRoute.isMetocValid(EPDShip.getSettings().getEnavSettings().getMetocTimeDiffTolerance())) {
                return;
            }
        }

        try {
            requestRouteMetoc(getActiveRoute());
            notifyListeners(RoutesUpdateEvent.ROUTE_METOC_CHANGED);
            LOG.info("Auto updated route metoc for active route");
        } catch (ShoreServiceException e) {
            LOG.error("Failed to auto update METOC for active route: "
                    + e.getMessage());
            synchronized (this) {
                activeRoute.removeMetoc();
            }            
            notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
        }

    }

//    public void aisRouteSuggestionReply(
//            AisAdressedRouteSuggestion routeSuggestion,
//            AisAdressedRouteSuggestion.Status status) {
//        switch (status) {
//        case ACCEPTED:
//            routeSuggestion.setStatus(Status.ACCEPTED);
//            aisServices.routeSuggestionReply(routeSuggestion);
//            break;
//        case REJECTED:
//            routeSuggestion.setStatus(Status.REJECTED);
//            aisServices.routeSuggestionReply(routeSuggestion);
//            break;
//        case NOTED:
//            routeSuggestion.setStatus(Status.NOTED);
//            aisServices.routeSuggestionReply(routeSuggestion);
//            break;
//        default:
//            break;
//        }
//
//        notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
//    }

//    public Set<AisAdressedRouteSuggestion> getAddressedSuggestedRoutes() {
//        return addressedSuggestedRoutes;
//    }
    
    

    public List<RecievedRoute> getSuggestedRoutes() {
        synchronized (suggestedRoutes) {
            return new ArrayList<>(suggestedRoutes);
        }
    }
    
    

    @Override
    public void run() {

        // Maintanaince routines
        while (true) {
            Util.sleep(10000);

            // Active route poll for METOC
            pollForMetoc();

            // Check validity of METOC for all routes
            checkValidMetoc();
            
            if (isRouteActive()) {
                intendedRouteService.broadcastIntendedRoute();
//                aisServices.periodicIntendedRouteBroadcast(activeRoute);
            }

        }

    }

    public void setIntendedRouteService(
            IntendedRouteService intendedRouteService) {
        this.intendedRouteService = intendedRouteService;
        
    }

}

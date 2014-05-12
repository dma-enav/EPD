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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionStatus;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.ActiveRoute.ActiveWpSelectionResult;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.settings.handlers.MetocHandlerCommonSettings;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.component_panels.ShowDockableDialog;
import dk.dma.epd.ship.gui.component_panels.ShowDockableDialog.dock_type;
import dk.dma.epd.ship.gui.route.RouteSuggestionDialog;
import dk.dma.epd.ship.service.RouteSuggestionHandler;
import dk.dma.epd.ship.service.SuggestedRoute;
import dk.dma.epd.ship.service.SuggestedRoute.SuggestedRouteStatus;
import dk.dma.epd.ship.settings.handlers.RouteManagerSettings;

/**
 * Manager for handling a collection of routes and active route
 */
@ThreadSafe
public class RouteManager extends RouteManagerCommon implements IPntDataListener {

    private static final long serialVersionUID = -9019124285849351709L;
    private static final String ROUTES_FILE = EPD.getInstance().getHomePath().resolve(".routes").toString();
    private static final Logger LOG = LoggerFactory.getLogger(RouteManager.class);

    private volatile RouteSuggestionHandler routeSuggestionHandler;
    private volatile PntHandler pntHandler;
    
    @GuardedBy("suggestedRoutes")
    private List<SuggestedRoute> suggestedRoutes = new LinkedList<>();    
    private RouteSuggestionDialog routeSuggestionDialog;
    
    /**
     * Constructor
     */
    protected RouteManager(RouteManagerSettings<?> routeManagerSettings, MetocHandlerCommonSettings<?> metocHandlerSettings) {
        super(routeManagerSettings, metocHandlerSettings);
    }

    /**
     * Gets the {@link RouteManagerSettings} of this {@link RouteManager}.
     * @return The {@link RouteManagerSettings} of this {@link RouteManager}.
     */
    @Override
    public RouteManagerSettings<?> getRouteManagerSettings() {
        return (RouteManagerSettings<?>) super.getRouteManagerSettings();
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
            activeRoute.setWpCircleMin(getRouteManagerSettings().getMinWpRadius());
            // Set relaxed WP change
            activeRoute.setRelaxedWpChange(getRouteManagerSettings().isRelaxedWpChange());
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
    /** Route suggestion operations      **/
    /**************************************/

    /**
     * Called when a new suggested route is received via the Maritime Cloud
     * @param message the route suggestion
     */
    public void receiveRouteSuggestion(SuggestedRoute message){
        
        synchronized(suggestedRoutes){
            suggestedRoutes.add(message);            
        }
        
        // Update route layer
        notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
        
        // Show dialog
        routeSuggestionDialog.showSuggestion(message);
    }
    
    /**
     * Display the route suggestion dialog
     * @param id the index of the suggested route to display
     */
    public void showSuggestionDialog(int id){
        // Show dialog
        synchronized (suggestedRoutes) {
            routeSuggestionDialog.showSuggestion(suggestedRoutes.get(id));
        }                
    }
    
    /**
     * Accepts the given suggested route
     * @param route the suggested route to accept
     * @return if the route was accepted
     */
    public boolean acceptSuggested(SuggestedRoute route){
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
    
    /**
     * Removes the given suggested route
     * @param route the suggested route to remove
     * @return if the route was removed
     */
    public boolean removeSuggested(SuggestedRoute route){
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
    
    /**
     * Sends a route suggestion reply over the Maritime Cloud
     * @param routeSuggestion the route suggestion
     * @param status the acceptance status of the route
     * @param message an additional message
     */
    public void routeSuggestionReply(
            SuggestedRoute routeSuggestion,
            SuggestedRouteStatus status, String message) {
        

        switch (status) {
        case ACCEPTED:
            routeSuggestion.setStatus(SuggestedRouteStatus.ACCEPTED);
            acceptSuggested(routeSuggestion);
            routeSuggestionHandler.sendRouteExchangeReply(RouteSuggestionStatus.RECEIVED_ACCEPTED, routeSuggestion.getId(), message);
            break;
        case REJECTED:
            //Remove it
            routeSuggestion.setStatus(SuggestedRouteStatus.REJECTED);
            routeSuggestionHandler.sendRouteExchangeReply(RouteSuggestionStatus.RECEIVED_REJECTED, routeSuggestion.getId(), message);
            break;
        case NOTED:
            //Do nothing
            routeSuggestion.setStatus(SuggestedRouteStatus.NOTED);
            routeSuggestionHandler.sendRouteExchangeReply(RouteSuggestionStatus.RECEIVED_NOTED, routeSuggestion.getId(), message);
            break;
        default:
            break;
        }

        notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
    }
    
    /**
     * Returns a reference to the route {@linkplain RouteSuggestionDialog}
     * @return a reference to the route {@linkplain RouteSuggestionDialog}
     */
    public RouteSuggestionDialog getRouteSuggestionDialog() {
        return routeSuggestionDialog;
    }

    /**
     * Returns the list of suggested routes
     * @return the list of suggested routes
     */
    public List<SuggestedRoute> getSuggestedRoutes() {
        synchronized (suggestedRoutes) {
            return new ArrayList<>(suggestedRoutes);
        }
    }
    
    /**
     * Sets the list of suggested routes
     * @param suggestedRoutes the list of suggested routes
     */
    private void setSuggestedRoutes(List<SuggestedRoute> suggestedRoutes) {
        synchronized (suggestedRoutes) {
            if (suggestedRoutes != null) {
                this.suggestedRoutes = suggestedRoutes;
            }
        }
    }

    /**************************************/
    /** Life cycle operations            **/
    /**************************************/
    
    /**
     * Loads and instantiates a {@code RouteManager} from the 
     * default routes file.
     * @return the new route manager
     */
    public static RouteManager loadRouteManager(RouteManagerSettings<?> routeManagerSettings, MetocHandlerCommonSettings<?> metocHandlerSettings) {
        RouteManager manager = new RouteManager(routeManagerSettings, metocHandlerSettings);
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
        if (obj instanceof RouteSuggestionDialog) {
            routeSuggestionDialog = (RouteSuggestionDialog) obj;
        }
        if (obj instanceof RouteSuggestionHandler) {
            routeSuggestionHandler = (RouteSuggestionHandler) obj;
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

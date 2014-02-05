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
package dk.dma.epd.common.prototype.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.broadcast.BroadcastListener;
import net.maritimecloud.net.broadcast.BroadcastMessageHeader;
import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.enavcloud.IntendedRouteBroadcast;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;

/**
 * Intended route service implementation.
 * <p>
 * Listens for intended route broadcasts, and updates the
 * vessel target when one is received.
 */
public class IntendedRouteHandlerCommon 
    extends EnavServiceHandlerCommon {

    /**
     * Time an intended route is considered valid without update
     */
    public static final long ROUTE_TTL = 10 * 60 * 1000; // 10 min
    
    protected ConcurrentHashMap<Long, IntendedRoute> intendedRoutes = new ConcurrentHashMap<>();  
    protected List<IIntendedRouteListener> listeners = new CopyOnWriteArrayList<>();
    
    private AisHandlerCommon aisHandler;

    /**
     * Constructor
     */
    public IntendedRouteHandlerCommon() {
        super();
        
        // Update the list of intended routes every minute. Involves:
        // * Remove stale intended routes
        // * Cause the active intended routes to update (i.e. update "active" color)
        getScheduler().scheduleWithFixedDelay(new Runnable() {
            @Override public void run() {
                updateIntendedRoutes();
            }
        }, 1, 1, TimeUnit.MINUTES);
    }
    
    /**
     * Returns the intended route associated with the given MMSI
     * @param mmsi the MMSI of the intended route
     * @return the intended route or null if not present
     */
    public IntendedRoute getIntendedRoute(long mmsi) {
        return intendedRoutes.get(mmsi);
    }
    
    /**
     * Returns a copy of the current list of intended routes
     * @return a copy of the current list of intended routes
     */
    public List<IntendedRoute> fetchIntendedRoutes() {
        List<IntendedRoute> list = new ArrayList<>();
        list.addAll(intendedRoutes.values());
        return list;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        
        // Hook up as a broadcast listener
        connection.broadcastListen(IntendedRouteBroadcast.class,
                new BroadcastListener<IntendedRouteBroadcast>() {
                    public void onMessage(BroadcastMessageHeader l, IntendedRouteBroadcast r) {

                        getStatus().markCloudReception();
                        int id = MaritimeCloudUtils.toMmsi(l.getId());
                        updateIntendedRoute(id, r.getIntendedRoute());
                    }
                });
    }

    /**
     * Update intended route of vessel target
     * 
     * @param mmsi
     * @param routeData
     */
    private synchronized void updateIntendedRoute(long mmsi, Route routeData) {
        
        boolean existing = intendedRoutes.containsKey(mmsi);
        IntendedRoute intendedRoute = new IntendedRoute(routeData);
        intendedRoute.setMmsi(mmsi);
        
        // Check if this is a real intended route or one that signals a removal
        if (!intendedRoute.hasRoute()) {
            if (intendedRoutes.containsKey(mmsi)) {
                intendedRoutes.remove(mmsi);
                fireIntendedRouteRemoved(intendedRoute);
            }
            return;
        }
        
        // The intended route is valid
        intendedRoutes.put(mmsi, intendedRoute);
        
        // Sanity check
        if (aisHandler != null) {
            // Try to find exiting target
            VesselTarget vesselTarget = aisHandler.getVesselTarget(mmsi);
            if (vesselTarget != null) {
                intendedRoute.update(vesselTarget.getPositionData());
            }
        }

        // Broadcast the happy occasion
        if (existing) {
            fireIntendedRouteUpdated(intendedRoute);
        } else {
            fireIntendedRouteAdded(intendedRoute);            
        }        
    }
    
    /**
     * Update the list of intended routes every minute. Involves:
     * <ul>
     *   <li>Remove stale intended routes.</li>
     *   <li>Cause the active intended routes to update (i.e. update "active" color).</li>
     * <ul>
     */
    private synchronized void updateIntendedRoutes() {
        Date now = PntTime.getInstance().getDate();
        for (Iterator<Map.Entry<Long, IntendedRoute>> it = intendedRoutes.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Long, IntendedRoute> entry = it.next();
            if (now.getTime() - entry.getValue().getReceived().getTime() > ROUTE_TTL) {
                // Remove the intended route
                it.remove();
                fireIntendedRouteRemoved(entry.getValue());
            } else {
                // Fire an update to cause the route graphics to update
                // i.e. change color depending on receive time
                fireIntendedRouteUpdated(entry.getValue());
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof AisHandlerCommon) {
            aisHandler = (AisHandlerCommon) obj;
        }
    }
    
    /**
     * Hide all intended routes 
     */
    public final void hideAllIntendedRoutes() {
        for (IntendedRoute intendedRoute : intendedRoutes.values()) {
            intendedRoute.setVisible(false);
            fireIntendedRouteUpdated(intendedRoute);
        }
    }

    /**
     * Show all intended routes 
     */
    public final void showAllIntendedRoutes() {
        for (IntendedRoute intendedRoute : intendedRoutes.values()) {
            intendedRoute.setVisible(true);
            fireIntendedRouteUpdated(intendedRoute);
        }
    }
    

    /****************************************/
    /** Listener functions                 **/
    /****************************************/
    
    /**
     * Adds an intended route listener
     * @param listener the listener to add
     */
    public void addListener(IIntendedRouteListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes an intended route listener
     * @param listener the listener to remove
     */
    public void removeListener(IIntendedRouteListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Called when an intended route has been added
     * @param intendedRoute the intended route
     */
    public void fireIntendedRouteAdded(IntendedRoute intendedRoute) {
        for (IIntendedRouteListener listener : listeners) {
            listener.intendedRouteAdded(intendedRoute);
        }
    }

    /**
     * Called when an intended route has been updated
     * @param intendedRoute the intended route
     */
    public void fireIntendedRouteUpdated(IntendedRoute intendedRoute) {
        for (IIntendedRouteListener listener : listeners) {
            listener.intendedRouteUpdated(intendedRoute);
        }
    }
    
    /**
     * Called when an intended route has been removed
     * @param intendedRoute the intended route
     */
    public void fireIntendedRouteRemoved(IntendedRoute intendedRoute) {
        for (IIntendedRouteListener listener : listeners) {
            listener.intendedRouteRemoved(intendedRoute);
        }
    }
}

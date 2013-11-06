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
package dk.dma.epd.common.prototype.model.voyage;

import java.util.ArrayList;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.model.route.Route;

/**
 * TODO This class could possibly be changed to a VoyageManagerCommon. As of
 * now, it is only needed to fire update events to listeners, but it could just
 * as well contain more functionality like RouteManagers.
 * 
 * @author Janus Varmarken
 */
public class VoyageEventDispatcher {

    /**
     * Listeners that listen for updates on voyages.
     */
    private ArrayList<IVoyageUpdateListener> updateListeners = new ArrayList<IVoyageUpdateListener>();

    /**
     * Register a listener with this event dispatcher.
     * 
     * @param listener
     *            The listener to register.
     */
    public void registerListener(IVoyageUpdateListener listener) {
        this.updateListeners.add(listener);
    }

    /**
     * Unregister a listener with this event dispatcher.
     * 
     * @param listener
     *            The listener to unregister.
     */
    public void unregisterListener(IVoyageUpdateListener listener) {
        this.updateListeners.remove(listener);
    }

    /**
     * Notify listeners of an update to a voyage.
     * 
     * @param typeOfUpdate
     *            Specifies what kind of update this is.
     * @param updatedVoyage
     *            The voyage that was updated.
     * @param routeIndex
     *            Index used to define the voyage type (e.g. is it a STCC voyage
     *            or a modified STCC voyage)
     */
    public void notifyListenersOfVoyageUpdate(VoyageUpdateEvent typeOfUpdate,
            Route updatedVoyage, int routeIndex) {
        for (IVoyageUpdateListener listener : this.updateListeners) {
            listener.voyageUpdated(typeOfUpdate, updatedVoyage, routeIndex);
        }
    }
}

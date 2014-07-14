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
package dk.dma.epd.common.prototype.model.voyage;

import java.util.ArrayList;

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

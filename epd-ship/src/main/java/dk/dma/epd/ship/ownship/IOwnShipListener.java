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
package dk.dma.epd.ship.ownship;

import dk.dma.epd.common.prototype.ais.VesselTarget;

/**
 * Interface to implement for classes wanting to receive own-ship updates
 */
public interface IOwnShipListener {

    /**
     * Called when the own-ship has been updated
     * 
     * @param ownShipHandler
     *            the {@code OwnShipHandler}
     */
    void ownShipUpdated(OwnShipHandler ownShipHandler);

    /**
     * Invoked when own ship is changed to a new instance of
     * {@link VesselTarget}.
     * 
     * @param oldValue
     *            The {@link VesselTarget} instance previously used to model own
     *            ship data. May be null.
     * @param newValue
     *            The {@link VesselTarget} instance now used to model own ship
     *            data.
     */
    void ownShipChanged(VesselTarget oldValue, VesselTarget newValue);
}

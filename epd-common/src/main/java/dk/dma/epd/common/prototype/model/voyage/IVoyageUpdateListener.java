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

import dk.dma.epd.common.prototype.model.route.Route;

/**
 * @author Janus Varmarken
 */
public interface IVoyageUpdateListener {

    /**
     * Notify this listener of an update to a voyage.
     * 
     * @param typeOfUpdate
     *            Specifies what kind of update this is.
     * @param updatedVoyage
     *            The voyage that was updated.
     * @param routeIndex
     *            Index used to define the voyage type (e.g. is it a STCC voyage
     *            or a modified STCC voyage)
     */
    void voyageUpdated(VoyageUpdateEvent typeOfUpdate, Route updatedVoyage,
            int routeIndex);

}

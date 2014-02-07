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
package dk.dma.epd.common.prototype.enavcloud;

import java.util.Date;
import java.util.List;

import net.maritimecloud.net.broadcast.BroadcastMessage;
import dk.dma.enav.model.voyage.Route;

/**
 * Used for intended route broadcasts
 */
public class IntendedRouteBroadcast extends BroadcastMessage {

    private Route intendedRoute;
    private int activeWPIndex;
    private List<Date> originalEtas;

    /**
     * Returns the intended route being broadcasted
     * @return the intended route being broadcasted
     */
    public Route getIntendedRoute() {
        return intendedRoute;
    }

    /**
     * Sets the intended route being broadcasted
     * @param intendedRoute the intended route being broadcasted
     */
    public void setIntendedRoute(Route intendedRoute) {
        this.intendedRoute = intendedRoute;
    }

    /**
     * Returns the active way point index
     * @return the active way point index
     */
    public int getActiveWPIndex() {
        return activeWPIndex;
    }

    /**
     * Sets the active way point index
     * @param activeWPIndex the active way point index
     */
    public void setActiveWPIndex(int activeWPIndex) {
        this.activeWPIndex = activeWPIndex;
    }

    /**
     * Returns the original ETA's for the way points
     * @return the original ETA's for the way points
     */
    public List<Date> getOriginalEtas() {
        return originalEtas;
    }

    /**
     * Sets the original ETA's for the way points
     * @param originalEtas the original ETA's for the way points
     */
    public void setOriginalEtas(List<Date> originalEtas) {
        this.originalEtas = originalEtas;
    }

}

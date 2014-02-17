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
package dk.dma.epd.common.prototype.enavcloud.intendedroute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IntendedRouteMessage {

    private int activeWpIndex;
    private List<Date> plannedEtas = new ArrayList<>();
    private ArrayList<Waypoint> waypoints = new ArrayList<>();

    public IntendedRouteMessage() {

    }

    public int getActiveWpIndex() {
        return activeWpIndex;
    }

    public void setActiveWpIndex(int activeWpIndex) {
        this.activeWpIndex = activeWpIndex;
    }

    public List<Date> getPlannedEtas() {
        return plannedEtas;
    }

    public void setPlannedEtas(List<Date> plannedEtas) {
        this.plannedEtas = plannedEtas;
    }

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

}

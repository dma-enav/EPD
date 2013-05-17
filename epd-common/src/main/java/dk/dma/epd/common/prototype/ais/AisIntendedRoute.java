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
package dk.dma.epd.common.prototype.ais;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.jcip.annotations.ThreadSafe;
import dk.dma.ais.message.binary.RouteMessage;

/**
 * Class representing an AIS intended route
 */
@ThreadSafe
public class AisIntendedRoute extends AisRouteData {
    private static final long serialVersionUID = 1L;
            
    protected List<Date> etas = new ArrayList<>();    
    protected Double speed;
    protected Double activeWpRange;
    
    /**
     * Copy constructor
     * @param routeData
     */
    public AisIntendedRoute(AisIntendedRoute routeData) {
        super(routeData);
        this.etas = routeData.etas;
        this.speed = routeData.speed;
        this.activeWpRange = routeData.activeWpRange;
    }
    
    /**
     * Constructor given AIS route information
     * @param routeInformation
     */
    public AisIntendedRoute(RouteMessage routeMessage) {
        super(routeMessage);
        if (duration == 0) {
            // Cancel route
            return;
        }
        
        if (waypoints.size() == 0) {
            // Cancel route
            return;
        }
        
        // Calculate avg speed
        speed = ranges.get(waypoints.size() - 1) / (routeMessage.getDuration() / 60.0d);
        
        // ETA's
        long start = etaFirst.getTime();
        etas.add(etaFirst);
        for (int i=0; i < waypoints.size() - 1; i++) {
            double dist = ranges.get(i + 1) - ranges.get(i);
            double dur = dist / speed * 60 * 60 * 1000;
            start += dur;
            etas.add(new Date(start));
        }
        
    }
        
    /**
     * Update range to active WP given the targets new position
     * @param posData
     */
    public synchronized void update(VesselPositionData posData) {
        if (posData == null || posData.getPos() == null || waypoints.size() == 0) {
            return;
        }
        
        // Range to first wp
        activeWpRange = posData.getPos().rhumbLineDistanceTo(waypoints.get(0)) / 1852.0;
    }
    
    public synchronized Double getRange(int index) {
        if (activeWpRange == null) {
            return null;
        }
        return activeWpRange + ranges.get(index);
    }
    
    public synchronized Date getEta(int index) {
        if (index >= etas.size()) {
            return null;
        }
        return etas.get(index);
    }
        
    public synchronized Double getSpeed() {
        return speed;
    }
    
}

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

import java.io.Serializable;
import java.util.Map;


/**
 * Container class for storing AIS view as serialized object
 */
public class AisStore implements Serializable {

    private static final long serialVersionUID = 2L;
    
    private Map<Integer, AtoNTarget> atonTargets;
    private Map<Long, VesselTarget> vesselTargets;
    private Map<Long, SarTarget> sarTargets;
    private VesselTarget ownShip; 
    
    public AisStore() {
        
    }

    public Map<Integer, AtoNTarget> getAtonTargets() {
        return atonTargets;
    }

    public void setAtonTargets(Map<Integer, AtoNTarget> atonTargets) {
        this.atonTargets = atonTargets;
    }

    public Map<Long, VesselTarget> getVesselTargets() {
        return vesselTargets;
    }

    public void setVesselTargets(Map<Long, VesselTarget> vesselTargets) {
        this.vesselTargets = vesselTargets;
    }

    public Map<Long, SarTarget> getSarTargets() {
        return sarTargets;
    }

    public void setSarTargets(Map<Long, SarTarget> sarTargets) {
        this.sarTargets = sarTargets;
    }
    
    public VesselTarget getOwnShip() {
        return ownShip;
    }
    
    public void setOwnShip(VesselTarget ownShip) {
        this.ownShip = ownShip;
    }
    
}

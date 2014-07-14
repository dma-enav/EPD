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
    
    public void setOwnShip(VesselTarget ownShip) {
        this.ownShip = ownShip;
    }
    
    public VesselTarget getOwnShip() {
        return ownShip;
    }
}

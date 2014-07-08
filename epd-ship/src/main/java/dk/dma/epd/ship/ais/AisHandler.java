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
package dk.dma.epd.ship.ais;

import net.jcip.annotations.ThreadSafe;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.SensorSettings;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ownship.IOwnShipListener;
import dk.dma.epd.ship.ownship.OwnShipHandler;

/**
 * Class for handling incoming AIS messages and maintainer of AIS target tables
 */
@ThreadSafe
public class AisHandler extends AisHandlerCommon implements IOwnShipListener {

    private final double aisRange;

    /**
     * Constructor
     * @param sensorSettings
     * @param aisSettings
     */
    public AisHandler(SensorSettings sensorSettings, AisSettings aisSettings) {
        super(aisSettings);
        aisRange = sensorSettings.getAisSensorRange();
    }

    /**
     * Determine if position is within range
     * 
     * @param pos
     * @return
     */
    @Override
    protected boolean isWithinRange(Position pos) {
        if (getAisRange() <= 0) {
            return true;
        }
        PntData pntData = EPDShip.getInstance().getPntHandler().getCurrentData();
        if (pntData == null) {
            return false;
        }
        double distance = pntData.getPosition().rhumbLineDistanceTo(pos) / 1852.0;
        return distance <= aisRange;
    }

    /**
     * Returns the Ais range
     * @return the Ais range
     */
    public double getAisRange() {
        return aisRange;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof OwnShipHandler) {
            ((OwnShipHandler)obj).addListener(this);
        }
    }    
    
    /**
     * {@inheritDoc}
     */
    @Override 
    public void ownShipUpdated(OwnShipHandler ownShipHandler) { 
    }
    
    /**
     * {@inheritDoc}
     */
    @Override 
    public void ownShipChanged(VesselTarget oldValue, VesselTarget newValue) {
        clearAisTargets();
    }
}

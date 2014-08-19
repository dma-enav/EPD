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
package dk.dma.epd.ship.predictor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.ais.VesselTarget.AisClass;
import dk.dma.epd.common.prototype.predictor.DynamicPrediction;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorPredictionData;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorStateData;
import dk.dma.epd.ship.ownship.IOwnShipListener;
import dk.dma.epd.ship.ownship.OwnShipHandler;

/**
 * Simple test generator of own ship predictions
 */
public class DynamicPredictor extends MapHandlerChild implements IOwnShipListener {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicPredictor.class);
    
    private static float INTERVAL = 60.0f; // 60 secs
    private static int PREDICTIONS = 6;
    
    private volatile DynamicPredictorHandler dynamicPredictorHandler;
    
    public DynamicPredictor() {
        
    }

    @Override
    public void ownShipUpdated(OwnShipHandler ownShipHandler) {
        LOG.info("Own ship data has changed");
        if (dynamicPredictorHandler == null) {
            LOG.error("No dynamic predictor handler found");
            return;
        }
        
        // Get ais target
        VesselTarget ownShip = ownShipHandler.getComputedAisTarget();
        if (ownShip == null || ownShip.getAisClass() == AisClass.B) {
            return;
        }
        VesselPositionData posData = ownShip.getPositionData();
        VesselStaticData staticData = ownShip.getStaticData();
        if (posData == null || !posData.hasPos()) {
            return;
        }

        long t = PntTime.getDate().getTime();
        Position pos = posData.getPos();
        float cog = posData.getCog();
        float sog = posData.getSog();
        float heading = posData.getTrueHeading();
        if (heading == 511) {
            heading = cog;            
        }
        float rot = posData.getRot();
        int width = 40;
        int length = 180;
        if (staticData != null) {
            width = staticData.getDimPort() + staticData.getDimStarboard();
            length = staticData.getDimBow() + staticData.getDimStern();
        }
        
        // Generate state data
        DynamicPredictorStateData state = new DynamicPredictorStateData(PREDICTIONS, pos, heading, cog, sog, length, width, t);
        
        // Generate predictions
        List<DynamicPredictorPredictionData> predictions = new ArrayList<>();        
        for (int i=0; i < PREDICTIONS; i++) {
            double sogMpS = (sog * 1852.0) / 3600.0;
            float rotDpS = rot / 60.0f;
            double dist = sogMpS * INTERVAL;            
            pos = CoordinateSystem.CARTESIAN.pointOnBearing(pos, dist, cog);
            cog = (INTERVAL * rotDpS + cog) % 360.0f;
            heading = (INTERVAL * rotDpS + heading) % 360.0f;
            t += INTERVAL;
            
            DynamicPredictorPredictionData prediction = new DynamicPredictorPredictionData(i + 1, pos, heading, cog, sog, t);
            predictions.add(prediction);
        }
        
        // Distribute to dynamic predictor handler
        DynamicPrediction dynamicPrediction = new DynamicPrediction(ownShip.getMmsi(), state, predictions);
        dynamicPredictorHandler.ownShipDynamicPredictionChanged(dynamicPrediction);
    }

    @Override
    public void ownShipChanged(VesselTarget oldValue, VesselTarget newValue) {
        
    }
    
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        if (obj instanceof OwnShipHandler) {
            ((OwnShipHandler) obj).addListener(this);
        }
        if (obj instanceof DynamicPredictorHandler) {
            dynamicPredictorHandler = (DynamicPredictorHandler)obj;
        }
    }

}

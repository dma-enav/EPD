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

import dk.dma.epd.common.prototype.predictor.DynamicPrediction;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorData;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorPredictionData;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorStateData;
import dk.dma.epd.common.prototype.sensor.predictor.IDynamicPredictorDataListener;
import dk.dma.epd.ship.ownship.OwnShipHandler;

/**
 * The purpose of this class is to receive the individual parts (sentences) of a dynamic prediction and combine these parts into a complete
 * dynamic prediction. The complete dynamic prediction is forwarded to a {@link DynamicPredictorHandler} which is in charge of distributing
 * the dynamic prediction to interested clients.
 */
public class DynamicPredictorSentenceParser extends MapHandlerChild implements IDynamicPredictorDataListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(DynamicPredictorSentenceParser.class);
    
    private final List<DynamicPredictorPredictionData> predictions = new ArrayList<>();
    
    private volatile DynamicPredictorStateData state;
    
    private volatile DynamicPredictorHandler dynamicPredictorHandler;
    
    private volatile OwnShipHandler ownShipHandler;
    
    @Override
    public void dynamicPredictorUpdate(DynamicPredictorData dynamicPredictorData) {        
        DynamicPrediction toDistribute = null;
        synchronized (this) {            

            if (dynamicPredictorHandler == null || ownShipHandler == null) {
                // We cannot distribute sensor data if handlers are not yet available.
                return;
            }
            
            if (dynamicPredictorData instanceof DynamicPredictorStateData) {
                state = (DynamicPredictorStateData) dynamicPredictorData;
                predictions.clear();
                return;
            }

            DynamicPredictorPredictionData prediction = (DynamicPredictorPredictionData) dynamicPredictorData;
            DynamicPredictorPredictionData prev = (predictions.size() > 0) ? predictions.get(predictions.size() - 1) : null;

            if (state == null || (prev != null && (prev.getNumber() != prediction.getNumber() - 1))
                    || (prev == null && prediction.getNumber() != 1)) {
                LOG.error("Out of sequence prediction");
                return;
            }
            predictions.add(prediction);

            if (state.getCount() == prediction.getNumber()) {
                toDistribute = new DynamicPrediction(ownShipHandler.getMmsi(), state, new ArrayList<>(predictions));
                state = null;
                predictions.clear();
            }
        }

        if (toDistribute != null) {
            this.dynamicPredictorHandler.ownShipDynamicPredictionChanged(toDistribute);
        }
    }
    
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        if (obj instanceof DynamicPredictorHandler) {
            synchronized(this) {
                this.dynamicPredictorHandler = (DynamicPredictorHandler) obj;
            }
        } else if (obj instanceof OwnShipHandler) {
            synchronized(this) {
                this.ownShipHandler = (OwnShipHandler) obj;
            }
        }
    }
}

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
package dk.dma.epd.ship.predictor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.predictor.DynamicPrediction;
import dk.dma.epd.common.prototype.predictor.DynamicPredictorHandlerCommon;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorData;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorPredictionData;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorStateData;
import dk.dma.epd.common.prototype.sensor.predictor.IDynamicPredictorDataListener;
import dk.dma.epd.ship.ownship.OwnShipHandler;

/**
 * The purpose of this class is to receive the individual parts (sentences) of a dynamic prediction and combine these parts into a complete
 * dynamic prediction. The complete dynamic prediction is forwarded to a {@link DynamicPredictorHandlerCommon} which is in charge of distributing
 * the dynamic prediction to interested clients.
 */
public class DynamicPredictorSentenceParser extends MapHandlerChild implements IDynamicPredictorDataListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(DynamicPredictorSentenceParser.class);
    
    private final List<DynamicPredictorPredictionData> predictions = new ArrayList<>();
    
    private volatile DynamicPredictorStateData state;
    
    private volatile DynamicPredictorHandlerCommon dynamicPredictorHandler;
    
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
            this.dynamicPredictorHandler.receivePredictions(toDistribute);
        }
    }
    
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        if (obj instanceof DynamicPredictorHandlerCommon) {
            synchronized(this) {
                this.dynamicPredictorHandler = (DynamicPredictorHandlerCommon) obj;
            }
        } else if (obj instanceof OwnShipHandler) {
            synchronized(this) {
                this.ownShipHandler = (OwnShipHandler) obj;
            }
        }
    }
}

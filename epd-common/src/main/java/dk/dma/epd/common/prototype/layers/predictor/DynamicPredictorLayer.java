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
package dk.dma.epd.common.prototype.layers.predictor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.predictor.DynamicPrediction;
import dk.dma.epd.common.prototype.predictor.DynamicPredictorHandlerCommon;
import dk.dma.epd.common.prototype.predictor.IDynamicPredictionsListener;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorPredictionData;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorStateData;

/**
 * Layer that displays dynamic predictor data.
 * 
 * @author Ole Bakman Borup & Janus Varmarken
 * 
 */
public class DynamicPredictorLayer extends EPDLayerCommon implements
        ProjectionListener, IDynamicPredictionsListener {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
            .getLogger(DynamicPredictorLayer.class);
    
    /**
     * Maps the MMSI of a vessel to a list of graphics that visualize dynamic
     * predictor data for the vessel.
     */
    private final ConcurrentHashMap<Long, List<DynamicPredictionGraphic>> graphicMap = new ConcurrentHashMap<>();

    @Override
    public void receivePredictions(DynamicPrediction dynamicPrediction) {
        // Clear old prediction graphics for the mmsi of the new prediction.
        graphicMap.put(dynamicPrediction.getMmsi(),
                new ArrayList<DynamicPredictionGraphic>());

        DynamicPredictorStateData state = dynamicPrediction.getHeaderData();
        LOG.info("Layer received dynamic prediction: " + state);

        float vesselWidth = state.getWidth();
        float vesselLength = state.getLength();
        // Create graphics for prediction points
        float count = 0f;
        for (DynamicPredictorPredictionData prediction : dynamicPrediction
                .getPredictionDataPoints()) {
            LOG.info("Dynamic predictor data: " + prediction);
            // Position is the middle of the ship
            Position pos = prediction.getPosition();
            float heading = prediction.getHeading();
            /*
             * Base distances on the assumption that position marks the middle
             * of ship
             */
            float distBow = vesselLength / 2.0f;
            float distStern = distBow;
            float distPort = vesselWidth / 2.0f;
            float distStarboard = distPort;

            VesselPortrayalData portrayalData = new VesselPortrayalData(pos,
                    heading, distBow, distStern, distPort, distStarboard);
            DynamicPredictionGraphic dpg = new DynamicPredictionGraphic();

            dpg.update(portrayalData);
            // TODO move color selection such that own ship prediction can be
            // portrayed in different color
            float alpha = count / (dynamicPrediction.getPredictionDataPoints().size());
            Color c = new Color(0.0f, 0.5f, 0.0f, 1.0f - alpha);
            dpg.setLinePaint(c);
            dpg.setFillPaint(c);
            
            graphicMap.get(dynamicPrediction.getMmsi()).add(dpg);
            count++;
        }
        // Repaint
        doPrepare();
    }

    @Override
    public void receivePredictionTimeout(DynamicPrediction prediction) {
        // Remove graphics for timed out prediction.
        graphicMap.remove(prediction.getMmsi());
        // Repaint
        this.doPrepare();
    }

    @Override
    public synchronized OMGraphicList prepare() {
        // clear old
        graphics.clear();
        // add current
        for (Long key : this.graphicMap.keySet()) {
            graphics.addAll(graphicMap.get(key));
        }
        // Super is in charge of calling project() on graphics field.
        return super.prepare();
    }

    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        if (obj instanceof DynamicPredictorHandlerCommon) {
            ((DynamicPredictorHandlerCommon) obj).addListener(this);
        }
    }

    /*
     * TODO remove dynamic predictor listener in findAndUndo (but remember to
     * verify that this works with multiple mapframes on EPD shore)
     */
}

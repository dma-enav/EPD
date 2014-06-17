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

public class DynamicPredictorLayer extends EPDLayerCommon implements ProjectionListener, IDynamicPredictionsListener {

    private final ConcurrentHashMap<Long, List<DynamicPredictionGraphic>> graphicMap = new ConcurrentHashMap<>();
    
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger(DynamicPredictorLayer.class);

    public DynamicPredictorLayer() {

    }

    @Override
    public void receivePredictions(DynamicPrediction dynamicPrediction) {
        if (dynamicPrediction == null) {
            /*
             *  TODO as for now null means timeout. Later on in development, handler
             *  must send something else but null to indicate timeout such that layer
             *  may know exactly what dynamic prediction has timed out (e.g. if it
             *  displays dynamic predictions for other vessels than just own ship)
             */
            graphics.clear();
            return;
        }
        // clear old prediction graphics for the mmsi of the new prediction.
        graphicMap.put(dynamicPrediction.getMmsi(), new ArrayList<DynamicPredictionGraphic>());
        
        DynamicPredictorStateData state = dynamicPrediction.getHeaderData();
        LOG.info("Layer received dynamic prediction: " + state);

        float vesselWidth = state.getWidth();
        float vesselLength = state.getLength();
        
        for (DynamicPredictorPredictionData prediction : dynamicPrediction.getPredictionDataPoints()) {
            LOG.info("Dynamic predictor data: " + prediction);
            // Position is the middle of the ship
            Position pos = prediction.getPosition();
            float heading = prediction.getHeading();
            // Base distances on the assumption that pos marks the middle of ship
            float distBow = vesselLength / 2.0f;
            float distStern = distBow;
            float distPort = vesselWidth / 2.0f;
            float distStarboard = distPort;
            
            VesselPortrayalData portrayalData = new VesselPortrayalData(pos, heading, distBow, distStern, distPort, distStarboard);
            DynamicPredictionGraphic dpg = new DynamicPredictionGraphic();
            
            dpg.update(portrayalData);
            // TODO move color selection such that own ship prediction can be portrayed in different color
            dpg.setLinePaint(Color.GRAY);
            dpg.setFillPaint(Color.GRAY);
            graphicMap.get(dynamicPrediction.getMmsi()).add(dpg);
        }
               
        doPrepare();
    }
    
    @Override
    public synchronized OMGraphicList prepare() {
        // clear old
        graphics.clear();
        // add current
        for(Long key : this.graphicMap.keySet()) {
            graphics.addAll(graphicMap.get(key));
        }
        // Super is in charge of calling project() on graphics field.
        return super.prepare();
    }
    
    /**
     * TODO
     * @return
     */
    protected void onDynamicPredictionGraphicAdded(DynamicPredictionGraphic dpg) {
        
    }
    
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        if (obj instanceof DynamicPredictorHandlerCommon) {
            ((DynamicPredictorHandlerCommon) obj).addListener(this);
        }
    }
}

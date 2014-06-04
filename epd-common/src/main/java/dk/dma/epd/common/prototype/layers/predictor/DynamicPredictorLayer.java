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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.predictor.DynamicPredictorHandler;
import dk.dma.epd.common.prototype.predictor.IDynamicPredictionsListener;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorPredictionData;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorStateData;

public class DynamicPredictorLayer extends EPDLayerCommon implements ProjectionListener, IDynamicPredictionsListener {
    
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger(DynamicPredictorLayer.class);

    public DynamicPredictorLayer() {

    }

    @Override
    public void receivePredictions(DynamicPredictorStateData state, List<DynamicPredictorPredictionData> predictions) {
        LOG.info("Layer received dynamic prediction: " + state);

        if (state == null) {
            // No predictions, if we are currently not showing anything just return
            // return
            return;
        }

        state.getLength();
        state.getWidth();
        
        graphics.clear();

        for (DynamicPredictorPredictionData prediction : predictions) {
            LOG.info("Dynamic predictor data: " + prediction);
            Position pos = prediction.getPosition();
            double heading = prediction.getHeading();
            long time = prediction.getTime();

            // Position is the middle of the ship

            // Draw outlines with headings on position
            // Maybe different gray shading to differentiate
            
            
            LatLonPoint center = new LatLonPoint.Double(pos.getLatitude(), pos.getLongitude());
            double lengthRadians = Length.METER.toRadians(state.getLength());
            double headingR = Math.toRadians(prediction.getHeading());
            LatLonPoint end = center.getPoint(lengthRadians / 2, headingR);
            LatLonPoint start = center.getPoint(lengthRadians / 2, headingR + Math.PI);
            
            graphics.add(new OMLine(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude(), OMLine.LINETYPE_STRAIGHT));            
            graphics.add(new OMCircle(pos.getLatitude(), pos.getLongitude(), 10, 10));

        }
        
        doPrepare();
    }

    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        if (obj instanceof DynamicPredictorHandler) {
            ((DynamicPredictorHandler) obj).addListener(this);
        }
    }

}

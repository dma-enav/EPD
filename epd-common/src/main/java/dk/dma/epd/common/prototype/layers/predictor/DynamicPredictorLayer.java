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

import com.bbn.openmap.event.ProjectionListener;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.predictor.DynamicPredictorHandler;
import dk.dma.epd.common.prototype.predictor.IDynamicPredictionsListener;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorPredictionData;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorStateData;

public class DynamicPredictorLayer extends EPDLayerCommon implements ProjectionListener, IDynamicPredictionsListener {

    private static final long serialVersionUID = 1L;

    public DynamicPredictorLayer() {

    }

    @Override
    public void receivePredictions(DynamicPredictorStateData state, List<DynamicPredictorPredictionData> predictions) {

        System.out.println("Layer received dynamic prediction: " + state);

        if (state == null) {
            // No predictions, if we are currently not showing anything just return
            // return
            return;
        }

        state.getLength();
        state.getWidth();

        for (DynamicPredictorPredictionData prediction : predictions) {
            Position pos = prediction.getPosition();
            double heading = prediction.getHeading();
            long time = prediction.getTime();

            // Position is the middle of the ship

            // Draw outlines with headings on position
            // Maybe different gray shading to differentiate

        }

    }

    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        if (obj instanceof DynamicPredictorHandler) {
            ((DynamicPredictorHandler) obj).addListener(this);
        }
    }

}

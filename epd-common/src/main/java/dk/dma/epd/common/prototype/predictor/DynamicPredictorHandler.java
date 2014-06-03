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
package dk.dma.epd.common.prototype.predictor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorData;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorPredictionData;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorStateData;
import dk.dma.epd.common.prototype.sensor.predictor.IDynamicPredictorDataListener;

/**
 * Class for handling and distributing dynamic prediction information
 */
@ThreadSafe
public class DynamicPredictorHandler extends MapHandlerChild implements Runnable, IDynamicPredictorDataListener {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicPredictorHandler.class);

    private static final long TIMEOUT = 30 * 1000; // 30 sec

    private final CopyOnWriteArrayList<IDynamicPredictionsListener> listeners = new CopyOnWriteArrayList<>();

    private final List<DynamicPredictorPredictionData> predictions = new ArrayList<>();
    private volatile DynamicPredictorStateData state;
    private volatile long lastPrediction;

    public DynamicPredictorHandler() {
        EPD.startThread(this, "DynamicPredictorHandler");
    }

    @Override
    public void dynamicPredictorUpdate(DynamicPredictorData dynamicPredictorData) {
        List<DynamicPredictorPredictionData> toDistribute = null;        
        DynamicPredictorStateData s = null;
        
        synchronized (this) {

            if (dynamicPredictorData instanceof DynamicPredictorStateData) {
                state = (DynamicPredictorStateData) dynamicPredictorData;
                predictions.clear();
                return;
            }

            DynamicPredictorPredictionData prediction = (DynamicPredictorPredictionData) dynamicPredictorData;
            DynamicPredictorPredictionData prev = (predictions.size() > 0) ? predictions.get(predictions.size() - 1) : null;

            if (state == null || (prev != null && (prev.getNumber() != prediction.getNumber() + 1))
                    || (prev == null && prediction.getNumber() != 1)) {
                LOG.error("Out of sequence prediction");
                return;
            }
            predictions.add(prediction);

            if (state.getCount() == prediction.getNumber()) {
                lastPrediction = System.currentTimeMillis();
                toDistribute = new ArrayList<>(predictions);
                s = state;
                state = null;
                predictions.clear();
            }

        }

        if (toDistribute != null) {
            for (IDynamicPredictionsListener listener : listeners) {
                listener.receivePredictions(s, toDistribute);
            }
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            // Distribute timeout
            if (System.currentTimeMillis() - lastPrediction > TIMEOUT) {
                for (IDynamicPredictionsListener listener : listeners) {
                    listener.receivePredictions(null, null);
                }
            }
        }

    }

    public void addListener(IDynamicPredictionsListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IDynamicPredictionsListener listener) {
        listeners.remove(listener);
    }

}

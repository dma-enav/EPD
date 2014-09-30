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

import dk.dma.epd.common.prototype.predictor.DynamicPrediction;
import dk.dma.epd.common.prototype.predictor.DynamicPredictorHandlerCommon;
import net.maritimecloud.mms.MmsClient;

/**
 * Extends {@link DynamicPredictorHandlerCommon} with functionality that allows
 * for reception of dynamic predictor data from the own ship dynamic predictor
 * sensor as well as broadcast of this data through the Maritime Cloud.
 * 
 * @author Janus Varmarken
 */
public class DynamicPredictorHandler extends DynamicPredictorHandlerCommon {

    /**
     * Time interval between each broadcast of own ship dynamic prediction.
     * Unit: milliseconds.
     */
    public static final long DYNAMIC_PREDICTION_BROADCAST_INTERVAL = 10000L;

    /**
     * The most recent dynamic prediction for own ship.
     */
    private volatile DynamicPrediction latestOwnShipPrediction;

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(final MmsClient connection) {
        /*
         * Let super register as broadcast listener of dynamic predictions from
         * other vessels.
         */
// TODO: Maritime Cloud 0.2 re-factoring
//        super.cloudConnected(connection);
//        // Prepare broadcast of own ship dynamic prediction.
//        ConnectedRunnableWrapper job = new ConnectedRunnableWrapper(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        /*
//                         * Use a local reference to allow null check without a
//                         * field lock.
//                         */
//                        DynamicPrediction toBroadcast = DynamicPredictorHandler.this.latestOwnShipPrediction;
//                        // If no prediction or timed out prediction, we don't
//                        // broadcast
//                        if (toBroadcast == null
//                                || !DynamicPredictorHandler.this
//                                        .isDynamicPredictionValid(DynamicPredictorHandler.this.latestOwnShipPrediction)) {
//                            return;
//                        }
//                        connection.broadcast(toBroadcast);
//                    }
//                });
//        // Schedule broadcast to run repeatedly.
//        getScheduler().scheduleWithFixedDelay(job,
//                DYNAMIC_PREDICTION_BROADCAST_INTERVAL,
//                DYNAMIC_PREDICTION_BROADCAST_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * Updates this handler with a new dynamic prediction for own ship.
     * 
     * @param newPrediction
     *            The new dynamic prediction for own ship.
     */
    public void ownShipDynamicPredictionChanged(DynamicPrediction newPrediction) {
        // Store in field for repeated broadcast to access.
        this.latestOwnShipPrediction = newPrediction;
        // Publish own ship prediction to local listeners.
        this.publishDynamicPrediction(newPrediction);
    }
}

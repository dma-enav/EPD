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

import java.util.concurrent.TimeUnit;

import net.maritimecloud.net.MaritimeCloudClient;
import dk.dma.epd.common.prototype.predictor.DynamicPrediction;
import dk.dma.epd.common.prototype.predictor.DynamicPredictorHandlerCommon;

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
    public void cloudConnected(final MaritimeCloudClient connection) {
        /*
         * Let super register as broadcast listener of dynamic predictions from
         * other vessels.
         */
        super.cloudConnected(connection);
        // Prepare broadcast of own ship dynamic prediction.
        ConnectedRunnableWrapper job = new ConnectedRunnableWrapper(
                new Runnable() {
                    @Override
                    public void run() {
                        /*
                         * Use a local reference to allow null check without a
                         * field lock.
                         */
                        DynamicPrediction toBroadcast = DynamicPredictorHandler.this.latestOwnShipPrediction;
                        // If no prediction or timed out prediction, we don't
                        // broadcast
                        if (toBroadcast == null
                                || !DynamicPredictorHandler.this
                                        .isDynamicPredictionValid(DynamicPredictorHandler.this.latestOwnShipPrediction)) {
                            return;
                        }
                        connection.broadcast(toBroadcast);
                    }
                });
        // Schedule broadcast to run repeatedly.
        getScheduler().scheduleWithFixedDelay(job,
                DYNAMIC_PREDICTION_BROADCAST_INTERVAL,
                DYNAMIC_PREDICTION_BROADCAST_INTERVAL, TimeUnit.MILLISECONDS);
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

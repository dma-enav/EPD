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

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jcip.annotations.ThreadSafe;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.broadcast.BroadcastListener;
import net.maritimecloud.net.broadcast.BroadcastMessageHeader;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;

/**
 * Class for handling and distributing dynamic prediction information. Clients
 * can receive notifications by implementing {@link IDynamicPredictionsListener}
 * and registering as an observer of a {@link DynamicPredictorHandlerCommon}.
 * 
 * @author Ole Bakman Borup & Janus Varmarken
 */
@ThreadSafe
public class DynamicPredictorHandlerCommon extends EnavServiceHandlerCommon {

    /**
     * Validity duration of a dynamic prediction. Unit: milliseconds.
     */
    public static final long TIMEOUT = 30 * 1000;

    /**
     * Maps an MMSI to its corresponding (most recent)
     * {@link DynamicPredictionTimeoutTask}.
     */
    private final ConcurrentHashMap<Long, DynamicPredictionTimeoutTask> scheduledTimeouts = new ConcurrentHashMap<>();

    /**
     * Supports delayed invocation of {@link DynamicPredictionTimeoutTask}s.
     */
    private final Timer timeoutScheduler = new Timer(true);

    /**
     * List of listeners that want to receive dynamic prediction updates from
     * this handler.
     */
    private final CopyOnWriteArrayList<IDynamicPredictionsListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Listen for dynamic prediction updates from this handler. Remember to call
     * {@link #removeListener(IDynamicPredictionsListener)} when the added
     * listener is no longer in use to allow for garbage collection of the
     * listener.
     * 
     * @param listener
     *            The object that is to receive dynamic prediction updates.
     */
    public void addListener(IDynamicPredictionsListener listener) {
        if (listener == this) {
            throw new IllegalArgumentException(
                    "Cannot add self as observer of self.");
        }
        listeners.addIfAbsent(listener);
    }

    /**
     * Stop listening for dynamic predictions from this handler.
     * 
     * @param listener
     *            The object that should no longer receive dynamic prediction
     *            updates.
     */
    public void removeListener(IDynamicPredictionsListener listener) {
        listeners.remove(listener);
    }

    /**
     * Checks if a {@link DynamicPrediction} is considered valid (i.e. hasn't
     * timed out) by this handler. The {@link DynamicPrediction} is valid if its
     * header time stamp plus {@link #TIMEOUT} is later than the current PNT
     * time.
     * 
     * @param prediction
     *            The prediction to check for validity.
     * @return {@code true} if the prediction has <b>not</b> timed out,
     *         {@code false} if the prediction has timed out.
     */
    public boolean isDynamicPredictionValid(DynamicPrediction prediction) {
        return prediction.getHeaderData().getTime() + TIMEOUT > PntTime
                .getInstance().getDate().getTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        super.cloudConnected(connection);
        // Listen for dynamic prediction broadcasts from cloud
        connection.broadcastListen(DynamicPrediction.class,
                new BroadcastListener<DynamicPrediction>() {
                    @Override
                    public void onMessage(BroadcastMessageHeader header,
                            DynamicPrediction prediction) {
                        assert MaritimeCloudUtils.toMmsi(header.getId()) == prediction
                                .getMmsi();
                        /*
                         * Notify listeners of dynamic prediction received from
                         * cloud.
                         */
                        DynamicPredictorHandlerCommon.this
                                .publishDynamicPrediction(prediction);
                    }
                });
    }

    /**
     * Publishes a {@link DynamicPrediction} to all registered listeners.
     * 
     * @param prediction
     *            The dynamic prediction to publish.
     */
    protected void publishDynamicPrediction(DynamicPrediction prediction) {
        // Any pending timeouts for this MMSI?
        TimerTask scheduled = this.scheduledTimeouts.get(prediction.getMmsi());
        if (scheduled != null) {
            // Found a pending timeout.
            // Remove it as timeout is refreshed by the current prediction.
            /*
             * Cancel task so that it won't cause a timeout prediction to be
             * fired.
             */
            scheduled.cancel();
            // Remove from map
            this.scheduledTimeouts.remove(prediction.getMmsi());
            /*
             * Perform cleanup: remove cancelled tasks from timer queue such
             * that they become eligible for garbage collection. Note: this
             * trades time for space, see documentation for purge().
             */
            this.timeoutScheduler.purge();
        }
        // Create pending timeout for current prediction
        DynamicPredictionTimeoutTask pendingTimeout = new DynamicPredictionTimeoutTask(
                prediction);
        // Schedule timeout task to run if timeout occurs.
        this.timeoutScheduler.schedule(pendingTimeout, TIMEOUT);
        // Store reference to timeout task to allow cancellation.
        this.scheduledTimeouts.put(prediction.getMmsi(), pendingTimeout);
        // Publish the current prediction to listeners.
        for (IDynamicPredictionsListener listener : this.listeners) {
            listener.receivePredictions(prediction);
        }
    }

    /**
     * Publish that a {@link DynamicPrediction} has timed out.
     * 
     * @param timedOutPrediction
     *            The {@link DynamicPrediction} that has timed out.
     */
    private void publishTimeout(DynamicPrediction timedOutPrediction) {
        for (IDynamicPredictionsListener listener : this.listeners) {
            listener.receivePredictionTimeout(timedOutPrediction);
        }
    }

    /**
     * A task that publishes timeout of a {@link DynamicPrediction}. Using a
     * {@link Timer}, clients can schedule the task to be run when the timeout
     * occurs.
     * 
     * @author Janus Varmarken
     * 
     */
    private class DynamicPredictionTimeoutTask extends TimerTask {

        /**
         * The prediction that this task will publish timeout for.
         */
        private final DynamicPrediction prediction;

        /**
         * Creates a new {@link DynamicPredictionTimeoutTask}.
         * 
         * @param prediction
         *            The prediction that this task will publish timeout for.
         */
        public DynamicPredictionTimeoutTask(DynamicPrediction prediction) {
            this.prediction = Objects.requireNonNull(prediction);
        }

        /**
         * Publishes the timeout to listeners.
         */
        @Override
        public void run() {
            /*
             * If timeout occurs, we need to remove it from map to allow for
             * garbage collection.
             */
            DynamicPredictorHandlerCommon.this.scheduledTimeouts.remove(
                    prediction.getMmsi(), this);
            /*
             * And now we do the actual work, i.e. we publish the timeout to
             * listeners.
             */
            DynamicPredictorHandlerCommon.this.publishTimeout(prediction);
        }

    }
}

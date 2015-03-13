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
package dk.dma.epd.common.prototype.service;

import com.bbn.openmap.MapHandlerChild;
import dk.dma.epd.common.prototype.service.MaritimeCloudService.IMaritimeCloudListener;
import dk.dma.epd.common.prototype.status.CloudStatus;
import net.maritimecloud.net.mms.MmsClient;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Abstract base for all e-Navigation services.
 * <p>
 * The base implementation will hook up to the {@linkplain MaritimeCloudService} and register as a listener
 */
public abstract class EnavServiceHandlerCommon extends MapHandlerChild implements IMaritimeCloudListener {

    protected MaritimeCloudService maritimeCloudService;
    private ScheduledExecutorService scheduler;
    private final int schedulerPoolSize;

    /**
     * Constructor
     */
    protected EnavServiceHandlerCommon() {
        this(1);
    }

    /**
     * Constructor
     * 
     * @param schedulerPoolSize
     *            the scheduler core pool size
     */
    protected EnavServiceHandlerCommon(int schedulerPoolSize) {
        this.schedulerPoolSize = schedulerPoolSize;
    }

    /**
     * Returns a reference to the {@linkplain MaritimeCloudService}
     * 
     * @return a reference to the {@linkplain MaritimeCloudService}
     */
    @SuppressWarnings("unused")
    public synchronized MaritimeCloudService getMaritimeCloudService() {
        return maritimeCloudService;
    }

    /**
     * Returns a reference to the cloud client connection
     * 
     * @return a reference to the cloud client connection
     */
    public synchronized MmsClient getMmsClient() {
        return (maritimeCloudService == null) ? null : maritimeCloudService.getConnection();
    }

    /**
     * Returns a reference to the cloud status
     * 
     * @return a reference to the cloud status
     */
    public synchronized CloudStatus getStatus() {
        return (maritimeCloudService == null) ? null : maritimeCloudService.getStatus();
    }

    /**
     * Returns if there is a live connection to the Maritime Cloud
     * 
     * @return if there is a live connection to the Maritime Cloud
     */
    public synchronized boolean isConnected() {
        // Consider using the isClosed()/isConnected methods of the connection
        return maritimeCloudService != null && maritimeCloudService.isConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (obj instanceof MaritimeCloudService) {
            maritimeCloudService = (MaritimeCloudService) obj;
            maritimeCloudService.addListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndUndo(Object obj) {

        if (obj instanceof MaritimeCloudService) {
            maritimeCloudService.removeListener(this);
            maritimeCloudService = null;
        }

        super.findAndUndo(obj);
    }

    /**
     * Will clean up the e-Navigation service
     */
    public synchronized void shutdown() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    /****************************************/
    /** Scheduler functions **/
    /****************************************/

    /**
     * Returns the scheduler associated with this e-Navigation service. The scheduler is created the first time this method is
     * called.
     * 
     * @return the scheduler
     */
    protected synchronized ScheduledExecutorService getScheduler() {
        if (scheduler == null) {
            scheduler = Executors.newScheduledThreadPool(schedulerPoolSize);
        }
        return scheduler;
    }

    /**
     * Schedules the given command to be run periodically in a separate thread.
     * <p>
     * The command is wrapper so that it will only actually be run when there is a live connection to the maritime cloud.
     * 
     * @param command
     *            the command to schedule
     * @param initialDelay
     *            the initial delay in {@linkplain TimeUnit}
     * @param delay
     *            the subsequent delay in {@linkplain TimeUnit}
     * @param unit
     *            the {@linkplain TimeUnit} for the delays
     * @return a {@linkplain ScheduledFuture} representing pending completion of the task
     */
    protected ScheduledFuture<?> scheduleWithFixedDelayWhenConnected(final Runnable command, long initialDelay, long delay,
            TimeUnit unit) {
        return getScheduler().scheduleWithFixedDelay(new ConnectedRunnableWrapper(command), initialDelay, delay, unit);
    }

    /**
     * Submits the given command for execution in a separate thread.
     * <p>
     * The command is wrapper so that it will only actually be run if there is a live connection to the maritime cloud at time of
     * execution.
     * 
     * @param command
     *            the command to submit
     * @return a {@linkplain Future} representing pending completion of the task
     */
    protected Future<?> submitIfConnected(final Runnable command) {
        return getScheduler().submit(new ConnectedRunnableWrapper(command));
    }

    /****************************************/
    /** IMaritimeCloudListener functions **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MmsClient connection) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudDisconnected() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudError(String error) {
    }

    /****************************************/
    /** Helper classes                     **/
    /****************************************/

    /**
     * Defines the statuses of a cloud message. Since some of the status can arrive out of order from the cloud, introduce an order
     * to the statuses and a method for combining them.
     */
    public enum CloudMessageStatus {
        NOT_SENT(0, "Not sent - check network status"), 
        SENT(1, "Sent"), 
        SENT_FAILED(2, "Failed to send message"), 
        RECEIVED_BY_CLOUD(3, "Sent and received by cloud"), 
        RECEIVED_BY_CLIENT(4, "Sent and received by client"), 
        HANDLED_BY_CLIENT(5, "Sent and acknowledged by client");

        String title;
        int order;

        private CloudMessageStatus(int order, String title) {
            this.title = title;
            this.order = order;
        }

        public String getTitle() {
            return title;
        }

        /**
         * Ensures that we do not "downgrade" a status when the updates arrives out-of-order
         * 
         * @param other
         *            the cloud message status to combine with
         * @return the combined cloud message status
         */
        public CloudMessageStatus combine(CloudMessageStatus other) {
            return (other == null || order >= other.order) ? this : other;
        }
    }

    /**
     * Simple wrapper of the {@linkplain Runnable} interface that only executes the wrapped runnable if there is a live connection
     * to the maritime cloud.
     */
    public class ConnectedRunnableWrapper implements Runnable {

        Runnable runnable;

        /**
         * Constructor
         * 
         * @param runnable
         *            the wrapped runnable
         */
        public ConnectedRunnableWrapper(Runnable runnable) {
            this.runnable = runnable;
        }

        /**
         * Runnable run method.
         */
        @Override
        public void run() {
            // Only execute wrapped runnable if connected
            if (isConnected()) {
                runnable.run();
            }
        }
    }
}

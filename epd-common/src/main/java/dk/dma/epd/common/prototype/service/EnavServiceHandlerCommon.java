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
import dk.dma.epd.common.prototype.enavcloud.TODO;
import dk.dma.epd.common.prototype.service.MaritimeCloudService.IMaritimeCloudListener;
import dk.dma.epd.common.prototype.status.CloudStatus;
import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.net.mms.MmsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * Abstract base for all e-Navigation services.
 * <p>
 * The base implementation will hook up to the {@linkplain MaritimeCloudService}
 * and register as a listener
 */
public abstract class EnavServiceHandlerCommon extends MapHandlerChild implements IMaritimeCloudListener {
   
    private static final Logger LOG = LoggerFactory.getLogger(EnavServiceHandlerCommon.class);
    
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
     * @param schedulerPoolSize the scheduler core pool size
     */
    protected EnavServiceHandlerCommon(int schedulerPoolSize) {
        this.schedulerPoolSize = schedulerPoolSize;
    }
    
    /**
     * Returns a reference to the {@linkplain MaritimeCloudService}
     * @return a reference to the {@linkplain MaritimeCloudService}
     */
    public synchronized MaritimeCloudService getMaritimeCloudService() {
        return maritimeCloudService;
    }
    
    /**
     * Returns a reference to the cloud client connection
     * @return a reference to the cloud client connection
     */
    public synchronized MmsClient getMmsClient() {
        return (maritimeCloudService == null) ? null : maritimeCloudService.getConnection();
    }
    
    /**
     * Returns a reference to the cloud status 
     * @return a reference to the cloud status 
     */
    public synchronized CloudStatus getStatus() {
        return (maritimeCloudService == null) ? null : maritimeCloudService.getStatus();
    }
    
    
    /**
     * Returns if there is a live connection to the Maritime Cloud
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
            maritimeCloudService = (MaritimeCloudService)obj;
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
    /** Scheduler functions                **/
    /****************************************/
    
    /**
     * Returns the scheduler associated with this e-Navigation service.
     * The scheduler is created the first time this method is called.
     * @return
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
     * The command is wrapper so that it will only actually be run when there
     * is a live connection to the maritime cloud.
     * 
     * @param command the command to schedule
     * @param initialDelay the initial delay in {@linkplain TimeUnit}
     * @param delay the subsequent delay in {@linkplain TimeUnit}
     * @param unit the {@linkplain TimeUnit} for the delays
     * @return a {@linkplain ScheduledFuture} representing pending completion of the task
     */
    protected ScheduledFuture<?> scheduleWithFixedDelayWhenConnected(final Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return getScheduler().scheduleWithFixedDelay(
                new ConnectedRunnableWrapper(command), 
                initialDelay, 
                delay, 
                unit);
    }
    
    /**
     * Submits the given command for execution in a separate thread.
     * <p>
     * The command is wrapper so that it will only actually be run if there
     * is a live connection to the maritime cloud at time of execution.
     * 
     * @param command the command to submit
     * @return a {@linkplain Future} representing pending completion of the task
     */
    protected Future<?> submitIfConnected(final Runnable command) {
        return getScheduler().submit(new ConnectedRunnableWrapper(command));
    }
    
    /****************************************/
    /** IMaritimeCloudListener functions   **/
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
    /** Maritime Cloud messaging           **/
    /****************************************/    

    /**
     * Sends the {@code message} to the service endpoint with the given {@code id} in the {@code serviceList}.
     * <p>
     * If the service endpoint is not found, this method will do nothing
     *  
     * @param serviceList the list of service endpoints
     * @param id the maritime id of the service endpoint
     * @param message the message to send
     * @param statusListener if not {@code null}, the listener will be updated with the message status
     * @return if the message was submitted to an endpoint
     */
    protected <M extends TODO.ServiceMessage<R>, R> boolean sendMaritimeCloudMessage(List<TODO.ServiceEndpoint<M, R>> serviceList, MaritimeId id, M message,
            ICloudMessageListener<M, R> statusListener) {

        return true;
// TODO: Maritime Cloud 0.2 re-factoring
//        // Look up the service endpoint
//        ServiceEndpoint<M, R> endpoint = MaritimeCloudUtils.findServiceWithId(serviceList, id);
//
//        if (endpoint != null) {
//            // Send the message
//            return sendMaritimeCloudMessage(endpoint, message, statusListener);
//
//        } else {
//            LOG.error("No Maritime Cloud service endpoint. Message skipped: " + message);
//            return false;
//        }

    }
    
    /**
     * Sends the {@code message} to the give service {@code endpoint}.
     *  
     * @param endpoint the service endpoints
     * @param message the message to send
     * @param statusListener if not {@code null}, the listener will be updated with the message status
     * @return if the message was submitted to an endpoint
     */
    protected <M extends TODO.ServiceMessage<R>, R> boolean sendMaritimeCloudMessage(TODO.ServiceEndpoint<M, R> endpoint, final M message, final ICloudMessageListener<M, R> statusListener) {
        
        if (endpoint == null) {
            return false;
        }

        return true;
// TODO: Maritime Cloud 0.2 re-factoring
//        // Send the message
//        ServiceInvocationFuture<R> f = endpoint.invoke(message);
//        registerStatusListener(f, message, statusListener);
//
//        LOG.info("Sent Maritime Cloud message: " +  message);
//        return true;
    }

    /**
     * Sends the {@code message} to the service with the given id.
     *  
     * @param id the service id
     * @param message the message to send
     * @param statusListener if not {@code null}, the listener will be updated with the message status
     * @return if the message was submitted to an endpoint
     */
    protected <M extends TODO.ServiceMessage<R>, R> boolean sendMaritimeCloudMessage(MaritimeId id, final M message, final ICloudMessageListener<M, R> statusListener) {

        return true;
// TODO: Maritime Cloud 0.2 re-factoring
//        if (id == null) {
//            return false;
//        }
//
//        // Send the message
//        ServiceInvocationFuture<R> f = getMmsClient().serviceInvoke(id, message);
//        registerStatusListener(f, message, statusListener);
//
//        LOG.info("Sent Maritime Cloud message: " +  message);
//        return true;
    }

    /**
     * Registers a status listener on the service invocation future
     * 
     * @param f the the service invocation future
     * @param message the message 
     * @param statusListener the status listener
     */
    private <M, R>  void registerStatusListener(TODO.ServiceInvocationFuture<R> f, final M message, final ICloudMessageListener<M, R> statusListener) {
        if (statusListener != null) {

// TODO: Maritime Cloud 0.2 re-factoring
//            // Register a consumer that will be called when the recipient has completed the request
//            f.handle(new BiConsumer<R, Throwable>() {
//                @Override
//                public void accept(R reply, Throwable r) {
//                    statusListener.messageHandled(message, reply);
//                }
//            });
//
//            // Register a consumer that will be called when the Maritime Cloud has received the message
//            f.receivedByCloud().handle(new BiConsumer<Object, Throwable>() {
//                @Override
//                public void accept(Object l, Throwable r) {
//                    statusListener.messageReceivedByCloud(message);
//                }
//            });
        }
    }
    
    /****************************************/
    /** Helper classes                     **/
    /****************************************/
    
    /**
     * Defines the statuses of a cloud message. Since some of the
     * status can arrive out of order from the cloud, introduce an
     * order to the statuses and a method for combining them.
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
        
        public String getTitle() { return title; }
        
        /**
         * Ensures that we do not "downgrade" a status when the updates 
         * arrives out-of-order
         * @param other the cloud message status to combine with
         * @return the combined cloud message status
         */
        public CloudMessageStatus combine(CloudMessageStatus other) {
            return (other == null || order >= other.order) ? this : other;
        }
    }
    
    /**
     * Can be implemented by status listeners passed along to the 
     * {@code sendMaritimeCloudMessage()} function
     */
    public interface ICloudMessageListener<M, R> {
        
        /**
         * Called when the message is received by the cloud
         * @param message the maritime cloud message
         */
        void messageReceivedByCloud(M message);
        
        /**
         * Called when the message has been handled by the client
         * @param message the maritime cloud message
         * @param reply the reply
         */
        void messageHandled(M message, R reply);
    }
    
    /**
     * Simple wrapper of the {@linkplain Runnable} interface
     * that only executes the wrapped runnable if there
     * is a live connection to the maritime cloud.
     */
    public class ConnectedRunnableWrapper implements Runnable {
        
        Runnable runnable;
        
        /**
         * Constructor
         * @param runnable the wrapped runnable
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

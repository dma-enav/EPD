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
package dk.dma.epd.common.prototype.service;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.bbn.openmap.MapHandlerChild;

import net.maritimecloud.net.MaritimeCloudClient;
import dk.dma.epd.common.prototype.service.MaritimeCloudServiceCommon.IMaritimeCloudListener;
import dk.dma.epd.common.prototype.status.CloudStatus;


/**
 * Abstract base for all e-Navigation services.
 * <p>
 * The base implementation will hook up to the {@linkplain MaritimeCloudServiceCommon}
 * and register as a listener
 */
public abstract class EnavServiceHandlerCommon extends MapHandlerChild implements IMaritimeCloudListener {
    
    protected MaritimeCloudServiceCommon maritimeCloudService;
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
     * Returns a reference to the {@linkplain MaritimeCloudServiceCommon}
     * @return a reference to the {@linkplain MaritimeCloudServiceCommon}
     */
    public synchronized MaritimeCloudServiceCommon getMaritimeCloudService() {
        return maritimeCloudService;
    }
    
    /**
     * Returns a reference to the cloud client connection
     * @return a reference to the cloud client connection
     */
    public synchronized MaritimeCloudClient getMaritimeCloudConnection() {
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
        
        if (obj instanceof MaritimeCloudServiceCommon) {
            maritimeCloudService = (MaritimeCloudServiceCommon)obj;
            maritimeCloudService.addListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndUndo(Object obj) {        
        
        if (obj instanceof MaritimeCloudServiceCommon) {
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
    public void cloudConnected(MaritimeCloudClient connection) {
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
     * Simple wrapper of the {@linkplain Runnable} interface
     * that only executes the wrapped runnable if there
     * is a live connection to the maritime cloud.
     */
    class ConnectedRunnableWrapper implements Runnable {
        
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

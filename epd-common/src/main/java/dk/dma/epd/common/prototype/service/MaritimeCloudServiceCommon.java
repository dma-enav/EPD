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

import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.net.ClosingCode;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.MaritimeCloudClientConfiguration;
import net.maritimecloud.net.MaritimeCloudConnection;
import net.maritimecloud.util.geometry.PositionReader;
import net.maritimecloud.util.geometry.PositionTime;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.settings.EnavSettings;
import dk.dma.epd.common.prototype.status.CloudStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.util.Util;

/**
 * Service that provides an interface to the Maritime Cloud connection.
 * <p>
 * For technical reasons, each application should only have 
 * one live connection to the maritime cloud.<br/> 
 * The purpose of the {@code MaritimeCloudServiceCommon} is to be the only 
 * access point to this service.
 * <p>
 * Clients of this service should hook up a listeners to be notified 
 * when the service is running or stopped.
 * <p>
 * Future improvements:
 * <ul>
 *   <li>Perform listener tasks in a thread pool</li>
 * </ul>
 */
public abstract class MaritimeCloudServiceCommon 
    extends MapHandlerChild 
    implements Runnable, IStatusComponent {

    /** 
     * Set this flag to true, if you want to log all 
     * messages sent and received by the {@linkplain MaritimeCloudClient} 
     */
    private static final boolean LOG_MARITIME_CLOUD_ACTIVITY = false;
    private static final int MARITIME_CLOUD_SLEEP_TIME = 10000;

    private static final Logger LOG = LoggerFactory.getLogger(MaritimeCloudServiceCommon.class);

    protected MaritimeCloudClient connection;
    
    protected List<IMaritimeCloudListener> listeners = new CopyOnWriteArrayList<>();
    protected CloudStatus cloudStatus = new CloudStatus();
    protected String hostPort;
    protected boolean stopped = true;
    
    /**
     * Constructor 
     */
    public MaritimeCloudServiceCommon() {
    }

    /**
     * Reads the e-Navigation settings for connection parameters
     */
    protected void readEnavSettings() {
        EnavSettings enavSettings = EPD.getInstance().getSettings().getEnavSettings();
        
        this.hostPort = String.format("%s:%d",
                enavSettings.getCloudServerHost(),
                enavSettings.getCloudServerPort());
    }
    
    /**
     * Returns a reference to the cloud client connection
     * @return a reference to the cloud client connection
     */
    public MaritimeCloudClient getConnection() {
        return connection;
    }

    /**
     * Returns the maritime id to connect with
     * @return the maritime id to connect with
     */
    public abstract MaritimeId getMaritimeId();
    
    /**
     * Returns the current position
     * @return the current position
     */
    public abstract Position getCurrentPosition();

    /**
     * Returns the cloud status
     */
    @Override
    public CloudStatus getStatus() {
        return cloudStatus;
    }

    /*********************************/
    /** Life cycle functionality    **/
    /*********************************/

    /**
     * Starts the Maritime cloud client
     */
    public void start() {
        if (!stopped) {
            return;
        }
        // Update the eNav settings
        readEnavSettings();
        stopped = false;
        new Thread(this).start();
    }
    
    /**
     * Stops the Maritime cloud client
     */
    public synchronized void stop() {
        if (stopped) {
            return;
        }
        
        this.stopped = true;
        if (connection != null) {
            try {
                connection.close();
                connection.awaitTermination(2, TimeUnit.SECONDS);
            } catch (Exception e) {
                LOG.error("Error terminating cloud connection");
            }
            connection = null;
        }
    }
    
    /**
     * Thread run method
     */
    @Override
    public void run() {

        // Start by connecting 
        while (!stopped) {
            Util.sleep(MARITIME_CLOUD_SLEEP_TIME);
            if (getMaritimeId() != null) {
                if (initConnection(hostPort, getMaritimeId())) {
                    try {
                        fireConnected(connection);
                    } catch (Exception e) {
                        cloudStatus.markFailedSend();
                        cloudStatus.markFailedReceive();
                        fireError(e.getMessage());
                    }
                    break;
                }
            }
        }
        
        // Periodic tasks
        while (!stopped) {
            firePeriodicTask();
            
            cloudStatus.markCloudReception();
            Util.sleep(MARITIME_CLOUD_SLEEP_TIME);
        }
        
        // Flag that we are stopped
        fireDisconnected();
    }

    /**
     * Create the Maritime Cloud connection
     */
    private boolean initConnection(String host, MaritimeId id) {
        LOG.info("Connecting to cloud server: " + host  + " with maritime id " + id);

        MaritimeCloudClientConfiguration enavCloudConnection = MaritimeCloudClientConfiguration.create(id);

        // Hook up a position reader
        enavCloudConnection.setPositionReader(new PositionReader() {
            @Override public PositionTime getCurrentPosition() {
                long now = System.currentTimeMillis();
                Position pos =  MaritimeCloudServiceCommon.this.getCurrentPosition();
                if (pos != null) {
                    return PositionTime.create(pos.getLatitude(), pos.getLongitude(), now);
                } else {
                    return PositionTime.create(0.0, 0.0, System.currentTimeMillis());
                }
            }});
        
        // Check if we need to log the MaritimeCloudConnection activity
        if (LOG_MARITIME_CLOUD_ACTIVITY) {
            enavCloudConnection.addListener(new MaritimeCloudConnection.Listener() {
                @Override public void messageReceived(String message) {
                    LOG.info("Received:" + message);
                }
    
                @Override public void messageSend(String message) {
                    LOG.info("Sending :" + message);
                }
                
                @Override public void connecting(URI host) {
                    LOG.info("Connecting to cloud :" + host);
                }
                
                @Override public void disconnected(ClosingCode closeReason) {
                    LOG.info("Disconnecting from cloud :" + closeReason);
                }
            });
        }
        
        try {
            enavCloudConnection.setHost(host);
            connection = enavCloudConnection.build();

            if (connection != null) {
                cloudStatus.markCloudReception();
                cloudStatus.markSuccesfullSend();
                LOG.info("Connected succesfully to cloud server: " + host + " with shipId " + id);
                return true;
            } else {
                fireError("Failed building a maritime cloud connection");
                return false; 
            }
        } catch (Exception e) {
            fireError(e.getMessage());
            cloudStatus.markFailedSend();
            cloudStatus.markFailedReceive();
            LOG.error("Failed to connect to server: " + e);
            return false; 
        }
    }
    
    /*********************************/
    /** Listener functionality      **/
    /*********************************/
    
    /**
     * Adds a listener for cloud connection status changes
     * @param listener the listener to add
     */
    public final void addListener(IMaritimeCloudListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener 
     * @param listener the listener to remove
     */
    public final void removeListener(IMaritimeCloudListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notifies listeners that a connection has been established to the maritime cloud
     * @param connection the connection
     */
    protected void fireConnected(MaritimeCloudClient connection) {
        for (IMaritimeCloudListener listener : listeners) {
            listener.cloudConnected(connection);
        }
    }

    /**
     * Notifies listeners that a connection has been terminated to the maritime cloud
     */
    protected void fireDisconnected() {
        for (IMaritimeCloudListener listener : listeners) {
            listener.cloudDisconnected();
        }
    }

    /**
     * Notifies listeners that an error has occurred
     * @param error the error messsage
     */
    protected void fireError(String error) {
        for (IMaritimeCloudListener listener : listeners) {
            listener.cloudError(error);
        }
    }

    /**
     * Notifies listeners that they can perform periodic tasks
     */
    protected void firePeriodicTask() {
        for (IMaritimeCloudListener listener : listeners) {
            listener.cloudPeriodicTask();
        }
    }
    
    /**
     * Provides a listener interface to the Maritime Cloud connection status.
     * <p>
     * Listeners should provide their own error handling and not throw exceptions
     * in the methods.<br>
     * Neither should not synchronously perform long-lasting tasks.
     */
    public interface IMaritimeCloudListener {
        
        /**
         * Called when the connection to the maritime cloud has been established.
         * <p>
         * Can be used by listeners to hook up services.
         * 
         * @param connection the maritime cloud connection
         */
        void cloudConnected(MaritimeCloudClient connection);

        /**
         * Called when the connection to the maritime cloud has been terminated.
         * <p>
         * Can be used by listeners to clean up.
         */
        void cloudDisconnected();
        
        /**
         * Called if an error has occurred.
         * 
         * @param error the error message
         */
        void cloudError(String error);
        
        /**
         * Called periodically when the service is connected to the maritime cloud.
         * <p>
         * Can be used by listeners to perform periodic tasks.
         */
        void cloudPeriodicTask();
    }

}

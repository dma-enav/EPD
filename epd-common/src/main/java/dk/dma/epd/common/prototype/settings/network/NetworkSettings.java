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
package dk.dma.epd.common.prototype.settings.network;

import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.observers.NetworkSettingsListener;

/**
 * Base class for maintaining network related settings.
 * 
 * @author Janus Varmarken
 */
public class NetworkSettings<OBSERVER extends NetworkSettingsListener>
        extends ObservedSettings<OBSERVER> {

    /**
     * Setting specifying a port.
     */
    private int port = 9000;

    /**
     * Setting specifying a host name or address.
     */
    private String host = "localhost";

    /**
     * Setting specifying a connection establishment timeout (in milliseconds).
     */
    private int connectTimeout = 30000;

    /**
     * Setting specifying a timeout (in milliseconds) for reading.
     */
    private int readTimeout = 60000;

    /**
     * Get the port number.
     * 
     * @return The port number.
     */
    public int getPort() {
        try {
            this.settingLock.readLock().lock();
            return this.port;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Change the port number.
     * 
     * @param port
     *            The new port number.
     */
    public void setPort(final int port) {
        try {
            this.settingLock.writeLock().lock();
            if (this.port == port) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers
            this.port = port;
            for (OBSERVER obs : this.observers) {
                obs.onPortChanged(port);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the host name.
     * 
     * @return The host name.
     */
    public String getHost() {
        try {
            this.settingLock.readLock().lock();
            return this.host;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Change the host name.
     * 
     * @param host
     *            The new host name.
     */
    public void setHost(final String host) {
        try {
            this.settingLock.writeLock().lock();
            if (this.host.equals(host)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.host = host;
            for (OBSERVER obs : this.observers) {
                obs.onHostChanged(host);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the connection establishment timeout. Unit is milliseconds.
     * 
     * @return The connection establishment timeout in milliseconds.
     */
    public int getConnectTimeout() {
        try {
            this.settingLock.readLock().lock();
            return this.connectTimeout;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Change the connection establishment timeout. Unit is milliseconds.
     * 
     * @param connectTimeout
     *            The new connection establishment timeout in milliseconds.
     */
    public void setConnectTimeout(final int connectTimeout) {
        try {
            this.settingLock.writeLock().lock();
            if (this.connectTimeout == connectTimeout) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.connectTimeout = connectTimeout;
            for (OBSERVER obs : this.observers) {
                obs.onConnectTimeoutChanged(connectTimeout);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the read timeout. Unit is milliseconds.
     * 
     * @return The read timeout in milliseconds.
     */
    public int getReadTimeout() {
        try {
            this.settingLock.readLock().lock();
            return this.readTimeout;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Change the read timeout. Unit is milliseconds.
     * 
     * @param readTimeout
     *            The new read timeout in milliseconds.
     */
    public void setReadTimeout(final int readTimeout) {
        try {
            this.settingLock.writeLock().lock();
            if (this.readTimeout == readTimeout) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.readTimeout = readTimeout;
            for (OBSERVER obs : this.observers) {
                obs.onReadTimeoutChanged(readTimeout);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }
}

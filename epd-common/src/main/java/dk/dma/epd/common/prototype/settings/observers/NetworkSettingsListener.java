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
package dk.dma.epd.common.prototype.settings.observers;

import dk.dma.epd.common.prototype.settings.network.NetworkSettings;

/**
 * Interface for observing a {@link NetworkSettings} for changes.
 * 
 * @author Janus Varmarken
 * 
 */
public interface NetworkSettingsListener {
    
    /**
     * Invoked when {@link NetworkSettings#getPort()} has changed.
     * 
     * @param newPort
     *            The updated port number.
     */
    void onPortChanged(int newPort);

    /**
     * Invoked when {@link NetworkSettings#getHost()} has changed.
     * 
     * @param newHost
     *            The updated host name.
     */
    void onHostChanged(String newHost);

    /**
     * Invoked when {@link NetworkSettings#getConnectTimeout()} has changed.
     * 
     * @param newConnectTimeout
     *            The updated connect timeout. See
     *            {@link NetworkSettings#getConnectTimeout()} for details
     *            such as unit.
     */
    void onConnectTimeoutChanged(int newConnectTimeout);

    /**
     * Invoked when {@link NetworkSettings#getReadTimeout()} has changed.
     * 
     * @param newReadTimeout
     *            The updated read timeout. See
     *            {@link NetworkSettings#getReadTimeout()} for details such
     *            as unit.
     */
    void onReadTimeoutChanged(int newReadTimeout);
    
}

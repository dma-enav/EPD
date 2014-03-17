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

/**
 * Base class for maintaining network related settings.
 * 
 * @author Janus Varmarken
 */
public class NetworkSettings<OBSERVER extends INetworkSettingsObserver> extends
        ObservedSettings<OBSERVER> {

    /**
     * Setting specifying a port.
     */
    private int port;
    
    /**
     * Setting specifying a host name or address.
     */
    private String host;

    /**
     * Setting specifying a connection establishment timeout (in milliseconds).
     */
    private int connectTimeout = 30000;

    /**
     * Setting specifying a timeout (in milliseconds) for reading.
     */
    private int readTimeout = 60000;
}

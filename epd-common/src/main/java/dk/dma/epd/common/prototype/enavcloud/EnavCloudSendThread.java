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
package dk.dma.epd.common.prototype.enavcloud;

import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.broadcast.BroadcastMessage;


/**
 * Thread for sending messages over the maritime cloud
 */
public class EnavCloudSendThread extends Thread {

    private BroadcastMessage message;
    MaritimeCloudClient connection;

    /**
     * Constructor
     * @param message the message to send
     * @param connection the maritime cloud connection
     */
    public EnavCloudSendThread(BroadcastMessage message,
            MaritimeCloudClient connection) {
        this.message = message;
        this.connection = connection;
    }

    /**
     * Main thread run method.
     * Broadcasts the message.
     */
    @Override
    public void run() {
        if (connection != null) {
            connection.broadcast(message);
        }
    }
}

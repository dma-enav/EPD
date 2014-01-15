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
 * Thread for sending AIS messages
 */
public class EnavCloudSendThread extends Thread {

    private BroadcastMessage message;
    // private EnavCloudHandler enavCloudHandler;
    MaritimeCloudClient connection;

    // private static final Logger LOG = Logger
    // .getLogger(EnavCloudSendThread.class);

    public EnavCloudSendThread(BroadcastMessage message,
            MaritimeCloudClient connection) {
        this.message = message;
        this.connection = connection;
        // this.sendRequest = sendRequest;
        // this.aisServices = aisServices;
    }

    @Override
    public void run() {

        if (connection != null) {
            connection.broadcast(message);
        }
        // dk.dma.enav.communication.PersistentConnection.State test =
        // connection.getState();

        // Send message

        // try (MaritimeNetworkConnection c =
        // aisServices.getEnavCloudConnection().connect()) {
        // for (;;) {

        // try {
        // while (true) {

        // Thread.sleep(5000);
        // }
        // } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

    }

}

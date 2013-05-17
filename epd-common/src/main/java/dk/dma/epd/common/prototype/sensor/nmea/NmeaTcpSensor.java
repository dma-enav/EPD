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
package dk.dma.epd.common.prototype.sensor.nmea;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import net.jcip.annotations.ThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.ais.reader.SendException;
import dk.dma.ais.reader.SendRequest;
import dk.dma.ais.sentence.Abk;
import dk.dma.enav.util.function.Consumer;

/**
 * TCP NMEA sensor
 */
@ThreadSafe
public class NmeaTcpSensor extends NmeaSensor {

    private static final Logger LOG = LoggerFactory.getLogger(NmeaTcpSensor.class);

    private static final int TCP_READ_TIMEOUT = 60000; // 1 min

    private volatile long reconnectInterval = 5000; // Default 5 sec
    private volatile String hostname;
    private volatile int port;
    private volatile OutputStream outputStream;

    private volatile Socket clientSocket = new Socket();

    public NmeaTcpSensor() {
    }

    public NmeaTcpSensor(String hostname, int port) {
        this();
        this.hostname = hostname;
        this.port = port;
    }

    public NmeaTcpSensor(String hostPort) {
        this();
        String[] parts = StringUtils.split(hostPort, ':');
        this.hostname = parts[0];
        this.port = Integer.parseInt(parts[1]);
    }

    @Override
    public void run() {

        while (true) {
            try {
                disconnect();
                connect();
                readLoop(clientSocket.getInputStream());
            } catch (IOException e) {
                LOG.error("TCP NMEA sensor failed: " + e.getMessage() + " retry in " + reconnectInterval / 1000 + " seconds");
                try {
                    Thread.sleep(reconnectInterval);
                } catch (InterruptedException intE) {
                }
            }
        }
    }

    private void connect() throws IOException {
        try {
            clientSocket = new Socket();
            InetSocketAddress address = new InetSocketAddress(hostname, port);
            clientSocket.connect(address);
            clientSocket.setKeepAlive(true);
            clientSocket.setSoTimeout(TCP_READ_TIMEOUT);
            outputStream = clientSocket.getOutputStream();
            LOG.info("NMEA source connected " + hostname + ":" + port);
        } catch (UnknownHostException e) {
            LOG.error("Unknown host: " + hostname + ": " + e.getMessage());
            throw e;
        } catch (IOException e) {
            LOG.error("Could not connect to NMEA source: " + hostname + ": " + e.getMessage());
            throw e;
        }
    }

    private void disconnect() {
        if (clientSocket != null && getStatus() == Status.CONNECTED) {
            try {
                LOG.info("Disconnecting source " + hostname + ":" + port);
                clientSocket.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void send(SendRequest sendRequest, Consumer<Abk> resultListener) throws SendException {
        doSend(sendRequest, resultListener, outputStream);
    }

    public Status getStatus() {
        return clientSocket.isConnected() ? Status.CONNECTED : Status.DISCONNECTED;
    }

    public long getReconnectInterval() {
        return reconnectInterval;
    }

    public void setReconnectInterval(long reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

}

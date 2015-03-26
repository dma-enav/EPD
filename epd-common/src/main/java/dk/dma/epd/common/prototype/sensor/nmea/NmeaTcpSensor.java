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
package dk.dma.epd.common.prototype.sensor.nmea;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final AtomicBoolean outputStreamSet = new AtomicBoolean(false);

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

        while (!isStopped()) {
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
        
        // Disconnect and flag that the sensor has terminated
        disconnect();
        flagTerminated();
        LOG.warn("TCP NMEA sensor terminated");
    }

    private void connect() throws IOException {
        try {
            clientSocket = new Socket();
            InetSocketAddress address = new InetSocketAddress(hostname, port);
            clientSocket.connect(address);
            clientSocket.setKeepAlive(true);
            clientSocket.setSoTimeout(TCP_READ_TIMEOUT);
            // mark the outputStream as not established yet
            outputStreamSet.set(false);
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
        // TODO block until connection is established both for initial
        // connection and sleep+reconnect. Something like
        // j.u.c.ReentrantLock is probably the go here. Care
        // would have to be taken with two cases being that this send was called
        // from the same thread as the read loop (called synchronously by a
        // listener) and the other case where the send was called from a
        // different thread to the read loop.

        // atomically initialize the outputStream if required
        if (outputStreamSet.compareAndSet(false, true))
            try {
                outputStream = clientSocket.getOutputStream();
            } catch (IOException e) {
                throw new SendException("Could not connect to NMEA source outputStream: "
                        + clientSocket.toString());
            }
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

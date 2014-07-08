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
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

import dk.dma.ais.reader.SendException;
import dk.dma.ais.reader.SendRequest;
import dk.dma.ais.sentence.Abk;
import dk.dma.enav.util.function.Consumer;

public class NmeaUdpSensor extends NmeaSensor {
    
    private static final Logger LOG = LoggerFactory.getLogger(NmeaUdpSensor.class);

    private final int port;
    
    public NmeaUdpSensor(int port) {
        this.port = port;
    }
    
    @Override
    public void run() {
        byte[] receiveData = new byte[256];
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            while (!isStopped()) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength(), Charsets.US_ASCII);
                handleLine(sentence);
            }            
        } catch (IOException e) {
            LOG.error("Failed to listen on UDP socket", e);
        }

        // Flag that the sensor has terminated
        flagTerminated();
        LOG.warn("UDP NMEA sensor terminated");
    }

    @Override
    public void send(SendRequest sendRequest, Consumer<Abk> resultListener) throws SendException {
        throw new UnsupportedOperationException();

    }

}

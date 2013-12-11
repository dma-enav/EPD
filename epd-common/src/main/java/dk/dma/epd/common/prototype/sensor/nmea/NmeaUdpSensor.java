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
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                handleLine(new String(receivePacket.getData(), Charsets.US_ASCII));
            }            
        } catch (IOException e) {
            LOG.error("Failed to listen on UDP socket", e);
        }
    }

    @Override
    public void send(SendRequest sendRequest, Consumer<Abk> resultListener) throws SendException {
        throw new UnsupportedOperationException();

    }

}

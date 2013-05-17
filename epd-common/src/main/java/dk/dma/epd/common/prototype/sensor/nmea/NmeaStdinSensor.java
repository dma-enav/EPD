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

import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.ais.reader.SendException;
import dk.dma.ais.reader.SendRequest;
import dk.dma.ais.sentence.Abk;
import dk.dma.enav.util.function.Consumer;

/**
 * NMEA sensor taking input from STDIN
 */
@ThreadSafe
public class NmeaStdinSensor extends NmeaSensor {

    private static final Logger LOG = LoggerFactory.getLogger(NmeaStdinSensor.class);

    public NmeaStdinSensor() {

    }

    @Override
    public void run() {
        try {
            readLoop(System.in);
        } catch (IOException e) {
            LOG.error("Failed to open stdin");
        }
    }

    @Override
    public void send(SendRequest sendRequest, Consumer<Abk> resultListener) throws SendException {
        throw new SendException("Cannot send to stdin sensor");
    }

}

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

        // Flag that the sensor has terminated
        flagTerminated();
        LOG.warn("Stdin NMEA sensor terminated");
    }

    @Override
    public void send(SendRequest sendRequest, Consumer<Abk> resultListener) throws SendException {
        throw new SendException("Cannot send to stdin sensor");
    }

}

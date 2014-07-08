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

import java.awt.Frame;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;

import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.ais.reader.SendException;
import dk.dma.ais.reader.SendRequest;
import dk.dma.ais.sentence.Abk;
import dk.dma.enav.util.function.Consumer;
import dk.dma.epd.common.prototype.settings.SensorSettings;
import dk.dma.epd.common.util.Util;


/**
 * NMEA sensor reading from file
 */
@ThreadSafe
public class NmeaFileSensor extends NmeaSensor {
    
    private static final Logger LOG = LoggerFactory.getLogger(NmeaFileSensor.class);
    
    private final String filename;
    private InputStream inputStream;
    private volatile Frame frame;
    
    public NmeaFileSensor(String filename, SensorSettings sensorSettings) {
        LOG.info("Using AIS replay file: " + filename);
        this.filename = filename;
        setReplay(true);
        setReplaySpeedup(sensorSettings.getReplaySpeedup());
        setReplayStartDate(sensorSettings.getReplayStartDate());
        LOG.info("Replay start date: " + sensorSettings.getReplayStartDate());
    }

    @Override
    public void run() {
        // Open file
        inputStream = null;
        try {
            inputStream = new FileInputStream(filename);
        } catch (IOException e) {
            LOG.error("Failed to open replay file: " + filename + ": " + e.getMessage());
            return;
        }
        
        // Wait for frame and confirmation
        while (frame == null) {
            Util.sleep(1000);
        }
        Util.sleep(5000);
        JOptionPane.showMessageDialog(frame, "Start replay");        
        
        
        // Read
        try {
            readLoop(inputStream);
        } catch (IOException e) {
            LOG.error("Error while reading replay file: " + filename + ": " + e.getMessage());
        }
        
        long dataElapsed = getDataEnd().getTime() - getDataStart().getTime();
        long realElapsed = (getReplayEnd().getTime() - getReplayStart().getTime()) * getReplaySpeedup();
                
        LOG.info("Replay data start: " + getDataStart() + " end: " + getDataEnd() + " elapsed: " + dataElapsed / 1000);
        LOG.info("Replay real start: " + getReplayStart() + " end: " + getReplayEnd() + " elapsed: " + realElapsed / 1000);
        
        if (frame != null && !isStopped()) {
            JOptionPane.showMessageDialog(frame, "Replay finished");
        }
        
        // Flag that the sensor has terminated
        try { inputStream.close(); } catch (Exception ex) {}
        flagTerminated();
        LOG.warn("File NMEA sensor terminated");
    }
    
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        if (obj instanceof Frame) {
            frame = (Frame)obj;
        }
    }

    @Override
    public void send(SendRequest sendRequest, Consumer<Abk> resultListener) throws SendException {
        throw new SendException("Cannot send to file sensor");
    }

}

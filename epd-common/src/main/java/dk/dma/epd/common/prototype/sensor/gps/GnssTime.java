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
package dk.dma.epd.common.prototype.sensor.gps;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.sensor.nmea.NmeaSensor;
import dk.dma.epd.common.prototype.sensor.nmea.SensorType;

/**
 * Singleton component class that maintains GNSS time as an offset from computer time
 */
public final class GnssTime extends MapHandlerChild implements IGnssTimeListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(GnssTime.class);
    
    private long offset;
    private NmeaSensor nmeaSensor;
    
    private static GnssTime instance;
    
    private GnssTime() {
        
    }

    /**
     * Receive GNSS time update
     */
    @Override
    public synchronized void receive(GnssTimeMessage gnssTimeMessage) {        
        if (gnssTimeMessage == null) {
            return;
        }
        offset = new Date().getTime() - gnssTimeMessage.getTime().getTime();
        LOG.debug("New GPS time offset: " + offset);        
    }
    
    /**
     * Get GNSS date
     * @return date
     */
    public synchronized Date getDate() {
        return new Date(new Date().getTime() - offset);         
    }
    
    public static void init() {
        synchronized (GnssTime.class) {
            if (instance == null) {
                instance = new GnssTime();
            }            
        }
    }
    
    public static GnssTime getInstance() {
        synchronized (GnssTime.class) {
            return instance;
        }
    }
    
    /**
     * Hook up to an NMEA sensor
     */
    @Override
    public void findAndInit(Object obj) {
        if (nmeaSensor != null) {
            return;
        }
        if (obj instanceof NmeaSensor) {
            NmeaSensor sensor = (NmeaSensor)obj;
            if (sensor.isSensorType(SensorType.GPS)) {
                nmeaSensor = sensor;
                nmeaSensor.addGnssTimeListener(this);
            }
        }
    }
    
    @Override
    public void findAndUndo(Object obj) {
        if (obj == nmeaSensor) {
            nmeaSensor.removeGnssTimeListener(this);
        }
    }

}

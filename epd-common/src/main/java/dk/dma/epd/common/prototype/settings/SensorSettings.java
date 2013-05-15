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
package dk.dma.epd.common.prototype.settings;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.util.ParseUtils;

/**
 * Sensor settings
 */
public class SensorSettings implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger(SensorSettings.class);
    
    private static final String PREFIX = "sensor.";
       private static final TimeZone TZ_GMT = TimeZone.getTimeZone("GMT+0000");

        public static String getISO8620(Date date) {
            SimpleDateFormat iso8601gmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            iso8601gmt.setTimeZone(TZ_GMT);
            return iso8601gmt.format(date);
        }
        
    public enum SensorConnectionType {
        NONE, TCP, SERIAL, FILE, AIS_SHARED;
        public static SensorConnectionType parseString(String type) {
            if (type.equalsIgnoreCase("TCP")) {
                return TCP;
            } else if (type.equalsIgnoreCase("SERIAL")) {
                return SERIAL;
            } else if (type.equalsIgnoreCase("FILE")) {
                return FILE;
            } else if (type.equalsIgnoreCase("AIS_SHARED")) {
                return AIS_SHARED;
            }
            return NONE;
        }
    }
    
    private SensorConnectionType aisConnectionType = SensorConnectionType.TCP; 
    private String aisHostOrSerialPort = "localhost";
    private String aisFilename = "";
    private int aisTcpPort = 4001;
    
    private SensorConnectionType gpsConnectionType = SensorConnectionType.AIS_SHARED;
    private String gpsHostOrSerialPort = "COM11";
    private String gpsFilename = "";
    private int gpsTcpPort = 8888;
    
    private boolean startTransponder = true;
    /**
     * If farther away than this range, the messages are discarded
     * In nautical miles (theoretical distance is about 40 miles)
    */
    private double aisSensorRange;
        
    private int replaySpeedup = 1;
    private Date replayStartDate;

    public SensorSettings() {
        
    }
    
    public void readProperties(Properties props) {
        aisConnectionType = SensorConnectionType.parseString(props.getProperty(PREFIX + "aisConnectionType", aisConnectionType.name()));
        aisHostOrSerialPort = props.getProperty(PREFIX + "aisHostOrSerialPort", aisHostOrSerialPort);
        aisTcpPort = PropUtils.intFromProperties(props, PREFIX + "aisTcpPort", aisTcpPort);
        gpsConnectionType = SensorConnectionType.parseString(props.getProperty(PREFIX + "gpsConnectionType", gpsConnectionType.name()));
        gpsHostOrSerialPort = props.getProperty(PREFIX + "gpsHostOrSerialPort", gpsHostOrSerialPort);
        gpsTcpPort = PropUtils.intFromProperties(props, PREFIX + "gpsTcpPort", gpsTcpPort);
        startTransponder = PropUtils.booleanFromProperties(props, PREFIX + "startTransponder", startTransponder);
        aisSensorRange = PropUtils.doubleFromProperties(props, PREFIX + "aisSensorRange", aisSensorRange);
        aisFilename = props.getProperty(PREFIX + "aisFilename", aisFilename);
        gpsFilename = props.getProperty(PREFIX + "gpsFilename", gpsFilename);
        replaySpeedup = PropUtils.intFromProperties(props, PREFIX + "replaySpeedup", replaySpeedup);
        String replayStartStr = props.getProperty(PREFIX + "replayStartDate", "");
        if (replayStartStr.length() > 0) {
            try {
                replayStartDate = ParseUtils.parseIso8602(replayStartStr);
                LOG.info("replayStartDate: " + replayStartDate);
            } catch (FormatException e) {
                LOG.error("Failed to parse replayStartDate");
            }
        }
    }
    
    public void setProperties(Properties props) {
        props.put(PREFIX + "aisConnectionType", aisConnectionType.name());
        props.put(PREFIX + "aisHostOrSerialPort", aisHostOrSerialPort);
        props.put(PREFIX + "aisTcpPort", Integer.toString(aisTcpPort));
        props.put(PREFIX + "gpsConnectionType", gpsConnectionType.name());
        props.put(PREFIX + "gpsHostOrSerialPort", gpsHostOrSerialPort);
        props.put(PREFIX + "gpsTcpPort", Integer.toString(gpsTcpPort));
        props.put(PREFIX + "startTransponder", Boolean.toString(startTransponder));
        props.put(PREFIX + "aisSensorRange", Double.toString(aisSensorRange));
        props.put(PREFIX + "aisFilename", aisFilename);
        props.put(PREFIX + "gpsFilename", gpsFilename);
        props.put(PREFIX + "replaySpeedup", Integer.toString(replaySpeedup));
        String replayStartStr = "";
        if (replayStartDate != null) {            
            replayStartStr = getISO8620(replayStartDate);
        }
        props.put(PREFIX + "replayStartDate", replayStartStr);
    }

    public SensorConnectionType getAisConnectionType() {
        return aisConnectionType;
    }

    public void setAisConnectionType(SensorConnectionType aisConnectionType) {
        this.aisConnectionType = aisConnectionType;
    }

    public String getAisHostOrSerialPort() {
        return aisHostOrSerialPort;
    }

    public void setAisHostOrSerialPort(String aisHostOrSerialPort) {
        this.aisHostOrSerialPort = aisHostOrSerialPort;
    }

    public int getAisTcpPort() {
        return aisTcpPort;
    }

    public void setAisTcpPort(int aisTcpPort) {
        this.aisTcpPort = aisTcpPort;
    }

    public SensorConnectionType getGpsConnectionType() {
        return gpsConnectionType;
    }

    public void setGpsConnectionType(SensorConnectionType gpsConnectionType) {
        this.gpsConnectionType = gpsConnectionType;
    }

    public String getGpsHostOrSerialPort() {
        return gpsHostOrSerialPort;
    }

    public void setGpsHostOrSerialPort(String gpsHostOrSerialPort) {
        this.gpsHostOrSerialPort = gpsHostOrSerialPort;
    }

    public int getGpsTcpPort() {
        return gpsTcpPort;
    }

    public void setGpsTcpPort(int gpsTcpPort) {
        this.gpsTcpPort = gpsTcpPort;
    }

    public boolean isStartTransponder() {
        return startTransponder;
    }
    
    public void setStartTransponder(boolean startTransponder) {
        this.startTransponder = startTransponder;
    }
    
    public double getAisSensorRange() {
        return aisSensorRange;
    }
    
    public void setAisSensorRange(double aisSensorRange) {
        this.aisSensorRange = aisSensorRange;
    }
    
    public String getAisFilename() {
        return aisFilename;
    }
    
    public void setAisFilename(String aisFilename) {
        this.aisFilename = aisFilename;
    }
    
    public String getGpsFilename() {
        return gpsFilename;
    }
    
    public void setGpsFilename(String gpsFilename) {
        this.gpsFilename = gpsFilename;
    }
    
    public int getReplaySpeedup() {
        return replaySpeedup;
    }
    
    public void setReplaySpeedup(int replaySpeedup) {
        this.replaySpeedup = replaySpeedup;
    }
    
    public Date getReplayStartDate() {
        return replayStartDate;
    }

    public void setReplayStartDate(Date replayStartDate) {
        this.replayStartDate = replayStartDate;
    }
    
}
